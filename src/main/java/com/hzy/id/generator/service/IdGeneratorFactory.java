package com.hzy.id.generator.service;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzy.id.generator.service.impl.AutoIncrementStrategy;
import com.hzy.id.generator.service.impl.DateIncrementStrategy;
import com.hzy.id.generator.service.impl.RandomStrategy;
import com.hzy.id.generator.service.impl.SnowFlakeStrategy;

public class IdGeneratorFactory {
    
    private static Logger logger = LoggerFactory.getLogger(IdGeneratorFactory.class);

    /**
     * 单据类型与规则策略的Map
     */
    private static Map<String, IdStrategy> typeAndStrategyMap = new Hashtable<>();
    
    public static IdStrategy bulid(String idType){
        
        IdStrategy strategy = typeAndStrategyMap.get(idType);
        
        if (strategy == null) {
            synchronized (typeAndStrategyMap) {
                if (typeAndStrategyMap.get(idType) == null) {
                    strategy = bulidByType(idType);
                    typeAndStrategyMap.put(idType, strategy);
                }
            }
        }
        
        strategy = typeAndStrategyMap.get(idType);
        logger.trace("根据单据类型[{}],获取单据规则[{}]", idType, strategy.getClass());
        return strategy;
    }

    private static IdStrategy bulidByType(String idType) {
        switch (idType) {
        case "AUTOINCREMENT": 
            // 如果是采购单
            return new AutoIncrementStrategy("PO");
        case "DATEINCREMENT":
            // 如果是送货单
            return new DateIncrementStrategy("DO");
        case "RANDOM":
            // 如果是对账单
            return new RandomStrategy();
        case "SNOWFLAKE":
            // 如果是文件的编码获取
            return new SnowFlakeStrategy();
        default:
            logger.error("单据类型[{}],没有匹配的单据规则策略", idType);
            throw new RuntimeException("该单据类型没有匹配的单据规则策略");
        }        
    }

    private IdGeneratorFactory() {
        super();
    }
}
