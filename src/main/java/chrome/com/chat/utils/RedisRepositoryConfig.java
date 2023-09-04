package chrome.com.chat.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfig {
    private final RedisProperties redisProperties;

    // RedisConectionFactory를 생성하는 메서드로, Lettuce 라이브러리를 사용하여 Redis 서버와의 연결을 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // redisProperties 객체에서 호스트와 포트 정보를 가져와 LettuceConnectionFactory를 생성하여 반환
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    // RedisTemplate을 생성하는 메서드
    // RedisTemplate은 Redis와 상호작용하는 유틸리티 클래스이다.
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // key-value 직렬화를 위해 StringRedisSerializer() 사용
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
