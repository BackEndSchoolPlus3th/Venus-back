package com.ll.server.domain.notification.dto;

import com.ll.server.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationDTO {
    private Long id;
    private String message;
    private String url;

    public NotificationDTO(Notification notification){
        id=notification.getId();
        message=notification.getMessage();
        url=notification.getUrl();
    }
}
