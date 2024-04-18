package com.boot.demo.Service;

import com.boot.demo.Repository.RefreshTokenRepository;
import com.boot.demo.Repository.UserRepository;
import com.boot.demo.dto.LoginDto;
import com.boot.demo.dto.TokenResponse;
import com.boot.demo.dto.UserFormDto;
import com.boot.demo.entity.RefreshToken;
import com.boot.demo.entity.User;
import com.boot.demo.jwt.TokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public boolean validate(UserFormDto userFormDto){

        User existUser = userRepository.findById(userFormDto.getId()).orElse(null);

        if(existUser != null){
            return false;
        }
        return true;

    }

    public void signup(UserFormDto user){

        if(!validate(user)) throw new RuntimeException("이미 존재하는 유저 입니다.");

        User saveUser = User.createUser(user, passwordEncoder);
        userRepository.save(saveUser);
    }

    public TokenResponse login(LoginDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 1.해당 유저 조회
        User user = userRepository.findById(authentication.getName())
                .orElseThrow(EntityNotFoundException::new);

        String newRefreshToken = tokenProvider.createRefreshToken(Duration.ofDays(1));
        // 2.해당 유저와 매핑된 리프레쉬 토큰 조회
        RefreshToken existRefreshToken = refreshTokenService.findByUser(user);
        // 2-2.기존에 저장된 리프레쉬 토큰이 없다면 생성하고 저장
        if(existRefreshToken == null){
            refreshTokenService.saveRefreshToken(new RefreshToken(user,newRefreshToken));
        }
        //2-1.리프레쉬 토큰이 있다면 기존 토큰 파쇄 후 재발급
        else{
            existRefreshToken.update(newRefreshToken);
        }
        // 3. Access토큰을 발급하고 TokenResponse(엑세스 + 리프레쉬) 반환
        String accessToken = tokenProvider.createAccessToken(user, Duration.ofHours(2));
        return new TokenResponse(accessToken, newRefreshToken);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
    }


}
