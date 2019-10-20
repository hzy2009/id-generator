package com.hzy.id.generator.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hzy.id.generator.IdGeneratorApplication;
import com.hzy.id.generator.controller.IdGeneratorController;

@SpringBootTest(classes = IdGeneratorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ConcurrentStressTest {

	private static final Logger LOG = LoggerFactory.getLogger(ConcurrentStressTest.class);

	@Autowired
	IdGeneratorController controller;
	
	@Test
	public void makeIdsTest() throws InterruptedException, ExecutionException {
//		this.makeIdsStressTest("RONDAM");
//		this.makeIdsStressTest("SNOWFLAKE");
		this.makeIdsStressTest("AUTOINCREMENT");
//		this.makeIdsStressTest("DATEINCREMENT");
	}

	public void makeIdsStressTest(final String idType) throws InterruptedException, ExecutionException {
		List<Future<TestResult>> resultSet = new ArrayList<>();

		int maxRequest = 50 * 100 * 100; // 50w个请求， 100个并发。
//		int maxRequest = 100 * 100 * 100; // 50w个请求， 100个并发。
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		int i = 0;
		long now = System.currentTimeMillis();
		for (; i < maxRequest; i++) {

			Future<TestResult> result = executorService.submit(() -> {
				long t1 = System.nanoTime();
				String id = controller.makeId(idType);
				// 记录耗时，和结果
				return new TestResult(System.nanoTime() - t1, id);
			});

			resultSet.add(result);
		}
		LOG.info("所有请求已经发出，完成次数是：{}", i);

		executorService.shutdown();
		boolean isAllFinish = executorService.awaitTermination(120, TimeUnit.SECONDS);
		double total = Double.valueOf(System.currentTimeMillis() - now) / 1000;
		double qps = Double.valueOf(maxRequest) / total ;
		LOG.info("所有请求已经完成[{}]，完成时间是：{}, qps是:{}", isAllFinish, total, qps);

		List<TestResult> set = resultSet.stream().map(o -> {
			try {
				TestResult id = o.get();
				if (id.getResult() == null) {
					LOG.warn("结果为空");
				}
				return id;
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("出现异常", e);
				return null;
			}
		}).filter(o-> o.getResult() != null).collect(Collectors.toList());

		long all = set.stream().map(TestResult::getResult).distinct().count();

		Long first = set.stream().map(TestResult::getCost).min(Long::compare).get();
		Long last = set.stream().map(TestResult::getCost).max(Long::compare).get();
		Long avg = set.stream().map(TestResult::getCost).collect(Collectors.averagingLong(o -> {
			return o;
		})).longValue();
		LOG.info("所有请求[{}]中,不重复的有:{}，最小的时间是：{}，最大的时间是:{}，平均时长:{}", set.size(), all, first/1000000, last/1000000, avg/1000);
	}

}
