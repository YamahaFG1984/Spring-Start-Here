package com.example.ssh_ch11_ex1.proxies;

import com.example.ssh_ch11_ex1.model.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentsProxy {

  private final RestClient restClient;

  public PaymentsProxy(@Value("${payments.url:http://localhost:8080}") String paymentsUrl) {
    this.restClient = RestClient.create(paymentsUrl);
  }

  public Payment createPayment(String requestId, Payment payment) {
    return restClient.post()
        .uri("/payment")
        .header("requestId", requestId)
        .body(payment)
        .retrieve()
        .body(Payment.class);
  }
}
