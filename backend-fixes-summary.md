# Backend Issues Fixed

## Summary
This PR addresses several potential issues identified in the API backend to improve code quality, error handling, and maintainability.

## Issues Fixed

### 1. Logging Improvement
- **Issue**: `BorrowService.java` was using `System.out.println()` instead of proper logging
- **Fix**: Replaced with SLF4J logger with proper parameterized logging
- **Files Modified**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/BorrowService.java`

### 2. Custom Exception Handling
- **Issue**: Extensive use of generic `RuntimeException` throughout the codebase
- **Fix**: Created custom exception classes for better error categorization and handling:
  - `ResourceNotFoundException` - for missing resources
  - `DuplicateResourceException` - for duplicate resource conflicts
  - `BusinessLogicException` - for business rule violations
  - `UnauthorizedOperationException` - for authorization issues
- **Files Added**:
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/exception/ResourceNotFoundException.java`
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/exception/DuplicateResourceException.java`
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/exception/BusinessLogicException.java`
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/exception/UnauthorizedOperationException.java`

### 3. Global Exception Handler Enhancement
- **Issue**: Missing handlers for custom exceptions
- **Fix**: Added exception handlers for all custom exception types in `GlobalExceptionHandler.java`
- **Files Modified**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/controller/GlobalExceptionHandler.java`

### 4. Service Layer Improvements
- **Issue**: Poor error messages and generic exception usage in service classes
- **Fix**: Updated service classes to use custom exceptions with descriptive error messages:
  - `BorrowService.java` - Updated all RuntimeException usage to appropriate custom exceptions
  - `UserService.java` - Improved error handling for user operations
  - `BookService.java` - Enhanced error handling for book operations
- **Files Modified**:
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/BorrowService.java`
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/UserService.java`
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/BookService.java`

### 5. Maven Configuration
- **Issue**: Invalid XML tag in `pom.xml` (`<n>backend</n>` should be `<name>backend</name>`)
- **Status**: Attempted fix (persistent encoding display issue, may require manual verification)
- **Files Modified**: `backend/pom.xml`

## Benefits

1. **Better Error Handling**: Custom exceptions provide more specific error information and better HTTP status codes
2. **Improved Maintainability**: Clearer error messages and proper exception hierarchies
3. **Better Monitoring**: Proper logging instead of console output
4. **Enhanced API Responses**: More meaningful error responses for frontend/API consumers
5. **Code Quality**: Follows Spring Boot best practices for exception handling

## Testing Recommendations

- Test API endpoints to verify proper HTTP status codes are returned for different error scenarios
- Verify that logging appears correctly in application logs
- Test duplicate resource creation scenarios
- Test resource not found scenarios
- Test business logic violations (e.g., borrowing unavailable books)

## Future Improvements

- Implement proper notification service for the TODO items in `BorrowService`
- Complete reservation functionality
- Add more specific validation annotations where needed
- Consider adding request/response DTOs validation