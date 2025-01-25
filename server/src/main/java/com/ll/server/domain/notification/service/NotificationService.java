package com.ll.server.domain.notification.service;


import com.ll.server.domain.notification.entity.Notification;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    //알림 저장
    @Transactional
    public void saveNotification(MockUser user, String message, String url){
        Notification notification=Notification.builder()
                .user(user)
                .message(message)
                .url(url)
                .hasRead(false)
                .build();

        notificationRepository.save(notification);
    }

    //안 읽은 알림들을 반환
    public List<Notification> findUnreadNotification(String nickname){
       return notificationRepository.findNotificationsByUser_NicknameAndHasReadIsFalse(nickname);
    }

    //RDB에 저장되므로, 오래된 읽은 알림은 주기적으로 삭제가 가능하도록 할 수도 있다. (Batch 이용. 주기적 삭제는 MVP가 아니므로 주기적 삭제는 나중에 구현할 수도)
    @Transactional
    public void deleteReadNotificationFrom(LocalDateTime from){

        notificationRepository.deleteNotificationsByCreateDateBeforeAndHasReadIsTrue(from);
    }


    @Transactional
    public void readNotification(Long id) {
        Optional<Notification> findOptional = notificationRepository.findById(id);
        if(findOptional.isEmpty()) return;

        Notification find=findOptional.get();
        find.setReadTrue();
    }
}
