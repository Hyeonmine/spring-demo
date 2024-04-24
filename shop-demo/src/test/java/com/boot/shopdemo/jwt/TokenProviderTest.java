package com.boot.shopdemo.jwt;

import com.boot.shopdemo.dto.MemberFormDto;
import com.boot.shopdemo.entity.Member;
import com.boot.shopdemo.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Date;

import static com.boot.shopdemo.jwt.JwtFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //1. generateToken() 검증 테스트

    @DisplayName("generateToken: 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setName("Test User");
        memberFormDto.setEmail("user@email.com");
        memberFormDto.setPassword("test");
        memberFormDto.setAddress("Test Address");

        Member testMember = Member.createMember(memberFormDto, passwordEncoder);

        String token = tokenProvider.generateToken(testMember, Duration.ofDays(14));

        Long memberId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("member_id", Long.class);

        assertThat(memberId).isEqualTo(testMember.getId());
    }

    //2. validToken()검증 테스트
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다")
    @Test
    void validToken_invalidToken(){
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);
        //then
        assertThat(result).isFalse();
    }
}
