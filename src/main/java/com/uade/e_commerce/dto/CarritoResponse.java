package com.uade.e_commerce.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {

    private Long carritoId;
    private Long usuarioId;
    private List<ItemCarritoResponse> items;
    private Double total;
}
