package com.boot.shopdemo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "refresh_Token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RefreshToken_id", updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn (name = "user_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "Refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(Member member, String refreshToken){
        this.member = member;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken){
        this.refreshToken = newRefreshToken;
        return this;
    }

}
