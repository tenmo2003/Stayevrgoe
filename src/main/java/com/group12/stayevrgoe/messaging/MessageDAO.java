package com.group12.stayevrgoe.messaging;

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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class MessageDAO implements DAO<Message, MessageFilter> {
    private final MongoTemplate mongoTemplate;
    private final LoadingCache<String, Message> messageCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public Message load(String id) throws Exception {
                    Message message = mongoTemplate.findById(id, Message.class);
                    if (message == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "Message not found");
                    }
                    return message;
                }
            });

    @Override
    public Message getByUniqueAttribute(String id) {
        try {
            return messageCache.get(id);
        } catch (ExecutionException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");
        }
    }

    @Override
    public List<Message> get(MessageFilter filter, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatId").is(filter.getChatId()));

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        List<Message> messages = mongoTemplate.find(query, Message.class);

        ThreadPoolUtils.executeTask(() ->
                messageCache.putAll(
                        messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()))
                )
        );

        return messages;
    }

    @Override
    public Message save(Message message) {
        return mongoTemplate.save(message);
    }

    @Override
    public void delete(String id) {
        messageCache.invalidate(id);
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), Message.class);
    }
}
