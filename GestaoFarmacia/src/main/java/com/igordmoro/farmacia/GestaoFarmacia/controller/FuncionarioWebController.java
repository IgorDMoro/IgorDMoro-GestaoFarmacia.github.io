package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo; // Importe a entidade Cargo
import com.igordmoro.farmacia.GestaoFarmacia.repository.FuncionarioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository; // Importe o repositório de Cargo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable; // Para @PathVariable
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;

@Controller // Esta é a classe de controle para páginas web HTML
@RequestMapping("/funcionarios") // Mapeia para http://localhost:8080/funcionarios
public class FuncionarioWebController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private CargoRepository cargoRepository; // Para carregar a lista de cargos no formulário

    // Método para listar todos os funcionários
    @GetMapping
    public String listarFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios"; // Retorna o nome do template 'funcionarios.html' (lista)
    }

    // Método para exibir o formulário de adição de funcionário
    @GetMapping("/novo")
    public String exibirFormularioAdicao(Model model) {
        model.addAttribute("funcionario", new Funcionario()); // Objeto Funcionario vazio
        model.addAttribute("cargos", cargoRepository.findAll()); // <<<< ESSENCIAL: Adicionar a lista de cargos
        return "funcionario-form"; // Retorna o nome do template do formulário
    }

    // Método para exibir o formulário de edição de funcionário
    @GetMapping("/editar/{idFuncionario}") // A URL agora usa idFuncionario
    public String exibirFormularioEdicao(@PathVariable Long idFuncionario, Model model) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(idFuncionario);
        if (funcionario.isPresent()) {
            model.addAttribute("funcionario", funcionario.get());
            model.addAttribute("cargos", cargoRepository.findAll()); // <<<< ESSENCIAL: Adicionar a lista de cargos
            return "funcionario-form";
        } else {
            return "redirect:/funcionarios";
        }
    }

    // Método para salvar um funcionário
    @PostMapping
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario, RedirectAttributes redirectAttributes) {
        // Lógica de cálculo do salário líquido e imposto deve ser movida para um Service.
        // O controller apenas recebe o objeto e passa para o service salvar.
        // Exemplo:
        // if (funcionario.getCargo() != null && funcionario.getCargo().getId() != null) {
        //     Cargo cargoExistente = cargoRepository.findById(funcionario.getCargo().getId())
        //                                         .orElseThrow(() -> new RuntimeException("Cargo não encontrado"));
        //     funcionario.setCargo(cargoExistente); // Associa o objeto Cargo completo
        //     // Lógica de cálculo que precisa do cargo:
        //     // funcionario.setSalarioLiquido(calculateSalarioLiquido(funcionario));
        //     // funcionario.setImposto(calculateImposto(funcionario));
        // }
        if (funcionario.getCargo() != null && funcionario.getCargo().getId() != null) {
            Optional<Cargo> cargoExistente = cargoRepository.findById(funcionario.getCargo().getId());
            if (cargoExistente.isPresent()) {
                // Associa a instância gerenciada (completa) do Cargo ao Funcionário
                funcionario.setCargo(cargoExistente.get());
            } else {
                // Se o Cargo com o ID informado não for encontrado (pode acontecer com ID inválido, por exemplo)
                redirectAttributes.addFlashAttribute("errorMessage", "Cargo selecionado não encontrado.");
                return "redirect:/funcionarios/novo"; // Redireciona de volta para o formulário com erro
            }
        } else {
            // Se nenhum Cargo foi selecionado (o campo é 'required', mas é bom ter uma validação extra)
            redirectAttributes.addFlashAttribute("errorMessage", "Cargo é obrigatório.");
            return "redirect:/funcionarios/novo"; // Redireciona de volta para o formulário com erro
        }
        // --- FIM DA CORREÇÃO ---

        // A lógica de cálculo do salário líquido e imposto DEVE ser movida para um Service.
        // O controller apenas recebe o objeto e passa para o service salvar/processar.
        // Por exemplo, você teria um FuncionarioService com um método salvarFuncionario(Funcionario funcionario)
        // que faria esses cálculos e validações.
        // Exemplo: funcionarioService.salvarFuncionario(funcionario);

        funcionarioRepository.save(funcionario);
        redirectAttributes.addFlashAttribute("successMessage", "Funcionário salvo com sucesso!");
        return "redirect:/funcionarios";
    }


    // Método para excluir um funcionário
    @GetMapping("/excluir/{idFuncionario}") // A URL agora usa idFuncionario
    public String excluirFuncionario(@PathVariable Long idFuncionario) {
        funcionarioRepository.deleteById(idFuncionario);
        return "redirect:/funcionarios";
    }
}