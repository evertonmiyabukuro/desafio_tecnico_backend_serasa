package com.serasa.DesafioTecnicoBackEnd.services;

import com.serasa.DesafioTecnicoBackEnd.models.PesagemDTO;
import com.serasa.DesafioTecnicoBackEnd.models.ResultadoPesagemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FilaPesagensEmMemoriaService {
    private static final long TTL_MILLIS = 5 * 60 * 1000L; //Manter o registro em memória por no máximo 5 minutos
    private static final int MAX_PER_ID = 10; //Manter no máximo 10 leituras em memória por balança e caminhão

    private final ConcurrentHashMap<String, Registro> listaDadosPesagens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> listaPesagens = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(FilaPesagensEmMemoriaService.class);

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

        return listaDadosPesagens.containsKey(objetoPesagem.getPesagemId());
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
        if (objetoPesagem.getPesagemId() == null) {
            String key = objetoPesagem.getPlate() + "-" + objetoPesagem.getIdBalanca();

            logger.info("Registro de pesagem não veio com a chave informada, verificando se já há registro de balança ativo...");

            String uuid = listaPesagens.computeIfAbsent(key, k -> {
                String novoId = UUID.randomUUID().toString();
                logger.info("Chave inexistente, gerando nova chave");
                return novoId;
            });
            objetoPesagem.setPesagemId(uuid);

            logger.info("Chave: {}", uuid);
        }

        Registro registroNaFila = listaDadosPesagens.computeIfAbsent(objetoPesagem.getPesagemId(), k -> new Registro());

        registroNaFila.lock.lock();
        try {
            long horaAtual = now;

            //Se o tempo desde a última atualização para essa balança foi menor que 100ms, ignora essa leitura.
            if(Math.abs(horaAtual - registroNaFila.lastAccess)<1000){
                logger.info("Não inserindo leitura, pois a requisição foi feita a menos de 100ms");
                return;
            }

            if (registroNaFila.deque.size() >= MAX_PER_ID) {
                logger.info("Atingiu máximo de registros, removendo o registro mais antigo...");
                registroNaFila.deque.removeFirst();
            }
            logger.info("Inserindo o registro na fila...");
            registroNaFila.deque.addLast(objetoPesagem);
            logger.info("Contagem atual: {}", registroNaFila.deque.size());
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
     - Caso obedeça a esses critérios, remove o registro da lista em memória (tanto de pesagens
       quanto de IDs) e retorna a última leitura.
     */
    public ResultadoPesagemDTO extrairRegistroCasoEstavel(String idPesagem){
        List<PesagemDTO> listaPesagem = this.getListaPesagem(idPesagem);

        if (listaPesagem == null){
            logger.info("Chave {} não possui registro de pesagem ativo", idPesagem);
            return null;
        }

        if (listaPesagem.size() < 10) {
            logger.info("Chave {} não tem registro de pesagem com pelo menos 1s de amostragem", idPesagem);
            return null;
        }

        List<Float> weights = listaPesagem.stream()
                .map(PesagemDTO::getWeight)
                .toList();

        float max = Collections.max(weights);
        float min = Collections.min(weights);

        float median = (max + min) / 2f;

        if (max/min > 1.05f) {
            logger.info("Variação da balança para a pesagem {} está acima do tolerado (5% entre o máximo ({}) e mínimo ({})).", idPesagem, max, min);
            return null;
        }

        float maxAllowedDiff = 0.025f * median;

        for (float w : weights) {
            if (Math.abs(w - median) > maxAllowedDiff) {
                logger.info("Um dos registros de pesagem {} está acima do tolerado (2,5% da mediana {}: tolerado {}, encontrado {})).", idPesagem, median, maxAllowedDiff, Math.abs(w - median));
                return null;
            }
        }

        PesagemDTO registroASerConsiderado = listaPesagem.get(listaPesagem.size() - 1);

        Registro registroNaFila = listaDadosPesagens.get(idPesagem);
        logger.info("Removendo dados de pesagem e retornando último resultado de pesagem {}, pois está estável", idPesagem);
        listaDadosPesagens.remove(idPesagem,registroNaFila);

        String idPesagemEmMemoria = listaPesagens.get(idPesagem);
        logger.info("Removendo ID {} para placa e balança {} + {}", idPesagem, registroASerConsiderado.getPlate(), registroASerConsiderado.getIdBalanca());
        listaPesagens.remove(idPesagem,idPesagemEmMemoria);

        return new ResultadoPesagemDTO(registroASerConsiderado.getWeight(), registroASerConsiderado.getPlate(), registroASerConsiderado.getIdBalanca());
    }

    /*
     Retorna um snapshot das leituras para determinado ID de pesagem
     */
    private List<PesagemDTO> getListaPesagem(String idPesagem) {
        Registro registro = listaDadosPesagens.get(idPesagem);
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
        for (Map.Entry<String, Registro> atual : listaDadosPesagens.entrySet()) {
            Registro registro = atual.getValue();

            if (now - registro.lastAccess > TTL_MILLIS) {
                listaDadosPesagens.remove(atual.getKey(), registro);
            }
        }
    }

    private static class Registro {
        final ReentrantLock lock = new ReentrantLock();
        final Deque<PesagemDTO> deque = new ArrayDeque<>(MAX_PER_ID);
        volatile long lastAccess = 0;
    }
}
