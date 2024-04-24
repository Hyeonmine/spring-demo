package com.boot.shopdemo.service;

import com.boot.shopdemo.entity.Member;
import com.boot.shopdemo.entity.RefreshToken;
import com.boot.shopdemo.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("잘못된 토큰입니다."));
    }

    public RefreshToken findByUser(Member member){
        return refreshTokenRepository.findByMember(member)
                .orElse(null);
    }

    public void saveRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }

    public void removeToken(String refreshToken){
        RefreshToken findToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(EntityNotFoundException::new);
        refreshTokenRepository.delete(findToken);
    }

}
