package com.serasa.DesafioTecnicoBackEnd.controllers.cadastros;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.FilialModel;
import com.serasa.DesafioTecnicoBackEnd.repository.FilialRepository;

@RestController
@RequestMapping(path="/cadastros/Filial")
public class FilialController {
    @Autowired
    private FilialRepository filialRepository;

    @Operation(summary = "Retornar todas as filiais", description = "Retorna uma lista JSON com todas as filiais cadastradas no sistema")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @GetMapping
    public @ResponseBody Iterable<FilialModel> getTodos() {
        return filialRepository.findAll();
    }

    @Operation(summary = "Busca uma filial", description = "Retorna uma filial, para o ID informado")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Filial não encontrada para o ID informado")
    @GetMapping("/{idFilial}")
    public FilialModel getPorId(@PathVariable int idFilial) {
        return filialRepository.findById(idFilial).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial não encontrada"));
    }

    @Operation(summary = "Insere uma filial", description = "Insere uma filial com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Inserção efetuada com sucesso")
    @ApiResponse(responseCode = "412", description = "O nome da filial é inválido. Ele deve ter no mínimo 3 caracteres."
    )
    @PostMapping
    public FilialModel inserir(@RequestBody FilialModel filial){
        if (filial.getNome() == null || filial.getNome().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "O nome da filial deve conter no mínimo 3 caracteres"
            );
        }

        return filialRepository.save(filial);
    }

    @Operation(summary = "Atualizar uma filial", description = "Atualiza uma filial já existente com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Atualização efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Filial com o ID solicitado não encontrada")
    @ApiResponse(responseCode = "412", description = "O nome da filial é inválido. Ele deve ter no mínimo 3 caracteres.")
    @PutMapping("/{idFilial}")
    public FilialModel atualizarDados(@PathVariable int idFilial, @RequestBody FilialModel filial){
        if (filial.getNome() == null || filial.getNome().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "O nome da filial deve conter no mínimo 3 caracteres"
            );
        }

        FilialModel filialNoSistema = filialRepository.findById(idFilial).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para atualização não foi localizado"));

        filialNoSistema.setNome(filial.getNome());

        return filialRepository.save(filialNoSistema);
    }

    @Operation(summary = "Excluir uma filial", description = "Exclui a filial de ID informado no sistema")
    @ApiResponse(responseCode = "200", description = "Exclusão efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Filial não encontrada")
    @DeleteMapping("/{idFilial}")
    public void delete(@PathVariable int idFilial) {
        FilialModel filialNoSistema = filialRepository.findById(idFilial).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O id informado para exclusão não foi localizado"));

        filialRepository.delete(filialNoSistema);
    }
}
