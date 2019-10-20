package com.hzy.id.generator.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * 日期加自增策略
 * @author hzy
 *
 */
public class DateIncrementStrategy extends AutoIncrementStrategy {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public DateIncrementStrategy(String idType) {
		super(idType);
	}

	@Override
	public Queue<String> makeIds(int quantity) {
		String dateStr = dateFormat.format(new Date());
		Queue<String> ids = super.makeIds(quantity);
		if (ids == null || ids.size() != quantity) {
			throw new RuntimeException(String.format("获取编码失败，单据类型:%s", this.getIdType()));
		}
		
		return ids.stream().map(id->{
			return String.format("%s%s", dateStr, id);
		}).collect(Collectors.toCollection(LinkedBlockingQueue::new));
	}

}
