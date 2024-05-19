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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelDAO implements DAO<Hotel, HotelFilter> {
    private final MongoTemplate mongoTemplate;
    private final LoadingCache<String, Hotel> hotelCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public Hotel load(String id) throws Exception {
                    return mongoTemplate.findById(id, Hotel.class);
                }
            });

    @Override
    public Hotel getByUniqueAttribute(String id) {
        try {
            return hotelCache.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<Hotel> get(HotelFilter filter, Pageable pageable) {
        Query query = new Query();
        if (StringUtils.hasText(filter.getLocation())) {
            // make a search query with some regexp
            query.addCriteria(
                    Criteria.where("location")
                            .regex(String.format("^%s", filter.getLocation()), "i")
            );
        }
        if (StringUtils.hasText(filter.getName())) {
            // make a search query with some regexp
            query.addCriteria(
                    Criteria.where("name")
                            .regex(String.format("^%s", filter.getName()), "i")
            );
        }
        if (!CollectionUtils.isEmpty(filter.getFacilities())) {
            query.addCriteria(
                    Criteria.where("facilities").all(filter.getFacilities())
            );
        }
        query.addCriteria(Criteria.where("minPriceInUSD").gte(filter.getMinPrice()));
        query.addCriteria(Criteria.where("maxPriceInUSD").lte(filter.getMaxPrice()));

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<Hotel> hotels = mongoTemplate.find(query, Hotel.class);

        BackgroundUtils.executeTask(() ->
                hotelCache.putAll(hotels.stream()
                .collect(Collectors.toMap(Hotel::getId, Function.identity()))));

        return hotels;
    }

    @Override
    public Hotel save(Hotel hotel) {
        return mongoTemplate.save(hotel);
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), Hotel.class);
    }

}
