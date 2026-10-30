package com.example.stadiumfind.features.match.domain.model

import java.time.LocalDateTime

/**
 * 1. Vòng đời trạng thái bài đăng tìm kèo (State Machine).
 */
enum class MatchPostStatus {
    DRAFT,
    OPEN,
    PENDING_CONFIRM,
    MATCHED,
    FULL,
    IN_PROGRESS,
    COMPLETED,
    EXPIRED,
    CANCELLED;

    val canAcceptNewRequests: Boolean
        get() = this == OPEN || this == PENDING_CONFIRM
}

/**
 * Phân loại quy tắc chia sẻ chi phí sân bóng.
 */
enum class CostSharingRule(val label: String) {
    SPLIT_50_50("Chia đôi 50 - 50"),
    SPLIT_70_30("Đội nhà 70% - Đội khách 30%"),
    LOSER_PAYS("Thua trả toàn bộ tiền sân"),
    FREE("Giao lưu miễn phí"),
    PER_SLOT("Chia đều theo từng slot vãng lai")
}

/**
 * Vị trí cầu thủ khi tuyển vãng lai.
 */
enum class PlayerPosition(val label: String) {
    GOALKEEPER("Thủ môn (GK)"),
    DEFENDER("Hậu vệ (DF)"),
    MIDFIELDER("Tiền vệ (MF)"),
    STRIKER("Tiền đạo (FW)"),
    ANY("Vị trí bất kỳ")
}

/**
 * Cỡ sân bóng đá.
 */
enum class PitchSize(val label: String) {
    FIVE_A_SIDE("Sân 5 người"),
    SEVEN_A_SIDE("Sân 7 người"),
    ELEVEN_A_SIDE("Sân 11 người")
}

/**
 * Trình độ bóng đá của đội.
 */
enum class SkillLevel(val label: String) {
    VERY_WEAK("Mới tập đá / Vui vẻ"),
    WEAK("Yếu - Trung bình yếu"),
    MEDIUM("Trung bình (Khá phổ biến)"),
    HARD("Trung bình khá - Đá cứng"),
    VERY_HARD("Chuyên nghiệp / Bán chuyên")
}

/**
 * 2. Phân loại cấu hình chi tiết theo 3 Loại Kèo (Sealed Interface).
 */
sealed interface MatchTypeSpec {
    data class TeamVsTeam(
        val costSharingRule: CostSharingRule = CostSharingRule.SPLIT_50_50,
        val homeJerseyColor: String? = null
    ) : MatchTypeSpec

    data class SlotRecruitment(
        val requiredPositions: Map<PlayerPosition, Int> = emptyMap(),
        val feePerSlotVnd: Long = 0L,
        val isGoalkeeperFree: Boolean = true
    ) : MatchTypeSpec

    data class LookingForPitch(
        val preferredDistrict: String = "",
        val maxRadiusKm: Int = 5,
        val costSharingRule: CostSharingRule = CostSharingRule.SPLIT_50_50
    ) : MatchTypeSpec
}

/**
 * 3. Nguồn gốc Sân bóng (3 nguồn: Đơn đặt sân đã có, Sân ngoài nhập tay, hoặc Chưa có sân).
 */
sealed interface PitchSourceSpec {
    data class VerifiedBooking(
        val bookingId: Long,
        val stadiumId: Long,
        val stadiumName: String,
        val courtId: Long,
        val courtName: String,
        val playDate: String,
        val startTime: String,
        val endTime: String,
        val pitchSize: PitchSize = PitchSize.SEVEN_A_SIDE,
        val pitchPriceVnd: Long = 0L
    ) : PitchSourceSpec

    data class ExternalPitch(
        val pitchName: String,
        val address: String,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val playDate: String,
        val startTime: String,
        val endTime: String,
        val pitchSize: PitchSize = PitchSize.SEVEN_A_SIDE
    ) : PitchSourceSpec

    data class NoPitchYet(
        val preferredAreaName: String,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val radiusKm: Int = 5,
        val preferredDate: String,
        val preferredStartTime: String,
        val durationMinutes: Int = 90,
        val pitchSize: PitchSize = PitchSize.SEVEN_A_SIDE
    ) : PitchSourceSpec
}

/**
 * 4. Quy tắc kiểm tra quyền chỉnh sửa theo BR-04.
 */
data class EditPermissionPolicy(
    val canEditDateTimeAndPitch: Boolean,
    val canEditFinancials: Boolean,
    val canEditSkillAndNotes: Boolean,
    val requiresOpponentConsent: Boolean
) {
    companion object {
        fun from(status: MatchPostStatus, approvedParticipantsCount: Int): EditPermissionPolicy {
            val isLockedByParticipants = status == MatchPostStatus.MATCHED ||
                    status == MatchPostStatus.FULL ||
                    approvedParticipantsCount > 0

            return EditPermissionPolicy(
                canEditDateTimeAndPitch = !isLockedByParticipants && status in listOf(MatchPostStatus.DRAFT, MatchPostStatus.OPEN),
                canEditFinancials = !isLockedByParticipants && status in listOf(MatchPostStatus.DRAFT, MatchPostStatus.OPEN),
                canEditSkillAndNotes = status in listOf(
                    MatchPostStatus.DRAFT,
                    MatchPostStatus.OPEN,
                    MatchPostStatus.PENDING_CONFIRM,
                    MatchPostStatus.MATCHED
                ),
                requiresOpponentConsent = isLockedByParticipants
            )
        }
    }
}
