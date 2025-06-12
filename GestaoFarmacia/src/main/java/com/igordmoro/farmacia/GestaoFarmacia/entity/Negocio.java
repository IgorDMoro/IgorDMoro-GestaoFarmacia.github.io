package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "negocios")
public class Negocio implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // Chave primária do Negócio

	@Column(name = "quantidade_negocio", nullable = false)
	private int quantidade;

	@ManyToOne
	@JoinColumn(name = "produto_id", nullable = false) // Coluna de chave estrangeira
	private Produto produto;

	@ManyToOne
	@JoinColumn(name = "servico_id", nullable = false) // Coluna de chave estrangeira
	private Servico servico; // Adicionado para mapeamento Bidirecional com Servico (OneToMany)

	// Construtor padrão JPA
	public Negocio() {
	}

	// Seu construtor existente, se for usar (sem ID)
	public Negocio(int quantidade, Produto produto) {
		this.quantidade = quantidade;
		this.produto = produto;
		// O campo 'servico' será setado quando associado a um Serviço existente
	}

	// Getters e Setters (Certifique-se de que todos estão aqui)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}
}