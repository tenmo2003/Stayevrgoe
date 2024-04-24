package com.group12.stayevrgoe.hotel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HotelDAO implements DAO<Hotel, HotelFilter> {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Hotel> get(HotelFilter filter) {
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

        return mongoTemplate.find(query, Hotel.class);
    }

    @Override
    public Hotel save(Hotel hotel) {
        return mongoTemplate.save(hotel);
    }
}
