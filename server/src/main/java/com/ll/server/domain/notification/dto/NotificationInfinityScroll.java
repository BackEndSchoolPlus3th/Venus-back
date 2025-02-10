package com.ll.server.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationInfinityScroll {
    private List<NotificationDTO> notifications;
    private long lastId;

    public NotificationInfinityScroll(List<NotificationDTO> notifications){
        if(notifications==null || notifications.isEmpty()){
            this.notifications=null;
            lastId=-1;
        }else{
            this.notifications=notifications;
            lastId=notifications.getLast().getId();
        }
    }
}
