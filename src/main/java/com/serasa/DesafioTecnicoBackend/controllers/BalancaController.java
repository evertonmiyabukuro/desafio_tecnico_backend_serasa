package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.FilialModel;
import com.serasa.DesafioTecnicoBackEnd.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.BalancaModel;
import com.serasa.DesafioTecnicoBackEnd.repository.BalancaRepository;

@RestController
@RequestMapping(path="/cadastros/Balanca")
public class BalancaController {
    @Autowired
    private BalancaRepository balancaRepository;
    @Autowired
    private FilialRepository filialRepository;

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
        FilialModel filial = filialRepository.findById(balanca.getFilial().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial não encontrada"));
        balanca.setFilial(filial);

        return balancaRepository.save(balanca);
    }

    @PutMapping("/{id}")
    public BalancaModel atualizarDados(@PathVariable int id, @RequestBody BalancaModel balanca){
        BalancaModel balancaNoSistema = balancaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        FilialModel filial = filialRepository.findById(balanca.getFilial().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial não encontrada"));
        balancaNoSistema.setFilial(filial);

        return balancaRepository.save(balancaNoSistema);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        balancaRepository.deleteById(id);
    }
}
