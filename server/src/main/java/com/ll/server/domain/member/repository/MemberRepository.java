package com.ll.server.domain.member.repository;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByEmailAndProvider(String email, Provider provider);

    boolean existsByEmail(String email);

    List<Member> findAllByNicknameIn(List<String> mentionedNames);

    Optional<Member> findMemberByEmail(String email);

    Optional<Member> findMemberByProviderId(String providerId);
}
