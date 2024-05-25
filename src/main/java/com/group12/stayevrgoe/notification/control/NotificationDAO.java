package com.group12.stayevrgoe.notification.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.notification.entity.Notification;
import com.group12.stayevrgoe.notification.entity.NotificationFilter;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class NotificationDAO implements DAO<Notification, NotificationFilter> {
    private final MongoTemplate mongoTemplate;
    private final LoadingCache<String, Notification> notificationCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public Notification load(String id) throws Exception {
                    Notification notification = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), Notification.class);
                    if (notification == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "Notification not found");
                    }
                    return notification;
                }
            });

    @Override
    public Notification getByUniqueAttribute(String id) {
        try {
            return notificationCache.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<Notification> get(NotificationFilter filter, Pageable pageable) {
        Query query = new Query();

        if (filter.getSentTo() != null) {
            query.addCriteria(Criteria.where("sentTo").is(filter.getSentTo()));
        }
        if (filter.getType() != null) {
            query.addCriteria(Criteria.where("type").is(filter.getType()));
        }

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        List<Notification> notifications = mongoTemplate.find(query, Notification.class);

        ThreadPoolUtils.executeTask(() ->
                notificationCache.putAll(notifications.stream()
                        .collect(java.util.stream.Collectors.toMap(Notification::getId, notification -> notification))));

        return notifications;
    }

    @Override
    public Notification save(Notification notification) {
        return mongoTemplate.save(notification);
    }

    @Override
    public void delete(String id) {
        notificationCache.invalidate(id);
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), Notification.class);
    }
}
