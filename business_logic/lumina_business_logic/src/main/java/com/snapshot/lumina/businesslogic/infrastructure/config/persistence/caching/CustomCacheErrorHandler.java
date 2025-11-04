package com.snapshot.lumina.businesslogic.infrastructure.config.persistence.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.error("Redis cache GET error - Cache: {}, Key: {}, Error: {}",
                cache.getName(), key, exception.getMessage(), exception);
        // Fail silently and fall through to database
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("Redis cache PUT error - Cache: {}, Key: {}, Error: {}",
                cache.getName(), key, exception.getMessage(), exception);
        // Fail silently and continue without caching
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Redis cache EVICT error - Cache: {}, Key: {}, Error: {}",
                cache.getName(), key, exception.getMessage(), exception);
        // Fail silently and continue
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Redis cache CLEAR error - Cache: {}, Error: {}",
                cache.getName(), exception.getMessage(), exception);
        // Fail silently and continue
    }
}

