package com.ll.server.domain.notification.repository;

import com.ll.server.domain.notification.entity.Notification;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByMember_NicknameAndHasSentIsTrueAndHasReadIsFalse(String nickname);

    List<Notification> findNotificationsByMember_NicknameAndHasSentIsFalse(String nickname);

    void deleteNotificationsByCreateDateBeforeAndHasReadIsTrue(LocalDateTime from);

    List<Notification> findNotificationsByHasReadIsFalse();

    Page<Notification> findNotificationsByMember_IdAndHasSentIsTrueAndHasReadIsFalseOrderByIdDesc(Long memberId, Pageable pageable);

    List<Notification> findNotificationsByMember_IdAndHasSentIsTrueAndHasReadIsFalseOrderByIdDesc(Long memberId);

    List<Notification> findNotificationsByMember_IdAndHasSentIsFalse(Long user_id);

    List<Notification> findNotificationsByHasSentIsFalse();

    Page<Notification> findNotificationsByMember_IdOrderByIdDesc(Long userId, Pageable pageable);

    //전체 알림 조회. 무한스크롤 첫번째 시도
    List<Notification> findNotificationsByMember_IdOrderByIdDesc(Long userId, Limit limit);

    //전체 알림 조회. 무한스크롤 이후 시도
    List<Notification> findNotificationsByMember_IdAndIdLessThanOrderByIdDesc(Long userId, Long lastId, Limit limit);

    Page<Notification> findNotificationsByMember_Nickname(String nickname, Pageable pageable);

    List<Notification> findNotificationsByIdInAndHasReadIsFalse(List<Long> notifyIds);

    List<Notification> findNotificationsByIdInAndHasSentIsFalse(List<Long> notifyIds);

    Optional<Notification> findByIdAndHasSentIsFalse(Long notifyId);

    Optional<Notification> findByIdAndHasReadIsFalse(Long notifyId);
}
