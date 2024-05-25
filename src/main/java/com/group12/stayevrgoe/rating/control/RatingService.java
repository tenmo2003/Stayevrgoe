package com.group12.stayevrgoe.rating.control;

import com.group12.stayevrgoe.hotel.control.HotelDAO;
import com.group12.stayevrgoe.hotel.entity.Hotel;
import com.group12.stayevrgoe.hotel.entity.HotelRoom;
import com.group12.stayevrgoe.hotel.entity.HotelRoomDAO;
import com.group12.stayevrgoe.rating.entity.*;
import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
import com.group12.stayevrgoe.shared.utils.ThreadPoolUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author anhvn
 */
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingDAO ratingDAO;
    private final HotelDAO hotelDAO;
    private final HotelRoomDAO hotelRoomDAO;

    public List<RichRatingDTO> get(RatingFilter filter, Pageable pageable) {
        return ratingDAO.getRichRatings(filter, pageable);
    }

    public Rating addNewRating(NewRatingDTO dto) {
        Rating rating = ratingDAO.save(Rating.builder()
                .hotelRoomId(dto.getHotelRoomId())
                .userId(AuthenticationUtils.getCurrentUser().getId())
                .value(dto.getValue())
                .comment(dto.getComment())
                .build());

        ThreadPoolUtils.executeTask(() -> {
            HotelRoom hotelRoom = hotelRoomDAO.getByUniqueAttribute(dto.getHotelRoomId());
            hotelRoom.setRatingCount(hotelRoom.getRatingCount() + 1);
            hotelRoom.setTotalRating(hotelRoom.getTotalRating() + dto.getValue());

            hotelRoomDAO.save(hotelRoom);

            Hotel hotel = hotelDAO.getByUniqueAttribute(hotelRoom.getHotelId());
            hotel.setRatingCount(hotel.getRatingCount() + 1);
            hotel.setTotalRating(hotel.getTotalRating() + dto.getValue());

            hotelDAO.save(hotel);
        });

        return rating;
    }

    public Rating respond(RespondDTO dto) {
        Rating rating = ratingDAO.getByUniqueAttribute(dto.getRatingId());

        RatingResponse response = new RatingResponse();
        response.setContent(dto.getContent());
        response.setCreatedDate(new Date());
        rating.setHotelResponse(response);

        return ratingDAO.save(rating);
    }
}
