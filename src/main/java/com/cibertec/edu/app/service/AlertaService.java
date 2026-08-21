package com.cibertec.edu.app.service;

import com.cibertec.edu.app.dto.AlertaEvent;
import com.cibertec.edu.app.dto.AlertaRequest;
import com.cibertec.edu.app.dto.AlertaResponse;
import com.cibertec.edu.app.entity.Alerta;
import com.cibertec.edu.app.entity.Riesgo;
import com.cibertec.edu.app.entity.TipoRiesgo;
import com.cibertec.edu.app.exception.ResourceNotFoundException;
import com.cibertec.edu.app.repository.AlertaRepository;
import com.cibertec.edu.app.websocket.AlertaWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final TipoRiesgoService tipoRiesgoService;
    private final RiesgoService riesgoService;
    private final AlertaWebSocketHandler alertaWebSocketHandler;

    public AlertaResponse create(AlertaRequest request) {
        TipoRiesgo tipoRiesgo = tipoRiesgoService.getEntityById(request.getTipoRiesgoId());

        Alerta alerta = Alerta.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .tipoRiesgo(tipoRiesgo)
                .nivel(request.getNivel())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();

        if (request.getRiesgoId() != null) {
            Riesgo riesgo = riesgoService.getEntityById(request.getRiesgoId());
            alerta.setRiesgo(riesgo);
        }

        alerta = alertaRepository.save(alerta);
        AlertaResponse response = toDto(alerta);

        alertaWebSocketHandler.broadcast(
                AlertaEvent.builder()
                        .type("ALERTA_NUEVA")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build());

        return response;
    }

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
