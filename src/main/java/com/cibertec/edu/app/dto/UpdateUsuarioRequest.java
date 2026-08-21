package com.cibertec.edu.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}
