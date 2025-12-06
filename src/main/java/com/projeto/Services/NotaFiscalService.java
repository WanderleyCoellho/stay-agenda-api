package com.projeto.Services;

import com.projeto.dto.ItemNotaScrapedDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotaFiscalService {

    /**
     * Método principal que recebe a URL e devolve a lista de itens
     */
    public List<ItemNotaScrapedDTO> lerItensDaUrl(String htmlUrl) {
        List<ItemNotaScrapedDTO> listaDeItens = new ArrayList<>();

        try {
            // 1. Conecta na URL (Finge ser um navegador Mozilla para não ser bloqueado)
            Document doc = Jsoup.connect(htmlUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // 2. Tenta encontrar as linhas da tabela.
            // O seletor "tr[id^=Item]" funciona na maioria das SEFAZ (significa id que
            // começa com "Item")
            Elements linhasDaTabela = doc.select("tr[id^=Item]");

            // SE NÃO ACHAR NADA, tenta um seletor genérico de tabela (fallback)
            if (linhasDaTabela.isEmpty()) {
                linhasDaTabela = doc.select("table tbody tr");
            }

            for (Element linha : linhasDaTabela) {
                // Tenta extrair os dados. Se a classe mudar, esses select precisam mudar.
                String nome = linha.select(".txtTit").text();

                // Se o nome vier vazio, pula essa linha (provavelmente não é produto)
                if (nome == null || nome.isEmpty())
                    continue;

                String qtdTexto = linha.select(".Rqtd").text(); // Ex: "Qtde.: 2,000"
                String valorTexto = linha.select(".RvlUnit").text(); // Ex: "Vl. Unit.: 10,00"

                // Tenta pegar o código (algumas notas mostram assim: (Código: 1234))
                String codigo = linha.select(".RCod").text();

                // 3. Conversão e Limpeza
                Double qtdNumerica = limparEConverter(qtdTexto);
                Double valorNumerico = limparEConverter(valorTexto);

                // 4. Preenche o DTO
                ItemNotaScrapedDTO itemDto = new ItemNotaScrapedDTO();
                itemDto.setNomeProduto(nome.toUpperCase()); // Salva em maiúsculo pra padronizar
                itemDto.setQuantidade(qtdNumerica);
                itemDto.setValorUnitario(valorNumerico);
                itemDto.setValorTotalItem(qtdNumerica * valorNumerico);
                itemDto.setCodigo(codigo);

                // Adiciona na lista final
                listaDeItens.add(itemDto);
            }

        } catch (IOException e) {
            // Se der erro de conexão, imprime no console e devolve lista vazia (ou lance
            // uma exceção personalizada)
            System.err.println("Erro ao conectar na SEFAZ: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro genérico ao processar nota: " + e.getMessage());
            e.printStackTrace();
        }

        return listaDeItens;
    }

    /**
     * Método Auxiliar Privado: Limpa a sujeira do texto e transforma em Double
     * Entrada esperada: "Qtde.: 2,000" ou "Vl. Unit.: 10,90"
     * Saída: 2.0 ou 10.9
     */
    private Double limparEConverter(String textoSujo) {
        if (textoSujo == null || textoSujo.isEmpty()) {
            return 0.0;
        }

        try {
            // 1. Remove rótulos comuns (Case Insensitive)
            String limpo = textoSujo.replaceAll("(?i)Qtde\\.:", "")
                    .replaceAll("(?i)Vl\\. Unit\\.:", "")
                    .replaceAll("(?i)UN:", "")
                    .trim();

            // 2. O Brasil usa vírgula para decimal. O Java usa Ponto.
            // Ex: "1.200,50" (mil duzentos e cinquenta centavos) -> vira "1200.50"
            if (limpo.contains(",")) {
                limpo = limpo.replace(".", ""); // Remove ponto de milhar se existir
                limpo = limpo.replace(",", "."); // Troca vírgula decimal por ponto
            }

            // 3. Converte
            return Double.parseDouble(limpo);

        } catch (NumberFormatException e) {
            // Se não conseguir converter, retorna 0.0 para não quebrar o sistema
            System.err.println("Não consegui converter o valor: " + textoSujo);
            return 0.0;
        }
    }
}