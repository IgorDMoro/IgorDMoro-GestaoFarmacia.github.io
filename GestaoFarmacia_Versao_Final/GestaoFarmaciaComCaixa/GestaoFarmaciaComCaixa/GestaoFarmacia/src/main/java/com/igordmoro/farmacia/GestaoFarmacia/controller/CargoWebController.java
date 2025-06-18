// src/main/java/com/igordmoro/farmacia/GestaoFarmacia/controller/CargoWebController.java

package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository;
import com.igordmoro.farmacia.GestaoFarmacia.service.FuncionarioService; // Importar FuncionarioService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map; // Importar Map

@Controller
@RequestMapping("/cargos")
public class CargoWebController {

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private FuncionarioService funcionarioService; // Injetar FuncionarioService

    @GetMapping
    public String listarCargos(Model model) {
        List<Cargo> cargos = cargoRepository.findAll();
        model.addAttribute("cargos", cargos);

        // Obter a contagem de funcionários por ID de cargo
        Map<Long, Long> contagemFuncionariosPorCargo = funcionarioService.contarFuncionariosPorIdCargo();
        model.addAttribute("contagemFuncionariosPorCargo", contagemFuncionariosPorCargo); // Adicionar ao modelo

        return "cargos";
    }

    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("cargo", new Cargo());
        return "cargo-form";
    }

    @PostMapping
    public String saveCargo(@ModelAttribute Cargo cargo, RedirectAttributes redirectAttributes) {
        cargoRepository.save(cargo);
        redirectAttributes.addFlashAttribute("message", "Cargo salvo com sucesso!");
        return "redirect:/cargos";
    }
}