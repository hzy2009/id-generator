package com.hzy.id.generator.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.hzy.id.generator.service.IdStrategy;

/**
 * 随机码的生成
 * @author hzy
 *
 */
public class RandomStrategy implements IdStrategy {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static List<String> code = new ArrayList<>();
    private static Random random = new Random();

    static {
        //向集合中添加0-9 a-z A-Z
        //添加0~9
        for (int i = 0; i < 10; i++) {
            code.add(String.valueOf(i));
        }

        //添加大小写字母
        for (int i = 0; i < 26; i++) {
            code.add(String.valueOf((char) (65 + i)));
            code.add(String.valueOf((char) (97 + i)));
        }
    }
    
    @Override
    public Queue<String> makeIds(int quantity) {
        String dateStr = dateFormat.format(new Date());
        Queue<String> idList = new LinkedBlockingQueue<>(quantity);
        
        while (idList.size() < quantity) {
            StringBuffer uuid = new StringBuffer();
            uuid.append(dateStr);
            uuid.append('-');
            uuid.append(getEightRandomCode());
            idList.add(uuid.toString());
        }
        return idList;
    }

    /**
     * 获取八位随机码
     * @return
     */
    private static String getEightRandomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            //通过随机数，从集合中得到相应的字符
            sb.append(code.get(random.nextInt(code.size())));
        }
        return sb.toString();
    }
}
