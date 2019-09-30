package com.hzy.id.generator.service.impl;

import java.util.LinkedHashSet;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.hzy.id.generator.SpringContextHolder;
import com.hzy.id.generator.service.IdStrategy;

/**
 * 自增策略(流水号策略)
 * @author hzy
 *
 */
public class AutoIncrementStrategy implements IdStrategy {
    
    private StringRedisTemplate redisTemplate;
    private String redisKey;
    private String idType;
    private int maxLength = 7; //流水号最大长度

    public AutoIncrementStrategy(String idType) {
        this.idType = idType;
        this.redisKey = String.format("%s-auto-increment", idType);
        this.redisTemplate = SpringContextHolder.getBean("redisTemplate");
        if (this.redisTemplate == null) {
            throw new RuntimeException("redis连接失败");
        }
    }

    @Override
    public LinkedHashSet<String> makeIds(int quantity) {
        LinkedHashSet<String> ids = new LinkedHashSet<>(quantity);
        while (ids.size() < quantity) {
            Long value = redisTemplate.opsForValue().increment(redisKey);
            if (value == null) {
                throw new RuntimeException(String.format("获取编码失败，单据类型:%s", idType));
            }
            
            ids.add(this.padding(String.valueOf(value), maxLength));
        }
        return ids;
    }
    
    
    private String padding(String value, int maxLength) {
        int valueLength = value.length();
        int diff = maxLength - valueLength;
        if (diff < 0) {
            throw new RuntimeException(String.format("单据号已经超出最大长度,单据号:%s,最大长度：%s", value, maxLength));
        }
    
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < diff; i++) {
            stringBuffer.append("0");
        }
        stringBuffer.append(value);
        return stringBuffer.toString();
    }

    public String getIdType() {
        return this.idType;
    }
}
