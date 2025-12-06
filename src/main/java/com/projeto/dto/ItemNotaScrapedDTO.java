package com.projeto.dto; // Ajuste para o nome do seu pacote

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemNotaScrapedDTO {

    // Estes são os campos que vamos "raspar" do site
    private String nomeProduto;
    private String unidadeMedida; // Ex: UN, KG, CX
    private Double quantidade;
    private Double valorUnitario;
    private Double valorTotalItem;
    private String codigo;
}