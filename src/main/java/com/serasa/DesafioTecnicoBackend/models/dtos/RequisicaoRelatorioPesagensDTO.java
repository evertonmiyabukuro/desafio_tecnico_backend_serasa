package com.serasa.DesafioTecnicoBackEnd.models.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequisicaoRelatorioPesagensDTO {

    private Integer idFilial;
    private String placa;
    private Integer idTipoGrao;

    @NotNull(message = "Data inicial é obrigatória")
    private LocalDateTime periodoInicial;
    @NotNull(message = "Data final é obrigatória")
    private LocalDateTime periodoFinal;
}
