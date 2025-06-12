package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status;
import com.igordmoro.farmacia.GestaoFarmacia.entity.TipoServico;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioFinanceiroService {

    private final ServicoRepository servicoRepository;

    @Autowired
    public RelatorioFinanceiroService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    // Método para calcular o valor total de um serviço (se não for feito na entidade)
    public double calcularValorServico(Servico servico) {
        double valor = 0;
        if (servico.getNegocios() != null) {
            for (var negocio : servico.getNegocios()) {
                if (negocio.getProduto() != null) {
                    if (servico.getTipoServico() == TipoServico.VENDA) {
                        valor += negocio.getProduto().getPrecoVenda() * negocio.getQuantidade();
                    } else if (servico.getTipoServico() == TipoServico.COMPRA) {
                        valor += negocio.getProduto().getPrecoCusto() * negocio.getQuantidade();
                    }
                }
            }
        }
        return valor;
    }

    public double calcularLucroTotal() {
        List<Servico> servicosConcluidos = servicoRepository.findByStatus(Status.CONCLUÍDO);
        double somaVendas = 0;
        double somaCompras = 0;

        for (Servico s : servicosConcluidos) {
            double valorServico = calcularValorServico(s); // Calcula o valor dinamicamente
            if (s.getTipoServico() == TipoServico.VENDA) {
                somaVendas += valorServico;
            } else if (s.getTipoServico() == TipoServico.COMPRA) {
                somaCompras += valorServico;
            }
        }
        return somaVendas - somaCompras;
    }

    public double calcularEstimativaLucroTotal() {
        List<Servico> servicosNaoCancelados = servicoRepository.findAll().stream()
                .filter(s -> s.getStatus() != Status.CANCELADO)
                .collect(Collectors.toList());
        double somaVendas = 0;
        double somaCompras = 0;

        for (Servico s : servicosNaoCancelados) {
            double valorServico = calcularValorServico(s);
            if (s.getTipoServico() == TipoServico.VENDA) {
                somaVendas += valorServico;
            } else if (s.getTipoServico() == TipoServico.COMPRA) {
                somaCompras += valorServico;
            }
        }
        return somaVendas - somaCompras;
    }

    public double calculaLucroMensal(int mes, int ano) {
        List<Servico> servicosDoMes = servicoRepository.findAll().stream()
                .filter(s -> s.getData().getMonthValue() == mes && s.getData().getYear() == ano && s.getStatus() == Status.CONCLUÍDO)
                .collect(Collectors.toList());
        double somaVendas = 0;
        double somaCompras = 0;

        for (Servico s : servicosDoMes) {
            double valorServico = calcularValorServico(s);
            if (s.getTipoServico() == TipoServico.VENDA) {
                somaVendas += valorServico;
            } else if (s.getTipoServico() == TipoServico.COMPRA) {
                somaCompras += valorServico;
            }
        }
        return somaVendas - somaCompras;
    }

    public double calculaEstimativaLucroMensal(int mes, int ano) {
        List<Servico> servicosDoMes = servicoRepository.findAll().stream()
                .filter(s -> s.getData().getMonthValue() == mes && s.getData().getYear() == ano && s.getStatus() != Status.CANCELADO)
                .collect(Collectors.toList());
        double somaVendas = 0;
        double somaCompras = 0;

        for (Servico s : servicosDoMes) {
            double valorServico = calcularValorServico(s);
            if (s.getTipoServico() == TipoServico.VENDA) {
                somaVendas += valorServico;
            } else if (s.getTipoServico() == TipoServico.COMPRA) {
                somaCompras += valorServico;
            }
        }
        return somaVendas - somaCompras;
    }

    public double calculaLucroAnual(int ano) {
        List<Servico> servicosDoAno = servicoRepository.findAll().stream()
                .filter(s -> s.getData().getYear() == ano && s.getStatus() == Status.CONCLUÍDO)
                .collect(Collectors.toList());
        double somaVendas = 0;
        double somaCompras = 0;

        for (Servico s : servicosDoAno) {
            double valorServico = calcularValorServico(s);
            if (s.getTipoServico() == TipoServico.VENDA) {
                somaVendas += valorServico;
            } else if (s.getTipoServico() == TipoServico.COMPRA) {
                somaCompras += valorServico;
            }
        }
        return somaVendas - somaCompras;
    }

    public double calculaEstimativaLucroAnual(int ano) {
        List<Servico> servicosDoAno = servicoRepository.findAll().stream()
                .filter(s -> s.getData().getYear() == ano && s.getStatus() != Status.CANCELADO)
                .collect(Collectors.toList());
        double somaVendas = 0;
        double somaCompras = 0;

        for (Servico s : servicosDoAno) {
            double valorServico = calcularValorServico(s);
            if (s.getTipoServico() == TipoServico.VENDA) {
                somaVendas += valorServico;
            } else if (s.getTipoServico() == TipoServico.COMPRA) {
                somaCompras += valorServico;
            }
        }
        return somaVendas - somaCompras;
    }
}