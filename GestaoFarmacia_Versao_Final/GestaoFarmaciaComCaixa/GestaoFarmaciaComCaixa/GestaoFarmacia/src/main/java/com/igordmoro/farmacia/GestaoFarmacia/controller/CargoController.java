package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor;
import com.igordmoro.farmacia.GestaoFarmacia.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoService cargoService;

    @Autowired
    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @PostMapping
    public ResponseEntity<Cargo> criarCargo(@RequestBody Cargo cargo) {
        try {
            Cargo novoCargo = cargoService.salvarCargo(cargo);
            return new ResponseEntity<>(novoCargo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<Cargo> listarTodosCargos() {
        return cargoService.listarTodosCargos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> buscarCargoPorId(@PathVariable Long id) {
        return cargoService.buscarCargoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/por-setor/{setor}") // Exemplo: /api/cargos/por-setor/GERENTE_FILIAL
    public ResponseEntity<Cargo> buscarCargoPorSetor(@PathVariable String setor) {
        try {
            Setor setorEnum = Setor.valueOf(setor.toUpperCase().replace(" ", "_"));
            return cargoService.buscarCargoPorSetor(setorEnum)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setor inválido: " + setor);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cargo> atualizarCargo(@PathVariable Long id, @RequestBody Cargo cargoDetails) {
        // Assegura que o ID do corpo da requisição seja o mesmo do path
        cargoDetails.setId(id); // Set the ID from path variable
        try {
            Cargo updatedCargo = cargoService.salvarCargo(cargoDetails); // save() atualiza se o ID existe
            return ResponseEntity.ok(updatedCargo);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        // Para uma atualização mais granular, você pode buscar o cargo existente,
        // atualizar seus campos e depois salvar. O `salvarCargo` já lida com isso.
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCargo(@PathVariable Long id) {
        try {
            cargoService.deletarCargo(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); // 404 Not Found
        }
    }
}