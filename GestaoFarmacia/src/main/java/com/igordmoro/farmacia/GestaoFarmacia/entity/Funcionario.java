package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "funcionarios")
public class Funcionario implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID agora é gerenciado pelo JPA e é Long

	@ManyToOne // Um funcionário tem um Cargo
	@JoinColumn(name = "cargo_id", nullable = false) // Coluna de chave estrangeira
	private Cargo cargo;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "salario_bruto", nullable = false)
	private double salarioBruto;

	@Column(name = "salario_liquido", nullable = false)
	private double salarioLiquido; // Este campo deve ser calculado, não direto no form

	@Column(name = "idade", nullable = false)
	private int idade;

	@Column(name = "genero", nullable = false)
	private char genero;

	@Column(name = "imposto", nullable = false)
	private double imposto; // Este campo deve ser calculado, não direto no form

	// Construtor padrão exigido pelo JPA
	public Funcionario() {
		// Inicializações padrão ou deixadas para o JPA
	}

	// Seu construtor existente, se for usar (sem ID, pois é gerado automaticamente)
	public Funcionario(Cargo cargo, String nome, double salarioBruto, int idade, char genero) {
		this.cargo = cargo;
		this.nome = nome;
		this.salarioBruto = salarioBruto;
		this.idade = idade;
		this.genero = genero;
		// As validações devem ser movidas para um service ou antes da chamada do construtor
		// setSalarioLiquido(); // Chamar o cálculo aqui, talvez no setter de salarioBruto ou em um serviço
	}

	// Getters e Setters (Certifique-se de que todos estão aqui)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getSalarioBruto() {
		return salarioBruto;
	}

	public void setSalarioBruto(double salarioBruto) {
		this.salarioBruto = salarioBruto;
		// Lógica de cálculo de salarioLiquido e imposto aqui ou em um serviço
	}

	public double getSalarioLiquido() {
		return salarioLiquido;
	}

	public void setSalarioLiquido(double salarioLiquido) { // Este setter pode ser privado se o cálculo for interno
		this.salarioLiquido = salarioLiquido;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public char getGenero() {
		return genero;
	}

	public void setGenero(char genero) {
		this.genero = genero;
	}

	public double getImposto() {
		return imposto;
	}

	public void setImposto(double imposto) { // Este setter pode ser privado se o cálculo for interno
		this.imposto = imposto;
	}
}