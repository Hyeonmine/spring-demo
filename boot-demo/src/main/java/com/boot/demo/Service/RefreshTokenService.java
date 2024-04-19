package com.boot.demo.Service;


import com.boot.demo.Repository.RefreshTokenRepository;
import com.boot.demo.entity.RefreshToken;
import com.boot.demo.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new IllegalArgumentException("잘못된 토큰입니다."));
    }

    public RefreshToken findByUser(User user){
        return refreshTokenRepository.findByUser(user)
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
