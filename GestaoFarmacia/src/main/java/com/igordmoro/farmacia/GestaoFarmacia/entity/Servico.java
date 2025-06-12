package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList; // Usar ArrayList para inicialização
import java.util.List;

@Entity
@Table(name = "servicos")
public class Servico implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_servico") // Se você quer manter o nome da coluna no DB como id_servico
	private Long idServico; // Mudei para Long e idServico para corresponder ao seu original

	@ManyToOne
	@JoinColumn(name = "funcionario_id", nullable = false)
	private Funcionario funcionario;

	@ManyToOne
	@JoinColumn(name = "transportadora_id", nullable = false)
	private Transportadora transportadora;

	@OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Negocio> negocios = new ArrayList<>(); // Inicializar a lista aqui ou no construtor

	@Enumerated(EnumType.STRING)
	@Column(name = "status_servico", nullable = false)
	private Status status;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_servico", nullable = false)
	private TipoServico tipoServico;

	@Column(name = "valor_total", nullable = false)
	private double valor;

	@Column(name = "data_servico", nullable = false)
	private LocalDate data;

	// Construtor padrão JPA
	public Servico() {
		// this.negocios = new ArrayList<>(); // Já inicializado acima, ou pode ser aqui
		this.status = Status.ABERTO; // Define um status padrão ao criar
	}

	// Seu construtor existente, se for usar (sem ID)
	public Servico(Funcionario funcionario, Transportadora transportadora, TipoServico tipoServico, LocalDate data) {
		this.funcionario = funcionario;
		this.transportadora = transportadora;
		this.tipoServico = tipoServico;
		this.data = data;
		this.negocios = new ArrayList<>();
		this.status = Status.ABERTO; // Define status padrão
		this.valor = 0.0; // Valor inicial
	}

	// Getters e Setters (Certifique-se de que todos estão aqui)
	public Long getIdServico() {
		return idServico;
	}

	public void setIdServico(Long idServico) {
		this.idServico = idServico;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Transportadora getTransportadora() {
		return transportadora;
	}

	public void setTransportadora(Transportadora transportadora) {
		this.transportadora = transportadora;
	}

	public List<Negocio> getNegocios() {
		return negocios;
	}

	public void setNegocios(List<Negocio> negocios) {
		this.negocios = negocios;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public TipoServico getTipoServico() {
		return tipoServico;
	}

	public void setTipoServico(TipoServico tipoServico) {
		this.tipoServico = tipoServico;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) { // Este setter pode ser privado se o cálculo for interno
		this.valor = valor;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}
}