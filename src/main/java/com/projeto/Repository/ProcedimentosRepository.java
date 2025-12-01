package com.projeto.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.Models.ProcedimentosModel;

public interface ProcedimentosRepository extends JpaRepository<ProcedimentosModel, Long> {

}
