package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.service.RelatorioFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
        return ResponseEntity.ok(relatorioFinanceiroService.calcularLucroTotal());
    }

    @GetMapping("/estimativa-lucro-total")
    public ResponseEntity<Double> getEstimativaLucroTotal() {
        return ResponseEntity.ok(relatorioFinanceiroService.calcularEstimativaLucroTotal());
    }

    @GetMapping("/mensal/lucro/{ano}/{mes}")
    public ResponseEntity<Double> getLucroMensalByPath(@PathVariable int ano, @PathVariable int mes) {
        try {
            double lucro = relatorioFinanceiroService.calcularLucroMensal(mes, ano);
            return ResponseEntity.ok(lucro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/mensal/estimativa/{ano}/{mes}")
    public ResponseEntity<Double> getEstimativaLucroMensalByPath(@PathVariable int ano, @PathVariable int mes) {
        try {
            double estimativa = relatorioFinanceiroService.calcularEstimativaLucroMensal(mes, ano);
            return ResponseEntity.ok(estimativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/anual/lucro/{ano}")
    public ResponseEntity<Double> getLucroAnualByPath(@PathVariable int ano) {
        try {
            double lucro = relatorioFinanceiroService.calcularLucroAnual(ano);
            return ResponseEntity.ok(lucro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/anual/estimativa/{ano}")
    public ResponseEntity<Double> getEstimativaLucroAnualByPath(@PathVariable int ano) {
        try {
            double estimativa = relatorioFinanceiroService.calcularEstimativaLucroAnual(ano);
            return ResponseEntity.ok(estimativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/lucroMensal")
    public ResponseEntity<Double> getLucroMensalByRequestParam(@RequestParam int mes, @RequestParam int ano) {
        try {
            double lucro = relatorioFinanceiroService.calcularLucroMensal(mes, ano);
            return ResponseEntity.ok(lucro);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/estimativaLucroMensal")
    public ResponseEntity<Double> getEstimativaLucroMensalByRequestParam(@RequestParam int mes, @RequestParam int ano) {
        try {
            double estimativa = relatorioFinanceiroService.calcularEstimativaLucroMensal(mes, ano);
            return ResponseEntity.ok(estimativa);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/lucroAnual")
    public ResponseEntity<Double> getLucroAnualByRequestParam(@RequestParam int ano) {
        try {
            double lucro = relatorioFinanceiroService.calcularLucroAnual(ano);
            return ResponseEntity.ok(lucro);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/estimativaLucroAnual")
    public ResponseEntity<Double> getEstimativaLucroAnualByRequestParam(@RequestParam int ano) {
        try {
            double estimativa = relatorioFinanceiroService.calcularEstimativaLucroAnual(ano);
            return ResponseEntity.ok(estimativa);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}