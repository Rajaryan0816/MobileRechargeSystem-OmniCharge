package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.PaymentCompletedEvent;
import com.example.demo.enums.RechargeStatus;
import com.example.demo.model.Recharge;
import com.example.demo.repository.RechargeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final RechargeRepository rechargeRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RECHARGE_PAYMENT)
    @Transactional
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for Recharge ID {}", event.getRechargeId());

        try {
            Recharge recharge = rechargeRepository.findById(event.getRechargeId()).orElse(null);
            if (recharge == null) {
                log.warn("Recharge record not found for Recharge ID {}", event.getRechargeId());
                return;
            }

            if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
                recharge.setStatus(RechargeStatus.SUCCESS);
                recharge.setTransactionId(event.getTransactionId());
                log.info("Updated Recharge {} status to SUCCESS via RabbitMQ event.", recharge.getId());
            } else if ("FAILED".equalsIgnoreCase(event.getStatus())) {
                recharge.setStatus(RechargeStatus.FAILED);
                log.warn("Payment failed for Recharge {}. Reason: {}", recharge.getId(), event.getMessage());
            }

            rechargeRepository.save(recharge);
        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent for Recharge ID {}", event.getRechargeId(), e);
        }
    }
}
