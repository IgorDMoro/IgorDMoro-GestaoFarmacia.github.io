// src/main/java/com/igordmoro/farmacia/GestaoFarmacia/repository/TransportadoraRepository.java
package com.igordmoro.farmacia.GestaoFarmacia.repository;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Transportadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe Optional

@Repository
public interface TransportadoraRepository extends JpaRepository<Transportadora, Long> {

    /**
     * Declaração de um método personalizado para buscar uma Transportadora pelo CNPJ.
     * O Spring Data JPA automaticamente gera a implementação deste método em tempo de execução.
     *
     * @param cnpj O CNPJ da transportadora a ser buscada.
     * @return Um Optional contendo a Transportadora se encontrada, ou um Optional vazio.
     */
    Optional<Transportadora> findByCnpj(Long cnpj);
}