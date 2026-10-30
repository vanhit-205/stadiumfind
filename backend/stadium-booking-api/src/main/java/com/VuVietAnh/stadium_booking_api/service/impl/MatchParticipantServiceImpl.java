package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.MatchParticipantRequest;
import com.VuVietAnh.stadium_booking_api.dto.request.NotificationCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.MatchParticipantResponse;
import com.VuVietAnh.stadium_booking_api.entity.MatchParticipant;
import com.VuVietAnh.stadium_booking_api.entity.MatchPost;
import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import com.VuVietAnh.stadium_booking_api.enums.NotificationType;
import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
import com.VuVietAnh.stadium_booking_api.exception.BadRequestException;
import com.VuVietAnh.stadium_booking_api.exception.DuplicateResourceException;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.MatchParticipantRepository;
import com.VuVietAnh.stadium_booking_api.repository.MatchPostRepository;
import com.VuVietAnh.stadium_booking_api.repository.UserRepository;
import com.VuVietAnh.stadium_booking_api.service.MatchParticipantService;
import com.VuVietAnh.stadium_booking_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchParticipantServiceImpl implements MatchParticipantService {

    private final MatchParticipantRepository participantRepository;
    private final MatchPostRepository matchPostRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MatchParticipantResponse joinMatch(MatchParticipantRequest request) {
        MatchPost matchPost = matchPostRepository.findById(request.getMatchPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Match post not found with id: " + request.getMatchPostId()));

        if (matchPost.getStatus() != MatchStatus.OPEN) {
            throw new BadRequestException("Match post is not open for joining");
        }

        if (matchPost.getCurrentParticipants() >= matchPost.getMaxParticipants()) {
            throw new BadRequestException("Match is already full");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (matchPost.getCreator().getId().equals(request.getUserId())) {
            throw new BadRequestException("Creator is already a participant of this match");
        }

        if (participantRepository.existsByMatchPostIdAndUserId(request.getMatchPostId(), request.getUserId())) {
            throw new DuplicateResourceException("User has already joined this match post");
        }

        MatchParticipant participant = MatchParticipant.builder()
                .matchPost(matchPost)
                .user(user)
                .status(ParticipantStatus.PENDING)
                .note(request.getNote())
                .build();

        MatchParticipant saved = participantRepository.save(participant);

        // === Gửi thông báo cho chủ kèo: có người xin tham gia ===
        sendJoinRequestNotificationToCreator(matchPost, user);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MatchParticipantResponse updateParticipantStatus(Long participantId, ParticipantStatus status) {
        MatchParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant request not found with id: " + participantId));

        ParticipantStatus oldStatus = participant.getStatus();
        participant.setStatus(status);

        MatchPost matchPost = participant.getMatchPost();

        // Cập nhật số lượng người tham gia thực tế
        if (oldStatus != ParticipantStatus.ACCEPTED && status == ParticipantStatus.ACCEPTED) {
            matchPost.setCurrentParticipants(matchPost.getCurrentParticipants() + 1);
            if (matchPost.getCurrentParticipants() >= matchPost.getMaxParticipants()) {
                matchPost.setStatus(MatchStatus.FULL);
            }
            matchPostRepository.save(matchPost);
        } else if (oldStatus == ParticipantStatus.ACCEPTED && status != ParticipantStatus.ACCEPTED) {
            matchPost.setCurrentParticipants(Math.max(1, matchPost.getCurrentParticipants() - 1));
            if (matchPost.getStatus() == MatchStatus.FULL) {
                matchPost.setStatus(MatchStatus.OPEN);
            }
            matchPostRepository.save(matchPost);
        }

        MatchParticipant updated = participantRepository.save(participant);

        // === Gửi thông báo cho người tham gia khi trạng thái thay đổi ===
        sendStatusChangeNotificationToParticipant(participant, matchPost, status);

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchParticipantResponse> getParticipantsByMatchPost(Long matchPostId) {
        return participantRepository.findByMatchPostId(matchPostId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchParticipantResponse> getMatchHistoryByUser(Long userId, Pageable pageable) {
        return participantRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchParticipantResponse> getMatchHistoryByUserAndStatus(Long userId, ParticipantStatus status, Pageable pageable) {
        return participantRepository.findByUserIdAndStatus(userId, status, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void leaveMatch(Long matchPostId, Long userId) {
        MatchParticipant participant = participantRepository.findByMatchPostIdAndUserId(matchPostId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant record not found"));

        if (participant.getStatus() == ParticipantStatus.ACCEPTED) {
            MatchPost matchPost = participant.getMatchPost();
            matchPost.setCurrentParticipants(Math.max(1, matchPost.getCurrentParticipants() - 1));
            if (matchPost.getStatus() == MatchStatus.FULL) {
                matchPost.setStatus(MatchStatus.OPEN);
            }
            matchPostRepository.save(matchPost);
        }

        participantRepository.delete(participant);
    }

    // ======================== Notification Helpers ========================

    /**
     * Gửi thông báo đến Chủ kèo khi có người gửi yêu cầu tham gia.
     */
    private void sendJoinRequestNotificationToCreator(MatchPost matchPost, User requester) {
        try {
            NotificationCreateRequest notification = NotificationCreateRequest.builder()
                    .userId(matchPost.getCreator().getId())
                    .title("Yêu cầu tham gia kèo mới")
                    .content(requester.getFullName() + " muốn tham gia kèo \"" + matchPost.getTitle() + "\". Hãy vào xem và duyệt!")
                    .type(NotificationType.MATCH_JOIN_REQUEST)
                    .referenceId(matchPost.getId())
                    .build();
            notificationService.createNotification(notification);
        } catch (Exception e) {
            // Không để lỗi notification làm gián đoạn luồng chính
            log.error("Failed to send join request notification to creator: {}", e.getMessage(), e);
        }
    }

    /**
     * Gửi thông báo đến Người tham gia khi trạng thái yêu cầu của họ thay đổi (Duyệt / Từ chối).
     */
    private void sendStatusChangeNotificationToParticipant(MatchParticipant participant, MatchPost matchPost, ParticipantStatus newStatus) {
        try {
            String title;
            String content;

            switch (newStatus) {
                case ACCEPTED:
                    title = "Yêu cầu tham gia đã được duyệt!";
                    content = "Bạn đã được chấp nhận tham gia kèo \"" + matchPost.getTitle() + "\". Hãy chuẩn bị sẵn sàng!";
                    break;
                case REJECTED:
                    title = "Yêu cầu tham gia bị từ chối";
                    content = "Rất tiếc, yêu cầu tham gia kèo \"" + matchPost.getTitle() + "\" của bạn đã không được chấp nhận.";
                    break;
                default:
                    // Không gửi thông báo cho các trạng thái khác (PENDING, CANCELLED)
                    return;
            }

            NotificationCreateRequest notification = NotificationCreateRequest.builder()
                    .userId(participant.getUser().getId())
                    .title(title)
                    .content(content)
                    .type(NotificationType.MATCH)
                    .referenceId(matchPost.getId())
                    .build();
            notificationService.createNotification(notification);
        } catch (Exception e) {
            // Không để lỗi notification làm gián đoạn luồng chính
            log.error("Failed to send status change notification to participant: {}", e.getMessage(), e);
        }
    }

    // ======================== Mapper ========================

    private MatchParticipantResponse mapToResponse(MatchParticipant participant) {
        MatchPost matchPost = participant.getMatchPost();
        User creator = matchPost.getCreator();

        return MatchParticipantResponse.builder()
                .id(participant.getId())
                .matchPostId(matchPost.getId())
                .userId(participant.getUser().getId())
                .userName(participant.getUser().getFullName())
                .userAvatar(participant.getUser().getAvatarUrl())
                .userPhoneNumber(participant.getUser().getPhoneNumber())
                .status(participant.getStatus())
                .joinedAt(participant.getJoinedAt())
                .note(participant.getNote())
                .createdAt(participant.getCreatedAt())
                .updatedAt(participant.getUpdatedAt())
                // Thông tin tóm tắt kèo (MatchPost Summary)
                .matchTitle(matchPost.getTitle())
                .sportType(matchPost.getSportType())
                .playDate(matchPost.getPlayDate())
                .startTime(matchPost.getStartTime())
                .endTime(matchPost.getEndTime())
                .stadiumName(matchPost.getCourt() != null && matchPost.getCourt().getStadium() != null
                        ? matchPost.getCourt().getStadium().getName() : null)
                .courtName(matchPost.getCourt() != null ? matchPost.getCourt().getName() : null)
                .maxParticipants(matchPost.getMaxParticipants())
                .currentParticipants(matchPost.getCurrentParticipants())
                .pricePerPerson(matchPost.getPricePerPerson())
                .matchStatus(matchPost.getStatus())
                .creatorName(creator != null ? creator.getFullName() : null)
                .creatorAvatar(creator != null ? creator.getAvatarUrl() : null)
                .creatorPhone(creator != null ? creator.getPhoneNumber() : null)
                .build();
    }
}

