package com.example.auth_api.Services;

import com.example.auth_api.Exceptions.Customs.*;
import com.example.auth_api.Models.Dtos.LoginResponse;
import com.example.auth_api.Models.Dtos.LoginUserDTO;
import com.example.auth_api.Models.Dtos.RegisterUserDTO;
import com.example.auth_api.Models.Dtos.VerifyUserDTO;
import com.example.auth_api.Models.Entities.ERole;
import com.example.auth_api.Models.Entities.Role;
import com.example.auth_api.Models.Entities.User;
import com.example.auth_api.Repositories.RoleRepository;
import com.example.auth_api.Repositories.UserRepository;
import com.example.auth_api.Security.JwtUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;

    public void register(RegisterUserDTO registerUserDTO) throws MessagingException {
        if(userRepository.existsByUsername(registerUserDTO.getUsername())){
            throw new UserAlreadyExistsException(registerUserDTO.getUsername());
        }
        if(userRepository.existsByEmail(registerUserDTO.getEmail())){
            throw new UserAlreadyExistsException(registerUserDTO.getEmail());
        }
        User user=User.builder()
                .username(registerUserDTO.getUsername())
                .email(registerUserDTO.getEmail())
                .password(passwordEncoder.encode(registerUserDTO.getPassword()))
                .verificationCode(generarVerificacionCode())
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                .enabled(false)//para activacion por mail.
                .build();

        Role userRole=roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(()->new RoleNotFoundException(ERole.ROLE_USER.toString()));

        user.setRoles(Set.of(userRole));

        enviarVerificacionEmail(user);

        userRepository.save(user);
    }

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

    //verifica si el codigo es el correcto segun el usuario
    public void verifyUser(VerifyUserDTO input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new VerificationCodeExpiredException();
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new InvalidVerificationCodeException();
            }
        } else {
            throw new UserNotFoundException(input.getEmail());
        }
    }

    public void reenviarCodigoVerificacion(String email) throws MessagingException {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.isEnabled()) {
                throw new AccountAlreadyVerifiedException(email);
            }
            user.setVerificationCode(generarVerificacionCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            enviarVerificacionEmail(user);

            userRepository.save(user);
        } else {
            throw new UserNotFoundException(email);
        }
    }

    private void enviarVerificacionEmail(User user) throws MessagingException {
        String subject = "Verificacion de cuenta";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Bienvenido a tu app!</h2>"
                + "<p style=\"font-size: 16px;\">Ingrese el código de verificación a continuación para continuar:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Codigo de verificacion:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + user.getVerificationCode() + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        emailService.enviarNotificacionMail(user.getEmail(), subject, htmlMessage);
    }
    private String generarVerificacionCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
