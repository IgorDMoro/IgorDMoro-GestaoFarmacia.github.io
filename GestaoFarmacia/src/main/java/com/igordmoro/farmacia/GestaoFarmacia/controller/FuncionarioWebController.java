package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.repository.FuncionarioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Importar ModelAttribute
import org.springframework.web.bind.annotation.PostMapping; // Importar PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioWebController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @GetMapping
    public String listarFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios";
    }

    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        model.addAttribute("cargos", cargoRepository.findAll()); // Passa a lista de cargos para o formulário
        return "funcionario-form";
    }

    @PostMapping
    public String saveFuncionario(@ModelAttribute Funcionario funcionario, RedirectAttributes redirectAttributes) {
        // Recalcular salarioLiquido e imposto se necessário, antes de salvar
        // funcionario.setSalarioLiquido();
        // funcionario.setImposto(...);
        funcionarioRepository.save(funcionario);
        redirectAttributes.addFlashAttribute("message", "Funcionário salvo com sucesso!");
        return "redirect:/funcionarios";
    }
}