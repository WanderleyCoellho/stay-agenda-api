package com.projeto.Repository;

import com.projeto.Models.UsuariosModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuariosModel, Long> {
    
    // Método que o Spring Security vai usar para achar o usuário no banco
    UserDetails findByLogin(String login);
}