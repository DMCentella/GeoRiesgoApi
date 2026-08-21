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
public class RiesgoResponse {
    private Long id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private String nivel;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;
}
