package com.igordmoro.farmacia.GestaoFarmacia.repository;

import com.igordmoro.farmacia.GestaoFarmacia.entity.Servico;
import com.igordmoro.farmacia.GestaoFarmacia.entity.Status; // Importe o enum Status
import com.igordmoro.farmacia.GestaoFarmacia.entity.TipoServico; // Importe o enum TipoServico
import com.igordmoro.farmacia.GestaoFarmacia.entity.Funcionario; // Importe Funcionario se for usar em buscas
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    // JpaRepository já fornece: save(), findById(), findAll(), deleteById(), etc.

    // Métodos de busca personalizados baseados na lógica da antiga classe Empresa:
    List<Servico> findByStatus(Status status); // Para listar serviços em aberto, concluídos, etc.
    List<Servico> findByTipoServico(TipoServico tipoServico);
    List<Servico> findByDataBetween(LocalDate startDate, LocalDate endDate); // Para buscas por período
    List<Servico> findByFuncionario(Funcionario funcionario); // Para buscar serviços por funcionário responsável

    // Você pode adicionar métodos para buscar por mês ou ano, por exemplo:
    // List<Servico> findByData_Month(int month); // Não é suportado diretamente assim, mas JPA permite
    // @Query("SELECT s FROM Servico s WHERE FUNCTION('MONTH', s.data) = :month AND FUNCTION('YEAR', s.data) = :year")
    // List<Servico> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}