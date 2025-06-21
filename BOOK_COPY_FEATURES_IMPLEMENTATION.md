# Book Copy Features Implementation

This document provides a comprehensive overview of the Book Copy related features implemented in the frontend of the Online Library Management System.

## Overview

The Book Copy management system allows for detailed tracking and management of individual copies of books in the library. Each book can have multiple physical copies, each with its own status, barcode, and maintenance history.

## Features Implemented

### 1. Enhanced Type Definitions

**File:** `frontend/src/lib/api/types.ts`

- **BookCopy Interface**: Complete type definition matching the backend model
  - `id`, `book`, `barcode`, `status`, `purchasePrice`, `purchaseTime`, `lastMaintenance`, `createTime`, `updateTime`
- **BookCopyStatus**: Type union for copy statuses
  - `AVAILABLE`, `BORROWED`, `MAINTENANCE`, `SCRAPPED`, `DISCARDED`
- **BookCopyCreateDto**: Data transfer object for creating new copies
- **BookCopyUpdateDto**: Data transfer object for updating existing copies

### 2. Book Copy Service

**File:** `frontend/src/lib/api/services/book-copies.ts`

- **API Methods**:
  - `getCopiesByBookId(bookId)`: Retrieve all copies for a specific book
  - `createCopy(copyData)`: Create new book copy (future implementation)
  - `updateCopy(copyId, copyData)`: Update copy details (future implementation)
  - `deleteCopy(copyId)`: Delete a copy (future implementation)
  - `updateCopyStatus(copyId, status)`: Update copy status (future implementation)
  - `getCopyById(copyId)`: Get individual copy details (future implementation)

- **Helper Methods**:
  - `getStatusColor(status)`: Get text color for status display
  - `getStatusBadgeColor(status)`: Get badge styling for status
  - `formatStatus(status)`: Format status for user-friendly display

### 3. Book Copy Components

#### BookCopyCard Component
**File:** `frontend/src/components/BookCopyCard.vue`

- **Features**:
  - Displays individual copy information with status badge
  - Shows barcode, purchase details, maintenance history
  - Provides action buttons for borrow, return, maintenance, and edit
  - Responsive design with hover effects
  - Admin-only actions when appropriate

- **Props**:
  - `copy`: BookCopy object
  - `showActions`: Boolean to control action button visibility

- **Events**:
  - `borrow`, `return`, `maintenance`, `edit`: Copy management actions

#### BookCopyList Component
**File:** `frontend/src/components/BookCopyList.vue`

- **Features**:
  - Grid display of multiple book copies
  - Real-time filtering by status
  - Sorting options (ID, barcode, status, dates)
  - Statistics display (available, borrowed, maintenance counts)
  - Loading and empty state handling
  - Responsive grid layout

- **Filtering & Sorting**:
  - Status filter: All, Available, Borrowed, Maintenance, Scrapped, Discarded
  - Sort by: Copy ID, Barcode, Status, Created Date, Purchase Date
  - Ascending/Descending order toggle

### 4. Book Copy Management View

**File:** `frontend/src/views/BookCopiesView.vue`

- **Features**:
  - Comprehensive copy management interface
  - Breadcrumb navigation
  - Admin-only copy creation and editing
  - Integration with existing book data
  - Error handling and loading states

- **Functionality**:
  - View all copies for a specific book
  - Create new copies (admin only)
  - Edit existing copies (admin only)
  - Perform copy actions (borrow, return, maintenance)
  - Real-time status updates

- **Dialog Forms**:
  - Create Copy Dialog: Barcode, status, purchase price
  - Edit Copy Dialog: Update copy details

### 5. Integration with Existing Features

#### Book Detail View Enhancement
**File:** `frontend/src/views/BookDetailView.vue`

- **Added Features**:
  - "View Copies" button in action buttons section
  - Direct navigation to book copy management
  - Integration with existing borrow functionality

#### Router Configuration
**File:** `frontend/src/router/index.ts`

- **New Route**: `/books/:id/copies`
  - Component: `BookCopiesView.vue`
  - Props enabled for book ID parameter

#### API Service Integration
**File:** `frontend/src/lib/api/index.ts`

- **Exports**:
  - `bookCopiesService`: Service instance
  - `BookCopiesService`: Service class
  - Integration with unified API object

## User Interface Features

### Status Management
- **Visual Status Indicators**: Color-coded badges for each status
- **Status Colors**:
  - Available: Green
  - Borrowed: Blue  
  - Maintenance: Yellow
  - Scrapped/Discarded: Red

### Responsive Design
- **Mobile-First**: Responsive grid layouts
- **Adaptive Actions**: Action buttons adjust based on screen size
- **Touch-Friendly**: Properly sized touch targets

### Accessibility
- **Screen Reader Support**: Proper ARIA labels and semantic HTML
- **Keyboard Navigation**: All interactive elements accessible via keyboard
- **Color Contrast**: High contrast colors for status indicators

## Future Backend Integration

The implementation is designed to easily integrate with future backend endpoints:

### Planned Backend Endpoints
- `POST /api/v1/book-copies`: Create new copy
- `PUT /api/v1/book-copies/{id}`: Update copy
- `DELETE /api/v1/book-copies/{id}`: Delete copy
- `PATCH /api/v1/book-copies/{id}/status`: Update status
- `GET /api/v1/book-copies/{id}`: Get copy details

### Error Handling
- **Graceful Degradation**: Features show appropriate error messages when backend endpoints are not available
- **User Feedback**: Toast notifications for all operations
- **Retry Mechanisms**: Built-in retry for failed operations

## Security & Permissions

### Role-Based Access
- **Regular Users**: Can view copies and borrow available ones
- **Admin Users**: Full CRUD operations on copies
- **Conditional UI**: Admin-only features hidden from regular users

### Data Validation
- **Frontend Validation**: Form validation before API calls
- **Error Handling**: Comprehensive error handling with user feedback
- **Input Sanitization**: Proper input handling and validation

## Performance Optimizations

### Efficient Data Loading
- **Parallel Requests**: Book and copy data loaded simultaneously
- **Lazy Loading**: Components loaded on demand
- **Caching**: Browser caching for static assets

### User Experience
- **Loading States**: Skeleton loaders during data fetching
- **Optimistic Updates**: Immediate UI feedback with rollback on errors
- **Responsive Interactions**: Immediate visual feedback for user actions

## Testing Considerations

### Component Testing
- **Unit Tests**: Individual component functionality
- **Integration Tests**: Component interaction testing
- **E2E Tests**: Full user workflow testing

### Accessibility Testing
- **Screen Reader Testing**: NVDA/JAWS compatibility
- **Keyboard Navigation**: Tab order and focus management
- **Color Contrast**: WCAG compliance verification

## Installation & Usage

### Prerequisites
- Vue 3 application with existing library management setup
- Lucide Vue Next for icons
- Sonner for toast notifications
- Tailwind CSS for styling

### Integration Steps
1. Import and register components in your Vue application
2. Add the new route to your router configuration
3. Update existing book detail views to include copy management links
4. Configure backend endpoints when available

### Example Usage

```vue
<!-- Using the BookCopyList component -->
<BookCopyList 
  :copies="bookCopies"
  :loading="isLoading"
  @borrow="handleBorrow"
  @edit="handleEdit"
/>

<!-- Using the BookCopyCard component -->
<BookCopyCard 
  :copy="singleCopy"
  :show-actions="true"
  @borrow="handleBorrow"
/>
```

## Conclusion

The Book Copy features provide a comprehensive solution for managing individual book copies in the library system. The implementation is designed for scalability, maintainability, and excellent user experience while maintaining backward compatibility with existing features.

The modular design allows for easy extension and integration with additional features as the system grows.