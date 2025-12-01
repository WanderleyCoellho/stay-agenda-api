package com.projeto.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "configuracao_empresa")
@Data
public class EmpresaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeEmpresa; // Ex: "Barbearia VIP"
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logo; // A imagem
    
    private String tipoArquivoLogo; // Ex: image/png
}