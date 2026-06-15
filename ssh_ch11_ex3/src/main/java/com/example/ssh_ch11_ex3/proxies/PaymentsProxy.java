package com.example.ssh_ch11_ex3.proxies;

import com.example.ssh_ch11_ex3.model.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentsProxy {

  private final RestTemplate rest;

  @Value("${payments.url:http://localhost:8080}")
  private String paymentsUrl;

  public PaymentsProxy() {
    this.rest = new RestTemplate();
  }

  public Payment createPayment(String requestId, Payment payment) {
    String url = paymentsUrl + "/payment";

    HttpHeaders headers = new HttpHeaders();
    headers.add("requestId", requestId);

    HttpEntity<Payment> httpEntity = new HttpEntity<>(payment, headers);

    return rest.postForObject(url, httpEntity, Payment.class);
  }
}
