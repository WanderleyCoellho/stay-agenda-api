package com.projeto.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.Models.CategoriasModel;

@Repository
public interface CategoriasRepository extends JpaRepository<CategoriasModel, Long> {

}
