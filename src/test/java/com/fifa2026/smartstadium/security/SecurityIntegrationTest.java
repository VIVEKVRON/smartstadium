package com.fifa2026.smartstadium.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.supabase.jwt-secret:default-secret-key-for-testing-purposes-only-with-long-length}")
    private String jwtSecret;

    private String generateToken(String role) {
        return Jwts.builder()
                .subject("test-user-id")
                .claim("email", "test@example.com")
                .claim("user_role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    @DisplayName("Public endpoints accessible without auth")
    void publicEndpoints_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/stadiums"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/assistant/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stadiumId\":\"00000000-0000-0000-0000-000000000000\", \"query\":\"hello\", \"language\":\"en\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Staff-only endpoints return 401/403 without auth")
    void staffEndpoints_Return403WithoutAuth() throws Exception {
        // Without auth, Spring Security might return 401 or 403 depending on config.
        mockMvc.perform(post("/api/v1/crowd/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden()); // In standard spring boot it's 403 when unauthenticated but path is secured
    }

    @Test
    @DisplayName("Staff endpoints accessible with valid STAFF JWT")
    void staffEndpoints_AccessibleWithStaffJwt() throws Exception {
        String staffToken = generateToken("STAFF");

        // When it is authorized, it might fail validation or give 400 Bad Request because of empty body or bad JSON format.
        // We expect it NOT to be 401 or 403.
        mockMvc.perform(post("/api/v1/crowd/ingest")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stadiumId\":\"00000000-0000-0000-0000-000000000000\", \"zoneId\":\"00000000-0000-0000-0000-000000000000\", \"peopleCount\":50, \"cameraSource\":\"cam1\"}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected status not to be 401 or 403, but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("Staff endpoints blocked with FAN JWT")
    void staffEndpoints_BlockedWithFanJwt() throws Exception {
        String fanToken = generateToken("FAN");

        mockMvc.perform(post("/api/v1/crowd/ingest")
                        .header("Authorization", "Bearer " + fanToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
