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

        return new RepostDTO(repostRepository.findById(id).get());
    }


    // repost 삭제
    @Transactional
    public String checkDelete_R(Long repostId, Long userId) {
        Optional<Repost> target = repostRepository.findById(repostId);
        Repost repost = target.orElseThrow(() -> new RuntimeException("Repost not found"));
        System.out.println("repost = " + repost);
        Member repostMember = repost.getMember();
        Long writerId = repostMember.getId();
        System.out.println("writerId = " + writerId);

        if (writerId.equals(userId)) {
            if (target.isPresent() && target.get().getDeletedAt() == null) {
                repost.setDeletedAt(LocalDateTime.now());
                repost.deleteComments();
                repost.deleteLikes();
            }
        } else {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        return "삭제 성공";
    }

    // 댓글 삭제
    @Transactional
    public String checkDelete_C(Long repostId, Long id, Long userId) {
        Optional<Comment> target = commentRepository.findByRepostIdAndId(repostId,id);
        Comment comment = target.orElseThrow(() -> new RuntimeException("Repost not found"));
        System.out.println("comment = " + comment);
        Member commentMember = comment.getMember();
        Long writerId = commentMember.getId();
        System.out.println("writerId = " + writerId);

        if (writerId.equals(userId)) {
            if (target.isPresent() && target.get().getDeletedAt() == null)
                comment.setDeletedAt(LocalDateTime.now());
        } else {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        return "삭제 성공";
    }


    // 댓글 수정
    @Transactional
    public CommentDTO modify_C(Long postId, Long commentId, String content, Long userId) {
        // id 같은지 확인
        Optional<Comment> target = commentRepository.findByRepostIdAndId(postId, commentId);
        Comment comment = target.orElseThrow(() -> new RuntimeException("Comment not found"));
        Member commentMember = comment.getMember();
        Long writerId = commentMember.getId();


        if (writerId.equals(userId)) {
            if (target.isPresent()) {
                comment.setContent(content);
                comment.setModifyDate(LocalDateTime.now());
            }
        } else {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        return new CommentDTO(comment);
    }

    @Transactional
    @Notify
    public RepostDTO save(RepostWriteRequest request){
        Member user=memberRepository.findById(request.getWriterId()).get();

        News news=newsRepository.findAll()
                .stream()
                .filter(n -> n.getId().equals(request.getNewsId()))
                .findFirst()
                .get();

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

        return new RepostDTO(repost);
    }

    public List<RepostDTO> findByUserNickname(String nickname){
        return repostRepository.findRepostsByMember_Nickname(nickname)
                .stream()
                .filter(repost->repost.getDeletedAt()==null)
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }

    public List<RepostDTO> findAll(){
        return repostRepository.findAll()
                .stream()
                .filter(repost->repost.getDeletedAt()==null)
                .map(RepostDTO::new)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getAllComment(Long postId) {
        Repost repost = getRepost(postId);
        if (repost == null) return null;

        return repost.getComments().stream()
                .filter(comment -> comment.getDeletedAt()==null)
                .map(CommentDTO::new)
                .collect(Collectors.toList());

    }

//    @Transactional
//    public String deleteComment(Long postId, Long commentId) {
//        Optional<Repost> repostOptional=repostRepository.findById(postId);
//        if(repostOptional.isEmpty()) return "댓글 삭제 실패";
//
//        Repost repost=repostOptional.get();
//
//        if(repost.getDeletedAt()!=null) return "댓글 삭제 실패";
//
//        Comment target = getComment(commentId, repost);
//        if (target == null) return "댓글 삭제 실패";
//
//        target.setDeletedAt(LocalDateTime.now());
//
//        return "댓글 삭제 성공";
//    }

//    @Transactional
//    public CommentDTO modifyComment(Long postId, Long commentId, String content) {
//        Repost repost = getRepost(postId);
//        if (repost == null) return null;
//
//        Comment target = getComment(commentId, repost);
//        if (target == null) return null;
//
//        target.setContent(content);
//        target.setModifyDate(LocalDateTime.now());
//
//        return new CommentDTO(target);
//    }

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
        return new LikeDTO(repost.addLike(user));

    }



}
