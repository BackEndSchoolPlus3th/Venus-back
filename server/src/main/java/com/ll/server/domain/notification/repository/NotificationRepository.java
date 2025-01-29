package com.ll.server.domain.notification.repository;

import com.ll.server.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByUser_NicknameAndHasSentIsTrueAndHasReadIsFalse(String nickname);

    List<Notification> findNotificationsByUser_NicknameAndHasSentIsFalse(String nickname);

    void deleteNotificationsByCreateDateBeforeAndHasReadIsTrue(LocalDateTime from);

    List<Notification> findNotificationsByHasReadIsFalse();

    List<Notification> findNotificationsByUser_IdAndHasSentIsTrueAndHasReadIsFalse(Long id);

    List<Notification> findNotificationsByUser_IdAndHasSentIsFalse(Long user_id);

    List<Notification> findNotificationsByHasSentIsFalse();

    List<Notification> findNotificationsByUser_Id(Long userId);

    List<Notification> findNotificationsByUser_Nickname(String nickname);

    List<Notification> findNotificationsByIdInAndHasReadIsFalse(List<Long> notifyIds);

    List<Notification> findNotificationsByIdInAndHasSentIsFalse(List<Long> notifyIds);

    Optional<Notification> findByIdAndHasSentIsFalse(Long notifyId);

    Optional<Notification> findByIdAndHasReadIsFalse(Long notifyId);
}
