package com.example.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NOTIFICATION = "notification_queue";

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NOTIFICATION, true); // true = durable
    }

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
