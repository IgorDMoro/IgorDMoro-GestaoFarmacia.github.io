package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    @Autowired
    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public Cargo salvarCargo(Cargo cargo) {
        // Validações de negócio para Cargo
        if (cargo.getSetor() == null) {
            throw new IllegalArgumentException("Setor do cargo não pode ser nulo.");
        }
        if (cargo.getValeAlimento() < 0 || cargo.getValeTransporte() < 0 ||
                cargo.getPlanoSaude() < 0 || cargo.getPlanoOdonto() < 0) {
            throw new IllegalArgumentException("Valores de benefícios não podem ser negativos.");
        }
        return cargoRepository.save(cargo);
    }

    public List<Cargo> listarTodosCargos() {
        return cargoRepository.findAll();
    }

    public Optional<Cargo> buscarCargoPorId(Long id) {
        return cargoRepository.findById(id);
    }

    public Optional<Cargo> buscarCargoPorSetor(Setor setor) {
        return cargoRepository.findBySetor(setor);
    }

    public void deletarCargo(Long id) {
        if (!cargoRepository.existsById(id)) {
            throw new IllegalArgumentException("Cargo não encontrado com ID: " + id);
        }
        cargoRepository.deleteById(id);
    }
}