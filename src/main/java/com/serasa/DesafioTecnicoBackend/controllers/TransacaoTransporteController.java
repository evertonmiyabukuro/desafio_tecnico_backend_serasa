package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.*;
import com.serasa.DesafioTecnicoBackEnd.repository.BalancaRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.CaminhaoRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.TipoGraoRepository;
import com.serasa.DesafioTecnicoBackEnd.services.FilaPesagensEmMemoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.repository.TransacaoTransporteRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.PesagensRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path="/TransacaoTransporte")
public class TransacaoTransporteController {

    @Autowired
    private TransacaoTransporteRepository transacaoTransporteRepository;
    @Autowired
    private PesagensRepository pesagensRepository;
    @Autowired
    private BalancaRepository balancaRepository;
    @Autowired
    private CaminhaoRepository caminhaoRepository;
    private final FilaPesagensEmMemoriaService filaPesagensEmMemoriaService;
    @Autowired
    private TipoGraoRepository tipoGraoRepository;

    public TransacaoTransporteController(FilaPesagensEmMemoriaService filaPesagens) {
        this.filaPesagensEmMemoriaService = filaPesagens;
    }

    @Operation(summary = "Iniciar uma transação de transporte", description = "Inicia uma transação de transporte com as informações informadas")
    @ApiResponse(responseCode = "200", description = "Transação de transporte iniciada. É retornado o ID da mesma.")
    @ApiResponse(responseCode = "412", description = "O caminhão ou tipo de grão informado para a transação de transporte não está cadastrado no sistema.")
    @PostMapping(path="/abrir")
    public TransacaoTransporteRespostaDTO abrirTransacaoTransporte(@RequestBody TransacaoTransporteModel transacaoTransporte){
        CaminhaoModel caminhaoEncontrado = caminhaoRepository.findById(transacaoTransporte.getCaminhao().getPlaca()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Caminhão não encontrado"));
        TipoGraoModel tipoGraoEncontrado = tipoGraoRepository.findById(transacaoTransporte.getGrao().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Tipo do grão não encontrado"));

        transacaoTransporte.setCaminhao(caminhaoEncontrado);
        transacaoTransporte.setGrao(tipoGraoEncontrado);
        transacaoTransporte.setPesagem(null);

        Integer idGerado = transacaoTransporteRepository.save(transacaoTransporte).getId();
        return new TransacaoTransporteRespostaDTO(idGerado);
    }

    @Operation(summary = "Finaliza uma transação de transporte", description = "Finaliza uma transação de transporte com o id e id de pesagem informados")
    @ApiResponse(responseCode = "200", description = "Transação de transporte finalizada com sucesso.")
    @ApiResponse(responseCode = "400", description = "A transação de transporte com o ID informado não foi encontrada no sistema ou o registro de pesagem para o ID informado não foi encontrado, está instável ou está expirado.")
    @ApiResponse(responseCode = "412", description = "O caminhão ou a balança informados para a transação de transporte não estão cadastrados no sistema.")
    @PutMapping(path="/finalizar/{idTransacaoTransporte}/{idPesagem}")
    public TransacaoTransporteModel finalizarTransacaoTransporte(@PathVariable int idTransacaoTransporte, @PathVariable String idPesagem){
        TransacaoTransporteModel transacaoTransporteAAtualizar = transacaoTransporteRepository.findById(idTransacaoTransporte).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação de transporte não encontrada"));

        ResultadoPesagemDTO resultadoPesagem = this.filaPesagensEmMemoriaService.extrairRegistroCasoEstavel(idPesagem);
        if (resultadoPesagem == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "O registro de pesagem não está válido para finalização. Ele pode ");
        }

        BalancaModel balancaOndeFoiPesado =  balancaRepository.findById(resultadoPesagem.idBalanca()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Balança não encontrada para a pesagem informada"));
        CaminhaoModel caminhaoDaPesagem = caminhaoRepository.findById(resultadoPesagem.placa()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Caminhão não encontrado para a pesagem informada"));

        PesagensModel pesagem = new PesagensModel();

        pesagem.setCaminhao(caminhaoDaPesagem);
        pesagem.setPeso_bruto_estabilizado(resultadoPesagem.pesoRegistrado());
        pesagem.setTara(caminhaoDaPesagem.getTara());
        pesagem.setPeso_liquido(resultadoPesagem.pesoRegistrado()-caminhaoDaPesagem.getTara());
        pesagem.setData_hora_pesagem(LocalDateTime.now());
        pesagem.setBalanca(balancaOndeFoiPesado);
        pesagem.setTipoGrao(transacaoTransporteAAtualizar.getGrao());
        pesagem.setCusto_carga(transacaoTransporteAAtualizar.getGrao().getCustoPorTonelada() * (pesagem.getPeso_liquido()/1000));

        pesagensRepository.save(pesagem);

        transacaoTransporteAAtualizar.setData_Hora_Retorno(LocalDateTime.now());
        transacaoTransporteAAtualizar.setPesagem(pesagem);

        return transacaoTransporteRepository.save(transacaoTransporteAAtualizar);
    }
}
