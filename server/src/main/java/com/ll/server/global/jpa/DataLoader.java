package com.ll.server.global.jpa;

import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.follow.controller.ApiV1FollowController;
import com.ll.server.domain.follow.dto.FollowRequest;
import com.ll.server.domain.member.MemberRole;
import com.ll.server.domain.member.dto.MemberRequest;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.enums.NewsCategory;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.domain.repost.controller.ApiV1RepostController;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final MemberService memberService;
    private final ApiV1FollowController followController;
    private final ApiV1RepostController repostController;
    private final RepostRepository repostRepository;
//    private final RepostDocRepository repostDocRepository;
//    private final NewsDocRepository newsDocRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

//        repostDocRepository.deleteAll();
//        newsDocRepository.deleteAll();

        MemberRequest publisherSignup=MemberRequest.builder()
                .email("publisher@example.com")
                .nickname("Test Publisher")
                .password("1234")
                .role(MemberRole.PUBLISHER)
                .providerId("1234")
                .build();
        Member publisherUser=memberService.join(publisherSignup);

        List<Member> users=new ArrayList<>();
        for(int i=0;i<3;i++){
            MemberRequest signupRequest=MemberRequest.builder()
                    .email((i+1)+"@example.com")
                    .nickname("user"+(i+1))
                    .password("1234")
                    .role(MemberRole.MEMBER)
                    .providerId("1234")
                    .build();
            users.add(memberService.join(signupRequest));
        }

        Member user1=users.getFirst();
        Member user2=users.get(1);
        Member user3=users.get(2);

        FollowRequest followPublisher= FollowRequest.builder()
                .followerId(publisherUser.getId())
                .followeeId(user1.getId())
                .build();
        followController.follow(followPublisher);
        //user1이 테스트 언론사를 구독. 알림 1개째

        FollowRequest followRequest1= FollowRequest.builder()
                .followerId(user1.getId())
                .followeeId(user2.getId())
                .build();

        FollowRequest followRequest2= FollowRequest.builder()
                .followerId(user1.getId())
                .followeeId(user3.getId())
                .build();
        followController.follow(followRequest1);
        //user2가 user1을 구독. 알림 2개째

        followController.follow(followRequest2);
        //user3가 user1을 구독. 알림 3개째


        // News 객체 생성
        News news=News.builder()
                .category(NewsCategory.SOCIETY)
                .thumbnailUrl("http://example.com/thumbnail.jpg")
                .imageUrl("http://example.com/image.jpg")
                .contentUrl( "http://example.com")
                .author("John Doe")
                .title("Test News")
                .content("This is a test news content")
                .publisher("Test Publisher")
                .build();
        newsService.saveForTest(news);
        //user1에게 구독 사실을 전송. 알림 4개째.


        RepostWriteRequest repostRequest1=RepostWriteRequest.builder()
                .content("복잡하고 빠른 텍스트 검색이 중요하고 성능이 우선이라면 Elasticsearch에서 검색하고 DB에서 엔티티를 조회하는 방식이 유리합니다.")
                .mentions(user2.getNickname()+","+user3.getNickname())
                .newsId(news.getId())
                .writerId(user1.getId())
                .build();
        RepostDTO repostDTO1=repostController.write(repostRequest1);
        //user1이 작성한 글이므로 user2/3에게 알림이 가고, 멘션을 2와 3에게 했으므로 알림이 감. 알림 8개째.

        RepostWriteRequest repostRequest2=RepostWriteRequest.builder()
                .content("최신 데이터의 일관성이 더 중요하다면, DB에서 직접 검색하는 방법이 적합합니다.")
                .newsId(news.getId())
                .writerId(user2.getId())
                .build();

        RepostDTO repostDTO2= repostController.write(repostRequest2);

        RepostWriteRequest repostRequest3=RepostWriteRequest.builder()
                .content("대부분의 경우 Elasticsearch + DB 하이브리드 접근 방식이 효율적입니다.")
                .newsId(news.getId())
                .writerId(user3.getId())
                .build();

        RepostDTO repostDTO3=repostController.write(repostRequest3);

        for(int i=0;i<3;i++){
            CommentWriteRequest commentWriteRequest=
                    CommentWriteRequest.builder()
                            .content("1번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user3")
                            .build();
            repostController.addComment(repostDTO1.getRepostId(),commentWriteRequest);
        }
        //user2, user3가 user1의 리포스트에 댓글. user1, user2가 멘션. 알림 12개.

        for(int i=0;i<3;i++){
            CommentWriteRequest commentWriteRequest=
                    CommentWriteRequest.builder()
                            .content("2번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user1")
                            .build();
            repostController.addComment(repostDTO2.getRepostId(),commentWriteRequest);
        }
        //16개

        for(int i=0;i<3;i++){
            CommentWriteRequest commentWriteRequest=
                    CommentWriteRequest.builder()
                            .content("3번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user2")
                            .build();
            repostController.addComment(repostDTO3.getRepostId(),commentWriteRequest);
        }
        //20개

        repostController.markLike(repostDTO1.getRepostId(),user2.getId());
        repostController.markLike(repostDTO2.getRepostId(),user3.getId());
        repostController.markLike(repostDTO3.getRepostId(),user1.getId());
        //23개

    }
}