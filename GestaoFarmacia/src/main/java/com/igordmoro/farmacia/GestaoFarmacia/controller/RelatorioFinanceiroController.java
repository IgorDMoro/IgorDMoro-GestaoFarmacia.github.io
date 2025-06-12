package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.service.RelatorioFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioFinanceiroController {

    private final RelatorioFinanceiroService relatorioFinanceiroService;

    @Autowired
    public RelatorioFinanceiroController(RelatorioFinanceiroService relatorioFinanceiroService) {
        this.relatorioFinanceiroService = relatorioFinanceiroService;
    }

    @GetMapping("/lucro-total")
    public ResponseEntity<Double> getLucroTotal() {
        double lucro = relatorioFinanceiroService.calcularLucroTotal();
        return ResponseEntity.ok(lucro);
    }

    @GetMapping("/estimativa-lucro-total")
    public ResponseEntity<Double> getEstimativaLucroTotal() {
        double estimativaLucro = relatorioFinanceiroService.calcularEstimativaLucroTotal();
        return ResponseEntity.ok(estimativaLucro);
    }

    @GetMapping("/lucro-mensal/{ano}/{mes}")
    public ResponseEntity<Double> getLucroMensal(@PathVariable int ano, @PathVariable int mes) {
        double lucro = relatorioFinanceiroService.calculaLucroMensal(mes, ano);
        return ResponseEntity.ok(lucro);
    }

    @GetMapping("/estimativa-lucro-mensal/{ano}/{mes}")
    public ResponseEntity<Double> getEstimativaLucroMensal(@PathVariable int ano, @PathVariable int mes) {
        double estimativaLucro = relatorioFinanceiroService.calculaEstimativaLucroMensal(mes, ano);
        return ResponseEntity.ok(estimativaLucro);
    }

    @GetMapping("/lucro-anual/{ano}")
    public ResponseEntity<Double> getLucroAnual(@PathVariable int ano) {
        double lucro = relatorioFinanceiroService.calculaLucroAnual(ano);
        return ResponseEntity.ok(lucro);
    }

    @GetMapping("/estimativa-lucro-anual/{ano}")
    public ResponseEntity<Double> getEstimativaLucroAnual(@PathVariable int ano) {
        double estimativaLucro = relatorioFinanceiroService.calculaEstimativaLucroAnual(ano);
        return ResponseEntity.ok(estimativaLucro);
    }
}