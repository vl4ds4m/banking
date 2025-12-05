package org.vl4ds4m.banking.common.handler.idempotency;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.vl4ds4m.banking.common.Common;

@Configuration
@ConditionalOnBooleanProperty(name = "enabled", prefix = IdempotencyProperties.PREFIX)
@EnableConfigurationProperties(IdempotencyProperties.class)
@RequiredArgsConstructor
public class IdempotencyConfig implements EnvironmentAware {

    private final IdempotencyProperties idempotencyProps;

    @Nullable
    private Environment environment;

    @Bean
    public RedisTemplate<String, IdempotencyValue> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var template = new RedisTemplate<String, IdempotencyValue>();
        template.setConnectionFactory(redisConnectionFactory);

        var stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(IdempotencyValue.class);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

    @Bean
    public IdempotencyInterceptor idempotencyInterceptor(RedisTemplate<String, IdempotencyValue> redisTemplate) {
        IdempotencyHandler handler = useRedis()
                ? new RedisIdempotencyHandler(redisTemplate, idempotencyProps.ttl())
                : new DummyIdempotencyHandler();
        return new IdempotencyInterceptor(handler);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private Environment getEnvironment() {
        if (environment == null) {
            throw new IllegalStateException();
        }
        return environment;
    }

    private boolean useRedis() {
        Environment env = getEnvironment();
        return env.matchesProfiles("redis") || !Common.isRunStandalone(env);
    }
}
