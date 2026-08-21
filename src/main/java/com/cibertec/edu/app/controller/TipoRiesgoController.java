package com.cibertec.edu.app.controller;

import com.cibertec.edu.app.dto.TipoRiesgoResponse;
import com.cibertec.edu.app.service.TipoRiesgoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-riesgo")
@RequiredArgsConstructor
@Tag(name = "Tipos de Riesgo", description = "Endpoints de tipos de riesgo")
public class TipoRiesgoController {

    private final TipoRiesgoService tipoRiesgoService;

    @GetMapping
    @Operation(summary = "Listar todos los tipos de riesgo")
    public ResponseEntity<List<TipoRiesgoResponse>> findAll() {
        return ResponseEntity.ok(tipoRiesgoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tipo de riesgo por ID")
    public ResponseEntity<TipoRiesgoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoRiesgoService.findById(id));
    }
}
