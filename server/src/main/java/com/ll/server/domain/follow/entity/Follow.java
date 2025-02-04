package com.ll.server.domain.follow.entity;


import com.ll.server.domain.member.entity.Member;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Follow extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    Member follower;//구독을 받는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    Member followee;//구독한 사람
}
