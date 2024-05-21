package com.group12.stayevrgoe.rating;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.BackgroundUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

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
                            return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(key)), Rating.class);
                        }
                    }
            );

    @Override
    public Rating getByUniqueAttribute(String attr) {
        try {
            return ratingCache.get(attr);
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

        BackgroundUtils.executeTask(
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
}
