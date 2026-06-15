package com.example.ssh_ch11_ex4.proxies;

import com.example.ssh_ch11_ex4.model.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentsProxy {

  private final WebClient webClient;

  public PaymentsProxy(@Value("${payments.url:http://localhost:8080}") String paymentsUrl) {
    this.webClient = WebClient.create(paymentsUrl);
  }

  public Payment createPayment(String requestId, Payment payment) {
    return webClient.post()
        .uri("/payment")
        .header("requestId", requestId)
        .bodyValue(payment)
        .retrieve()
        .bodyToMono(Payment.class)
        .block();
  }
}
