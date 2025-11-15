package com.serasa.DesafioTecnicoBackEnd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping(path="/cadastros/Caminhao")
public class CaminhaoController {
    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @GetMapping
    public @ResponseBody Iterable<CaminhaoModel> getTodos() {
        // This returns a JSON or XML with the users
        return caminhaoRepository.findAll();
    }

    @GetMapping("/{placa}")
    public CaminhaoModel getPorPlaca(@PathVariable String placa) {
        // This returns a JSON or XML with the users
        return caminhaoRepository.findById(placa).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caminhão não encontrado"));
    }

    @PostMapping
    public CaminhaoModel inserir(@RequestBody CaminhaoModel caminhao){
        return caminhaoRepository.save(caminhao);
    }

    @PutMapping("/{placa}")
    public CaminhaoModel atualizarDados(@PathVariable String placa, @RequestBody CaminhaoModel caminhao){
        if(placa != caminhao.getPlaca()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A placa informada para atualização não bate com a requisitada");
        }
        return caminhaoRepository.save(caminhao);
    }

    @DeleteMapping("/{placa}")
    public void delete(@PathVariable String placa) {
        caminhaoRepository.deleteById(placa);
    }

}