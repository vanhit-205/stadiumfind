# 🚀 Tài Liệu Hướng Dẫn Kiểm Thử API (StadiumBooking API)

Tài liệu này hướng dẫn chi tiết cách kiểm thử toàn bộ các API của dự án **StadiumBooking API** trên **Postman** và **cURL**, bao gồm quy trình đăng ký, đăng nhập nhận JWT Token, sử dụng Token để truy cập API bảo mật, và làm mới Token (Refresh Token).

---

## 🛠 1. Cấu hình Môi trường (Postman Environment)

Để dễ dàng quản lý Token và gọi API nhanh chóng, bạn nên tạo một **Environment** trên Postman đặt tên là `StadiumFind-Local` với các biến sau:

| Tên biến (Variable) | Giá trị khởi tạo (Initial Value) | Mô tả |
| :--- | :--- | :--- |
| `base_url` | `http://localhost:8080` | URL gốc của dịch vụ backend |
| `access_token` | *(Tự động cập nhật sau khi login)* | Chuỗi JWT Access Token |
| `refresh_token` | *(Tự động cập nhật sau khi login)* | Chuỗi JWT Refresh Token |

---

## 🔑 2. Danh Sách API Authentication (`/api/auth`)

### 2.1 Đăng ký tài khoản (Register)

Tạo mới tài khoản người dùng hệ thống (`USER`, `STADIUM_OWNER`, `ADMIN`).

- **Method**: `POST`
- **URL**: `{{base_url}}/api/auth/register`
- **Headers**:
  - `Content-Type: application/json`

#### Request Body (JSON Mẫu):
```json
{
  "username": "stadium_owner_01",
  "password": "password123",
  "email": "owner01@stadiumfind.com",
  "fullName": "Nguyễn Văn Chủ Sân",
  "phoneNumber": "0987654321",
  "role": "STADIUM_OWNER",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

> **Lưu ý về Validation:**
> - `username`: Chiều dài từ 3 - 50 ký tự, không được trùng.
> - `password`: Tối thiểu 6 ký tự.
> - `email`: Đúng định dạng email, không được trùng.
> - `phoneNumber`: Định dạng số điện thoại Việt Nam (`^(0|\+84)[0-9]{9}$`).
> - `role`: Các giá trị hợp lệ bao gồm `USER`, `STADIUM_OWNER`, `ADMIN`.

#### Response Mẫu (HTTP 201 Created):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGFkaXVtX293bmVyXzAxIiwiaWF0IjoxN...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGFkaXVtX293bmVyXzAxIiwidHlwZSI6...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "stadium_owner_01",
    "email": "owner01@stadiumfind.com",
    "fullName": "Nguyễn Văn Chủ Sân",
    "phoneNumber": "0987654321",
    "role": "STADIUM_OWNER",
    "avatarUrl": "https://example.com/avatar.jpg",
    "isActive": true,
    "createdAt": "2026-08-05T07:30:00Z",
    "updatedAt": "2026-08-05T07:30:00Z"
  }
}
```

---

### 2.2 Đăng nhập (Login)

Đăng nhập bằng Username hoặc Email để lấy cặp Access Token & Refresh Token.

- **Method**: `POST`
- **URL**: `{{base_url}}/api/auth/login`
- **Headers**:
  - `Content-Type: application/json`

#### Request Body (JSON Mẫu):
```json
{
  "usernameOrEmail": "stadium_owner_01",
  "password": "password123"
}
```

#### Response Mẫu (HTTP 200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGFkaXVtX293bmVyXzAxIiwiaWF0IjoxN...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGFkaXVtX293bmVyXzAxIiwidHlwZSI6...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "stadium_owner_01",
    "email": "owner01@stadiumfind.com",
    "fullName": "Nguyễn Văn Chủ Sân",
    "role": "STADIUM_OWNER"
  }
}
```

> 💡 **Mẹo Postman (Auto-save Token):**  
> Chèn script này vào tab **Tests** của request `Login` để Postman tự lấy Token gán vào biến môi trường:
> ```javascript
> if (pm.response.code === 200) {
>     var jsonData = pm.response.json();
>     pm.environment.set("access_token", jsonData.accessToken);
>     pm.environment.set("refresh_token", jsonData.refreshToken);
> }
> ```

---

### 2.3 Cấp lại Access Token mới (Refresh Token)

Sử dụng Refresh Token để lấy lại Access Token mới khi token cũ hết hạn mà không cần bắt user đăng nhập lại.

- **Method**: `POST`
- **URL**: `{{base_url}}/api/auth/refresh-token`
- **Headers**:
  - `Content-Type: application/json`

#### Request Body (JSON Mẫu):
```json
{
  "refreshToken": "{{refresh_token}}"
}
```

#### Response Mẫu (HTTP 200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGFkaXVtX293bmVyXzAxIiwia...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGFkaXVtX293bmVyXzAxIiwidHlw...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": { ... }
}
```

---

## 🔒 3. Kiểm Thử API Được Bảo Vệ (Secured API)

### 3.1 Lấy thông tin User hiện tại (Test Authentication)

Endpoint này yêu cầu xác thực JWT.

- **Method**: `GET`
- **URL**: `{{base_url}}/api/test/user`
- **Authorization**:
  - **Type**: `Bearer Token`
  - **Token**: `{{access_token}}`
  *(Hoặc cài đặt thủ công ở tab **Headers**: `Authorization` = `Bearer {{access_token}}`)*

#### Trường hợp 1: Token hợp lệ (HTTP 200 OK)
```text
Xác thực thành công! Bạn đang truy cập với tư cách user: stadium_owner_01
```

#### Trường hợp 2: Không truyền Token / Token hết hạn (HTTP 401 Unauthorized / HTTP 403 Forbidden)
```json
{
  "timestamp": "2026-08-05T07:32:00.123+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/test/user"
}
```

---

## 📋 4. Tổng Hợp Lỗi Thường Gặp (HTTP Status Codes)

| Mã lỗi HTTP | Tên lỗi | Nguyên nhân & Cách khắc phục |
| :--- | :--- | :--- |
| `200 OK` | Thành công | Request xử lý thành công. |
| `201 Created` | Tạo mới thành công | Tạo mới tài khoản hoặc dữ liệu thành công. |
| `400 Bad Request` | Dữ liệu không hợp lệ | Dữ liệu đầu vào vi phạm validation (vd: email sai định dạng, thiếu password). |
| `401 Unauthorized` | Chưa xác thực / Sai password | Token hết hạn, token không đúng định dạng, hoặc mật khẩu đăng nhập không đúng. |
| `403 Forbidden` | Không có quyền truy cập | Tài khoản không đúng Role yêu cầu (vd: `USER` cố gọi API của `ADMIN`). |
| `409 Conflict` | Trùng lặp tài nguyên | `username` hoặc `email` đã tồn tại trong hệ thống. |
| `500 Internal Error` | Lỗi máy chủ | Lỗi code backend chưa bắt ngoại lệ hoặc lỗi kết nối DB. |

---

## 💻 5. cURL Command Mẫu Cho Developer

#### Đăng nhập bằng cURL:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "stadium_owner_01",
    "password": "password123"
  }'
```

#### Gọi Secured API bằng cURL:
```bash
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```
