package com.serasa.DesafioTecnicoBackEnd.controllers;

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

@Controller
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
        if(id != tipoGrao.getId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id informado para atualização não bate com o requisitado");
        }
        return tipoGraoRepository.save(tipoGrao);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        tipoGraoRepository.deleteById(id);
    }


}
