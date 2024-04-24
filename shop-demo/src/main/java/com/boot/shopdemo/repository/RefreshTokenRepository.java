package com.boot.shopdemo.repository;

import com.boot.shopdemo.entity.Member;
import com.boot.shopdemo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember(Member member);
    Optional<RefreshToken> findByRefreshToken(String RefreshToken);
}
