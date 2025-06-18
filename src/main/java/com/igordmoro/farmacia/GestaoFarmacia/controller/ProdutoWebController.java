package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller // Indica que esta é uma classe de controle web
@RequestMapping("/produtos") // Mapeia todas as requisições que começam com /produtos
public class ProdutoWebController {

    @Autowired // Injeta uma instância de ProdutoRepository
    private ProdutoRepository produtoRepository;

    // Método para listar todos os produtos
    @GetMapping // Mapeia requisições GET para /produtos
    public String listarProdutos(Model model) {
        List<Produto> produtos = produtoRepository.findAll(); // Busca todos os produtos do banco
        model.addAttribute("produtos", produtos); // Adiciona a lista de produtos ao modelo
        return "produtos"; // Retorna o nome da view (thymeleaf)
    }

    // Método para exibir o formulário de adição/edição de produto
    @GetMapping("/form") // Mapeia requisições GET para /produtos/form
    public String exibirFormularioAdicao(Model model) {
        model.addAttribute("produto", new Produto()); // Cria um novo objeto Produto para o formulário
        return "produto-form"; // Retorna o nome da view do formulário
    }

    // Método para exibir o formulário de edição de produto
    @GetMapping("/editar/{idProduto}") // Mapeia requisições GET para /produtos/editar/{idProduto}
    public String exibirFormularioEdicao(@PathVariable Long idProduto, Model model) {
        Optional<Produto> produto = produtoRepository.findById(idProduto); // Busca o produto pelo ID
        if (produto.isPresent()) {
            model.addAttribute("produto", produto.get()); // Adiciona o produto encontrado ao modelo
            return "produto-form"; // Retorna o nome da view do formulário
        } else {
            // Se o produto não for encontrado, redireciona para a lista
            return "redirect:/produtos";
        }
    }

    // Método para salvar um produto (novo ou existente)
    @PostMapping // Mapeia requisições POST para /produtos
    public String salvarProduto(@ModelAttribute Produto produto) {
        // Aqui você pode adicionar lógica de validação ou cálculo antes de salvar
        produtoRepository.save(produto); // Salva o produto no banco de dados
        return "redirect:/produtos"; // Redireciona para a lista de produtos após salvar
    }

    // Método para excluir um produto
    @GetMapping("/excluir/{idProduto}") // Mapeia requisições GET para /produtos/excluir/{idProduto}
    public String excluirProduto(@PathVariable Long idProduto) {
        produtoRepository.deleteById(idProduto); // Exclui o produto pelo ID
        return "redirect:/produtos"; // Redireciona para a lista de produtos
    }
}