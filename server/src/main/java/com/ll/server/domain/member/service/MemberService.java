package com.ll.server.domain.member.service;

import com.ll.server.domain.comment.dto.CommentDTO;
import com.ll.server.domain.comment.dto.CommentResponse;
import com.ll.server.domain.comment.repository.CommentRepository;
import com.ll.server.domain.follow.dto.FolloweeListResponse;
import com.ll.server.domain.follow.dto.FollowerListResponse;
import com.ll.server.domain.follow.repository.FollowRepository;
import com.ll.server.domain.like.repository.LikeRepository;
import com.ll.server.domain.member.dto.MemberDto;
import com.ll.server.domain.member.dto.MemberProfile;
import com.ll.server.domain.member.dto.MemberRequest;
import com.ll.server.domain.member.dto.MemberResponse;
import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import com.ll.server.domain.member.repository.MemberRepository;
import com.ll.server.domain.news.news.dto.NewsOnly;
import com.ll.server.domain.news.news.dto.NewsOnlyResponse;
import com.ll.server.domain.news.news.repository.NewsRepository;
import com.ll.server.domain.repost.dto.RepostOnly;
import com.ll.server.domain.repost.dto.RepostOnlyResponse;
import com.ll.server.domain.repost.repository.RepostRepository;
import com.ll.server.global.exception.CustomAuthorizationException;
import com.ll.server.global.exception.ErrorCode;
import com.ll.server.global.jwt.JwtProvider;
import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomRequestException;
import com.ll.server.global.rsData.RsData;
import com.ll.server.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final RepostRepository repostRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final NewsRepository newsRepository;

    public MemberResponse getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthorizationException(ErrorCode.USER_NOT_FOUND));

        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .role(member.getRole().name())
                .build();

    }


    @Transactional
    public Member join(MemberRequest request){
        return this.join(request.getEmail(),request.getPassword(),request.getRole(), request.getNickname(),request.getProvider(),request.getProviderId());
    }

    @Transactional
    public Member join(String email,
                       String password,
                       MemberRole role,
                       //String name,
                       String nickname,
                       Provider provider,
                       String providerId) {

        // 존재하는 지 체크
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new CustomRequestException(ReturnCode.ALREADY_EXIST);
                });

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .build();

        String refreshToken = jwtProvider.genRefreshToken(member);
        member.setRefreshToken(refreshToken);

        return memberRepository.save(member);

    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new CustomRequestException(ReturnCode.NOT_FOUND_ENTITY));
    }

    public List<Member> getMembersByNickName(List<String> nickName) {
        return memberRepository.findMembersByNicknameIn(nickName);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        return jwtProvider.verify(token);
    }

    // 토큰갱신
    public RsData<String> refreshAccessToken(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
        String accessToken = jwtProvider.genAccessToken(member);
        return new RsData<>("200", "토큰 갱신에 성공하였습니다.", accessToken);
    }
    // 토큰으로 User 정보 가져오기
    public SecurityUser getUserFromAccessToken(String accessToken) {
        Map<String, Object> payloadBody = jwtProvider.getClaims(accessToken);
        long id = (int) payloadBody.get("id");
        String nickname = (String) payloadBody.get("username");
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new SecurityUser(id, nickname, "", authorities);
    }

    public MemberProfile getMemberProfile(Long memberId) {
        Optional<Member> optional=memberRepository.findById(memberId);
        if(optional.isEmpty()) return null;

        Member member=optional.get();
        MemberDto memberDto=new MemberDto(member);

        RepostOnlyResponse repostWritten=new RepostOnlyResponse(
                repostRepository.findRepostsByMember_IdAndDeletedAtIsNull(memberId)
                        .stream()
                        .map(RepostOnly::new)
                        .collect(Collectors.toList())
        );

        RepostOnlyResponse repostLiked=new RepostOnlyResponse(
                likeRepository.findLikesByMember_IdAndDeletedIsFalse(memberId)
                        .stream()
                        .map(like -> new RepostOnly(like.getRepost()))
                        .collect(Collectors.toList())
        );


        CommentResponse commentWritten=new CommentResponse(
            commentRepository.findCommentsByMember_IdAndDeletedAtIsNull(memberId)
                    .stream()
                    .map(CommentDTO::new)
                    .collect(Collectors.toList())
        );

        FollowerListResponse followerList=new FollowerListResponse(
                followRepository.findFollowsByFollowee_Id(memberId)
        );

        FolloweeListResponse followeeList=new FolloweeListResponse(
                followRepository.findFollowsByFollower_Id(memberId)
        );

        if(member.getRole().equals(MemberRole.PUBLISHER)){
            NewsOnlyResponse newsWritten= new NewsOnlyResponse(
                            newsRepository.findNewsByPublisherAndDeletedAtIsNull(member.getNickname())
                                    .stream()
                                    .map(NewsOnly::new)
                                    .collect(Collectors.toList())
                    );

            return new MemberProfile(memberDto,repostWritten,repostLiked,commentWritten,followerList,followeeList,newsWritten);
        }

        return new MemberProfile(memberDto,repostWritten,repostLiked,commentWritten,followerList,followeeList);
    }
}
