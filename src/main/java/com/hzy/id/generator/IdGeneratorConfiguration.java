package com.hzy.id.generator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class IdGeneratorConfiguration {

	@Bean(name = "redisTemplate")
    public StringRedisTemplate redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory) {
		StringRedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
		return redisTemplate;
    }

	@Bean("springContextHolder")
	public SpringContextHolder setApplicationContext(ApplicationContext applicationContext){
		SpringContextHolder holder = new SpringContextHolder(applicationContext);
		return holder;
	}
	
}
