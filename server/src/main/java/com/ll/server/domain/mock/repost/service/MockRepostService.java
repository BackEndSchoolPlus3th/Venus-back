package com.ll.server.domain.mock.repost.service;

import com.ll.server.domain.mock.comment.dto.MockCommentDTO;
import com.ll.server.domain.mock.comment.dto.MockCommentWriteRequest;
import com.ll.server.domain.mock.comment.entity.MockComment;
import com.ll.server.domain.mock.like.dto.MockLikeDTO;
import com.ll.server.domain.mock.like.entity.MockLike;
import com.ll.server.domain.mock.news.entity.MockNews;
import com.ll.server.domain.mock.news.repository.MockNewsRepository;
import com.ll.server.domain.mock.repost.dto.MockRepostDTO;
import com.ll.server.domain.mock.repost.dto.MockRepostWriteRequest;
import com.ll.server.domain.mock.repost.entity.MockRepost;
import com.ll.server.domain.mock.repost.repository.MockRepostRepository;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
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
public class MockRepostService {
    private final MockRepostRepository repostRepository;
    private final MockUserRepository userRepository;
    private final MockNewsRepository newsRepository;


    public MockRepostDTO findById(Long id){
        return new MockRepostDTO(repostRepository.findById(id).get());
    }

    @Transactional
    public String delete(Long id){
        Optional<MockRepost> target=repostRepository.findById(id);
        if(target.isPresent() && target.get().getDeletedAt()==null) {
            MockRepost repost=target.get();
            repost.setDeletedAt(LocalDateTime.now());
            repost.deleteComments();
            repost.deleteLikes();
            return "삭제 성공";
        }
        return "삭제 실패";
    }

    @Transactional
    public MockRepostDTO save(MockRepostWriteRequest request){
        MockUser user=userRepository.findById(request.getWriterId()).get();

        MockNews news=newsRepository.findAll()
                .stream()
                .filter(n -> n.getId().equals(request.getNewsId()))
                .findFirst()
                .get();

        List<MockUser> metionedUserList=userRepository.findMockUsersByNicknameIn(request.getMentionedNames());
        MockRepost repost=
                MockRepost.builder()
                        .user(user)
                        .news(news)
                        .content(request.getContent())
                        .build();

        repostRepository.save(repost);

        if(metionedUserList!=null && !metionedUserList.isEmpty()){
            for(MockUser mentionedUser : metionedUserList){
                repost.addMention(mentionedUser);
            }
        }

        return new MockRepostDTO(repost);
    }

    public List<MockRepostDTO> findByUserNickname(String nickname){
        return repostRepository.findMockRepostsByUser_Nickname(nickname)
                .stream()
                .filter(repost->repost.getDeletedAt()==null)
                .map(MockRepostDTO::new)
                .collect(Collectors.toList());
    }

    public List<MockRepostDTO> findAll(){
        return repostRepository.findAll()
                .stream()
                .filter(repost->repost.getDeletedAt()==null)
                .map(MockRepostDTO::new)
                .collect(Collectors.toList());
    }

    public List<MockCommentDTO> getAllComment(Long postId) {
        MockRepost repost = getMockRepost(postId);
        if (repost == null) return null;

        return repost.getComments().stream()
                .filter(comment -> comment.getDeletedAt()==null)
                .map(MockCommentDTO::new)
                .collect(Collectors.toList());

    }

    @Transactional
    public String deleteComment(Long postId, Long commentId) {
        Optional<MockRepost> repostOptional=repostRepository.findById(postId);
        if(repostOptional.isEmpty()) return "댓글 삭제 실패";

        MockRepost repost=repostOptional.get();

        if(repost.getDeletedAt()!=null) return "댓글 삭제 실패";

        MockComment target = getMockComment(commentId, repost);
        if (target == null) return "댓글 삭제 실패";

        target.setDeletedAt(LocalDateTime.now());

        return "댓글 삭제 성공";
    }

    @Transactional
    public MockCommentDTO modifyComment(Long postId, Long commentId, String content) {
        MockRepost repost = getMockRepost(postId);
        if (repost == null) return null;

        MockComment target = getMockComment(commentId, repost);
        if (target == null) return null;

        target.setContent(content);
        target.setModifyDate(LocalDateTime.now());

        return new MockCommentDTO(target);
    }

    private MockComment getMockComment(Long commentId, MockRepost repost) {
        Optional<MockComment> commentOptional= repost.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId) && comment.getDeletedAt()==null)
                .findFirst();
        return commentOptional.orElse(null);
    }

    @Transactional
    public MockCommentDTO addComment(Long postId, MockCommentWriteRequest request) {
        MockRepost repost = getMockRepost(postId);
        if (repost == null) return null;

        MockUser user=userRepository.findById(request.getWriterId()).get();

        List<MockUser> mentionedUsers=userRepository.findMockUsersByNicknameIn(request.getMentionedNames());

        MockComment comment=repost.addComment(user,mentionedUsers,request.getContent());

        return new MockCommentDTO(comment);
    }

    private MockRepost getMockRepost(Long postId) {
        Optional<MockRepost> repostOptional=repostRepository.findById(postId);
        if(repostOptional.isEmpty()) return null;

        MockRepost repost=repostOptional.get();
        if(repost.getDeletedAt()!=null) return null;

        return repost;
    }

    public List<MockLikeDTO> getAllLike(Long repostId) {
        MockRepost repost = getMockRepost(repostId);
        if(repost ==null) return null;

        return repost.getLikes().stream()
                .filter(like -> !like.getDeleted())
                .map(MockLikeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public String deleteLike(Long repostId, Long userId) {
        MockRepost repost=getMockRepost(repostId);
        if(repost==null) return "좋아요 취소 실패";

        List<MockLike> likes=repost.getLikes();

        Optional<MockLike> likeOptional=
        likes.stream()
                .filter(like -> !like.getDeleted() && like.getUser().getId().equals(userId))
                .findFirst();

        if(likeOptional.isEmpty()) return "좋아요 취소 실패";

        MockLike like=likeOptional.get();
        like.setDeleted(true);

        return "좋아요 취소 성공";

    }

    @Transactional
    public MockLikeDTO markLike(Long repostId, Long userId) {
        MockRepost repost=getMockRepost(repostId);
        if(repost==null) return null;

        MockUser user=userRepository.findById(userId).get();
        return new MockLikeDTO(repost.addLike(user));

    }
}
