package com.example.ssh_ch11_ex2.proxies;

import com.example.ssh_ch11_ex2.model.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payments", url = "${payments.url:http://localhost:8080}")
public interface PaymentsProxy {

  @PostMapping("/payment")
  Payment createPayment(
      @RequestHeader String requestId,
      @RequestBody Payment payment);
}
