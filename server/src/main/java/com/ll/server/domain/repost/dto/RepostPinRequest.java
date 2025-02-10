package com.ll.server.domain.repost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class RepostPinRequest {
    private boolean pinned;
}
