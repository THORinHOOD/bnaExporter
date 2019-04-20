package com.archimatetool.example.utils;

public class Scripts {
	private String prepublish;
	private String pretest;
	private String lint;
	private String test;
	
	public Scripts setPrepublish(String prepublish) {
		this.prepublish = prepublish;
		return this;
	}
	
	public Scripts setPretest(String pretest) {
		this.pretest = pretest;
		return this;
	}
	
	public Scripts setLint(String lint) {
		this.lint = lint;
		return this;
	}
	
	public Scripts setTest(String test) {
		this.test = test;
		return this;
	}
	
	public String getPrepublish() {
		return prepublish;
	}
	
	public String getPretest() {
		return pretest;
	}
	
	public String getLint() {
		return lint;
	}
}
