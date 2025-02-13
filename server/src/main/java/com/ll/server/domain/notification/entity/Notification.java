package com.ll.server.domain.notification.entity;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    private String message;
    @Builder.Default
    private Boolean hasRead = false;
    @Builder.Default
    private Boolean hasSent = false;
    private String url;

    public void setReadTrue() {
        hasRead = true;
    }

    public void setSentTrue() {
        hasSent = true;
    }
}
