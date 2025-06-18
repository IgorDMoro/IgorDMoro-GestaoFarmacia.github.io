package com.igordmoro.farmacia.GestaoFarmacia.entity; // PACOTE EM MINÚSCULAS!

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transportadoras")
public class Transportadora {

	@Id // Marca 'id' como a chave primária
	@GeneratedValue(strategy = GenerationType.IDENTITY) // O DB gera o ID automaticamente
	private Long id; // <--- GARANTA QUE O TIPO É LONG E QUE O NOME É 'id'

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "cnpj", unique = true, nullable = false)
	private Long cnpj; // <--- CNPJ como Long para números grandes

	@Column(name = "local_atendimento", nullable = false)
	private String localAtendimento;

	// Construtor padrão exigido pelo JPA
	public Transportadora() {
	}

	// Construtor para facilitar a criação (sem ID no construtor, pois é gerado)
	public Transportadora(String nome, Long cnpj, String localAtendimento) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.localAtendimento = localAtendimento;
	}

	// Getters e Setters para o ID são ESSENCIAIS para o JPA
	public Long getId() { // <--- ESTE É O MÉTODO QUE ESTÁ FALTANDO OU INVISÍVEL
		return id;
	}

	public void setId(Long id) { // <--- E ESTE É O SETTER CORRESPONDENTE
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public String getLocalAtendimento() {
		return localAtendimento;
	}

	public void setLocalAtendimento(String localAtendimento) {
		this.localAtendimento = localAtendimento;
	}
}