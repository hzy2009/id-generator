package com.hzy.id.generator.service.impl;

import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hzy.id.generator.IdGeneratorApplication;

@SpringBootTest(classes=IdGeneratorApplication.class, webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class RandomStrategyTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(RandomStrategyTest.class);
	
	@Test
	public void makeIdsTest(){
		int quantity = 100000;
		RandomStrategy strategy = new RandomStrategy();
		long t1 = System.currentTimeMillis();
		Queue<String> ids = strategy.makeIds(quantity);
		long cost = System.currentTimeMillis() - t1;
		LOG.info(String.format("生成 [%s] 个Id，耗时 [%s]ms", quantity, cost));
		Assert.assertEquals("id数量不对", ids.size(), quantity);
	}
	
}
