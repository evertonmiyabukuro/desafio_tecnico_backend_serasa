package com.serasa.DesafioTecnicoBackEnd.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "filial")
public class FilialModel {
    @Id
    private int Id;
    private String Nome;
}