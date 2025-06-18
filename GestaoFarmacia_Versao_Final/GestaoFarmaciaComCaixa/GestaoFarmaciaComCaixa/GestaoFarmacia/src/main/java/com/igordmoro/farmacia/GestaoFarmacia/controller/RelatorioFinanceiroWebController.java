package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.service.RelatorioFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional; // Importação adicionada para Optional

@Controller
@RequestMapping("/relatorios")
public class RelatorioFinanceiroWebController {

    private final RelatorioFinanceiroService relatorioFinanceiroService;

    @Autowired
    public RelatorioFinanceiroWebController(RelatorioFinanceiroService relatorioFinanceiroService) {
        this.relatorioFinanceiroService = relatorioFinanceiroService;
    }

    @GetMapping
    public String showRelatoriosPage(
            @RequestParam(value = "mes", required = false) Integer mes, // Aceita mes como parâmetro opcional
            @RequestParam(value = "ano", required = false) Integer ano, // Aceita ano como parâmetro opcional
            Model model) {

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // Se mes ou ano não forem fornecidos na requisição, usa o mês e ano atuais como padrão.
        int selectedMonth = Optional.ofNullable(mes).orElse(currentMonth);
        int selectedYear = Optional.ofNullable(ano).orElse(currentYear);

        // Dados do caixa e saldos
        model.addAttribute("caixaInicial", relatorioFinanceiroService.getCaixaInicial());
        model.addAttribute("saldoAtual", relatorioFinanceiroService.calcularSaldoAtual());
        model.addAttribute("estimativaSaldoAtual", relatorioFinanceiroService.calcularEstimativaSaldoAtual());
        model.addAttribute("saldoMensalAtual",
                relatorioFinanceiroService.calcularSaldoMensal(selectedMonth, selectedYear));
        model.addAttribute("estimativaSaldoMensalAtual",
                relatorioFinanceiroService.calcularEstimativaSaldoMensal(selectedMonth, selectedYear));

        // Dados de lucros
        model.addAttribute("lucroTotal", relatorioFinanceiroService.calcularLucroTotal());
        model.addAttribute("estimativaLucroTotal", relatorioFinanceiroService.calcularEstimativaLucroTotal());
        model.addAttribute("lucroMensalAtual",
                relatorioFinanceiroService.calcularLucroMensal(selectedMonth, selectedYear));
        model.addAttribute("estimativaLucroMensalAtual",
                relatorioFinanceiroService.calcularEstimativaLucroMensal(selectedMonth, selectedYear));
        model.addAttribute("lucroAnualAtual",
                relatorioFinanceiroService.calcularLucroAnual(selectedYear));
        model.addAttribute("estimativaLucroAnualAtual",
                relatorioFinanceiroService.calcularEstimativaLucroAnual(selectedYear));

        // Metadados para o formulário de seleção
        model.addAttribute("currentMonth", selectedMonth);
        model.addAttribute("currentYear", selectedYear);
        model.addAttribute("monthNames", getMonthNames());


        return "relatorio";
    }

    // Endpoints API (mantidos como estavam)
    @GetMapping("/api/caixaInicial")
    @ResponseBody
    public double getCaixaInicialApi() {
        return relatorioFinanceiroService.getCaixaInicial();
    }

    @GetMapping("/api/saldoAtual")
    @ResponseBody
    public double getSaldoAtualApi() {
        return relatorioFinanceiroService.calcularSaldoAtual();
    }

    @GetMapping("/api/estimativaSaldoAtual")
    @ResponseBody
    public double getEstimativaSaldoAtualApi() {
        return relatorioFinanceiroService.calcularEstimativaSaldoAtual();
    }

    @GetMapping("/api/lucroTotal")
    @ResponseBody
    public double getLucroTotalApi() {
        return relatorioFinanceiroService.calcularLucroTotal();
    }

    @GetMapping("/api/estimativaLucroTotal")
    @ResponseBody
    public double getEstimativaLucroTotalApi() {
        return relatorioFinanceiroService.calcularEstimativaLucroTotal();
    }

    @GetMapping("/api/lucroMensal")
    @ResponseBody
    public double getLucroMensalApi(@RequestParam int mes, @RequestParam int ano) {
        try {
            return relatorioFinanceiroService.calcularLucroMensal(mes, ano);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/estimativaLucroMensal")
    @ResponseBody
    public double getEstimativaLucroMensalApi(@RequestParam int mes, @RequestParam int ano) {
        try {
            return relatorioFinanceiroService.calcularEstimativaLucroMensal(mes, ano);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/lucroAnual")
    @ResponseBody
    public double getLucroAnualApi(@RequestParam int ano) {
        try {
            return relatorioFinanceiroService.calcularLucroAnual(ano);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/estimativaLucroAnual")
    @ResponseBody
    public double getEstimativaLucroAnualApi(@RequestParam int ano) {
        try {
            return relatorioFinanceiroService.calcularEstimativaLucroAnual(ano);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private List<String> getMonthNames() {
        return Arrays.stream(Month.values())
                .map(month -> month.getDisplayName(TextStyle.FULL, new Locale("pt", "BR")))
                .toList();
    }
}