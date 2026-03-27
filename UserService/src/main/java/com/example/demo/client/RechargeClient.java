package com.example.demo.client;

import com.example.demo.dto.RechargeHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "RechargeService")
public interface RechargeClient {

    @GetMapping("/api/recharges/user/{userId}")
    List<RechargeHistoryDto> getRechargesByUserId(@PathVariable("userId") Long userId);
}
