package com.serasa.DesafioTecnicoBackEnd;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CaminhaoModel {
    @Id
    private String Placa;
    private float Tara;
}