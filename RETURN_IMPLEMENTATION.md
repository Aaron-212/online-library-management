# Return Borrowed Book Copy Implementation

## Overview
This document describes the implementation of the return borrowed book copy functionality for the Online Library Management System.

## Backend Implementation

### Existing Functionality
The backend return functionality was already fully implemented in the `BorrowService` and `BorrowController` classes:

#### BorrowService.returnBook()
- **Location**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/BorrowService.java`
- **Method**: `public Borrow returnBook(Long userId, Long copyId)`
- **Features**:
  - Finds active borrow record for the user and copy
  - Sets the actual return time
  - Checks if the book is overdue and calculates fines
  - Updates book copy status to AVAILABLE
  - Processes next reservation in queue if any
  - Returns updated borrow record

#### BorrowController Return Endpoints
- **Location**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/controller/BorrowController.java`

##### User Return Endpoint
- **Endpoint**: `POST /api/v1/borrow/return`
- **Request**: `BorrowRequestDto` with `userId` and `copyId`
- **Response**: `BorrowResponseDto` with success message and updated borrow details
- **Security**: Authenticated users only

##### Admin Return Endpoint (Added)
- **Endpoint**: `POST /api/v1/borrow/admin/return`
- **Request**: `BorrowRequestDto` with `userId` and `copyId`
- **Response**: `BorrowResponseDto` with admin-specific message
- **Security**: Admin role required

### Return Business Logic
1. **Validation**: Ensures active borrow record exists for the user and copy
2. **Return Processing**: Sets actual return time to current timestamp
3. **Overdue Handling**: 
   - Checks if return is past due date
   - Calculates fines based on configurable rate per day
   - Updates status to OVERDUE if late
4. **Book Copy Management**: Updates copy status to AVAILABLE
5. **Reservation Processing**: Automatically processes next user in reservation queue
6. **Fee Calculation**: Applies configurable late fees for overdue returns

## Frontend Implementation

### API Service Updates
Updated `frontend/src/lib/api/services/borrow.ts` to properly integrate with backend:

#### Main Methods
- **`returnBookDirect(request: BorrowRequestDto)`**: Direct backend integration
- **`renewBookDirect(request: BorrowRequestDto)`**: Direct backend integration

#### Convenience Methods
- **`returnBook(borrowId: number)`**: Accepts borrow ID, fetches details, calls backend
- **`renewBook(borrowId: number)`**: Accepts borrow ID, fetches details, calls backend

#### Data Retrieval Methods
- **`getMyCurrentBorrowings()`**: Gets active borrowings (`/my-current`)
- **`getMyBorrowHistory()`**: Gets complete history (`/my-history`)
- **`getUserBorrows()`**: Legacy method with simulated pagination

#### Admin Methods
- **`adminReturnBook(request: BorrowRequestDto)`**: Admin return functionality
- **`adminRenewBook(request: BorrowRequestDto)`**: Admin renew functionality

### Frontend Component Integration
The existing Vue components already use the return functionality:

#### BorrowingManagementView.vue
- Displays user's borrowing history and current loans
- Provides return buttons for active borrows
- Shows overdue status and calculated fees
- Calls `borrowService.returnBook(borrowId)`

#### ApiExample.vue
- Demonstrates API usage with return functionality
- Shows current borrows with return/renew buttons
- Handles success/error states

## API Endpoints Summary

### User Endpoints
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/api/v1/borrow/return` | Return a book | `BorrowRequestDto` | `BorrowResponseDto` |
| POST | `/api/v1/borrow/renew` | Renew a book | `BorrowRequestDto` | `BorrowResponseDto` |
| GET | `/api/v1/borrow/my-current` | Get current borrows | - | `List<BorrowDto>` |
| GET | `/api/v1/borrow/my-history` | Get borrow history | - | `List<BorrowDto>` |

### Admin Endpoints
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/api/v1/borrow/admin/return` | Admin return book | `BorrowRequestDto` | `BorrowResponseDto` |
| POST | `/api/v1/borrow/admin/renew` | Admin renew book | `BorrowRequestDto` | `BorrowResponseDto` |
| GET | `/api/v1/borrow/admin/all` | Get all borrows | pagination params | `List<BorrowDto>` |

## Data Transfer Objects

### BorrowRequestDto
```java
public record BorrowRequestDto(
    @NotNull @Positive Long userId,
    @NotNull @Positive Long copyId
) {}
```

### BorrowResponseDto
```java
public record BorrowResponseDto(
    Long borrowId,
    Long userId,
    String username,
    Long copyId,
    String bookTitle,
    String isbn,
    LocalDateTime borrowTime,
    LocalDateTime returnTime,
    Borrow.Status status,
    String message
) {}
```

### BorrowDto
```java
public record BorrowDto(
    Long borrowId,
    Long userId,
    String username,
    Long copyId,
    String bookTitle,
    String isbn,
    LocalDateTime borrowTime,
    LocalDateTime returnTime,
    LocalDateTime actualReturnTime,
    Borrow.Status status,
    BigDecimal fine
) {}
```

## Features Implemented

### ✅ Core Return Functionality
- Return borrowed book copies
- Update book copy availability
- Calculate late fees for overdue returns
- Process reservation queue automatically

### ✅ Frontend Integration
- Vue.js components with return buttons
- Error handling and success messages
- Backward compatibility with existing code
- Loading states and user feedback

### ✅ Admin Capabilities
- Admin can return books on behalf of users
- Admin can view all borrowing records
- Proper authorization and security

### ✅ Business Logic
- Configurable borrowing rules integration
- Automatic fine calculation
- Status management (BORROWED → RETURNED/OVERDUE)
- Reservation queue processing

## Configuration

The system uses configurable borrowing rules:
- **Fine per day**: Configurable rate for overdue books
- **Loan period**: Default borrowing period in days
- **Renewal settings**: Whether renewals are allowed and for how long

## Security

- **Authentication**: All endpoints require valid JWT token
- **Authorization**: Admin endpoints require ADMIN role
- **User isolation**: Users can only return their own books (except admins)
- **Input validation**: All inputs are validated using Bean Validation

## Testing

The implementation includes:
- Backend unit tests for service layer
- Integration tests for controller endpoints
- Frontend component tests
- API integration tests

## Error Handling

Comprehensive error handling for:
- Invalid borrow records
- Unauthorized access attempts
- Book not found scenarios
- System errors with appropriate HTTP status codes

## Future Enhancements

Potential improvements:
1. **Batch Returns**: Allow returning multiple books at once
2. **Return Notifications**: Email/SMS notifications on successful returns
3. **Return History Export**: Export functionality for return records
4. **Mobile App Integration**: API ready for mobile applications
5. **QR Code Returns**: Quick return using book copy barcodes