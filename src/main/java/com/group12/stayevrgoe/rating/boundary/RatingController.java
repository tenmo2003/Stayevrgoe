package com.group12.stayevrgoe.rating.boundary;

import com.group12.stayevrgoe.rating.control.RatingService;
import com.group12.stayevrgoe.rating.entity.NewRatingDTO;
import com.group12.stayevrgoe.rating.entity.RatingFilter;
import com.group12.stayevrgoe.rating.entity.RespondDTO;
import com.group12.stayevrgoe.shared.network.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author anhvn
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    @GetMapping("/ratings")
    public ApiResponse getRatings(@RequestParam(required = false) String hotelRoomId,
                                  @RequestParam(required = false) String userId,
                                  @RequestParam(required = false, defaultValue = "-1") String value,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
                                  @RequestParam(name="page") int pageNumber,
                                  @RequestParam(name="size") int pageSize) {
        int valueInt = Integer.parseInt(value);
        RatingFilter filter = new RatingFilter(hotelRoomId, userId, valueInt, from, to);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return new ApiResponse(HttpStatus.OK, "Ratings retrieved", ratingService.get(filter, pageable));
    }

    @PostMapping("/rating")
    public ApiResponse createRating(@RequestBody NewRatingDTO dto) {
        return new ApiResponse(HttpStatus.OK, "Rating created", ratingService.addNewRating(dto));
    }

    @PostMapping("/response")
    public ApiResponse respond(@RequestBody RespondDTO dto) {
        return new ApiResponse(HttpStatus.OK, "Rating responded", ratingService.respond(dto));
    }
}
