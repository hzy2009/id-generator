package com.hzy.id.generator.service;

import java.util.LinkedHashSet;

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
	public LinkedHashSet<String> makeIds(int quantity);
}
