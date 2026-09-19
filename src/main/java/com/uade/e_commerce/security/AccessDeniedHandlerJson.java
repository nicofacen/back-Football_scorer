package com.uade.e_commerce.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.uade.e_commerce.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 403: sabemos quien es (token valido), pero el rol no alcanza. Ej: un CLIENTE
// pegandole a POST /api/productos. Distinto del 401 de JwtAuthenticationEntryPoint.
// JSON armado a mano por el mismo motivo que JwtAuthenticationEntryPoint (ver ahi).
@Component
public class AccessDeniedHandlerJson implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "No tenes permisos para acceder a este recurso",
                request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JsonError.serializar(error));
    }

}
