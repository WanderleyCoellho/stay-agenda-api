package com.projeto.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "formaspagamento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormasPagamentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; 
    private BigDecimal taxa; // Porcentagem

    // --- NOVO CAMPO ---
    // true = Cliente paga a taxa (acrescenta no valor)
    // false = Estabelecimento paga (desconta do valor)
    private Boolean repassarTaxa; 
}