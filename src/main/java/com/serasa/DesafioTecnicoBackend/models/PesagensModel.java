package com.serasa.DesafioTecnicoBackEnd.models;

import com.serasa.DesafioTecnicoBackEnd.models.CaminhaoModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "pesagens")
public class PesagensModel {
    @Id
    private int Id;

    @ManyToOne
    @JoinColumn(name="placa", nullable = false)
    private CaminhaoModel Caminhao;

    private float peso_bruto_estabilizado;
    private float tara;

    private float peso_liquido;
    private LocalDateTime data_hora_pesagem;

    @ManyToOne
    @JoinColumn(name="id_balanca", nullable = false)
    private BalancaModel balanca;

    @ManyToOne
    @JoinColumn(name="id_tipo_grao", nullable = false)
    private TipoGraoModel TipoGrao;

    private float Custo_carga;
}