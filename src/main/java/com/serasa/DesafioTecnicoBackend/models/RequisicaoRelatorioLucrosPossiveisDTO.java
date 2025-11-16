package com.serasa.DesafioTecnicoBackEnd.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequisicaoRelatorioLucrosPossiveisDTO {
    private Integer idFilial;
    private String placa;
    private Integer idTipoGrao;

    private LocalDateTime periodoInicial;
    private LocalDateTime periodoFinal;
}
