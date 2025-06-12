package com.igordmoro.farmacia.GestaoFarmacia.controller; // Pacote em minúsculas

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo; // Importe Cargo
import com.igordmoro.farmacia.GestaoFarmacia.service.FuncionarioService;
import com.igordmoro.farmacia.GestaoFarmacia.service.CargoService; // Importe CargoService para buscar o Cargo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final CargoService cargoService; // Injetar CargoService para buscar cargos

    @Autowired
    public FuncionarioController(FuncionarioService funcionarioService, CargoService cargoService) {
        this.funcionarioService = funcionarioService;
        this.cargoService = cargoService;
    }

    @PostMapping
    public ResponseEntity<Funcionario> criarFuncionario(@RequestBody Funcionario funcionario) {
        try {
            // Antes de passar para o serviço, garanta que o Cargo referenciado existe
            if (funcionario.getCargo() == null || funcionario.getCargo().getId() == null) {
                throw new IllegalArgumentException("Funcionário deve ter um cargo associado com um ID válido.");
            }
            // O FuncionarioService agora faz a busca do cargo por ID, então basta garantir que o ID está lá.
            Funcionario novoFuncionario = funcionarioService.salvarFuncionario(funcionario);
            return new ResponseEntity<>(novoFuncionario, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @GetMapping
    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioService.listarTodosFuncionarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioPorId(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioService.buscarFuncionarioPorId(id);
        return funcionario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizarFuncionario(@PathVariable Long id, @RequestBody Funcionario funcionarioDetails) {
        Optional<Funcionario> optionalFuncionario = funcionarioService.buscarFuncionarioPorId(id);
        if (optionalFuncionario.isPresent()) {
            Funcionario funcionarioExistente = optionalFuncionario.get();

            funcionarioExistente.setNome(funcionarioDetails.getNome());
            funcionarioExistente.setIdade(funcionarioDetails.getIdade());
            funcionarioExistente.setGenero(funcionarioDetails.getGenero());
            funcionarioExistente.setSalarioBruto(funcionarioDetails.getSalarioBruto());

            // Se o Cargo for atualizado, garanta que o novo Cargo referenciado existe
            if (funcionarioDetails.getCargo() != null && funcionarioDetails.getCargo().getId() != null) {
                // Busque o Cargo para garantir que ele existe e está gerenciado pelo JPA
                Cargo novoCargo = cargoService.buscarCargoPorId(funcionarioDetails.getCargo().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cargo não encontrado com ID: " + funcionarioDetails.getCargo().getId()));
                funcionarioExistente.setCargo(novoCargo);
            } else if (funcionarioDetails.getCargo() != null) {
                // Se um Cargo foi fornecido mas sem ID, é um erro, pois esperamos um ID para um cargo existente.
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para atualizar o cargo, forneça um ID de cargo existente.");
            }


            try {
                Funcionario funcionarioAtualizado = funcionarioService.salvarFuncionario(funcionarioExistente);
                return ResponseEntity.ok(funcionarioAtualizado);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        try {
            funcionarioService.deletarFuncionario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/por-setor/{setor}")
    public List<Funcionario> listarFuncionariosPorSetor(@PathVariable String setor) {
        try {
            Setor setorEnum = Setor.valueOf(setor.toUpperCase().replace(" ", "_"));
            return funcionarioService.buscarFuncionariosPorSetor(setorEnum);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setor inválido: " + setor, e);
        }
    }

    @GetMapping("/contagem-por-setor")
    public Map<String, Long> getContagemFuncionariosPorSetor() {
        return funcionarioService.contarFuncionariosPorSetor();
    }
}