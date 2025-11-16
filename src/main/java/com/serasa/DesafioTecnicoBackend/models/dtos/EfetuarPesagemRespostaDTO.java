package com.serasa.DesafioTecnicoBackEnd.models.dtos;

import lombok.Data;

@Data
public class EfetuarPesagemRespostaDTO {
    private String idPesagem;

    public EfetuarPesagemRespostaDTO(String id) {
        this.idPesagem = id;
    }
}
