package com.ll.server.domain.member.repository;

import com.ll.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByEmail(String email);
    boolean existsByEmail(String email);

    List<Member> findMembersByNicknameIn(List<String> mentionedNames);
}
