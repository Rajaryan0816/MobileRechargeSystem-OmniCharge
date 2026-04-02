package com.example.demo.client;

import com.example.demo.dto.RechargeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RechargeService")
public interface RechargeClient {

    @GetMapping("/api/recharges/id/{id}")
    RechargeDto getRechargeById(@PathVariable("id") Long id);
}
