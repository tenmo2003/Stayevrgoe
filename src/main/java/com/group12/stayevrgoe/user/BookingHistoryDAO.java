package com.group12.stayevrgoe.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class BookingHistoryDAO implements DAO<BookingHistory, BookingHistoryFilter> {
    private final MongoTemplate mongoTemplate;
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


        return null;
    }

    @Override
    public BookingHistory save(BookingHistory bookingHistory) {
        return null;
    }
}
