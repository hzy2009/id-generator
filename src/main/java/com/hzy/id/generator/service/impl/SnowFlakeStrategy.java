package com.hzy.id.generator.service.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzy.id.generator.SpringContextHolder;
import com.hzy.id.generator.service.IdStrategy;

/**
 * 雪花Id策略
 * @author hzy
 *
 */
public class SnowFlakeStrategy implements IdStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(SnowFlakeStrategy.class);

    private final static long TWEPOCH = 1288834974657L;

    // 机器标识位数
    private final static long WORKER_ID_BITS = 5L;

    // 数据中心标识位数
    private final static long DATA_CENTER_ID_BITS = 5L;

    // 机器ID最大值 31
    private final static long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

    // 数据中心ID最大值 31
    private final static long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);

    // 毫秒内自增位
    private final static long SEQUENCE_BITS = 12L;

    // 机器ID偏左移12位
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;

    private final static long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // 时间毫秒左移22位
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private final static long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    private long lastTimestamp = -1L;

    private long sequence = 0L;
    private final long workerId;
    private final long dataCenterId;
    
    @Override
    public LinkedHashSet<String> makeIds(int quantity) {
        LinkedHashSet<String> ids = new LinkedHashSet<>(quantity);
        while (ids.size() < quantity ) {
            ids.add(String.valueOf(nextId()));
        }
        return ids;
    }

    /**
     * 单例禁止new实例化
     * @param workerId
     * @param dataCenterId
     */
    public SnowFlakeStrategy() {
        
//        String workerIdStr = SpringContextHolder.getEvnProperties("id.generator.workerId");
        //第一次使用获取mac地址的
        long workerIdLong = 0;
        try {
            workerIdLong = getWorkerId();
        } catch (SocketException | UnknownHostException | NullPointerException e) {
            logger.error("获取机器ID失败", e);
        }
        if (workerIdLong > MAX_WORKER_ID || workerIdLong <= 0) {
            workerId = getRandom();
            
        }else {
            workerId = workerIdLong;
        }

        String dataCenterIdStr = SpringContextHolder.getEvnProperties("id.generator.dataCenterId");
        long dataCenterIdLong = 0;
        if (dataCenterIdStr == null || !dataCenterIdStr.trim().matches("\\d+")) {
            logger.warn(String.format("数据中心ID配置异常，配置不能为空，而且必须位数字。数据中心Id：%s", dataCenterIdStr));
            dataCenterIdLong = getRandom();
        }else {
            dataCenterIdLong = Long.valueOf(dataCenterIdStr.trim());
        }
        
        if (dataCenterIdLong > MAX_DATA_CENTER_ID || dataCenterIdLong <= 0) {
            logger.warn(String.format("%s 数据中心ID最大值 必须是 %d 到 %d 之间", dataCenterIdLong, 0, MAX_DATA_CENTER_ID));
            dataCenterId = getRandom();
        }else {
            dataCenterId = dataCenterIdLong;
        }
    }

    /**
     * 生成1-31之间的随机数
     *
     * @return
     */
    private static long getRandom() {
        int max = (int) (MAX_WORKER_ID);
        int min = 1;
        Random random = new Random();
        long result = random.nextInt(max - min) + min;
        return result;
    }

    private long nextId() {
        long timestamp = time();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟向后移动，拒绝生成id  " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;

        // ID偏移组合生成最终的ID，并返回ID
        long nextId = ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
            | (dataCenterId << DATA_CENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;

        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.time();
        while (timestamp <= lastTimestamp) {
            timestamp = this.time();
        }
        return timestamp;
    }

    private long time() {
        return System.currentTimeMillis();
    }

    private static long getWorkerId() throws SocketException, UnknownHostException, NullPointerException {
        @SuppressWarnings("unused")
        InetAddress ip = InetAddress.getLocalHost();

        NetworkInterface network = null;
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface nint = en.nextElement();
            if (!nint.isLoopback() && nint.getHardwareAddress() != null) {
                network = nint;
                break;
            }
        }

        byte[] mac = network.getHardwareAddress();

        Random rnd = new Random();
        byte rndByte = (byte) (rnd.nextInt() & 0x000000FF);

        // 取mac地址最后一位和随机数
        return ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) rndByte) << 8))) >> 6;
    }
}