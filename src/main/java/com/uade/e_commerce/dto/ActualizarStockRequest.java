package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

// Valor absoluto, no delta: { "stock": 50 } deja el stock en 50.
// Mandarlo dos veces da el mismo resultado (idempotente).
@Data
public class ActualizarStockRequest {

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
}
