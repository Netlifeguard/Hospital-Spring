package com.nie.common.config;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BloomFilterConfig {
    private BloomFilter<Integer> bloomFilter;

    @PostConstruct
    public void init() {
        bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 1000, 0.01);
    }

    public void add(Integer id) {
        bloomFilter.put(id);
    }

    public boolean mightContain(Integer id) {
        return bloomFilter.mightContain(id);
    }
}
