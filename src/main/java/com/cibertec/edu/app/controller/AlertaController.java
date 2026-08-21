package com.cibertec.edu.app.controller;

import com.cibertec.edu.app.dto.AlertaResponse;
import com.cibertec.edu.app.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Endpoints de alertas")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    @Operation(summary = "Listar alertas activas")
    public ResponseEntity<List<AlertaResponse>> findAll() {
        return ResponseEntity.ok(alertaService.findAllActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una alerta")
    public ResponseEntity<AlertaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.findById(id));
    }
}
