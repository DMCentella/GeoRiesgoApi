package com.cibertec.edu.app.service;

import com.cibertec.edu.app.dto.AlertaResponse;
import com.cibertec.edu.app.entity.Alerta;
import com.cibertec.edu.app.exception.ResourceNotFoundException;
import com.cibertec.edu.app.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public List<AlertaResponse> findAllActivas() {
        return alertaRepository.findByActivaTrue().stream()
                .map(this::toDto)
                .toList();
    }

    public List<AlertaResponse> findAll() {
        return alertaRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public AlertaResponse findById(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));
        return toDto(alerta);
    }

    private AlertaResponse toDto(Alerta a) {
        return AlertaResponse.builder()
                .id(a.getId())
                .titulo(a.getTitulo())
                .descripcion(a.getDescripcion())
                .tipoRiesgo(a.getTipoRiesgo() != null ? a.getTipoRiesgo().getNombre() : null)
                .nivel(a.getNivel().name())
                .latitud(a.getLatitud())
                .longitud(a.getLongitud())
                .activa(a.getActiva())
                .riesgoId(a.getRiesgo() != null ? a.getRiesgo().getId() : null)
                .build();
    }
}
