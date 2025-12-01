package com.projeto.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.Models.ClientesModel;

@Repository
public interface ClientesRepository extends JpaRepository<ClientesModel, Long> {

}
