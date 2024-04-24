package com.boot.shopdemo.service;

import com.boot.shopdemo.dto.LoginDto;
import com.boot.shopdemo.dto.MemberFormDto;
import com.boot.shopdemo.dto.TokenRequest;
import com.boot.shopdemo.dto.TokenResponse;
import com.boot.shopdemo.entity.Member;
import com.boot.shopdemo.entity.RefreshToken;
import com.boot.shopdemo.jwt.TokenProvider;
import com.boot.shopdemo.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional // 중간에 코드하나 잘못되면 다 롤백하기 위해
@RequiredArgsConstructor //의존성 주입
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public boolean validate(MemberFormDto memberFormDto){
        Member existMember = memberRepository.findById(memberFormDto.getEmail()).orElse(null);

        if(existMember != null) {
            return false;
        }else {
            return true;
        }
    }

    public void signup(MemberFormDto member){

        if(!validate(member)) throw new RuntimeException("이미 존재하는 유저입니다.");

        Member saveMember = Member.createMember(member, passwordEncoder);
        memberRepository.save(saveMember);
    }

    public TokenResponse login(LoginDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //1.해당 유저 조회
        Member member = memberRepository.findById(authentication.getName())
                .orElseThrow(EntityNotFoundException::new);

        String newRefreshToken = tokenProvider.createRefreshToken(Duration.ofDays(1));
        RefreshToken existRefreshToken = refreshTokenService.findByUser(member);

        if(existRefreshToken == null){
            refreshTokenService.saveRefreshToken(new RefreshToken(member, newRefreshToken));
        }
        else{
            existRefreshToken.update(newRefreshToken);
        }
        String accessToken = tokenProvider.createAccessToken(member, Duration.ofHours(2));
        return new TokenResponse(accessToken, newRefreshToken, member.getRole().getKey());
    }

    public void logout(TokenRequest request){
        refreshTokenService.removeToken(request.getRefreshToken());
    }

    public TokenResponse tokenRefresh(TokenRequest tokenRequest) {
        if(!tokenProvider.validToken(tokenRequest.getRefreshToken())){
            throw new IllegalArgumentException("Unexpected token");
        }

        RefreshToken refreshToken = refreshTokenService.findByRefreshToken(tokenRequest.getRefreshToken());

        Member member = refreshToken.getMember();
        String accessToken = tokenProvider.createAccessToken(member, Duration.ofHours(2));
        String newRefreshToken = refreshToken.update(tokenProvider.createRefreshToken(Duration.ofDays(1))).getRefreshToken();

        return new TokenResponse(accessToken, newRefreshToken, member.getRole().getKey());
    }

    private UserDetails findById(String username){
        return memberRepository.findById(username)
                .orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email);

        if(member!=null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
