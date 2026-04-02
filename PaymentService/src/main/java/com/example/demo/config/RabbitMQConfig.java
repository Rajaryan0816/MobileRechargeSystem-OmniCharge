package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NOTIFICATION = "notification_queue";
    public static final String EXCHANGE_PAYMENT = "payment_exchange";
    public static final String ROUTING_KEY_PAYMENT_COMPLETED = "payment.completed";

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NOTIFICATION, true);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(EXCHANGE_PAYMENT);
    }

    @Bean
    public Binding bindingNotification(Queue notificationQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(notificationQueue).to(paymentExchange).with(ROUTING_KEY_PAYMENT_COMPLETED);
    }

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
