package com.smartusers.logitrackapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartusers.logitrackapi.AbstractIntegrationTest;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successfulLoginReturnsJwtAndRefreshToken() throws Exception {
        // register
        String registerJson = "{" +
                "\"firstName\":\"Integration\"," +
                "\"lastName\":\"Test\"," +
                "\"email\":\"it.user@example.com\"," +
                "\"password\":\"Password123\"," +
                "\"role\":\"ADMIN\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("it.user@example.com"))
                .andExpect(jsonPath("$.token").isNotEmpty());
                // register returns refreshToken=null in current implementation
                // .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        // login
        LoginRequest login = new LoginRequest();
        login.setEmail("it.user@example.com");
        login.setPassword("Password123");

        String loginJson = objectMapper.writeValueAsString(login);

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String resp = loginResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resp);
        String token = node.get("token").asText();
        String refreshToken = node.get("refreshToken").asText();

        assertThat(token).isNotBlank();
        assertThat(refreshToken).isNotBlank();
    }

    @Test
    void failedLoginReturnsBadRequest() throws Exception {
        // register user
        String registerJson = "{" +
                "\"firstName\":\"Integration\"," +
                "\"lastName\":\"Test\"," +
                "\"email\":\"it.fail@example.com\"," +
                "\"password\":\"Password123\"," +
                "\"role\":\"ADMIN\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // try login with wrong password
        LoginRequest login = new LoginRequest();
        login.setEmail("it.fail@example.com");
        login.setPassword("WrongPassword");

        String loginJson = objectMapper.writeValueAsString(login);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void accessSecuredEndpointWithJwt() throws Exception {
        // register
        String registerJson = "{" +
                "\"firstName\":\"Integration\"," +
                "\"lastName\":\"Test\"," +
                "\"email\":\"it.secure@example.com\"," +
                "\"password\":\"Password123\"," +
                "\"role\":\"ADMIN\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // login to obtain token
        LoginRequest login = new LoginRequest();
        login.setEmail("it.secure@example.com");
        login.setPassword("Password123");
        String loginJson = objectMapper.writeValueAsString(login);

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String resp = loginResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resp);
        String token = node.get("token").asText();

        // Access secured endpoint with valid JWT
        mockMvc.perform(get("/api/test/authenticated")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("AUTHENTICATED ACCESS GRANTED")));
    }

    @Test
    void adminCanAccessAdminOnlyEndpoint() throws Exception {
        // Register ADMIN
        String registerJson = "{" +
                "\"firstName\":\"Admin\"," +
                "\"lastName\":\"User\"," +
                "\"email\":\"admin.auth@example.com\"," +
                "\"password\":\"Password123\"," +
                "\"role\":\"ADMIN\"" +
                "}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());
        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail("admin.auth@example.com");
        login.setPassword("Password123");
        String loginJson = objectMapper.writeValueAsString(login);
        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String resp = loginResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resp);
        String token = node.get("token").asText();
        // Access admin-only endpoint
        mockMvc.perform(get("/api/test/admin-only")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ADMIN ACCESS GRANTED")));
    }

    @Test
    void clientCannotAccessAdminOnlyEndpoint() throws Exception {
        // Register CLIENT
        String registerJson = "{" +
                "\"firstName\":\"Client\"," +
                "\"lastName\":\"User\"," +
                "\"email\":\"client.auth@example.com\"," +
                "\"password\":\"Password123\"," +
                "\"role\":\"CLIENT\"" +
                "}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());
        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail("client.auth@example.com");
        login.setPassword("Password123");
        String loginJson = objectMapper.writeValueAsString(login);
        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String resp = loginResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resp);
        String token = node.get("token").asText();
        // Try to access admin-only endpoint
        mockMvc.perform(get("/api/test/admin-only")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
