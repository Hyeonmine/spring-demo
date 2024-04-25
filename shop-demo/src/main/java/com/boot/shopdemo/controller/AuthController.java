package com.boot.shopdemo.controller;

import com.boot.shopdemo.dto.LoginDto;
import com.boot.shopdemo.dto.MemberFormDto;
import com.boot.shopdemo.dto.TokenRequest;
import com.boot.shopdemo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid MemberFormDto memberFormDto){

        try{
            memberService.signup(memberFormDto);
            return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid LoginDto loginDto){
        try{
            return new ResponseEntity<>(memberService.login(loginDto) , HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid TokenRequest request){
        try{
            memberService.logout(request);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid TokenRequest tokenRequest){
        try{
            return new ResponseEntity<>(memberService.tokenRefresh(tokenRequest), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
