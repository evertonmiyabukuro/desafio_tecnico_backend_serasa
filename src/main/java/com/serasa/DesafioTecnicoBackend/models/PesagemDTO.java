package com.serasa.DesafioTecnicoBackEnd.models;
import lombok.Data;

@Data
public class PesagemDTO {
    private Integer idBalanca;
    private String plate;
    private Float weight;
    private String pesagemId;
}
