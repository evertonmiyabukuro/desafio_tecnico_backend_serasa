package com.serasa.DesafioTecnicoBackEnd.controllers.relatorios;

import com.serasa.DesafioTecnicoBackEnd.models.*;
import com.serasa.DesafioTecnicoBackEnd.models.dtos.*;
import com.serasa.DesafioTecnicoBackEnd.services.RelatoriosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/relatorios")
public class RelatoriosController {
    private final RelatoriosService relatoriosService;

    public RelatoriosController(RelatoriosService relatoriosService) {
        this.relatoriosService = relatoriosService;
    }

    @Operation(summary = "Emitir um relatório de pesagem", description = "Retorna todas as pesagens encontradas para determinada filial, caminhão, tipo de grão e período (referência inicial e final) ")
    @ApiResponse(responseCode = "200", description = "Relatório emitido com sucesso")
    @PostMapping("/pesagens")
    public @ResponseBody Iterable<PesagensModel> getRelatorioPesagens(@RequestBody RequisicaoRelatorioPesagensDTO filtros) {
        return relatoriosService.relatorioPesagens(filtros);
    }

    @Operation(summary = "Emitir um relatório de custos", description = "Emite um relatório de custos filtrados por determinada filial, caminhão, tipo de grão e período (referência inicial e final) ")
    @ApiResponse(responseCode = "200", description = "Relatório emitido com sucesso")
    @PostMapping("/custos")
    public @ResponseBody Iterable<RelatorioCustosDTO> getRelatorioCustos(@RequestBody RequisicaoRelatorioCustosDTO filtros) {
        return relatoriosService.relatorioCustosCompra(filtros);
    }

    @Operation(summary = "Emitir um relatório de lucros possíveis", description = "Emite um relatório de lucros possíveis filtrados por determinada filial, caminhão, tipo de grão e período (referência inicial e final) ")
    @ApiResponse(responseCode = "200", description = "Relatório emitido com sucesso")
    @PostMapping("/lucrosPossiveis")
    public @ResponseBody Iterable<RelatorioLucrosPossiveisDTO> getRelatorioLucrosPossiveis(@RequestBody RequisicaoRelatorioLucrosPossiveisDTO filtros) {
        return relatoriosService.relatorioLucrosPossiveis(filtros);
    }


}
