package com.projeto.Repositories;

import com.projeto.Models.EstoqueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<EstoqueModel, Long> {
    // Facilita buscar se o item já existe pelo nome
    EstoqueModel findByNomeItem(String nomeItem);
}