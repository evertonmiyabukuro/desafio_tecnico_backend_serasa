package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.*;
import com.serasa.DesafioTecnicoBackEnd.repository.BalancaRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.CaminhaoRepository;
import com.serasa.DesafioTecnicoBackEnd.services.FilaPesagensEmMemoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.repository.TransacaoTransporteRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.repository.PesagensRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping(path="/cadastros/TransacaoTransporte")
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

    public TransacaoTransporteController(FilaPesagensEmMemoriaService filaPesagens) {
        this.filaPesagensEmMemoriaService = filaPesagens;
    }

    @PostMapping(path="/abrir")
    public Integer abrirTransacaoTransporte(@RequestBody TransacaoTransporteModel transacaoTransporte){
        return transacaoTransporteRepository.save(transacaoTransporte).getId();
    }

    @PutMapping(path="/finalizar/{id}/{idPesagem}")
    public TransacaoTransporteModel finalizarTransacaoTransporte(@PathVariable int id, @PathVariable String idPesagem){
        TransacaoTransporteModel transacaoTransporteAAtualizar = transacaoTransporteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação de transporte não encontrada"));

        ResultadoPesagemDTO resultadoPesagem = this.filaPesagensEmMemoriaService.extrairRegistroCasoEstavel(idPesagem);
        if (resultadoPesagem == null){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "O registro de pesagem não está válido para finalização");
        }

        BalancaModel balancaOndeFoiPesado =  balancaRepository.findById(resultadoPesagem.idBalanca()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Balança não encontrada para a pesagem informada"));
        CaminhaoModel caminhaoDaPesagem = caminhaoRepository.findById(resultadoPesagem.placa()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caminhão não encontrado para a pesagem informada"));

        PesagensModel pesagem = new PesagensModel();

        pesagem.setCaminhao(caminhaoDaPesagem);
        pesagem.setPeso_bruto_estabilizado(resultadoPesagem.pesoRegistrado());
        pesagem.setTara(caminhaoDaPesagem.getTara());
        pesagem.setPeso_liquido(resultadoPesagem.pesoRegistrado()-caminhaoDaPesagem.getTara());
        pesagem.setData_hora_pesagem(LocalDateTime.now());
        pesagem.setBalanca(balancaOndeFoiPesado);
        pesagem.setTipoGrao(transacaoTransporteAAtualizar.getGrao());
        pesagem.setCusto_carga(transacaoTransporteAAtualizar.getGrao().getCustoPorTonelada() * pesagem.getPeso_liquido());

        pesagensRepository.save(pesagem);

        transacaoTransporteAAtualizar.setData_Hora_Retorno(LocalDateTime.now());
        transacaoTransporteAAtualizar.setId_Pesagem(pesagem.getId());

        return transacaoTransporteRepository.save(transacaoTransporteAAtualizar);
    }
}
