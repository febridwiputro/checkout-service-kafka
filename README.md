# Checkout Service

------------------------------------------------------------------------

## Overview

- RESTful API - Checkout, order management, product catalog
- Kafka Event Streaming - Asynchronous order processing
- Stock Management - Real-time inventory tracking with reservation
- Database Persistence - PostgreSQL with Flyway migrations
- Validation - Request validation with Jakarta Bean Validation
- Logging - Comprehensive logging for debugging

------------------------------------------------------------------------

## Architecture

### System Flow
┌─────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Client    │ ──HTTP──│ Checkout Service │ ──DB──► │  PostgreSQL  │
└─────────────┘         └──────────────────┘         └──────────────┘
                               │      ▲
                          Kafka│      │Kafka
                          Topic│      │Topic
                               ▼      │
                        ┌──────────────────────┐
                        │  Apache Kafka        │
                        │  - checkout.order    │
                        │    .processed        │
                        └──────────────────────┘
                               │
                               ▼
                        ┌──────────────────────┐
                        │ Notification Service │
                        └──────────────────────┘

### Checkout Process Flow

1. Client sends POST /api/checkout
2. Validate cart & products
3. Check stock availability
4. Reserve stock (available → reserved)
5. Create order (status: PENDING)
6. Process payment (dummy implementation)
7. Update order status (PENDING → CONFIRMED)
8. Confirm stock deduction (reserved → deducted)
9. Produce Kafka message → checkout.order.processed
10. Return order response to client
11. Consumer processes notification

------------------------------------------------------------------------

## Prerequisites

### Required Software

  Software     Version
  ------------ ---------
  Java         17+
  PostgreSQL   17.5
  Kafka        3.x
  Docker       Latest

### Environment Setup

``` bash
git clone https://github.com/your-repo/checkout-service-kafka.git
cd checkout-service-kafka
docker-compose up -d
mvn quarkus:dev
```

### Docker Compose

``` yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.6.0
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

...
```

------------------------------------------------------------------------

# API Endpoints

Base URL:

    http://localhost:8081

------------------------------------------------------------------------

## 1. Checkout API

POST /api/checkout
Process checkout and create order.

### Request Example

``` bash
curl -X 'POST' \
  'http://localhost:8081/api/checkout' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "customerId": "CUST-001",
  "customerEmail": "customer@example.com",
  "paymentMethod": "BANK_TRANSFER",
  "totalAmount": 350000,
  "shippingAddress": {
    "street": "Jl. Melati No. 10",
    "city": "Jakarta Selatan",
    "province": "DKI Jakarta",
    "postalCode": "12940",
    "phoneNumber": "081234567890"
  },
  "items": [
    {
      "productId": "60c65ff9-46a6-4b39-b481-52d8168867b7",
      "quantity": 2,
      "price": 75000
    },
    {
      "productId": "dd959d75-cfcf-4217-b6ce-cd2ca05836e8",
      "quantity": 1,
      "price": 200000
    }
  ]
}'
```

### Response Success

``` json
{
  "code": "201",
  "success": true,
  "message": "Checkout completed successfully",
  "data": {
    "id": "089483d3-6db2-4798-b840-e5c63c89760d",
    "orderNumber": "ORD-1764184386128",
    "customerId": "CUST-001",
    "customerEmail": "customer@example.com",
    "totalAmount": 350000,
    "paymentMethod": "BANK_TRANSFER",
    "paymentStatus": "PAID",
    "orderStatus": "CONFIRMED",
    "shippingAddress": "Jl. Melati No. 10, Jakarta Selatan, DKI Jakarta, 12940 - Phone: 081234567890",
    "createdAt": "2025-11-27T02:13:06.129342",
    "updatedAt": "2025-11-27T02:13:06.139404",
    "items": [
      {
        "id": "637680d9-dce7-4433-a452-1c4d16430131",
        "productId": "60c65ff9-46a6-4b39-b481-52d8168867b7",
        "productName": "Laptop Dell XPS 13",
        "quantity": 2,
        "price": 75000,
        "subtotal": 150000
      },
      {
        "id": "ee0bbe66-c437-47a8-9218-64457c66e8a0",
        "productId": "dd959d75-cfcf-4217-b6ce-cd2ca05836e8",
        "productName": "iPhone 15 Pro",
        "quantity": 1,
        "price": 200000,
        "subtotal": 200000
      }
    ],
    "processInstanceId": null
  },
  "timestamp": "2025-11-27T02:13:06.159561"
}
```

------------------------------------------------------------------------

## 2. Order Status API --- GET /api/checkout/order/{orderId}

Retrieve status order.

### request
``` bash
curl -X 'GET' \
  'http://localhost:8081/api/checkout/order/089483d3-6db2-4798-b840-e5c63c89760d' \
  -H 'accept: application/json'
```

### response
``` json
{
  "code": "200",
  "success": true,
  "message": "Order status retrieved",
  "data": {
    "id": "089483d3-6db2-4798-b840-e5c63c89760d",
    "orderNumber": "ORD-1764184386128",
    "customerId": "CUST-001",
    "customerEmail": "customer@example.com",
    "totalAmount": 350000,
    "paymentMethod": "BANK_TRANSFER",
    "paymentStatus": "PAID",
    "orderStatus": "CONFIRMED",
    "shippingAddress": "Jl. Melati No. 10, Jakarta Selatan, DKI Jakarta, 12940 - Phone: 081234567890",
    "createdAt": "2025-11-27T02:13:06.129342",
    "updatedAt": "2025-11-27T02:13:06.139404",
    "items": [
      {
        "id": "637680d9-dce7-4433-a452-1c4d16430131",
        "productId": "60c65ff9-46a6-4b39-b481-52d8168867b7",
        "productName": "Laptop Dell XPS 13",
        "quantity": 2,
        "price": 75000,
        "subtotal": 150000
      },
      {
        "id": "ee0bbe66-c437-47a8-9218-64457c66e8a0",
        "productId": "dd959d75-cfcf-4217-b6ce-cd2ca05836e8",
        "productName": "iPhone 15 Pro",
        "quantity": 1,
        "price": 200000,
        "subtotal": 200000
      }
    ],
    "processInstanceId": null
  },
  "timestamp": "2025-11-27T02:42:12.073089"
}
```

------------------------------------------------------------------------

## 3. Product APIs

-   GET /api/products\
### request
``` bash
curl -X 'GET' \
  'http://localhost:8081/api/products' \
  -H 'accept: application/json'
```
### response 
``` json
{
  "code": "200",
  "success": true,
  "message": "All products retrieved successfully",
  "data": [
    {
      "productId": "2b5f29b3-602e-4044-95a7-14b152a9aafa",
      "productName": "Samsung Galaxy S24",
      "description": "Auto-migrated from inventory",
      "price": 12000000,
      "availableStock": 75,
      "category": "Smartphones",
      "imageUrl": "https://via.placeholder.com/300x300/0066cc/ffffff?text=Samsung+Galaxy+S24",
      "isActive": true,
      "createdAt": "2025-11-26T14:12:59.781679",
      "updatedAt": "2025-11-26T14:12:59.781679"
    }
  ]
}
```

-   GET /api/products/{productId}\
### request
``` bash
curl -X 'GET' \
  'http://localhost:8081/api/products/2b5f29b3-602e-4044-95a7-14b152a9aafa' \
  -H 'accept: application/json'
```

### response
``` json
{
  "code": "200",
  "success": true,
  "message": "Product detail retrieved successfully",
  "data": {
    "productId": "2b5f29b3-602e-4044-95a7-14b152a9aafa",
    "productName": "Samsung Galaxy S24",
    "description": "Auto-migrated from inventory",
    "price": 12000000,
    "availableStock": 75,
    "category": "Smartphones",
    "imageUrl": "https://via.placeholder.com/300x300/0066cc/ffffff?text=Samsung+Galaxy+S24",
    "isActive": true,
    "createdAt": "2025-11-26T14:12:59.781679",
    "updatedAt": "2025-11-26T14:12:59.781679"
  },
  "timestamp": "2025-11-27T02:46:36.450963"
}
```

------------------------------------------------------------------------

# Kafka Integration

  --------------------------------------------------------------------------------------
  Topic                      Purpose           Producer            Consumer
  -------------------------- ----------------- ------------------- ---------------------
  checkout.order.created     Request eksternal External System     CheckoutConsumer

  checkout.order.processed   Order selesai     CheckoutService     NotificationService
  --------------------------------------------------------------------------------------

### Kafka Flow

    Checkout → Kafka Producer → Topic → Kafka Consumer → Send Notification

### Kafka Message
``` bash
{
  "orderId": "089483d3-6db2-4798-b840-e5c63c89760d",
  "status": "SUCCESS",
  "orderNumber": "ORD-1764184386128",
  "customerId": "CUST-001"
}
```
### Kafka Logs
#### Producer Log
``` bash
02:13:06 INFO [ap.ch.ka.pr.CheckoutOrderProducer] (executor-thread-1) 
Successfully produced message to Kafka: 
{"orderId":"089483d3-6db2-4798-b840-e5c63c89760d","status":"SUCCESS","orderNumber":"ORD-1764184386128","customerId":"CUST-001"}
```

#### Consumer Log
``` bash
02:13:06 INFO [ap.ch.ka.co.OrderProcessedConsumer] (vert.x-worker-thread-34) 
Received processed order from Kafka: 
{"orderId":"089483d3-6db2-4798-b840-e5c63c89760d","status":"SUCCESS","orderNumber":"ORD-1764184386128","customerId":"CUST-001"}

02:13:06 INFO [ap.ch.ka.co.OrderProcessedConsumer] (vert.x-worker-thread-34) 
Successfully processed order notification - OrderID: 089483d3-6db2-4798-b840-e5c63c89760d, Customer: CUST-001, Status: SUCCESS
```
------------------------------------------------------------------------
