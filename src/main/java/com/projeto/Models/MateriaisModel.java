package com.projeto.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;

@Entity
@Table(name = "materiais_procedimento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MateriaisModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; // Ex: Henna
    private BigDecimal preco; // Ex: 40.00
    private BigDecimal rendimento; // Ex: 20 (aplicações)

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private ProcedimentosModel procedimento;
}