package com.ll.server.domain.repost.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NewsRepostInfinityResponse {
    private List<RepostUnderNews> reposts;
    private long count;
    private long lastId;
    private LocalDateTime lastTime;

    public NewsRepostInfinityResponse(List<RepostUnderNews> reposts) {
        if (reposts == null || reposts.isEmpty()) {
            this.reposts = null;
            count = 0;
            lastTime = null;
            lastId = -1;
        } else {
            this.reposts = reposts;
            count = reposts.size();
            lastId = reposts.getLast().getRepostId();
            lastTime = reposts.getLast().getCreateDate();
        }
    }
}
