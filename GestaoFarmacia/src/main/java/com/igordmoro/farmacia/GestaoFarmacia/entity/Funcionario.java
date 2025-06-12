package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*; // Importe tudo de JPA
import java.io.Serializable;

@Entity
@Table(name = "funcionarios")
public class Funcionario implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_funcionario") // Mapeia para a coluna 'id_funcionario' no DB
	private Long idFuncionario; // O nome da propriedade deve ser 'idFuncionario'

	@ManyToOne
	@JoinColumn(name = "cargo_id", nullable = false)
	private Cargo cargo;

	@Column(name = "nome", nullable = false)
	private String nome;

	@Column(name = "salario_bruto", nullable = false)
	private double salarioBruto;

	@Column(name = "salario_liquido", nullable = false) // Garanta que este campo seja preenchido no service
	private double salarioLiquido;

	@Column(name = "idade", nullable = false)
	private int idade;

	@Column(name = "genero", nullable = false)
	private Character genero; // <<<< ESSA É A MUDANÇA CRÍTICA: de 'char' para 'Character'

	@Column(name = "imposto", nullable = false) // Garanta que este campo seja preenchido no service
	private double imposto;

	// Construtor padrão exigido pelo JPA
	public Funcionario() {
	}

	// Construtor com argumentos para criação (sem ID)
	public Funcionario(Cargo cargo, String nome, double salarioBruto, int idade, Character genero) {
		this.cargo = cargo;
		this.nome = nome;
		this.salarioBruto = salarioBruto;
		this.idade = idade;
		this.genero = genero;
		// Salário líquido e imposto devem ser calculados no service
	}

	// --- Getters e Setters (Mantenha todos estes, sem erros de digitação) ---
	public Long getIdFuncionario() {
		return idFuncionario;
	}

	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
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
		// O cálculo do salário líquido e imposto DEVE ser feito no serviço,
		// pois aqui na entidade ele não terá acesso completo ao 'cargo' para os vales/planos
	}

	public double getSalarioLiquido() {
		return salarioLiquido;
	}

	public void setSalarioLiquido(double salarioLiquido) {
		this.salarioLiquido = salarioLiquido;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public Character getGenero() { // <<<< GETTER PARA CHARACTER
		return genero;
	}

	public void setGenero(Character genero) { // <<<< SETTER PARA CHARACTER
		this.genero = genero;
	}

	public double getImposto() {
		return imposto;
	}

	public void setImposto(double imposto) {
		this.imposto = imposto;
	}
}