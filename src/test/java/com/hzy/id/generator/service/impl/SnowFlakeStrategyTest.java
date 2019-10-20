package com.hzy.id.generator.service.impl;

import java.util.Iterator;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hzy.id.generator.IdGeneratorApplication;

@SpringBootTest(classes=IdGeneratorApplication.class, webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class SnowFlakeStrategyTest {
	
	@Test
	public void makeIdsTest(){
		int quantity = 100000;
		SnowFlakeStrategy strategy = new SnowFlakeStrategy();
		long t1 = System.currentTimeMillis();
		Queue<String> ids = strategy.makeIds(quantity);
		long cost = System.currentTimeMillis() - t1;
		System.out.print(String.format("生成 [%s] 个Id，耗时 [%s]ms", quantity, cost));
		
		Assert.assertEquals("id数量不对", ids.size(), quantity);
		
		Iterator<String> iterator = ids.iterator();
		String first = iterator.next();
		String last = null;
		while (iterator.hasNext()) {
			last = iterator.next();
		}
		System.out.println(String.format("第一个：%s,最后一个:%s", first, last));
	}

}
