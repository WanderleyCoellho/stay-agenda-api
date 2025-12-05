package com.projeto.Models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "procedimentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedimentosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relacionamento --- Um procedimento tem uma unica categoria
    @ManyToOne
    // Foreign Key/Chave Estrangeira --- ou seja a coluna que referencia a outra
    // tabela
    @JoinColumn(name = "categoria_id")
    private CategoriasModel categoria;

    @JsonIgnore
    @OneToMany(mappedBy = "procedimento")
    private List<PromocoesModel> promocao;

    @JsonIgnore
    @OneToMany(mappedBy = "procedimentos")
    private List<MapeamentosModel> mapeamentos;

    @JsonIgnore
    @ManyToMany(mappedBy = "procedimentos") // Aponta para a lista 'procedimentos' no AgendamentosModel
    private List<AgendamentosModel> agendamentos;

    private String procedimento;
    private String descricao;
    private BigDecimal valor;
    private BigDecimal valorManual;

    // --- NOVOS CAMPOS DE PRECIFICAÇÃO ---
    private Integer tempoMinutos; // Ex: 45 min
    private BigDecimal margemLucro; // Ex: 50%
    private Boolean usarPrecificacao; // A tal flag (Se true, bloqueia edição manual do valor?)

    // --- LISTA DE MATERIAIS SALVOS ---
    @OneToMany(mappedBy = "procedimento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MateriaisModel> materiais = new ArrayList<>();

}
