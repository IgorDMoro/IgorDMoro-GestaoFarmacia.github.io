package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.*;
import com.igordmoro.farmacia.GestaoFarmacia.repository.*;
import com.igordmoro.farmacia.GestaoFarmacia.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private ServicoService servicoService; // ✅ Adicionado aqui!

    @GetMapping("/{id}") // Corrigido: era /servicos/{id} — mas já estamos em /servicos
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
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy")); // ✅ AQUI ESTÁ O FORMATTER
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
        if (servico.getNegocios() != null) {
            for (Negocio negocio : servico.getNegocios()) {
                negocio.setServico(servico);
            }
        }

        servicoRepository.save(servico);
        redirectAttributes.addFlashAttribute("message", "Serviço salvo com sucesso!");
        return "redirect:/servicos";
    }
}
