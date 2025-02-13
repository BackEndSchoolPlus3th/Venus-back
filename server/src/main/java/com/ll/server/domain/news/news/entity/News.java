package com.ll.server.domain.news.news.entity;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.saved.entity.Saved;
import com.ll.server.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class News extends BaseEntity {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String publisher;
    private String author;
    private String imageUrl;
    private String thumbnailUrl;
    private String contentUrl;
    @Enumerated(EnumType.STRING)
    private NewsCategory category;
    private LocalDateTime publishedAt;

    @Builder.Default
    private LocalDateTime deletedAt = null;

    @OneToMany(mappedBy = "news")
    @Builder.Default
    private List<Repost> reposts = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Saved> savedList = new ArrayList<>();

    public void addRepost(Repost repost) {
        reposts.add(repost);
    }

    public void addSaved(Member member){
        for(Saved existing : savedList){
            if(existing.getMember().getId().equals(member.getId())) {
                existing.setDeleted(false);
                return;
            }
        }
        Saved saved = Saved.builder()
                .news(this)
                .member(member)
                .build();

        savedList.add(saved);
    }

    public void removeReposts() {
        reposts.forEach(
                Repost::delete
        );

    }

    public void removeSavedList(){
        savedList.forEach(Saved::delete);
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        setModifyDate(LocalDateTime.now());
        removeReposts();
        removeSavedList();
    }
}
