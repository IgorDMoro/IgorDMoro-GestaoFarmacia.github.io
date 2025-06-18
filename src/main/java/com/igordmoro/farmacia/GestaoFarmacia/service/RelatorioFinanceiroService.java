package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status;
import com.igordmoro.farmacia.GestaoFarmacia.entity.TipoServico;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RelatorioFinanceiroService {

    private static final double CAIXA_INICIAL = 200000.00;
    private final ServicoRepository servicoRepository;

    @Autowired
    public RelatorioFinanceiroService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    // Métodos do caixa inicial
    public double getCaixaInicial() {
        return CAIXA_INICIAL;
    }

    public double calcularSaldoAtual() {
        return CAIXA_INICIAL + calcularLucroTotal();
    }

    public double calcularEstimativaSaldoAtual() {
        return CAIXA_INICIAL + calcularEstimativaLucroTotal();
    }

    public double calcularSaldoMensal(int mes, int ano) {
        return CAIXA_INICIAL + calcularLucroAnual(ano) - calcularLucroAteMesAnterior(mes, ano);
    }

    public double calcularEstimativaSaldoMensal(int mes, int ano) {
        return CAIXA_INICIAL + calcularEstimativaLucroAnual(ano) - calcularEstimativaLucroAteMesAnterior(mes, ano);
    }

    // Métodos principais de cálculo
    @Transactional(readOnly = true)
    public double calcularLucroTotal() {
        return servicoRepository.findAll().stream()
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularEstimativaLucroTotal() {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getStatus() == Status.ABERTO) // Filtrando apenas ABERTOS para estimativa
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularLucroMensal(int mes, int ano) {
        validarPeriodo(mes, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getMonthValue() == mes && s.getData().getYear() == ano)
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularEstimativaLucroMensal(int mes, int ano) {
        validarPeriodo(mes, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getMonthValue() == mes && s.getData().getYear() == ano)
                .filter(s -> s.getStatus() == Status.ABERTO) // Filtrando apenas ABERTOS para estimativa
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularLucroAnual(int ano) {
        validarPeriodo(null, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getYear() == ano)
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    @Transactional(readOnly = true)
    public double calcularEstimativaLucroAnual(int ano) {
        validarPeriodo(null, ano);
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getYear() == ano)
                .filter(s -> s.getStatus() == Status.ABERTO) // Filtrando apenas ABERTOS para estimativa
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    // Métodos auxiliares
    private void validarPeriodo(Integer mes, int ano) {
        if (mes != null && (mes < 1 || mes > 12)) {
            throw new IllegalArgumentException("Mês inválido. Deve ser entre 1 e 12.");
        }
        if (ano < 1900 || ano > 2100) {
            throw new IllegalArgumentException("Ano inválido. Deve ser entre 1900 e 2100.");
        }
    }

    // O método calcularLucroDoServicoDetalhado não é mais necessário aqui,
    // pois o impacto financeiro será lido diretamente do objeto Servico.
    // Pode ser removido, ou mantido se houver outro uso fora deste serviço.
    private double calcularLucroDoServicoDetalhado(Servico servico) {
        double total = 0;

        for (int i = 0; i < servico.getProdutos().size(); i++) {
            Produto produto = servico.getProdutos().get(i);
            int quantidade = servico.getQuantidades().get(i);

            if (servico.getTipoServico() == TipoServico.VENDA) {
                total += (produto.getPrecoVenda() - produto.getPrecoCusto()) * quantidade;
            } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                total -= produto.getPrecoCusto() * quantidade;
            }
        }
        return total;
    }


    private double calcularLucroAteMesAnterior(int mes, int ano) {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getYear() == ano)
                .filter(s -> s.getData().getMonthValue() < mes)
                .filter(s -> Objects.equals(s.getStatus(), Status.CONCLUÍDO))
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }

    private double calcularEstimativaLucroAteMesAnterior(int mes, int ano) {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getData() != null && s.getData().getYear() == ano)
                .filter(s -> s.getData().getMonthValue() < mes)
                .filter(s -> s.getStatus() == Status.ABERTO) // Filtrando apenas ABERTOS para estimativa
                .mapToDouble(Servico::getImpactoFinanceiro) // ⬅️ Modificado
                .sum();
    }
}