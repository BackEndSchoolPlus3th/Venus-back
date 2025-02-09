package com.ll.server.domain.notification.controller;

import com.ll.server.domain.notification.dto.NotificationDTO;
import com.ll.server.domain.notification.entity.Notification;
import com.ll.server.domain.notification.service.NotificationService;
import com.ll.server.global.jpa.BaseEntity;
import com.ll.server.global.response.response.ApiResponse;
import com.ll.server.global.response.response.CustomPage;
import com.ll.server.global.sse.EmitterManager;
import com.ll.server.global.utils.AccessTokenParser;
import com.ll.server.global.utils.MyConstant;
import com.ll.server.global.validation.PageLimitSizeValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    @Data
    private static class NotificationPageRequest{
        private int page=0;
        private int limit=20;
    }


//    //알림탭을 클릭
//    @GetMapping
//    public ApiResponse<List<NotificationDTO>> getAll(HttpServletRequest request){
////        Long memberId = AccessTokenParser.getMemberIdByCookie(jwtProvider,request);
//        Pageable pageable=PageRequest.of(0,10, Sort.by("id").descending());
//        Page<NotificationDTO> result=notificationService.getSummary(memberId, pageable);
//
//        return ApiResponse.of(result.getContent());
//    }
//
//
//    //알림 상세탭에 들어간 경우. (유저 엔티티 ID로 찾음)
//    @GetMapping("/details")
//    public ApiResponse<?> getNotificationsById(@RequestBody NotificationPageRequest request, HttpServletRequest httpServletRequest){
//        PageLimitSizeValidator.validateSize(request.getPage(),request.getLimit(), MyConstant.PAGELIMITATION);
//        Pageable pageable= PageRequest.of(request.getPage(),request.getLimit(),Sort.by("id").descending());
//
////        Long userId=AccessTokenParser.getMemberIdByCookie(jwtProvider,httpServletRequest);
//
//        Page<NotificationDTO> result= notificationService.findAllNotificationsById(userId,pageable);
//        return ApiResponse.of(CustomPage.of(result));
//    }
//
//    //알림 하나를 클릭했을 때
//    @GetMapping("/{notifyId}")
//    public ApiResponse<NotificationDTO> readNotification(@PathVariable("notifyId") Long notifyId){
//        return ApiResponse.of(new NotificationDTO(notificationService.readNotification(notifyId)));
//    }
//
//    //모두 읽음 버튼
//    @PostMapping()
//    public ApiResponse<List<NotificationDTO>> pressAllReadButton(HttpServletRequest request){
////        Long memberId=AccessTokenParser.getMemberIdByCookie(jwtProvider,request);
//
//        List<Notification> notifications=notificationService.findUnreadNotificationsById(memberId);
//        List<Long> notifyIds=notifications.stream()
//                .map(BaseEntity::getId)
//                .toList();
//
//        return ApiResponse.of(
//                notificationService.readNotifications(notifyIds)
//                .stream()
//                .map(NotificationDTO::new)
//                .collect(Collectors.toList())
//        );
//    }
//
//
//    //SSE 연결과 동시에 안 보낸 알림이 있으면 와바박 보냄
//    @GetMapping("/connect")
//    public void connect(HttpServletRequest request){
//
//
//        //방법1. 접속한 유저의 accessToken을 이용해 유저 정보를 알아내기
////        Long userId=AccessTokenParser.getMemberIdByCookie(jwtProvider,request).longValue();
//
//
//        //방법2. SecurityContext에서 Authentication(UserDetail)을 얻어오는 방법
//
//        SseEmitter emitter=new SseEmitter();
//        emitterManager.addEmitter(userId,emitter);
//
//        List<Notification> notifications=notificationService.findUnsentNotificationsById(userId);
//
//        if(notifications==null || notifications.isEmpty()){
//            return;
//        }
//
//        List<Long> successToSend=new ArrayList<>();
//
//        for(Notification notification:notifications){
//            NotificationDTO toSend=new NotificationDTO(notification);
//            if(emitterManager.sendNotification(userId,toSend)){
//                successToSend.add(toSend.getId());
//            }
//        }
//
//        if(successToSend.isEmpty()) return;
//
//        notificationService.sendNotifications(successToSend);
//
//    }
}
