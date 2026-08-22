package com.cibertec.edu.app.controller;

import com.cibertec.edu.app.dto.UpdateUsuarioRequest;
import com.cibertec.edu.app.dto.UsuarioDto;
import com.cibertec.edu.app.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints de perfil de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil de un usuario por id")
    public ResponseEntity<UsuarioDto> getMe(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(usuarioService.getMe(usuarioId));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar nombre de un usuario")
    public ResponseEntity<UsuarioDto> updateMe(@Valid @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.updateMe(request));
    }
}
