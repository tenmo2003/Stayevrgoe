package com.group12.stayevrgoe.shared.interfaces;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DAO<K, V> {
    K getByUniqueAttribute(String id);
    List<K> get(V filter, Pageable pageable);
    K save(K k);
    void delete(String id);
}
