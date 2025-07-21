package com.example.auth_api.Controllers;

import com.example.auth_api.Models.Dtos.LoginResponse;
import com.example.auth_api.Models.Dtos.LoginUserDTO;
import com.example.auth_api.Models.Dtos.RegisterUserDTO;
import com.example.auth_api.Models.Dtos.VerifyUserDTO;
import com.example.auth_api.Services.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterUserDTO registerUserDTO) throws MessagingException {
        authService.register(registerUserDTO);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody LoginUserDTO loginUserDTO){
        return authService.login(loginUserDTO);
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public void verifyUser(@RequestBody VerifyUserDTO verifyUserDTO){
        authService.verifyUser(verifyUserDTO);
    }

    @PostMapping("/resend")
    @ResponseStatus(HttpStatus.OK)
    public void resendVerificationCode(@RequestParam String email) throws MessagingException {
        authService.reenviarCodigoVerificacion(email);
    }
}
