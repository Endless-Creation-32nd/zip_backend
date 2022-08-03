package com.zip.backend.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // Json 객체 받아오기 위해 설정, 생성자 삭제함
public class SignUpRequest {

    private String name;
    private String email;
    private String password;

}
