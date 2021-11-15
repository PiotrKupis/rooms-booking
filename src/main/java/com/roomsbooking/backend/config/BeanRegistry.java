package com.roomsbooking.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class responsible for registering beans.
 */
@Configuration
@EnableCaching
public class BeanRegistry {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("resort", "room");
    }
}
