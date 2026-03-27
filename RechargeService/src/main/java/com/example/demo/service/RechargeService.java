package com.example.demo.service;

import com.example.demo.dto.RechargeRequestDto;
import com.example.demo.model.Recharge;

import java.util.List;

public interface RechargeService {
    Recharge processRecharge(RechargeRequestDto requestDto);
    List<Recharge> getRechargesByUserId(Long userId);
    Recharge getRechargeById(Long id);
}
