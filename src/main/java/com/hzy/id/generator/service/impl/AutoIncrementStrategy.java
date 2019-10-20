package com.hzy.id.generator.service.impl;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.hzy.id.generator.SpringContextHolder;
import com.hzy.id.generator.service.IdStrategy;

/**
 * 自增策略(流水号策略)
 * @author hzy
 *
 */
public class AutoIncrementStrategy implements IdStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(AutoIncrementStrategy.class);
    private static final Long maxWaitTimeout = 10000L;
    private StringRedisTemplate redisTemplate;
    private String redisKey;
    private String idType;
    private int maxLength = 7; //流水号最大长度
    private int cacheLength; //缓存的容量 
    LinkedBlockingQueue<Long> cache;
    private Condition isWaitForFull;
    private Condition isWaitForEmpty;
    private AtomicInteger waitCount = new AtomicInteger();
    ReentrantLock lock;
    
    
    public AutoIncrementStrategy (String idType) {
        this(idType, 1000);
    }

    public AutoIncrementStrategy(String idType, int cacheLength) {
        this.idType = idType;
        this.redisKey = String.format("%s-auto-increment", idType);
        this.redisTemplate = SpringContextHolder.getBean("redisTemplate");
        if (this.redisTemplate == null) {
            throw new RuntimeException("redis连接失败");
        }
        
        this.cacheLength = cacheLength;
        cache = new LinkedBlockingQueue<>();
        
        lock = new ReentrantLock();
        
        isWaitForFull = lock.newCondition();
        isWaitForEmpty = lock.newCondition();
        
        Thread writeCacheThread = new Thread(new WriteCacheTask());
        writeCacheThread.setName("AutoIncrementStrategy-write-cache-Thread");
        writeCacheThread.start();
    }

    @Override
    public Queue<String> makeIds(int quantity) {
        Queue<String> ids = new LinkedBlockingQueue<>(quantity);
        try {
	        while (ids.size() < quantity) {
	            Long value = this.readFromCache();
	            ids.add(this.padding(String.valueOf(value), maxLength));
	        }
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        return ids;
    }

    private Long readFromCache() {
        long t1 = System.currentTimeMillis();
        Long value = null;
        
        while(value == null){
            value = cache.poll();
            
            if (value == null ) {
            	logger.debug("没有拿到号，休息下");
            	try {
            		lock.lock();
            		waitCount.incrementAndGet();
            		isWaitForEmpty.signal();
					isWaitForFull.await(1, TimeUnit.SECONDS);
					
				} catch (InterruptedException e) {
					logger.error("等待拿号的过程中出现异常", e);
					throw new RuntimeException(e);
					
				}finally {
					lock.unlock();
					waitCount.decrementAndGet();
				}
            }
            
            if (System.currentTimeMillis() - t1 > maxWaitTimeout) {
                throw new RuntimeException("等待太久都没有拿到号");
            }
        }
        
        return value;
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
    
    class WriteCacheTask implements Runnable{
        private boolean status = true;
        
        WriteCacheTask(){
        	super();
        }

        @Override
        public void run() {
            logger.info("从redis获取自增编码的缓存写入开始");
            while(status){
                
                if (!cache.isEmpty()) {
                    try {
                    	lock.lock();
                    	isWaitForEmpty.await();
                    } catch (InterruptedException e) {
                        logger.error("从redis获取自增编码的缓存写入异常", e);
                    }finally {
                    	lock.unlock();
					}
                    
                    continue;
                }
                
                try {
                	lock.lock();
                    logger.debug("开始拿从reids拿号");
                    Long maxLong = redisTemplate.opsForValue().increment(AutoIncrementStrategy.this.redisKey, cacheLength);
                    
                    for(int i=0; i<cacheLength; i++){
                        cache.offer(maxLong - cacheLength + i + 1);
                    }
                    logger.debug("拿到号并且缓存好了");
                    isWaitForFull.signalAll();
                } catch (Exception e) {
                	logger.error("从redis获取自增编码的缓存写入异常", e);
                	
				} finally {
					lock.unlock();
				}
                logger.info("从redis获取自增编码的缓存写入结束");
            }
        }
        
        
    }
}
