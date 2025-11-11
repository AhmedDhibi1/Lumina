# Auth Service API Documentation

## Overview

The Auth Service is a secure authentication and authorization microservice that integrates with Keycloak to provide user management and JWT-based authentication. It provides a complete set of endpoints for user registration, login, token management, and profile operations.

## Base URL

```
Development: http://localhost:8081
```

## Key Features

- ✅ Email-based authentication
- ✅ User registration and profile management
- ✅ JWT token management with HTTP-only cookies
- ✅ Password reset workflow
- ✅ Token refresh mechanism
- ✅ Comprehensive error handling with error codes
- ✅ CORS support for frontend integration
- ✅ Keycloak integration

## Authentication Flow

1. **Registration**: User creates an account with email and password
2. **Login**: User authenticates with email and password
3. **Token Storage**: Access and refresh tokens stored in HTTP-only cookies
4. **Token Refresh**: Automatic token refresh using refresh token
5. **Logout**: Token revocation and cookie cleanup

## API Endpoints

### Authentication Endpoints

#### 1. User Registration

Register a new user account.

**Endpoint**: `POST /auth/register`

**Access**: Public

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Validation Rules**:
- `email`: Required, must be valid email format
- `password`: Required, minimum 8 characters
- `firstName`: Required, 1-50 characters
- `lastName`: Required, 1-50 characters

**Success Response** (201 Created):
```json
{
  "success": true,
  "message": "Registration successful! Your account has been created. Please login with your email and password.",
  "data": null,
  "timestamp": "2025-11-11T18:00:00"
}
```

**Error Responses**:

*User Already Exists* (400):
```json
{
  "success": false,
  "message": "Registration failed",
  "data": {
    "errorCode": "USER_EXISTS",
    "errorMessage": "User with this email already exists."
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

*Validation Error* (400):
```json
{
  "success": false,
  "message": "Validation failed. Please check your input.",
  "data": {
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters",
    "errorCode": "VALIDATION_ERROR"
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

---

#### 2. User Login

Authenticate a user and receive JWT tokens.

**Endpoint**: `POST /auth/login`

**Access**: Public

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Login successful. Welcome back!",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "username": "user@example.com",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "emailVerified": false
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

**Cookies Set**:
- `accessToken`: HTTP-only, 15 minutes expiry
- `refreshToken`: HTTP-only, 10 hours expiry

**Error Responses**:

*Invalid Credentials* (401):
```json
{
  "success": false,
  "message": "Authentication failed",
  "data": {
    "errorCode": "INVALID_CREDENTIALS",
    "errorMessage": "Invalid email or password. Please check your credentials."
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

---

#### 3. Refresh Token

Get a new access token using the refresh token.

**Endpoint**: `POST /auth/refresh`

**Access**: Public (requires refresh token cookie)

**Request**: No body required

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "username": "user@example.com",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "emailVerified": false
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

**New Cookies Set**:
- `accessToken`: Updated with new token
- `refreshToken`: Updated with new token

**Error Response** (401):
```json
{
  "success": false,
  "message": "Authentication failed",
  "data": {
    "errorCode": "TOKEN_REFRESH_ERROR",
    "errorMessage": "Token refresh failed: Invalid or expired refresh token"
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

---

#### 4. Logout

Revoke tokens and clear cookies.

**Endpoint**: `POST /auth/logout`

**Access**: Authenticated (requires access token cookie)

**Request**: No body required

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2025-11-11T18:00:00"
}
```

**Cookies Cleared**:
- `accessToken`: Removed
- `refreshToken`: Removed

---

### User Profile Endpoints

#### 5. Get User Profile

Retrieve the current user's profile information.

**Endpoint**: `GET /user/profile`

**Access**: Authenticated (requires access token cookie)

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "username": "user@example.com",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "emailVerified": false
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

---

#### 6. Update User Profile

Update user profile information (email, first name, last name).

**Endpoint**: `PUT /user/profile`

**Access**: Authenticated (requires access token cookie)

**Request Body**:
```json
{
  "email": "newemail@example.com",
  "firstName": "Jane",
  "lastName": "Smith"
}
```

**Note**: All fields are optional. Only include fields you want to update.

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "username": "newemail@example.com",
    "email": "newemail@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "emailVerified": false
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

**Error Response** - Email Already Exists (401):
```json
{
  "success": false,
  "message": "Authentication failed",
  "data": {
    "errorCode": "EMAIL_ALREADY_EXISTS",
    "errorMessage": "Email is already in use"
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

---

#### 7. Change Password

Change the user's password (requires current password verification).

**Endpoint**: `POST /user/change-password`

**Access**: Authenticated (requires access token cookie)

**Request Body**:
```json
{
  "currentPassword": "OldPassword123",
  "newPassword": "NewSecurePassword456"
}
```

**Validation Rules**:
- `currentPassword`: Required
- `newPassword`: Required, minimum 8 characters

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Password changed successfully. Please login with your new password.",
  "data": null,
  "timestamp": "2025-11-11T18:00:00"
}
```

**Error Response** - Invalid Current Password (401):
```json
{
  "success": false,
  "message": "Authentication failed",
  "data": {
    "errorCode": "INVALID_CURRENT_PASSWORD",
    "errorMessage": "Current password is incorrect"
  },
  "timestamp": "2025-11-11T18:00:00"
}
```

---

#### 8. Request Password Reset

Request a password reset email.

**Endpoint**: `POST /user/reset-password`

**Access**: Public

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "If an account exists with this email, a password reset link has been sent.",
  "data": null,
  "timestamp": "2025-11-11T18:00:00"
}
```

**Note**: For security reasons, the response is the same whether the email exists or not (prevents email enumeration attacks).

---

## Error Codes

| Error Code | Description | HTTP Status |
|------------|-------------|-------------|
| `AUTH_ERROR` | General authentication error | 401 |
| `INVALID_CREDENTIALS` | Invalid email or password | 401 |
| `LOGIN_FAILED` | Login attempt failed | 401 |
| `TOKEN_REFRESH_ERROR` | Token refresh failed | 401 |
| `USER_INFO_ERROR` | Failed to retrieve user info | 401 |
| `INVALID_CURRENT_PASSWORD` | Current password is incorrect | 401 |
| `PROFILE_FETCH_ERROR` | Failed to fetch user profile | 401 |
| `PROFILE_UPDATE_ERROR` | Failed to update profile | 401 |
| `PASSWORD_CHANGE_ERROR` | Failed to change password | 401 |
| `EMAIL_ALREADY_EXISTS` | Email is already in use | 401 |
| `REGISTRATION_ERROR` | General registration error | 400 |
| `REGISTRATION_FAILED` | Registration attempt failed | 400 |
| `USER_EXISTS` | User already exists | 400 |
| `VALIDATION_ERROR` | Input validation failed | 400 |
| `INVALID_ARGUMENT` | Invalid input provided | 400 |
| `USER_FETCH_ERROR` | Failed to fetch user from Keycloak | 401 |
| `USER_UPDATE_ERROR` | Failed to update user in Keycloak | 401 |
| `PASSWORD_RESET_ERROR` | Failed to reset password | 401 |
| `PASSWORD_RESET_EMAIL_ERROR` | Failed to send reset email | 401 |
| `USER_NOT_FOUND` | User not found | 401 |
| `INTERNAL_ERROR` | Internal server error | 500 |

---

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `KEYCLOAK_CLIENT_SECRET` | Keycloak client secret | `your-client-secret-here` |

### Application Configuration

Key configuration values in `application-dev.yml`:

```yaml
server:
  port: 8081

keycloak:
  realm: lumina
  auth-server-url: http://localhost:8180
  resource: auth-service-client

jwt:
  cookie:
    access-token:
      max-age: 900  # 15 minutes
    refresh-token:
      max-age: 36000  # 10 hours

cors:
  allowed-origins: http://localhost:3000,http://localhost:3001
```

---

## Security Considerations

1. **HTTP-Only Cookies**: Tokens stored in HTTP-only cookies prevent XSS attacks
2. **CORS Configuration**: Properly configured CORS for frontend integration
3. **Secure Cookies**: Set `secure: true` in production for HTTPS
4. **Token Expiration**: Short-lived access tokens (15 min) with refresh mechanism
5. **Password Requirements**: Minimum 8 characters enforced
6. **Email Enumeration Prevention**: Password reset doesn't reveal if email exists
7. **Stateless Sessions**: No server-side session storage

---

## Health Check

**Endpoint**: `GET /actuator/health`

**Access**: Public

**Response**:
```json
{
  "status": "UP"
}
```

---

## Testing with cURL

### Register a User
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPassword123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "test@example.com",
    "password": "TestPassword123"
  }'
```

### Get Profile (using cookies from login)
```bash
curl -X GET http://localhost:8081/user/profile \
  -b cookies.txt
```

### Refresh Token
```bash
curl -X POST http://localhost:8081/auth/refresh \
  -b cookies.txt \
  -c cookies.txt
```

### Logout
```bash
curl -X POST http://localhost:8081/auth/logout \
  -b cookies.txt
```

---

## Integration with Frontend

### Example: React/TypeScript

```typescript
// Login function
async function login(email: string, password: string) {
  const response = await fetch('http://localhost:8081/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // Important: Include cookies
    body: JSON.stringify({ email, password }),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.data.errorMessage);
  }
  
  return await response.json();
}

// Get profile function
async function getProfile() {
  const response = await fetch('http://localhost:8081/user/profile', {
    method: 'GET',
    credentials: 'include', // Important: Include cookies
  });
  
  if (!response.ok) {
    throw new Error('Failed to fetch profile');
  }
  
  return await response.json();
}

// Refresh token function (call before token expires)
async function refreshToken() {
  const response = await fetch('http://localhost:8081/auth/refresh', {
    method: 'POST',
    credentials: 'include',
  });
  
  if (!response.ok) {
    // Redirect to login
    window.location.href = '/login';
  }
}
```

---

## Logging

The service provides comprehensive logging at various levels:

- **DEBUG**: Request/response details, token operations
- **INFO**: Successful operations, user actions
- **WARN**: Non-critical issues, security events
- **ERROR**: Failures, exceptions with stack traces

Log format: `[timestamp] [level] [logger] - message`

---

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Keycloak 23.0.0 (running on port 8180)

### Build
```bash
cd auth_service/auth-service
./mvnw clean package
```

### Run
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run Tests
```bash
./mvnw test
```

---

## Support

For issues or questions, please refer to the main project repository or contact the development team.
