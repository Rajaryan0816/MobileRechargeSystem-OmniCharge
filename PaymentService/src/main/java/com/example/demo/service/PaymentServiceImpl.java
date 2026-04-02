package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.PaymentCompletedEvent;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.entity.Payment;
import com.example.demo.entity.Payment.PaymentStatus;
import com.example.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final com.example.demo.client.RechargeClient rechargeClient;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Initiating payment for recharge {}", request.getRechargeId());

        // Fetch recharge details from RechargeService
        com.example.demo.dto.RechargeDto rechargeDto = null;
        try {
            rechargeDto = rechargeClient.getRechargeById(request.getRechargeId());
        } catch (Exception e) {
            log.error("Failed to fetch recharge details: {}", e.getMessage());
            throw new RuntimeException("Invalid Recharge ID or RechargeService unavailable");
        }

        if (rechargeDto == null) {
            throw new RuntimeException("Recharge details not found for ID: " + request.getRechargeId());
        }

        // Override or supply missing fields
        Long userId = request.getUserId() != null ? request.getUserId() : rechargeDto.getUserId();
        Double amount = request.getAmount() != null ? request.getAmount() : rechargeDto.getAmount();

        log.info("Processing payment for user {} and recharge {} with amount {}", userId, request.getRechargeId(), amount);

        // 1. Create unique transaction ID
        String transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Initial Payment Record saving as PENDING
        Payment payment = Payment.builder()
                .userId(userId)
                .userEmail(request.getUserEmail())
                .rechargeId(request.getRechargeId())
                .amount(amount)
                .transactionId(transactionId)
                .status(PaymentStatus.PENDING)
                .build();
        
        payment = paymentRepository.save(payment);

        // 3. Simulate Payment Processing
        try {
            // Simulate a bank decline if the user tries to pay $0.00 or negative
            if (amount != null && amount <= 0.0) {
                throw new RuntimeException("Bank Decline: Invalid amount.");
            }
            
            payment.setStatus(PaymentStatus.SUCCESS);
            payment = paymentRepository.save(payment);

            // 4. Publish Event to RabbitMQ
            publishPaymentEvent(payment);

            log.info("Payment {} completed successfully.", transactionId);
            
            return PaymentResponse.builder()
                    .transactionId(transactionId)
                    .status(payment.getStatus())
                    .message("Payment processed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Payment {} failed: {}", transactionId, e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);
            
            // Publish the FAILED event to RabbitMQ so the user gets a "Payment Failed" email!
            publishPaymentEvent(payment);
            
            return PaymentResponse.builder()
                    .transactionId(transactionId)
                    .status(payment.getStatus())
                    .message("Payment failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        log.info("Fetching payments for userId: {}", userId);
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> getPayments(){
    	log.info("Fetching payments ");
    	return paymentRepository.findAll();
    }
    private void publishPaymentEvent(Payment payment) {
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .userEmail(payment.getUserEmail())
                .rechargeId(payment.getRechargeId())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().name())
                .message("Your payment of " + payment.getAmount() + " was successful. Transaction ID: " + payment.getTransactionId())
                .build();
        
        log.info("Publishing PaymentCompletedEvent to RabbitMQ for transaction {}", payment.getTransactionId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PAYMENT, RabbitMQConfig.ROUTING_KEY_PAYMENT_COMPLETED, event);
    }
}
