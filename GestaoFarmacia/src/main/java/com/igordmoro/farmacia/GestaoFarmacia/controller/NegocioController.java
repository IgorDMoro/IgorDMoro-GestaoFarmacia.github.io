package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Negocio;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto;
import com.igordmoro.farmacia.GestaoFarmacia.service.NegocioService;
import com.igordmoro.farmacia.GestaoFarmacia.service.ServicoService; // Para buscar o serviço pai
import com.igordmoro.farmacia.GestaoFarmacia.service.ProdutoService; // Para buscar o produto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/negocios") // Rota base para Negocios (pode ser aninhado em /servicos/{servicoId}/negocios)
public class NegocioController {

    private final NegocioService negocioService;
    private final ServicoService servicoService; // Para associar Negocio a Servico
    private final ProdutoService produtoService; // Para associar Negocio a Produto

    @Autowired
    public NegocioController(NegocioService negocioService, ServicoService servicoService, ProdutoService produtoService) {
        this.negocioService = negocioService;
        this.servicoService = servicoService;
        this.produtoService = produtoService;
    }

    @PostMapping // POST /api/negocios (Cria um Negócio e o associa a um Serviço e Produto)
    public ResponseEntity<Negocio> criarNegocio(@RequestBody Negocio negocio) {
        try {
            // Garante que o Produto referenciado existe
            Produto produto = produtoService.buscarProdutoPorId(negocio.getProduto().getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + negocio.getProduto().getIdProduto()));
            negocio.setProduto(produto);

            Negocio novoNegocio = negocioService.salvarNegocio(negocio);
            return new ResponseEntity<>(novoNegocio, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Método para adicionar um Negócio a um Serviço existente
    @PostMapping("/servicos/{servicoId}") // POST /api/negocios/servicos/{servicoId}
    public ResponseEntity<Servico> adicionarNegocioAoServico(@PathVariable Long servicoId, @RequestBody Negocio negocio) {
        try {
            Servico servico = servicoService.buscarServicoPorId(servicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com ID: " + servicoId));

            // Garante que o Produto referenciado existe
            Produto produto = produtoService.buscarProdutoPorId(negocio.getProduto().getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + negocio.getProduto().getIdProduto()));
            negocio.setProduto(produto);

            // Salva o novo negócio (ou ele será salvo em cascata com o Serviço)
            Negocio savedNegocio = negocioService.salvarNegocio(negocio);

            // Adiciona o negócio à lista do serviço e salva o serviço
            servico.getNegocios().add(savedNegocio);
            Servico updatedServico = servicoService.salvarServico(servico); // Isso salvará o negócio em cascata

            return ResponseEntity.ok(updatedServico);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao adicionar negócio ao serviço.", e);
        }
    }


    @GetMapping
    public List<Negocio> listarTodosNegocios() {
        return negocioService.listarTodosNegocios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Negocio> buscarNegocioPorId(@PathVariable Long id) {
        return negocioService.buscarNegocioPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNegocio(@PathVariable Long id) {
        try {
            negocioService.deletarNegocio(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Considere adicionar endpoints para listar negócios de um serviço específico
    @GetMapping("/servicos/{servicoId}/todos") // GET /api/negocios/servicos/{servicoId}/todos
    public List<Negocio> listarNegociosPorServico(@PathVariable Long servicoId) {
        Servico servico = servicoService.buscarServicoPorId(servicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado com ID: " + servicoId));
        return servico.getNegocios(); // Retorna a lista de negócios do serviço
    }

}