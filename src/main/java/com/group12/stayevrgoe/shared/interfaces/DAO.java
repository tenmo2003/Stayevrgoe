package com.group12.stayevrgoe.shared.interfaces;

import java.util.List;

public interface DAO<K, V> {
    List<K> get(V filter);
    K save(K k);
}
