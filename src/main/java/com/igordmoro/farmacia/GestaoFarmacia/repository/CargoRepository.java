package com.igordmoro.farmacia.GestaoFarmacia.repository;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor; // Importe o enum Setor se for usar em buscas
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe Optional para métodos que podem não encontrar um resultado

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    // JpaRepository já fornece: save(), findById(), findAll(), deleteById(), etc.

    // Exemplo de método de busca personalizado por setor do Cargo:
    Optional<Cargo> findBySetor(Setor setor);
}