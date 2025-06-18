package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Transportadora;
import com.igordmoro.farmacia.GestaoFarmacia.service.TransportadoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transportadoras")
public class TransportadoraController {

    private final TransportadoraService transportadoraService;

    @Autowired
    public TransportadoraController(TransportadoraService transportadoraService) {
        this.transportadoraService = transportadoraService;
    }

    @PostMapping
    public ResponseEntity<Transportadora> criarTransportadora(@RequestBody Transportadora transportadora) {
        try {
            Transportadora novaTransportadora = transportadoraService.salvarTransportadora(transportadora);
            return new ResponseEntity<>(novaTransportadora, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<Transportadora> listarTodasTransportadoras() {
        return transportadoraService.listarTodasTransportadoras();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportadora> buscarTransportadoraPorId(@PathVariable Long id) {
        return transportadoraService.buscarTransportadoraPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}") // GET /api/transportadoras/cnpj/{cnpj}
    public ResponseEntity<Transportadora> buscarTransportadoraPorCnpj(@PathVariable Long cnpj) {
        return transportadoraService.buscarTransportadoraPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Transportadora> atualizarTransportadora(@PathVariable Long id, @RequestBody Transportadora transportadoraDetails) {
        // Assegura que o ID do corpo da requisição seja o mesmo do path
        transportadoraDetails.setId(id);
        try {
            Transportadora updatedTransportadora = transportadoraService.salvarTransportadora(transportadoraDetails);
            return ResponseEntity.ok(updatedTransportadora);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransportadora(@PathVariable Long id) {
        try {
            transportadoraService.deletarTransportadora(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}