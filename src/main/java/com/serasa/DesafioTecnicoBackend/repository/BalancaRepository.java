package com.serasa.DesafioTecnicoBackEnd.repository;

import com.serasa.DesafioTecnicoBackEnd.models.BalancaModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BalancaRepository extends CrudRepository<com.serasa.DesafioTecnicoBackEnd.models.BalancaModel, Integer> {
    Optional<BalancaModel> findByidentificadorAutorizacao(String identificadorAutorizacao);
}