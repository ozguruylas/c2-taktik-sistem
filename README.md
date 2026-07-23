# 🛰️ C2 Tactical Command Center (Autonomous UAV Command & Control & Live Telemetry Simulation Engine)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![WebSocket](https://img.shields.io/badge/Protocol-STOMP%20%2F%20SockJS-blue.svg)](https://stomp.spec.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Strategy%20Pattern-red.svg)](https://en.wikipedia.org/wiki/Strategy_pattern)
[![Frontend](https://img.shields.io/badge/Map%20Engine-Leaflet.js-green.svg)](https://leafletjs.com/)

**C2 Tactical Command Center** is an enterprise-grade Command and Control (C2) and Digital Twin simulation engine inspired by defense industry avionics requirements and tactical field operations. It is architected with real-time telemetry broadcasting, autonomous decision-making algorithms, and strict Object-Oriented Programming (OOP & SOLID) principles.

---

## 📌 Project Overview & Engineering Vision

The system actively rejects spaghetti code and rigid if-else control blocks, managing the fleet's operational lifecycle through the **Strategy Design Pattern** driven by an asynchronous background simulation engine. Instead of relying on clunky, continuous client-side polling, it implements a low-latency, server-based **Server-Push** architecture to deliver an authentic real-time radar experience.

---

## 🧠 Architectural Design & OOP Principles

The core architecture is built upon the **State Machine** abstraction widely preferred in military and mission-critical avionics software:

*   **Polymorphism & Strategy Pattern:** The `DroneStateStrategy` interface was designed to govern real-time operational states (`IDLE`, `IN_MISSION`, `RETURNING`, `MAINTENANCE`). Each flight mode encapsulates its own distinct behavior, and the system dynamically invokes the appropriate strategy at runtime via polymorphism.
*   **Open/Closed Principle (OCP):** Introducing a new military tactic or flight maneuver (e.g., Kamikaze Mode, Reconnaissance Flight) requires zero modification to existing core business logic; developers simply integrate a new strategy class implementing the base interface.
*   **Encapsulation:** UAV battery levels, spatial coordinates, and operational statuses are strictly closed to direct modification; all state transitions are strictly governed by domain business rules and threshold validations.
*   **Clean Code & Magic Number Refactoring:** All arbitrary numerical values across the codebase (wind deviation coefficients, critical battery limits, boundary coordinates) have been refactored into `private static final` constants, ensuring enterprise-level maintainability.

---

## ⚙️ Background Simulation Engine & Data Pipeline

The backbone of the application is the **Autonomous Simulation Engine**, which operates continuously in the background without human intervention:

1.  **Asynchronous Heartbeat (`@Scheduled`):** Running on Spring Boot's scheduler threads, the engine triggers every second to mathematically compute the trajectory, wind deviation, and battery consumption of all airborne UAVs.
2.  **Database Synchronization (JPA / Hibernate):** Computed telemetry data is instantaneously mapped and persisted into a relational database (**PostgreSQL / H2**) via the ORM layer.
3.  **Real-Time Radio Broadcast (STOMP Push):** The live telemetry stream processed in the background is pushed over a bidirectional **WebSocket (STOMP/SockJS)** message broker via the `/topic/drones` frequency directly to the **Leaflet.js** map interface in microseconds (Server-Push).

---

## 🛩️ Autonomous Flight Physics & Avionics Rules

*   **Organic Deviation (Turbulence Simulation):** To prevent UAVs from traversing the map in an artificial, robotic straight line, mathematical wind and turbulence variance coefficients (`Math.random()`) are integrated. Each unit navigates toward its waypoint with realistic, curved flight dynamics.
*   **Bingo Fuel & RTB (Return To Base) Protocol:** When a UAV's battery drops to the operational critical threshold of **55%**, the autopilot overrides manual directives, cancels the mission, and calculates an automatic **Return To Base (RTB)** vector. Upon arriving at the Ankara headquarters (HQ), the unit is recharged and reset to the operational `IDLE` standby mode.
*   **No-Op Protection:** Depleted or malfunctioning units transition to the `MAINTENANCE` status, intentionally locking into a static state to prevent unnecessary CPU cycle consumption within the background simulation loop.

---

## 🛠️ Technology Stack

### Backend & Core Framework
*   **Java 17+ & Spring Boot 3.x:** Core application framework and IoC Container
*   **Spring WebSocket & STOMP / SockJS:** Low-latency bidirectional message broker
*   **Spring Data JPA & Hibernate:** ORM and data persistence layer
*   **Maven:** Dependency and build management
*   **Lombok:** Boilerplate code reduction

### Frontend & GIS Engine
*   **Leaflet.js:** Vector Map Engine and SVG flight vector rendering
*   **STOMP.js & SockJS Client:** WebSocket consumer client
*   **HTML5 & Modern CSS:** Dark Military Tactical UI Theme

### Database & Software Architecture
*   **PostgreSQL / H2 In-Memory Database:** Relational data modeling
*   **Design Patterns:** Strategy Pattern, State Machine Modeling, Layered Architecture
*   **Methodologies:** SOLID, OOP, Clean Code, DRY, KISS

---

## 📊 Autonomous Lifecycle (State Machine)

The autonomous state transitions of a UAV within the simulation engine occur according to the following rules:

```mermaid
stateDiagram-v2
    [*] --> IDLE : System Registration & Deploy
    
    IDLE --> IN_MISSION : Operator Command (Target Assignment)
    
    IN_MISSION --> RETURNING : Bingo Fuel (Battery < 55%)
    IN_MISSION --> RETURNING : Manual RTB Command
    
    RETURNING --> IDLE : Base Arrival & Recharge (100%)
    
    IN_MISSION --> MAINTENANCE : Mid-Air Battery Depletion (0%)
    RETURNING --> MAINTENANCE : Mid-Air Battery Depletion (0%)
    
    MAINTENANCE --> [*] : Offline (No-Op Cycle)