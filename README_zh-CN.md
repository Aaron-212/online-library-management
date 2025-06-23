# 在线图书馆管理系统（Online Library Management System）

[English Version](README.md)

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5.13-brightgreen.svg)

> 一个基于 **Spring Boot + Vue 3** 的全栈在线图书馆管理系统示例，包含图书、用户、借阅、预约、费用及后台管理等完整业务流程。

## 📚 项目亮点

- **前后端分离**：Spring Boot 提供 RESTful API，Vue 3 + Vite 构建现代化管理界面。
- **完善的业务功能**：支持图书管理、借阅规则、预约、评论、收藏、费用结算、统计报表等。
- **安全可靠**：采用 JWT + Spring Security 进行身份认证与权限控制。
- **类型安全**：后端使用 Lombok & Bean Validation，前端全面采用 TypeScript 与 Pinia 状态管理。
- **现代化 UI**：基于 shadcn-vue 组件库与 Tailwind CSS，内置暗黑模式切换。

## 🏗️ 技术栈

| 层 | 技术 | 说明 |
|----|------|------|
| 后端 | Spring Boot · Spring Security · Hibernate/JPA · Maven | 核心框架与 ORM 持久化 |
| 数据库 | MySQL / PostgreSQL | 任意兼容的关系型数据库（默认 `H2` 内存库用于快速启动） |
| 认证 | JWT | 无状态令牌登录 |
| 前端 | Vue 3 · Vite · TypeScript | 现代化前端开发体验 |
| UI | shadcn-vue · lucide-vue-next · Tailwind CSS | 一致的组件与图标体系 |
| 状态管理 | Pinia | 轻量级 store |
| 路由 | Vue Router | 单页应用路由控制 |
| HTTP 客户端 | 自定义 API Client (Fetch) | 封装接口与类型定义 |

## 📂 项目结构

```
 online-library-management/
 ├─ backend/        # Spring Boot 应用
 │  └─ src/main/... # 业务代码（控制器 / 服务 / 仓储 / DTO 等）
 ├─ frontend/       # Vue 3 前端应用
 │  ├─ src/         # 组件、视图、路由、状态、API 等
 │  └─ index.html   # SPA 入口
 └─ README.md       # 项目说明
```

## 🚀 快速开始

### 前置条件

- Node.js ≥ 21（建议使用 [Bun](https://bun.sh/) 运行前端）
- JDK ≥ 21
- Maven ≥ 3.8

> 如需使用本地数据库，请准备好 MySQL / PostgreSQL 并修改 `backend/src/main/resources/application-dev.properties` 配置。

### 一键启动（开发模式）

在项目根目录执行：

```bash
# 后端：启动 Spring Boot
cd backend
./mvnw spring-boot:run
```

另开终端：

```bash
# 前端：启动 Vite + Bun
cd frontend
bun install  # 首次运行下载依赖
bun run dev      # http://localhost:5173
```

访问：
- 前端 SPA：`http://localhost:5173`
- 后端 API：`http://localhost:8080/api`

### 生产构建

```bash
# 构建前端静态文件
cd frontend
bun run build
# 构建 Jar 包
cd ../backend
./mvnw clean package -DskipTests
```

生成的 `frontend/dist` 可部署到任何静态资源服务器；后端 `target/*-SNAPSHOT.jar` 可通过 `java -jar` 运行。

## ⚙️ 环境配置

- **数据库**：在 `backend/src/main/resources/application-dev.properties` 中配置 `spring.datasource.*`。
- **JWT 密钥**：在 `application.properties` 设置 `jwt.secret` 与过期时间。
- **前端环境变量**：在 `.env` 文件中维护 API 根路径等。

## 🛠️ 常用脚本

| 目录 | 命令 | 说明 |
|-----|------|------|
| backend | `./mvnw spring-boot:run` | 运行后端（热重载） |
| backend | `./mvnw test` | 运行后端测试 |
| frontend | `bun dev` | 启动前端开发服务器 |
| frontend | `bun run build` | 生产构建 |
| frontend | `bun run lint` | ESLint + Prettier 检查 |

## 🧪 测试数据&初始化

项目内置演示数据，默认会在启动时插入基本用户、图书及借阅记录，方便快速体验。

## 📑 API 文档

后端使用 Spring REST 文档（或 Swagger）生成 API 文档，启动后访问：

```
http://localhost:8080/swagger-ui/index.html
```

## 📝 许可证

本项目使用 **MIT License**，详情见 [LICENSE](LICENSE)。 
