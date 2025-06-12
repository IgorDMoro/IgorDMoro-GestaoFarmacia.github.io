package com.igordmoro.farmacia.GestaoFarmacia.service; // Pacote em minúsculas

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor;
import com.igordmoro.farmacia.GestaoFarmacia.repository.FuncionarioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository, CargoRepository cargoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
    }

    public Funcionario salvarFuncionario(Funcionario funcionario) {
        // Validações de negócio para Funcionario
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio.");
        }
        if (funcionario.getIdade() <= 0) {
            throw new IllegalArgumentException("Idade do funcionário deve ser maior que zero.");
        }
        if (funcionario.getSalarioBruto() <= 0) {
            throw new IllegalArgumentException("Salário bruto do funcionário deve ser maior que zero.");
        }
        if (funcionario.getGenero() != 'M' && funcionario.getGenero() != 'F') {
            throw new IllegalArgumentException("Gênero inválido. Use 'M' para Masculino ou 'F' para Feminino.");
        }

        // --- Ponto de atenção para o Cargo ---
        if (funcionario.getCargo() == null) {
            throw new IllegalArgumentException("Funcionário deve ter um cargo associado.");
        }

        if (funcionario.getCargo().getId() != null) {
            // Se o Cargo já tem um ID, busca-o para garantir que ele existe no banco de dados
            Cargo existingCargo = cargoRepository.findById(funcionario.getCargo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cargo não encontrado com o ID fornecido: " + funcionario.getCargo().getId()));
            funcionario.setCargo(existingCargo); // Associa o cargo gerenciado pela JPA
        } else {
            // Se o Cargo não tem ID, é um novo Cargo que precisa ser salvo primeiro
            // Ou o cenário é que o Cargo deve ser pré-existente e você só passa o ID
            // Este `else` é mais complexo, geralmente preferimos que cargos sejam criados separadamente.
            // Se você espera criar um cargo "inline" com o funcionário, precisará de lógica aqui.
            // Por enquanto, vamos assumir que o cargo já existe ou será salvo, mas é melhor
            // que o cliente da API forneça um Cargo.id existente.
            // Para simplificar, vou remover a capacidade de criar um cargo "inline" aqui e exigir um ID.
            throw new IllegalArgumentException("Novo cargo deve ser criado separadamente ou um ID de cargo existente deve ser fornecido.");
        }

        calcularSalarioLiquidoEImposto(funcionario);

        return funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> listarTodosFuncionarios() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        funcionarios.forEach(this::calcularSalarioLiquidoEImposto);
        return funcionarios;
    }

    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        funcionario.ifPresent(this::calcularSalarioLiquidoEImposto);
        return funcionario;
    }

    public void deletarFuncionario(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Funcionário não encontrado com ID: " + id);
        }
        funcionarioRepository.deleteById(id);
    }

    public List<Funcionario> buscarFuncionariosPorSetor(Setor setor) {
        List<Funcionario> funcionarios = funcionarioRepository.findByCargo_Setor(setor);
        funcionarios.forEach(this::calcularSalarioLiquidoEImposto);
        return funcionarios;
    }

    public Map<String, Long> contarFuncionariosPorSetor() {
        Map<String, Long> contagemPorSetor = new HashMap<>();
        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();

        for (Setor setor : Setor.values()) {
            long count = todosFuncionarios.stream()
                    .filter(f -> f.getCargo() != null && f.getCargo().getSetor() == setor)
                    .count();
            contagemPorSetor.put(setor.name().replace("_", " "), count);
        }
        return contagemPorSetor;
    }

    private void calcularSalarioLiquidoEImposto(Funcionario f) {
        double imposto = 0;
        if (f.getSalarioBruto() > 4664.68) {
            imposto = f.getSalarioBruto() * 0.275;
        } else if (f.getSalarioBruto() >= 3751.06) {
            imposto = f.getSalarioBruto() * 0.225;
        } else if (f.getSalarioBruto() >= 2826.66) {
            imposto = f.getSalarioBruto() * 0.15;
        } else if (f.getSalarioBruto() >= 2112.01) {
            imposto = f.getSalarioBruto() * 0.075;
        }
        f.setImposto(imposto);
        f.setSalarioLiquido(f.getSalarioBruto() - imposto);
    }
}