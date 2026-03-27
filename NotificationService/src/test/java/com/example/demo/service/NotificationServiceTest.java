package com.example.demo.service;

import com.example.demo.dto.PaymentCompletedEvent;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private PaymentCompletedEvent successEvent;
    private PaymentCompletedEvent failedEvent;
    private PaymentCompletedEvent noEmailEvent;

    @BeforeEach
    void setUp() {
        successEvent = PaymentCompletedEvent.builder()
                .paymentId(1L)
                .userId(10L)
                .userEmail("user@example.com")
                .rechargeId(100L)
                .amount(500.0)
                .transactionId("TXN-001")
                .status("SUCCESS")
                .message("Payment successful")
                .build();

        failedEvent = PaymentCompletedEvent.builder()
                .paymentId(2L)
                .userId(20L)
                .userEmail("other@example.com")
                .rechargeId(200L)
                .amount(200.0)
                .transactionId("TXN-002")
                .status("FAILED")
                .message("Payment failed")
                .build();

        // Event with no email but with a userId (e.g., SMS-only user)
        noEmailEvent = PaymentCompletedEvent.builder()
                .paymentId(3L)
                .userId(30L)
                .userEmail(null)
                .rechargeId(300L)
                .amount(100.0)
                .transactionId("TXN-003")
                .status("SUCCESS")
                .message("Recharge successful")
                .build();
    }

    // ──────────────────────────────────────────────
    // consumePaymentEvent — SUCCESS with email
    // ──────────────────────────────────────────────

    @Test
    void testConsumePaymentEvent_SuccessWithEmail_SendsEmailAndSaves() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.consumePaymentEvent(successEvent);

        // Email should be sent once
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        // Notification persisted to DB
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ──────────────────────────────────────────────
    // consumePaymentEvent — FAILED status
    // ──────────────────────────────────────────────

    @Test
    void testConsumePaymentEvent_FailedStatus_StillSendsEmailAndSaves() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.consumePaymentEvent(failedEvent);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ──────────────────────────────────────────────
    // consumePaymentEvent — no email (SMS fallback)
    // ──────────────────────────────────────────────

    @Test
    void testConsumePaymentEvent_NoEmail_SkipsEmailSendsToSMS() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.consumePaymentEvent(noEmailEvent);

        // Mail should NOT be called when no email is present
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        // But notification still saved
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ──────────────────────────────────────────────
    // consumePaymentEvent — email send fails, fallback logs, still saves to DB
    // ──────────────────────────────────────────────

    @Test
    void testConsumePaymentEvent_EmailFails_FallbackAndStillSaves() {
        doThrow(new RuntimeException("SMTP connection failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        // Should NOT throw — the service catches email exceptions internally
        notificationService.consumePaymentEvent(successEvent);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        // Notification still persisted even when email fails
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ──────────────────────────────────────────────
    // consumePaymentEvent — empty email string treated as no email
    // ──────────────────────────────────────────────

    @Test
    void testConsumePaymentEvent_EmptyEmail_SkipsEmail() {
        successEvent.setUserEmail("");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.consumePaymentEvent(successEvent);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
