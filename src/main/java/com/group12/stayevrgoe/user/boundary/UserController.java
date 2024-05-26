package com.group12.stayevrgoe.user.boundary;

import com.group12.stayevrgoe.shared.network.ApiResponse;
import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.user.control.UserService;
import com.group12.stayevrgoe.user.entity.BookingHistoryFilter;
import com.group12.stayevrgoe.user.entity.EditUserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users/{id}")
    public ApiResponse getUserById(@PathVariable String id) {
        return new ApiResponse(HttpStatus.OK, "OK", userService.getUserById(id));
    }

    @PatchMapping("/users/me")
    public ApiResponse editUserInfo(@RequestBody EditUserInfoDTO dto) {
        userService.editUserInfo(dto);
        return new ApiResponse(HttpStatus.OK, "OK");
    }
}
