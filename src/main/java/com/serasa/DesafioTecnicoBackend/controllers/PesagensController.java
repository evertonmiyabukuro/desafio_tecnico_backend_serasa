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
import com.serasa.DesafioTecnicoBackEnd.repository.repository.PesagensRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping(path="/pesagens")
public class PesagensController {
    @Autowired
    private PesagensRepository pesagensRepository;

    private final FilaPesagensEmMemoriaService filaPesagensEmMemoriaService;

    public PesagensController(FilaPesagensEmMemoriaService filaPesagens) {
        this.filaPesagensEmMemoriaService = filaPesagens;
    }

    @PostMapping
    public String EfetuarPesagem(@RequestBody PesagemDTO dadosPesagem){
        if(!filaPesagensEmMemoriaService.objetoPesagemValido(dadosPesagem)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O identificador para os dados de pesagem informados são inválidos");
        }

        filaPesagensEmMemoriaService.adicionarRegistroPesagem(dadosPesagem);

        return dadosPesagem.getPesagemId();

        //Validar se já tem uma pesagem rodando para esse caminhão
        //Se não tiver, gerar um novo UUID e inserir em lista


        //Ao final, retornar o id do objeto de pesagem para o cliente incluir nas requisições futuras

//        return UUID.randomUUID();
    }
}
