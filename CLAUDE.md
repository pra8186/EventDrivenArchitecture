# CLAUDE.md — Event-Driven Project AI Collaboration Log

## Project Overview

## Project Architecture

```
eventdriven/ (Gradle multi-module)
├── eureka-server/     — Service discovery (port 8761)
├── core-service/      — Tax APIs + Feign client (port 8081) Feign is used to call external API's from java project
└── event-service/     — Kafka event log + REST (port 8082)
```

### Communication Flow

```
Client → core-service (8081) → TaxService → Feign Client
                                                  │
                                    Eureka discovers event-service
                                                  │
                                                  ▼
                                     event-service (8082)
                                     POST /api/events
                                                  │
                                     EventProducerService → Kafka topic
                                                  │
                                     EventConsumerService ← Kafka topic
                                                  │
                                     EventRecordRepository → DB
```

## How to Run

```bash
# Terminal 1 — Eureka Server
./gradlew :eureka-server:bootRun

# Terminal 2 — Event Service
./gradlew :event-service:bootRun

# Terminal 3 — Core Service
./gradlew :core-service:bootRun

# Terminal 4 - Root Class
./gradlew :bootRun

# Test
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"securepass","firstName":"John","lastName":"Doe","ssnEncrypted":"enc-001"}'

# Check events persisted via Kafka
curl http://localhost:8082/api/events
```
