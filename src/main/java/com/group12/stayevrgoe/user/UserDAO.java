package com.group12.stayevrgoe.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.ThreadPoolUtils;
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
import java.util.stream.Collectors;

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

    @Override
    public User getByUniqueAttribute(String email) {
        try {
            return userCache.get(email);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<User> get(UserFilter filter, Pageable pageable) {
        Query query = new Query();

        if (StringUtils.hasText(filter.getEmail())) {
            query.addCriteria(Criteria.where("email")
                    .regex(String.format("^%s", filter.getEmail()), "i"));
        }
        if (StringUtils.hasText(filter.getName())) {
            query.addCriteria(Criteria.where("name")
                    .regex(String.format("^%s", filter.getName()), "i"));
        }
        if (filter.getRole() != null) {
            query.addCriteria(Criteria.where("role").is(filter.getRole()));
        }

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<User> users = mongoTemplate.find(query, User.class);

        ThreadPoolUtils.executeTask(() ->
                userCache.putAll(users.stream()
                        .collect(Collectors.toMap(User::getEmail, Function.identity()))));

        return users;
    }

    @Override
    public User save(User k) {
        User user = mongoTemplate.save(k);
        ThreadPoolUtils.executeTask(() -> userCache.put(user.getEmail(), user));
        return user;
    }

    @Override
    public void delete(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), User.class);
    }
}
