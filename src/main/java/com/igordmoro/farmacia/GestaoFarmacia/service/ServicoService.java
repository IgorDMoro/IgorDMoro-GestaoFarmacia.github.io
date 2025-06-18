package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.*;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ProdutoRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ProdutoRepository produtoRepository;

    @Autowired
    public ServicoService(ServicoRepository servicoRepository, ProdutoRepository produtoRepository) {
        this.servicoRepository = servicoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Servico salvarServico(Servico servico) {
        // ✔️ Validações básicas
        if (servico.getFuncionario() == null || servico.getFuncionario().getIdFuncionario() == null) {
            throw new IllegalArgumentException("Serviço deve ter um funcionário associado.");
        }
        if (servico.getTransportadora() == null || servico.getTransportadora().getId() == null) {
            throw new IllegalArgumentException("Serviço deve ter uma transportadora associada.");
        }
        if (servico.getData() == null) {
            throw new IllegalArgumentException("Data do serviço não pode ser nula.");
        }
        if (servico.getProdutos() == null || servico.getProdutos().isEmpty() || !servico.isListaProdutosQuantidadesConsistente()) {
            throw new IllegalArgumentException("Serviço deve conter produtos e quantidades válidas.");
        }

        double valorTotal = 0.0;
        double impactoFinanceiroCalculado = 0.0;

        for (int i = 0; i < servico.getProdutos().size(); i++) {
            final int index = i;

            Produto produto = produtoRepository.findById(
                    servico.getProdutos().get(index).getIdProduto()
            ).orElseThrow(() -> new IllegalArgumentException(
                    "Produto não encontrado com ID: " + servico.getProdutos().get(index).getIdProduto()
            ));

            int quantidade = servico.getQuantidades().get(index);

            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade do produto " + produto.getNome() + " deve ser maior que zero.");
            }

            if (servico.getTipoServico() == TipoServico.VENDA) {
                // A verificação e atualização de estoque só deve ocorrer se for um novo serviço
                // ou se o serviço estiver sendo atualizado de forma que o estoque precise ser ajustado.
                // Para uma atualização de um serviço existente que já teve seu estoque ajustado,
                // evitamos re-ajustar o estoque aqui.
                // Como estamos chamando salvarServico para migrar o campo, precisamos ter cuidado com o estoque.
                // Idealmente, a migração seria um método separado que não toca no estoque.
                // Para simplificar, vou manter a lógica de estoque aqui, mas é um ponto a ser otimizado
                // se a migração for um processo regular.
                if (servico.getIdServico() == null || !servicoRepository.existsById(servico.getIdServico())) { // Se for um novo serviço
                    if (produto.getQuantidadeEstoque() < quantidade) {
                        throw new IllegalArgumentException(
                                "Estoque insuficiente para o produto: " + produto.getNome() +
                                        ". Disponível: " + produto.getQuantidadeEstoque() +
                                        ", Solicitado: " + quantidade
                        );
                    }
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
                }
                impactoFinanceiroCalculado += (produto.getPrecoVenda() - produto.getPrecoCusto()) * quantidade;
            } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                if (servico.getIdServico() == null || !servicoRepository.existsById(servico.getIdServico())) { // Se for um novo serviço
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
                }
                impactoFinanceiroCalculado -= produto.getPrecoCusto() * quantidade;
            }

            // Apenas salve o produto se houve alteração de estoque (para novos serviços)
            if (servico.getIdServico() == null || !servicoRepository.existsById(servico.getIdServico())) {
                produtoRepository.save(produto);
            }


            double precoUnitarioTransacao = (servico.getTipoServico() == TipoServico.VENDA)
                    ? produto.getPrecoVenda()
                    : produto.getPrecoCusto();

            valorTotal += precoUnitarioTransacao * quantidade;
        }

        servico.setValor(valorTotal);
        servico.setImpactoFinanceiro(impactoFinanceiroCalculado);
        return servicoRepository.save(servico);
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodosServicos() {
        return servicoRepository.findAll();
    }

    @Transactional
    public Optional<Servico> buscarServicoPorId(Long id) {
        return servicoRepository.findById(id);
    }

    @Transactional
    public void deletarServico(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Serviço não encontrado com ID: " + id);
        }
        servicoRepository.deleteById(id);
    }

    @Transactional
    public void cancelarServico(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com ID: " + id));

        if (servico.getStatus() == Status.CONCLUÍDO) {
            throw new IllegalStateException("Não é possível cancelar um serviço já concluído.");
        }

        servico.setStatus(Status.CANCELADO);
        servicoRepository.save(servico);
    }

    @Transactional
    public void concluirServico(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com ID: " + id));

        if (servico.getStatus() == Status.CANCELADO) {
            throw new IllegalStateException("Não é possível concluir um serviço cancelado.");
        }

        servico.setStatus(Status.CONCLUÍDO);
        servicoRepository.save(servico);
    }

    @Transactional
    public List<Servico> listarServicosEmAberto() {
        return servicoRepository.findByStatus(Status.ABERTO);
    }

    /**
     * Este método recalcula e persiste o impacto financeiro para todos os serviços existentes
     * que podem ter o campo 'impactoFinanceiro' nulo. Deve ser executado uma única vez
     * após a implantação da nova versão com o campo 'impactoFinanceiro' na entidade Servico.
     */
    @Transactional
    public void atualizarImpactoFinanceiroParaServicosExistentes() {
        List<Servico> todosServicos = servicoRepository.findAll();
        for (Servico servico : todosServicos) {
            // Recalcula o impacto financeiro usando a mesma lógica de salvarServico
            // mas sem alterar o estoque dos produtos novamente.
            double impactoFinanceiroCalculado = 0.0;
            if (servico.getProdutos() != null && !servico.getProdutos().isEmpty() && servico.isListaProdutosQuantidadesConsistente()) {
                for (int i = 0; i < servico.getProdutos().size(); i++) {
                    Produto produto = servico.getProdutos().get(i); // Assume que o produto já está carregado
                    int quantidade = servico.getQuantidades().get(i);

                    // Busca o produto mais recente do banco para garantir que precoVenda e precoCusto estão atualizados
                    Produto produtoAtualizado = produtoRepository.findById(produto.getIdProduto())
                            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produto.getIdProduto()));

                    if (servico.getTipoServico() == TipoServico.VENDA) {
                        impactoFinanceiroCalculado += (produtoAtualizado.getPrecoVenda() - produtoAtualizado.getPrecoCusto()) * quantidade;
                    } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                        impactoFinanceiroCalculado -= produtoAtualizado.getPrecoCusto() * quantidade;
                    }
                }
            }
            servico.setImpactoFinanceiro(impactoFinanceiroCalculado);
            servicoRepository.save(servico); // Salva o serviço com o impacto financeiro atualizado
        }
    }
}