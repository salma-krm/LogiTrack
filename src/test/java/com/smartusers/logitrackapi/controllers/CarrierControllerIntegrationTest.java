package com.smartusers.logitrackapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CarrierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String registerAndLoginAs(String email, String password, String role) throws Exception {
        // Register
        String registerJson = "{" +
                "\"firstName\":\"Carrier\"," +
                "\"lastName\":\"Admin\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"role\":\"" + role + "\"}";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());
        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);
        String loginJson = objectMapper.writeValueAsString(login);
        String resp = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }

    @Test
    void createCarrier_withAdminToken_shouldSucceed() throws Exception {
        String token = registerAndLoginAs("carrier.admin@example.com", "Password123", "ADMIN");
        CarrierRequest request = new CarrierRequest();
        request.setName("CarrierInt");
        request.setCode("CINT");
        mockMvc.perform(post("/api/carriers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CarrierInt"));
    }

    @Test
    void createCarrier_withoutToken_shouldReturn401() throws Exception {
        CarrierRequest request = new CarrierRequest();
        request.setName("CarrierNoAuth");
        request.setCode("CNA");
        mockMvc.perform(post("/api/carriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCarrier_withClientToken_shouldReturn403() throws Exception {
        String token = registerAndLoginAs("carrier.client@example.com", "Password123", "CLIENT");
        CarrierRequest request = new CarrierRequest();
        request.setName("CarrierClient");
        request.setCode("CCL");
        mockMvc.perform(post("/api/carriers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}

