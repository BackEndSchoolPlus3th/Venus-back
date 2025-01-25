package com.ll.server.domain.mock.user.repository;

import com.ll.server.domain.mock.user.entity.MockUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockUserRepository extends JpaRepository<MockUser,Long> {
    MockUser findByEmailAndPassword(String email, String password);

    MockUser findByEmail(String email);

    MockUser findByNickname(String nickName);

    List<MockUser> findMockUsersByNicknameIn(List<String> nicknames);
}
