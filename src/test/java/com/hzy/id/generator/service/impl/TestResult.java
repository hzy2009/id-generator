package com.hzy.id.generator.service.impl;

public class TestResult implements Comparable<TestResult> {
	
	private Long cost;
	private String result;
	
	public TestResult(long cost, String reslut) {
		super();
		this.cost = cost;
		this.result = reslut;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj == null) {
//			return false;
//		}
//		
//		if (!(obj instanceof TestResult)) {
//			return false;
//		}
//		
//		TestResult t = (TestResult) obj;
//		
//		if (this.getReslut() == null && t.getReslut() == null) {
//			return true;
//		}
//		
//		if (this.getReslut() == null && t.getReslut() != null) {
//			return false;
//		}
//		
//		return this.getReslut().equals(t.reslut);
//	}

	public Long getCost() {
		return cost;
	}

	public String getResult() {
		return result;
	}

	@Override
	public int compareTo(TestResult o) {
		if (o == null) {
			return 1;
		}
		
		if (this.getCost() == null && o.getCost() == null) {
			return 0;
		}
		
		if (this.getCost() == null && o.getCost() != null) {
			return -1;
		}
		
		if (this.getCost() != null && o.getCost() == null) {
			return 1;
		}
		
		return this.getCost().compareTo(o.getCost());
	}

}
