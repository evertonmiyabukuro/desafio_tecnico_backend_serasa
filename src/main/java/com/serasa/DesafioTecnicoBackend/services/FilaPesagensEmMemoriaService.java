package com.serasa.DesafioTecnicoBackEnd.services;

import com.serasa.DesafioTecnicoBackEnd.models.PesagemDTO;
import com.serasa.DesafioTecnicoBackEnd.models.ResultadoPesagemDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FilaPesagensEmMemoriaService {
    private static final long TTL_MILLIS = 5 * 60 * 1000L; // 5 minutes
    private static final int MAX_PER_ID = 10;

    private final ConcurrentHashMap<String, Registro> listaPesagens = new ConcurrentHashMap<>();

    public boolean objetoPesagemValido(PesagemDTO objetoPesagem){
        if(objetoPesagem.getPesagemId() == null){
            return true;
        }

        return listaPesagens.containsKey(objetoPesagem.getPesagemId());
    }

    public void adicionarRegistroPesagem(PesagemDTO objetoPesagem) {
        long now = System.currentTimeMillis();

        if(objetoPesagem.getPesagemId() == null){
            String uuidExistente = listaPesagens.entrySet()
                    .stream()
                    .filter(e -> {
                        Registro registro = e.getValue();
                        registro.lock.lock();
                        try {
                            // Check if any item in the deque matches plate & idBalanca
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
                // Reuse the existing UUID
                objetoPesagem.setPesagemId(uuidExistente);
            } else {
                // Create new UUID
                objetoPesagem.setPesagemId(UUID.randomUUID().toString());
            }
        }

        Registro registroNaFila = listaPesagens.computeIfAbsent(objetoPesagem.getPesagemId(), k -> new Registro());

        // lock per-id to mutate the deque and update lastAccess
        registroNaFila.lock.lock();
        try {
            // if the deque is full, remove oldest (FIFO) to keep at most MAX_PER_ID
            if (registroNaFila.deque.size() >= MAX_PER_ID) {
                registroNaFila.deque.removeFirst();
            }
            registroNaFila.deque.addLast(objetoPesagem);
            registroNaFila.lastAccess = now;
        } finally {
            registroNaFila.lock.unlock();
        }
    }

    public ResultadoPesagemDTO extrairRegistroCasoEstavel(String idPesagem){
        List<PesagemDTO> listaPesagem = this.getListaPesagem(idPesagem);

        if (listaPesagem == null){
            return null;
        }

        if (listaPesagem.size() < 10) {
            return null; // only evaluate with 10 readings
        }

        // Extract weights
        List<Float> weights = listaPesagem.stream()
                .map(PesagemDTO::getWeight)
                .toList();

        float max = Collections.max(weights);
        float min = Collections.min(weights);

        float median = (max + min) / 2f;
        float range = max - min;

        // Condition 2: difference within 5% of median
        float maxAllowedDiff = 0.05f * median;
        if (range > maxAllowedDiff) {
            return null;
        }

        // Condition 1: all values within 5% of median
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

    private List<PesagemDTO> getListaPesagem(String idPesagem) {
        Registro registro = listaPesagens.get(idPesagem);
        if (registro == null) return null;

        registro.lock.lock();
        try {
            // copy to avoid exposing internal deque
            List<PesagemDTO> copy = new ArrayList<>(registro.deque);
            registro.lastAccess = System.currentTimeMillis(); // access updates TTL
            return copy;
        } finally {
            registro.lock.unlock();
        }
    }

    @Scheduled(fixedRateString = "300000")
    public void removerRegistrosExpirados() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Registro> atual : listaPesagens.entrySet()) {
            Registro registro = atual.getValue();

            if (now - registro.lastAccess > TTL_MILLIS) {
                // use remove(key, value) to avoid removing a replaced entry
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
