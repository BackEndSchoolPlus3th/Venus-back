package com.ll.server.global.sse;

import com.ll.server.domain.notification.dto.NotificationDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmitterManager {
    private final Map<Long, SseEmitter> userIdSseEmitterMap = new ConcurrentHashMap<>();

    public void addEmitter(Long memberId, SseEmitter emitter) {
        if (userIdSseEmitterMap.containsKey(memberId)) return;

        userIdSseEmitterMap.put(memberId, emitter);
        emitter.onCompletion(() -> userIdSseEmitterMap.remove(memberId));

        emitter.onTimeout(() -> {
            userIdSseEmitterMap.remove(memberId);
            emitter.complete();
        });

        emitter.onError((e) -> {
            userIdSseEmitterMap.remove(memberId);
            emitter.completeWithError(e);
        });
    }

    public Boolean sendNotification(Long userId, NotificationDTO notificationDTO) {
        if (!userIdSseEmitterMap.containsKey(userId)) return false;

        try {
            userIdSseEmitterMap.get(userId)
                    .send(SseEmitter.event()
                            .name("notification/" + userId)
                            .data(notificationDTO));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
