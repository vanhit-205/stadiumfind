package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm kiếm người dùng theo username (Phục vụ Authentication/Login)
    Optional<User> findByUsername(String username);

    // Tìm kiếm người dùng theo email (Phục vụ Login/Reset Password)
    Optional<User> findByEmail(String email);

    // Kiểm tra username đã tồn tại hay chưa (Phục vụ Đăng ký)
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại hay chưa (Phục vụ Đăng ký)
    boolean existsByEmail(String email);

    // Phân trang danh sách người dùng theo Vai trò (Role: ADMIN, USER, STADIUM_OWNER)
    Page<User> findByRole(Role role, Pageable pageable);

    // Phân trang danh sách các tài khoản đang hoạt động
    Page<User> findByIsActiveTrue(Pageable pageable);
}
