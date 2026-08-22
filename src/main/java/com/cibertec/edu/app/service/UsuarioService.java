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

    public UsuarioDto getMe(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return toDto(usuario);
    }

    public UsuarioDto updateMe(UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setNombre(request.getNombre());
        usuario = usuarioRepository.save(usuario);
        return toDto(usuario);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private UsuarioDto toDto(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }
}
