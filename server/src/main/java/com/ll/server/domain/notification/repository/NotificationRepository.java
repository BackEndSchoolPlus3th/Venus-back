package com.ll.server.domain.notification.repository;

import com.ll.server.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByUser_NicknameAndHasReadIsFalse(String nickname);

    void deleteNotificationsByCreateDateBeforeAndHasReadIsTrue(LocalDateTime from);
}
