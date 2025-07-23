package com.example.auth_api.Services;

import com.example.auth_api.Exceptions.Customs.*;
import com.example.auth_api.Models.Dtos.*;
import com.example.auth_api.Models.Entities.User;
import com.example.auth_api.Repositories.UserRepository;
import com.example.auth_api.Security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    //verifica si el usuario existe y verifico la cuenta,
    //luego crea el token y devuelve un objeto con datos del user y el token.

    public LoginResponse login(LoginUserDTO loginUserDTO){
        User user=authenticate(loginUserDTO);
        String jwtToken=jwtUtils.generateToken(user);
        return new LoginResponse(jwtToken,user.getUsername(),user.getEmail(),
                user.getRoles()
                .stream().map(role->role.getName().name())
                .toList());
    }

    private User authenticate(LoginUserDTO input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException(input.getEmail()));

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException(input.getEmail());
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

}
