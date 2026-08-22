package com.cibertec.edu.app.repository;

import com.cibertec.edu.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
