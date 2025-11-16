package com.serasa.DesafioTecnicoBackEnd.models;

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
    private Integer id;

    @ManyToOne
    @JoinColumn(name="placa", nullable = false)
    private CaminhaoModel caminhao;

    @Column(name = "peso_bruto_estabilizado")
    private Float pesoBrutoEstabilizado;
    private Float tara;

    @Column(name = "peso_liquido")
    private Float pesoLiquido;
    @Column(name = "data_hora_pesagem")
    private LocalDateTime dataHoraPesagem;

    @ManyToOne
    @JoinColumn(name="id_balanca", nullable = false)
    private BalancaModel balanca;

    @ManyToOne
    @JoinColumn(name="id_tipo_grao", nullable = false)
    private TipoGraoModel tipoGrao;

    @Column(name = "custo_carga")
    private Float custoCarga;
}