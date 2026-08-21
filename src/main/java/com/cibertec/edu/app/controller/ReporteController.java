package com.cibertec.edu.app.controller;

import com.cibertec.edu.app.dto.ReporteRequest;
import com.cibertec.edu.app.dto.ReporteResponse;
import com.cibertec.edu.app.entity.Usuario;
import com.cibertec.edu.app.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints de reportes ciudadanos")
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear un reporte ciudadano con foto opcional")
    public ResponseEntity<ReporteResponse> create(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestPart("datos") ReporteRequest request,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return ResponseEntity.ok(reporteService.create(usuario, request, foto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un reporte por ID")
    public ResponseEntity<ReporteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.findById(id));
    }
}
