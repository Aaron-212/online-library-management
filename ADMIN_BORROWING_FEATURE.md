# Admin Borrowing Management Feature

## Overview
This feature adds comprehensive admin functionality for registering and managing book borrowing on behalf of users. Admins can now register borrowing records for any user in the system through a dedicated admin interface.

## Backend Changes

### 1. User Controller Enhancements
- **File**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/controller/UserController.java`
- **New Endpoint**: `GET /api/v1/users/all` (Admin only)
- Allows admins to retrieve paginated list of all users
- Supports search functionality by username or email

### 2. User Service & Repository Updates
- **Files**: 
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/UserService.java`
  - `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/repository/UserRepository.java`
- **New Methods**:
  - `findAllUsers(Pageable)` - Get all users with pagination
  - `searchUsers(String, Pageable)` - Search users by username/email

### 3. Borrow Controller Enhancements
- **File**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/controller/BorrowController.java`
- **New Endpoints**:
  - `POST /api/v1/borrow/admin/borrow` (Admin only) - Create borrow record for any user
  - `GET /api/v1/borrow/admin/all` (Admin only) - Get all borrowing records

### 4. Borrow Service Updates
- **File**: `backend/src/main/java/com/aaron212/onlinelibrarymanagement/backend/service/BorrowService.java`
- **New Method**: `getAllBorrowings(int page, int size)` - Get paginated borrowing records

## Frontend Changes

### 1. API Services Updates
- **Files**:
  - `frontend/src/lib/api/services/users.ts` - Added `getAllUsers()` method
  - `frontend/src/lib/api/services/borrow.ts` - Added admin methods for borrowing management
  - `frontend/src/lib/api/types.ts` - Updated `BorrowRequestDto` and `BorrowResponseDto` types

### 2. New Admin View
- **File**: `frontend/src/views/AdminBorrowingView.vue`
- **Features**:
  - User search and selection with autocomplete
  - Book search and selection with availability checking
  - Book copy selection for available copies
  - Comprehensive borrowing records management
  - Filtering by status (Active, Returned, Overdue)
  - Real-time search across all borrowing records
  - Admin dashboard with borrowing statistics

### 3. Navigation Updates
- **Files**:
  - `frontend/src/router/index.ts` - Added `/admin/borrowing` route
  - `frontend/src/components/AppSidebar.vue` - Added "Borrowing Management" to admin navigation

## Key Features

### Admin Borrowing Registration
1. **User Selection**: Admins can search and select any user from the system
2. **Book Selection**: Search books by title, author, or ISBN
3. **Copy Selection**: Choose from available book copies
4. **Validation**: Ensures only available copies can be borrowed
5. **Real-time Feedback**: Shows success/error messages for all operations

### Comprehensive Borrowing Management
1. **Statistics Dashboard**: Shows total borrows, active borrows, and overdue books
2. **Advanced Filtering**: Filter by status and search across multiple fields
3. **Status Badges**: Visual indicators for borrowed, returned, overdue, and due soon books
4. **Interactive Records**: Click on books to view details, users to view profiles
5. **Real-time Updates**: Automatically refreshes data after operations

### Security & Authorization
- All admin endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`
- Frontend routes require admin authentication
- Proper error handling and user feedback

## Usage Instructions

### For Admins:
1. Navigate to "Borrowing Management" in the admin section of the sidebar
2. Click "Register Borrow" to create a new borrowing record
3. Search and select a user
4. Search and select a book
5. Choose an available copy
6. Click "Register Borrow" to complete the process

### API Endpoints:
- `GET /api/v1/users/all?page=0&size=10&search=keyword` - Get users (admin)
- `POST /api/v1/borrow/admin/borrow` - Create borrow record (admin)
- `GET /api/v1/borrow/admin/all?page=0&size=10` - Get all borrows (admin)

## Technical Details

### Authentication Flow:
1. User must be authenticated and have ADMIN role
2. Frontend checks user role before allowing access to admin routes
3. Backend validates admin privileges on all admin endpoints

### Data Flow:
1. Admin selects user → API fetches user list
2. Admin selects book → API fetches book copies
3. Admin submits form → API creates borrow record
4. Success → UI refreshes borrowing list

### Error Handling:
- Comprehensive error messages for failed operations
- Validation for required fields and business rules
- Graceful handling of network errors and API failures

## Future Enhancements
- Bulk borrowing operations
- Borrowing history export
- Advanced reporting and analytics
- Email notifications for borrowing events
- Borrowing approvals workflow