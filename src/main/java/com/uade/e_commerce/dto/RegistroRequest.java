package com.uade.e_commerce.dto;

import lombok.Data;

@Data
public class RegistroRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
}
