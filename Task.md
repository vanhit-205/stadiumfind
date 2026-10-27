# 🗓️ Lịch Trình Phát Triển Dự Án StadiumFind (30 Ngày)

**Mục tiêu:** Hoàn thành ứng dụng Android (Kotlin, Clean Architecture) + Backend (Spring Boot AI gen).
**Thời gian mỗi ngày:** 3.5 - 4 giờ.
**Nguyên tắc:** Chủ nhật nghỉ ngơi hoàn toàn (hoặc chỉ review code nhẹ nhàng).

---

## Tuần 1: Khởi Tạo Nền Tảng & Database
- [x] **Ngày 1 (Thứ 2): Khởi tạo Project & Database**
  - [x] **Backend (AI):** Prompt AI sinh ERD (Database Schema) cho các bảng `User`, `Stadium`, `Booking`, `MatchPost`.
  - [x] **Backend (AI):** Khởi tạo project Spring Boot qua Spring Initializr, kết nối MySQL.
  - [x] **Frontend:** Tạo project Android Studio. Setup `build.gradle` (Hilt, Retrofit, Navigation, Coroutines).
- [x] **Ngày 2 (Thứ 3): Thiết lập Clean Architecture**
  - [x] **Backend (AI):** Gen các class `@Entity` (JPA) và interface `Repository`.
  - [x] **Frontend:** Tạo cấu trúc thư mục (core, features/auth, features/stadium, v.v.).
  - [x] **Frontend:** Cấu hình Hilt `AppModule`, `NetworkModule`.
- [x] **Ngày 3 (Thứ 4): Xây dựng Core Network**
  - [x] **Frontend:** Viết `BaseResponse`, `Resource` (Sealed class) quản lý state.
  - [x] **Frontend:** Setup `Retrofit` và `OkHttpClient` kèm Logging Interceptor.
- [x] **Ngày 5 (Thứ 5): Base UI & Navigation**
  - [x] **Frontend:** Thiết lập Theme, Colors, Typography.
  - [x] **Frontend:** Tạo các Custom Views dùng chung: PrimaryButton, AppTextField, LoadingDialog.
  - [x] **Frontend:** Thiết lập Navigation Graph (Bottom Navigation).
- [x] **Ngày 6 (Thứ 6): Xác thực (Backend)**
  - [x] **Backend (AI):** Gen cấu hình Spring Security + JWT. 
  - [x] **Backend (AI):** Viết và test API `/api/auth/register` và `/api/auth/login` trên Postman.
- [x] **Ngày 7 (Chủ nhật): 💤 Nghỉ ngơi**

---

## Tuần 2: Module Auth & Hiển Thị Sân Bóng (Paging)
- [x] **Ngày 8 (Thứ 2): UI Đăng nhập & Đăng ký**
  - [x] **Frontend:** Code màn hình Login và Register.
  - [x] **Frontend:** Xử lý validate dữ liệu nhập (email format, password length).
- [x] **Ngày 9 (Thứ 3): Logic Xác thực (Clean Architecture)**
  - [x] **Frontend:** Viết `AuthApiService`, `AuthRepository`, `LoginUseCase`.
  - [x] **Frontend:** Tích hợp `DataStore` để lưu JWT Token. Viết `AuthInterceptor`.
- [x] **Ngày 10 (Thứ 4): Ghép API Auth & ViewModel**
  - [x] **Frontend:** Code `AuthViewModel` với `StateFlow`.
  - [x] **Frontend:** Gọi API Login, xử lý loading/success/error. Điều hướng vào màn Home.
- [x] **Ngày 11 (Thứ 5): API Sân Bóng (Backend)**
  - [x] **Backend (AI):** Gen API `/api/stadiums` có hỗ trợ phân trang (Pageable).
  - [x] **Backend (AI):** Viết script SQL insert 15-20 sân bóng giả lập để test.
- [x] **Ngày 12 (Thứ 6): UI Màn Hình Home**
  - [x] **Frontend:** Thiết kế layout item sân bóng (CardView, Rating, Giá).
  - [x] **Frontend:** Tích hợp thư viện `Coil` để load ảnh sân từ URL.
- [x] **Ngày 13 (Thứ 7): Paging 3 & Danh sách sân**
  - [x] **Frontend:** Viết `StadiumPagingSource` và `GetStadiumsUseCase`.
  - [x] **Frontend:** Dùng `PagingDataAdapter` hiển thị danh sách sân lên RecyclerView/LazyColumn.
- [ ] **Ngày 14 (Chủ nhật): 💤 Nghỉ ngơi**

---

## Tuần 3: Bản Đồ (OpenStreetMap) & Chi Tiết Sân
- [x] **Ngày 15 (Thứ 2): Setup OpenStreetMap (osmdroid)**
  - [x] **Frontend:** Tích hợp thư viện osmdroid. Cấu hình tile source & cache.
  - [x] **Frontend:** Viết lại SearchFragment dùng MapView (osmdroid) thay Google Maps.
  - [x] **Frontend:** Xin quyền vị trí (Runtime Permissions) & Code lấy tọa độ hiện tại (Fused Location).
- [x] **Ngày 16 (Thứ 3): Rải Marker Sân Bóng (osmdroid)**
  - [x] **Backend (AI):** Gen API trả về tọa độ các sân gần nhất.
  - [x] **Frontend:** Gọi API, dùng osmdroid Marker/Overlay hiển thị sân lên bản đồ.
- [x] **Ngày 17 (Thứ 4): Tương tác Bản Đồ (osmdroid)**
  - [x] **Frontend:** Xử lý sự kiện click Marker (osmdroid ItemizedOverlay) hiện BottomSheet thông tin sân.
  - [x] **Frontend:** Nút bấm từ BottomSheet điều hướng sang màn Chi tiết.
- [x] **Ngày 18 (Thứ 5): UI Chi Tiết Sân**
  - [x] **Frontend:** Code UI màn hình Detail (Image Slider, Tiện ích sân: Wifi, Nước uống).
- [x] **Ngày 19 (Thứ 6): Custom Calendar & TimePicker**
  - [x] **Frontend:** Tự code UI chọn Ngày (dạng lịch ngang) và chọn Giờ (Grid các khung giờ, hỗ trợ toggle ca 60p/90p).
- [x] **Ngày 20 (Thứ 7): Logic Chọn Giờ Trống**
  - [x] **Backend (AI):** Gen API kiểm tra danh sách giờ đã có người đặt trong một ngày cụ thể.
  - [x] **Frontend:** Disable (Làm mờ/Khóa) các khung giờ đã có người đặt trên UI.
- [x] **Ngày 21 (Chủ nhật): 💤 Nghỉ ngơi**

---

## Tuần 4: Đặt Sân, Mã QR & Mạng Xã Hội (Tìm Kèo)
- [x] **Ngày 22 (Thứ 2): Luồng Đặt Sân (Booking)**
  - [x] **Backend (AI):** Gen API `/api/bookings` (Check overlap time bằng `@Query` JPA).
  - [x] **Frontend:** Viết API Service, UseCase và ViewModel cho luồng Đặt sân.
- [ ] **Ngày 23 (Thứ 3): Mã QR Xác Nhận**
  - [ ] **Frontend:** Gọi API Đặt sân. Khi thành công, nhận `ticketId`.
  - [ ] **Frontend:** Tích hợp thư viện `ZXing` sinh mã QR code hiển thị trên màn hình Success.
- [ ] **Ngày 24 (Thứ 4): Quản lý Vé (Ticket)**
  - [ ] **Frontend:** Code màn hình "Vé của tôi" (My Bookings) dạng Tab (Sắp tới / Đã đá).
- [ ] **Ngày 25 (Thứ 5): API Tìm Kèo (Backend)**
  - [ ] **Backend (AI):** Gen API CRUD bảng `MatchPost` và API `JoinMatch`.
- [ ] **Ngày 26 (Thứ 6): UI Bảng Tin Tìm Kèo (Feed)**
  - [ ] **Frontend:** Code UI màn hình News Feed và Form đăng bài tìm người.
- [ ] **Ngày 27 (Thứ 7): Tương tác "Tham Gia Kèo"**
  - [ ] **Frontend:** Ghép API Đăng bài và Xin tham gia kèo. Cập nhật state UI.
- [ ] **Ngày 28 (Chủ nhật): 💤 Nghỉ ngơi**

---

## Tuần 5: Lưu Trữ Offline & Thông Báo (Hoàn thiện)
- [ ] **Ngày 29 (Thứ 2): Room Database (Offline Cache)**
  - [ ] **Frontend:** Cấu hình Room DB (`TicketEntity`, `TicketDao`).
  - [ ] **Frontend:** Cache vé vào Room khi fetch từ API để mở được QR lúc mất mạng.
- [ ] **Ngày 30 (Thứ 3): Push Notification & Polish**
  - [ ] **Backend (AI):** Gen code tích hợp Firebase Admin bắn push báo có người xin vào kèo.
  - [ ] **Frontend:** Setup Firebase Cloud Messaging (FCM) nhận thông báo.
  - [ ] **Frontend:** Test luồng tổng thể, fix bug UI, dọn dẹp code dư thừa.
