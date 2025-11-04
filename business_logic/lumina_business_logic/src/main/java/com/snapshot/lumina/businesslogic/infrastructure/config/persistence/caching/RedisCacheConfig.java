package com.snapshot.lumina.businesslogic.infrastructure.config.persistence.caching;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Enable polymorphic type handling for security
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     ObjectMapper cacheObjectMapper) {

        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(cacheObjectMapper)
                        )
                );

        // Specific cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Workspaces cache - 30 minutes
        cacheConfigurations.put("workspaces",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // Workspaces by user - 20 minutes (more volatile)
        cacheConfigurations.put("workspacesByUser",
                defaultConfig.entryTtl(Duration.ofMinutes(20)));

        // Existence checks - 15 minutes (frequent changes)
        cacheConfigurations.put("workspaceExists",
                defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // Name existence checks - 10 minutes
        cacheConfigurations.put("workspaceExistsByName",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Workspace members - 25 minutes
        cacheConfigurations.put("workspaceMembers",
                defaultConfig.entryTtl(Duration.ofMinutes(25)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}