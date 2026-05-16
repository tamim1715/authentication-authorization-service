# Authentication & Authorization Service

Production-grade Authentication and Authorization Service built using **Spring Boot**, **JWT (RS256)**, and **MySQL** following modern backend engineering best practices.

---

# 🚀 Features

## ✅ Authentication
- User Registration
- User Login
- JWT Access Token Authentication
- RS256 Public/Private Key Signing
- Refresh Token Rotation
- Refresh Token Reuse Detection
- Secure Logout

---

## ✅ Security
- BCrypt Password Hashing
- Global Exception Handling
- DTO Validation
- Secure Token Hashing (SHA-256)
- Email Verification
- Forgot Password Flow
- Refresh Token Revocation
- Session Invalidation

---

## ✅ Production-Grade Design
- Layered Architecture
- Clean Code Principles
- Single Responsibility Principle
- Stateless JWT Authentication
- Database-backed Refresh Tokens
- Secure API Design
- Structured Package Organization

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java 21 | Runtime |
| Spring Boot | Backend Framework |
| Spring Security | Authentication & Authorization |
| JWT (RS256) | Access Token |
| MySQL | Database |
| JPA / Hibernate | ORM |
| Maven | Dependency Management |
| Lombok | Boilerplate Reduction |
| BCrypt | Password Hashing |

---

## 📂 Project Structure

```text
src/main/java/com/tamim/auth

├── config
├── controller
│   └── auth
├── domain
│   ├── auth
│   └── user
├── dto
│   ├── auth
│   └── user
├── exception
├── mapper
├── repository
│   ├── auth
│   └── user
├── security
│   ├── filter
│   ├── jwt
│   └── user
├── service
│   ├── auth
│   └── email
└── util
```
## 🔐 Authentication Flow
```
Login
↓
Generate Access Token (JWT RS256)
↓
Generate Refresh Token
↓
Store Hashed Refresh Token in DB
↓
Client Receives Tokens
```

## 🔄 Refresh Token Flow
```
Client Sends Refresh Token
  ↓
Hash Token
  ↓
Validate DB Record
  ↓
Detect Reuse
  ↓
Rotate Refresh Token
  ↓
Generate New Access Token
```

## 📧 Email Verification Flow
```
Register
  ↓
Generate Verification Token
  ↓
Send Verification Email
  ↓
User Verifies Email
  ↓
Enable Account
```

## 🔑 Forgot Password Flow
```
Forgot Password Request
  ↓
Generate Reset Token
  ↓
Send Email
  ↓
Reset Password
  ↓
Invalidate Existing Sessions
```

## 🔒 JWT Configuration
### Algorithm
```
RS256
```
### Generate RSA Keys
```
openssl genrsa -out private.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.pem -out private_pkcs8.pem
openssl rsa -in private.pem -pubout -out public.pem
```
### Key Location
```
src/main/resources/keys
```
### Files
```
private_pkcs8.pem
public.pem
```

## ⚙️ Environment Variables
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db
    username: root
    password: password

jwt:
  private-key: classpath:keys/private_pkcs8.pem
  public-key: classpath:keys/public.pem
```

## 🧪 Running the Project
### Clone Repository
```
git clone https://github.com/tamim1715/authentication-authorization-service.git
```
### Run MySQL
```
docker run --name auth-mysql \
-e MYSQL_ROOT_PASSWORD=password \
-e MYSQL_DATABASE=auth_db \
-p 3306:3306 \
-d mysql:8
```
### Start Application
```
./mvnw spring-boot:run
```

## 📌 API Endpoints
### Authentication
```
| Method | Endpoint             | Description          |
| ------ | -------------------- | -------------------- |
| POST   | `/api/v1/auth/register` | Register User        |
| POST   | `/api/v1/auth/login`    | Login                |
| POST   | `/api/v1/auth/refresh`  | Refresh Access Token |
| POST   | `/api/v1/auth/logout`   | Logout               |
```
### Email Verification
```
| Method | Endpoint                        |
| ------ | ------------------------------- |
| POST   | `/api/v1/auth/verify-email`        |
| POST   | `/api/v1/auth/resend-verification` |
```
### Password Reset
```
| Method | Endpoint                    |
| ------ | --------------------------- |
| POST   | `/api/v1/auth/forgot-password` |
| POST   | `/api/v1/auth/reset-password`  |
```
