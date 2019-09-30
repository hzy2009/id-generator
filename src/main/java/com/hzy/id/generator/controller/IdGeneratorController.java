package com.hzy.id.generator.controller;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hzy.id.generator.client.IdGeneratorService;
import com.hzy.id.generator.service.IdGeneratorFactory;
import com.hzy.id.generator.service.IdStrategy;

@RestController
@RequestMapping("idGenerator")
public class IdGeneratorController implements IdGeneratorService {
	
	@RequestMapping("makeId")
	@Override
	public String makeId(String idType) {
		Set<String> ids = this.makeIds(idType, 1);
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		return ids.iterator().next();
	}

	@RequestMapping("makeIds")
	@Override
	public LinkedHashSet<String> makeIds(String idType, int quantity) {
		IdStrategy idStrategy = IdGeneratorFactory.bulid(idType);
		return idStrategy.makeIds(quantity);
	}

}
