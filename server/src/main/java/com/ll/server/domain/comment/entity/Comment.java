package com.ll.server.domain.comment.entity;

import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class Comment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Repost repost;

    @ManyToOne(fetch = FetchType.LAZY)
    private MockUser user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private LocalDateTime deletedAt=null;

//    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL,orphanRemoval = true)
//    @ToString.Exclude
//    @Builder.Default
//    @JsonIgnore
//    List<CommentMention> mentions=new ArrayList<>();
//
//    public void addMention(MockUser user){
//        CommentMention mention=CommentMention
//                .builder()
//                .comment(this)
//                .user(user)
//                .build();
//
//        mentions.add(mention);
//    }
}
