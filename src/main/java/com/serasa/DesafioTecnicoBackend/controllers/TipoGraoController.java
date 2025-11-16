package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.FilialModel;
import com.serasa.DesafioTecnicoBackEnd.repository.TipoGraoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.TipoGraoModel;
import com.serasa.DesafioTecnicoBackEnd.repository.TipoGraoRepository;

import java.util.List;

@RestController
@RequestMapping(path="/cadastros/TipoGrao")
public class TipoGraoController {
    @Autowired
    private TipoGraoRepository tipoGraoRepository;

    @GetMapping
    public Iterable<TipoGraoModel> getAll() {
        return tipoGraoRepository.findAll();
    }

    @GetMapping("/{id}")
    public TipoGraoModel getById(@PathVariable int id) {
        return tipoGraoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de grão não encontrado"));
    }

    @PostMapping
    public TipoGraoModel inserir(@RequestBody TipoGraoModel tipoGrao) {
        return tipoGraoRepository.save(tipoGrao);
    }

    @PutMapping("/{id}")
    public TipoGraoModel atualizar(@PathVariable int id, @RequestBody TipoGraoModel tipoGrao) {
        TipoGraoModel tipoGraoNoSistema = tipoGraoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        if(tipoGrao.getNome()!=null) {
            tipoGraoNoSistema.setNome(tipoGrao.getNome());
        }
        if(tipoGrao.getCustoPorTonelada()!=null) {
            tipoGraoNoSistema.setCustoPorTonelada(tipoGrao.getCustoPorTonelada());
        }


        return tipoGraoRepository.save(tipoGraoNoSistema);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        tipoGraoRepository.deleteById(id);
    }


}
