package com.group12.stayevrgoe.user;

import com.group12.stayevrgoe.shared.network.ApiResponse;
import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/me/booking-histories")
    public ApiResponse getBookingHistories(@RequestParam(required = false) String hotelRoomId,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
                                           @RequestParam(name = "page") int pageNumber,
                                           @RequestParam(name = "size") int pageSize) {
        BookingHistoryFilter filter = new BookingHistoryFilter();
        filter.setHotelRoomId(hotelRoomId);
        filter.setFrom(from);
        filter.setTo(to);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return new ApiResponse(HttpStatus.OK, "OK", userService.getBookingHistories(filter, pageable));
    }

    @GetMapping("/users/me")
    public ApiResponse getPersonalInformation() {
        return new ApiResponse(HttpStatus.OK, "OK", AuthenticationUtils.getCurrentUser());
    }
}
