package app.checkout.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderProcessedDto {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("orderNumber")
    private String orderNumber;

    @JsonProperty("customerId")
    private String customerId;

    public OrderProcessedDto() {}

    public OrderProcessedDto(String orderId, String status, String orderNumber, String customerId) {
        this.orderId = orderId;
        this.status = status;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}