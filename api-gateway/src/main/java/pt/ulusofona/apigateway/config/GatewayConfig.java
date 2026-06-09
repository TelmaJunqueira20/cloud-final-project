package pt.ulusofona.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${USER_SERVICE_HOST:localhost}")
    private String userServiceHost;

    @Value("${PRODUCT_SERVICE_HOST:localhost}")
    private String productServiceHost;

    @Value("${ORDER_SERVICE_HOST:localhost}")
    private String orderServiceHost;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://" + userServiceHost + ":8081"))
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://" + productServiceHost + ":8082"))
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://" + orderServiceHost + ":8083"))
                .build();
    }
}