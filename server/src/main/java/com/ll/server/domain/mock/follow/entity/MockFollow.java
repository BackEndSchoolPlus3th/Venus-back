package com.ll.server.domain.mock.follow.entity;


import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MockFollow extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    MockUser follower;//구독을 받는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    MockUser followee;//구독한 사람
}
