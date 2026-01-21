package com.ab.cmsBackend.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String email;
    private String phone;
    private String password;
}