package com.projeto.Controllers;

import com.projeto.Models.DespesasModel;
import com.projeto.Repository.DespesasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projeto.Services.NotaFiscalService;
import java.util.Map;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/despesas")
public class DespesasController {

    @Autowired
    private DespesasRepository repository;
    @Autowired
    private NotaFiscalService notaService;

    @GetMapping
    public List<DespesasModel> listar() {
        return repository.findAll();
    }

    @PostMapping("/importar/qrcode")
    public ResponseEntity<?> importarPorQrCode(@RequestBody Map<String, String> payload) {
        String url = payload.get("url");
        try {
            notaService.lerItensDaUrl(url);
            return ResponseEntity.ok().body("Nota processada e despesa criada!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao ler nota: " + e.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DespesasModel criar(@RequestBody DespesasModel despesa) {
        if (despesa.getDataDespesa() == null) {
            despesa.setDataDespesa(LocalDate.now());
        }
        return repository.save(despesa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}