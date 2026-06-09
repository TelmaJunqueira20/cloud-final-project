package pt.ulusofona.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.productservice.event.OrderCreatedEvent;
import pt.ulusofona.productservice.event.OrderItemEvent;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;

/**
 * SQS event consumer for order-related events.
 * Consumes OrderCreatedEvent from AWS SQS and updates product inventory.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRepository productRepository;

    @SqsListener("${cloud.aws.sqs.queue-name:cloud-final-project-orders}")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent via SQS for order ID: {}", event.getOrderId());

        try {
            for (OrderItemEvent item : event.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException(
                                "Product not found with ID: " + item.getProductId()));

                int newStock = product.getStockQuantity() - item.getQuantity();
                if (newStock < 0) {
                    log.warn("Insufficient stock for product {} (Order ID: {}). Current: {}, Requested: {}",
                            product.getName(), event.getOrderId(),
                            product.getStockQuantity(), item.getQuantity());
                    continue;
                }

                product.setStockQuantity(newStock);
                productRepository.save(product);
                log.info("Updated stock for product {}: {} -> {} (Order ID: {})",
                        product.getName(),
                        product.getStockQuantity() + item.getQuantity(),
                        newStock, event.getOrderId());
            }
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order ID: {}",
                    event.getOrderId(), e);
        }
    }
}