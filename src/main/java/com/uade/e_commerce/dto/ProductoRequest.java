package com.uade.e_commerce.dto;

import lombok.Data;

@Data
public class ProductoRequest {

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String imagenUrl;
}
