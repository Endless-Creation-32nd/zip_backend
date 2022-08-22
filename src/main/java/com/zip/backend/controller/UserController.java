//package com.zip.backend.controller;
//
//import com.zip.backend.domain.user.User;
//import com.zip.backend.domain.user.UserRepository;
//import com.zip.backend.exception.ResourceNotFoundException;
//import com.zip.backend.security.CurrentUser;
//import com.zip.backend.security.UserPrincipal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//// 무엇을 위한 class 인지 모르겠음
//@RequiredArgsConstructor
//@RestController
//public class UserController {
//    private final UserRepository userRepository;
//
//    @GetMapping("/user/me")
//    @PreAuthorize("hasRole('USER')")
//    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
//        return userRepository.findById(userPrincipal.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("User","id",userPrincipal.getId()));
//    }
//}
