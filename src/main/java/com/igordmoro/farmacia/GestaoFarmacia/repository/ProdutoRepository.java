// src/main/java/com/igordmoro/farmacia/gestaofarmacia/repository/ProdutoRepository.java
package com.igordmoro.farmacia.GestaoFarmacia.repository;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Opcional, mas boa prática para clareza
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Spring Data JPA fornece: save(), findById(), findAll(), deleteById(), etc.
    // Exemplo de método de busca personalizado:
    // Optional<Produto> findByNome(String nome);
}