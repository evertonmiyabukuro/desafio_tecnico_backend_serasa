package com.serasa.DesafioTecnicoBackEnd.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tipograo")
public class TipoGraoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private String Nome;
    private Float CustoPorTonelada;
}