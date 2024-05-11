package com.group12.stayevrgoe.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.BackgroundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class BookingHistoryDAO implements DAO<BookingHistory, BookingHistoryFilter> {
    private final MongoTemplate mongoTemplate;
    private final BackgroundService backgroundService;
    private final LoadingCache<String, BookingHistory> cacheById = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build(new CacheLoader<>() {
                @Override
                public BookingHistory load(String id) throws Exception {
                    return mongoTemplate.findById(id, BookingHistory.class);
                }
            });


    @Override
    public BookingHistory getByUniqueAttribute(String id) {
        try {
            return cacheById.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<BookingHistory> get(BookingHistoryFilter filter, Pageable pageable) {
        Query query = new Query();
        if (StringUtils.hasText(filter.getUserEmail())) {
            query.addCriteria(Criteria.where("userEmail").is(filter.getUserEmail()));
        }
        if (StringUtils.hasText(filter.getHotelRoomId())) {
            query.addCriteria(Criteria.where("hotelRoomId").is(filter.getHotelRoomId()));
        }
        // TODO: Check for necessity of from and to fields in filter

        List<BookingHistory> bookingHistories = mongoTemplate.find(query, BookingHistory.class);

        backgroundService.executeTask(() ->
                cacheById.putAll(bookingHistories.stream()
                        .collect(Collectors.toMap(
                                BookingHistory::getId, Function.identity()
                        ))));

        return bookingHistories;
    }

    @Override
    public BookingHistory save(BookingHistory bookingHistory) {
        BookingHistory saved = mongoTemplate.save(bookingHistory);
        backgroundService.executeTask(() -> cacheById.put(saved.getId(), saved));
        return saved;
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), BookingHistory.class);
    }
}
