package com.projeto.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mapeamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapeamentosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao; // Sua descrição do mapeamento

    // --- NOVOS CAMPOS PARA SUPORTAR VÍDEO E FOTO ---
    private String nomeArquivo; // ex: "meu_video.mp4"
    private String tipoArquivo; // ex: "video/mp4" ou "image/jpeg"

    @Lob // Indica objeto grande
    @Column(name = "midia", columnDefinition = "LONGBLOB") // Garante espaço no MySQL
    private byte[] midia; 

    // --- RELACIONAMENTOS ---
    
    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private ProcedimentosModel procedimentos;
    
    @ManyToOne
    @JoinColumn(name = "agendamento_id")
    private AgendamentosModel agendamentos;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClientesModel clientes;
}