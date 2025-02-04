package com.ll.server.domain.notification.controller;

import com.ll.server.domain.notification.dto.NotificationDTO;
import com.ll.server.domain.notification.entity.Notification;
import com.ll.server.domain.notification.service.NotificationService;
import com.ll.server.global.jpa.BaseEntity;
import com.ll.server.global.sse.EmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class ApiV1NotificationController {
    private final NotificationService notificationService;
    private final EmitterManager emitterManager;
    //private final JwtProvider jwtProvider;


    @GetMapping
    public List<NotificationDTO> getAll(){
        return notificationService.getAll()
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    //알림 상세탭에 들어간 경우. (닉네임으로 찾음)
    @GetMapping("/{nickname}")
    public List<NotificationDTO> getNotificationsByUsername(@PathVariable("nickname") String nickname){
        return notificationService.findAllNotificationsByUsername(nickname)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }


    //알림 상세탭에 들어간 경우. (유저 엔티티 ID로 찾음)
//    @GetMapping("/{id}")
//    public List<NotificationDTO> getNotificationsById(@RequestParam("userId") Long userId){
//        return notificationService.findAllNotificationsById(userId)
//                .stream()
//                .map(NotificationDTO::new)
//                .collect(Collectors.toList());
//    }

    //알림 하나를 클릭했을 때
    @GetMapping("/{notifyId}")
    public NotificationDTO readNotification(@PathVariable("notifyId") Long notifyId){
        return new NotificationDTO(notificationService.readNotification(notifyId));
    }

    //모두 읽음 버튼
    @PostMapping("/{memberId}")
    public List<NotificationDTO> pressAllReadButton(@PathVariable("memberId") Long memberId){
        List<Notification> notifications=notificationService.findUnreadNotificationsById(memberId);
        List<Long> notifyIds=notifications.stream()
                .map(BaseEntity::getId)
                .toList();

        return notificationService.readNotifications(notifyIds)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    /*
    HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String accessToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
            }
        }
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        System.out.println(claims);
        String email = (String) claims.get("email");
        System.out.println(email);
     */


    //SSE 연결과 동시에 안 보낸 알림이 있으면 와바박 보냄
    @GetMapping("/connect/{userId}")
    public void connect(@PathVariable("userId") Long userId
                        //,HttpServletRequest request
                        ){
        /*

        //방법1. 접속한 유저의 accessToken을 이용해 유저 정보를 알아내기

        Cookie[] cookies = request.getCookies();
        String accessToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
            }
        }
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        Long userId = claims.get("id");

        */

        //방법2. SecurityContext에서 Authentication(UserDetail)을 얻어오는 방법

        SseEmitter emitter=new SseEmitter();
        emitterManager.addEmitter(userId,emitter);

        List<Notification> notifications=notificationService.findUnsentNotificationsById(userId);

        if(notifications==null || notifications.isEmpty()){
            return;
        }

        List<Long> successToSend=new ArrayList<>();

        for(Notification notification:notifications){
            NotificationDTO toSend=new NotificationDTO(notification);
            if(emitterManager.sendNotification(userId,toSend)){
                successToSend.add(toSend.getId());
            }
        }

        if(successToSend.isEmpty()) return;

        notificationService.sendNotifications(successToSend);

    }

}
