package com.serasa.DesafioTecnicoBackEnd.services;

import com.serasa.DesafioTecnicoBackEnd.models.*;
import com.serasa.DesafioTecnicoBackEnd.models.dtos.*;
import com.serasa.DesafioTecnicoBackEnd.repository.PesagensRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.specifications.PesagensSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatoriosService {
    @Autowired
    private PesagensRepository pesagensRepository;


    public List<RelatorioPesagensDTO> relatorioPesagens(RequisicaoRelatorioPesagensDTO filtros) {
        Specification<PesagensModel> spec = PesagensSpecification.filter(
                filtros.getIdFilial(),
                filtros.getPlaca(),
                filtros.getIdTipoGrao(),
                filtros.getPeriodoInicial(),
                filtros.getPeriodoFinal()
        );

        List<PesagensModel> pesagens = pesagensRepository.findAll(spec);

        return pesagens.stream()
                .map(p -> new RelatorioPesagensDTO() {
                    @Override
                    public String getFilial() {
                        return p.getBalanca().getFilial().getNome();
                    }

                    @Override
                    public String getPlaca() {
                        return p.getCaminhao().getPlaca();
                    }

                    @Override
                    public String getTipoGrao() {
                        return p.getTipoGrao().getNome();
                    }

                    @Override
                    public Float getPesoBrutoEstabilizado() {
                        return p.getPesoBrutoEstabilizado();
                    }

                    @Override
                    public Float getTara() {
                        return p.getTara();
                    }

                    @Override
                    public Float getPesoLiquido() {
                        return p.getPesoLiquido();
                    }

                    @Override
                    public Float getCustoCarga() {
                        return p.getCustoCarga();
                    }
                }).collect(Collectors.toList());
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
