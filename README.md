# Online Library Management System

[中文版](README_zh-CN.md)

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5.13-brightgreen.svg)

> A full-stack online library management system example based on **Spring Boot + Vue 3**, including complete business processes for books, users, borrowing, reservations, fees, and backend management.

## 📚 Project Highlights

- **Frontend/Backend Separation**: Spring Boot provides RESTful APIs, while Vue 3 + Vite builds a modern management interface.
- **Complete Business Functions**: Supports book management, borrowing rules, reservations, comments, favorites, fee settlement, statistical reports, etc.
- **Secure and Reliable**: Uses JWT + Spring Security for authentication and access control.
- **Type-Safe**: Backend uses Lombok & Bean Validation, frontend fully adopts TypeScript with Pinia for state management.
- **Modern UI**: Based on shadcn-vue component library and Tailwind CSS, with a built-in dark mode switch.

## 🏗️ Tech Stack

| Layer      | Technology                                           | Description                                        |
|------------|------------------------------------------------------|----------------------------------------------------|
| Backend    | Spring Boot · Spring Security · Hibernate/JPA · Maven | Core framework and ORM persistence                 |
| Database   | MySQL / PostgreSQL                                   | Any compatible relational database (H2 in-memory for quick start) |
| Auth       | JWT                                                  | Stateless token-based login                        |
| Frontend   | Vue 3 · Vite · TypeScript                            | Modern frontend development experience             |
| UI         | shadcn-vue · lucide-vue-next · Tailwind CSS          | Consistent component and icon system               |
| State Mgmt | Pinia                                                | Lightweight state management store                 |
| Routing    | Vue Router                                           | Single-page application routing                    |
| HTTP Client| Custom API Client (Fetch)                            | Encapsulated API calls and type definitions        |

## 📂 Project Structure

```
 online-library-management/
 ├─ backend/        # Spring Boot application
 │  └─ src/main/... # Business logic (Controllers / Services / Repositories / DTOs, etc.)
 ├─ frontend/       # Vue 3 frontend application
 │  ├─ src/         # Components, views, router, stores, API, etc.
 │  └─ index.html   # SPA entry point
 └─ README.md       # Project documentation
```

## 🚀 Getting Started

### Prerequisites

- Node.js ≥ 21 (Bun is recommended to run the frontend)
- JDK ≥ 21
- Maven ≥ 3.8

> If you need to use a local database, please prepare MySQL / PostgreSQL and modify the configuration in `backend/src/main/resources/application-dev.properties`.

### Running in Development Mode

In the project root directory, execute:

```bash
# Backend: Start Spring Boot
cd backend
./mvnw spring-boot:run
```

In another terminal:

```bash
# Frontend: Start Vite + Bun
cd frontend
bun install  # Install dependencies for the first time
bun run dev      # http://localhost:5173
```

Access:
- Frontend SPA: `http://localhost:5173`
- Backend API: `http://localhost:8080/api`

### Production Build

```bash
# Build frontend static files
cd frontend
bun run build

# Build Jar package
cd ../backend
./mvnw clean package -DskipTests
```

The generated `frontend/dist` can be deployed to any static file server; the backend `target/*-SNAPSHOT.jar` can be run with `java -jar`.

## ⚙️ Configuration

- **Database**: Configure `spring.datasource.*` in `backend/src/main/resources/application-dev.properties`.
- **JWT Secret**: Set `jwt.secret` and expiration time in `application.properties`.
- **Frontend Environment Variables**: Maintain the API root path and other settings in a `.env` file in the `frontend` directory.

## 🛠️ Available Scripts

| Directory | Command                  | Description                    |
|-----------|--------------------------|--------------------------------|
| backend   | `./mvnw spring-boot:run` | Run backend (with hot-reload)  |
| backend   | `./mvnw test`            | Run backend tests              |
| frontend  | `bun dev`                | Start frontend dev server      |
| frontend  | `bun run build`          | Build for production           |
| frontend  | `bun run lint`           | Check with ESLint + Prettier   |

## 🧪 Test Data

The project initializes with demo data at startup, including basic users, books, and borrowing records, to provide a quick experience.

## 📑 API Documentation

The backend uses SpringDoc to generate OpenAPI documentation. Once the application is running, you can access it at:

```
http://localhost:8080/swagger-ui/index.html
```

## 📝 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.
