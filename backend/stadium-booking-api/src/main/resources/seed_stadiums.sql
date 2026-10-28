-- ==============================================================================
-- StadiumFind: Script nạp dữ liệu mẫu (Seed Data) cho 18 sân bóng thực tế
-- Khớp hoàn toàn với schema bảng stadiums, users, courts trong MySQL
-- ==============================================================================

USE stadiumfind;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Tạo 2 tài khoản Chủ Sân (Owner) mẫu (Mật khẩu mặc định: 123456)
-- Hash BCrypt cho 123456: $2a$10$eACCYoNOHEqgkVE8aIWT8eS876CDQ/4.x4sU5cKzW5Vw5.5K0jB5q
INSERT INTO users (id, username, email, password, full_name, phone_number, role, is_active, created_at, updated_at)
VALUES 
(1001, 'owner_hanoi', 'owner.hanoi@stadiumfind.vn', '$2a$10$eACCYoNOHEqgkVE8aIWT8eS876CDQ/4.x4sU5cKzW5Vw5.5K0jB5q', 'Nguyễn Văn An (Chủ sân HN)', '0912345678', 'STADIUM_OWNER', b'1', NOW(), NOW()),
(1002, 'owner_hcm', 'owner.hcm@stadiumfind.vn', '$2a$10$eACCYoNOHEqgkVE8aIWT8eS876CDQ/4.x4sU5cKzW5Vw5.5K0jB5q', 'Trần Thị Bình (Chủ sân HCM)', '0987654321', 'STADIUM_OWNER', b'1', NOW(), NOW())
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), role = VALUES(role), is_active = VALUES(is_active);

-- 2. Tạo 18 Cụm Sân Bóng (Stadiums) tại Hà Nội, Đà Nẵng, TP.HCM
INSERT INTO stadiums (id, name, address, latitude, longitude, province_city, district, phone_number, description, opening_time, closing_time, image_url, rating, owner_id, created_at, updated_at)
VALUES
(1, 'Sân bóng Thượng Đình', '129 Đ. Nguyễn Trãi, Thượng Đình', 20.9984, 105.8174, 'Hà Nội', 'Thanh Xuân', '0912345601',
 'Cụm sân cỏ nhân tạo tiêu chuẩn chất lượng cao, hệ thống thoát nước ngầm hiện đại, giàn đèn LED cao áp siêu sáng, có khán đài có mái che và bãi gửi xe rộng rãi.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?w=800', 4.8, 1001, NOW(), NOW()),

(2, 'Sân bóng C500 - Học Viện An Ninh', 'Đ. Chiến Thắng, Văn Quán', 20.9806, 105.7923, 'Hà Nội', 'Hà Đông', '0912345602',
 'Cụm sân bóng nổi tiếng tại khu vực Hà Đông. Mặt cỏ êm ái chống trầy xước, tổ chức nhiều giải bóng đá sinh viên và phong trào, căn tin phục vụ giải khát chu đáo.',
 '05:30:00', '23:30:00', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', 4.7, 1001, NOW(), NOW()),

(3, 'Sân bóng Thành Phát', 'Số 2 Hoàng Minh Giám, Trung Hòa', 21.0069, 105.8005, 'Hà Nội', 'Cầu Giấy', '0912345603',
 'Vị trí đắc địa ngay trung tâm Cầu Giấy, mặt cỏ nhân tạo FIFA thay mới 2024. Phù hợp cho các trận đấu giao lưu sau giờ làm việc của dân văn phòng.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800', 4.9, 1001, NOW(), NOW()),

(4, 'Sân bóng Thủy Lợi', '95 Đ. Chùa Bộc, Trung Liệt', 21.0075, 105.8277, 'Hà Nội', 'Đống Đa', '0912345604',
 'Nằm trong khuôn viên Đại học Thủy Lợi. Mặt sân bằng phẳng, thoáng đãng, có phòng thay đồ và nhà tắm nóng lạnh tiện nghi cho cầu thủ.',
 '06:00:00', '22:30:00', 'https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?w=800', 4.6, 1001, NOW(), NOW()),

(5, 'Sân bóng Đông Đô', 'Ngõ 33 Tân Ấp, Phúc Xá', 21.0494, 105.8458, 'Hà Nội', 'Ba Đình', '0912345605',
 'Sân bóng ven sông Hồng với không gian thoáng mát, nhiều gió tự nhiên. Cỏ nhân tạo chất lượng tốt, bóng lăn đầm chân, giá thuê hợp lý.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1575361204480-aadea25e6e68?w=800', 4.5, 1001, NOW(), NOW()),

(6, 'Sân bóng An Khánh', 'KĐT Nam An Khánh, An Khánh', 20.9991, 105.7278, 'Hà Nội', 'Hoài Đức', '0912345606',
 'Quần thể sân bóng ngoại thành không gian khoáng đạt, bãi đỗ xe ô tô cực lớn miễn phí. Thích hợp tổ chức các giải đấu phong trào công ty quy mô lớn.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6?w=800', 4.6, 1001, NOW(), NOW()),

(7, 'Sân bóng Quảng Bá', '67 Đ. Tô Ngọc Vân, Quảng An', 21.0664, 105.8249, 'Hà Nội', 'Tây Hồ', '0912345607',
 'Tận hưởng không khí mát lành ven Hồ Tây. Dàn đèn sáng rực rỡ, hạt cao su phủ đều, mặt sân đạt chuẩn cho những đường chuyền kỹ thuật đỉnh cao.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1556056504-5c7696c4c28d?w=800', 4.8, 1001, NOW(), NOW()),

(8, 'Sân bóng Phúc Anh', 'Đ. Mễ Trì Hạ, Mễ Trì', 21.0152, 105.7801, 'Hà Nội', 'Nam Từ Liêm', '0912345608',
 'Ngay gần Keangnam và The Garden. Cụm sân sạch sẽ, chuyên nghiệp, có nước uống miễn phí và dịch vụ cho thuê giày, áo bib thi đấu phong trào.',
 '06:00:00', '23:30:00', 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=800', 4.7, 1001, NOW(), NOW()),

(9, 'Sân bóng Bách Khoa', 'Số 1 Đ. Đại Cồ Việt, Bách Khoa', 21.0068, 105.8432, 'Hà Nội', 'Hai Bà Trưng', '0912345609',
 'Địa điểm quen thuộc của cộng đồng bóng đá phủi Hà Nội. Không khí cuồng nhiệt, mặt cỏ được bảo trì thường xuyên, giá cả sinh viên.',
 '06:00:00', '22:30:00', 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?w=800', 4.5, 1001, NOW(), NOW()),

(10, 'Sân bóng Chuyên Việt', '98 Đ. Tiểu La, Hòa Cường Bắc', 16.0469, 108.2148, 'Đà Nẵng', 'Hải Châu', '0912345610',
 'Khu phức hợp thể thao đa năng gồm cả sân bóng đá cỏ nhân tạo và sân Pickleball tiêu chuẩn quốc tế. Trang thiết bị hiện đại, phục vụ tận tâm.',
 '05:30:00', '23:00:00', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', 4.9, 1001, NOW(), NOW()),

(11, 'Sân bóng H2 Tân Bình', '104 Đ. Phổ Quang, Phường 2', 10.8062, 106.6669, 'TP. Hồ Chí Minh', 'Tân Bình', '0987654311',
 'Cụm sân quy mô khủng gần sân bay Tân Sơn Nhất với hơn 10 sân cỏ nhân tạo đạt chuẩn châu Âu, có phòng thay đồ máy lạnh và bãi gửi xe rộng.',
 '06:00:00', '23:30:00', 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800', 4.8, 1002, NOW(), NOW()),

(12, 'Sân bóng Chu Văn An', 'Đ. Chu Văn An, Phường 26', 10.8176, 106.7088, 'TP. Hồ Chí Minh', 'Bình Thạnh', '0987654312',
 'Cụm sân bóng đá mini 5 và 7 người sôi động hàng đầu Bình Thạnh. Hệ thống lưới chắn bóng cao, giàn đèn led tiết kiệm điện nhưng độ sáng cực cao.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?w=800', 4.6, 1002, NOW(), NOW()),

(13, 'Sân bóng K334', '334 Đ. Lê Trọng Tấn, Tây Thạnh', 10.8122, 106.6262, 'TP. Hồ Chí Minh', 'Tân Phú', '0987654313',
 'Sân cỏ sợi kim cương mềm mại, giảm thiểu chấn thương khớp gối tối đa. Khu vực căng tin sạch đẹp, nhiều đồ uống giải nhiệt thể thao.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1575361204480-aadea25e6e68?w=800', 4.7, 1002, NOW(), NOW()),

(14, 'Sân bóng Tao Đàn', '1 Đ. Huyền Trân Công Chúa, Bến Thành', 10.7744, 106.6918, 'TP. Hồ Chí Minh', 'Quận 1', '0987654314',
 'Tọa lạc giữa lá phổi xanh công viên Tao Đàn trung tâm Quận 1. Không gian đá bóng trong lành, đẳng cấp, điểm hẹn của các giải đấu giao hữu.',
 '06:00:00', '22:30:00', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6?w=800', 4.8, 1002, NOW(), NOW()),

(15, 'Sân bóng Thảo Điền Sport', '28 Đ. Thảo Điền, Thảo Điền', 10.8032, 106.7329, 'TP. Hồ Chí Minh', 'TP. Thủ Đức', '0987654315',
 'Khu vực Thảo Điền phong cách sang trọng, tiện nghi cao cấp. Đạt chuẩn quốc tế, thường xuyên có các huấn luyện viên nước ngoài tổ chức giải.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1556056504-5c7696c4c28d?w=800', 4.9, 1002, NOW(), NOW()),

(16, 'Sân bóng Phú Thọ', '219 Đ. Lý Thường Kiệt, Phường 15', 10.7686, 106.6579, 'TP. Hồ Chí Minh', 'Quận 11', '0987654316',
 'Thuộc quần thể Trung tâm Thể dục Thể thao Phú Thọ. Sân kích thước chuẩn thi đấu, khán đài lớn, bãi đỗ xe an ninh 24/7.',
 '06:00:00', '22:30:00', 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=800', 4.7, 1002, NOW(), NOW()),

(17, 'Sân bóng Thanh Đa', '1010 Đ. Bình Quới, Phường 28', 10.8354, 106.7262, 'TP. Hồ Chí Minh', 'Bình Thạnh', '0987654317',
 'Bán đảo Thanh Đa bình yên với làn gió sông mát rượi. Mặt cỏ xanh mướt, dịch vụ tận tâm, giá ưu đãi cho học sinh - sinh viên vào ban ngày.',
 '06:00:00', '23:00:00', 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?w=800', 4.6, 1002, NOW(), NOW()),

(18, 'Sân bóng Sala Thủ Thiêm', 'Đ. Mai Chí Thọ, KĐT Sala', 10.7712, 106.7214, 'TP. Hồ Chí Minh', 'TP. Thủ Đức', '0987654318',
 'Cụm sân cao cấp bậc nhất KĐT Sala Thủ Thiêm, view ngắm trọn tòa nhà Landmark 81 lung linh về đêm. Mặt cỏ chuẩn FIFA Pro, dịch vụ VIP.',
 '05:30:00', '23:30:00', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', 4.9, 1002, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name), 
    address = VALUES(address), 
    latitude = VALUES(latitude),
    longitude = VALUES(longitude),
    province_city = VALUES(province_city), 
    district = VALUES(district), 
    image_url = VALUES(image_url), 
    rating = VALUES(rating);

-- 3. Tạo hơn 40 Sân Con (Courts) thuộc về các Cụm Sân trên
INSERT INTO courts (id, name, type, sport_type, price_per_hour, status, description, stadium_id, created_at, updated_at)
VALUES
-- Sân 1: Thượng Đình (3 sân)
(101, 'Sân 7A - Thượng Đình', 'FOOTBALL_7', 'Football', 350000.00, 'AVAILABLE', 'Sân 7 cỏ nhân tạo cao cấp', 1, NOW(), NOW()),
(102, 'Sân 7B - Thượng Đình', 'FOOTBALL_7', 'Football', 350000.00, 'AVAILABLE', 'Sân 7 cỏ nhân tạo cao cấp', 1, NOW(), NOW()),
(103, 'Sân 5A - Thượng Đình', 'FOOTBALL_5', 'Football', 250000.00, 'AVAILABLE', 'Sân mini 5 người', 1, NOW(), NOW()),

-- Sân 2: C500 (3 sân)
(104, 'Sân 7-1 C500', 'FOOTBALL_7', 'Football', 400000.00, 'AVAILABLE', 'Sân 7 người chuẩn giải đấu', 2, NOW(), NOW()),
(105, 'Sân 7-2 C500', 'FOOTBALL_7', 'Football', 400000.00, 'AVAILABLE', 'Sân 7 người chuẩn giải đấu', 2, NOW(), NOW()),
(106, 'Sân 11 C500', 'FOOTBALL_11', 'Football', 800000.00, 'AVAILABLE', 'Sân 11 người đại tiêu chuẩn', 2, NOW(), NOW()),

-- Sân 3: Thành Phát (4 sân)
(107, 'Sân 7-A Thành Phát', 'FOOTBALL_7', 'Football', 450000.00, 'AVAILABLE', 'Sân 7 cỏ FIFA 2024', 3, NOW(), NOW()),
(108, 'Sân 7-B Thành Phát', 'FOOTBALL_7', 'Football', 450000.00, 'AVAILABLE', 'Sân 7 cỏ FIFA 2024', 3, NOW(), NOW()),
(109, 'Sân 7-C Thành Phát', 'FOOTBALL_7', 'Football', 450000.00, 'AVAILABLE', 'Sân 7 cỏ FIFA 2024', 3, NOW(), NOW()),
(110, 'Sân 5-A Thành Phát', 'FOOTBALL_5', 'Football', 300000.00, 'AVAILABLE', 'Sân 5 mini tốc độ', 3, NOW(), NOW()),

-- Sân 4: Thủy Lợi (2 sân)
(111, 'Sân 7 Thủy Lợi', 'FOOTBALL_7', 'Football', 380000.00, 'AVAILABLE', 'Sân 7 Chùa Bộc', 4, NOW(), NOW()),
(112, 'Sân 5 Thủy Lợi', 'FOOTBALL_5', 'Football', 260000.00, 'AVAILABLE', 'Sân 5 Chùa Bộc', 4, NOW(), NOW()),

-- Sân 5: Đông Đô (2 sân)
(113, 'Sân 7 Đông Đô 1', 'FOOTBALL_7', 'Football', 350000.00, 'AVAILABLE', 'Sân 7 ven sông mát', 5, NOW(), NOW()),
(114, 'Sân 7 Đông Đô 2', 'FOOTBALL_7', 'Football', 350000.00, 'AVAILABLE', 'Sân 7 ven sông mát', 5, NOW(), NOW()),

-- Sân 6: An Khánh (3 sân)
(115, 'Sân 7 An Khánh 1', 'FOOTBALL_7', 'Football', 300000.00, 'AVAILABLE', 'Sân 7 KĐT Nam An Khánh', 6, NOW(), NOW()),
(116, 'Sân 7 An Khánh 2', 'FOOTBALL_7', 'Football', 300000.00, 'AVAILABLE', 'Sân 7 KĐT Nam An Khánh', 6, NOW(), NOW()),
(117, 'Sân 11 An Khánh', 'FOOTBALL_11', 'Football', 700000.00, 'AVAILABLE', 'Sân 11 người thi đấu lớn', 6, NOW(), NOW()),

-- Sân 7: Quảng Bá (2 sân)
(118, 'Sân 7 Tây Hồ', 'FOOTBALL_7', 'Football', 420000.00, 'AVAILABLE', 'Sân 7 ven hồ view đẹp', 7, NOW(), NOW()),
(119, 'Sân 7 Quảng Bá', 'FOOTBALL_7', 'Football', 420000.00, 'AVAILABLE', 'Sân 7 đèn led cao cấp', 7, NOW(), NOW()),

-- Sân 8: Phúc Anh (3 sân)
(120, 'Sân 7 PA-1', 'FOOTBALL_7', 'Football', 400000.00, 'AVAILABLE', 'Sân 7 người Mễ Trì', 8, NOW(), NOW()),
(121, 'Sân 7 PA-2', 'FOOTBALL_7', 'Football', 400000.00, 'AVAILABLE', 'Sân 7 người Mễ Trì', 8, NOW(), NOW()),
(122, 'Sân 5 PA', 'FOOTBALL_5', 'Football', 280000.00, 'AVAILABLE', 'Sân 5 mini Mễ Trì', 8, NOW(), NOW()),

-- Sân 9: Bách Khoa (2 sân)
(123, 'Sân 7 BK-A', 'FOOTBALL_7', 'Football', 380000.00, 'AVAILABLE', 'Sân 7 Đại Cồ Việt', 9, NOW(), NOW()),
(124, 'Sân 7 BK-B', 'FOOTBALL_7', 'Football', 380000.00, 'AVAILABLE', 'Sân 7 Đại Cồ Việt', 9, NOW(), NOW()),

-- Sân 10: Chuyên Việt Đà Nẵng (3 sân: Bóng đá & Pickleball)
(125, 'Sân 7 CV-1', 'FOOTBALL_7', 'Football', 320000.00, 'AVAILABLE', 'Sân 7 cỏ nhân tạo', 10, NOW(), NOW()),
(126, 'Sân 7 CV-2', 'FOOTBALL_7', 'Football', 320000.00, 'AVAILABLE', 'Sân 7 cỏ nhân tạo', 10, NOW(), NOW()),
(127, 'Sân Pickleball Chuyên Việt', 'PICKLEBALL', 'Pickleball', 150000.00, 'AVAILABLE', 'Sân Pickleball cao cấp', 10, NOW(), NOW()),

-- Sân 11: H2 Tân Bình (3 sân)
(128, 'Sân 5 H2-A', 'FOOTBALL_5', 'Football', 250000.00, 'AVAILABLE', 'Sân 5 cỏ đẹp Tân Bình', 11, NOW(), NOW()),
(129, 'Sân 5 H2-B', 'FOOTBALL_5', 'Football', 250000.00, 'AVAILABLE', 'Sân 5 cỏ đẹp Tân Bình', 11, NOW(), NOW()),
(130, 'Sân 7 H2', 'FOOTBALL_7', 'Football', 450000.00, 'AVAILABLE', 'Sân 7 người Phổ Quang', 11, NOW(), NOW()),

-- Sân 12: Chu Văn An (3 sân)
(131, 'Sân 5 CVA-1', 'FOOTBALL_5', 'Football', 220000.00, 'AVAILABLE', 'Sân 5 mini Bình Thạnh', 12, NOW(), NOW()),
(132, 'Sân 5 CVA-2', 'FOOTBALL_5', 'Football', 220000.00, 'AVAILABLE', 'Sân 5 mini Bình Thạnh', 12, NOW(), NOW()),
(133, 'Sân 7 CVA', 'FOOTBALL_7', 'Football', 400000.00, 'AVAILABLE', 'Sân 7 Chu Văn An', 12, NOW(), NOW()),

-- Sân 13: K334 Tân Phú (3 sân)
(134, 'Sân 5 K334-1', 'FOOTBALL_5', 'Football', 240000.00, 'AVAILABLE', 'Sân 5 cỏ kim cương', 13, NOW(), NOW()),
(135, 'Sân 5 K334-2', 'FOOTBALL_5', 'Football', 240000.00, 'AVAILABLE', 'Sân 5 cỏ kim cương', 13, NOW(), NOW()),
(136, 'Sân 7 K334', 'FOOTBALL_7', 'Football', 420000.00, 'AVAILABLE', 'Sân 7 Lê Trọng Tấn', 13, NOW(), NOW()),

-- Sân 14: Tao Đàn Quận 1 (3 sân)
(137, 'Sân 5 TĐ-1', 'FOOTBALL_5', 'Football', 300000.00, 'AVAILABLE', 'Sân 5 trung tâm Quận 1', 14, NOW(), NOW()),
(138, 'Sân 5 TĐ-2', 'FOOTBALL_5', 'Football', 300000.00, 'AVAILABLE', 'Sân 5 trung tâm Quận 1', 14, NOW(), NOW()),
(139, 'Sân 7 TĐ', 'FOOTBALL_7', 'Football', 500000.00, 'AVAILABLE', 'Sân 7 công viên Tao Đàn', 14, NOW(), NOW()),

-- Sân 15: Thảo Điền (2 sân)
(140, 'Sân 5 Thảo Điền VIP', 'FOOTBALL_5', 'Football', 350000.00, 'AVAILABLE', 'Sân 5 mini cao cấp', 15, NOW(), NOW()),
(141, 'Sân 7 Thảo Điền', 'FOOTBALL_7', 'Football', 600000.00, 'AVAILABLE', 'Sân 7 chuẩn quốc tế', 15, NOW(), NOW()),

-- Sân 16: Phú Thọ Quận 11 (3 sân)
(142, 'Sân 5 PT-1', 'FOOTBALL_5', 'Football', 250000.00, 'AVAILABLE', 'Sân 5 mini Phú Thọ', 16, NOW(), NOW()),
(143, 'Sân 5 PT-2', 'FOOTBALL_5', 'Football', 250000.00, 'AVAILABLE', 'Sân 5 mini Phú Thọ', 16, NOW(), NOW()),
(144, 'Sân 7 PT', 'FOOTBALL_7', 'Football', 450000.00, 'AVAILABLE', 'Sân 7 người Phú Thọ', 16, NOW(), NOW()),

-- Sân 17: Thanh Đa Bình Thạnh (2 sân)
(145, 'Sân 7 Thanh Đa 1', 'FOOTBALL_7', 'Football', 380000.00, 'AVAILABLE', 'Sân 7 ven sông Bình Quới', 17, NOW(), NOW()),
(146, 'Sân 7 Thanh Đa 2', 'FOOTBALL_7', 'Football', 380000.00, 'AVAILABLE', 'Sân 7 ven sông Bình Quới', 17, NOW(), NOW()),

-- Sân 18: Sala Thủ Thiêm (3 sân)
(147, 'Sân 7 Sala-1', 'FOOTBALL_7', 'Football', 550000.00, 'AVAILABLE', 'Sân 7 VIP view Landmark 81', 18, NOW(), NOW()),
(148, 'Sân 7 Sala-2', 'FOOTBALL_7', 'Football', 550000.00, 'AVAILABLE', 'Sân 7 VIP view Landmark 81', 18, NOW(), NOW()),
(149, 'Sân 11 Sala', 'FOOTBALL_11', 'Football', 1100000.00, 'AVAILABLE', 'Sân 11 đại tiêu chuẩn FIFA Pro', 18, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name), 
    type = VALUES(type),
    sport_type = VALUES(sport_type), 
    price_per_hour = VALUES(price_per_hour);

-- 4. Tạo các đơn đặt sân mẫu (Sample Bookings) để test kiểm tra ca đã đặt
INSERT INTO bookings (id, booking_date, start_time, end_time, total_price, status, payment_status, note, user_id, court_id, created_at, updated_at)
VALUES
(501, CURRENT_DATE, '17:30:00', '19:00:00', 525000.00, 'CONFIRMED', 'PAID', 'Trận đấu giao lưu nội bộ FC IT', 1001, 101, NOW(), NOW()),
(502, CURRENT_DATE, '20:30:00', '22:00:00', 525000.00, 'CONFIRMED', 'PAID', 'Đá cúp phong trào tuần', 1002, 101, NOW(), NOW()),
(503, DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '19:00:00', '20:30:00', 525000.00, 'CONFIRMED', 'PAID', 'Kèo giao hữu thứ 7', 1001, 101, NOW(), NOW()),
(504, CURRENT_DATE, '16:00:00', '17:30:00', 525000.00, 'CONFIRMED', 'PAID', 'Tập luyện thể lực ca chiều', 1002, 102, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    status = VALUES(status),
    payment_status = VALUES(payment_status);

-- 5. Tạo các bài đăng tìm kèo mẫu (Sample Match Posts - Ngày 25)
INSERT INTO match_posts (id, title, description, sport_type, play_date, start_time, end_time, max_participants, current_participants, price_per_person, status, creator_id, court_id, created_at, updated_at)
VALUES
(701, 'Cần tìm 3 bạn đá bóng sân 7 Thượng Đình tối nay', 'Đội mình thiếu 2-3 người đá dưỡng sinh vui vẻ sau giờ làm, trình độ phong trào trung bình. Anh em nào rảnh giao lưu nhé!', 'Football', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '19:00:00', '20:30:00', 5, 2, 50000.00, 'OPEN', 1001, 101, NOW(), NOW()),
(702, 'Kèo bóng đá 5 người - Giao lưu cọ xát C500', 'Tìm đối tác đá cánh trái và thủ môn, đội toàn anh em IT nhiệt tình, có nước uống sẵn.', 'Football', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), '17:30:00', '19:00:00', 4, 1, 60000.00, 'OPEN', 1002, 103, NOW(), NOW()),
(703, 'Giao lưu Pickleball đôi nam nữ Chuyên Việt Đà Nẵng', 'Nhóm mình có 2 người cần tìm thêm 2 bạn nữa đánh đôi Pickleball, sân đẹp có mái che.', 'Pickleball', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '18:00:00', '19:30:00', 4, 4, 45000.00, 'FULL', 1001, 126, NOW(), NOW()),
(704, 'Kèo bóng đá 7 người Sala Thủ Thiêm cuối tuần', 'Đội chuẩn bị đá giải cần tuyển thêm tiền đạo và trung vệ cứng, chia tiền sân theo đầu người.', 'Football', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), '19:30:00', '21:00:00', 6, 2, 80000.00, 'OPEN', 1002, 147, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    current_participants = VALUES(current_participants),
    status = VALUES(status);

-- 6. Tạo người tham gia kèo mẫu (Sample Match Participants)
INSERT INTO match_participants (id, match_post_id, user_id, status, joined_at, note, created_at, updated_at)
VALUES
(801, 701, 1002, 'ACCEPTED', NOW(), 'Đá được vị trí tiền vệ cánh hoặc hậu vệ', NOW(), NOW()),
(802, 704, 1001, 'ACCEPTED', NOW(), 'Đá trung vệ chắc chắn', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    status = VALUES(status),
    note = VALUES(note);

SET FOREIGN_KEY_CHECKS = 1;
