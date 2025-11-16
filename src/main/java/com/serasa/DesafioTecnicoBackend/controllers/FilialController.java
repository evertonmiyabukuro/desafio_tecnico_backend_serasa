package com.serasa.DesafioTecnicoBackEnd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.FilialModel;
import com.serasa.DesafioTecnicoBackEnd.repository.FilialRepository;

@Controller
@RequestMapping(path="/cadastros/Filial")
public class FilialController {
    @Autowired
    private FilialRepository filialRepository;

    @GetMapping
    public @ResponseBody Iterable<FilialModel> getTodos() {
        // This returns a JSON or XML with the users
        return filialRepository.findAll();
    }

    @GetMapping("/{id}")
    public FilialModel getPorId(@PathVariable int id) {
        // This returns a JSON or XML with the users
        return filialRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial não encontrada"));
    }

    @PostMapping
    public FilialModel inserir(@RequestBody FilialModel balanca){
        return filialRepository.save(balanca);
    }

    @PutMapping("/{id}")
    public FilialModel atualizarDados(@PathVariable int id, @RequestBody FilialModel balanca){
        if(id != balanca.getId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id informado para atualização não bate com o requisitado");
        }
        return filialRepository.save(balanca);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        filialRepository.deleteById(id);
    }
}
