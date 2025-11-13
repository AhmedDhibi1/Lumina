# Keycloak JWT Authentication & Authorization Implementation Plan

## Executive Summary

Comprehensive implementation plan for integrating Keycloak-based JWT authentication and authorization across a microservices architecture with two backend services: `auth_service` and `business_logic`.

**From:** 15-Year Experienced Java Senior Developer | DDD & Spring Boot Expert  
**Date:** November 10, 2025  
**Commit:** 42c60cb (feature/business_logic branch)  
**Implementation Time:** 2-3 weeks (40-60 hours)

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Service Responsibilities](#2-service-responsibilities)
3. [Authentication Flow](#3-authentication-flow)
4. [Technology Stack](#4-technology-stack)
5. [Keycloak Configuration](#5-keycloak-configuration)
6. [Auth Service Implementation](#6-auth-service-implementation)
7. [Business Logic Service Implementation](#7-business-logic-service-implementation)
8. [Security Best Practices](#8-security-best-practices)
9. [Implementation Roadmap](#9-implementation-roadmap)
10. [Testing Strategy](#10-testing-strategy)
11. [Deployment Considerations](#11-deployment-considerations)
12. [Troubleshooting Guide](#12-troubleshooting-guide)

---

## 1. Architecture Overview

### 1.1 High-Level Architecture

```
┌──────────────┐
│   Frontend   │
│  (React/Vue) │
└──────┬───────┘
       │
       ├─────────────────────────────────┐
       │                                 │
       │ Login/Register                  │ Business Requests
       │ (username, password)            │ (JWT in Cookie)
       │                                 │
       ▼                                 ▼
┌──────────────────┐            ┌─────────────────────┐
│  Auth Service    │            │ Business Logic      │
│  Port: 8081      │            │ Service             │
│                  │            │ Port: 8080          │
│  ┌────────────┐  │            │                     │
│  │  Login     │  │            │  ┌───────────────┐  │
│  │  Register  │  │            │  │ JWT Filter    │  │
│  │  Refresh   │  │            │  │ (Validate)    │  │
│  │  Logout    │  │            │  └───────┬───────┘  │
│  └────────────┘  │            │          │          │
└────────┬─────────┘            │          ▼          │
         │                      │  ┌───────────────┐  │
         │                      │  │ Extract User  │  │
         │                      │  │ from JWT      │  │
         │                      │  └───────┬───────┘  │
         │                      │          │          │
         ▼                      │          ▼          │
┌─────────────────┐             │  ┌───────────────┐  │
│    Keycloak     │◄────────────┤  │ Business      │  │
│  Identity &     │  Validate   │  │ Logic Layer   │  │
│  Access Mgmt    │  Token      │  └───────────────┘  │
│                 │             └─────────────────────┘
│ Port: 8180      │
└─────────────────┘
```

### 1.2 Communication Flow

**Authentication Flow:**
```
Frontend → Auth Service → Keycloak → Auth Service → Frontend (JWT in cookie)
```

**Business Request Flow:**
```
Frontend (JWT) → Business Logic Service → Validate JWT → Extract User → Execute Logic
```

---

## 2. Service Responsibilities

### 2.1 Auth Service (`auth_service`)

**Primary Responsibility:** Authentication & Authorization Gateway

**Responsibilities:**
- ✅ Handle user login requests
- ✅ Handle user registration requests
- ✅ Integrate with Keycloak for token issuance
- ✅ Refresh access tokens using refresh tokens
- ✅ Handle logout and token revocation
- ✅ Return tokens to frontend in HTTP-only cookies
- ✅ Handle password reset workflows
- ✅ Provide user profile endpoints

**Does NOT:**
- ❌ Handle business logic
- ❌ Access business domain entities
- ❌ Interact with business database

**Port:** 8081  
**Base Path:** `/auth`

### 2.2 Business Logic Service (`business_logic`)

**Primary Responsibility:** Core Business Logic Execution

**Responsibilities:**
- ✅ Validate JWT on every incoming request
- ✅ Extract user information from validated JWT
- ✅ Execute business logic (Create Workspace, etc.)
- ✅ Enforce authorization based on roles/permissions
- ✅ Access domain layer and infrastructure
- ✅ Return business responses

**Does NOT:**
- ❌ Issue or refresh tokens
- ❌ Handle login/registration
- ❌ Directly interact with Keycloak for authentication

**Port:** 8080  
**Base Path:** `/api/v1`

---

## 3. Authentication Flow

### 3.1 Login Flow

```
Step 1: User Submits Credentials
┌──────────┐
│ Frontend │ POST /auth/login
│          │ { username, password }
└────┬─────┘
     │
     ▼
┌────────────────┐
│ Auth Service   │
│ LoginController│
└────┬───────────┘
     │
     ▼
┌────────────────┐
│ Keycloak Client│ POST /realms/{realm}/protocol/openid-connect/token
│                │ grant_type=password
└────┬───────────┘
     │
     ▼
┌────────────────┐
│   Keycloak     │ Authenticate user
│                │ Generate tokens
└────┬───────────┘
     │
     │ { access_token, refresh_token, expires_in }
     ▼
┌────────────────┐
│ Auth Service   │ Set HTTP-only cookies
│                │ - accessToken
│                │ - refreshToken
└────┬───────────┘
     │
     ▼
┌──────────┐
│ Frontend │ Receives tokens in cookies
│          │ Stores user info in state
└──────────┘
```

### 3.2 Business Request Flow

```
Step 1: User Makes Business Request
┌──────────┐
│ Frontend │ POST /api/v1/workspaces
│          │ Cookie: accessToken=<jwt>
└────┬─────┘
     │
     ▼
┌────────────────────┐
│ Business Logic     │
│ JwtAuthFilter      │ Extract JWT from cookie
└────┬───────────────┘
     │
     ▼
┌────────────────────┐
│ JWT Validator      │ Validate with Keycloak public key
│                    │ or introspect endpoint
└────┬───────────────┘
     │
     │ Valid ✅
     ▼
┌────────────────────┐
│ Extract User Info  │ user_id, username, email, roles
│                    │ from JWT claims
└────┬───────────────┘
     │
     ▼
┌────────────────────┐
│ SecurityContext    │ Set authentication
│                    │ Principal: UserDetails
└────┬───────────────┘
     │
     ▼
┌────────────────────┐
│ Controller         │ @AuthenticationPrincipal UserDetails
│                    │ Execute business logic
└────┬───────────────┘
     │
     ▼
┌──────────┐
│ Response │ { success: true, data: {...} }
└──────────┘
```

### 3.3 Token Refresh Flow

```
┌──────────┐
│ Frontend │ POST /auth/refresh
│          │ Cookie: refreshToken=<jwt>
└────┬─────┘
     │
     ▼
┌────────────────┐
│ Auth Service   │
│ TokenController│
└────┬───────────┘
     │
     ▼
┌────────────────┐
│ Keycloak Client│ POST /token
│                │ grant_type=refresh_token
└────┬───────────┘
     │
     ▼
┌────────────────┐
│   Keycloak     │ Validate refresh token
│                │ Generate new access token
└────┬───────────┘
     │
     │ { access_token, refresh_token, expires_in }
     ▼
┌────────────────┐
│ Auth Service   │ Update cookies
└────┬───────────┘
     │
     ▼
┌──────────┐
│ Frontend │ Continue with new token
└──────────┘
```

---

## 4. Technology Stack

### 4.1 Auth Service

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Keycloak -->
    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-spring-boot-starter</artifactId>
        <version>23.0.0</version>
    </dependency>
    
    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-admin-client</artifactId>
        <version>23.0.0</version>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

### 4.2 Business Logic Service

```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- Keycloak Adapter for JWT Validation -->
    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-spring-boot-starter</artifactId>
        <version>23.0.0</version>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

---

## 5. Keycloak Configuration

### 5.1 Keycloak Setup

**Installation:**
```bash
# Using Docker
docker run -d \
  --name keycloak \
  -p 8180:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:23.0.0 \
  start-dev
```

**Access:** http://localhost:8180

### 5.2 Realm Configuration

**Realm Name:** `lumina`

**Create Realm:**
1. Login to Keycloak Admin Console
2. Click "Create Realm"
3. Name: `lumina`
4. Click "Create"

### 5.3 Client Configuration

#### 5.3.1 Auth Service Client

**Client ID:** `auth-service-client`  
**Client Type:** `confidential`  
**Valid Redirect URIs:** `http://localhost:8081/*`  
**Web Origins:** `http://localhost:3000` (Frontend)

**Settings:**
- Client authentication: ON
- Authorization: OFF
- Standard flow: ON
- Direct access grants: ON
- Service accounts: ON

**Advanced Settings:**
- Access Token Lifespan: 15 minutes
- Client Session Idle: 30 minutes
- Client Session Max: 10 hours

#### 5.3.2 Business Logic Client

**Client ID:** `business-logic-client`  
**Client Type:** `bearer-only` or `public`  
**Purpose:** JWT validation only

**Settings:**
- Client authentication: OFF (if public)
- Standard flow: OFF
- Direct access grants: OFF

### 5.4 Roles Configuration

**Realm Roles:**
- `ROLE_USER` - Standard user
- `ROLE_ADMIN` - Administrator
- `ROLE_WORKSPACE_OWNER` - Workspace owner
- `ROLE_WORKSPACE_MEMBER` - Workspace member

**Client Roles (business-logic-client):**
- `CREATE_WORKSPACE`
- `DELETE_WORKSPACE`
- `MANAGE_MEMBERS`

### 5.5 User Attributes

**Standard Claims:**
- `sub` (Subject - User ID)
- `preferred_username` (Username)
- `email`
- `email_verified`
- `given_name`
- `family_name`

**Custom Claims (Mappers):**
- `user_id` → `sub`
- `roles` → realm_access.roles + resource_access.{client}.roles

---

## 6. Auth Service Implementation

### 6.1 Project Structure

```
auth_service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── lumina/
│       │           └── auth/
│       │               ├── AuthServiceApplication.java
│       │               ├── config/
│       │               │   ├── KeycloakConfig.java
│       │               │   ├── SecurityConfig.java
│       │               │   └── CorsConfig.java
│       │               ├── controller/
│       │               │   ├── AuthController.java
│       │               │   ├── TokenController.java
│       │               │   └── UserController.java
│       │               ├── dto/
│       │               │   ├── LoginRequest.java
│       │               │   ├── RegisterRequest.java
│       │               │   ├── TokenResponse.java
│       │               │   └── UserInfoResponse.java
│       │               ├── service/
│       │               │   ├── AuthService.java
│       │               │   ├── KeycloakService.java
│       │               │   └── TokenService.java
│       │               ├── exception/
│       │               │   ├── AuthenticationException.java
│       │               │   ├── RegistrationException.java
│       │               │   └── GlobalExceptionHandler.java
│       │               └── util/
│       │                   └── CookieUtil.java
│       └── resources/
│           ├── application.yml
│           └── application-dev.yml
└── pom.xml
```

### 6.2 Configuration (application.yml)

```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service

keycloak:
  realm: lumina
  auth-server-url: http://localhost:8180
  resource: auth-service-client
  credentials:
    secret: ${KEYCLOAK_CLIENT_SECRET:your-client-secret-here}
  ssl-required: external
  public-client: false
  confidential-port: 0
  use-resource-role-mappings: false

jwt:
  cookie:
    access-token:
      name: accessToken
      max-age: 900  # 15 minutes
      http-only: true
      secure: false  # true in production
      same-site: Strict
    refresh-token:
      name: refreshToken
      max-age: 36000  # 10 hours
      http-only: true
      secure: false  # true in production
      same-site: Strict

cors:
  allowed-origins: http://localhost:3000
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true

logging:
  level:
    com.lumina.auth: DEBUG
    org.keycloak: INFO
```

### 6.3 Keycloak Configuration

```java
package com.lumina.auth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}
```

### 6.4 Security Configuration

```java
package com.lumina.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless API
            .cors(cors -> {})  // Use CorsConfig
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register", "/auth/refresh").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
```

### 6.5 DTOs

#### LoginRequest.java
```java
package com.lumina.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginRequest {
    @NotBlank(message = "Username is required")
    String username;

    @NotBlank(message = "Password is required")
    String password;
}
```

#### RegisterRequest.java
```java
package com.lumina.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;

    String firstName;
    String lastName;
}
```

#### TokenResponse.java
```java
package com.lumina.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    Integer expiresIn;
    Integer refreshExpiresIn;
}
```

### 6.6 Auth Controller

```java
package com.lumina.auth.controller;

import com.lumina.auth.dto.*;
import com.lumina.auth.service.AuthService;
import com.lumina.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserInfoResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        log.info("Login attempt for user: {}", request.getUsername());
        
        try {
            TokenResponse tokenResponse = authService.login(request);
            
            // Set tokens in HTTP-only cookies
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());
            
            // Get user info
            UserInfoResponse userInfo = authService.getUserInfo(tokenResponse.getAccessToken());
            
            log.info("Login successful for user: {}", request.getUsername());
            
            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Login successful")
                    .data(userInfo)
                    .build());
                    
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new AuthenticationException("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Registration attempt for user: {}", request.getUsername());
        
        try {
            authService.register(request);
            
            log.info("Registration successful for user: {}", request.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<String>builder()
                            .success(true)
                            .message("Registration successful. Please login.")
                            .build());
                            
        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getUsername(), e);
            throw new RegistrationException("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        
        log.info("Logout request");
        
        try {
            if (refreshToken != null) {
                authService.logout(refreshToken);
            }
            
            // Clear cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);
            
            log.info("Logout successful");
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Logout successful")
                    .build());
                    
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new AuthenticationException("Logout failed: " + e.getMessage());
        }
    }
}
```

### 6.7 Token Controller

```java
package com.lumina.auth.controller;

import com.lumina.auth.dto.ApiResponse;
import com.lumina.auth.dto.TokenResponse;
import com.lumina.auth.dto.UserInfoResponse;
import com.lumina.auth.service.TokenService;
import com.lumina.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<UserInfoResponse>> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        
        log.info("Token refresh request");
        
        try {
            TokenResponse tokenResponse = tokenService.refreshToken(refreshToken);
            
            // Update cookies with new tokens
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());
            
            // Get updated user info
            UserInfoResponse userInfo = tokenService.getUserInfo(tokenResponse.getAccessToken());
            
            log.info("Token refresh successful");
            
            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(userInfo)
                    .build());
                    
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            // Clear invalid cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);
            throw new AuthenticationException("Token refresh failed: " + e.getMessage());
        }
    }
}
```

### 6.8 Auth Service

```java
package com.lumina.auth.service;

import com.lumina.auth.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Keycloak keycloakClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public TokenResponse login(LoginRequest request) {
        log.debug("Attempting login for user: {}", request.getUsername());
        
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", 
                authServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, entity, Map.class);
            
            Map<String, Object> body = response.getBody();
            
            return TokenResponse.builder()
                    .accessToken((String) body.get("access_token"))
                    .refreshToken((String) body.get("refresh_token"))
                    .tokenType((String) body.get("token_type"))
                    .expiresIn((Integer) body.get("expires_in"))
                    .refreshExpiresIn((Integer) body.get("refresh_expires_in"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new AuthenticationException("Invalid credentials");
        }
    }

    public void register(RegisterRequest request) {
        log.debug("Attempting registration for user: {}", request.getUsername());
        
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(false);  // Require email verification
            
            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));
            
            // Create user in Keycloak
            keycloakClient.realm(realm).users().create(user);
            
            log.info("User registered successfully: {}", request.getUsername());
            
        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getUsername(), e);
            throw new RegistrationException("Registration failed: " + e.getMessage());
        }
    }

    public void logout(String refreshToken) {
        log.debug("Attempting logout");
        
        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout", 
                authServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        
        try {
            restTemplate.exchange(logoutUrl, HttpMethod.POST, entity, Void.class);
            log.info("Logout successful");
        } catch (Exception e) {
            log.warn("Logout failed, but proceeding with client-side cleanup", e);
        }
    }

    public UserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = String.format("%s/realms/%s/protocol/openid-connect/userinfo", 
                authServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, Map.class);
            
            Map<String, Object> body = response.getBody();
            
            return UserInfoResponse.builder()
                    .userId((String) body.get("sub"))
                    .username((String) body.get("preferred_username"))
                    .email((String) body.get("email"))
                    .firstName((String) body.get("given_name"))
                    .lastName((String) body.get("family_name"))
                    .emailVerified((Boolean) body.get("email_verified"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new AuthenticationException("Failed to get user info");
        }
    }
}
```

### 6.9 Cookie Utility

```java
package com.lumina.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.cookie.access-token.name}")
    private String accessTokenName;

    @Value("${jwt.cookie.access-token.max-age}")
    private int accessTokenMaxAge;

    @Value("${jwt.cookie.refresh-token.name}")
    private String refreshTokenName;

    @Value("${jwt.cookie.refresh-token.max-age}")
    private int refreshTokenMaxAge;

    @Value("${jwt.cookie.access-token.http-only}")
    private boolean httpOnly;

    @Value("${jwt.cookie.access-token.secure}")
    private boolean secure;

    @Value("${jwt.cookie.access-token.same-site}")
    private String sameSite;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(accessTokenName, token, accessTokenMaxAge);
        response.addCookie(cookie);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(refreshTokenName, token, refreshTokenMaxAge);
        response.addCookie(cookie);
    }

    public void clearAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = createCookie(accessTokenName, "", 0);
        response.addCookie(cookie);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = createCookie(refreshTokenName, "", 0);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        // Note: SameSite attribute requires Spring Boot 2.6+ or manual header manipulation
        return cookie;
    }
}
```

---

## 7. Business Logic Service Implementation

### 7.1 Configuration (application.yml)

Add to existing configuration:

```yaml
keycloak:
  realm: lumina
  auth-server-url: http://localhost:8180
  resource: business-logic-client
  public-client: true
  bearer-only: true
  ssl-required: external
  use-resource-role-mappings: true
  principal-attribute: preferred_username

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/lumina
          jwk-set-uri: http://localhost:8180/realms/lumina/protocol/openid-connect/certs

jwt:
  cookie:
    access-token:
      name: accessToken

logging:
  level:
    org.keycloak: INFO
    org.springframework.security: DEBUG
```

### 7.2 Security Configuration

```java
package com.lumina.businesslogic.infrastructure.config;

import com.lumina.businesslogic.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 7.3 JWT Authentication Filter

```java
package com.lumina.businesslogic.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final JwtUserDetailsService userDetailsService;

    @Value("${jwt.cookie.access-token.name}")
    private String accessTokenCookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromCookie(request);
            
            if (jwt != null && jwtValidator.validateToken(jwt)) {
                String username = jwtValidator.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                        
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                    
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Authentication set for user: {}", username);
            }
            
        } catch (Exception e) {
            log.error("Cannot set user authentication", e);
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> accessTokenCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
```

### 7.4 JWT Validator

```java
package com.lumina.businesslogic.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtValidator {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private RSAPublicKey publicKey;

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        }
        return false;
    }

    /**
     * Extract username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("preferred_username", String.class);
    }

    /**
     * Extract user ID from JWT token
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();  // 'sub' claim
    }

    /**
     * Get Keycloak public key for JWT verification
     * In production, fetch from JWKS endpoint
     */
    private RSAPublicKey getPublicKey() {
        if (publicKey == null) {
            // Fetch public key from Keycloak JWKS endpoint
            // Implementation: Call ${keycloak.auth-server-url}/realms/${realm}/protocol/openid-connect/certs
            // Parse JWKS response and extract RSA public key
            
            // For now, this is a placeholder
            // Real implementation should cache the public key and refresh periodically
            throw new UnsupportedOperationException("Implement public key fetching from Keycloak JWKS");
        }
        return publicKey;
    }
}
```

### 7.5 JWT User Details Service

```java
package com.lumina.businesslogic.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final JwtValidator jwtValidator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Extract JWT from current request
        String jwt = extractJwtFromCurrentRequest();
        
        if (jwt == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        // Extract roles from JWT
        List<GrantedAuthority> authorities = extractAuthorities(jwt);
        
        return User.builder()
                .username(username)
                .password("")  // Not used
                .authorities(authorities)
                .build();
    }

    private String extractJwtFromCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            if (request.getCookies() != null) {
                return Arrays.stream(request.getCookies())
                        .filter(cookie -> "accessToken".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    private List<GrantedAuthority> extractAuthorities(String jwt) {
        // Parse JWT and extract roles from claims
        // Implementation depends on how roles are stored in JWT
        // Typically: realm_access.roles + resource_access.{client}.roles
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));  // Default role
        
        // TODO: Extract actual roles from JWT claims
        
        return authorities;
    }
}
```

### 7.6 Update WorkspaceController

```java
package com.lumina.businesslogic.presentation.api.v1.workspace;

import com.lumina.businesslogic.application.usecase.CreateWorkspaceUseCase;
import com.lumina.businesslogic.application.dto.CreateWorkspaceCommand;
import com.lumina.businesslogic.infrastructure.security.JwtValidator;
import com.lumina.businesslogic.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final WorkspaceApiMapper workspaceApiMapper;
    private final JwtValidator jwtValidator;

    @PostMapping
    @PreAuthorize("hasRole('USER')")  // Require USER role
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        
        log.info("Create workspace request from user: {}", userDetails.getUsername());
        
        // Extract user ID from JWT
        String jwt = extractJwtFromCookie(httpRequest);
        String userId = jwtValidator.getUserIdFromToken(jwt);
        
        // Map to command with actual user ID from JWT
        CreateWorkspaceCommand command = workspaceApiMapper.toCommand(request);
        command.setCreatorUserId(UUID.fromString(userId));  // Use JWT user ID
        
        // Execute use case
        CreateWorkspaceUseCase.CreateWorkspaceResult result = 
            createWorkspaceUseCase.execute(command);
        
        // Map to response
        WorkspaceResponse response = workspaceApiMapper.toResponse(result.getWorkspace());
        
        log.info("Workspace created successfully: {}", result.getWorkspace().getWorkspaceId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<WorkspaceResponse>builder()
                        .success(true)
                        .message("Workspace created successfully")
                        .data(response)
                        .timestamp(java.time.Instant.now())
                        .build());
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "accessToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
```

---

## 8. Security Best Practices

### 8.1 Token Security

**✅ DO:**
- Use HTTP-only cookies for token storage
- Set secure flag in production (HTTPS only)
- Use SameSite=Strict for CSRF protection
- Implement short token expiration (15 minutes for access, 10 hours for refresh)
- Validate token signature with Keycloak public key
- Log all authentication attempts

**❌ DON'T:**
- Store tokens in localStorage or sessionStorage
- Expose tokens in URL parameters
- Use long expiration times
- Skip token signature validation
- Expose sensitive user data in error messages

### 8.2 CORS Configuration

**Auth Service:**
```yaml
cors:
  allowed-origins: http://localhost:3000, https://yourdomain.com
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: Content-Type,Authorization
  allow-credentials: true
  max-age: 3600
```

**Business Logic Service:**
```yaml
cors:
  allowed-origins: http://localhost:3000, https://yourdomain.com
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: Content-Type
  allow-credentials: true
  max-age: 3600
```

### 8.3 Rate Limiting

Implement rate limiting to prevent brute force attacks:

```java
// Add to Auth Service
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    // Implementation using Bucket4j or similar
}
```

### 8.4 Logging & Monitoring

**Log:**
- All authentication attempts (success and failure)
- Token refresh requests
- Authorization failures
- Suspicious activity (multiple failed attempts)

**Monitor:**
- Failed login attempts per user
- Token refresh frequency
- Invalid token attempts
- Response times

---

## 9. Implementation Roadmap

### Phase 1: Keycloak Setup (Week 1, 8-12 hours)

**Day 1-2:**
- [ ] Install and configure Keycloak
- [ ] Create lumina realm
- [ ] Create clients (auth-service-client, business-logic-client)
- [ ] Configure roles (ROLE_USER, ROLE_ADMIN, etc.)
- [ ] Test Keycloak admin console

**Day 3:**
- [ ] Configure token settings (expiration, refresh)
- [ ] Set up user attribute mappers
- [ ] Test token generation via Postman

### Phase 2: Auth Service (Week 1-2, 16-24 hours)

**Day 4-5:**
- [ ] Create auth_service Spring Boot project
- [ ] Add dependencies (Keycloak, Security, Validation)
- [ ] Configure application.yml
- [ ] Implement KeycloakConfig
- [ ] Implement SecurityConfig

**Day 6-7:**
- [ ] Implement DTOs (LoginRequest, RegisterRequest, TokenResponse)
- [ ] Implement AuthService (login, register)
- [ ] Implement TokenService (refresh)
- [ ] Implement CookieUtil

**Day 8-9:**
- [ ] Implement AuthController
- [ ] Implement TokenController
- [ ] Implement GlobalExceptionHandler
- [ ] Test all endpoints with Postman

### Phase 3: Business Logic Service (Week 2, 12-16 hours)

**Day 10-11:**
- [ ] Add Keycloak dependencies to business_logic service
- [ ] Configure application.yml with Keycloak settings
- [ ] Implement SecurityConfig
- [ ] Implement JwtAuthenticationFilter
- [ ] Implement JwtValidator

**Day 12:**
- [ ] Implement JwtUserDetailsService
- [ ] Update WorkspaceController to extract user from JWT
- [ ] Test authentication flow end-to-end

### Phase 4: Integration & Testing (Week 2-3, 8-12 hours)

**Day 13-14:**
- [ ] Integration testing (Auth → Keycloak → Business Logic)
- [ ] Test all authentication scenarios
- [ ] Test authorization (roles and permissions)
- [ ] Load testing with JMeter
- [ ] Security testing (OWASP ZAP)

### Phase 5: Production Readiness (Week 3, 4-8 hours)

**Day 15:**
- [ ] Externalize configuration (environment variables)
- [ ] Set up HTTPS/TLS certificates
- [ ] Configure production Keycloak
- [ ] Set up monitoring and logging
- [ ] Documentation and deployment guide

**Total Implementation Time:** 48-72 hours (2-3 weeks)

---

## 10. Testing Strategy

### 10.1 Unit Tests

**Auth Service:**
```java
@SpringBootTest
class AuthServiceTest {
    @Test
    void testLogin_Success() {
        // Test successful login
    }
    
    @Test
    void testLogin_InvalidCredentials() {
        // Test invalid credentials
    }
    
    @Test
    void testRegister_Success() {
        // Test successful registration
    }
    
    @Test
    void testRegister_DuplicateUsername() {
        // Test duplicate username
    }
}
```

### 10.2 Integration Tests

**Business Logic Service:**
```java
@SpringBootTest
@AutoConfigureMockMvc
class WorkspaceControllerIntegrationTest {
    
    @Test
    void testCreateWorkspace_WithValidJWT() {
        // Test with valid JWT
    }
    
    @Test
    void testCreateWorkspace_WithExpiredJWT() {
        // Test with expired JWT
    }
    
    @Test
    void testCreateWorkspace_WithoutJWT() {
        // Test without JWT (should return 401)
    }
}
```

### 10.3 Manual Testing Scenarios

**Scenario 1: Complete Authentication Flow**
1. Register new user via Auth Service
2. Login with credentials
3. Verify tokens in cookies
4. Make business request with token
5. Verify user ID is extracted from JWT

**Scenario 2: Token Refresh**
1. Login and get tokens
2. Wait for access token to expire
3. Use refresh token to get new access token
4. Verify new token works

**Scenario 3: Logout**
1. Login
2. Logout
3. Verify cookies are cleared
4. Verify tokens are revoked in Keycloak

**Scenario 4: Authorization**
1. Create user with ROLE_USER
2. Attempt to access admin endpoint
3. Verify 403 Forbidden response

---

## 11. Deployment Considerations

### 11.1 Environment Variables

**Auth Service:**
```bash
KEYCLOAK_AUTH_SERVER_URL=https://keycloak.yourdomain.com
KEYCLOAK_REALM=lumina
KEYCLOAK_CLIENT_ID=auth-service-client
KEYCLOAK_CLIENT_SECRET=${CLIENT_SECRET}
JWT_COOKIE_SECURE=true
CORS_ALLOWED_ORIGINS=https://yourdomain.com
```

**Business Logic Service:**
```bash
KEYCLOAK_AUTH_SERVER_URL=https://keycloak.yourdomain.com
KEYCLOAK_REALM=lumina
KEYCLOAK_RESOURCE=business-logic-client
JWT_COOKIE_SECURE=true
```

### 11.2 Docker Compose

```yaml
version: '3.8'

services:
  keycloak:
    image: quay.io/keycloak/keycloak:23.0.0
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: ${KEYCLOAK_DB_PASSWORD}
    ports:
      - "8180:8080"
    depends_on:
      - postgres
    command: start-dev

  auth-service:
    build: ./auth_service
    environment:
      KEYCLOAK_AUTH_SERVER_URL: http://keycloak:8080
      KEYCLOAK_CLIENT_SECRET: ${AUTH_CLIENT_SECRET}
    ports:
      - "8081:8081"
    depends_on:
      - keycloak

  business-logic:
    build: ./business_logic
    environment:
      KEYCLOAK_AUTH_SERVER_URL: http://keycloak:8080
    ports:
      - "8080:8080"
    depends_on:
      - keycloak
      - postgres

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### 11.3 Production Checklist

- [ ] HTTPS/TLS enabled for all services
- [ ] Keycloak running in production mode (not dev mode)
- [ ] Secure cookie flags enabled
- [ ] CORS properly configured with actual domain
- [ ] Environment variables externalized
- [ ] Database backed by persistent storage
- [ ] Monitoring and alerting configured
- [ ] Rate limiting enabled
- [ ] Logging configured
- [ ] Backup and disaster recovery plan
- [ ] Security headers configured
- [ ] CSRF protection enabled for state-changing operations

---

## 12. Troubleshooting Guide

### 12.1 Common Issues

**Issue: "Invalid token signature"**
- **Cause:** Public key mismatch or expired key
- **Solution:** Refresh public key from Keycloak JWKS endpoint

**Issue: "CORS error when calling Auth Service"**
- **Cause:** Missing or incorrect CORS configuration
- **Solution:** Verify allowed origins include frontend URL

**Issue: "Token not found in cookie"**
- **Cause:** Cookie not being sent by browser
- **Solution:** Verify cookie domain, path, and SameSite settings

**Issue: "401 Unauthorized on business logic requests"**
- **Cause:** JWT validation failing
- **Solution:** Check JWT expiration, signature, and Keycloak configuration

### 12.2 Debugging

**Enable Debug Logging:**
```yaml
logging:
  level:
    com.lumina: DEBUG
    org.keycloak: DEBUG
    org.springframework.security: DEBUG
```

**Check JWT Contents:**
```java
// Decode JWT (base64 decode the payload)
String[] parts = jwt.split("\\.");
String payload = new String(Base64.getDecoder().decode(parts[1]));
System.out.println("JWT Payload: " + payload);
```

---

## Summary

This implementation plan provides a complete roadmap for integrating Keycloak-based JWT authentication and authorization into your microservices architecture.

**Key Outcomes:**
1. ✅ Secure authentication via Keycloak
2. ✅ JWT-based stateless authorization
3. ✅ Proper separation of concerns (auth vs business logic)
4. ✅ Production-ready security (HTTP-only cookies, proper validation)
5. ✅ Scalable architecture supporting multiple services

**Timeline:** 2-3 weeks (48-72 hours)

**Next Steps:**
1. Review this plan and confirm approach
2. Set up Keycloak instance
3. Begin Phase 1 implementation
4. Iteratively test each phase
5. Deploy to staging environment
6. Conduct security audit
7. Deploy to production

---

**Document Version:** 1.0  
**Last Updated:** November 10, 2025  
**Author:** Senior Java Developer (15 years DDD experience)
