package com.group12.stayevrgoe.rating;

import com.group12.stayevrgoe.shared.utils.AuthenticationUtils;
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

    public List<RichRatingDTO> get(RatingFilter filter, Pageable pageable) {
        return ratingDAO.getRichRatings(filter, pageable);
    }

    public Rating addNewRating(NewRatingDTO dto) {
        return ratingDAO.save(Rating.builder()
                .hotelRoomId(dto.getHotelRoomId())
                .userId(AuthenticationUtils.getCurrentUser().getId())
                .value(dto.getValue())
                .comment(dto.getComment())
                .build());
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
