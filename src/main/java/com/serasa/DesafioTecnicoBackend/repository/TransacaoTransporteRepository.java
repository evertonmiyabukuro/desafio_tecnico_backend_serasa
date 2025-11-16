package com.serasa.DesafioTecnicoBackEnd.repository;

import com.serasa.DesafioTecnicoBackEnd.models.TransacaoTransporteModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransacaoTransporteRepository extends CrudRepository<com.serasa.DesafioTecnicoBackEnd.models.TransacaoTransporteModel, Integer> {
    Optional<TransacaoTransporteModel> findFirstByCaminhaoPlacaAndGraoIdAndDataHoraRetornoIsNull(String placa, Integer idGrao);
}