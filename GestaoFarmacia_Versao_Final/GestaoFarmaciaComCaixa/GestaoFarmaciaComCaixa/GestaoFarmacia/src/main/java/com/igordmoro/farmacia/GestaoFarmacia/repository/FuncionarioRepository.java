// src/main/java/com/igordmoro/farmacia/gestaofarmacia/repository/FuncionarioRepository.java
package com.igordmoro.farmacia.GestaoFarmacia.repository;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor; // Importe o enum Setor
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    // Exemplo de método personalizado para buscar funcionários por setor
    List<Funcionario> findByCargo_Setor(Setor setor);

    // Exemplo de método personalizado para contar funcionários por setor
    long countByCargo_Setor(Setor setor);
}