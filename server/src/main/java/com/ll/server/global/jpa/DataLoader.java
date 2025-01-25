package com.ll.server.global.jpa;

import com.ll.server.domain.mock.comment.dto.MockCommentWriteRequest;
import com.ll.server.domain.mock.follow.controller.ApiV1MockFollowController;
import com.ll.server.domain.mock.follow.dto.MockFollowRequest;
import com.ll.server.domain.mock.repost.controller.ApiV1MockRepostController;
import com.ll.server.domain.mock.repost.dto.MockRepostDTO;
import com.ll.server.domain.mock.repost.dto.MockRepostWriteRequest;
import com.ll.server.domain.mock.repost.repository.MockRepostRepository;
import com.ll.server.domain.mock.user.dto.MockUserSignupRequest;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.service.MockUserService;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.entity.Publisher;
import com.ll.server.domain.news.news.enums.Country;
import com.ll.server.domain.news.news.repository.NewsPublisherRepository;
import com.ll.server.domain.news.news.repository.NewsRepository;
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
    private final NewsPublisherRepository newsPublisherRepository;
    private final MockUserService userService;
    private final ApiV1MockFollowController followController;
    private final ApiV1MockRepostController repostController;
    private final MockRepostRepository repostRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Publisher 객체 생성 (Country enum 값 사용)
        Publisher publisher = new Publisher("Test Publisher", Country.USA); // Publisher는 Country 값을 받습니다.
        newsPublisherRepository.save(publisher);
        // News 객체 생성
        News news = new News("Test News", "This is a test news content", publisher, "John Doe",
                "http://example.com/image.jpg", "http://example.com/thumbnail.jpg",
                "http://example.com");
        newsRepository.save(news);

        List<MockUser> users=new ArrayList<>();
        for(int i=0;i<3;i++){
            MockUserSignupRequest signupRequest=MockUserSignupRequest.builder()
                    .email((i+1)+"@example.com")
                    .nickname("user"+(i+1))
                    .password("1234")
                    .build();
            users.add(userService.signup(signupRequest));
        }

        MockUser user1=users.getFirst();
        MockUser user2=users.get(1);
        MockUser user3=users.get(2);


        MockFollowRequest followRequest1=MockFollowRequest.builder()
                .followerId(user1.getId())
                .followeeId(user2.getId())
                .build();

        MockFollowRequest followRequest2=MockFollowRequest.builder()
                .followerId(user1.getId())
                .followeeId(user3.getId())
                .build();
        followController.follow(followRequest1);
        followController.follow(followRequest2);

        MockRepostWriteRequest repostRequest1=MockRepostWriteRequest.builder()
                .content("연습용1")
                .mentions(user2.getNickname()+","+user3.getNickname())
                .newsId(news.getId())
                .writerId(user1.getId())
                .build();
        MockRepostDTO repostDTO1=repostController.write(repostRequest1);

        MockRepostWriteRequest repostRequest2=MockRepostWriteRequest.builder()
                .content("연습용2")
                .newsId(news.getId())
                .writerId(user2.getId())
                .build();

        MockRepostDTO repostDTO2= repostController.write(repostRequest2);

        MockRepostWriteRequest repostRequest3=MockRepostWriteRequest.builder()
                .content("연습용3")
                .newsId(news.getId())
                .writerId(user3.getId())
                .build();

        MockRepostDTO repostDTO3=repostController.write(repostRequest3);

        for(int i=0;i<3;i++){
            MockCommentWriteRequest commentWriteRequest=
                    MockCommentWriteRequest.builder()
                            .content("1번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user3")
                            .build();
            repostController.addComment(repostDTO1.getRepostId(),commentWriteRequest);
        }

        for(int i=0;i<3;i++){
            MockCommentWriteRequest commentWriteRequest=
                    MockCommentWriteRequest.builder()
                            .content("2번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user1")
                            .build();
            repostController.addComment(repostDTO2.getRepostId(),commentWriteRequest);
        }

        for(int i=0;i<3;i++){
            MockCommentWriteRequest commentWriteRequest=
                    MockCommentWriteRequest.builder()
                            .content("3번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user2")
                            .build();
            repostController.addComment(repostDTO3.getRepostId(),commentWriteRequest);
        }

        repostController.markLike(repostDTO1.getRepostId(),user2.getId());
        repostController.markLike(repostDTO2.getRepostId(),user3.getId());
        repostController.markLike(repostDTO3.getRepostId(),user1.getId());

    }
}