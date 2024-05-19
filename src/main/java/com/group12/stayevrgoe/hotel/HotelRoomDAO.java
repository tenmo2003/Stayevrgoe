package com.group12.stayevrgoe.hotel;

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
import org.springframework.util.CollectionUtils;

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
public class HotelRoomDAO implements DAO<HotelRoom, HotelRoomFilter> {
    private final MongoTemplate mongoTemplate;

    private final LoadingCache<String, HotelRoom> cacheById = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build(new CacheLoader<>() {
                @Override
                public HotelRoom load(String id) throws Exception {
                    return mongoTemplate.findById(id, HotelRoom.class);
                }
            });

    @Override
    public HotelRoom getByUniqueAttribute(String id) {
        try {
            return cacheById.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<HotelRoom> get(HotelRoomFilter filter, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where("hotelId").is(filter.getHotelId()));
        if (CollectionUtils.isEmpty(filter.getFacilities())) {
            query.addCriteria(Criteria.where("facilities").all(filter.getFacilities()));
        }
        query.addCriteria(Criteria.where("priceInUSD").gte(filter.getMinPrice()).lte(filter.getMaxPrice()));

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<HotelRoom> rooms = mongoTemplate.find(query, HotelRoom.class);

        BackgroundUtils.executeTask(() ->
                cacheById.putAll(
                        rooms.stream()
                                .collect(Collectors.toMap(
                                        HotelRoom::getId, Function.identity())
                                )));
        return rooms;
    }

    @Override
    public HotelRoom save(HotelRoom hotelRoom) {
        HotelRoom room = mongoTemplate.save(hotelRoom);
        BackgroundUtils.executeTask(() -> cacheById.put(room.getId(), room));
        return room;
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), HotelRoom.class);
    }
}
