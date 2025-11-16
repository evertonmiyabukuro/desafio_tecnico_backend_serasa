package com.serasa.DesafioTecnicoBackEnd.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.BalancaModel;
import com.serasa.DesafioTecnicoBackEnd.repository.BalancaRepository;

@Controller
@RequestMapping(path="/cadastros/Balanca")
public class BalancaController {
    @Autowired
    private BalancaRepository balancaRepository;

    @GetMapping
    public @ResponseBody Iterable<BalancaModel> getTodos() {
        // This returns a JSON or XML with the users
        return balancaRepository.findAll();
    }

    @GetMapping("/{id}")
    public BalancaModel getPorId(@PathVariable int id) {
        // This returns a JSON or XML with the users
        return balancaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Balança não encontrado"));
    }

    @PostMapping
    public BalancaModel inserir(@RequestBody BalancaModel balanca){
        return balancaRepository.save(balanca);
    }

    @PutMapping("/{id}")
    public BalancaModel atualizarDados(@PathVariable int id, @RequestBody BalancaModel balanca){
        if(id != balanca.getId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id informado para atualização não bate com o requisitado");
        }
        return balancaRepository.save(balanca);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        balancaRepository.deleteById(id);
    }
}
