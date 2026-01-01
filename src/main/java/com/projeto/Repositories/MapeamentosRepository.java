package com.projeto.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.Models.MapeamentosModel;

public interface MapeamentosRepository extends JpaRepository<MapeamentosModel, Long> {

    List<MapeamentosModel> findByClientesId(Long clienteId);
}
