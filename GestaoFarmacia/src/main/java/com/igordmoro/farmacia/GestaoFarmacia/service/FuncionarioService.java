package com.igordmoro.farmacia.GestaoFarmacia.service;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Cargo;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Setor; // Certifique-se de que este enum está correto
import com.igordmoro.farmacia.GestaoFarmacia.repository.FuncionarioRepository;
import com.igordmoro.farmacia.GestaoFarmacia.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe esta anotação

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.NoSuchElementException; // Importe para NoSuchElementException
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

    /**
     * Calcula o valor do Imposto de Renda (IR) com base na tabela fornecida.
     *
     * @param baseDeCalculo A base de cálculo para o IR (salário bruto - outros descontos).
     * @return O valor do imposto a ser deduzido.
     */
    private double calcularImpostoDeRenda(double baseDeCalculo) {
        // Tabela de Alíquotas e Deduções do IR:
        // Base de Cálculo (R$)           Alíquota (%)  Dedução do IR (R$)
        // Até R$ 2.428,80                  zero             zero
        // De R$ 2.428,81 até R$ 2.826,65  7,5 %            R$ 182,16
        // De R$ 2.826,66 até R$ 3.751,05  15 %             R$ 394,16
        // De R$ 3.751,06 até R$ 4.664,68  22,5 %           R$ 675,49
        // Acima de R$ 4.664,68              27,5 %           R$ 908,75

        if (baseDeCalculo <= 2428.80) {
            return 0; // Isento
        } else if (baseDeCalculo <= 2826.65) {
            return (baseDeCalculo * 0.075) - 182.16;
        } else if (baseDeCalculo <= 3751.05) {
            return (baseDeCalculo * 0.15) - 394.16;
        } else if (baseDeCalculo <= 4664.68) {
            return (baseDeCalculo * 0.225) - 675.49;
        } else { // Acima de R$ 4.664,68
            return (baseDeCalculo * 0.275) - 908.75;
        }
    }

    /**
     * Calcula o salário líquido e o imposto de renda para um funcionário.
     * Este método é chamado internamente antes de salvar ou ao buscar um funcionário.
     *
     * @param funcionario O objeto Funcionario a ter seus valores calculados.
     */
    private void calcularSalarioLiquidoEImposto(Funcionario funcionario) {
        double salarioBruto = funcionario.getSalarioBruto();
        double outrosDescontos = 0; // Placeholder para outros descontos (ex: INSS, Vales, Plano de Saúde)
        // Se houver, a lógica para obtê-los (do Cargo, por exemplo) deve ser adicionada aqui.

        // A base de cálculo do IR é o salário bruto menos outros descontos.
        double baseDeCalculoIR = salarioBruto - outrosDescontos;
        double impostoCalculado = calcularImpostoDeRenda(baseDeCalculoIR);

        // Garante que o imposto não seja negativo (casos onde a dedução é maior que o imposto bruto)
        if (impostoCalculado < 0) {
            impostoCalculado = 0;
        }

        double salarioLiquidoCalculado = salarioBruto - impostoCalculado - outrosDescontos;

        funcionario.setImposto(impostoCalculado);
        funcionario.setSalarioLiquido(salarioLiquidoCalculado);
    }

    /**
     * Salva um novo funcionário no banco de dados, realizando validações
     * e calculando o salário líquido e imposto.
     *
     * @param funcionario O objeto Funcionario a ser salvo.
     * @return O Funcionario salvo e atualizado com os cálculos.
     * @throws IllegalArgumentException Se alguma validação de negócio falhar.
     * @throws NoSuchElementException Se o Cargo associado não for encontrado.
     */
    @Transactional // Garante que a operação seja atômica
    public Funcionario salvarFuncionario(Funcionario funcionario) {
        // Validações de negócio
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

        // --- Tratamento e validação do Cargo associado ---
        if (funcionario.getCargo() == null || funcionario.getCargo().getId() == null) {
            throw new IllegalArgumentException("Funcionário deve ter um cargo associado com um ID válido.");
        }

        // Busca o Cargo pelo ID para garantir que ele existe e é uma entidade gerenciada pelo JPA
        Cargo existingCargo = cargoRepository.findById(funcionario.getCargo().getId())
                .orElseThrow(() -> new NoSuchElementException("Cargo não encontrado com o ID fornecido: " + funcionario.getCargo().getId()));
        funcionario.setCargo(existingCargo); // Associa o cargo gerenciado ao funcionário

        // Calcula o salário líquido e o imposto antes de salvar
        calcularSalarioLiquidoEImposto(funcionario);

        return funcionarioRepository.save(funcionario);
    }

    /**
     * Lista todos os funcionários, garantindo que seus salários líquidos e impostos estejam calculados.
     *
     * @return Uma lista de todos os funcionários.
     */
    @Transactional(readOnly = true) // Otimização para operações de leitura
    public List<Funcionario> listarTodosFuncionarios() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        // Garante que o salário líquido e imposto estejam calculados para cada funcionário retornado
        funcionarios.forEach(this::calcularSalarioLiquidoEImposto);
        return funcionarios;
    }

    /**
     * Busca um funcionário pelo seu ID, calculando o salário líquido e imposto se encontrado.
     *
     * @param id O ID do funcionário.
     * @return Um Optional contendo o funcionário, se encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        // Garante que o salário líquido e imposto estejam calculados se o funcionário for encontrado
        funcionario.ifPresent(this::calcularSalarioLiquidoEImposto);
        return funcionario;
    }

    /**
     * Deleta um funcionário pelo seu ID.
     *
     * @param id O ID do funcionário a ser deletado.
     * @throws NoSuchElementException Se o funcionário não for encontrado.
     */
    @Transactional
    public void deletarFuncionario(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new NoSuchElementException("Funcionário não encontrado com ID: " + id);
        }
        funcionarioRepository.deleteById(id);
    }

    /**
     * Atualiza os dados de um funcionário existente.
     *
     * @param id O ID do funcionário a ser atualizado.
     * @param funcionarioDetails Um objeto Funcionario contendo os dados atualizados.
     * @return O Funcionario atualizado e salvo.
     * @throws NoSuchElementException Se o funcionário ou o novo Cargo não forem encontrados.
     * @throws IllegalArgumentException Se alguma validação de negócio falhar.
     */
    @Transactional
    public Funcionario atualizarFuncionario(Long id, Funcionario funcionarioDetails) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado com ID: " + id));

        // Atualiza apenas os campos que podem ser modificados pelo payload
        funcionarioExistente.setNome(funcionarioDetails.getNome());
        funcionarioExistente.setSalarioBruto(funcionarioDetails.getSalarioBruto());
        funcionarioExistente.setIdade(funcionarioDetails.getIdade());
        funcionarioExistente.setGenero(funcionarioDetails.getGenero());

        // --- Atualização e validação do Cargo ---
        if (funcionarioDetails.getCargo() != null && funcionarioDetails.getCargo().getId() != null) {
            Cargo novoCargo = cargoRepository.findById(funcionarioDetails.getCargo().getId())
                    .orElseThrow(() -> new NoSuchElementException("Cargo não encontrado com o ID fornecido: " + funcionarioDetails.getCargo().getId()));
            funcionarioExistente.setCargo(novoCargo); // Associa o novo cargo gerenciado
        } else if (funcionarioDetails.getCargo() == null) {
            // Se o Cargo for explicitamente nulo no payload de atualização e for obrigatório.
            throw new IllegalArgumentException("Funcionário deve ter um cargo associado.");
        }
        // Se o Cargo no funcionarioDetails não for nulo, mas o ID for nulo, a validação acima já o pegou.

        // Recalcula o salário líquido e imposto com os novos dados
        calcularSalarioLiquidoEImposto(funcionarioExistente);

        return funcionarioRepository.save(funcionarioExistente);
    }

    /**
     * Busca funcionários pertencentes a um setor específico.
     *
     * @param setor O enum Setor para filtrar os funcionários.
     * @return Uma lista de funcionários do setor especificado.
     */
    @Transactional(readOnly = true)
    public List<Funcionario> buscarFuncionariosPorSetor(Setor setor) {
        List<Funcionario> funcionarios = funcionarioRepository.findByCargo_Setor(setor);
        funcionarios.forEach(this::calcularSalarioLiquidoEImposto);
        return funcionarios;
    }

    /**
     * Conta o número de funcionários por setor.
     *
     * @return Um mapa onde a chave é o nome do setor e o valor é a contagem de funcionários.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> contarFuncionariosPorSetor() {
        Map<String, Long> contagemPorSetor = new HashMap<>();
        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll(); // Pode ser otimizado com query nativa/JPQL para grandes volumes

        for (Setor setor : Setor.values()) {
            long count = todosFuncionarios.stream()
                    .filter(f -> f.getCargo() != null && f.getCargo().getSetor() == setor)
                    .count();
            // Substitui "_" por " " para melhor apresentação do nome do setor
            contagemPorSetor.put(setor.name().replace("_", " "), count);
        }
        return contagemPorSetor;
    }
}