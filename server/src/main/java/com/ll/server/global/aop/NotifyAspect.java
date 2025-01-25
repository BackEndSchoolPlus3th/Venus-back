package com.ll.server.global.aop;

import com.ll.server.domain.mention.commentmention.dto.CommentMentionDTO;
import com.ll.server.domain.mention.repostmention.entity.RepostMention;
import com.ll.server.domain.mock.comment.dto.MockCommentDTO;
import com.ll.server.domain.mock.follow.dto.MockFollowDTO;
import com.ll.server.domain.mock.follow.entity.MockFollow;
import com.ll.server.domain.mock.follow.repository.MockFollowRepository;
import com.ll.server.domain.mock.like.dto.MockLikeDTO;
import com.ll.server.domain.mock.repost.dto.MockRepostDTO;
import com.ll.server.domain.mock.repost.entity.MockRepost;
import com.ll.server.domain.mock.repost.repository.MockRepostRepository;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
import com.ll.server.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class NotifyAspect {
    private final NotificationService notificationService;
    private final MockFollowRepository mockFollowRepository;
    private final MockRepostRepository repostRepository;
    private final MockUserRepository userRepository;

    @Around("@annotation(com.ll.server.domain.notification.Notify)")//Notify 어노테이션이 붙은 메서드를 실행하면 이 놈이 대리로 실행 후 본인 할 일을 한다.
    public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj=joinPoint.proceed(); //가로챌 대상이 되는 걸 proceed를 통해 호출한다. 비즈니스 로직을 여기서 실행했다고 보면 된다.

        
        if(obj instanceof MockRepostDTO repostDTO){
            Long repostId=repostDTO.getRepostId();
            MockRepost repost=repostRepository.findById(repostId).get();

            Long followerId=repostDTO.getWriterId();
            String followerName=repostDTO.getNickname();

            List<MockFollow> followList= mockFollowRepository.findMockFollowsByFollower_Id(followerId);

            String url="http://localhost:8080//api/v1/repost/"+repostId;

            for(MockFollow follow : followList){
                MockUser followee=follow.getFollowee();
                notificationService.saveNotification(followee,followerName+"님이 새 리포스트를 올렸습니다.",url);
            }

            List<RepostMention> repostMentions=repost.getMentions();
            String repostUser=repostDTO.getNickname();

            for(RepostMention mention: repostMentions){
                MockUser mentionedUser=mention.getUser();
                if(mention.getUser().getId().equals(repostDTO.getWriterId())) continue;
                notificationService.saveNotification(mentionedUser,repostUser+"님이 당신을 멘션했습니다.",url);
            }

        }

        if(obj instanceof MockFollowDTO followDTO){
            MockFollow follow= mockFollowRepository.findById(followDTO.getId()).get();

            MockUser follower=follow.getFollower();
            String followerName=followDTO.getFollower();
            String followeeName=followDTO.getFollowee();
            String url="http://localhost:8080/api/v1/follows/followees?nickname="+followerName;
            notificationService.saveNotification(follower,followeeName+"님이 팔로우하였습니다.",url);

        }

        if(obj instanceof MockCommentDTO commentDTO){
            String url = "http://localhost:8080/api/v1/repost/" + commentDTO.getRepostId();

            Long repostWriterId=commentDTO.getRepostWriterId();
            Long commentWriterId=commentDTO.getCommentWriterId();

            MockUser repostWriter=userRepository.findById(repostWriterId).get();

            if(!repostWriterId.equals(commentWriterId)) {
                notificationService.saveNotification(repostWriter, "내 리포스트에 "+commentDTO.getRepostWriterName()+"님이 댓글을 달았습니다.", url);
            }

            List<CommentMentionDTO> mentionList= commentDTO.getMentions();

            for(CommentMentionDTO mentionDTO : mentionList){
                System.out.println("댓글 멘션 찍는 중:"+mentionDTO.getMentionName());
                Long mentionedUserId=mentionDTO.getMentionUserId();

                if(mentionedUserId.equals(commentWriterId)) {
                    continue;
                }
                MockUser mentionedUser=userRepository.findById(mentionedUserId).get();
                notificationService.saveNotification(mentionedUser,commentDTO.getCommentWriterName()+"님이 당신을 멘션했습니다.",url);
            }
        }

        if(obj instanceof MockLikeDTO mockLikeDTO){
            Long repostId=mockLikeDTO.getRepostId();
            Long repostWriterId=mockLikeDTO.getRepostWriterId();

            String checkedUserName=mockLikeDTO.getCheckedUserName();
            Long checkedUserId=mockLikeDTO.getCheckedUserId();

            if(!repostId.equals(checkedUserId)) {
                MockUser user = userRepository.findById(repostWriterId).get();
                String url = "http://localhost:8080/api/v1/reposts/" + repostId + "/likes";
                notificationService.saveNotification(user, checkedUserName + "님이 당신의 글에 좋아요를 눌렀습니다.", url);
            }
        }

        return obj;
    }
}
