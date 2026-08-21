package com.cibertec.edu.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {
    private Long id;
    private UsuarioDto usuario;
    private String tipoRiesgo;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private String fotoUrl;
    private LocalDateTime fechaRegistro;
    private String estado;
}
