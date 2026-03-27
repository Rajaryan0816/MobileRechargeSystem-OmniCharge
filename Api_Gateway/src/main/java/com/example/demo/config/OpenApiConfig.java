package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class OpenApiConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRouterFunction() {
        return route("user-service")
                .route(path("/user-service/**"), http())
                .filter(rewritePath("/user-service/(?<remaining>.*)", "/${remaining}"))
                .filter(lb("USERSERVICE"))
                .filter(circuitBreaker("userServiceCB", URI.create("forward:/fallback/user-service")))
                .build()
                .and(route("operator-service")
                        .route(path("/operator-service/**"), http())
                        .filter(rewritePath("/operator-service/(?<remaining>.*)", "/${remaining}"))
                        .filter(lb("OPERATORSERVICE"))
                        .filter(circuitBreaker("operatorServiceCB", URI.create("forward:/fallback/operator-service")))
                        .build())
                .and(route("recharge-service")
                        .route(path("/recharge-service/**"), http())
                        .filter(rewritePath("/recharge-service/(?<remaining>.*)", "/${remaining}"))
                        .filter(lb("RECHARGESERVICE"))
                        .filter(circuitBreaker("rechargeServiceCB", URI.create("forward:/fallback/recharge-service")))
                        .build())
                .and(route("payment-service")
                        .route(path("/payment-service/**"), http())
                        .filter(rewritePath("/payment-service/(?<remaining>.*)", "/${remaining}"))
                        .filter(lb("PAYMENTSERVICE"))
                        .filter(circuitBreaker("paymentServiceCB", URI.create("forward:/fallback/payment-service")))
                        .build())
                .and(route("notification-service")
                        .route(path("/notification-service/**"), http())
                        .filter(rewritePath("/notification-service/(?<remaining>.*)", "/${remaining}"))
                        .filter(lb("NOTIFICATIONSERVICE"))
                        .filter(circuitBreaker("notificationServiceCB", URI.create("forward:/fallback/notification-service")))
                        .build());
    }


    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OmniCharge - API Gateway")
                        .description("Centralized API Gateway for OmniCharge Microservices Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OmniCharge Team")
                                .email("support@support-omnicharge.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                .components(new Components()
                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
