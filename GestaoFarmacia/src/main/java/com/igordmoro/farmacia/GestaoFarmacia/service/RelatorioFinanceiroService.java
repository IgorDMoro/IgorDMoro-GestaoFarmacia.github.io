package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Negocio;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status;
import com.igordmoro.farmacia.GestaoFarmacia.entity.TipoServico;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RelatorioFinanceiroService {

    private final ServicoRepository servicoRepository;

    @Autowired
    public RelatorioFinanceiroService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public double calcularValorServico(Servico servico) {
        double valor = 0;
        if (servico.getNegocios() != null) {
            for (var negocio : servico.getNegocios()) {
                if (negocio.getProduto() != null) {
                    if (servico.getTipoServico() == TipoServico.VENDA) {
                        valor += negocio.getProduto().getPrecoVenda() * negocio.getQuantidade();
                    } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                        valor -= negocio.getProduto().getPrecoCusto() * negocio.getQuantidade();
                    }
                }
            }
        }
        return valor;
    }

    @Transactional(readOnly = true)
    public double calcularLucroTotal() {
        return servicoRepository.findAll().stream()
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(this::calcularLucroDoServicoDetalhado)
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularEstimativaLucroTotal() {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getStatus() != Status.CANCELADO)
                .mapToDouble(this::calcularLucroDoServicoDetalhado)
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularLucroMensal(int mes, int ano) {
        validarPeriodo(mes, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getMonthValue() == mes && s.getData().getYear() == ano)
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(this::calcularLucroDoServicoDetalhado)
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularEstimativaLucroMensal(int mes, int ano) {
        validarPeriodo(mes, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getMonthValue() == mes && s.getData().getYear() == ano)
                .filter(s -> s.getStatus() != Status.CANCELADO)
                .mapToDouble(this::calcularLucroDoServicoDetalhado)
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularLucroAnual(int ano) {
        validarPeriodo(null, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getYear() == ano)
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(this::calcularLucroDoServicoDetalhado)
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularEstimativaLucroAnual(int ano) {
        validarPeriodo(null, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getYear() == ano)
                .filter(s -> s.getStatus() != Status.CANCELADO)
                .mapToDouble(this::calcularLucroDoServicoDetalhado)
                .sum();
    }

    private void validarPeriodo(Integer mes, int ano) {
        if (mes != null && (mes < 1 || mes > 12)) {
            throw new IllegalArgumentException("Mês inválido. Deve ser entre 1 e 12.");
        }
        if (ano < 1900 || ano > 2100) {
            throw new IllegalArgumentException("Ano inválido. Deve ser entre 1900 e 2100.");
        }
    }

    private double calcularLucroDoServicoDetalhado(Servico servico) {
        double totalParaServico = 0;

        if (servico.getNegocios() != null) {
            for (Negocio negocio : servico.getNegocios()) {
                if (negocio != null && negocio.getProduto() != null) {
                    if (servico.getTipoServico() == TipoServico.VENDA) {
                        totalParaServico += (negocio.getProduto().getPrecoVenda() - negocio.getProduto().getPrecoCusto()) * negocio.getQuantidade();
                    } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                        totalParaServico -= negocio.getProduto().getPrecoCusto() * negocio.getQuantidade();
                    }
                }
            }
        }
        return totalParaServico;
    }
}