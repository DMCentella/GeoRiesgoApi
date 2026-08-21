package com.cibertec.edu.app.controller;

import com.cibertec.edu.app.dto.UpdateUsuarioRequest;
import com.cibertec.edu.app.dto.UsuarioDto;
import com.cibertec.edu.app.entity.Usuario;
import com.cibertec.edu.app.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints de perfil de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<UsuarioDto> getMe(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.getMe(usuario));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<UsuarioDto> updateMe(@AuthenticationPrincipal Usuario usuario,
                                                @Valid @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.updateMe(usuario, request));
    }
}
