# GSVN - Microservices E-Commerce Platform

## Overview

**GSVN** is a high-performance, scalable backend ecosystem for an e-commerce platform inspired by the Goodsmile website. Built on Java 21 and Spring Boot, the system adopts a distributed **Microservices Architecture** with 12 specialized services, service discovery, centralized API routing, asynchronous messaging, and containerized deployment.

---

## Architecture & System Services

The platform consists of **14 decoupled microservices**, each running on its dedicated port and managing its isolated storage schema or database:

| Service Name | Port | Database / Storage Schema | Primary Responsibility |
| :--- | :--- | :--- | :--- |
| **Auth Service** | `5001` | PostgreSQL (`acc_db`), Redis (DB 1) | User authentication, identity management & JWT issuance |
| **Media Service** | `5002` | MinIO Storage (`media`, `temp` buckets) | Asset uploading, image optimization & storage |
| **HRM Service** | `5003` | PostgreSQL (`hrm_db`), Redis (DB 2) | Staff & internal human resources management |
| **Customer Service** | `5004` | PostgreSQL (`customer_db`), Redis (DB 6) | Customer profiles & account information |
| **Address Service** | `5005` | SQLite (`address_db.db`) | Geolocation & shipping address management |
| **Product Service** | `5006` | PostgreSQL (`product_db`), Redis (DB 3) | Dynamic product catalog (EAV Model) & categories |
| **Inventory Service**| `5007` | PostgreSQL (`inventory_db`), Redis (DB 7) | Stock control, warehouse management & encryption |
| **Promotion Service**| `5008` | PostgreSQL (`promotion_db`), Redis (DB 4) | Vouchers, discounts & promotional campaigns |
| **Shipment Service** | `5009` | PostgreSQL (`shipping_db`), Redis (DB 6) | Shipping integration with GHN API |
| **Search Service** | `5010` | Redis (DB 3) | Fast search index & product filtering |
| **Cart Service** | `5012` | PostgreSQL (`cart_db`), Redis (DB 8) | Shopping cart management |
| **Order Service** | `5013` | PostgreSQL (`order_db`), Redis (DB 7) | Order lifecycle & Saga transaction execution |
| **Payment Service** | `5014` | PostgreSQL (`payment_db`), Redis (DB 7) | VNPay Sandbox integration & instant processing |
| **Notification** | `5015` | Redis (DB 9), SMTP Mail Server | Asynchronous email & push notifications |

### Core Infrastructure Components
* **API Gateway & Admin UI:** Kong Gateway managed via **Konga Admin UI** (`:1337`).
* **Service Discovery:** 3-Node **HashiCorp Consul Cluster** (`:8500`).
* **Message Broker:** **RabbitMQ** (`:5672`) for distributed events and Saga/Outbox workflows.
* **Storage & Caching:** **PostgreSQL** (`:5432`), **Redis** (`:6379`), and **MinIO Object Storage** (`:9000` / Console `:9001`).

---

## Key Technical Features

* **API Gateway & Routing:** All requests are routed through **Kong Gateway**, enforcing security, rate limiting, and RBAC authorization across services.
* **Distributed Transactions (Saga & Outbox Patterns):** Uses **RabbitMQ** and **Resilience4j Circuit Breakers** to handle asynchronous distributed order processing and ensure data consistency.
* **Dynamic EAV Database Schema:** Implements an Entity-Attribute-Value model in **PostgreSQL** to handle 100+ dynamic attributes across SKUs and pre-order date ranges.
* **Security:** Hybrid **JWT Authentication** combined with **Google OAuth2** for identity verification.
* **Payment & Shipping Integration:** Native sandbox integrations with **VNPay Payment Gateway** and **GHN (Giao Hàng Nhanh)** real-time shipping calculation.
* **Multi-Stage Docker Builds:** Optimized Docker Compose configuration using Spring Boot layertools to accelerate rebuilds and reduce image sizes.

---

## Getting Started

### Prerequisites

* **Docker Engine** (v20.10+) & **Docker Compose**
* **JDK 21** & **Maven 3.8+** (Optional, for manual local builds)

---

### Installation & Deployment Steps

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/HiganTGB/GSVN.git](https://github.com/HiganTGB/GSVN.git)
   cd GSVN/docker
2. **Prepare Environment Variables from Example File**
```
cp .env.example .env

```


3. **Start Core Infrastructure:**
```bash
docker compose up -d postgres redis rabbitmq consul-server-1 consul-server-2 consul-server-3 minio

```


4. **Initialize Kong Gateway & Konga Admin UI:**
```bash
# Run database migrations for Kong
docker compose up kong-migrations

# Start Kong database and Konga UI database
docker compose up -d kong-database konga-db

# Prepare Konga seed data
docker compose up konga-prepare

# Launch Gateway & Management UI
docker compose up -d kong konga

```


5. **Configure Kong Gateway & MinIO Keys:**
* Open **Konga UI** at `http://localhost:1337` and connect to Kong Admin (`http://kong:8001`).
* Navigate to **Snapshots** $\rightarrow$ **Import from file** $\rightarrow$ select `snapshot.json` in the `docker` folder $\rightarrow$ Click **Restore All**.
* Open **MinIO Console** at `http://localhost:9001` $\rightarrow$ Create an **Access Key** $\rightarrow$ Update `MINIO_ACCESS_KEY` & `MINIO_SECRET_KEY` in your `.env` file.


6. **Build & Launch All Microservices:**
```bash
docker compose build
docker compose up -d

```



---

## System Management & Dashboard Links

Once the system is running, access the following management consoles:

* **HashiCorp Consul (Service Discovery):** [http://localhost:8500](http://localhost:8500)
* **Konga UI (API Gateway Admin):** [http://localhost:1337](http://localhost:1337)
* **MinIO Console (Object Storage):** [http://localhost:9001](http://localhost:9001)
* **Swagger API Docs:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Future Improvements

* **Elasticsearch Search Engine:** Integrate Elasticsearch into `Search Service` for high-performance full-text query processing.
* **Kubernetes Orchestration:** Migrate services from Docker Compose to Kubernetes (K8s) clusters on cloud infrastructure.
* **Centralized Observability:** Build an ELK/PLG stack (Prometheus, Grafana, Loki) for real-time log monitoring and metrics collection.
* **UI:** Develop a Frontend application.
* **Gateway** : using Kong OSS instead of Konga



