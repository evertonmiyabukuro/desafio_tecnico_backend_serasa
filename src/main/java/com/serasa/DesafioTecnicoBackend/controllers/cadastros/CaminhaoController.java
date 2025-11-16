package com.serasa.DesafioTecnicoBackEnd.controllers.cadastros;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.serasa.DesafioTecnicoBackEnd.models.CaminhaoModel;
import com.serasa.DesafioTecnicoBackEnd.repository.CaminhaoRepository;

@RestController
@RequestMapping(path="/cadastros/Caminhao")
public class CaminhaoController {
    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @Operation(summary = "Retornar todas os caminhões", description = "Retorna uma lista JSON com todas os caminhões cadastradaos no sistema")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @GetMapping
    public @ResponseBody Iterable<CaminhaoModel> getTodos() {
        return caminhaoRepository.findAll();
    }

    @Operation(summary = "Busca um caminhão", description = "Retorna um caminhão, para a placa informada")
    @ApiResponse(responseCode = "200", description = "Busca efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Caminhão não encontrada para a placa informada")
    @GetMapping("/{placa}")
    public CaminhaoModel getPorPlaca(@PathVariable String placa) {
        return caminhaoRepository.findById(placa).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caminhão não encontrado"));
    }

    @Operation(summary = "Insere um caminhão", description = "Insere um caminhão com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Inserção efetuada com sucesso")
    @ApiResponse(responseCode = "412", description = "A placa informada não segue o padrão do modelo Mercosul ou a tara é inválida (<= 0)")
    @PostMapping
    public CaminhaoModel inserir(@RequestBody CaminhaoModel caminhao) {
        if (caminhao.getPlaca() == null || !caminhao.getPlaca().matches("(?i)^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "A placa informada não segue o padrão do modelo Mercosul"
            );
        }

        if (caminhao.getTara() == null || caminhao.getTara() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "A tara informada deve ser maior que zero"
            );
        }

        caminhao.setPlaca(caminhao.getPlaca().toUpperCase());

        return caminhaoRepository.save(caminhao);
    }

    @Operation(summary = "Atualizar um caminhão", description = "Atualiza um caminhão já existente com os dados informados no corpo")
    @ApiResponse(responseCode = "200", description = "Atualização efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Caminhão com a placa solicitada não encontrado")
    @ApiResponse(responseCode = "412", description = "A placa informada não segue o padrão do modelo Mercosul ou a tara é inválida (<= 0)")
    @PutMapping("/{placa}")
    public CaminhaoModel atualizarDados(@PathVariable String placa, @RequestBody CaminhaoModel caminhao){
        if (placa == null || !placa.matches("(?i)^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "A placa informada não segue o padrão do modelo Mercosul"
            );
        }

        if (caminhao.getTara() == null || caminhao.getTara() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.PRECONDITION_FAILED,
                    "A tara informada deve ser maior que zero"
            );
        }

        CaminhaoModel caminhaoNoSistema = caminhaoRepository.findById(placa).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A placa informada para atualização não foi localizada"));

        caminhaoNoSistema.setTara( caminhao.getTara());
        return caminhaoRepository.save(caminhaoNoSistema);
    }

    @Operation(summary = "Excluir um caminhão", description = "Exclui o caminhão de placa informada no sistema")
    @ApiResponse(responseCode = "200", description = "Exclusão efetuada com sucesso")
    @ApiResponse(responseCode = "404", description = "Caminhão com a placa solicitada não encontrado")
    @DeleteMapping("/{placa}")
    public void delete(@PathVariable String placa) {
        CaminhaoModel caminhaoNoSistema = caminhaoRepository.findById(placa).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A placa informada para exclusão não foi localizada"));

        caminhaoRepository.delete(caminhaoNoSistema);
    }

}