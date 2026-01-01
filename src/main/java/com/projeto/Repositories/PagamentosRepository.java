package com.projeto.Repositories;

import com.projeto.Models.PagamentosModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentosRepository extends JpaRepository<PagamentosModel, Long> {
    // Podemos criar filtros personalizados aqui no futuro se precisar
}