package com.projeto.Controllers;

import com.projeto.Models.EmpresaModel;
import com.projeto.Repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {

    @Autowired
    private EmpresaRepository repository;

    // Busca a configuração (sempre pega a primeira)
    @GetMapping
    public ResponseEntity<EmpresaModel> getConfig() {
        List<EmpresaModel> lista = repository.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista.get(0));
    }

    // Salva ou Atualiza
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> salvarConfig(
            @RequestParam("nome") String nome,
            @RequestParam(value = "logo", required = false) MultipartFile logo
    ) {
        try {
            EmpresaModel empresa;
            List<EmpresaModel> lista = repository.findAll();
            
            if (lista.isEmpty()) {
                empresa = new EmpresaModel(); // Cria novo
            } else {
                empresa = lista.get(0); // Edita existente
            }

            empresa.setNomeEmpresa(nome);
            
            // Só atualiza a logo se o usuário enviou uma nova
            if (logo != null && !logo.isEmpty()) {
                empresa.setLogo(logo.getBytes());
                empresa.setTipoArquivoLogo(logo.getContentType());
            }

            repository.save(empresa);
            return ResponseEntity.ok().build();

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na imagem");
        }
    }
}