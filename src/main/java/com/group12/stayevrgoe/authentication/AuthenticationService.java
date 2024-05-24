package com.group12.stayevrgoe.authentication;

import com.group12.stayevrgoe.shared.configs.JwtService;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.user.MyUserDetails;
import com.group12.stayevrgoe.user.UserDAO;
import com.group12.stayevrgoe.user.User;
import com.group12.stayevrgoe.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String authenticate(CredentialsDTO credentialsDTO) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    credentialsDTO.getEmail(),
                    credentialsDTO.getPassword()
            );
            Authentication authentication = authenticationManager.authenticate(token);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            User user = myUserDetails.getUser();

            return jwtService.generateToken(user.getId());
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    public void signup(SignUpDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        userDAO.save(user);
    }
}
