# Online Library Management System - Frontend

Vue.js frontend application for the Online Library Management System.

## Features

### Dashboard System
The application now features separate dashboards for different user roles:

#### User Dashboard (`/dashboard`)
- **Audience**: Regular library users
- **Features**:
  - Personal borrowing statistics
  - Recent borrowing activity
  - Available books display
  - Quick actions for browsing and managing personal account
  - Personalized recommendations (coming soon)
  - Library notices and announcements

#### Admin Dashboard (`/admin/dashboard`)
- **Audience**: Library administrators  
- **Features**:
  - System-wide statistics (total users, books, borrows)
  - Administrative quick actions
  - Recent system activity
  - User management shortcuts
  - System configuration tools
  - Notice management

### Smart Navigation
The application automatically routes users to the appropriate dashboard:
- **Regular users** → User Dashboard (`/dashboard`)
- **Administrators** → Admin Dashboard (`/admin/dashboard`)
- **Sidebar navigation** → Dynamically shows appropriate dashboard link based on user role

### Authentication & Authorization
- Role-based access control (USER vs ADMIN)
- Protected routes with authentication guards
- Admin-only routes and features

## Getting Started

```bash
# Install dependencies
bun install

# Start development server
bun run dev

# Build for production
bun run build
```

## Project Structure

```
src/
├── views/
│   ├── DashboardView.vue      # User dashboard
│   ├── AdminDashboardView.vue # Admin dashboard
│   └── ...
├── components/
│   ├── AppSidebar.vue         # Smart navigation sidebar
│   └── ...
├── stores/
│   └── auth.ts                # Authentication store with role management
├── router/
│   └── index.ts               # Route configuration with role-based guards
└── ...
```

## Technologies Used

- Vue 3 with Composition API
- TypeScript
- Vue Router for navigation
- Pinia for state management
- Tailwind CSS for styling
- Radix Vue components
- Lucide Vue for icons

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
bun install
```

### Compile and Hot-Reload for Development

```sh
bun dev
```

### Type-Check, Compile and Minify for Production

```sh
bun run build
```

### Run Unit Tests with [Vitest](https://vitest.dev/)

```sh
bun test:unit
```

### Lint with [ESLint](https://eslint.org/)

```sh
bun lint
```
