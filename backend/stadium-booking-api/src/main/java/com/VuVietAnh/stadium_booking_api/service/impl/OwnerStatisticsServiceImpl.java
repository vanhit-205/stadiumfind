package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.response.OwnerRevenueResponse;
import com.VuVietAnh.stadium_booking_api.entity.Booking;
import com.VuVietAnh.stadium_booking_api.enums.BookingStatus;
import com.VuVietAnh.stadium_booking_api.repository.BookingRepository;
import com.VuVietAnh.stadium_booking_api.service.OwnerStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerStatisticsServiceImpl implements OwnerStatisticsService {

    private final BookingRepository bookingRepository;

    @Override
    public OwnerRevenueResponse getRevenueStatistics(Long ownerId, Long stadiumId, String type, Integer year, Integer month) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();
        String statType = (type != null && type.equalsIgnoreCase("MONTHLY")) ? "MONTHLY" : "DAILY";

        List<Booking> allBookings = bookingRepository.findByStadiumOwnerIdOrderByCreatedAtDesc(ownerId);

        // Lọc theo sân nếu stadiumId được truyền
        List<Booking> filtered = allBookings.stream()
                .filter(b -> b.getCourt() != null && b.getCourt().getStadium() != null)
                .filter(b -> stadiumId == null || b.getCourt().getStadium().getId().equals(stadiumId))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());

        BigDecimal grandTotal = BigDecimal.ZERO;
        long totalCount = 0;
        List<OwnerRevenueResponse.RevenueItemResponse> items = new ArrayList<>();

        if ("MONTHLY".equals(statType)) {
            // Theo từng tháng trong năm targetYear (1 -> 12)
            Map<Integer, List<Booking>> monthMap = filtered.stream()
                    .filter(b -> b.getBookingDate().getYear() == targetYear)
                    .collect(Collectors.groupingBy(b -> b.getBookingDate().getMonthValue()));

            for (int m = 1; m <= 12; m++) {
                List<Booking> bList = monthMap.getOrDefault(m, Collections.emptyList());
                BigDecimal sum = bList.stream()
                        .map(Booking::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                long count = bList.size();

                grandTotal = grandTotal.add(sum);
                totalCount += count;

                String period = String.format("Tháng %02d/%d", m, targetYear);
                items.add(OwnerRevenueResponse.RevenueItemResponse.builder()
                        .period(period)
                        .revenue(sum)
                        .bookingCount(count)
                        .build());
            }
        } else {
            // Theo từng ngày trong tháng targetMonth và targetYear
            YearMonth ym = YearMonth.of(targetYear, targetMonth);
            int daysInMonth = ym.lengthOfMonth();

            Map<LocalDate, List<Booking>> dayMap = filtered.stream()
                    .filter(b -> b.getBookingDate().getYear() == targetYear && b.getBookingDate().getMonthValue() == targetMonth)
                    .collect(Collectors.groupingBy(Booking::getBookingDate));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate date = LocalDate.of(targetYear, targetMonth, d);
                List<Booking> bList = dayMap.getOrDefault(date, Collections.emptyList());
                BigDecimal sum = bList.stream()
                        .map(Booking::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                long count = bList.size();

                grandTotal = grandTotal.add(sum);
                totalCount += count;

                items.add(OwnerRevenueResponse.RevenueItemResponse.builder()
                        .period(date.format(dtf))
                        .revenue(sum)
                        .bookingCount(count)
                        .build());
            }
        }

        return OwnerRevenueResponse.builder()
                .totalRevenue(grandTotal)
                .totalBookings(totalCount)
                .items(items)
                .build();
    }
}
