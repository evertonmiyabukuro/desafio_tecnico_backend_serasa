package com.serasa.DesafioTecnicoBackEnd.models;
import lombok.Data;

@Data
public class PesagemDTO {
    private int IdBalanca;
    private String Plate;
    private float Weight;
    private String PesagemId;
}
