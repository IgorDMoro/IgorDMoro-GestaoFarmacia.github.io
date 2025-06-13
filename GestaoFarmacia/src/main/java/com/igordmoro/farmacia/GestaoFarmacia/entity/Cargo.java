package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*;
import java.io.Serializable; // Boa prática

@Entity
@Table(name = "cargos") // Nome da tabela no banco de dados
public class Cargo implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Deixa o banco de dados gerar o ID
	private Long id; // Adicionado o campo ID para JPA

	@Enumerated(EnumType.STRING) // Armazena o enum como String no DB
	@Column(name = "setor_cargo", nullable = false) // Nome da coluna no DB
	private Setor setor;

	@Column(name = "vale_alimento")
	private double valeAlimento;

	@Column(name = "vale_transporte")
	private double valeTransporte;

	@Column(name = "plano_saude")
	private double planoSaude;

	@Column(name = "plano_odonto")
	private double planoOdonto;

	// Construtor padrão (sem argumentos) exigido pelo JPA
	public Cargo() {
	}

	// Seu construtor existente (sem ID, pois é gerado automaticamente)
	public Cargo(Setor setor, double valeAlimento, double valeTransporte, double planoSaude, double planoOdonto) {
		this.setor = setor;
		setValeAlimento(valeAlimento);
		setValeTransporte(valeTransporte);
		setPlanoSaude(planoSaude);
		setPlanoOdonto(planoOdonto);
	}

	// --- Getters e Setters (Mantenha os que você já tem ou use estes) ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	public double getValeAlimento() {
		return valeAlimento;
	}

	public void setValeAlimento(double valeAlimento) {
		try {
			if(valeAlimento < 0) {
				throw new IllegalArgumentException("Valor inválido, o valor do vale alimento deve ser maior que 0");
			}
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		this.valeAlimento = valeAlimento;
	}

	public double getValeTransporte() {
		return valeTransporte;
	}

	public void setValeTransporte(double valeTransporte) {
		try {
			if(valeTransporte < 0) {
				throw new IllegalArgumentException("Valor inválido, o valor do vale transporte deve ser maior que 0");
			}
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		this.valeTransporte = valeTransporte;
	}

	public double getPlanoSaude() {
		return planoSaude;
	}

	public void setPlanoSaude(double planoSaude) {
		try {
			if(planoSaude < 0) {
				throw new IllegalArgumentException("Valor inválido, o valor do plano de saúde deve ser maior que 0");
			}
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		this.planoSaude = planoSaude;
	}

	public double getPlanoOdonto() {
		return planoOdonto;
	}

	public void setPlanoOdonto(double planoOdonto) {
		try {
			if(planoOdonto < 0) {
				throw new IllegalArgumentException("Valor inválido, o valor do plano de saúde deve ser maior que 0");
			}
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		this.planoOdonto = planoOdonto;
	}

	public String getNome() {
		if (this.setor != null) {
			// Retorna o nome da constante do enum (ex: "GERENCIA", "VENDAS").
			// Se você tiver um método getDisplayName() no seu enum Setor
			// para nomes mais amigáveis (ex: "Gerência"), use-o aqui:
			// return this.setor.getDisplayName();
			return this.setor.name();
		}
		return "N/A"; // Caso o setor seja nulo
	}
}