package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "produtos")
public class Produto implements Serializable {

	@Id // Marca como chave primária
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Deixa o banco de dados gerar o ID automaticamente
	@Column(name = "id_produto") // Mapeia para a coluna 'id_produto' no DB
	private Long idProduto; // O nome da propriedade deve ser 'idProduto' (com 'P' maiúsculo)

	@Column(name = "nome_produto", nullable = false)
	private String nome;

	@Column(name = "preco_custo", nullable = false)
	private double precoCusto;

	@Column(name = "preco_venda", nullable = false)
	private double precoVenda;

	@Column(name = "quantidade_estoque", nullable = false)
	private int quantidadeEstoque;

	// Construtor padrão (sem argumentos) - ESSENCIAL para o JPA
	public Produto() {
		// Inicialização padrão para campos, se necessário, ou deixe como está para o JPA
	}

	// Construtor com argumentos (sem o ID, pois ele é gerado automaticamente)
	public Produto(String nome, double precoCusto, double precoVenda, int quantidadeEstoque) {
		this.nome = nome;
		this.precoCusto = precoCusto;
		this.precoVenda = precoVenda;
		this.quantidadeEstoque = quantidadeEstoque;
		// As validações devem ser movidas para um service ou antes da chamada do construtor
	}

	// --- Getters e Setters (Mantenha todos estes, sem erros de digitação) ---
	public Long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getPrecoCusto() {
		return precoCusto;
	}

	public void setPrecoCusto(double precoCusto) {
		this.precoCusto = precoCusto;
	}

	public double getPrecoVenda() {
		return precoVenda;
	}

	public void setPrecoVenda(double precoVenda) {
		this.precoVenda = precoVenda;
	}

	public int getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(int quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}
}