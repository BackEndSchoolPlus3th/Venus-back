package com.ll.server.global.jpa;

import com.ll.server.domain.comment.repository.CommentRepository;
import com.ll.server.domain.follow.controller.ApiV1FollowController;
import com.ll.server.domain.follow.service.FollowService;
import com.ll.server.domain.like.repository.LikeRepository;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.news.news.service.NewsFetchService;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.domain.repost.controller.ApiV1RepostController;
import com.ll.server.domain.repost.repository.RepostRepository;
import com.ll.server.domain.repost.service.RepostService;
import com.ll.server.domain.saved.repository.SavedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final MemberService memberService;
    private final ApiV1FollowController followController;
    private final ApiV1RepostController repostController;
    private final RepostRepository repostRepository;
    private final NewsFetchService newsFetchService;
    private final MemberRepository memberRepository;
    private final RepostService repostService;
    private final CommentRepository commentRepository;
    private final FollowService followService;
    private final LikeRepository likeRepository;
    private final SavedRepository savedRepository;
//    private final RepostDocRepository repostDocRepository;
//    private final NewsDocRepository newsDocRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

//        repostDocRepository.deleteAll();
//        newsDocRepository.deleteAll();
//        if(likeRepository.findAll().isEmpty()) {
//            Member first = memberRepository.findAll().get(0);
//            Member celebrity = memberRepository.findAll().get(1);
//            List<Repost> reposts = repostRepository.findAll();
//
//            for(Repost repost : reposts){
//                repostService.markLike(repost.getId(),celebrity.getId());
//                repostService.markLike(repost.getId(),first.getId());
//            }
//        }
//
//        if(savedRepository.findAll().isEmpty()){
//            Member celebrity = memberRepository.findAll().get(1);
//            List<News> newsList = newsRepository.findAll();
//
//            for(News news : newsList){
//                newsService.scrapNews(celebrity.getId(),news.getId());
//            }
//        }else{
//            Member celebrity = memberRepository.findAll().get(1);
//            List<News> newsList = newsRepository.findAll();
//
//            for(News news : newsList){
//                newsService.unscrapNews(celebrity.getId(),news.getId());
//            }
//        }

/*
        Faker faker=new Faker(Locale.KOREA);

        NewsResponse newsResponse=null;
        List<NewsDTO> newsDTO=null;
        if(newsRepository.findAll().isEmpty()){
            newsResponse=newsFetchService.fetchNews();
            newsDTO= Objects.requireNonNull(newsResponse).getNewsList();
        };


        if(memberRepository.findMemberByEmail("1@example.com").isEmpty()){
            SignupRequestDto signupRequest= SignupRequestDto.builder()
                    .email("1@example.com")
                    .nickname("user1")
                    .password("1234")
                    .build();
            memberService.signup(signupRequest);
        }

        if(memberRepository.findAll().size()<3){
            Member first=memberRepository.findAll().getFirst();
            Member celebrity = memberRepository.findAll().get(1);

            List<Member> members=new ArrayList<>();
            for(int i=0;i<50;i++) {
                SignupRequestDto signupReq = SignupRequestDto.builder()
                        .email("a"+(i+1)+"@example.com")
                        .nickname("exam"+(i+1))
                        .password("1234")
                        .build();
                members.add(memberService.signup(signupReq));
            }

            for(Member member : members){
                followService.save(celebrity.getId(), member.getId());
                followService.save(member.getId(),celebrity.getId());
            }

        }


        if(repostRepository.findAll().isEmpty()){
            Long targetId=0L;
            if(newsDTO==null || newsDTO.isEmpty()){
                newsDTO = newsRepository.findAll().stream().map(NewsDTO::new).collect(Collectors.toList());
                newsResponse=new NewsResponse(newsDTO);
                targetId = newsDTO.get(0).getId();
            }
            Member member=memberRepository.findMemberByEmail("1@example.com").get();
            for(int i=0;i<newsResponse.getCount()/2;i++) {

                if(i<5){
                    RepostWriteRequest repostRequest = RepostWriteRequest.builder()
                            .content(String.join("\r\n", faker.lorem().sentences(3)))
                            .mentions(member.getNickname() + "," + member.getNickname())
                            .newsId(targetId)
                            .writerId(member.getId())
                            .build();
                    RepostDTO repostDTO = repostService.save(repostRequest,null);
                    repostService.putPin(repostDTO.getRepostId());
                    continue;
                }

                RepostWriteRequest repostRequest = RepostWriteRequest.builder()
                        .content(String.join("\n", faker.lorem().sentences(3)))
                        .mentions(member.getNickname() + "," + member.getNickname())
                        .newsId(targetId)
                        .writerId(member.getId())
                        .build();
                repostController.write(repostRequest, null);
            }
        }

        if(commentRepository.findAll().isEmpty()){
            Repost repost = repostRepository.findAll().getFirst();
            Member member=memberRepository.findMemberByEmail("1@example.com").get();
            for(int i=0;i<100;i++){
                CommentWriteRequest req = CommentWriteRequest.builder()
                        .content(faker.famousLastWords().lastWords())
                        .writerId(member.getId())
                        .build();
                repostService.addComment(repost.getId(), req);
            }
        }

        */

/*

        SignupRequestDto publisherSignup=SignupRequestDto.builder()
                .email("publisher@example.com")
                .nickname("Test Publisher")
                .password("1234")
                .build();
        Member publisherUser=memberService.signup(publisherSignup);

        List<Member> users=new ArrayList<>();
        for(int i=0;i<3;i++){
            SignupRequestDto signupRequest=SignupRequestDto.builder()
                    .email((i+1)+"@example.com")
                    .nickname("user"+(i+1))
                    .password("1234")
                    .build();
            users.add(memberService.signup(signupRequest));
        }

        Member user1=users.getFirst();
        Member user2=users.get(1);
        Member user3=users.get(2);

        FollowRequest followPublisher= FollowRequest.builder()
                .followerId(publisherUser.getId())
                .followeeId(user1.getId())
                .build();
        followService.save(followPublisher.getFollowerId(),followPublisher.getFollowerId());
        //user1이 테스트 언론사를 구독. 알림 1개째

        FollowRequest followRequest1= FollowRequest.builder()
                .followerId(user1.getId())
                .followeeId(user2.getId())
                .build();

        FollowRequest followRequest2= FollowRequest.builder()
                .followerId(user1.getId())
                .followeeId(user3.getId())
                .build();
        followService.save(followRequest1.getFollowerId(),followRequest1.getFolloweeId());
        //user2가 user1을 구독. 알림 2개째

        followService.save(followRequest2.getFollowerId(),followRequest2.getFolloweeId());
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
        // ✅ test_img_1.jpg 로드 (클래스패스에서 읽기)
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/test_img_1.jpg");
        if (inputStream == null) {
            throw new FileNotFoundException("File not found in classpath: static/test_img_1.jpg");
        }
        // InputStream을 바이트 배열로 변환
        byte[] imageBytes1 = inputStream.readAllBytes();

        // MockMultipartFile로 변환
        MultipartFile imageFile1 = new MockMultipartFile("file", "test_img_1.jpg", "image/jpeg", imageBytes1);

        // 📤 저장
        RepostDTO repostDTO1 = repostService.save(repostRequest1, imageFile1);
        //user1이 작성한 글이므로 user2/3에게 알림이 가고, 멘션을 2와 3에게 했으므로 알림이 감. 알림 8개째.

        RepostWriteRequest repostRequest2=RepostWriteRequest.builder()
                .content("최신 데이터의 일관성이 더 중요하다면, DB에서 직접 검색하는 방법이 적합합니다.")
                .newsId(news.getId())
                .writerId(user2.getId())
                .build();

        // ✅ test_img_2.jpg 로드 (클래스패스에서 읽기)
        InputStream inputStream2 = getClass().getClassLoader().getResourceAsStream("static/test_img_2.jpg");
        if (inputStream2 == null) {
            throw new FileNotFoundException("File not found in classpath: static/test_img_2.jpg");
        }
        // InputStream을 바이트 배열로 변환
        byte[] imageBytes2 = inputStream2.readAllBytes();

        // MockMultipartFile로 변환
        MultipartFile imageFile2 = new MockMultipartFile("file", "test_img_2.jpg", "image/jpeg", imageBytes2);

        // 📤 저장
        RepostDTO repostDTO2 = repostService.save(repostRequest2, imageFile2);

        RepostWriteRequest repostRequest3=RepostWriteRequest.builder()
                .content("대부분의 경우 Elasticsearch + DB 하이브리드 접근 방식이 효율적입니다.")
                .newsId(news.getId())
                .writerId(user3.getId())
                .build();
        // ✅ test_img_3.jpg 로드 (클래스패스에서 읽기)
        InputStream inputStream3 = getClass().getClassLoader().getResourceAsStream("static/test_img_3.png");
        if (inputStream3 == null) {
            throw new FileNotFoundException("File not found in classpath: static/test_img_3.png");
        }
        // InputStream을 바이트 배열로 변환
        byte[] imageBytes3 = inputStream3.readAllBytes();

        // MockMultipartFile로 변환
        MultipartFile imageFile3 = new MockMultipartFile("file", "test_img_3.png", "image/png", imageBytes3);

        // 📤 저장
        RepostDTO repostDTO3 = repostService.save(repostRequest3, imageFile3);

        for(int i=0;i<3;i++){
            CommentWriteRequest commentWriteRequest=
                    CommentWriteRequest.builder()
                            .content("1번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user3")
                            .build();
            repostService.addComment(repostDTO1.getRepostId(),commentWriteRequest);
        }
        //user2, user3가 user1의 리포스트에 댓글. user1, user2가 멘션. 알림 12개.

        for(int i=0;i<3;i++){
            CommentWriteRequest commentWriteRequest=
                    CommentWriteRequest.builder()
                            .content("2번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user1")
                            .build();
            repostService.addComment(repostDTO2.getRepostId(),commentWriteRequest);
        }
        //16개

        for(int i=0;i<3;i++){
            CommentWriteRequest commentWriteRequest=
                    CommentWriteRequest.builder()
                            .content("3번 글 연습댓글"+(i+1))
                            .writerId(users.get(i).getId())
                            .mentions("user2")
                            .build();
            repostService.addComment(repostDTO3.getRepostId(),commentWriteRequest);
        }
        //20개

        repostService.markLike(repostDTO1.getRepostId(),user2.getId());
        repostService.markLike(repostDTO2.getRepostId(),user3.getId());
        repostService.markLike(repostDTO3.getRepostId(),user1.getId());
        //23개

*/

    }
}