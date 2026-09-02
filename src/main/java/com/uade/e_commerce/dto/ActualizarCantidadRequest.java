package com.uade.e_commerce.dto;

import lombok.Data;

// Solo cantidad: el producto y el item ya quedan fijados por la URL
// (/api/carritos/{usuarioId}/items/{itemId}), no hace falta repetirlos en el body.
@Data
public class ActualizarCantidadRequest {

    private Integer cantidad;
}
