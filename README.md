# FIFA 2026 Smart Stadiums & Tournament Operations Platform

A production-ready, secure web application for stadium navigation, crowd management, accessibility, and real-time fan assistance for the FIFA World Cup 2026.

## 🏟️ Architecture

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | Vanilla HTML/CSS/JS | Dashboard + Fan Chat UI (WCAG 2.1 AA) |
| **Backend** | Java 21 / Spring Boot 3.3.4 | REST API with Domain-Driven Design |
| **Security** | Spring Security + Supabase JWT | Role-based access (FAN vs STAFF) |
| **Database** | Supabase PostgreSQL + pgvector | Relational data + RAG embeddings |
| **Caching** | Caffeine (Spring Cache) | Transit schedules (5min TTL) |
| **Deployment** | Docker + Kubernetes | Production-ready containers |

## 🔑 Testing Credentials

Use the following credentials to bypass Supabase email verification during testing and access the **Staff Dashboard**:
- **Email:** `staff@fifa2026.com`
- **Password:** `Password123!`

*(Note: The Fan Chat interface is accessible without authentication by clicking "Continue as Fan".)*

## 🚀 Quick Start

### Prerequisites
- Java 21+ (JDK)
- Maven 3.9+ (or use the included Maven wrapper)
- Supabase account (project already provisioned)

### Environment Variables
```bash
export SUPABASE_DB_URL=jdbc:postgresql://db.anoofcmhgkdclgeredla.supabase.co:5432/postgres
export SUPABASE_DB_USER=postgres
export SUPABASE_DB_PASSWORD=your-password
export SUPABASE_URL=https://anoofcmhgkdclgeredla.supabase.co
export SUPABASE_ANON_KEY=your-anon-key
export SUPABASE_JWT_SECRET=your-jwt-secret
# Optional: Enable Gemini API for production RAG
export GEMINI_API_KEY=your-gemini-key
export GEMINI_ENABLED=true
```

### Build & Run
```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/smartstadium-0.0.1-SNAPSHOT.jar

# Or with Docker
docker-compose up --build
```

### Run Tests
```bash
mvn test                    # Unit tests
mvn verify                  # Integration tests
mvn jacoco:report          # Coverage report
```

## 📁 Project Structure

```
src/main/java/com/fifa2026/smartstadium/
├── SmartStadiumApplication.java
├── config/           # Security, Cache, CORS configs
├── security/         # JWT filter, Supabase Auth
├── domain/
│   ├── stadium/      # Stadium entity, CRUD API
│   ├── zone/         # Stadium zone management
│   ├── crowd/        # Crowd metrics + dashboard API
│   ├── transit/      # Transit schedules (cached)
│   └── assistant/    # RAG-powered GenAI chat
├── exception/        # Global error handling
```

## 🔐 Security

- **JWT Authentication**: Supabase-issued JWTs validated via HMAC-SHA256
- **Role-Based Access**: FAN (read-only) vs STAFF (full access)
- **RLS Policies**: Row-Level Security on all Supabase tables
- **CORS**: Configured for frontend origins
- **Stateless**: No server-side sessions

## 🤖 GenAI Assistant (RAG)

- **pgvector**: 768-dimensional embeddings for knowledge retrieval
- **Conditional Architecture**: Uses Gemini API when key is present, falls back to mock LLM
- **Multilingual**: Supports EN, ES, FR, AR
- **Knowledge Base**: Stadium maps, accessibility routes, transit schedules, FAQs

## 📊 Crowd Management

- **Edge CV Ingestion**: REST API accepts YOLOv8 camera feed data
- **Auto Alert Escalation**: NORMAL → ELEVATED → HIGH → CRITICAL
- **Real-Time Dashboard**: 10-second auto-refresh with heatmap visualization

## ♿ Accessibility (WCAG 2.1 AA)

- Semantic HTML5 elements
- ARIA labels on all interactive elements
- Minimum 4.5:1 contrast ratio
- Keyboard navigability
- Screen reader compatible live regions

## 🐳 Deployment

### Docker
```bash
docker build -t fifa2026/smartstadium:1.0.0 .
docker run -p 8080:8080 --env-file .env fifa2026/smartstadium:1.0.0
```

### Kubernetes
```bash
kubectl apply -f k8s/deployment.yaml
```

Features: 3 replicas, HPA (3-20 pods), rolling updates, health probes, non-root security context.

## 📝 License

Built for the FIFA World Cup 2026 Challenge.
