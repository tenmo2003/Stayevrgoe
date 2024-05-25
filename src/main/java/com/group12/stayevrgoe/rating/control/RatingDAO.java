package com.group12.stayevrgoe.rating.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.rating.entity.Rating;
import com.group12.stayevrgoe.rating.entity.RatingFilter;
import com.group12.stayevrgoe.rating.entity.RichRatingDTO;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.ThreadPoolUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class RatingDAO implements DAO<Rating, RatingFilter> {
    private final MongoTemplate mongoTemplate;
    private final LoadingCache<String, Rating> ratingCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(
                    new CacheLoader<String, Rating>() {
                        @Override
                        public Rating load(String key) throws Exception {
                            Rating rating = mongoTemplate.findById(key, Rating.class);
                            if (rating == null) {
                                throw new BusinessException(HttpStatus.NOT_FOUND, "Rating not found");
                            }
                            return rating;
                        }
                    }
            );

    @Override
    public Rating getByUniqueAttribute(String id) {
        try {
            return ratingCache.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    public List<Rating> get(RatingFilter filter, Pageable pageable) {
        Query query = new Query();

        if (StringUtils.hasText(filter.getHotelRoomId())) {
            query.addCriteria(Criteria.where("hotelRoomId").is(filter.getHotelRoomId()));
        }
        if (StringUtils.hasText(filter.getUserId())) {
            query.addCriteria(Criteria.where("userId").is(filter.getUserId()));
        }
        if (filter.getValue() != -1) {
            query.addCriteria(Criteria.where("value").is(filter.getValue()));
        }
        Criteria dateCriteria = Criteria.where("createdDate");
        if (filter.getFrom() != null) {
            dateCriteria.gte(filter.getFrom());
        }
        if (filter.getTo() != null) {
            dateCriteria.lte(filter.getTo());
        }
        query.addCriteria(dateCriteria);

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));

        List<Rating> ratings = mongoTemplate.find(query, Rating.class);

        ThreadPoolUtils.executeTask(
                () -> ratingCache.putAll(
                        ratings.stream()
                                .collect(
                                        java.util.stream.Collectors.toMap(
                                                Rating::getId,
                                                Function.identity()
                                        )))
        );

        return ratings;
    }

    @Override
    public Rating save(Rating rating) {
        return mongoTemplate.save(rating);
    }

    @Override
    public void delete(String id) {
        ratingCache.invalidate(id);
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), Rating.class);
    }

    public List<RichRatingDTO> getRichRatings(RatingFilter filter, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(filter.getHotelRoomId())) {
            criteria.and("hotelRoomId").is(filter.getHotelRoomId());
        }
        if (StringUtils.hasText(filter.getUserId())) {
            criteria.and("userId").is(filter.getUserId());
        }
        if (filter.getValue() != -1) {
            criteria.and("value").is(filter.getValue());
        }
        if (filter.getFrom() != null) {
            criteria.and("createdDate").gte(filter.getFrom());
        }
        if (filter.getTo() != null) {
            criteria.and("createdDate").lte(filter.getTo());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdDate")),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()),
                Aggregation.lookup("users", "userId", "_id", "user"),
                Aggregation.lookup("hotelRooms", "hotelRoomId", "_id", "hotelRoom")
        );

        List<RichRatingDTO> results =
                mongoTemplate.aggregate(aggregation, "ratings", RichRatingDTO.class)
                        .getMappedResults();

        ThreadPoolUtils.executeTask(() ->
                ratingCache.putAll(results.stream()
                        .collect(Collectors.toMap(RichRatingDTO::getId, RichRatingDTO::convertToRating))));


        return results;
    }
}
