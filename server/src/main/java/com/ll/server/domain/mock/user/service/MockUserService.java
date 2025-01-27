package com.ll.server.domain.mock.user.service;

import com.ll.server.domain.mock.user.dto.MockUserLoginRequest;
import com.ll.server.domain.mock.user.dto.MockUserSignupRequest;
import com.ll.server.domain.mock.user.entity.MockUser;
import com.ll.server.domain.mock.user.repository.MockUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MockUserService {
    private final MockUserRepository userRepository;

    @Transactional
    public MockUser signup(MockUserSignupRequest request){
        MockUser user=
                MockUser.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .nickname(request.getNickname())
                        .role(request.getRole())
                        .build();
        MockUser find=userRepository.findByEmail(request.getEmail());

        if(find==null) return userRepository.save(user);

        return null;
    }

    public MockUser login(MockUserLoginRequest request){
        return userRepository.findByEmailAndPassword(request.getEmail(),request.getPassword());
    }

    public MockUser findByNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }

    public List<MockUser> findUsersByNickNameIn(List<String> nicknames){
        return userRepository.findMockUsersByNicknameIn(nicknames);
    }
}
