package com.serasa.DesafioTecnicoBackEnd.services;

import com.serasa.DesafioTecnicoBackEnd.models.BalancaModel;
import com.serasa.DesafioTecnicoBackEnd.repository.BalancaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class BalancaAuthService {

    @Autowired
    private BalancaRepository balancaRepository;

    public BalancaModel validarAutorizacao(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autorização ausente ou inválida");
        }

        authHeader = authHeader.replace("Bearer ", "");

        if (authHeader == null || !autorizacaoValida(authHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autorização da balança ausente ou inválida");
        }

        return balancaRepository.findByidentificadorAutorizacao(authHeader)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Autorização não pertence a nenhuma balança cadastrada"));
    }

    private boolean autorizacaoValida(String autorizacao) {
        try {
            UUID.fromString(autorizacao);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}