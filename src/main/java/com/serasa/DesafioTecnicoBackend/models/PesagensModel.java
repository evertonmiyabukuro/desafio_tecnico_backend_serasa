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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name="placa", nullable = false)
    private CaminhaoModel Caminhao;

    private Float peso_bruto_estabilizado;
    private Float tara;

    private Float peso_liquido;
    private LocalDateTime data_hora_pesagem;

    @ManyToOne
    @JoinColumn(name="id_balanca", nullable = false)
    private BalancaModel balanca;

    @ManyToOne
    @JoinColumn(name="id_tipo_grao", nullable = false)
    private TipoGraoModel TipoGrao;

    private Float Custo_carga;
}