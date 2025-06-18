package com.igordmoro.farmacia.GestaoFarmacia;

import com.igordmoro.farmacia.GestaoFarmacia.service.ServicoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class GestaoFarmaciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoFarmaciaApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ServicoService servicoService) {
		return args -> {
			System.out.println("Executando migração de dados para impacto financeiro...");
			servicoService.atualizarImpactoFinanceiroParaServicosExistentes();
			System.out.println("Migração de dados concluída.");
		};
	}

}
