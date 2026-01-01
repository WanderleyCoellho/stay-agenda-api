package com.projeto.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.Models.ProcedimentosModel;

public interface ProcedimentosRepository extends JpaRepository<ProcedimentosModel, Long> {

}
