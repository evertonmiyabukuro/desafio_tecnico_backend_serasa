package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.*;
import com.serasa.DesafioTecnicoBackEnd.repository.BalancaRepository;
import com.serasa.DesafioTecnicoBackEnd.repository.CaminhaoRepository;
import com.serasa.DesafioTecnicoBackEnd.services.FilaPesagensEmMemoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path="/pesagens")
public class PesagensController {
    private final FilaPesagensEmMemoriaService filaPesagensEmMemoriaService;
    @Autowired
    private CaminhaoRepository caminhaoRepository;
    @Autowired
    private BalancaRepository balancaRepository;

    public PesagensController(FilaPesagensEmMemoriaService filaPesagens) {
        this.filaPesagensEmMemoriaService = filaPesagens;
    }

    @Operation(summary = "Registrar um peso para uma balança", description = "Registra a informação de peso para determinada balança e placa, com as informações enviadas pelo ESP32.")
    @ApiResponse(responseCode = "200", description = "Inserção efetuada com sucesso. Retorna o ID da pesagem, utilizado para finalizar a transação de transporte vinculada ao caminhão.")
    @ApiResponse(responseCode = "404", description = "As informações enviadas pelo ESP32 são inválidas. Foi enviado o objeto com um ID de pesagem expirado.")
    @PostMapping
    public EfetuarPesagemRespostaDTO EfetuarPesagem(@RequestHeader("Authorization") @RequestBody PesagemDTO dadosPesagem){
        caminhaoRepository.findById(dadosPesagem.getPlate()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "A placa informada na requisição não pertence a nenhum caminhão cadastrado"));
        balancaRepository.findById(dadosPesagem.getIdBalanca()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "A balança informada na requisição não está cadastrada"));

        if(!filaPesagensEmMemoriaService.objetoPesagemValido(dadosPesagem)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O identificador para os dados de pesagem informados são inválidos");
        }

        filaPesagensEmMemoriaService.adicionarRegistroPesagem(dadosPesagem);

        String idPesagem = dadosPesagem.getPesagemId();

        return new EfetuarPesagemRespostaDTO(idPesagem);
    }
}
