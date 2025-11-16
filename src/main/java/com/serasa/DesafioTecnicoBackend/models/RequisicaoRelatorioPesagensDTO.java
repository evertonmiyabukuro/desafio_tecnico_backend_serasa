package com.serasa.DesafioTecnicoBackEnd.models;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RequisicaoRelatorioPesagensDTO {

    private Integer idFilial;
    private String placa;
    private Integer idTipoGrao;

    private LocalDateTime periodoInicial;
    private LocalDateTime periodoFinal;
}
