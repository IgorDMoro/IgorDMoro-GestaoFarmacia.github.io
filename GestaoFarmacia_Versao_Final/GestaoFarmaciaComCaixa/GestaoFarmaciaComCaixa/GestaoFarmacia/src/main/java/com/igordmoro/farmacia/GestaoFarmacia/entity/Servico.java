package com.igordmoro.farmacia.GestaoFarmacia.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servicos")
public class Servico implements Serializable {

	@Column(name = "impacto_financeiro") // Novo campo
	private Double impactoFinanceiro;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_servico")
	private Long idServico;

	@ManyToOne
	@JoinColumn(name = "funcionario_id", nullable = false)
	private Funcionario funcionario;
	public Double getImpactoFinanceiro() {
		return impactoFinanceiro;
	}

	public void setImpactoFinanceiro(Double impactoFinanceiro) {
		this.impactoFinanceiro = impactoFinanceiro;
	}
	@ManyToOne
	@JoinColumn(name = "transportadora_id", nullable = false)
	private Transportadora transportadora;

	@ManyToMany
	@JoinTable(
			name = "servico_produtos",
			joinColumns = @JoinColumn(name = "servico_id"),
			inverseJoinColumns = @JoinColumn(name = "produto_id")
	)
	private List<Produto> produtos = new ArrayList<>();

	@ElementCollection
	@CollectionTable(
			name = "servico_quantidades",
			joinColumns = @JoinColumn(name = "servico_id")
	)
	@Column(name = "quantidade")
	private List<Integer> quantidades = new ArrayList<>();

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

	// ✅ Construtor padrão
	public Servico() {
		this.status = Status.ABERTO;
	}

	// ✅ Construtor com dados principais
	public Servico(Funcionario funcionario, Transportadora transportadora, TipoServico tipoServico, LocalDate data) {
		this.funcionario = funcionario;
		this.transportadora = transportadora;
		this.tipoServico = tipoServico;
		this.data = data;
		this.status = Status.ABERTO;
	}

	// ✅ Getters e Setters
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

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	public List<Integer> getQuantidades() {
		return quantidades;
	}

	public void setQuantidades(List<Integer> quantidades) {
		this.quantidades = quantidades;
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

	public void setValor(double valor) {
		this.valor = valor;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	// ✅ Métodos auxiliares para garantir consistência
	public boolean isListaProdutosQuantidadesConsistente() {
		return produtos != null && quantidades != null && produtos.size() == quantidades.size();
	}

	public void adicionarProduto(Produto produto, int quantidade) {
		this.produtos.add(produto);
		this.quantidades.add(quantidade);
	}

	public void removerProduto(int index) {
		this.produtos.remove(index);
		this.quantidades.remove(index);
	}

	public double calcularSubtotal(int index) {
		Produto produto = produtos.get(index);
		int quantidade = quantidades.get(index);
		double precoUnitario = tipoServico == TipoServico.VENDA ? produto.getPrecoVenda() : produto.getPrecoCusto();
		return precoUnitario * quantidade;
	}

	@PrePersist
	@PreUpdate
	private void validarConsistencia() {
		if (!isListaProdutosQuantidadesConsistente()) {
			throw new IllegalStateException("A lista de produtos e a lista de quantidades devem ter o mesmo tamanho.");
		}
	}
}
