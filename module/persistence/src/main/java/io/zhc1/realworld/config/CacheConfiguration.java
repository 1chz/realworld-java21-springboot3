package io.zhc1.realworld.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheName.ALL_TAGS);
        cacheManager.setCaffeine(
                Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(500));

        return cacheManager;
    }
}
