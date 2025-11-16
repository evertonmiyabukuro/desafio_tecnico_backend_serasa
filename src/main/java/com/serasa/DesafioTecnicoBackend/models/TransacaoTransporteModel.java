package com.serasa.DesafioTecnicoBackEnd.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transacaotransporte")
public class TransacaoTransporteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="id_grao", nullable = false)
    private TipoGraoModel grao;

    @ManyToOne
    @JoinColumn(name="placa_caminhao", nullable = false)
    private CaminhaoModel caminhao;

    @Column(name = "volume_comprado")
    private Float volumeComprado;

    @OneToOne
    @JoinColumn(name = "id_pesagem")
    private PesagensModel pesagem;

    @Column(name = "data_hora_saida")
    private LocalDateTime dataHoraSaida;
    @Column(name = "data_hora_retorno")
    private LocalDateTime dataHoraRetorno;
}
