package com.serasa.DesafioTecnicoBackEnd.services;

import com.serasa.DesafioTecnicoBackEnd.models.*;
import com.serasa.DesafioTecnicoBackEnd.repository.FilialRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.PesagensRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatoriosService {
    @Autowired
    private PesagensRepository pesagensRepository;


    public List<PesagensModel> relatorioPesagens(RequisicaoRelatorioPesagensDTO filtros) {
        List<PesagensModel> pesagens = pesagensRepository.queryRelatorioPesagens(
                filtros.getIdFilial(),
                filtros.getPlaca(),
                filtros.getIdTipoGrao(),
                filtros.getPeriodoInicial(),
                filtros.getPeriodoFinal()
        );

        return pesagens.stream().toList();
    }

    public List<RelatorioCustosDTO> relatorioCustosCompra(RequisicaoRelatorioCustosDTO filtros) {
        List<RelatorioCustosDTO> custos = pesagensRepository.queryRelatorioCustosDeCompra(
                filtros.getIdFilial(),
                filtros.getPlaca(),
                filtros.getIdTipoGrao(),
                filtros.getPeriodoInicial(),
                filtros.getPeriodoFinal()
        );

        return custos.stream().toList();
    }

    public List<RelatorioLucrosPossiveisDTO> relatorioLucrosPossiveis(RequisicaoRelatorioLucrosPossiveisDTO filtros) {
        List<RelatorioLucrosPossiveisDTO> lucrosPossiveis = pesagensRepository.queryRelatorioLucrosPossiveis(
                filtros.getIdFilial(),
                filtros.getPlaca(),
                filtros.getIdTipoGrao(),
                filtros.getPeriodoInicial(),
                filtros.getPeriodoFinal()
        );

        return lucrosPossiveis.stream().toList();
    }
}
