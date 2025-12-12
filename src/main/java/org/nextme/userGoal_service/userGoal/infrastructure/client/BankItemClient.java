package org.nextme.userGoal_service.userGoal.infrastructure.client;

import org.nextme.userGoal_service.userGoal.infrastructure.openfeign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "account-server",configuration = FeignConfig.class, url = "http://localhost:8080")
public interface BankItemClient {

    @GetMapping("/v1/financial-products/report")
    List<Map<String, Object>> getFinancialProducts();
}
