package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status;
import com.igordmoro.farmacia.GestaoFarmacia.entity.TipoServico; // Importe TipoServico
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe esta anotação

import java.time.LocalDate; // Importe LocalDate
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Importe Collectors (se usado)

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    @Autowired
    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public Servico salvarServico(Servico servico) {
        // Validações de negócio para Servico
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
            // Define um status padrão se não for fornecido na criação
            servico.setStatus(Status.ABERTO);
        }

        // Calcula o valor total do serviço baseado nos negócios associados (se houver)
        // O valor total deve ser calculado aqui, não na entidade se o campo for @Transient
        servico.setValor(calcularValorTotalServico(servico));

        return servicoRepository.save(servico);
    }

    @Transactional
    public List<Servico> listarTodosServicos() {
        // Para garantir que o valor total seja calculado ao listar
        List<Servico> servicos = servicoRepository.findAll();
        servicos.forEach(s -> s.setValor(calcularValorTotalServico(s)));
        return servicos;
    }

    @Transactional
    public Optional<Servico> buscarServicoPorId(Long id) {
        Optional<Servico> servico = servicoRepository.findById(id);
        servico.ifPresent(s -> s.setValor(calcularValorTotalServico(s))); // Calcula o valor se o serviço for encontrado
        return servico;
    }

    // --- NOVO MÉTODO: DELETAR SERVIÇO ---
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

    // Método auxiliar para calcular o valor total de um serviço
    // Colocado aqui, pois é lógica de negócio que pode ser usada por múltiplos métodos
    private double calcularValorTotalServico(Servico servico) {
        double total = 0;
        if (servico.getNegocios() != null) {
            for (var negocio : servico.getNegocios()) {
                if (negocio.getProduto() != null) {
                    if (servico.getTipoServico() == TipoServico.VENDA) {
                        total += negocio.getProduto().getPrecoVenda() * negocio.getQuantidade();
                    } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                        total += negocio.getProduto().getPrecoCusto() * negocio.getQuantidade();
                    }
                }
            }
        }
        return total;
    }

    // Métodos para cálculos financeiros (que antes estavam em Empresa.java)
    // Devem ir para RelatorioFinanceiroService ou um novo Servico Financeiro,
    // mas podem estar aqui temporariamente para testes.
    // ...
}