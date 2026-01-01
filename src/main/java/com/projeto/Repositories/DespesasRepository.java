package com.projeto.Repositories;

import com.projeto.Models.DespesasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DespesasRepository extends JpaRepository<DespesasModel, Long> {
    // Para filtrar despesas por mês no futuro
    List<DespesasModel> findByDataDespesaBetween(LocalDate inicio, LocalDate fim);
}