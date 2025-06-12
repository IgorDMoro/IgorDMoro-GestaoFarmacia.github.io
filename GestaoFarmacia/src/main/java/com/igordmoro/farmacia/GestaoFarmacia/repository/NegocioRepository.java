package com.igordmoro.farmacia.GestaoFarmacia.repository;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Negocio;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto; // Importe Produto se for usar em buscas
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Long> {
    // JpaRepository já fornece: save(), findById(), findAll(), deleteById(), etc.

    // Exemplo de método de busca personalizado para todos os negócios associados a um produto específico
    List<Negocio> findByProduto(Produto produto);

    // Exemplo de método para encontrar negócios com quantidade maior que um valor
    List<Negocio> findByQuantidadeGreaterThan(int quantidade);
}