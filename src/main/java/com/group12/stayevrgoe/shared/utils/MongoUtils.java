package com.group12.stayevrgoe.shared.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author anhvn
 */
@UtilityClass
public class MongoUtils {
    public static Aggregation getLeftJoinAggregation(String targetCollection, String localField, String foreignField, String as) {
        return Aggregation.newAggregation(
                Aggregation.lookup(targetCollection, localField, foreignField, as)
        );
    }

    public static Aggregation getInnerJoinAggregation(String targetCollection, String localField, String foreignField, String as) {
        return Aggregation.newAggregation(
                Aggregation.lookup(targetCollection, localField, foreignField, as),
                Aggregation.match(Criteria.where(as).ne(null))
        );
    }
}
