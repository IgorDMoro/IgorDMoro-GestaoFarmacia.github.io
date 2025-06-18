package com.igordmoro.farmacia.GestaoFarmacia.controller; // Ou .controller.web se você criar um subpacote

import com.igordmoro.farmacia.GestaoFarmacia.entity.Produto;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Transportadora;
import com.igordmoro.farmacia.GestaoFarmacia.repository.ServicoRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.TransportadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Use @Controller para views
import org.springframework.ui.Model; // Importe Model para passar dados para a view
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import java.util.List;

@Controller // Indica que esta classe é um controlador que retorna nomes de view (HTML)
@RequestMapping("/transportadoras") // Mapeia as requisições para /transportadoras
public class TransportadoraWebController {

    @Autowired
    private TransportadoraRepository transportadoraRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    // Este método vai exibir a lista de transportadoras em uma página HTML
    @GetMapping // Mapeia para GET http://localhost:8080/transportadoras
    public String listarTransportadoras(Model model) {
        List<Transportadora> transportadoras = transportadoraRepository.findAll();
        model.addAttribute("transportadoras", transportadoras); // Adiciona a lista ao modelo para a view
        return "transportadoras"; // Retorna o nome do template HTML (transportadoras.html)
    }

    // Você pode adicionar um método para exibir um formulário de adição
    @GetMapping("/form") // Mapeia para GET http://localhost:8080/transportadoras/form
    public String showAddForm(Model model) {
        model.addAttribute("transportadora", new Transportadora()); // Objeto vazio para preencher o formulário
        return "transportadora-form"; // Nome do template do formulário (que você criaria em um próximo passo)
    }

    // Um método para salvar (POST) do formulário viria aqui, usando @PostMapping

    @PostMapping // Mapeia requisições POST para /produtos
    public String salvarTransportadora(@ModelAttribute Transportadora transportadora) {
        // Aqui você pode adicionar lógica de validação ou cálculo antes de salvar
        transportadoraRepository.save(transportadora); // Salva o produto no banco de dados
        return "redirect:/transportadoras"; // Redireciona para a lista de produtos após salvar
    }

    @GetMapping("/excluir/{id}")
    public String excluirTransportadora(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Transportadora> transportadora = transportadoraRepository.findById(id);

        if (transportadora.isPresent()) {
            if (!servicoRepository.existsByTransportadora(transportadora.get())) {
                transportadoraRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("sucesso", "Transportadora excluída com sucesso.");
                return "redirect:/transportadoras"; // exclusão bem sucedida, redireciona para lista
            } else {
                model.addAttribute("erro", "Não é possível excluir: a transportadora está vinculada a um ou mais serviços.");

                // Adicione a lista de transportadoras para a view funcionar sem erro:
                List<Transportadora> transportadoras = transportadoraRepository.findAll();
                model.addAttribute("transportadoras", transportadoras);
                redirectAttributes.addFlashAttribute("erro", "Não é possível excluir: a transportadora está vinculada a um ou mais serviços.");
                return "transportadoras"; // retorna a view com dados carregados
            }
        }

        return "redirect:/transportadoras"; // se não encontrar, redireciona
    }

    @GetMapping("/editar/{id}")
    public String editarTransportadora(@PathVariable Long id, Model model) {
        Transportadora transportadora = transportadoraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("transportadora", transportadora);
        return "transportadora-form"; // Usa o mesmo formulário para editar
    }

    // e possivelmente redirecionando de volta para a lista.
}