package com.serasa.DesafioTecnicoBackEnd.models;

import com.serasa.DesafioTecnicoBackEnd.models.CaminhaoModel;
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
    private int Id;

    @ManyToOne
    @JoinColumn(name="id_grao", nullable = false)
    private TipoGraoModel Grao;

    @ManyToOne
    @JoinColumn(name="placa_caminhao", nullable = false)
    private CaminhaoModel Caminhao;

    private float Volume_Comprado;


    private int Id_Pesagem;

    private LocalDateTime Data_Hora_Saida;
    private LocalDateTime Data_Hora_Retorno;
}
