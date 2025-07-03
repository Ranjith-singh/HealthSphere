# HealthSphere

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![AWS/localstack](https://img.shields.io/badge/localstack/AWS-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)

A comprehensive microservices architecture for patient management with modern technologies.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Development Setup](#development-setup)
- [Deployment](#deployment)
- [Testing](#testing)
- [License](#license)

## Features

### Core Services
- **Patient Service**: Full CRUD operations with validation
- **Billing Service**: gRPC-based billing integration
- **Analytics Service**: kafka consumer for consuming events from patient service for analytics
- **Auth Service**: JWT token generation and validation

### Infrastructure
- **API Gateway**: Unified entry point with JWT validation
- **Service Discovery**: Built-in service registration
- **Event Streaming**: Kafka-based event bus
- **Database**: PostgreSQL with H2 for development

### Operational
- **Containerized**: Docker support for all services
- **IaC**: AWS CDK for cloud deployment
- **CI/CD**: GitHub Actions pipeline
- **Monitoring**: Prometheus metrics endpoint

## Architecture

```mermaid
graph TD
    A[Frontend] --> B[API Gateway]
    B --> C[Auth Service]
    C --> D[Patient Service]
    D --> E[gRPC Billing Service]
    D --> F[(PostgreSQL)]
    C --> F
    D --> G[[Kafka]]
    G --> i[Analytics Service]
