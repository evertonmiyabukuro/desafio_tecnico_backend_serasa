package com.serasa.DesafioTecnicoBackEnd.models;

import jakarta.persistence.*;
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
    private Integer id;
    @Column(name = "identificador_autorizacao")
    private String identificadorAutorizacao;
    @ManyToOne
    @JoinColumn(name="id_filial", nullable = false)
    private FilialModel filial;

    @PrePersist
    public void generateUUID() {
        if (this.identificadorAutorizacao == null) {
            this.identificadorAutorizacao = UUID.randomUUID().toString();
        }
    }
}
