package com.group12.stayevrgoe.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class UserDAO implements DAO<User, UserFilter> {
    private final MongoTemplate mongoTemplate;
    LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
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

    @Override
    public List<User> get(UserFilter filter) {
        return null;
    }

    @Override
    public User save(User k) {
        User user = mongoTemplate.save(k);
        userCache.put(user.getEmail(), user);
        return user;
    }
}
