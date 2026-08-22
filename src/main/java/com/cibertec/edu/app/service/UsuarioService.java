package com.cibertec.edu.app.service;

import com.cibertec.edu.app.dto.UpdateUsuarioRequest;
import com.cibertec.edu.app.dto.UsuarioDto;
import com.cibertec.edu.app.entity.Usuario;
import com.cibertec.edu.app.exception.ResourceNotFoundException;
import com.cibertec.edu.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDto getMe(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .build();
    }

    public UsuarioDto updateMe(Usuario usuario, UpdateUsuarioRequest request) {
        usuario.setNombre(request.getNombre());
        usuario = usuarioRepository.save(usuario);

        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .build();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
