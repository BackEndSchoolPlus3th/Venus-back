package com.ll.server.domain.repost.service;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.comment.repository.CommentRepository;
import com.ll.server.domain.elasticsearch.repost.service.RepostDocService;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.domain.notification.Notify;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostOnly;
import com.ll.server.domain.repost.dto.RepostUnderNews;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.repost.repository.RepostRepository;
import com.ll.server.global.aws.s3.S3Service;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.response.exception.CustomRequestException;
import com.ll.server.global.security.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RepostService {
    private final RepostRepository repostRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final NewsService newsService;
    private final S3Service s3Service;
    private final RepostDocService repostDocService;

    @Transactional
    @Notify
    public RepostDTO save(RepostWriteRequest request, MultipartFile imageFile) throws IOException {
        Member user = memberService.getMemberById(request.getWriterId());//AuthUtil.getCurrentMemberId()

        News news = newsService.getNews(request.getNewsId());

        List<Member> metionedMemberList = memberService.getMembersByNickName(request.getMentionedNames());

        // ✅ S3 업로드 로직 추가
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = s3Service.uploadFile(imageFile, "repost-images");
        }

        Repost repost =
                Repost.builder()
                        .member(user)
                        .news(news)
                        .content(request.getContent())
                        .imageUrl(imageUrl)
                        .build();

        repostRepository.save(repost);

        if (metionedMemberList != null && !metionedMemberList.isEmpty()) {
            for (Member mentionedMember : metionedMemberList) {
                repost.addMention(mentionedMember);
            }
        }

        news.addRepost(repost);

        return new RepostDTO(repost);
    }

    public List<RepostDTO> findByUserNickname(String nickname) {
        return repostRepository.findRepostsByMember_NicknameAndDeletedAtIsNull(nickname)
                .stream()
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }

    public Page<RepostOnly> findAll(Pageable pageable) {
        Page<Repost> result = repostRepository.findAllByDeletedAtIsNull(pageable);

        return new PageImpl<>(
                result.getContent().stream()
                        .map(RepostOnly::new)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    public Page<RepostDTO> findByMember(Member member, Pageable pageable) {
        Page<Repost> result = repostRepository.findRepostsByMemberAndDeletedAtIsNull(member, pageable);

        return new PageImpl<>(
                result.getContent().stream()
                        .filter(repost -> repost.getDeletedAt() == null)
                        .map(RepostDTO::new)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements()
        );
    }

    public List<RepostOnly> firstGetAll(int size) {
        return repostRepository.findAllByDeletedAtIsNullOrderByCreateDateDescIdDesc(Limit.of(size))
                .stream()
                .map(RepostOnly::new).collect(Collectors.toList());
    }

    public List<RepostOnly> afterGetAll(int size, LocalDateTime lastTime, Long lastId) {
        return repostRepository.findAllByDeletedAtIsNullAndCreateDateBeforeAndIdLessThanOrderByCreateDateDescIdDesc(lastTime, lastId, Limit.of(size))
                .stream()
                .map(RepostOnly::new).collect(Collectors.toList());
    }

    public Page<CommentDTO> getCommentPage(Long postId, Pageable pageable) {
        Repost repost = getRepost(postId);

        Page<Comment> comments = commentRepository.findCommentsByRepost_IdAndDeletedAtIsNull(postId, pageable);

        return new PageImpl<>(
                comments.getContent().stream()
                        .map(CommentDTO::new).collect(Collectors.toList()),
                comments.getPageable(),
                comments.getTotalElements()
        );
    }

    public List<CommentDTO> firstGetComment(Long postId, int size) {
        Repost repost = getRepost(postId);

        List<Comment> result = commentRepository.findCommentsByRepost_IdAndDeletedAtIsNullOrderByCreateDateAscIdAsc(postId, Limit.of(size));

        return result.stream().map(CommentDTO::new).collect(Collectors.toList());
    }

    public List<CommentDTO> afterGetComment(Long postId, int size, LocalDateTime lastTime, long lastId) {
        Repost repost = getRepost(postId);
        List<Comment> result = commentRepository.findCommentsByRepost_IdAndIdGreaterThanAndCreateDateAfterAndDeletedAtIsNullOrderByCreateDateAscIdAsc(postId, lastId, lastTime, Limit.of(size));

        return result
                .stream()
                .map(CommentDTO::new).collect(Collectors.toList());
    }

    public List<CommentDTO> getAllComment(Long postId) {
        Repost repost = getRepost(postId);

        List<Comment> result = commentRepository.findCommentsByRepost_IdAndDeletedAtIsNull(postId);

        return result
                .stream()
                .map(CommentDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public void deleteRepost(Long postId) {
        Repost target = getRepost(postId);

        checkWriter(target.getMember());

        target.delete();

    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Repost repost = getRepost(postId);

        Comment target = getComment(commentId, repost);

        checkWriter(target.getMember());

        target.delete();
    }

    @Transactional
    public CommentDTO modifyComment(Long postId, Long commentId, String content) {
        Repost repost = getRepost(postId);

        Comment target = getComment(commentId, repost);

        checkWriter(target.getMember());

        target.setContent(content);

        return new CommentDTO(target);
    }

    private void checkWriter(Member target) {
        Long currentMemberId = AuthUtil.getCurrentMemberId();
        Long targetMemberId = target.getId();
        if (!targetMemberId.equals(currentMemberId)) throw new CustomException(ReturnCode.NOT_AUTHORIZED);
    }

    private Comment getComment(Long commentId, Repost repost) {
        Comment getComment = repost.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId) && comment.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));

        return getComment;
    }

    @Transactional
    @Notify
    public CommentDTO addComment(Long postId, CommentWriteRequest request) {
        Repost repost = getRepost(postId);

        Member member = memberService.getMemberById(request.getWriterId());//AuthUtil.getCurrentMemberId());

        List<Member> mentionedMembers = memberService.getMembersByNickName(request.getMentionedNames());

        Comment comment = repost.addComment(member, mentionedMembers, request.getContent());

        return new CommentDTO(comment);
    }

    public Repost getRepost(Long postId) {
        return repostRepository.findById(postId)
                .orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public RepostDTO getRepostDTOById(Long postId) {
        Repost repost = getRepost(postId);

        return new RepostDTO(repost);
    }

    public List<LikeDTO> getAllLike(Long repostId) {
        Repost repost = getRepost(repostId);

        return repost.getLikes().stream()
                .filter(like -> !like.getDeleted())
                .map(LikeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLike(Long repostId, Long userId) {
        Repost repost = getRepost(repostId);

        List<Like> likes = repost.getLikes();

        Like memberLike = likes.stream()
                .filter(like -> !like.getDeleted() && like.getMember().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));

        memberLike.setDeleted(true);
    }

    @Transactional
    @Notify
    public LikeDTO markLike(Long repostId, Long userId) {
        Repost repost = getRepost(repostId);

        Member user = memberService.getMemberById(userId);
        return new LikeDTO(repost.addLike(user));

    }

    @Transactional
    public void putPin(Long repostId) {
        Repost repost = getRepost(repostId);
        News news = repost.getNews();

        checkPublisher(news);

        Repost pinned = repostRepository.findRepostByNewsIdAndPinnedIsTrueAndDeletedAtIsNull(news.getId());
        //아무것도 찾지 못한 경우나 이미 지워진 경우
        if (pinned == null) {
            repost.setPinned(true);
            return;
        }

        //뭔가 있는 경우 대체
        pinned.setPinned(false);
        repost.setPinned(true);

    }

    private void checkPublisher(News news) {
        String currentUserNickname = AuthUtil.getCurrentMemberNickname();
        Collection<? extends GrantedAuthority> authorizations = AuthUtil.getAuth();

        if (!news.getPublisher().equals(currentUserNickname) || authorizations == null || !authorizations.contains("PUBLISHER")) {
            throw new CustomException(ReturnCode.NOT_AUTHORIZED);
        }
    }

    @Transactional
    public void pullPin(Long repostId) {
        Repost repost = getRepost(repostId);
        News news = repost.getNews();

        checkPublisher(news);

        repost.setPinned(false);
    }

    public Page<RepostUnderNews> getNewsRepostCursorPagination(Long newsId, Pageable pageable) {
        Page<Repost> reposts = repostRepository.getNewsReposts(newsId, pageable);
        return new PageImpl<>(
                reposts.getContent().stream()
                        .map(RepostUnderNews::new)
                        .collect(Collectors.toList()),
                reposts.getPageable(),
                reposts.getTotalElements()
        );
    }

    public List<RepostUnderNews> firstGetNewsRepost(Long newsId, int size) {
        List<Repost> reposts = repostRepository.firstGetNewsReposts(newsId, Limit.of(size));
        return reposts.stream()
                .map(RepostUnderNews::new)
                .collect(Collectors.toList());
    }

    public List<RepostUnderNews> afterGetNewsRepost(Long newsId, int size, LocalDateTime lastTime, Long lastId) {
        List<Repost> reposts = repostRepository.afterGetNewsReposts(newsId, lastTime, lastId, Limit.of(size));
        return reposts.stream()
                .map(RepostUnderNews::new)
                .collect(Collectors.toList());
    }

    public List<RepostDTO> searchContent(String keyword) {
        List<Repost> reposts = repostRepository.findByContentContainingAndDeletedAtIsNull(keyword);
        return reposts.stream()
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }

    public List<RepostDTO> getHotTopics() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<Repost> hotReposts = repostRepository.findTodayshotReposts(startOfDay, PageRequest.of(0,5));
        //top 5

        return hotReposts.stream()
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }
}
