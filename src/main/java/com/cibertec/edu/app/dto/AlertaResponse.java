package com.cibertec.edu.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipoRiesgo;
    private String nivel;
    private Double latitud;
    private Double longitud;
    private Boolean activa;
    private Long riesgoId;
}
