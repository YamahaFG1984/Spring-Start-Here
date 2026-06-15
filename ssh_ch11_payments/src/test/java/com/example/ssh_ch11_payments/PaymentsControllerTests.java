package com.example.ssh_ch11_payments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PaymentsControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("""
            GIVEN a valid payment request
            WHEN the /payment endpoint is called
            THEN the response should be OK and contain the payment details
            """)
    void createPaymentTest() throws Exception {
        String requestId = UUID.randomUUID().toString();

        mockMvc.perform(post("/payment")
                        .header("requestId", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.0}"))
                .andExpect(status().isOk())
                .andExpect(header().string("requestId", requestId))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("""
            GIVEN a payment request without requestId header
            WHEN the /payment endpoint is called
            THEN the response should be Bad Request
            """)
    void createPaymentMissingHeaderTest() throws Exception {
        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.0}"))
                .andExpect(status().isBadRequest());
    }
}
