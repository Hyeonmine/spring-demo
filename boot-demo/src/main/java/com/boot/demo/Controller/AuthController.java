package com.boot.demo.Controller;

import com.boot.demo.Service.UserService;
import com.boot.demo.dto.LoginDto;
import com.boot.demo.dto.TokenRequest;
import com.boot.demo.dto.UserFormDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid UserFormDto userFormDto){
        try{
            userService.signup(userFormDto);
            return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid LoginDto loginDto){

        try {
            return new ResponseEntity<>(userService.login(loginDto), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid TokenRequest request){
        try{
            userService.logout(request);

        }catch (Exception e){
            log.info(e.getMessage());
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh") //refresh api
    public ResponseEntity<?> refresh(@Valid TokenRequest tokenRequest){
        try{
            return new ResponseEntity<>(userService.tokenRefresh(tokenRequest), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
