// src/main/java/com/igordmoro/farmacia/gestaofarmacia/service/ProdutoService.java
package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Autowired // Injeção de dependência do ProdutoRepository
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvarProduto(Produto produto) {
        // Mova a validação de `precoCusto` e `precoVenda` > 0 da entidade para aqui
        if (produto.getPrecoCusto() <= 0 || produto.getPrecoVenda() <= 0) {
            throw new IllegalArgumentException("Preço de custo e venda devem ser maiores que zero.");
        }
        // Validação de nome não vazio
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio.");
        }
        // Mova a lógica de `adicionarProduto` e `removerProduto` aqui
        return produtoRepository.save(produto); // Salva ou atualiza o produto
    }

    public List<Produto> listarTodosProdutos() {
        // Mova a lógica de `listarProdutos` aqui
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        // Mova a lógica de `pegarProdutoPorId` aqui
        return produtoRepository.findById(id);
    }

    public void deletarProduto(Long id) {
        // Mova a lógica de `removerProduto` aqui
        produtoRepository.deleteById(id);
    }

    // Adicione outros métodos de negócio, como `diminuirEstoque`, `aumentarEstoque` etc.
}