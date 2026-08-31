package com.uade.e_commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubResponse {

    private Long id;
    private String nombre;
    private String pais;
    private String escudoUrl;
}
