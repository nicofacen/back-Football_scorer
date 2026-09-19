package com.uade.e_commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uade.e_commerce.security.AccessDeniedHandlerJson;
import com.uade.e_commerce.security.JwtAuthenticationEntryPoint;
import com.uade.e_commerce.security.JwtAuthenticationFilter;
import com.uade.e_commerce.security.JwtService;
import com.uade.e_commerce.security.UsuarioDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtService jwtService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AccessDeniedHandlerJson accessDeniedHandlerJson;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService, JwtService jwtService,
                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                           AccessDeniedHandlerJson accessDeniedHandlerJson) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtService = jwtService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.accessDeniedHandlerJson = accessDeniedHandlerJson;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // La usa AuthService (turno Johnny) para autenticar en el login con
    // UsernamePasswordAuthenticationToken en vez de comparar el password a mano.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(jwtService, usuarioDetailsService);

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/usuarios/registro", "/api/usuarios/login", "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/api/productos/**", "/api/clubes/**", "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/productos/**", "/api/clubes/**", "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,
                        "/api/productos/**", "/api/clubes/**", "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,
                        "/api/productos/**", "/api/clubes/**", "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers("/api/carritos/**", "/api/ordenes/**", "/api/usuarios/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandlerJson)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
