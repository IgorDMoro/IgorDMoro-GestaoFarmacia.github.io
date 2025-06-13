package com.igordmoro.farmacia.GestaoFarmacia.controller;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.service.FuncionarioService;
import com.igordmoro.farmacia.GestaoFarmacia.service.CargoService; // Importe CargoService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException; // Importe NoSuchElementException

@Controller // Esta é a classe de controle para páginas web HTML
@RequestMapping("/funcionarios") // Mapeia para http://localhost:8080/funcionarios
public class FuncionarioWebController {

    private final FuncionarioService funcionarioService;
    private final CargoService cargoService; // Para carregar a lista de cargos no formulário

    @Autowired
    public FuncionarioWebController(FuncionarioService funcionarioService, CargoService cargoService) {
        this.funcionarioService = funcionarioService;
        this.cargoService = cargoService;
    }

    /**
     * Exibe a lista de todos os funcionários.
     * Popula o modelo com os funcionários obtidos do serviço. O serviço garante
     * que salarioLiquido e imposto estejam calculados.
     *
     * @param model O modelo para adicionar atributos.
     * @return O nome da view Thymeleaf para listar funcionários.
     */
    @GetMapping // Mapeia para /funcionarios
    public String listarFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioService.listarTodosFuncionarios(); // Obtém a lista com salários calculados
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios"; // Retorna o nome do template 'funcionarios.html' (lista)
    }

    /**
     * Exibe o formulário para adicionar um novo funcionário.
     * Popula o modelo com um objeto Funcionario vazio e a lista de Cargos.
     *
     * @param model O modelo para adicionar atributos.
     * @return O nome da view Thymeleaf do formulário.
     */
    @GetMapping("/novo") // Mapeia para /funcionarios/novo (para adicionar)
    public String exibirFormularioAdicao(Model model) {
        model.addAttribute("funcionario", new Funcionario()); // Objeto Funcionario vazio
        model.addAttribute("cargos", cargoService.listarTodosCargos()); // Adiciona a lista de cargos para o dropdown
        return "funcionario-form"; // Retorna o nome do template do formulário
    }

    /**
     * Exibe o formulário para edição de um funcionário existente.
     * Popula o modelo com o objeto Funcionario existente e a lista de Cargos.
     *
     * @param idFuncionario O ID do funcionário a ser editado.
     * @param model O modelo para adicionar atributos.
     * @param redirectAttributes Para mensagens de erro em caso de funcionário não encontrado.
     * @return O nome da view Thymeleaf do formulário ou um redirecionamento.
     */
    @GetMapping("/editar/{idFuncionario}") // Mapeia para /funcionarios/editar/{idFuncionario} (para editar)
    public String exibirFormularioEdicao(@PathVariable Long idFuncionario, Model model, RedirectAttributes redirectAttributes) {
        try {
            Funcionario funcionario = funcionarioService.buscarFuncionarioPorId(idFuncionario)
                    .orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado para edição com ID: " + idFuncionario));
            model.addAttribute("funcionario", funcionario);
            model.addAttribute("cargos", cargoService.listarTodosCargos()); // Adiciona a lista de cargos para o dropdown
            return "funcionario-form";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/funcionarios"; // Redireciona de volta para a lista se o funcionário não for encontrado
        }
    }

    /**
     * Processa o envio do formulário para salvar (criar ou atualizar) um funcionário.
     * A lógica de validação, associação de Cargo e cálculo de salários é delegada ao FuncionarioService.
     *
     * @param funcionario O objeto Funcionario enviado do formulário.
     * @param redirectAttributes Atributos para adicionar mensagens de redirecionamento (sucesso/erro).
     * @return Um redirecionamento para a lista de funcionários ou para o formulário em caso de erro.
     */
    @PostMapping // Mapeia para POST /funcionarios
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario, RedirectAttributes redirectAttributes) {
        try {
            if (funcionario.getIdFuncionario() == null) {
                // Se o ID é nulo, é uma nova criação
                funcionarioService.salvarFuncionario(funcionario);
                redirectAttributes.addFlashAttribute("message", "Funcionário adicionado com sucesso!");
            } else {
                // Se o ID existe, é uma atualização
                funcionarioService.atualizarFuncionario(funcionario.getIdFuncionario(), funcionario);
                redirectAttributes.addFlashAttribute("message", "Funcionário atualizado com sucesso!");
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            // Captura erros de validação ou de entidade não encontrada do serviço
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Decide para onde redirecionar com base se era uma criação ou edição
            String redirectTo = (funcionario.getIdFuncionario() == null) ? "/funcionarios/novo" : "/funcionarios/editar/" + funcionario.getIdFuncionario();
            return "redirect:" + redirectTo; // Redireciona de volta para o formulário com a mensagem de erro
        } catch (Exception e) {
            // Captura qualquer outro erro inesperado
            redirectAttributes.addFlashAttribute("error", "Erro interno ao salvar funcionário: " + e.getMessage());
            String redirectTo = (funcionario.getIdFuncionario() == null) ? "/funcionarios/novo" : "/funcionarios/editar/" + funcionario.getIdFuncionario();
            return "redirect:" + redirectTo;
        }
        return "redirect:/funcionarios"; // Redireciona para a lista após sucesso
    }

    /**
     * Processa a requisição para deletar um funcionário.
     *
     * @param idFuncionario O ID do funcionário a ser deletado.
     * @param redirectAttributes Atributos para adicionar mensagens de redirecionamento.
     * @return Um redirecionamento para a lista de funcionários.
     */
    @GetMapping("/deletar/{idFuncionario}") // Mapeia para /funcionarios/deletar/{idFuncionario}
    public String excluirFuncionario(@PathVariable Long idFuncionario, RedirectAttributes redirectAttributes) {
        try {
            funcionarioService.deletarFuncionario(idFuncionario);
            redirectAttributes.addFlashAttribute("message", "Funcionário deletado com sucesso!");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao deletar funcionário: " + e.getMessage());
        }
        return "redirect:/funcionarios";
    }
}