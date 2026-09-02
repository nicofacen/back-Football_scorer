package com.uade.e_commerce.dto;

import lombok.Data;

// Sin campo id: el item lo crea el service, no lo manda el cliente.
@Data
public class AgregarItemRequest {

    private Long productoId;
    private Integer cantidad;
}
