package com.transportista.guias.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    public static final String ROLE_DESCARGA_GUIA = "DESCARGA_GUIA";
    public static final String ROLE_GESTOR_GUIAS = "GESTOR_GUIAS";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/guias/*/descargar").hasRole(ROLE_DESCARGA_GUIA)
                        .requestMatchers("/api/guias/**").hasRole(ROLE_GESTOR_GUIAS)
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new AzureB2CRoleConverter());
        return converter;
    }

    static class AzureB2CRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private static final List<String> ROLE_CLAIMS = List.of("roles", "extension_Roles", "extension_roles");

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Set<String> roles = new HashSet<>();
            for (String claim : ROLE_CLAIMS) {
                Object value = jwt.getClaims().get(claim);
                roles.addAll(extractRoles(value));
            }

            Object scopes = jwt.getClaims().get("scp");
            if (scopes instanceof String scopeText) {
                roles.addAll(List.of(scopeText.split(" ")));
            }

            return roles.stream()
                    .filter(role -> role != null && !role.isBlank())
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        private Set<String> extractRoles(Object value) {
            if (value instanceof Collection<?> collection) {
                return collection.stream()
                        .map(String::valueOf)
                        .collect(Collectors.toSet());
            }
            if (value instanceof String roleText) {
                return Set.of(roleText.split("[, ]+"));
            }
            if (value instanceof Map<?, ?> map) {
                return map.values().stream()
                        .map(String::valueOf)
                        .collect(Collectors.toSet());
            }
            return Set.of();
        }
    }
}
