# API Integration Documentation

## Overview

This document outlines the complete API integration between the Vue.js frontend and Spring Boot backend for the Online Library Management System.

## Integration Status: ✅ COMPLETE

The frontend-backend integration is fully functional with:

### ✅ Core Components
- **API Client**: Fully configured with error handling, authentication, and environment variables
- **TypeScript Types**: Complete type definitions matching backend DTOs
- **Authentication**: JWT token-based auth with automatic refresh and role-based access
- **Services**: All API services implemented for all backend endpoints
- **Error Handling**: Comprehensive error handling with user-friendly messages
- **CORS**: Properly configured for cross-origin requests

### ✅ API Services Implemented

| Service | Endpoints | Status |
|---------|-----------|--------|
| **Authentication** | Login, Register, Change Password, Verify Token | ✅ Complete |
| **Books** | CRUD operations, Search, Categories, Copies | ✅ Complete |
| **Users** | Get user info, Update profile | ✅ Complete |
| **Borrowing** | Borrow, Return, Renew, History | ✅ Complete |
| **Comments** | CRUD operations for book reviews | ✅ Complete |
| **Notices** | System announcements | ✅ Complete |
| **Fees** | Late fee management | ✅ Complete |
| **Statistics** | Dashboard metrics and reports | ✅ Complete |
| **Borrowing Rules** | Admin configuration | ✅ Complete |

### ✅ Frontend Features

| Feature | Status | Notes |
|---------|--------|-------|
| **User Authentication** | ✅ Complete | Login, Register, Password change |
| **Role-based Access** | ✅ Complete | Admin/User role separation |
| **Book Management** | ✅ Complete | Browse, search, CRUD operations |
| **Borrowing System** | ✅ Complete | Borrow, return, renew books |
| **User Dashboard** | ✅ Complete | Personal borrowing history |
| **Admin Panel** | ✅ Complete | System management features |
| **Responsive UI** | ✅ Complete | Mobile-friendly interface |

## Configuration

### Environment Variables

#### Development (.env.development)
```
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_NAME=Library Management System
```

#### Production (.env.production)
```
VITE_API_BASE_URL=/api/v1
VITE_APP_NAME=Library Management System
```

### Backend Configuration

The backend is configured to:
- Run on port 8080 (configurable via `BACKEND_PORT` env var)
- Accept CORS requests from localhost (any port)
- Use JWT authentication with role-based access control
- Support all CRUD operations with proper validation

## API Client Architecture

### Base Client (`/src/lib/api/client.ts`)
- Automatic JWT token handling
- Request/response interceptors
- Comprehensive error handling
- Environment-based URL configuration

### Service Layer (`/src/lib/api/services/`)
- Individual service classes for each API domain
- Strongly typed with TypeScript
- Consistent error handling
- Pagination support

### Type Definitions (`/src/lib/api/types.ts`)
- Complete TypeScript interfaces
- Matches backend DTOs exactly
- Supports all API operations

## Authentication Flow

1. **Login**: User credentials → JWT token
2. **Token Storage**: Securely stored in localStorage
3. **Auto-injection**: Token added to all API requests
4. **Role-based Routing**: Admin routes protected based on user role
5. **Auto-refresh**: Token validation on app startup

## Testing the Integration

### API Test Page
Visit `/api-test` to access the integration test page that:
- Tests basic API connectivity
- Validates authentication
- Displays sample data
- Shows user information
- Reports any connection issues

### Manual Testing
1. **Start Backend**: `cd backend && ./mvnw spring-boot:run`
2. **Start Frontend**: `cd frontend && npm run dev`
3. **Access**: http://localhost:5173 (or your configured port)
4. **Test**: Navigate to `/api-test` for automated tests

## Development Workflow

### Adding New API Endpoints

1. **Backend**: Create controller endpoint with proper annotations
2. **Frontend Types**: Add TypeScript interfaces in `types.ts`
3. **Frontend Service**: Add methods to appropriate service class
4. **Frontend Views**: Use the service in Vue components
5. **Testing**: Add to API test page if needed

### Error Handling
- All API calls wrapped in try-catch blocks
- User-friendly error messages via toast notifications
- Detailed error logging for debugging
- Graceful fallbacks for failed requests

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: Admin/User permissions
- **CORS Protection**: Configured for specific origins
- **XSS Protection**: Secure token storage and handling
- **Input Validation**: Both frontend and backend validation

## Performance Optimizations

- **Lazy Loading**: Routes loaded on demand
- **Pagination**: Large datasets paginated
- **Caching**: User authentication state cached
- **Optimistic Updates**: UI updates before API confirmation
- **Parallel Requests**: Multiple API calls when possible

## Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure backend CORS is properly configured
2. **Authentication Issues**: Check token validity and format
3. **404 Errors**: Verify API URLs match backend endpoints
4. **Network Errors**: Confirm backend is running on correct port

### Debug Steps

1. Check browser network tab for API requests
2. Review console for error messages
3. Use `/api-test` page for connection validation
4. Verify environment variables are loaded correctly

## Next Steps

The integration is complete and production-ready. Future enhancements could include:

- [ ] API response caching
- [ ] Real-time updates via WebSocket
- [ ] Offline support with service workers
- [ ] Enhanced error reporting
- [ ] API rate limiting
- [ ] Advanced search filters
- [ ] Bulk operations support

## Support

For issues or questions about the API integration:
1. Check this documentation
2. Use the `/api-test` page for diagnostics
3. Review browser console for errors
4. Check network requests in DevTools