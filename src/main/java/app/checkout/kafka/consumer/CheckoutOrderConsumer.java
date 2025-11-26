package app.checkout.kafka.consumer;

import app.checkout.dto.CheckoutRequestDto;
import app.checkout.services.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CheckoutOrderConsumer {

    private static final Logger LOG = Logger.getLogger(CheckoutOrderConsumer.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    CheckoutService checkoutService;

    @Incoming("checkout-order")
    public void consume(String message) {
        try {
            LOG.infof("[Received message from Kafka]: %s", message);

            CheckoutRequestDto request = mapper.readValue(message, CheckoutRequestDto.class);
            checkoutService.processCheckout(request);

            LOG.infof("[Successfully processed checkout order for customer]: %s", request.getCustomerId());
        } catch (Exception e) {
            LOG.errorf("[Failed to process message]: %s - Error: %s", message, e.getMessage());
        }
    }
}