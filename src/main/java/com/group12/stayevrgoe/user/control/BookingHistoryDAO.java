package com.group12.stayevrgoe.user.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.ThreadPoolUtils;
import com.group12.stayevrgoe.user.entity.BookingHistory;
import com.group12.stayevrgoe.user.entity.BookingHistoryFilter;
import com.group12.stayevrgoe.user.entity.RichBookingHistoryDTO;
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
    private final LoadingCache<String, BookingHistory> cacheById = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build(new CacheLoader<>() {
                @Override
                public BookingHistory load(String id) throws Exception {
                    BookingHistory bookingHistory = mongoTemplate.findById(id, BookingHistory.class);
                    if (bookingHistory == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "Booking history not found");
                    }
                    return null;
                }
            });


    @Override
    public BookingHistory getById(String id) {
        try {
            return cacheById.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<BookingHistory> get(BookingHistoryFilter filter, Pageable pageable) {
        Query query = new Query();
        if (StringUtils.hasText(filter.getUserId())) {
            query.addCriteria(Criteria.where("userId").is(filter.getUserId()));
        }
        if (StringUtils.hasText(filter.getHotelRoomId())) {
            query.addCriteria(Criteria.where("hotelRoomId").is(filter.getHotelRoomId()));
        }

        List<BookingHistory> bookingHistories = mongoTemplate.find(query, BookingHistory.class);

        ThreadPoolUtils.executeTask(() ->
                cacheById.putAll(bookingHistories.stream()
                        .collect(Collectors.toMap(
                                BookingHistory::getId, Function.identity()
                        ))));

        return bookingHistories;
    }

    @Override
    public BookingHistory save(BookingHistory bookingHistory) {
        BookingHistory saved = mongoTemplate.save(bookingHistory);
        ThreadPoolUtils.executeTask(() -> cacheById.put(saved.getId(), saved));
        return saved;
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), BookingHistory.class);
    }

    public List<RichBookingHistoryDTO> getRichBookingHistories(BookingHistoryFilter filter, Pageable pageable) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(filter.getUserId())) {
            criteria.and("userId").is(filter.getUserId());
        }
        if (StringUtils.hasText(filter.getHotelRoomId())) {
            criteria.and("hotelRoomId").is(filter.getHotelRoomId());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "_id")),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()),
                Aggregation.lookup("hotels", "hotelId", "_id", "hotel"),
                Aggregation.lookup("users", "customerId", "_id", "customer"),
                Aggregation.lookup("hotelRooms", "hotelRoomId", "_id", "hotelRoom")
        );

        List<RichBookingHistoryDTO> results =
                mongoTemplate.aggregate(aggregation, "bookingHistories", RichBookingHistoryDTO.class)
                        .getMappedResults();

        ThreadPoolUtils.executeTask(() ->
                cacheById.putAll(results.stream()
                        .collect(Collectors.toMap(RichBookingHistoryDTO::getId, RichBookingHistoryDTO::toBookingHistory))));

        return results;
    }
}
