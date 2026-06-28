package com.transportista.guias;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void endpointCrearRequiereRolGestor() throws Exception {
        mockMvc.perform(post("/api/guias").with(jwt().jwt(jwt -> jwt.claim("roles", "DESCARGA_GUIA"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointDescargarRequiereRolDescarga() throws Exception {
        mockMvc.perform(get("/api/guias/1/descargar").with(jwt().jwt(jwt -> jwt.claim("roles", "GESTOR_GUIAS"))))
                .andExpect(status().isForbidden());
    }
}
