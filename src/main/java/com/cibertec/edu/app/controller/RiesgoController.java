package com.cibertec.edu.app.controller;

import com.cibertec.edu.app.dto.RiesgoResponse;
import com.cibertec.edu.app.service.RiesgoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/riesgos")
@RequiredArgsConstructor
@Tag(name = "Riesgos", description = "Endpoints de riesgos e incidentes")
public class RiesgoController {

    private final RiesgoService riesgoService;

    @GetMapping
    @Operation(summary = "Listar riesgos con filtros opcionales")
    public ResponseEntity<List<RiesgoResponse>> findAll(
            @RequestParam(required = false) Long tipo,
            @RequestParam(required = false) String nivel) {
        return ResponseEntity.ok(riesgoService.findAll(tipo, nivel));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un riesgo")
    public ResponseEntity<RiesgoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.findById(id));
    }
}
