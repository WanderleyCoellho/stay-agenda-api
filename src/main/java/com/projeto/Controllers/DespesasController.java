package com.projeto.Controllers;

import com.projeto.Models.DespesasModel;
import com.projeto.Repositories.DespesasRepository; // Verifique se é Repository ou Repositories no seu pacote
import com.projeto.Services.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/despesas")
@CrossOrigin(origins = "*") // Importante para o Frontend acessar
public class DespesasController {

    @Autowired
    private DespesasRepository repository;

    @Autowired
    private NotaFiscalService notaService;

    // 1. Listar (Original)
    @GetMapping
    public List<DespesasModel> listar() {
        return repository.findAll();
    }

    @GetMapping(params = { "inicio", "fim" })
    public List<DespesasModel> listar(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {
        if (inicio != null && fim != null) {
            LocalDate dataInicio = LocalDate.parse(inicio);
            LocalDate dataFim = LocalDate.parse(fim);
            return repository.findByDataDespesaBetween(dataInicio, dataFim);
        }
        return repository.findAll();
    }

    // 2. Importar QR Code (Original Restaurado)
    @PostMapping("/importar/qrcode")
    public ResponseEntity<?> importarPorQrCode(@RequestBody Map<String, String> payload) {
        String url = payload.get("url"); // O Frontend precisa enviar a chave "url"
        try {
            // O seu serviço faz a mágica de ler e salvar
            notaService.lerItensDaUrl(url);
            return ResponseEntity.ok().body("Nota processada e despesa criada!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao ler nota: " + e.getMessage());
        }
    }

    // 3. Criar Manualmente (Híbrido: Original + Melhorias de Data)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DespesasModel criar(@RequestBody DespesasModel despesa) {
        if (despesa.getDataDespesa() == null) {
            despesa.setDataDespesa(LocalDate.now());
        }
        if (despesa.getPago() == null) {
            despesa.setPago(false);
        }
        return repository.save(despesa);
    }

    // 4. Buscar por ID (Necessário para a tela de Edição funcionar)
    @GetMapping("/{id}")
    public ResponseEntity<DespesasModel> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Atualizar (Necessário para Salvar Vencimento, Status e Fornecedor)
    @PutMapping("/{id}")
    public ResponseEntity<DespesasModel> atualizar(@PathVariable Long id,
            @RequestBody DespesasModel despesaAtualizada) {
        return repository.findById(id)
                .map(record -> {
                    // Atualiza campos básicos
                    record.setDescricao(despesaAtualizada.getDescricao());
                    record.setValorTotal(despesaAtualizada.getValorTotal());
                    record.setCategoria(despesaAtualizada.getCategoria());
                    record.setDataDespesa(despesaAtualizada.getDataDespesa());

                    // Atualiza campos novos de gestão
                    record.setFornecedor(despesaAtualizada.getFornecedor());
                    record.setDataVencimento(despesaAtualizada.getDataVencimento());
                    record.setPago(despesaAtualizada.getPago());

                    DespesasModel updated = repository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    // 6. Deletar (Original)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}