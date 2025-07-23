package com.example.auth_api.Services;

import com.example.auth_api.Exceptions.Customs.IncorrectPasswordException;
import com.example.auth_api.Exceptions.Customs.InvalidVerificationCodeException;
import com.example.auth_api.Exceptions.Customs.UserNotFoundException;
import com.example.auth_api.Exceptions.Customs.VerificationCodeExpiredException;
import com.example.auth_api.Models.Dtos.ChangePasswordDTO;
import com.example.auth_api.Models.Dtos.ResetPasswordDTO;
import com.example.auth_api.Models.Entities.User;
import com.example.auth_api.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${frontend.reset-password.url}")
    private String resetPasswordBaseUrl;

    //cambio de password (cuando el user esta logueado)
    public void cambiarPassword(String email , ChangePasswordDTO changePasswordDTO){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException(email));

        if(!passwordEncoder.matches(changePasswordDTO.getPasswordActual(), user.getPassword())){
            throw new IncorrectPasswordException();
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNuevaPassword()));
        userRepository.save(user);
    }

    //guarda el token y envia un mail con una url y el token
    public void solicitarResetPassword(String email) throws MessagingException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            String token= UUID.randomUUID().toString();

            user.setVerificationCode(token);
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));

            userRepository.save(user);

            //se envia un mail con link que redirecciona a la pantalla de cambiar password.
            String resetLink = resetPasswordBaseUrl + "?token=" + token;
            emailService.enviarNotificacionMail(user.getEmail(),"resetLink", resetLink);
        }
    }

    //verifica el token y cambia el password
    public void resetPassword(ResetPasswordDTO dto){
        User user=userRepository.findByVerificationCode(dto.getToken())
                .orElseThrow(InvalidVerificationCodeException::new);

        if(user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
            throw new VerificationCodeExpiredException();
        }

        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);

        user.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));

        userRepository.save(user);
    }
}
