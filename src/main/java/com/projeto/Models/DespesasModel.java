package com.projeto.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "despesas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DespesasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private BigDecimal valorTotal;

    private LocalDate dataDespesa;

    private String categoria;

    @Column(length = 1000)
    private String linkNotaFiscal;

    private String fornecedor;

    private LocalDate dataVencimento;

    private Boolean pago;
}