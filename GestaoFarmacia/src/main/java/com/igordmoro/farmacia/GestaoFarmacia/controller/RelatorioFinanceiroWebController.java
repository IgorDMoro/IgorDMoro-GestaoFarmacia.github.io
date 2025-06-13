package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.service.RelatorioFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month; // Importe Month
import java.time.format.TextStyle; // Importe TextStyle
import java.util.Arrays;
import java.util.List;
import java.util.Locale; // Importe Locale

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

        model.addAttribute("lucroTotal", relatorioFinanceiroService.calcularLucroTotal());
        model.addAttribute("estimativaLucroTotal", relatorioFinanceiroService.calcularEstimativaLucroTotal());

        model.addAttribute("lucroMensalAtual", relatorioFinanceiroService.calcularLucroMensal(currentMonth, currentYear));
        model.addAttribute("estimativaLucroMensalAtual", relatorioFinanceiroService.calcularEstimativaLucroMensal(currentMonth, currentYear));

        model.addAttribute("lucroAnualAtual", relatorioFinanceiroService.calcularLucroAnual(currentYear));
        model.addAttribute("estimativaLucroAnualAtual", relatorioFinanceiroService.calcularEstimativaLucroAnual(currentYear));

        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);

        // Adiciona os nomes dos meses ao modelo
        model.addAttribute("monthNames", getMonthNames());

        return "relatorio";
    }

    // Método auxiliar para obter os nomes dos meses
    private List<String> getMonthNames() {
        return Arrays.stream(Month.values())
                .map(month -> month.getDisplayName(TextStyle.FULL, new Locale("pt", "BR")))
                .collect(java.util.stream.Collectors.toList());
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
}