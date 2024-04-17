package com.boot.demo.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

//    @CrossOrigin("http://localhost:3000")
    @GetMapping("/main")
    public String getMain(){
        return "Hello, World!";
    }
}
