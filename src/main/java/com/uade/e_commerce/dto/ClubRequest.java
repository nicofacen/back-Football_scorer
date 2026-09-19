package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClubRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String pais;
    private String escudoUrl;
}
