package com.cibertec.edu.app.service;

import com.cibertec.edu.app.dto.TipoRiesgoResponse;
import com.cibertec.edu.app.entity.TipoRiesgo;
import com.cibertec.edu.app.exception.ResourceNotFoundException;
import com.cibertec.edu.app.repository.TipoRiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoRiesgoService {

    private final TipoRiesgoRepository tipoRiesgoRepository;

    public List<TipoRiesgoResponse> findAll() {
        return tipoRiesgoRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public TipoRiesgoResponse findById(Long id) {
        TipoRiesgo tipo = tipoRiesgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de riesgo no encontrado"));
        return toDto(tipo);
    }

    public TipoRiesgo getEntityById(Long id) {
        return tipoRiesgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de riesgo no encontrado"));
    }

    private TipoRiesgoResponse toDto(TipoRiesgo t) {
        return TipoRiesgoResponse.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .icono(t.getIcono())
                .activo(t.getActivo())
                .build();
    }
}
