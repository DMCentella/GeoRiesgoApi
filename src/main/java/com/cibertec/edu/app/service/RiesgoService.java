package com.cibertec.edu.app.service;

import com.cibertec.edu.app.dto.RiesgoResponse;
import com.cibertec.edu.app.entity.Riesgo;
import com.cibertec.edu.app.enums.NivelRiesgo;
import com.cibertec.edu.app.exception.ResourceNotFoundException;
import com.cibertec.edu.app.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;

    public List<RiesgoResponse> findAll(Long tipoRiesgoId, String nivelStr) {
        List<Riesgo> riesgos;

        if (tipoRiesgoId != null && nivelStr != null) {
            NivelRiesgo nivel = NivelRiesgo.valueOf(nivelStr.toUpperCase());
            riesgos = riesgoRepository.findByActivoTrueAndTipoRiesgoIdAndNivel(tipoRiesgoId, nivel);
        } else if (tipoRiesgoId != null) {
            riesgos = riesgoRepository.findByActivoTrueAndTipoRiesgoId(tipoRiesgoId);
        } else if (nivelStr != null) {
            NivelRiesgo nivel = NivelRiesgo.valueOf(nivelStr.toUpperCase());
            riesgos = riesgoRepository.findByActivoTrueAndNivel(nivel);
        } else {
            riesgos = riesgoRepository.findByActivoTrue();
        }

        return riesgos.stream().map(this::toDto).toList();
    }

    public RiesgoResponse findById(Long id) {
        Riesgo riesgo = riesgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado"));
        return toDto(riesgo);
    }

    public Riesgo getEntityById(Long id) {
        return riesgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado"));
    }

    private RiesgoResponse toDto(Riesgo r) {
        return RiesgoResponse.builder()
                .id(r.getId())
                .tipo(r.getTipoRiesgo() != null ? r.getTipoRiesgo().getNombre() : null)
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .nivel(r.getNivel().name())
                .latitud(r.getLatitud())
                .longitud(r.getLongitud())
                .fechaRegistro(r.getFechaRegistro())
                .fechaActualizacion(r.getFechaActualizacion())
                .activo(r.getActivo())
                .build();
    }
}
