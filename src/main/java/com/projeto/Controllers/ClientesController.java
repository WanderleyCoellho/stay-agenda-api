package com.projeto.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.projeto.Models.ClientesModel;
import com.projeto.Repositories.ClientesRepository;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClientesController {

    @Autowired
    private ClientesRepository clienteRepository;

    // --- CREATE (POST) ---
    @PostMapping
    public ClientesModel criarCliente(@RequestBody ClientesModel cliente) {
        return clienteRepository.save(cliente);
    }

    // --- READ (GET) ---
    @GetMapping
    public List<ClientesModel> listarClientes() {
        return clienteRepository.findAll();
    }

    // --- READ (GET por ID) ---
    @GetMapping("/{id}")
    public ResponseEntity<ClientesModel> buscarClientePorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> ResponseEntity.ok().body(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- UPDATE (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<ClientesModel> atualizarCliente(@PathVariable Long id,
            @RequestBody ClientesModel clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNome(clienteAtualizado.getNome());
                    cliente.setDocumento(clienteAtualizado.getDocumento());
                    cliente.setEmail(clienteAtualizado.getEmail());
                    cliente.setRedesocial(clienteAtualizado.getRedesocial());
                    cliente.setTelefone(clienteAtualizado.getTelefone());
                    cliente.setTelefone2(clienteAtualizado.getTelefone2());
                    ClientesModel clienteSalvo = clienteRepository.save(cliente);
                    return ResponseEntity.ok().body(clienteSalvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- DELETE (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.delete(cliente);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
