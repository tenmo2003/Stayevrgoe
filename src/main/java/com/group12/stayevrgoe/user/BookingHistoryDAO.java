package com.group12.stayevrgoe.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.BackgroundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
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
    private final LoadingCache<String, List<BookingHistory>> cacheByUserEmail = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(100)
            .build(new CacheLoader<>() {
                @Override
                public List<BookingHistory> load(String userId) throws Exception {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("userId").is(userId));
                    return mongoTemplate.find(query, BookingHistory.class);
                }
            });

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
        return null;
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

        backgroundService.executeTask(() -> {
            cacheByUserEmail.putAll(bookingHistories.stream()
                    .collect(Collectors.groupingBy(
                                    BookingHistory::getUserEmail,
                                    Collectors.toList()
                            )
                    ));

            cacheById.putAll(bookingHistories.stream()
                    .collect(Collectors.toMap(
                            BookingHistory::getId, Function.identity()
                    )));
        });

        return Collections.emptyList();
    }

    @Override
    public BookingHistory save(BookingHistory bookingHistory) {
        BookingHistory saved = mongoTemplate.save(bookingHistory);
        cacheByUserEmail.invalidate(bookingHistory.getUserEmail());
        return saved;
    }
}
