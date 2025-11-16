package com.serasa.DesafioTecnicoBackEnd.controllers.cadastros;

import com.serasa.DesafioTecnicoBackEnd.repository.TipoGraoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.TipoGraoModel;

@RestController
@RequestMapping(path="/cadastros/TipoGrao")
public class TipoGraoController {
    @Autowired
    private TipoGraoRepository tipoGraoRepository;

    @Operation(summary = "Retornar todas os tipos de grão", description = "Retorna uma lista JSON com todas os tipos de grão cadastrados no sistema")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @GetMapping
    public Iterable<TipoGraoModel> getAll() {
        return tipoGraoRepository.findAll();
    }

    @Operation(summary = "Busca um tipo de grão", description = "Retorna um tipo de grão, para o ID informado")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tipo de grão não encontrado para o ID informado")
    @GetMapping("/{idTipoGrao}")
    public TipoGraoModel getById(@PathVariable int idTipoGrao) {
        return tipoGraoRepository.findById(idTipoGrao).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de grão não encontrado"));
    }

    @Operation(summary = "Insere um tipo de grão", description = "Insere um tipo de grão com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Inserção efetuada com sucesso")
    @ApiResponse(responseCode = "412", description = "O nome (mínimo 3 caracteres) e o custo por tonelada devem ser preenchidos."
    )
    @PostMapping
    public TipoGraoModel inserir(@RequestBody TipoGraoModel tipoGrao) {
        if (tipoGrao.getNome() == null || tipoGrao.getNome().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "O nome do tipo de grão deve ser preenchido com no mínimo 3 caracteres"
            );
        }

        if (tipoGrao.getCustoPorTonelada() == null) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "O custo por tonelada deve ser informado"
            );
        }

        return tipoGraoRepository.save(tipoGrao);
    }

    @Operation(summary = "Atualizar um tipo de grão", description = "Atualiza um tipo de grão já existente com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Atualização efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tipo de grão com o ID solicitado não encontrado")
    @ApiResponse(responseCode = "412", description = "O nome (mínimo 3 caracteres) e o custo por tonelada devem ser preenchidos.")
    @PutMapping("/{idTipoGrao}")
    public TipoGraoModel atualizar(@PathVariable int idTipoGrao, @RequestBody TipoGraoModel tipoGrao) {
        if (tipoGrao.getNome() != null && tipoGrao.getNome().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "O nome do tipo de grão deve ser preenchido"
            );
        }

        if (tipoGrao.getCustoPorTonelada() != null && tipoGrao.getCustoPorTonelada() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "O custo por tonelada deve ser informado e maior que zero"
            );
        }

        TipoGraoModel tipoGraoNoSistema = tipoGraoRepository.findById(idTipoGrao).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        if(tipoGrao.getNome()!=null) {
            tipoGraoNoSistema.setNome(tipoGrao.getNome());
        }
        if(tipoGrao.getCustoPorTonelada()!=null) {
            tipoGraoNoSistema.setCustoPorTonelada(tipoGrao.getCustoPorTonelada());
        }

        return tipoGraoRepository.save(tipoGraoNoSistema);
    }

    @Operation(summary = "Excluir um tipo de grão", description = "Exclui o tipo de grão de ID informado no sistema")
    @ApiResponse(responseCode = "200", description = "Exclusão efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tipo de grão com o ID solicitado não encontrado")
    @DeleteMapping("/{idTipoGrao}")
    public void delete(@PathVariable int idTipoGrao) {
        TipoGraoModel tipoGraoNoSistema = tipoGraoRepository.findById(idTipoGrao).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        tipoGraoRepository.delete(tipoGraoNoSistema);
    }


}
