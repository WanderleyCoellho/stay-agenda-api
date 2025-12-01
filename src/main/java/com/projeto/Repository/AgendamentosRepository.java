package com.projeto.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.Models.AgendamentosModel;

public interface AgendamentosRepository extends JpaRepository<AgendamentosModel, Long> {

    // O Spring Data cria o SQL sozinho baseado no nome do método
    List<AgendamentosModel> findByData(LocalDate data);
    
    // Opcional: Se quiser ordenar por horário
    List<AgendamentosModel> findByDataOrderByHoraInicialAsc(LocalDate data);
}
