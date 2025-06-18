package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping; // Importar PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para mensagens de sucesso

import java.util.List;

@Controller
@RequestMapping("/cargos")
public class CargoWebController {

    @Autowired
    private CargoRepository cargoRepository;

    @GetMapping
    public String listarCargos(Model model) {
        List<Cargo> cargos = cargoRepository.findAll();
        model.addAttribute("cargos", cargos);
        return "cargos";
    }

    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("cargo", new Cargo());
        return "cargo-form";
    }

    @PostMapping // Lida com o envio do formulário de adição/edição
    public String saveCargo(@ModelAttribute Cargo cargo, RedirectAttributes redirectAttributes) {
        cargoRepository.save(cargo);
        redirectAttributes.addFlashAttribute("message", "Cargo salvo com sucesso!");
        return "redirect:/cargos"; // Redireciona para a lista de cargos
    }
}