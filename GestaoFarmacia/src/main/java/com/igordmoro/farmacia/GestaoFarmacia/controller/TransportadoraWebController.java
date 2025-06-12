package com.igordmoro.farmacia.GestaoFarmacia.controller; // Ou .controller.web se você criar um subpacote

import com.igordmoro.farmacia.GestaoFarmacia.entity.Transportadora;
import com.igordmoro.farmacia.GestaoFarmacia.repository.TransportadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Use @Controller para views
import org.springframework.ui.Model; // Importe Model para passar dados para a view
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller // Indica que esta classe é um controlador que retorna nomes de view (HTML)
@RequestMapping("/transportadoras") // Mapeia as requisições para /transportadoras
public class TransportadoraWebController {

    @Autowired
    private TransportadoraRepository transportadoraRepository;

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
    // e possivelmente redirecionando de volta para a lista.
}