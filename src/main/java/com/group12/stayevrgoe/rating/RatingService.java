package com.group12.stayevrgoe.rating;

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

    public List<Rating> get(RatingFilter filter, Pageable pageable) {
        return ratingDAO.get(filter, pageable);
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
