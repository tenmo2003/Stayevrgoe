package com.group12.stayevrgoe.user;

import com.group12.stayevrgoe.user.domain.MyUserDetails;
import com.group12.stayevrgoe.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(username);

        return new MyUserDetails(username, user.getPassword(), new ArrayList<>(), user);
    }

    public User findUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
}
