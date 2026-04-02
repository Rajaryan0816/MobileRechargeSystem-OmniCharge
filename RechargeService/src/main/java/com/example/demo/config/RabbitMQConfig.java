package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_RECHARGE_PAYMENT = "recharge.payment.queue";
    public static final String EXCHANGE_PAYMENT = "payment_exchange";
    public static final String ROUTING_KEY_PAYMENT_COMPLETED = "payment.completed";

    @Bean
    public Queue rechargePaymentQueue() {
        return new Queue(QUEUE_RECHARGE_PAYMENT, true);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(EXCHANGE_PAYMENT);
    }

    @Bean
    public Binding rechargePaymentBinding(Queue rechargePaymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(rechargePaymentQueue).to(paymentExchange).with(ROUTING_KEY_PAYMENT_COMPLETED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
