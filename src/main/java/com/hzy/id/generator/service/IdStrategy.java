package com.hzy.id.generator.service;

import java.util.Queue;

/**
 * 单据号的生成规则
 * @author hzy
 *
 */
public interface IdStrategy {

    /**
     * 根据请求的数量返回对应的id
     * @param quantity
     * @return
     */
    public Queue<String> makeIds(int quantity);
}
