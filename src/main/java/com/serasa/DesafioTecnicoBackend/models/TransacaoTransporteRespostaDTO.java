package com.serasa.DesafioTecnicoBackEnd.models;

import lombok.Data;

@Data
public class TransacaoTransporteRespostaDTO {
    private Integer idTransacaoTransporte;

    public TransacaoTransporteRespostaDTO(Integer id) {
        this.idTransacaoTransporte = id;
    }
}
