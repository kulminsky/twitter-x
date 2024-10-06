package com.twitter.x.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory()
    }

    @Bean
    RedisTemplate<String, Integer> redisTemplate() {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>()
        redisTemplate.setConnectionFactory(redisConnectionFactory())
        redisTemplate.setKeySerializer(new StringRedisSerializer())
        redisTemplate.setValueSerializer(new StringRedisSerializer())
        return redisTemplate
    }
}
