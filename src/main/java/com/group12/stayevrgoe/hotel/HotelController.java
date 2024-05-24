package com.group12.stayevrgoe.hotel;

import com.group12.stayevrgoe.shared.network.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @GetMapping("/hotels")
    public ApiResponse getHotels(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String location,
                                 @RequestParam float minPrice,
                                 @RequestParam float maxPrice,
                                 @RequestParam(required = false) Set<HotelFacility> facilities,
                                 @RequestParam(name = "page") int pageNumber,
                                 @RequestParam(name = "limit") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HotelFilter filter = new HotelFilter(name, location, minPrice, maxPrice, (EnumSet<HotelFacility>) facilities);

        return new ApiResponse(HttpStatus.OK, "OK", hotelService.getHotels(filter, pageable));
    }

    @PostMapping("/hotel/book")
    public ApiResponse bookHotelRoom(@RequestBody HotelRoomBookDTO dto) {
        return new ApiResponse(HttpStatus.OK, "OK", hotelService.bookHotelRoom(dto));
    }

    @DeleteMapping("/hotel/book")
    public ApiResponse cancelBooking(@RequestParam String bookingId) {
        hotelService.cancelBooking(bookingId);
        return new ApiResponse(HttpStatus.OK, "Cancelled successfully");
    }

    @GetMapping("/hotel/rooms")
    public ApiResponse getHotelRooms(@RequestParam String hotelId,
                                     @RequestParam float minPrice,
                                     @RequestParam float maxPrice,
                                     @RequestParam(required = false) Set<HotelRoomFacility> facilities,
                                     @RequestParam(name = "page") int pageNumber,
                                     @RequestParam(name = "limit") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HotelRoomFilter filter = new HotelRoomFilter(hotelId, (EnumSet<HotelRoomFacility>) facilities, minPrice, maxPrice);

        return new ApiResponse(HttpStatus.OK, "OK", hotelService.getHotelRooms(filter, pageable));
    }

    @PostMapping("/hotel/register")
    public ApiResponse registerNewHotel(@ModelAttribute HotelRegisterDTO dto) {
        hotelService.registerNewHotel(dto);
        return new ApiResponse(HttpStatus.OK, "Registered successfully");
    }

    @PatchMapping("/hotel/approve")
    public ApiResponse approveHotel(@RequestParam String id) {
        hotelService.approveHotel(id);
        return new ApiResponse(HttpStatus.OK, "Approved successfully");
    }

    @DeleteMapping("/hotel")
    public ApiResponse deleteHotel(@RequestParam String id) {
        hotelService.deleteHotel(id);
        return new ApiResponse(HttpStatus.OK, "Deleted successfully");
    }

    @PostMapping("/hotel/room")
    public ApiResponse editHotelRoomInfo(@RequestBody HotelRoomAddDTO dto) {
        return new ApiResponse(HttpStatus.OK, "Updated successfully", hotelService.addNewHotelRoom(dto));
    }

    @PatchMapping("/hotel/room")
    public ApiResponse updateHotelPriceRange(@RequestBody HotelRoomEditDTO dto) {
        hotelService.editHotelRoomInfo(dto);
        return new ApiResponse(HttpStatus.OK, "Updated successfully");
    }

    @DeleteMapping("/hotel/room")
    public ApiResponse deleteHotelRoom(@RequestParam String roomId) {
        hotelService.deleteHotelRoom(roomId);
        return new ApiResponse(HttpStatus.OK, "Deleted successfully");
    }

}
