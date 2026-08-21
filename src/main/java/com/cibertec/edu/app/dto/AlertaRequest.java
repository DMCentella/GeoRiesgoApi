package com.cibertec.edu.app.dto;

import com.cibertec.edu.app.enums.NivelRiesgo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertaRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El tipo de riesgo es obligatorio")
    private Long tipoRiesgoId;

    @NotNull(message = "El nivel es obligatorio")
    private NivelRiesgo nivel;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    private Long riesgoId;
}
