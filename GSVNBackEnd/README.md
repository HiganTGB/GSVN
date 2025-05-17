# [GSVN - E-commerce]

## Overview

This project is a backend system designed to allow users to make purchases, similar to the functionality found on the Goodsmile US website. It provides the necessary infrastructure for managing product catalogs, user accounts, order processing, and integrates with VNPAY for payment processing. The system's database is also designed with a future transition to a microservices architecture in mind.

## Features
* **Database Design for Microservice Transition:** The database schema has been designed with future migration to a microservices architecture in mind. This foresight allows for smoother scalability and independent deployment of services when resources permit.
* **Orchestrated Transactions with Saga Pattern via RabbitMQ:** Implements the Saga pattern, utilizing RabbitMQ for asynchronous communication and ensuring data consistency across the entire order lifecycle, including order placement, inventory holding, and payment processing.
* **Centralized Logging with Filebeat and Logstash:** Implements Filebeat for log shipping and Logstash for log processing and management, providing a centralized system for monitoring and debugging.
* **Faster Search with Elasticsearch:** Utilizes Elasticsearch to provide fast and efficient product search capabilities.
* **User Authentication via Keycloak and Spring Addons OIDC Starter:** Implements a secure user authentication system leveraging Keycloak as the identity provider and the Spring Addons OIDC Starter library for integration.
## Technologies Used
* **Core** : Spring Boot - Java 17
* **Database:** MySQL,MongoDB,ElasticSearch
* **Message Broker:** RabbitMQ
* **Caching:** Redis
* **Authentication:** Keycloak, [Spring Addons OIDC Starter](https://github.com/ch4mpy/spring-addons/blob/master/spring-addons-starter-oidc/README.MD).
* **Object Storage:** Minio
* **Library:** MapStruct,Querydsl
* **Log** : LogStash,FileBeat
## Explanation
## Future Development
* **Microservices Architecture Transition:** Transition to microservices with Eureka and Kong Gateway.
* **Real-time Communication:** Implement WebSocket instead of polling for real-time updates.
* **Refund,Cancel Functionality:** Add support for refunds in VNPAY.
* **Message Queue Alternatives:** Explore the possibility of using Kafka instead of RabbitMQ.
* **More Functionality:** Discount,Shipping,Messaging System,
A more detailed explanation of the project's architecture, design decisions, or key concepts. This section can provide deeper insights into how the project works.
## API Documentation
Under development with Swagger
## Getting Started / Installation / Usage

# Setup and Usage Guide

This document outlines the steps to set up and use the services defined in the `docker-compose.yml` file and the Java backend application configured in the `Dockerfile` and `pom.xml`.

## Prerequisites

To follow the steps below, ensure you have the following software installed on your system:

* **Docker Engine:** Latest version recommended.
* **Docker Compose:** Latest version recommended.
* **Maven:** (Only required if you want to manually rebuild the Java application) Version 3.x.
* **Java Development Kit (JDK):** (Only required if you want to manually rebuild the Java application) Version 17.

## Installation Steps

1.  **Clone Repository (optional):** If the backend application source code is not already on your machine, clone the repository containing it.

    ```bash
    git clone <repository_address>
    cd <repository_directory_name>
    ```

2**Build and Run Docker Containers:** Use the `docker-compose up` command to build the images and start the containers defined in `docker-compose.yml`. Add the `-d` flag to run the containers in detached (background) mode.

    ```bash
    docker-compose up -d
    ```

    This command will:
    * Create the `gsvn-network` network.
    * Create the `mysql_data` and `mongodb_data` volumes.
    * Build the image for the `gsvnbackend` service based on the `Dockerfile` (if the image doesn't exist).
    * Start the containers in the order of their dependencies (e.g., `mysql-db` will start before `gsvnbackend`).
    * Expose the specified ports on your host machine.


**Important Notes:**
* **Filebeat Configuration:** The `filebeat.yml` file is configured to monitor log files in `/var/log/server/`. You might need to adjust this path to match the actual location of your application's log files within the container. You can also control the log output format and location of your Java backend application by configuring the `logback.xml` file in your project.
* **Maven Generate Scripts:** Before running the Docker Compose setup, ensure you have executed the Maven command to generate necessary source code, especially for QueryDSL. Run the following command in your backend project directory:
    ```bash
    mvn generate-sources
    ```
* **Kibana User:** If Kibana is not working due to Elasticsearch not yet providing a user, you might need to run a user creation script within the Elasticsearch container. Look for a script named `create_user.sh` in script folder. You can execute commands inside the Elasticsearch container using `docker exec -it elastic /bin/bash` and then run the script. Refer to the Elasticsearch documentation for specific user creation instructions if needed.
* **MinIO Bucket Creation:** To utilize MinIO for storage, you will likely need to create buckets. You can do this using the MinIO console UI (at `http://localhost:9001`) or the MinIO client (`mc`). For example, using `create_buckets.sh` in script folder:
    ```bash
    bash create_buckets.sh
    ```
  Replace `your-bucket-name` with the desired name for your bucket.

To stop all running containers, you can use the command:

```bash
docker-compose down
```



## Contact

You can reach me via [Outlook](mailto:giabao21sgu@outlook.com).

