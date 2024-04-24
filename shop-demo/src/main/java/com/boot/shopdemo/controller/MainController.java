package com.boot.shopdemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String getMain(Authentication authentication){
        System.out.println(authentication.getName());
        System.out.println(authentication.getAuthorities());
        return "Hello, World";
    }
}
