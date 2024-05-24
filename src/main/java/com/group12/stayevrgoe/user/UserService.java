package com.group12.stayevrgoe.user;

import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
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
        return userDAO.getByUniqueAttribute(id);
    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        User user = userDAO.getByUniqueAttribute(id);
        return new MyUserDetails(id, user.getPassword(), new ArrayList<>(), user);
    }

    public List<RichBookingHistoryDTO> getBookingHistories(BookingHistoryFilter filter, Pageable pageable) {
        filter.setUserId(AuthenticationUtils.getCurrentUser().getId());
        return bookingHistoryDAO.getRichBookingHistories(filter, pageable);
    }
}
