package app.checkout.kafka.producer;

import app.checkout.dto.OrderProcessedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CheckoutOrderProducer {

    private static final Logger LOG = Logger.getLogger(CheckoutOrderProducer.class);

    @Inject
    ObjectMapper objectMapper;

    @Channel("checkout-order-processed")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 100000)
    Emitter<String> emitter;

    public void send(String orderId, String status, String orderNumber, String customerId) {
        try {
            OrderProcessedDto dto = new OrderProcessedDto(orderId, status, orderNumber, customerId);
            String json = objectMapper.writeValueAsString(dto);
            
            emitter.send(json);
            LOG.infof("[Successfully produced message to Kafka]: %s", json);
        } catch (Exception e) {
            LOG.errorf("[Failed to produce message to Kafka] - Error: %s", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void send(String json) {
        try {
            emitter.send(json);
            LOG.infof("[Successfully produced message to Kafka]: %s", json);
        } catch (Exception e) {
            LOG.errorf("[Failed to produce message to Kafka]: %s - Error: %s", json, e.getMessage());
            throw e;
        }
    }
}