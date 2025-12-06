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

    private String descricao; // Ex: "Compra Semanal", "Conta de Luz"

    private BigDecimal valorTotal;

    private LocalDate dataDespesa;

    // Categoria: "MATERIAL", "FIXO" (Aluguel), "PESSOAL", etc.
    private String categoria;

    // Aqui vamos salvar o link do QR Code da nota (para consulta futura)
    @Column(length = 1000) // Aumentamos o tamanho porque links de nota são grandes
    private String linkNotaFiscal;
}