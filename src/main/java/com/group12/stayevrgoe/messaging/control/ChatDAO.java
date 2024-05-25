package com.group12.stayevrgoe.messaging.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.group12.stayevrgoe.hotel.control.HotelDAO;
import com.group12.stayevrgoe.messaging.entity.Chat;
import com.group12.stayevrgoe.messaging.entity.ChatFilter;
import com.group12.stayevrgoe.messaging.entity.RichChatDTO;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.interfaces.DAO;
import com.group12.stayevrgoe.shared.utils.ThreadPoolUtils;
import com.group12.stayevrgoe.user.control.UserDAO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author anhvn
 */
@Component
@RequiredArgsConstructor
public class ChatDAO implements DAO<Chat, ChatFilter> {
    private final MongoTemplate mongoTemplate;
    private final MessageDAO messageDAO;
    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;
    private final LoadingCache<String, Chat> chatCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build(new CacheLoader<>() {
                @Override
                public Chat load(String id) throws Exception {
                    Chat chat = mongoTemplate.findById(id, Chat.class);
                    if (chat == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "Chat not found");
                    }
                    return chat;
                }
            });


    @Override
    public Chat getByUniqueAttribute(String id) {
        return chatCache.getIfPresent(id);
    }

    @Override
    public List<Chat> get(ChatFilter filter, Pageable pageable) {
        return new ArrayList<>();
    }

    @Override
    public Chat save(Chat chat) {
        return mongoTemplate.save(chat);
    }

    @Override
    public void delete(String id) {
        chatCache.invalidate(id);
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), Chat.class);
    }

    public List<RichChatDTO> getRichChats(ChatFilter filter, Pageable pageable) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(filter.getCustomerId())) {
            criteria = criteria.and("customerId").is(filter.getCustomerId());
        }
        if (StringUtils.hasText(filter.getHotelId())) {
            criteria = criteria.and("hotelId").is(filter.getHotelId());
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.limit(pageable.getPageSize()),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastModifiedDate")),
                Aggregation.lookup("messages", "lastMessageId", "_id", "lastMessage"),
                Aggregation.lookup("hotels", "hotelId", "_id", "hotel"),
                Aggregation.lookup("users", "customerId", "_id", "customer")
        );

        List<RichChatDTO> results =
                mongoTemplate.aggregate(aggregation, "chats", RichChatDTO.class)
                        .getMappedResults();

        ThreadPoolUtils.executeTask(() ->
                chatCache.putAll(results.stream().collect(Collectors.toMap(RichChatDTO::getId, RichChatDTO::convertToChat))));

        return results;
    }

    public RichChatDTO getRichChat(Chat chat) {
        RichChatDTO richChatDTO = new RichChatDTO();
        richChatDTO.setId(chat.getId());
        richChatDTO.setCustomer(userDAO.getByUniqueAttribute(chat.getCustomerId()));
        richChatDTO.setHotel(hotelDAO.getByUniqueAttribute(chat.getHotelId()));
        richChatDTO.setLastMessage(messageDAO.getByUniqueAttribute(chat.getLastMessageId()));
        return richChatDTO;
    }
}
