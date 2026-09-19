package com.uade.e_commerce.security;

import com.uade.e_commerce.dto.ErrorResponse;

// Serializa ErrorResponse a mano para los handlers de seguridad (401/403), que
// escriben directo en el HttpServletResponse fuera del pipeline de Spring MVC y no
// pueden apoyarse en un ObjectMapper inyectado: ver el comentario en
// JwtAuthenticationEntryPoint.
final class JsonError {

    private JsonError() {
    }

    static String serializar(ErrorResponse error) {
        return "{"
                + "\"timestamp\":\"" + error.getTimestamp() + "\","
                + "\"status\":" + error.getStatus() + ","
                + "\"error\":\"" + escapar(error.getError()) + "\","
                + "\"mensaje\":\"" + escapar(error.getMensaje()) + "\","
                + "\"path\":\"" + escapar(error.getPath()) + "\""
                + "}";
    }

    private static String escapar(String valor) {
        return valor == null ? "" : valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
