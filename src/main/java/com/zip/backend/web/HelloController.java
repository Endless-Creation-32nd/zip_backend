package com.zip.backend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // @Controller 에 @ResponseBody 붙인 것
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/restricted")
    public String restricted() {
        return "to see this text you need to be logged in";
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello2(){
        return ResponseEntity.ok("hello");
    }
}
