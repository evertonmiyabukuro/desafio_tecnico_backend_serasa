package com.serasa.DesafioTecnicoBackEnd.models.dtos;

public interface RelatorioPesagensDTO {

    String getFilial();

    String getPlaca();

    String getTipoGrao();

    Float getPesoBrutoEstabilizado();

    Float getTara();

    Float getPesoLiquido();

    Float getCustoCarga();
}
