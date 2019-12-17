package com.github.rwsbillyang.spider;

import java.util.Map;


public interface NewsSpiderInterface {

	/**
	 * url是否合法正确
	 * */
	public  String regPattern();
	
	/**
	 * url格式不对时的提示信息
	 * */
	public  String errMsg();
	
	/**
	 * parse page
	 * */
	public  void doParse(String url,Map<String,String> map);
	
}
