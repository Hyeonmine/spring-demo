package com.boot.shopdemo.jwt;

import com.boot.shopdemo.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j //로그에 찍기
public class TokenProvider {

    private static final String Authentication_Key = "/auth";
    private final JwtProperties jwtProperties;

    public String createAccessToken(Member member, Duration expiredAt){
        Date now = new Date();

        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
    }

    //1.JWT 토큰 메서드
    private String makeToken(Date expiry, Member member){

        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //헤더 타입 : JWT
                // 내용 iss : propertise 파일에서 설정한 값
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now) //내용 iat : 현재 시간
                .setExpiration(expiry) //exp : expiry 멤버 변숫값
                .setSubject(member.getEmail()) //sub : 우저의 이메일
                .claim("id", member.getId()) //클레임 id : 유저 ID
                //서명: 비밀값과 함께 해시값을 HS256방식으로 암호화
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public String createRefreshToken(Duration expriredAt){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expriredAt.toMillis());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    //2.JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token){

        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) //비밀값으로 복호화
                    .parseClaimsJws(token);
                    return true;
        }catch (Exception e){ //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            log.info("유효하지 않은 JWT 토큰 입니다.");
            return false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parser() //클레임조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    //3.토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);

        if(claims.get(Authentication_Key) == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        String memberId = claims.getSubject();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(Authentication_Key).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        memberId, "",authorities
                ),
                token,
                authorities
        );
    }
}
