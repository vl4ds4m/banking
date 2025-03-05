package edu.vl4ds4m.banking.config;

import edu.vl4ds4m.banking.filter.IdempotencyFilter;
import edu.vl4ds4m.banking.properties.IdempotencyProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class IdempotencyConfig {
    private final IdempotencyProperties properties;

    public IdempotencyConfig(IdempotencyProperties properties) {
        this.properties = properties;
    }

    @Bean
    RedisTemplate<String, IdempotencyFilter.IdempotencyValue> redisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<IdempotencyFilter.IdempotencyValue> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(IdempotencyFilter.IdempotencyValue.class);

        RedisTemplate<String, IdempotencyFilter.IdempotencyValue> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

    @Bean
    public FilterRegistrationBean<IdempotencyFilter> idempotenceFilterRegistrationBean(
            RedisTemplate<String, IdempotencyFilter.IdempotencyValue> redisTemplate
    ) {
        FilterRegistrationBean<IdempotencyFilter> registrationBean = new FilterRegistrationBean<>();

        IdempotencyFilter idempotencyFilter = new IdempotencyFilter(redisTemplate, properties.ttl());

        registrationBean.setFilter(idempotencyFilter);
        registrationBean.addUrlPatterns(properties.paths());
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
