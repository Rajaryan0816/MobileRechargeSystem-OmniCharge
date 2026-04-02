package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.PaymentCompletedEvent;
import com.example.demo.dto.UserRegisteredEvent;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@Service
@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICATION)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @RabbitHandler
    @Transactional
    public void consumePaymentEvent(PaymentCompletedEvent event) {
        log.info("📬 Received Payment Event from RabbitMQ: {}", event);

        String targetEmail = event.getUserEmail();
        Long userId = event.getUserId();

        String subject = "OmniCharge - Payment " + event.getStatus();
        String messageBody = String.format("Hello!\n\nYour payment of $%.2f for Recharge ID %d was %s.\nTransaction ID: %s\n\nThank you for using OmniCharge!",
                event.getAmount(), event.getRechargeId(), event.getStatus(), event.getTransactionId());

        if (targetEmail != null && !targetEmail.isEmpty()) {
            sendEmail(targetEmail, subject, messageBody);
        } else if (userId != null) {
            log.info("📱 Sending SMS Notification to User ID [{}]: {}", userId, messageBody);
        } else {
            log.warn("⚠️ Received event with no UserId or UserEmail! Dropping notification.");
            return;
        }

        Notification notification = Notification.builder()
                .userId(userId)
                .userEmail(targetEmail)
                .message(messageBody)
                .build();

        notificationRepository.save(notification);
        log.info("✅ Notification successfully persisted (Payment).");
    }

    @RabbitHandler
    @Transactional
    public void consumeRegistrationEvent(UserRegisteredEvent event) {
        log.info("📬 Received Registration Event from RabbitMQ: {}", event);

        String targetEmail = event.getEmail();
        String userName = event.getUserName();

        String subject = "Welcome to OmniCharge!";
        String messageBody = String.format("Hello %s!\n\nWelcome to OmniCharge! Your account has been successfully created.\nRegistered Email: %s\n\nThank you for joining us!",
                userName, targetEmail);

        if (targetEmail != null && !targetEmail.isEmpty()) {
            sendEmail(targetEmail, subject, messageBody);
        } else {
            log.warn("⚠️ Received registration event with no email! Dropping notification.");
        }

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .userEmail(targetEmail)
                .message(messageBody)
                .build();

        notificationRepository.save(notification);
        log.info("✅ Notification successfully persisted (Registration).");
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            log.info("📧 Attempting to send Email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@omnicharge.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("✨ Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}. Error: {}", to, e.getMessage());
            log.info("📝 FALLBACK: Email content for {}: \nSubject: {}\nBody: {}", to, subject, body);
        }
    }
}
