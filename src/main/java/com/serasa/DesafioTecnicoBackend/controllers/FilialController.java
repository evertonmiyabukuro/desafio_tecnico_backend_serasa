package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.BalancaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.FilialModel;
import com.serasa.DesafioTecnicoBackEnd.repository.FilialRepository;

@RestController
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
    public FilialModel inserir(@RequestBody FilialModel filial){
        return filialRepository.save(filial);
    }

    @PutMapping("/{id}")
    public FilialModel atualizarDados(@PathVariable int id, @RequestBody FilialModel filial){
        FilialModel filialNoSistema = filialRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        filialNoSistema.setNome(filial.getNome());

        return filialRepository.save(filialNoSistema);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        filialRepository.deleteById(id);
    }
}
