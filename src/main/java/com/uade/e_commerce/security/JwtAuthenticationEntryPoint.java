package com.uade.e_commerce.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.e_commerce.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 401: no sabemos quien es. Sin header, header sin "Bearer ", firma invalida o token
// vencido, el JwtAuthenticationFilter deja pasar sin autenticar y esto es lo que
// responde cuando el endpoint exigia estar autenticado. El @RestControllerAdvice de
// Mati NO ve esto: pasa en la cadena de filtros, antes del DispatcherServlet.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "No autenticado: token ausente, invalido o vencido",
                request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }

}
