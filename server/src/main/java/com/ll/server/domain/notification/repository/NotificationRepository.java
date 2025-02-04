package com.ll.server.domain.notification.repository;

import com.ll.server.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByMember_NicknameAndHasSentIsTrueAndHasReadIsFalse(String nickname);

    List<Notification> findNotificationsByMember_NicknameAndHasSentIsFalse(String nickname);

    void deleteNotificationsByCreateDateBeforeAndHasReadIsTrue(LocalDateTime from);

    List<Notification> findNotificationsByHasReadIsFalse();

    List<Notification> findNotificationsByMember_IdAndHasSentIsTrueAndHasReadIsFalse(Long id);

    List<Notification> findNotificationsByMember_IdAndHasSentIsFalse(Long user_id);

    List<Notification> findNotificationsByHasSentIsFalse();

    List<Notification> findNotificationsByMember_Id(Long userId);

    List<Notification> findNotificationsByMember_Nickname(String nickname);

    List<Notification> findNotificationsByIdInAndHasReadIsFalse(List<Long> notifyIds);

    List<Notification> findNotificationsByIdInAndHasSentIsFalse(List<Long> notifyIds);

    Optional<Notification> findByIdAndHasSentIsFalse(Long notifyId);

    Optional<Notification> findByIdAndHasReadIsFalse(Long notifyId);
}
