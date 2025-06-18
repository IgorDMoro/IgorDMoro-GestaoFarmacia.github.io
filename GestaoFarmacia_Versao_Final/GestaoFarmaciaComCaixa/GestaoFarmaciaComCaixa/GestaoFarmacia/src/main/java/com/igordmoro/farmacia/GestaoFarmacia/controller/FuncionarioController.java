package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.service.FuncionarioService;
import com.igordmoro.farmacia.GestaoFarmacia.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final CargoService cargoService;

    @Autowired
    public FuncionarioController(FuncionarioService funcionarioService, CargoService cargoService) {
        this.funcionarioService = funcionarioService;
        this.cargoService = cargoService;
    }

    /**
     * Cria um novo funcionário. O salário líquido e o imposto são calculados no serviço.
     *
     * @param funcionario O objeto Funcionario a ser criado (com id do Cargo).
     * @return O Funcionario criado com seus valores calculados.
     */
    @PostMapping
    public ResponseEntity<Funcionario> criarFuncionario(@RequestBody Funcionario funcionario) {
        try {
            Funcionario novoFuncionario = funcionarioService.salvarFuncionario(funcionario);
            return new ResponseEntity<>(novoFuncionario, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar funcionário: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos os funcionários.
     *
     * @return Uma lista de todos os funcionários com seus salários líquidos e impostos calculados.
     */
    @GetMapping
    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioService.listarTodosFuncionarios();
    }

    /**
     * Busca um funcionário pelo seu ID.
     *
     * @param id O ID do funcionário.
     * @return O Funcionario correspondente, ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioPorId(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioService.buscarFuncionarioPorId(id);
        return funcionario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Atualiza os dados de um funcionário existente.
     *
     * @param id O ID do funcionário a ser atualizado.
     * @param funcionarioDetails Um objeto Funcionario com os dados a serem atualizados.
     * @return O Funcionario atualizado com seus novos valores calculados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizarFuncionario(@PathVariable Long id, @RequestBody Funcionario funcionarioDetails) {
        try {
            // A chamada correta para o método no service
            Funcionario funcionarioAtualizado = funcionarioService.atualizarFuncionario(id, funcionarioDetails);
            return ResponseEntity.ok(funcionarioAtualizado);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar funcionário: " + e.getMessage(), e);
        }
    }

    /**
     * Deleta um funcionário pelo seu ID.
     *
     * @param id O ID do funcionário a ser deletado.
     * @return Resposta sem conteúdo (204 No Content) se sucesso, ou 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        try {
            funcionarioService.deletarFuncionario(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao deletar funcionário: " + e.getMessage(), e);
        }
    }

    /**
     * Lista funcionários por setor.
     *
     * @param setor O nome do setor (string).
     * @return Uma lista de funcionários do setor especificado.
     */
    @GetMapping("/por-setor/{setor}")
    public List<Funcionario> listarFuncionariosPorSetor(@PathVariable String setor) {
        try {
            Setor setorEnum = Setor.valueOf(setor.toUpperCase().replace(" ", "_"));
            return funcionarioService.buscarFuncionariosPorSetor(setorEnum);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setor inválido: " + setor, e);
        }
    }

    /**
     * Retorna a contagem de funcionários por setor.
     *
     * @return Um mapa com a contagem de funcionários por setor.
     */
    @GetMapping("/contagem-por-setor")
    public Map<String, Long> getContagemFuncionariosPorSetor() {
        return funcionarioService.contarFuncionariosPorSetor();
    }
}