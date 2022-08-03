package com.zip.backend.controller.dto;

import lombok.Getter;
import lombok.Setter;

// /login 시 http body 에 있는 내용을 객체로 받아오기 위함
@Getter
@Setter
public class LoginRequest {

    private String email;
    private String password;

}
