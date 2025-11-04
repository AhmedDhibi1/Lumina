package com.snapshot.lumina.businesslogic.infrastructure.config.persistence.caching;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfig extends CachingConfigurerSupport {

    private final CustomCacheErrorHandler cacheErrorHandler;

    public CachingConfig(CustomCacheErrorHandler cacheErrorHandler) {
        this.cacheErrorHandler = cacheErrorHandler;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return cacheErrorHandler;
    }
}
