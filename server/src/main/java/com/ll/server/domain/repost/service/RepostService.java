package com.ll.server.domain.repost.service;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.comment.repository.CommentRepository;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.notification.Notify;
import com.ll.server.domain.repost.dto.RepostDTO;
import com.ll.server.domain.repost.dto.RepostWriteRequest;
import com.ll.server.domain.repost.entity.Repost;
import com.ll.server.domain.repost.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RepostService {
    private final RepostRepository repostRepository;
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;


    public RepostDTO findById(Long id){

        Repost repost=getRepost(id);
        if(repost==null) return null;

        return new RepostDTO(repost);
    }

    @Transactional
    public String delete(Long id){
        Repost repost=getRepost(id);
        if(repost==null)  return "삭제 실패";

        repost.setDeletedAt(LocalDateTime.now());
        repost.deleteComments();
        repost.deleteLikes();
        return "삭제 성공";
    }

    @Transactional
    @Notify
    public RepostDTO save(RepostWriteRequest request){
        Member user=memberRepository.findById(request.getWriterId()).get();

        News news=newsRepository.findById(request.getNewsId()).get();

        List<Member> metionedMemberList=memberRepository.findMembersByNicknameIn(request.getMentionedNames());
        Repost repost=
                Repost.builder()
                        .member(user)
                        .news(news)
                        .content(request.getContent())
                        .imageUrl(request.getImageUrl())
                        .build();

        repostRepository.save(repost);

        if(metionedMemberList!=null && !metionedMemberList.isEmpty()){
            for(Member mentionedMember : metionedMemberList){
                repost.addMention(mentionedMember);
            }
        }

        news.addRepost(repost);
        user.addRepost(repost);

        return new RepostDTO(repost);
    }

    public List<RepostDTO> findByUserNickname(String nickname){
        return repostRepository.findRepostsByMember_Nickname(nickname)
                .stream()
                .filter(repost->repost.getDeletedAt()==null)
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }

    public Page<RepostDTO> findAll(Pageable pageable){
        Page<Repost> result= repostRepository.findAll(pageable);

        return new PageImpl<>(
            result.getContent().stream()
                    .filter(repost -> repost.getDeletedAt()==null)
                    .map(RepostDTO::new)
                    .collect(Collectors.toList()),
            result.getPageable(),
            result.getTotalElements()
        );
    }

    public Page<CommentDTO> getAllComment(Long postId,Pageable pageable) {
        Repost repost = getRepost(postId);
        if (repost == null) return null;

        Page<Comment> comments = commentRepository.findCommentsByRepostId(postId,pageable);

        return new PageImpl<>(
                comments.getContent().stream()
                        .filter(comment -> comment.getDeletedAt()==null)
                        .map(CommentDTO::new).collect(Collectors.toList()),
                comments.getPageable(),
                comments.getTotalElements()
        );
    }

    @Transactional
    public String deleteComment(Long postId, Long commentId) {
        Repost repost=getRepost(postId);
        if(repost==null) return "댓글 삭제 실패";

        Comment target = getComment(commentId, repost);
        if (target == null) return "댓글 삭제 실패";

        target.setDeletedAt(LocalDateTime.now());

        return "댓글 삭제 성공";
    }

    @Transactional
    public CommentDTO modifyComment(Long postId, Long commentId, String content) {
        Repost repost = getRepost(postId);
        if (repost == null) return null;

        Comment target = getComment(commentId, repost);
        if (target == null) return null;

        target.setContent(content);
        target.setModifyDate(LocalDateTime.now());

        return new CommentDTO(target);
    }

    private Comment getComment(Long commentId, Repost repost) {
        Optional<Comment> commentOptional= repost.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId) && comment.getDeletedAt()==null)
                .findFirst();
        return commentOptional.orElse(null);
    }

    @Transactional
    @Notify
    public CommentDTO addComment(Long postId, CommentWriteRequest request) {
        Repost repost = getRepost(postId);
        if (repost == null) return null;

        Member member=memberRepository.findById(request.getWriterId()).get();

        List<Member> mentionedMembers=memberRepository.findMembersByNicknameIn(request.getMentionedNames());

        Comment comment=repost.addComment(member,mentionedMembers,request.getContent());
        member.addComment(comment);

        return new CommentDTO(comment);
    }

    private Repost getRepost(Long postId) {
        Optional<Repost> repostOptional=repostRepository.findById(postId);
        if(repostOptional.isEmpty()) return null;

        Repost repost=repostOptional.get();
        if(repost.getDeletedAt()!=null) return null;

        return repost;
    }

    public List<LikeDTO> getAllLike(Long repostId) {
        Repost repost = getRepost(repostId);
        if(repost ==null) return null;

        return repost.getLikes().stream()
                .filter(like -> !like.getDeleted())
                .map(LikeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public String deleteLike(Long repostId, Long userId) {
        Repost repost=getRepost(repostId);
        if(repost==null) return "좋아요 취소 실패";

        List<Like> likes=repost.getLikes();

        Optional<Like> likeOptional=
        likes.stream()
                .filter(like -> !like.getDeleted() && like.getMember().getId().equals(userId))
                .findFirst();

        if(likeOptional.isEmpty()) return "좋아요 취소 실패";

        Like like=likeOptional.get();
        like.setDeleted(true);

        return "좋아요 취소 성공";

    }

    @Transactional
    @Notify
    public LikeDTO markLike(Long repostId, Long userId) {
        Repost repost=getRepost(repostId);
        if(repost==null) return null;

        Member user=memberRepository.findById(userId).get();
        Like like=repost.addLike(user);
        user.addLike(like);

        return new LikeDTO(like);

    }
}
