package com.cibertec.edu.app.service;

import com.cibertec.edu.app.dto.ReporteRequest;
import com.cibertec.edu.app.dto.ReporteResponse;
import com.cibertec.edu.app.dto.UsuarioDto;
import com.cibertec.edu.app.entity.Reporte;
import com.cibertec.edu.app.entity.TipoRiesgo;
import com.cibertec.edu.app.entity.Usuario;
import com.cibertec.edu.app.enums.EstadoReporte;
import com.cibertec.edu.app.exception.ResourceNotFoundException;
import com.cibertec.edu.app.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final TipoRiesgoService tipoRiesgoService;

    public ReporteResponse create(Usuario usuario, ReporteRequest request, MultipartFile foto) {
        TipoRiesgo tipoRiesgo = tipoRiesgoService.getEntityById(request.getTipoRiesgoId());

        Reporte reporte = Reporte.builder()
                .usuario(usuario)
                .tipoRiesgo(tipoRiesgo)
                .descripcion(request.getDescripcion())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .fechaRegistro(LocalDateTime.now())
                .estado(EstadoReporte.PENDIENTE)
                .build();

        if (foto != null && !foto.isEmpty()) {
            String fotoUrl = saveFile(foto);
            reporte.setFotoUrl(fotoUrl);
        }

        reporte = reporteRepository.save(reporte);
        return toDto(reporte);
    }

    public ReporteResponse findById(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
        return toDto(reporte);
    }

    public List<ReporteResponse> findByUsuario(Usuario usuario) {
        return reporteRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    private String saveFile(MultipartFile file) {
        try {
            String uploadDir = "uploads/reportes";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            return "/uploads/reportes/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la foto", e);
        }
    }

    private ReporteResponse toDto(Reporte r) {
        UsuarioDto usuarioDto = UsuarioDto.builder()
                .id(r.getUsuario().getId())
                .nombre(r.getUsuario().getNombre())
                .email(r.getUsuario().getEmail())
                .rol(r.getUsuario().getRol() != null ? r.getUsuario().getRol().name() : null)
                .build();

        return ReporteResponse.builder()
                .id(r.getId())
                .usuario(usuarioDto)
                .tipoRiesgo(r.getTipoRiesgo() != null ? r.getTipoRiesgo().getNombre() : null)
                .descripcion(r.getDescripcion())
                .latitud(r.getLatitud())
                .longitud(r.getLongitud())
                .fotoUrl(r.getFotoUrl())
                .fechaRegistro(r.getFechaRegistro())
                .estado(r.getEstado().name())
                .build();
    }
}
