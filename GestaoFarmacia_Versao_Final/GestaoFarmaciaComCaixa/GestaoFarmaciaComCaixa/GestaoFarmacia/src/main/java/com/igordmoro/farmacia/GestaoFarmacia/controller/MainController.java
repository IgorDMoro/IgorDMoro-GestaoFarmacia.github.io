package com.igordmoro.farmacia.GestaoFarmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Indica que esta classe é um controlador web do Spring MVC
@RequestMapping("/") // Mapeia este controlador para a URL raiz da sua aplicação
public class MainController {

    /**
     * Este método é acionado quando uma requisição GET é feita para a URL raiz ("/").
     * Ele retorna o nome do template Thymeleaf que deve ser renderizado.
     *
     * @return O nome do template HTML (main-menu.html) para exibir o menu principal.
     */
    @GetMapping // Mapeia o método para requisições GET na URL definida pelo @RequestMapping
    public String showMainMenu() {
        return "main-menu"; // O Spring Boot/Thymeleaf buscará por 'src/main/resources/templates/main-menu.html'
    }
}
