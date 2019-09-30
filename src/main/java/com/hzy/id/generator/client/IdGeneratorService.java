package com.hzy.id.generator.client;

import java.util.LinkedHashSet;

/**
 * 发号机对外开放的接口
 * @author hzy
 *
 */
public interface IdGeneratorService {
	
	/**
	 * 生成下一个单据号
	 * @param idType 单据类型
	 * @return
	 */
	public String makeId(String idType);
	
	/**
	 * 生成一批单据号
	 * @param idType 单据类型
	 * @param auantity 需要生成的单据号的数量
	 * @return
	 */
	public LinkedHashSet<String> makeIds(String idType, int quantity);

}
