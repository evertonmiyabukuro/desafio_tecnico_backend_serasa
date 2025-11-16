package com.serasa.DesafioTecnicoBackEnd.models;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "balanca")
public class BalancaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;
    @Column(name = "identificadorAutorizacao")
    private String identificadorAutorizacao;
    @ManyToOne
    @JoinColumn(name="id_filial", nullable = false)
    private FilialModel Filial;

    @PrePersist
    public void generateUUID() {
        if (this.identificadorAutorizacao == null) {
            this.identificadorAutorizacao = UUID.randomUUID().toString();
        }
    }
}
