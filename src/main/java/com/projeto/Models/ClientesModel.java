package com.projeto.Models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientesModel {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String documento; // CPF ou CNPJ
    private String email;
    private String redesocial;
    private String telefone;
    private String telefone2;

    @JsonIgnore
    @OneToMany(mappedBy = "clientes")
    private List<MapeamentosModel> mapeamentos;
    
    @JsonIgnore
    @OneToMany(mappedBy = "clientes")
    private List<AgendamentosModel> agendamentos;
}