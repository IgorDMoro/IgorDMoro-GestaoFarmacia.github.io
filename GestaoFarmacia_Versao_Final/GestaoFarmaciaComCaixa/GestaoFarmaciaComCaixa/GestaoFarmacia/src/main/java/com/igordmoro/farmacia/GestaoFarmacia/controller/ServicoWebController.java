package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.*;
import com.igordmoro.farmacia.GestaoFarmacia.repository.*;
import com.igordmoro.farmacia.GestaoFarmacia.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
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

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ServicoService servicoService;

    @GetMapping("/{id}")
    public String detalhesServico(@PathVariable Long id, Model model) {
        Servico servico = servicoService.buscarServicoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        model.addAttribute("servico", servico);
        return "servico-detalhes";
    }

    @GetMapping
    public String listarServicos(Model model) {
        List<Servico> servicos = servicoService.listarTodosServicos();
        model.addAttribute("servicos", servicos);
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return "servicos";
    }

    @GetMapping("/form")
    public String showAddForm(Model model) {
        model.addAttribute("servico", new Servico());
        model.addAttribute("funcionarios", funcionarioRepository.findAll());
        model.addAttribute("transportadoras", transportadoraRepository.findAll());
        model.addAttribute("statusOptions", Status.values());
        model.addAttribute("tipoServicoOptions", TipoServico.values());
        model.addAttribute("produtos", produtoRepository.findAll());
        return "servico-form";
    }

    @PostMapping
    public String saveServico(@ModelAttribute Servico servico, RedirectAttributes redirectAttributes) {
        try {
            servicoService.salvarServico(servico);
            redirectAttributes.addFlashAttribute("message", "Serviço salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/servicos/form";
        }

        return "redirect:/servicos";
    }

    // Novo endpoint para atualizar o status do serviço
    @PostMapping("/{id}/status")
    public String updateServicoStatus(@PathVariable Long id,
                                      @RequestParam("newStatus") String newStatus,
                                      RedirectAttributes redirectAttributes) {
        try {
            Status statusEnum = Status.valueOf(newStatus.toUpperCase());
            if (statusEnum == Status.CONCLUÍDO) {
                servicoService.concluirServico(id);
                redirectAttributes.addFlashAttribute("message", "Serviço concluído com sucesso!");
            } else if (statusEnum == Status.CANCELADO) {
                servicoService.cancelarServico(id);
                redirectAttributes.addFlashAttribute("message", "Serviço cancelado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Status inválido para atualização.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/servicos";
    }

    @GetMapping("/{id}/concluir") // Or /servicos/{id}/alterar-status
    public String showConcluirServicoForm(@PathVariable Long id, Model model) {
        Servico servico = servicoService.buscarServicoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        model.addAttribute("servico", servico);
        model.addAttribute("statusOptions", Status.values()); // Pass status options to the form
        return "servico-concluir"; // This refers to src/main/resources/templates/servico-concluir.html
    }
}