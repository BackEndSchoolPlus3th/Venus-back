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
                .build();

        notificationRepository.save(notification);
    }

    //미처 못 보낸 알림들을 유저 닉네임으로 찾음. 유저 첫 접속 시 보내지지 않은 알림을 보내는 데 사용
    public List<Notification> findUnsentNotificationsByUsername(String nickname){
       return notificationRepository.findNotificationsByUser_NicknameAndHasSentIsFalse(nickname);
    }

    //미처 못 보낸 알림들을 유저 아이디로 찾음. 유저 첫 접속 시 보내지지 않은 알림을 보내는 데 사용
    public List<Notification> findUnsentNotificationsById(Long userId){
        return notificationRepository.findNotificationsByUser_IdAndHasSentIsFalse(userId);
    }


    //보내지긴 했으나 읽지 않은 알림들을 닉네임으로 찾음. 프론트엔드의 알림창 탭을 누르면 먼저 뜰 알림을 볼 수 있도록.
    public List<Notification> findUnreadNotificationsByUsername(String nickname){
        return notificationRepository.findNotificationsByUser_NicknameAndHasSentIsTrueAndHasReadIsFalse(nickname);
    }


    //보내지긴 했으나 읽지 않은 알림들을 유저의 ID로 찾음. 프론트엔드의 알림창 탭을 누르면 먼저 뜰 알림을 볼 수 있도록.
    public List<Notification> findUnreadNotificationsById(Long userId){
        return notificationRepository.findNotificationsByUser_IdAndHasSentIsTrueAndHasReadIsFalse(userId);
    }

    //특정 유저의 모든 알림을 user의 ID로 찾음. 알림 목록이라는 것이 있다면 종류 불문 띄울 수 있도록.
    public List<Notification> findAllNotificationsById(Long userId){
        return notificationRepository.findNotificationsByUser_Id(userId);
    }

    //특정 유저의 모든 알림을 user의 이름로 찾음. 알림 목록이라는 것이 있다면 종류 불문 띄울 수 있도록.
    public List<Notification> findAllNotificationsByUsername(String nickname){
        return notificationRepository.findNotificationsByUser_Nickname(nickname);
    }

    //RDB에 저장되므로, 오래된 읽은 알림은 주기적으로 삭제가 가능하도록 할 수도 있다.
    // (Batch 이용. 주기적 삭제는 MVP가 아니므로 주기적 삭제는 나중에 구현할 수도)
    @Transactional
    public void deleteReadNotificationFrom(LocalDateTime from){

        notificationRepository.deleteNotificationsByCreateDateBeforeAndHasReadIsTrue(from);
    }


    //이 ID는 알림 엔티티의 ID로, 해당 알림을 읽었음을 나타냄. 알림창 탭의 특정 알림을 클릭한 상태라면 이 상태.
    @Transactional
    public Notification readNotification(Long notifyId) {
        Optional<Notification> findOptional = notificationRepository.findByIdAndHasReadIsFalse(notifyId);
        if(findOptional.isEmpty()) return null;

        Notification find=findOptional.get();
        find.setReadTrue();

        return find;
    }

    //이 ID는 알림 엔티티의 ID 집합으로, "모두 읽음으로 처리"를 할 때 효율적으로 쿼리를 쏘기 위해.
    @Transactional
    public List<Notification> readNotifications(List<Long> notifyIds){
        List<Notification> notifications = notificationRepository.findNotificationsByIdInAndHasReadIsFalse(notifyIds);
        if(notifications.isEmpty()){
            return null;
        }

        for(Notification notification : notifications){
            notification.setReadTrue();
        }

        return notifications;

    }

    //이 ID는 알림 엔티티의 ID로, 해당 알림을 보냈음을 나타냄. 알림창 탭에 뜨는 상태라면 이 상태.
    @Transactional
    public void sendNotification(Long notifyId){
        Optional<Notification> findOptional = notificationRepository.findByIdAndHasSentIsFalse(notifyId);
        if(findOptional.isEmpty()) return;

        Notification find=findOptional.get();
        find.setSentTrue();
    }

    //이 ID들은 알림 엔티티의 ID로, 효율적인 쿼리를 위해 사용
    public void sendNotifications(List<Long> notifyIds) {
        List<Notification> notifications = notificationRepository.findNotificationsByIdInAndHasSentIsFalse(notifyIds);
        if(notifications.isEmpty()){
            return;
        }

        for(Notification notification : notifications){
            notification.setReadTrue();
        }
    }

    //NotifiyAspect에서 보내야할 알림을 찾을 때 사용한다.
    public List<Notification> findUnsentNotifications(){
        return notificationRepository.findNotificationsByHasSentIsFalse();
    }

}
