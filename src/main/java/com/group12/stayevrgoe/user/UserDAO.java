package com.group12.stayevrgoe.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.user.domain.User;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class UserDAO {
    private final MongoTemplate mongoTemplate;
    LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, User>() {
                @Override
                public User load(String email) throws Exception {
                    User user = mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), User.class);
                    if (user == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "User not found");
                    }
                    return user;
                }
            });

    public User findByEmail(String email) {
        try {
            return userCache.get(email);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }
}
