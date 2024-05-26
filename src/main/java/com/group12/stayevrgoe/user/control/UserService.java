package com.group12.stayevrgoe.user.control;

import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.user.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserDAO userDAO;
    private final BookingHistoryDAO bookingHistoryDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.getByEmail(username);

        return new MyUserDetails(username, user.getPassword(), new ArrayList<>(), user);
    }

    public User getUserByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    public User getUserById(String id) {
        return userDAO.getById(id);
    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        User user = userDAO.getById(id);
        return new MyUserDetails(id, user.getPassword(), new ArrayList<>(), user);
    }

    public List<RichBookingHistoryDTO> getBookingHistories(BookingHistoryFilter filter, Pageable pageable) {
        filter.setUserId(AuthenticationUtils.getCurrentUser().getId());
        return bookingHistoryDAO.getRichBookingHistories(filter, pageable);
    }

    public void editUserInfo(EditUserInfoDTO dto) {
        User user = userDAO.getById(AuthenticationUtils.getCurrentUser().getId());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        userDAO.save(user);
    }
}
