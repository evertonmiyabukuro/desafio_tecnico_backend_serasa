package com.serasa.DesafioTecnicoBackEnd.services;

import com.serasa.DesafioTecnicoBackEnd.models.PesagemDTO;
import com.serasa.DesafioTecnicoBackEnd.models.ResultadoPesagemDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FilaPesagensEmMemoriaService {
    private static final long TTL_MILLIS = 5 * 60 * 1000L; //Manter o registro em memória por no máximo 5 minutos
    private static final int MAX_PER_ID = 10; //Manter no máximo 10 leituras em memória por balança e caminhão

    private final ConcurrentHashMap<String, Registro> listaPesagens = new ConcurrentHashMap<>();

    /*
     Identifica se o objeto de passesagem retornado pelo ESP32 está válido.
     Para ser válido, ou ele precisa:
     - Ter o ID nulo (é checado na inserção se é referente a um registor já existente) ou;
     - Ter o ID informado e esse ID estar em memória.
     */
    public boolean objetoPesagemValido(PesagemDTO objetoPesagem){
        if(objetoPesagem.getPesagemId() == null){
            return true;
        }

        return listaPesagens.containsKey(objetoPesagem.getPesagemId());
    }

    /*
     Insere um registro de pesagem retornado pelo ESP32.
     A premissa aqui é que ele já foi validado.
     Nesse caso, primeiro checamos o ID. Se ele veio nulo, buscados dentre os registros em
     memória (por placa + id da balanca) e caso não tenha, criamos um novo ID para ele.
     Após encontrada ou gerado o ID correspondente, adicionamos em sua lista de leituras a
     leitura atual retornada pelo ESP32.
     Obs.: a leitura do ESP só será adicionada se informada 100ms ou mais após a leitura anterior.
     */
    public void adicionarRegistroPesagem(PesagemDTO objetoPesagem) {
        long now = System.currentTimeMillis();

        //Criação ou localização da ID de leitura por placa+id Balança
        if(objetoPesagem.getPesagemId() == null){
            String uuidExistente = listaPesagens.entrySet()
                    .stream()
                    .filter(e -> {
                        Registro registro = e.getValue();
                        registro.lock.lock();
                        try {
                            return registro.deque.stream().anyMatch(p ->
                                    Objects.equals(p.getPlate(), objetoPesagem.getPlate()) &&
                                            Objects.equals(p.getIdBalanca(), objetoPesagem.getIdBalanca())
                            );
                        } finally {
                            registro.lock.unlock();
                        }
                    })
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (uuidExistente != null) {
                objetoPesagem.setPesagemId(uuidExistente);
            } else {
                objetoPesagem.setPesagemId(UUID.randomUUID().toString());
            }
        }

        Registro registroNaFila = listaPesagens.computeIfAbsent(objetoPesagem.getPesagemId(), k -> new Registro());

        registroNaFila.lock.lock();
        try {
            long horaAtual = now;

            //Se o tempo desde a última atualização para essa balança foi menor que 100ms, ignora essa leitura.
            if((horaAtual - registroNaFila.lastAccess)<100){
                return;
            }

            if (registroNaFila.deque.size() >= MAX_PER_ID) {
                registroNaFila.deque.removeFirst();
            }
            registroNaFila.deque.addLast(objetoPesagem);
            registroNaFila.lastAccess = now;
        } finally {
            registroNaFila.lock.unlock();
        }
    }


    /*
     Valida se um registro de leitura está estável e caso positivo, extrai e retorna ele da lista.
     A regra aqui é:
     - Se não encontrar o ID da pesagem, retorna null
     - Se não tiver 10 registros de leitura (equivalente a 1s), retorna null
     - Caso tenha, verifica a mediana  das últimas leituras
     - Para retorar, a leituras máximas e mínimas devem estar a no máximo 5% de diferença uma
       da outra. E a diferença entre as leituras e a mediana deve estar a no máximo 2,5%.
     - Caso obedeça a esses critérios, remove o registro da lista em memória e  retorna a
       última leitura.
     */
    public ResultadoPesagemDTO extrairRegistroCasoEstavel(String idPesagem){
        List<PesagemDTO> listaPesagem = this.getListaPesagem(idPesagem);

        if (listaPesagem == null){
            return null;
        }

        if (listaPesagem.size() < 10) {
            return null;
        }

        List<Float> weights = listaPesagem.stream()
                .map(PesagemDTO::getWeight)
                .toList();

        float max = Collections.max(weights);
        float min = Collections.min(weights);

        float median = (max + min) / 2f;

        if (max/min > 1.05f) {
            return null;
        }

        float maxAllowedDiff = 0.025f * median;

        for (float w : weights) {
            if (Math.abs(w - median) > maxAllowedDiff) {
                return null;
            }
        }

        Registro registroNaFila = listaPesagens.get(idPesagem);
        listaPesagens.remove(idPesagem,registroNaFila);

        PesagemDTO registroASerConsiderado = listaPesagem.get(listaPesagem.size() - 1);
        return new ResultadoPesagemDTO(registroASerConsiderado.getWeight(), registroASerConsiderado.getPlate(), registroASerConsiderado.getIdBalanca());
    }

    /*
     Retorna um snapshot das leituras para determinado ID de pesagem
     */
    private List<PesagemDTO> getListaPesagem(String idPesagem) {
        Registro registro = listaPesagens.get(idPesagem);
        if (registro == null) return null;

        registro.lock.lock();
        try {
            List<PesagemDTO> copy = new ArrayList<>(registro.deque);
            registro.lastAccess = System.currentTimeMillis(); // access updates TTL
            return copy;
        } finally {
            registro.lock.unlock();
        }
    }

    /*
     Tarefa agendada dentro do Spring para limpar a lista em memória dos registros
     mais antigos que 5 minutos.
     */
    @Scheduled(fixedRateString = "60000")
    public void removerRegistrosExpirados() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Registro> atual : listaPesagens.entrySet()) {
            Registro registro = atual.getValue();

            if (now - registro.lastAccess > TTL_MILLIS) {
                listaPesagens.remove(atual.getKey(), registro);
            }
        }
    }

    private static class Registro {
        final ReentrantLock lock = new ReentrantLock();
        final Deque<PesagemDTO> deque = new ArrayDeque<>(MAX_PER_ID);
        volatile long lastAccess = System.currentTimeMillis();
    }
}
