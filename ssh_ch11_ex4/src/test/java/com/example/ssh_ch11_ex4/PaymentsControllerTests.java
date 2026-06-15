package com.example.ssh_ch11_ex4;

import com.example.ssh_ch11_ex4.model.Payment;
import com.example.ssh_ch11_ex4.proxies.PaymentsProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PaymentsControllerTests {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @MockitoBean
  private PaymentsProxy paymentsProxy;

  @Test
  @DisplayName(
      """
      GIVEN a payment request
      WHEN the /payment endpoint is called
      THEN the payment is created and returned
      """
  )
  void createPaymentTest() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    Payment payment = new Payment();
    payment.setAmount(100.0);

    when(paymentsProxy.createPayment(anyString(), any(Payment.class)))
        .thenReturn(payment);

    mockMvc.perform(post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"amount\": 100.0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(100.0));
  }
}
