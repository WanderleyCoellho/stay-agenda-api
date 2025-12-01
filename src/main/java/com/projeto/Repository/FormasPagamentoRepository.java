package com.projeto.Repository;

import com.projeto.Models.FormasPagamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormasPagamentoRepository extends JpaRepository<FormasPagamentoModel, Long> {
}