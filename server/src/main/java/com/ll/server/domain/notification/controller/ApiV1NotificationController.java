package com.ll.server.domain.notification.controller;

import com.ll.server.domain.notification.dto.NotificationDTO;
import com.ll.server.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class ApiV1NotificationController {
    private final NotificationService notificationService;
    private final CopyOnWriteArrayList<SseEmitter> sseEmitters=new CopyOnWriteArrayList<>();

    @GetMapping
    public List<NotificationDTO> getNotificationByUsername(@RequestParam("nickname") String nickname){
        return notificationService.findUnreadNotification(nickname)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    //SSE 연결
    @GetMapping("/connect")
    public SseEmitter connect(){
        SseEmitter emitter=new SseEmitter();
        sseEmitters.add(emitter);

        emitter.onCompletion(()->sseEmitters.remove(emitter));
        emitter.onTimeout(()->{
            sseEmitters.remove(emitter);
            emitter.complete();
        });
        emitter.onError((e)->{
            sseEmitters.remove(emitter);
            emitter.completeWithError(e);
        });

        /*
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null){
            emitter.complete();
            return emitter;
        }

        String username=null;
        if(auth.getPrincipal() instance of UserDetails){
            username=((UserDetails)auth.getPrincipal()).getName();
        }


        Optional<User> userOptional = userService.findByUserName(username);

        if (!userOptional.isPresent()) {
            emitter.complete();
            return emitter;
        }

        String user = userOptional.get().getUserName();
         */

//        MockUser user1= MockUser.builder()
//                .email("1234")
//                .password("1234")
//                .role(MockRole.USER)
//                .profileUrl("1234")
//                .provider("google")
//                .providerId("123456")
//                .refreshToken("1234")
//                .nickname("user1")
//                .build();
//
//        new Thread(() -> {
//            try {
//                while (true) {
//                    if (sseEmitters.contains(emitter)) {
//                        List<Notification> notifications = notificationService.findUnreadNotification(user1);
//
//                        if (!notifications.isEmpty()) {
//                            for (Notification notification : notifications) {
//                                if (!notification.getHasRead()) { // 알림이 전송되지 않았는지 확인
//                                    emitter.send(SseEmitter.event()
//                                            .name("notification")
//                                            .data(new NotificationDTO(notification));
//
//                                    // 알림을 읽음 상태로 업데이트 및 전송됨 상태로 설정
//                                    notificationService.readNotification(notification.getId());
//                                }
//                            }
//                        }
//                    } else {
//                        break; // emitter가 이미 완료된 경우 반복문 종료
//                    }
//                    Thread.sleep(10000); // 10초마다 알림을 체크
//                }
//            } catch (Exception e) {
//                // emitter.completeWithError(e);
//            }
//        }).start();

        return emitter;
    }

}
