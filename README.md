# 🏨 StayEase — Hotel Booking Microservices Platform

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-7.4-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Zipkin](https://img.shields.io/badge/Zipkin-Tracing-FE7139?style=for-the-badge&logo=openzipkin&logoColor=white)

> **StayEase** is a production-inspired, cloud-native hotel booking platform built using a **microservices architecture**. It demonstrates real-world patterns including JWT-based security, event-driven payments via Kafka, distributed locking with Redisson, and distributed tracing with Zipkin — all orchestrated through Spring Cloud.

---

## 📐 Architecture Overview

```
                        ┌─────────────────────────────────────────┐
                        │           CLIENT (REST / Frontend)       │
                        └────────────────┬────────────────────────┘
                                         │
                        ┌────────────────▼────────────────────────┐
                        │        API Gateway (Port: 8080)          │
                        │  • JWT Authentication Filter             │
                        │  • Route-based Authorization             │
                        │  • Load Balancing via Eureka             │
                        └──┬──────┬──────┬──────┬─────────────────┘
                           │      │      │      │
              ┌────────────▼┐  ┌──▼───┐ ┌▼─────────┐  ┌──────────────────┐
              │ User Service│  │Hotel │ │ Booking  │  │ Payment Service  │
              │  Port: 8081 │  │Svc   │ │ Service  │  │   Port: 8084     │
              │  • JWT Auth │  │8082  │ │  8083    │  │  • Kafka Events  │
              │  • Register │  │• CRUD│ │• Booking │  │  • Payment Flow  │
              │  • Login    │  │• Lock│ │• RestTmpl│  └────────┬─────────┘
              └─────────────┘  └──────┘ └──────────┘           │
                                                                │ Kafka Topic
                                                    ┌───────────▼──────────┐
                                                    │ Notification Service │
                                                    │     Port: 8085       │
                                                    │  • Email/SMS Alerts  │
                                                    └──────────────────────┘

        ┌──────────────────────────────────────────────────────────────────┐
        │                    Infrastructure Layer                          │
        │  Eureka Server (8761) │ Config Server (8888) │ Zipkin (9411)    │
        │  PostgreSQL (5432)    │ Redis (6379)          │ Kafka (9092)     │
        └──────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x, Spring Cloud 2023.x |
| **API Gateway** | Spring Cloud Gateway |
| **Service Discovery** | Netflix Eureka |
| **Config Management** | Spring Cloud Config Server |
| **Security** | Spring Security + JWT (JJWT) |
| **Database** | PostgreSQL 15 (per service) |
| **ORM** | Spring Data JPA / Hibernate |
| **Caching & Locking** | Redis 7 + Redisson (Distributed Lock) |
| **Messaging** | Apache Kafka (Confluent 7.4) |
| **HTTP Client** | RestTemplate (inter-service calls) |
| **Tracing** | Zipkin + Spring Cloud Sleuth |
| **Containerization** | Docker + Docker Compose |
| **Build Tool** | Maven (Multi-Module) |

---

## 📦 Services

| Service | Port | Responsibility |
|---|---|---|
| **api-gateway** | 8080 | Single entry point, JWT filter, routing |
| **eureka-server** | 8761 | Service registry & discovery |
| **config-server** | 8888 | Centralized config management |
| **user-service** | 8081 | User registration, login, JWT issuance |
| **hotel-service** | 8082 | Hotel CRUD, availability management |
| **booking-service** | 8083 | Booking creation, status management |
| **payment-service** | 8084 | Payment processing, Kafka event publisher |
| **notification-service** | 8085 | Kafka consumer, email/SMS notifications |

---

## ✨ Key Features

### 🔐 Security — JWT + API Gateway Filter
- All requests pass through the **API Gateway JWT Authentication Filter**
- Stateless authentication using **JJWT**
- Role-based access control (USER / ADMIN)
- Token validation happens at gateway level — no duplication in downstream services

### ⚡ Event-Driven Payments — Apache Kafka
- Payment Service publishes events to Kafka topics on payment success/failure
- Notification Service consumes events asynchronously
- Decoupled architecture ensures payment failures don't block booking flow

### 🔒 Distributed Locking — Redisson + Redis
- Hotel room availability is protected with **Redisson distributed locks**
- Prevents double-booking under concurrent requests
- Lock TTL ensures no deadlocks in failure scenarios

### 🌐 Service Discovery — Eureka
- All services register with Eureka Server on startup
- API Gateway uses Eureka for dynamic route resolution
- No hardcoded service URLs — fully dynamic

### ⚙️ Centralized Config — Spring Cloud Config Server
- All service configurations managed from a single Config Server
- Environment-specific configs (dev/prod) supported

### 🔍 Distributed Tracing — Zipkin
- End-to-end request tracing across all microservices
- Visualize latency bottlenecks at `http://localhost:9411`

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker Desktop
- Maven 3.8+

### 1. Clone the Repository
```bash
git clone https://github.com/tushar706/stayease.git
cd stayease
```

### 2. Start Infrastructure (Docker Compose)
```bash
docker-compose up -d
```
This starts: PostgreSQL, Redis, Kafka, Zookeeper, Zipkin

### 3. Start Services (in order)
```bash
# 1. Config Server
cd infrastructure-services/config-server
mvn spring-boot:run

# 2. Eureka Server
cd infrastructure-services/eureka-server
mvn spring-boot:run

# 3. Business Services (any order)
cd business-services/user-service && mvn spring-boot:run
cd business-services/hotel-service && mvn spring-boot:run
cd business-services/booking-service && mvn spring-boot:run
cd business-services/payment-service && mvn spring-boot:run
cd business-services/notification-service && mvn spring-boot:run

# 4. API Gateway (last)
cd infrastructure-services/api-gateway
mvn spring-boot:run
```

### 4. Verify Services
| Service | URL |
|---|---|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| Zipkin UI | http://localhost:9411 |

---

## 🔑 API Endpoints

All requests go through **API Gateway** at `http://localhost:8080`

### Auth (User Service)
```
POST   /api/users/register     → Register new user
POST   /api/users/login        → Login & get JWT token
GET    /api/users/{id}         → Get user details (AUTH required)
```

### Hotels
```
GET    /api/hotels             → List all hotels
GET    /api/hotels/{id}        → Get hotel by ID
POST   /api/hotels             → Create hotel (ADMIN)
PUT    /api/hotels/{id}        → Update hotel (ADMIN)
DELETE /api/hotels/{id}        → Delete hotel (ADMIN)
```

### Bookings
```
POST   /api/bookings           → Create booking (AUTH required)
GET    /api/bookings/{id}      → Get booking details
GET    /api/bookings/user/{userId} → Get user bookings
```

### Payments
```
POST   /api/payments           → Process payment
GET    /api/payments/{id}      → Get payment status
```

---

## 🗄️ Database Schema

Each service has its own **PostgreSQL database** (Database-per-Service pattern):

```
stayease_users      → users table
stayease_hotels     → hotels, rooms tables  
stayease_bookings   → bookings table
stayease_payments   → payments table
```

Schema auto-initialized via `init-db.sql` on Docker startup.

---

## 📊 Monitoring & Tracing

### Zipkin — Distributed Tracing
Access at: `http://localhost:9411`
- Trace requests end-to-end across services
- View latency per service hop
- Identify bottlenecks in microservice calls

---

## 📁 Project Structure

```
stayease/
├── business-services/
│   ├── user-service/
│   ├── hotel-service/
│   ├── booking-service/
│   ├── payment-service/
│   └── notification-service/
├── infrastructure-services/
│   ├── api-gateway/
│   ├── eureka-server/
│   └── config-server/
├── docker-compose.yml
├── init-db.sql
└── pom.xml
```

---

## 🎯 Design Patterns Used

| Pattern | Where Used |
|---|---|
| **API Gateway** | Single entry point, JWT filter |
| **Service Discovery** | Eureka — dynamic service resolution |
| **Database per Service** | Each service owns its PostgreSQL DB |
| **Event-Driven** | Kafka for payment → notification flow |
| **Distributed Lock** | Redisson for concurrent booking prevention |
| **Circuit Breaker** | RestTemplate with fallback handling |
| **Centralized Config** | Spring Cloud Config Server |

---

## 👨‍💻 Author

**Tushar**
- GitHub: [@tushar706](https://github.com/tushar706)
- Project: Built as a portfolio project demonstrating production-grade microservices architecture

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).