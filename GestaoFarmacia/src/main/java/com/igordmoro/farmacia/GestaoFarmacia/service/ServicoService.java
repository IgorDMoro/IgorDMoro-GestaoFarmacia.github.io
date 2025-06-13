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
        // ✅ Validações de dados obrigatórios
        if (servico.getFuncionario() == null || servico.getFuncionario().getIdFuncionario() == null) {
            throw new IllegalArgumentException("Serviço deve ter um funcionário associado com um ID válido.");
        }
        if (servico.getTransportadora() == null || servico.getTransportadora().getId() == null) {
            throw new IllegalArgumentException("Serviço deve ter uma transportadora associada com um ID válido.");
        }
        if (servico.getData() == null) {
            throw new IllegalArgumentException("A data do serviço não pode ser nula.");
        }
        if (servico.getTipoServico() == null) {
            throw new IllegalArgumentException("O tipo de serviço (COMPRA/VENDA) não pode ser nulo.");
        }
        if (servico.getStatus() == null) {
            servico.setStatus(Status.ABERTO); // default
        }

        // ✅ Processar Negócios (produtos) e calcular valor total
        double valorTotal = 0.0;
        for (Negocio negocio : servico.getNegocios()) {
            Produto produto = produtoRepository.findById(negocio.getProduto().getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + negocio.getProduto().getIdProduto()));

            negocio.setProduto(produto);
            negocio.setServico(servico); // ESSENCIAL para o relacionamento

            double subtotal = servico.getTipoServico() == TipoServico.VENDA
                    ? produto.getPrecoVenda() * negocio.getQuantidade()
                    : produto.getPrecoCusto() * negocio.getQuantidade();

            valorTotal += subtotal;
        }

        servico.setValor(valorTotal);

        return servicoRepository.save(servico);
    }

    @Transactional
    public List<Servico> listarTodosServicos() {
        List<Servico> servicos = servicoRepository.findAll();
        servicos.forEach(s -> s.setValor(calcularValorTotalServico(s)));
        return servicos;
    }

    @Transactional
    public Optional<Servico> buscarServicoPorId(Long id) {
        Optional<Servico> servico = servicoRepository.findById(id);
        servico.ifPresent(s -> s.setValor(calcularValorTotalServico(s)));
        return servico;
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
        List<Servico> servicos = servicoRepository.findByStatus(Status.ABERTO);
        servicos.forEach(s -> s.setValor(calcularValorTotalServico(s)));
        return servicos;
    }

    // Método auxiliar interno para recalcular o valor do serviço
    private double calcularValorTotalServico(Servico servico) {
        double total = 0;
        if (servico.getNegocios() != null) {
            for (Negocio negocio : servico.getNegocios()) {
                Produto produto = negocio.getProduto();
                if (produto != null) {
                    if (servico.getTipoServico() == TipoServico.VENDA) {
                        total += produto.getPrecoVenda() * negocio.getQuantidade();
                    } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                        total += produto.getPrecoCusto() * negocio.getQuantidade();
                    }
                }
            }
        }
        return total;
    }
}
