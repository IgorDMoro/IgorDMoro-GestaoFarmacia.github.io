package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Negocio;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto; // Importar Produto
import com.igordmoro.farmacia.GestaoFarmacia.repository.NegocioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ProdutoRepository; // Importar ProdutoRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NegocioService {

    private final NegocioRepository negocioRepository;
    private final ProdutoRepository produtoRepository; // Para garantir que o produto existe

    @Autowired
    public NegocioService(NegocioRepository negocioRepository, ProdutoRepository produtoRepository) {
        this.negocioRepository = negocioRepository;
        this.produtoRepository = produtoRepository;
    }

    public Negocio salvarNegocio(Negocio negocio) {
        // Validações de negócio para Negocio
        if (negocio.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade do negócio deve ser maior que zero.");
        }
        if (negocio.getProduto() == null || negocio.getProduto().getIdProduto() == null) {
            throw new IllegalArgumentException("Negócio deve ter um produto associado.");
        }

        // Verifica se o produto associado realmente existe no banco de dados
        Produto produtoExistente = produtoRepository.findById(negocio.getProduto().getIdProduto())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + negocio.getProduto().getIdProduto()));

        negocio.setProduto(produtoExistente); // Garante que o produto anexado seja o gerenciado pela JPA

        return negocioRepository.save(negocio);
    }

    public List<Negocio> listarTodosNegocios() {
        return negocioRepository.findAll();
    }

    public Optional<Negocio> buscarNegocioPorId(Long id) {
        return negocioRepository.findById(id);
    }

    public void deletarNegocio(Long id) {
        if (!negocioRepository.existsById(id)) {
            throw new IllegalArgumentException("Negócio não encontrado com ID: " + id);
        }
        negocioRepository.deleteById(id);
    }

    // Você pode adicionar métodos específicos de busca se necessário, como:
    public List<Negocio> buscarNegociosPorProduto(Produto produto) {
        return negocioRepository.findByProduto(produto);
    }
}