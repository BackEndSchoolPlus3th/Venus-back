package com.ll.server.domain.notification.service;


import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.notification.dto.NotificationDTO;
import com.ll.server.domain.notification.dto.NotificationInfinityScroll;
import com.ll.server.domain.notification.entity.Notification;
import com.ll.server.domain.notification.repository.NotificationRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.security.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    //알림 저장
    @Transactional
    public Notification saveNotification(Member member, String message, String url) {
        Notification notification = Notification.builder()
                .member(member)
                .message(message)
                .url(url)
                .build();

        return notificationRepository.save(notification);
    }

    //미처 못 보낸 알림들을 유저 닉네임으로 찾음. 유저 첫 접속 시 보내지지 않은 알림을 보내는 데 사용
    public List<Notification> findUnsentNotificationsByUsername(String nickname) {
        return notificationRepository.findNotificationsByMember_Nickname(nickname);
    }

    //미처 못 보낸 알림들을 유저 아이디로 찾음. 유저 첫 접속 시 보내지지 않은 알림을 보내는 데 사용
    public List<Notification> findUnsentNotificationsById(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));
        return notificationRepository.findNotificationsByMember(member);
    }


    //보내지긴 했으나 읽지 않은 알림들을 닉네임으로 찾음. 프론트엔드의 알림창 탭을 누르면 먼저 뜰 알림을 볼 수 있도록.
    public List<Notification> findUnreadNotificationsByUsername(String nickname) {
        return notificationRepository.findNotificationsByMember_Nickname(nickname);
    }


    //보내지긴 했으나 읽지 않은 알림들을 유저의 ID로 찾음. 프론트엔드의 알림창 탭을 누르면 먼저 뜰 알림을 볼 수 있도록.
    public Page<NotificationDTO> getSummary(Long userId, Pageable pageable) {
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));
        Page<Notification> result = notificationRepository.findNotificationsByMember(member, pageable);
        return new PageImpl<>(
                result.getContent().stream().map(NotificationDTO::new)
                        .collect(Collectors.toList())
                , result.getPageable()
                , result.getTotalElements()
        );
    }


    public List<Notification> findUnreadNotificationsById(Long userId) {
        checkUser(userId);
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));
        return notificationRepository.findNotificationsByMember(member);
    }

    //특정 유저의 모든 알림을 user의 ID로 찾음. 알림 목록이라는 것이 있다면 종류 불문 띄울 수 있도록.
    public Page<NotificationDTO> findAllNotificationsById(Long userId, Pageable pageable) {
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));
        Page<Notification> result = notificationRepository.findNotificationsByMember(member, pageable);

        return new PageImpl<>(
                result.getContent().stream().map(NotificationDTO::new).collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    public NotificationInfinityScroll firstGetAllNotifications(Long userId, int size) {
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));
        List<Notification> notifications = notificationRepository.findNotificationsByMember(member, Limit.of(size));

        List<NotificationDTO> dtos = notifications.stream().map(NotificationDTO::new).toList();
        return new NotificationInfinityScroll(dtos);
    }

    public NotificationInfinityScroll afterGetAllNotifications(Long userId, int size, Long lastId) {
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(ReturnCode.NOT_FOUND_ENTITY));
        List<Notification> notifications = notificationRepository.findNotificationsByMemberAndIdLessThanOrderByIdDesc(member, lastId, Limit.of(size));

        List<NotificationDTO> dtos = notifications.stream().map(NotificationDTO::new).toList();
        return new NotificationInfinityScroll(dtos);
    }

    //특정 유저의 모든 알림을 user의 이름로 찾음. 알림 목록이라는 것이 있다면 종류 불문 띄울 수 있도록.
    public Page<NotificationDTO> findAllNotificationsByUsername(String nickname, Pageable pageable) {
        Page<Notification> result = notificationRepository.findNotificationsByMember_Nickname(nickname, pageable);
        return new PageImpl<>(
                result.getContent().stream().map(NotificationDTO::new).collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    //RDB에 저장되므로, 오래된 읽은 알림은 주기적으로 삭제가 가능하도록 할 수도 있다.
    // (Batch 이용. 주기적 삭제는 MVP가 아니므로 주기적 삭제는 나중에 구현할 수도)
    @Transactional
    public void deleteReadNotificationFrom(LocalDateTime from) {
        notificationRepository.deleteNotificationsByCreateDateBefore(from);
    }


    //이 ID는 알림 엔티티의 ID로, 해당 알림을 읽었음을 나타냄. 알림창 탭의 특정 알림을 클릭한 상태라면 이 상태.
//    @Transactional
//    public Notification readNotification(Long notifyId) {
//        Notification find = notificationRepository.findById(notifyId).orElseThrow(() -> new CustomException(ReturnCode.NOT_FOUND_ENTITY));
//        checkUser(find.getMember().getId());
//
//        find.setReadTrue();
//
//        return find;
//    }

    private void checkUser(Long find) {
        Long readingMemberId = AuthUtil.getCurrentMemberId();
        if (!find.equals(readingMemberId)) throw new CustomException(ReturnCode.NOT_AUTHORIZED);
    }

    //이 ID는 알림 엔티티의 ID 집합으로, "모두 읽음으로 처리"를 할 때 효율적으로 쿼리를 쏘기 위해.
//    @Transactional
//    public List<Notification> readNotifications(List<Long> notifyIds) {
//        List<Notification> notifications = notificationRepository.findNotificationsByIdIn(notifyIds);
//        if (notifications.isEmpty()) {
//            throw new CustomException(ReturnCode.NOT_FOUND_ENTITY);
//        }
//
//
//        for (Notification notification : notifications) {
//            checkUser(notification.getMember().getId());
//            notification.setReadTrue();
//        }
//
//        return notifications;
//
//    }

    //이 ID는 알림 엔티티의 ID로, 해당 알림을 보냈음을 나타냄. 알림창 탭에 뜨는 상태라면 이 상태.
//    @Transactional
//    public void sendNotification(Long notifyId) {
//        Optional<Notification> findOptional = notificationRepository.findById(notifyId);
//        if (findOptional.isEmpty()) return;
//
//        Notification find = findOptional.get();
//        find.setSentTrue();
//    }

    //이 ID들은 알림 엔티티의 ID로, 효율적인 쿼리를 위해 사용 (알림 보내기)
//    @Transactional
//    public void sendNotifications(List<Long> notifyIds) {
//        List<Notification> notifications = notificationRepository.findNotificationsByIdIn(notifyIds);
//        if (notifications.isEmpty()) {
//            return;
//        }
//
//        for (Notification notification : notifications) {
//            notification.setReadTrue();
//        }
//    }

    //NotifiyAspect에서 보내야할 알림을 찾을 때 사용한다.
//    public List<Notification> findUnsentNotifications() {
//        return notificationRepository.findAll();
//    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
}
