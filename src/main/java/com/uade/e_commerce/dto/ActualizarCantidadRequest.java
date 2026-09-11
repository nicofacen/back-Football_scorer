package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// Solo cantidad: el producto y el item ya quedan fijados por la URL
// (/api/carritos/{usuarioId}/items/{itemId}), no hace falta repetirlos en el body.
@Data
public class ActualizarCantidadRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}
