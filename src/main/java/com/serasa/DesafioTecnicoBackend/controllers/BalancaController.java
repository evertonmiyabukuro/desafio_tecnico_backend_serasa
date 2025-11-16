package com.serasa.DesafioTecnicoBackEnd.controllers;

import com.serasa.DesafioTecnicoBackEnd.models.FilialModel;
import com.serasa.DesafioTecnicoBackEnd.repository.FilialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "Retornar todas as balanças", description = "Retorna uma lista JSON com todas as balanças cadastradas no sistema")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @GetMapping
    public @ResponseBody Iterable<BalancaModel> getTodas() {
        return balancaRepository.findAll();
    }

    @Operation(summary = "Busca uma balança", description = "Retorna uma balança, para o ID informado")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Balança não encontrada para o ID informado")
    @GetMapping("/{idBalanca}")
    public BalancaModel getPorId(@PathVariable int idBalanca) {
        return balancaRepository.findById(idBalanca).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Balança não encontrado"));
    }

    @Operation(summary = "Insere uma balança", description = "Insere uma balança com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Inserção efetuada com sucesso")
    @ApiResponse(responseCode = "412", description = "Filial da balança informada não encontrada")
    @PostMapping
    public BalancaModel inserir(@RequestBody BalancaModel balanca){
        FilialModel filial = filialRepository.findById(balanca.getFilial().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Filial não encontrada"));
        balanca.setFilial(filial);

        return balancaRepository.save(balanca);
    }

    @Operation(summary = "Atualizar uma balança", description = "Atualiza uma balança já existente com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Atualização efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Balança com o ID solicitado não encontrada")
    @ApiResponse(responseCode = "412", description = "Filial da balança informada não encontrada")
    @PutMapping("/{idBalanca}")
    public BalancaModel atualizarDados(@PathVariable int idBalanca, @RequestBody BalancaModel balanca){
        BalancaModel balancaNoSistema = balancaRepository.findById(idBalanca).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        FilialModel filial = filialRepository.findById(balanca.getFilial().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Filial não encontrada"));
        balancaNoSistema.setFilial(filial);

        return balancaRepository.save(balancaNoSistema);
    }

    @Operation(summary = "Excluir uma balança", description = "Exclui a balança de ID informado no sistema")
    @ApiResponse(responseCode = "200", description = "Exclusão efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Balança com o ID solicitado não encontrada")
    @DeleteMapping("/{idBalanca}")
    public void delete(@PathVariable int idBalanca) {
        BalancaModel balancaNoSistema = balancaRepository.findById(idBalanca).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A balança informada para exclusão não foi localizada"));
        balancaRepository.delete(balancaNoSistema);
    }
}
