package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Negocio;
import com.igordmoro.farmacia.GestaoFarmacia.repository.NegocioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ProdutoRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/negocios")
public class NegocioWebController {

    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @GetMapping
    public String listarNegocios(Model model) {
        List<Negocio> negocios = negocioRepository.findAll();
        model.addAttribute("negocios", negocios);
        return "negocios";
    }

    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("negocio", new Negocio());
        model.addAttribute("produtos", produtoRepository.findAll()); // Passa a lista de produtos
        model.addAttribute("servicos", servicoRepository.findAll()); // Passa a lista de serviços
        return "negocio-form";
    }

    @PostMapping
    public String saveNegocio(@ModelAttribute Negocio negocio, RedirectAttributes redirectAttributes) {
        negocioRepository.save(negocio);
        redirectAttributes.addFlashAttribute("message", "Negócio salvo com sucesso!");
        return "redirect:/negocios";
    }
}