package com.uade.e_commerce.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.uade.e_commerce.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 401: no sabemos quien es. Sin header, header sin "Bearer ", firma invalida o token
// vencido, el JwtAuthenticationFilter deja pasar sin autenticar y esto es lo que
// responde cuando el endpoint exigia estar autenticado. El @RestControllerAdvice de
// Mati NO ve esto: pasa en la cadena de filtros, antes del DispatcherServlet.
//
// El JSON se arma a mano (sin ObjectMapper) para no depender de que version de
// Jackson termine wireada como bean: este proyecto corre con Jackson 3
// (tools.jackson.*) via spring-boot-starter-jackson, mientras que jjwt-jackson
// trae com.fasterxml.jackson.databind 2.x solo como dependencia runtime propia.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

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
        response.getWriter().write(JsonError.serializar(error));
    }

}
