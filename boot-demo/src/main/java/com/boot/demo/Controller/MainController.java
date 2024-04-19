package com.boot.demo.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @CrossOrigin("http://localhost:3000")
    @GetMapping("/main")
    public String getMain(Authentication authentication){
        System.out.println(authentication.getName());
        System.out.println(authentication.getAuthorities());
        return "Hello, World!";
    }
}
