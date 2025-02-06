package com.ll.server.domain.repost.service;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.comment.repository.CommentRepository;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.member.service.MemberService;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.news.news.service.NewsService;
import com.ll.server.domain.notification.Notify;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostOnly;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.repost.repository.RepostRepository;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RepostService {
    private final RepostRepository repostRepository;
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final NewsService newsService;

    @Transactional
    @Notify
    public RepostDTO save(RepostWriteRequest request) {
        Member user = memberService.getMemberById(request.getWriterId());

        News news = newsService.getNews(request.getNewsId());

        List<Member> metionedMemberList = memberService.getMembersByNickName(request.getMentionedNames());
        Repost repost =
                Repost.builder()
                        .member(user)
                        .news(news)
                        .content(request.getContent())
                        .imageUrl(request.getImageUrl())
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
        return repostRepository.findRepostsByMember_Nickname(nickname)
                .stream()
                .filter(repost -> repost.getDeletedAt() == null)
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }

    public Page<RepostOnly> findAll(Pageable pageable){
        Page<Repost> result= repostRepository.findAll(pageable);

        return new PageImpl<>(
            result.getContent().stream()
                    .filter(repost -> repost.getDeletedAt()==null)
                    .map(RepostOnly::new)
                    .collect(Collectors.toList()),
            result.getPageable(),
            result.getTotalElements()
        );
    }

    public Page<CommentDTO> getAllComment(Long postId, Pageable pageable) {
        Repost repost = getRepost(postId);
        if (repost == null) return null;

        Page<Comment> comments = commentRepository.findCommentsByRepostId(postId, pageable);

        return new PageImpl<>(
                comments.getContent().stream()
                        .filter(comment -> comment.getDeletedAt() == null)
                        .map(CommentDTO::new).collect(Collectors.toList()),
                comments.getPageable(),
                comments.getTotalElements()
        );
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Repost repost = getRepost(postId);

        Comment target = getComment(commentId, repost);

        target.delete();
    }

    @Transactional
    public CommentDTO modifyComment(Long postId, Long commentId, String content) {
        Repost repost = getRepost(postId);

        Comment target = getComment(commentId, repost);

        target.setContent(content);
        target.setModifyDate(LocalDateTime.now());

        return new CommentDTO(target);
    }

    private Comment getComment(Long commentId, Repost repost) {
        Comment getComment = repost.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId) && comment.getDeletedAt() == null)
                .findFirst().orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
        return getComment;
    }

    @Transactional
    @Notify
    public CommentDTO addComment(Long postId, CommentWriteRequest request) {
        Repost repost = getRepost(postId);

        Member member = memberService.getMemberById(request.getWriterId());

        List<Member> mentionedMembers = memberService.getMembersByNickName(request.getMentionedNames());

        Comment comment = repost.addComment(member, mentionedMembers, request.getContent());

        return new CommentDTO(comment);
    }

    public Repost getRepost(Long postId) {
        return repostRepository.findById(postId).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
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
}
