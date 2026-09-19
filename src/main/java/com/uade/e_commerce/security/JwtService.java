package com.uade.e_commerce.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey claveSecreta;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secreto,
                       @Value("${jwt.expiration-ms}") long expiracionMs) {
        this.claveSecreta = Keys.hmacShaKeyFor(secreto.getBytes());
        this.expiracionMs = expiracionMs;
    }

    // userDetails.getUsername() es el email: asi lo arma UsuarioDetailsService (turno Kiki).
    public String generarToken(UserDetails userDetails) {
        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(claveSecreta)
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean esValido(String token, UserDetails userDetails) {
        String email = extraerEmail(token);
        return email.equals(userDetails.getUsername()) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

}
