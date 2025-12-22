//package org.nextme.userGoal_service.userGoal.infrastructure.client;
//
//import org.nextme.userGoal_service.userGoal.infrastructure.openfeign.FeignConfig;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@FeignClient(name = "account-server",configuration = FeignConfig.class, url = "http://34.50.7.8:30000")
//public interface feignClient {
//
//    @GetMapping("/v1/account/financial-products/report")
//    List<Map<String, Object>> getFinancialProducts();
//
//    @GetMapping("/v1/account/tran/tranList/{userId}")
//    List<Map<String, Object>> getTranList(@PathVariable("userId") UUID userId);
//
//}
