# Security Fix: Unauthorized Book Return Vulnerability

## Issue Description
**Severity**: HIGH
**Type**: Authorization Bypass
**CVE**: Internal-2024-001

A critical security vulnerability was discovered in the book return functionality that allowed any authenticated user to return books borrowed by other users.

## Root Cause
The backend's `POST /api/v1/borrow/return` endpoint accepted a `BorrowRequestDto` containing both `userId` and `copyId` without validating that the authenticated user matched the `userId` in the request body.

### Vulnerable Code
```java
@PostMapping("/return")
public ResponseEntity<?> returnBook(@Valid @RequestBody BorrowRequestDto requestDto) {
    // VULNERABLE: No validation that authenticated user matches requestDto.userId()
    Borrow borrow = borrowService.returnBook(requestDto.userId(), requestDto.copyId());
    // ... rest of method
}
```

## Attack Scenario
1. User A borrows a book (Copy ID: 123)
2. User B (malicious) discovers the API endpoint
3. User B sends a return request with `{"userId": A's ID, "copyId": 123}`
4. The book is returned on behalf of User A without authorization

## Fix Implementation

### Backend Security Enhancements

1. **Authentication Validation**: Added proper authentication checks to all user-facing endpoints
2. **Authorization Validation**: Added validation to ensure authenticated user matches the requested operation
3. **Secure Endpoint Prioritization**: Promoted the secure `PUT /{borrowId}/return` endpoint over the vulnerable `POST /return`

#### Fixed Code
```java
@PostMapping("/return")
public ResponseEntity<?> returnBook(@Valid @RequestBody BorrowRequestDto requestDto, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
    }

    // Get the authenticated user's ID
    Long authenticatedUserId = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
    
    // Security check: Ensure the request is for the authenticated user's borrowing
    if (!authenticatedUserId.equals(requestDto.userId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "无权限操作其他用户的借阅记录"));
    }
    
    // ... rest of method
}
```

### Frontend Security Enhancements

1. **Secure API Usage**: Updated frontend to use the secure `PUT /{borrowId}/return` endpoint by default
2. **UI-Level Validation**: Added `isUserBorrowed` prop to BookCopyCard to only show return buttons for user's own books
3. **Borrow Record Validation**: Added validation to ensure users can only return books they actually borrowed

#### Key Changes
- Updated `BorrowService.returnBook()` to use the secure endpoint
- Added `userBorrowedCopies` state management to track which copies belong to the current user
- Enhanced BookCopyCard component to only show return buttons for user's own books

### Additional Security Measures

1. **Consistent Pattern**: Applied the same security pattern to the renewal endpoint
2. **Backward Compatibility**: Maintained legacy endpoint functionality while adding security
3. **Error Handling**: Implemented proper error responses for authorization failures

## Security Testing Recommendations

1. **Penetration Testing**: Test all borrow/return endpoints with different user contexts
2. **Authorization Matrix Testing**: Verify that users can only access their own resources
3. **API Fuzzing**: Test endpoints with malformed or malicious payloads
4. **Session Management**: Verify proper session handling and token validation

## Deployment Notes

1. All existing functionality remains compatible
2. No database schema changes required
3. Frontend changes are backward compatible
4. Existing authenticated users will see improved security without disruption

## Monitoring and Alerting

Consider implementing:
1. Failed authorization attempt logging
2. Suspicious API usage pattern detection
3. User behavior anomaly detection
4. Security metrics dashboard

## Conclusion

This fix addresses a critical authorization bypass vulnerability by implementing proper user validation at both the API and UI levels. The solution maintains backward compatibility while significantly improving security posture.