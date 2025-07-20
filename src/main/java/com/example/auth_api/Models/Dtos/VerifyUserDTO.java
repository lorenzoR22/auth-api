package com.example.auth_api.Models.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerifyUserDTO {
    private String email;
    private String verificationCode;
}
