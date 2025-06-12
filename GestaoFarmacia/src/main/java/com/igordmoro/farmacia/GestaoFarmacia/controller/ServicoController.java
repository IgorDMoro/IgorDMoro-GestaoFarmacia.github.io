package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Transportadora;
import com.igordmoro.farmacia.GestaoFarmacia.service.ServicoService;
import com.igordmoro.farmacia.GestaoFarmacia.service.FuncionarioService;
import com.igordmoro.farmacia.GestaoFarmacia.service.TransportadoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;
    private final FuncionarioService funcionarioService; // Para injetar e buscar Funcionario
    private final TransportadoraService transportadoraService; // Para injetar e buscar Transportadora

    @Autowired
    public ServicoController(ServicoService servicoService, FuncionarioService funcionarioService, TransportadoraService transportadoraService) {
        this.servicoService = servicoService;
        this.funcionarioService = funcionarioService;
        this.transportadoraService = transportadoraService;
    }

    @PostMapping
    public ResponseEntity<Servico> criarServico(@RequestBody Servico servico) {
        try {
            // Garante que Funcionario e Transportadora existam antes de salvar o Servico
            if (servico.getFuncionario() != null && servico.getFuncionario().getId() != null) {
                Funcionario funcionario = funcionarioService.buscarFuncionarioPorId(servico.getFuncionario().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com ID: " + servico.getFuncionario().getId()));
                servico.setFuncionario(funcionario);
            } else {
                throw new IllegalArgumentException("Serviço deve ter um funcionário associado.");
            }

            if (servico.getTransportadora() != null && servico.getTransportadora().getId() != null) {
                Transportadora transportadora = transportadoraService.buscarTransportadoraPorId(servico.getTransportadora().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Transportadora não encontrada com ID: " + servico.getTransportadora().getId()));
                servico.setTransportadora(transportadora);
            } else {
                throw new IllegalArgumentException("Serviço deve ter uma transportadora associada.");
            }

            Servico novoServico = servicoService.salvarServico(servico);
            return new ResponseEntity<>(novoServico, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<Servico> listarTodosServicos() {
        return servicoService.listarTodosServicos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarServicoPorId(@PathVariable Long id) {
        return servicoService.buscarServicoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizarServico(@PathVariable Long id, @RequestBody Servico servicoDetails) {
        Optional<Servico> optionalServico = servicoService.buscarServicoPorId(id);
        if (optionalServico.isPresent()) {
            Servico existingServico = optionalServico.get();

            // Atualiza campos permitidos, mas garante que entidades relacionadas existam
            if (servicoDetails.getFuncionario() != null && servicoDetails.getFuncionario().getId() != null) {
                Funcionario funcionario = funcionarioService.buscarFuncionarioPorId(servicoDetails.getFuncionario().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não encontrado com ID: " + servicoDetails.getFuncionario().getId()));
                existingServico.setFuncionario(funcionario);
            }

            if (servicoDetails.getTransportadora() != null && servicoDetails.getTransportadora().getId() != null) {
                Transportadora transportadora = transportadoraService.buscarTransportadoraPorId(servicoDetails.getTransportadora().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transportadora não encontrada com ID: " + servicoDetails.getTransportadora().getId()));
                existingServico.setTransportadora(transportadora);
            }

            // Atualize outros campos do serviço
            existingServico.setStatus(servicoDetails.getStatus());
            existingServico.setTipoServico(servicoDetails.getTipoServico());
            existingServico.setData(servicoDetails.getData());
            // Negocios devem ser atualizados via endpoint específico se for o caso
            // existingServico.setNegocios(servicoDetails.getNegocios()); // Cuidado com isso, pode sobrescrever

            try {
                Servico updatedServico = servicoService.salvarServico(existingServico);
                return ResponseEntity.ok(updatedServico);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        try {
            servicoService.deletarServico(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar") // PATCH /api/servicos/{id}/cancelar
    public ResponseEntity<Servico> cancelarServico(@PathVariable Long id) {
        try {
            servicoService.cancelarServico(id);
            return servicoService.buscarServicoPorId(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); // Deve sempre encontrar se não lançou exceção
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()); // 409 Conflict
        }
    }

    @PatchMapping("/{id}/concluir") // PATCH /api/servicos/{id}/concluir
    public ResponseEntity<Servico> concluirServico(@PathVariable Long id) {
        try {
            servicoService.concluirServico(id);
            return servicoService.buscarServicoPorId(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/status/{status}") // GET /api/servicos/status/ABERTO
    public List<Servico> listarServicosPorStatus(@PathVariable String status) {
        try {
            Status statusEnum = Status.valueOf(status.toUpperCase());
            return servicoService.listarServicosEmAberto(); // Assumindo que este método serve para qualquer status
            // ou crie um método mais genérico no service: findByStatus(Status status)
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido: " + status, e);
        }
    }
}