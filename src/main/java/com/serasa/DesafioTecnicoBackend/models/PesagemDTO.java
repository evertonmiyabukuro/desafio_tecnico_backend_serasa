package com.serasa.DesafioTecnicoBackEnd.models;
import lombok.Data;

@Data
public class PesagemDTO {
    private Integer IdBalanca;
    private String Plate;
    private Float Weight;
    private String PesagemId;
}
