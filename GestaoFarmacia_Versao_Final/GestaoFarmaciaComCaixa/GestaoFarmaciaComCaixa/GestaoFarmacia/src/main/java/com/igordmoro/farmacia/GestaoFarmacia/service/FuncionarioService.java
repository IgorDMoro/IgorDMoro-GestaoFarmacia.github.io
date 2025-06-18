// src/main/java/com/igordmoro/farmacia/GestaoFarmacia/service/FuncionarioService.java

package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor;
import com.igordmoro.farmacia.GestaoFarmacia.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoService cargoService;

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository, CargoService cargoService) {
        this.funcionarioRepository = funcionarioRepository;
        this.cargoService = cargoService;
    }

    @Transactional
    public Funcionario salvarFuncionario(Funcionario funcionario) {
        // Validação básica do Cargo antes de salvar
        if (funcionario.getCargo() == null || funcionario.getCargo().getId() == null) {
            throw new IllegalArgumentException("Cargo é obrigatório para o funcionário.");
        }
        // Busca o Cargo completo do banco de dados para garantir que não estamos salvando um Cargo incompleto
        Cargo cargoExistente = cargoService.buscarCargoPorId(funcionario.getCargo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cargo com ID " + funcionario.getCargo().getId() + " não encontrado."));

        funcionario.setCargo(cargoExistente); // Atribui o cargo persistido

        // Calcular salário líquido e imposto baseado no cargo
        double salarioBruto = funcionario.getSalarioBruto();
        double imposto = calcularImposto(salarioBruto);
        double salarioLiquido = salarioBruto - imposto;

        // Deduzir benefícios do cargo (se existirem e forem aplicáveis)
        if (cargoExistente != null) {
            salarioLiquido -= cargoExistente.getValeAlimento();
            salarioLiquido -= cargoExistente.getValeTransporte();
            salarioLiquido -= cargoExistente.getPlanoSaude();
            salarioLiquido -= cargoExistente.getPlanoOdonto();
        }

        funcionario.setImposto(imposto);
        funcionario.setSalarioLiquido(salarioLiquido);

        return funcionarioRepository.save(funcionario);
    }

    // NOVO MÉTODO PARA ATUALIZAR FUNCIONÁRIO
    @Transactional
    public Funcionario atualizarFuncionario(Long id, Funcionario funcionarioDetails) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Funcionário com ID " + id + " não encontrado."));

        // Atualizar campos do funcionário existente com os detalhes fornecidos
        funcionarioExistente.setNome(funcionarioDetails.getNome());
        funcionarioExistente.setSalarioBruto(funcionarioDetails.getSalarioBruto());
        funcionarioExistente.setIdade(funcionarioDetails.getIdade());
        funcionarioExistente.setGenero(funcionarioDetails.getGenero());

        // Se o cargo foi alterado, busque e atribua o novo cargo
        if (funcionarioDetails.getCargo() != null && funcionarioDetails.getCargo().getId() != null) {
            Cargo novoCargo = cargoService.buscarCargoPorId(funcionarioDetails.getCargo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Novo Cargo com ID " + funcionarioDetails.getCargo().getId() + " não encontrado."));
            funcionarioExistente.setCargo(novoCargo);
        } else if (funcionarioDetails.getCargo() == null) {
            throw new IllegalArgumentException("Cargo não pode ser nulo para o funcionário.");
        }

        // Recalcular salário líquido e imposto com base nos novos dados e cargo
        double salarioBruto = funcionarioExistente.getSalarioBruto();
        double imposto = calcularImposto(salarioBruto);
        double salarioLiquido = salarioBruto - imposto;

        // Deduzir benefícios do cargo (do cargo atualizado do funcionário)
        if (funcionarioExistente.getCargo() != null) {
            salarioLiquido -= funcionarioExistente.getCargo().getValeAlimento();
            salarioLiquido -= funcionarioExistente.getCargo().getValeTransporte();
            salarioLiquido -= funcionarioExistente.getCargo().getPlanoSaude();
            salarioLiquido -= funcionarioExistente.getCargo().getPlanoOdonto();
        }

        funcionarioExistente.setImposto(imposto);
        funcionarioExistente.setSalarioLiquido(salarioLiquido);

        return funcionarioRepository.save(funcionarioExistente);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    @Transactional
    public void deletarFuncionario(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new NoSuchElementException("Funcionário com ID " + id + " não encontrado.");
        }
        funcionarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarFuncionariosPorSetor(Setor setor) {
        return funcionarioRepository.findByCargo_Setor(setor);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> contarFuncionariosPorSetor() {
        return funcionarioRepository.findAll().stream()
                .filter(f -> f.getCargo() != null && f.getCargo().getSetor() != null)
                .collect(Collectors.groupingBy(f -> f.getCargo().getSetor().name(), Collectors.counting()));
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> contarFuncionariosPorIdCargo() {
        return funcionarioRepository.findAll().stream()
                .filter(f -> f.getCargo() != null && f.getCargo().getId() != null)
                .collect(Collectors.groupingBy(f -> f.getCargo().getId(), Collectors.counting()));
    }

    private double calcularImposto(double salarioBruto) {
        // Exemplo simples de cálculo de imposto
        if (salarioBruto <= 2000.00) {
            return 0;
        } else if (salarioBruto <= 3000.00) {
            return salarioBruto * 0.075;
        } else if (salarioBruto <= 4000.00) {
            return salarioBruto * 0.15;
        } else {
            return salarioBruto * 0.275;
        }
    }
}