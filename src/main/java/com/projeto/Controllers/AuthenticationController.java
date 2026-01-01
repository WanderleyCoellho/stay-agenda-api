package com.projeto.Controllers;

import com.projeto.Models.UsuariosModel;
import com.projeto.Repositories.UsuarioRepository;
import com.projeto.dto.LoginDTO;
import com.projeto.dto.RegisterDTO;
import com.projeto.dto.LoginResponseDTO;
import com.projeto.Security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private TokenService tokenService;

    // LOGIN: Retorna <Object> para aceitar tanto LoginResponseDTO quanto String de
    // erro
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((UsuariosModel) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");

        } catch (Exception e) {
            System.out.println("❌ ERRO GENÉRICO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // REGISTER: Retorna <Void> pois não tem corpo na resposta de sucesso
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDTO data) {
        if (this.repository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UsuariosModel newUser = new UsuariosModel(null, data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}