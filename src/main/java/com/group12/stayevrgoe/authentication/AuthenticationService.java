package com.group12.stayevrgoe.authentication;

import com.group12.stayevrgoe.authentication.domain.CredentialsDTO;
import com.group12.stayevrgoe.authentication.domain.SignUpDTO;
import com.group12.stayevrgoe.shared.configs.JwtService;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.user.UserDAO;
import com.group12.stayevrgoe.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public String authenticate(CredentialsDTO credentialsDTO) {
        User user = userDAO.findByEmail(credentialsDTO.getEmail());

        if (!bCryptPasswordEncoder.matches(credentialsDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public void signup(SignUpDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.USER);
        userDAO.save(user);
    }
}
