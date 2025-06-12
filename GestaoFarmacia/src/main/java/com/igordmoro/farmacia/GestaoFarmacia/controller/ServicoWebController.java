package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status;
import com.igordmoro.farmacia.GestaoFarmacia.entity.TipoServico;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.FuncionarioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.TransportadoraRepository;
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
@RequestMapping("/servicos")
public class ServicoWebController {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private TransportadoraRepository transportadoraRepository;

    @GetMapping
    public String listarServicos(Model model) {
        List<Servico> servicos = servicoRepository.findAll();
        model.addAttribute("servicos", servicos);
        return "servicos";
    }

    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("servico", new Servico());
        model.addAttribute("funcionarios", funcionarioRepository.findAll());
        model.addAttribute("transportadoras", transportadoraRepository.findAll());
        model.addAttribute("statusOptions", Status.values()); // Passa os valores do enum Status
        model.addAttribute("tipoServicoOptions", TipoServico.values()); // Passa os valores do enum TipoServico
        return "servico-form";
    }

    @PostMapping
    public String saveServico(@ModelAttribute Servico servico, RedirectAttributes redirectAttributes) {
        servicoRepository.save(servico);
        redirectAttributes.addFlashAttribute("message", "Serviço salvo com sucesso!");
        return "redirect:/servicos";
    }
}