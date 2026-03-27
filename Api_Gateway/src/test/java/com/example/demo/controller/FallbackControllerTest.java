package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FallbackController.class)
public class FallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUserServiceFallback() throws Exception {
        mockMvc.perform(get("/fallback/user-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User Service is currently unavailable. Please try again later."));
    }

    @Test
    void testOperatorServiceFallback() throws Exception {
        mockMvc.perform(get("/fallback/operator-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Operator Service is currently unavailable. Please try again later."));
    }

    @Test
    void testRechargeServiceFallback() throws Exception {
        mockMvc.perform(get("/fallback/recharge-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Recharge Service is currently unavailable. Please try again later."));
    }

    @Test
    void testPaymentServiceFallback() throws Exception {
        mockMvc.perform(get("/fallback/payment-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Payment Service is currently unavailable. Please try again later."));
    }

    @Test
    void testNotificationServiceFallback() throws Exception {
        mockMvc.perform(get("/fallback/notification-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Notification Service is currently unavailable. Please try again later."));
    }
}
