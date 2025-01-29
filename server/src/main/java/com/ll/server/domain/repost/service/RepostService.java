package com.ll.server.domain.repost.service;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentWriteRequest;
import com.ll.server.domain.comment.entity.Comment;
import com.ll.server.domain.like.dto.LikeDTO;
import com.ll.server.domain.like.entity.Like;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
import com.ll.server.domain.news.news.entity.News;
import com.ll.server.domain.news.news.repository.NewsRepository;
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
    private final MockUserRepository userRepository;
    private final NewsRepository newsRepository;


    public RepostDTO findById(Long id){
        return new RepostDTO(repostRepository.findById(id).get());
    }

    @Transactional
    public String delete(Long id){
        Optional<Repost> target=repostRepository.findById(id);
        if(target.isPresent() && target.get().getDeletedAt()==null) {
            Repost repost=target.get();
            repost.setDeletedAt(LocalDateTime.now());
            repost.deleteComments();
            repost.deleteLikes();
            return "삭제 성공";
        }
        return "삭제 실패";
    }

    @Transactional
    public RepostDTO save(RepostWriteRequest request){
        MockUser user=userRepository.findById(request.getWriterId()).get();

        News news=newsRepository.findAll()
                .stream()
                .filter(n -> n.getId().equals(request.getNewsId()))
                .findFirst()
                .get();

        List<MockUser> metionedUserList=userRepository.findMockUsersByNicknameIn(request.getMentionedNames());
        Repost repost=
                Repost.builder()
                        .user(user)
                        .news(news)
                        .content(request.getContent())
                        .build();

        repostRepository.save(repost);

//        if(metionedUserList!=null && !metionedUserList.isEmpty()){
//            for(MockUser mentionedUser : metionedUserList){
//                repost.addMention(mentionedUser);
//            }
//        }

        return new RepostDTO(repost);
    }

    public List<RepostDTO> findByUserNickname(String nickname){
        return repostRepository.findRepostsByUser_Nickname(nickname)
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

    @Transactional
    public String deleteComment(Long postId, Long commentId) {
        Optional<Repost> repostOptional=repostRepository.findById(postId);
        if(repostOptional.isEmpty()) return "댓글 삭제 실패";

        Repost repost=repostOptional.get();

        if(repost.getDeletedAt()!=null) return "댓글 삭제 실패";

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
    public CommentDTO addComment(Long postId, CommentWriteRequest request) {
        Repost repost = getRepost(postId);
        if (repost == null) return null;

        MockUser user=userRepository.findById(request.getWriterId()).get();

//        List<MockUser> mentionedUsers=userRepository.findMockUsersByNicknameIn(request.getMentionedNames());
        List<MockUser> mentionedUsers=null;

        Comment comment=repost.addComment(user,mentionedUsers,request.getContent());

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
                .filter(like -> !like.getDeleted() && like.getUser().getId().equals(userId))
                .findFirst();

        if(likeOptional.isEmpty()) return "좋아요 취소 실패";

        Like like=likeOptional.get();
        like.setDeleted(true);

        return "좋아요 취소 성공";

    }

    @Transactional
    public LikeDTO markLike(Long repostId, Long userId) {
        Repost repost=getRepost(repostId);
        if(repost==null) return null;

        MockUser user=userRepository.findById(userId).get();
        return new LikeDTO(repost.addLike(user));

    }
}
