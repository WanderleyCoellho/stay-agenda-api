package com.projeto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MovimentacaoFinanceiraDTO {
    private Long id;
    private LocalDate data;
    private String descricao;
    private BigDecimal valor; // Valor Líquido já calculado
    private String tipo; // "ENTRADA" ou "SAIDA"
    private String categoria; // Ex: "PIX", "CARTÃO", "LUZ", "ÁGUA"
}