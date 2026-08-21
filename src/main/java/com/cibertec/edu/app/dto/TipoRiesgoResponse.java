package com.cibertec.edu.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoRiesgoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Boolean activo;
}
