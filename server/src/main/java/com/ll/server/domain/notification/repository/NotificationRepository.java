package com.ll.server.domain.notification.repository;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.notification.entity.Notification;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByMember_Nickname(String nickname);


    void deleteNotificationsByCreateDateBefore(LocalDateTime from);


    Page<Notification> findNotificationsByMember(Member member, Pageable pageable);

    List<Notification> findNotificationsByMember(Member member, Limit pageable);

    List<Notification> findNotificationsByMember(Member member);



    //전체 알림 조회. 무한스크롤 첫번째 시도
    List<Notification> findNotificationsByMemberOrderByIdDesc(Member user, Limit limit);

    //전체 알림 조회. 무한스크롤 이후 시도
    List<Notification> findNotificationsByMemberAndIdLessThanOrderByIdDesc(Member user, Long lastId, Limit limit);

    Page<Notification> findNotificationsByMember_Nickname(String nickname, Pageable pageable);

    List<Notification> findNotificationsByIdIn(List<Long> notifyIds);


    Optional<Notification> findById(Long notifyId);

}
