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

@Controller
@RequestMapping("/relatorios")
public class RelatorioFinanceiroWebController {

    private final RelatorioFinanceiroService relatorioFinanceiroService;

    @Autowired
    public RelatorioFinanceiroWebController(RelatorioFinanceiroService relatorioFinanceiroService) {
        this.relatorioFinanceiroService = relatorioFinanceiroService;
    }

    @GetMapping
    public String showRelatoriosPage(Model model) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // Dados do caixa e saldos
        model.addAttribute("caixaInicial", relatorioFinanceiroService.getCaixaInicial());
        model.addAttribute("saldoAtual", relatorioFinanceiroService.calcularSaldoAtual());
        model.addAttribute("estimativaSaldoAtual", relatorioFinanceiroService.calcularEstimativaSaldoAtual());
        model.addAttribute("saldoMensalAtual",
                relatorioFinanceiroService.calcularSaldoMensal(currentMonth, currentYear));
        model.addAttribute("estimativaSaldoMensalAtual",
                relatorioFinanceiroService.calcularEstimativaSaldoMensal(currentMonth, currentYear));

        // Dados de lucros
        model.addAttribute("lucroTotal", relatorioFinanceiroService.calcularLucroTotal());
        model.addAttribute("estimativaLucroTotal", relatorioFinanceiroService.calcularEstimativaLucroTotal());
        model.addAttribute("lucroMensalAtual",
                relatorioFinanceiroService.calcularLucroMensal(currentMonth, currentYear));
        model.addAttribute("estimativaLucroMensalAtual",
                relatorioFinanceiroService.calcularEstimativaLucroMensal(currentMonth, currentYear));
        model.addAttribute("lucroAnualAtual",
                relatorioFinanceiroService.calcularLucroAnual(currentYear));
        model.addAttribute("estimativaLucroAnualAtual",
                relatorioFinanceiroService.calcularEstimativaLucroAnual(currentYear));

        // Metadados
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("monthNames", getMonthNames());

        return "relatorio";
    }

    // Endpoints API
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