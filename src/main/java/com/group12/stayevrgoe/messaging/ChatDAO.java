package com.group12.stayevrgoe.messaging;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.ThreadPoolUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class ChatDAO implements DAO<RichChatDTO, ChatFilter> {
    private final MongoTemplate mongoTemplate;
    private final Cache<String, RichChatDTO> richChatCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build();


    @Override
    public RichChatDTO getByUniqueAttribute(String id) {
        return richChatCache.getIfPresent(id);
    }

    @Override
    public List<RichChatDTO> get(ChatFilter filter, Pageable pageable) {
        Query query = new Query();

        if (StringUtils.hasText(filter.getCustomerId())) {
            query.addCriteria(Criteria.where("customerId").is(filter.getCustomerId()));
        }

        if (StringUtils.hasText(filter.getHotelId())) {
            query.addCriteria(Criteria.where("hotelId").is(filter.getHotelId()));
        }

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "lastModifiedDate"));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("customerId").is(filter.getCustomerId())),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastModifiedDate")),
                Aggregation.lookup("messages", "lastMessageId", "_id", "lastMessage"),
                Aggregation.lookup("hotels", "hotelId", "_id", "hotel"),
                Aggregation.lookup("users", "customerId", "_id", "customer")
        );

        List<RichChatDTO> results =
                mongoTemplate.aggregate(aggregation, "chats", RichChatDTO.class)
                        .getMappedResults();

        ThreadPoolUtils.executeTask(() ->
                richChatCache.putAll(results.stream().collect(Collectors.toMap(RichChatDTO::getId, Function.identity()))));

        return results;
    }

    @Override
    public RichChatDTO save(RichChatDTO chat) {
        Chat chatToSave = new Chat();
        chatToSave.setId(chat.getId());
        chatToSave.setCustomerId(chat.getCustomer().getId());
        chatToSave.setHotelId(chat.getHotel().getId());
        chatToSave.setLastMessageId(chat.getLastMessage().getId());
        
        return mongoTemplate.save(chat);
    }

    @Override
    public void delete(String id) {
        richChatCache.invalidate(id);
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), Chat.class);
    }
}
