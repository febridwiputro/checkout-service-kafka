package app.checkout.kafka.consumer;

import app.checkout.dto.OrderProcessedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderProcessedConsumer {

    private static final Logger LOG = Logger.getLogger(OrderProcessedConsumer.class);

    @Inject
    ObjectMapper mapper;

    @Incoming("checkout-order-processed-consumer")
    public void consume(String message) {
        try {
            LOG.infof("[Received processed order from Kafka]: %s", message);
            
            OrderProcessedDto order = mapper.readValue(message, OrderProcessedDto.class);
            LOG.infof("[Successfully processed order notification] - OrderID: %s, Customer: %s, Status: %s",
                     order.getOrderId(), order.getCustomerId(), order.getStatus());
        } catch (Exception e) {
            LOG.errorf("[Failed to process message]: %s - Error: %s", message, e.getMessage());
        }
    }
}