package com.github.rwsbillyang.spider;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NewsSpider163 extends NewsSpiderStreamHandle implements NewsSpiderInterface{
	private static final Logger log = LoggerFactory.getLogger(NewsSpider163.class);
	//https://3g.163.com/all/article/DB8SPSIU0001875P.html
	//https://3g.163.com/all/article/DB85P66L0001899O.html

	public String regPattern() {
		return "http(s)?://3g\\.163\\.com/\\S+";
	}


	public String errMsg() {
		return "请确认链接是否以开头： https://3g.163.com/";
	}
	
	@Override
	public String[][] getInfoArray() {
		String[][] infoArray = {
				//{"date",PATTERN_PREFIX,"<em id=\"post-date\"",">","<"},
				//{"user",PATTERN_CONTAIN,"id=\"post-user\"",">","<"},
				//{"link",PATTERN_PREFIX,"var msg_link","\"","\""},
				{SpiderConstants.BRIEF, PATTERN_PREFIX,"<meta property=\"og:description\"","content=\"","\">"},
				{SpiderConstants.IMGURL, PATTERN_PREFIX,"<meta property=\"og:image\"","content=\"","\""},
				{SpiderConstants.TITLE, PATTERN_PREFIX,"<h1 class=\"title\"",">","<"},
				{SpiderConstants.CONTENT, PATTERN_MULTIPlE_LINES_EQUAL,"<div class=\"page js-page on\">",null,"<div class=\"otitle_editor\">",PATTERN_MULTIPlE_LINES_EQUAL,null,null},
				};
		return infoArray;
	}
	

	public void doParse(String url, Map<String, String> map) {
		getPageAndParse(url,map);	
		map.put(SpiderConstants.LINK, url);
		map.put(SpiderConstants.USER, "网易");
	}
	

	/**
	 * @deprecated
	 * too slow
	 * */
	public void doParseBasedInJsoup(String url, Map<String, String> map) {
		try {
			Document doc=Jsoup.connect(url).timeout(20*1000).userAgent(NewsSpider.UAs[0]).followRedirects(true).get();
			String text = doc.select("div.head > .title").text();
			map.put(SpiderConstants.TITLE, text); 
			
			text = doc.select("meta[name=description]").attr("content");
			map.put(SpiderConstants.BRIEF, text);
			
			text = doc.select("meta[property=og:image]").attr("content");
			map.put(SpiderConstants.IMGURL, text);
			
			Elements es = doc.select("div.content > .page");
			int size  = es.size();
			if(size>1)
			{
				text = es.html();
				text += es.last().html();
				log.info("has load more");
			}else
			{
				text = es.html();
			}
			map.put("content", text);
			
			
			map.put(SpiderConstants.LINK, url);
			map.put(SpiderConstants.USER, "网易");
			
			map.put(SpiderConstants.RET, SpiderConstants.OK);
			map.put(SpiderConstants.MSG, "恭喜，解析成功，请编辑保存！");
		} catch (IOException e) {
			e.printStackTrace();
			map.put(SpiderConstants.RET, SpiderConstants.KO);
			map.put(SpiderConstants.MSG, "获取文章内容超时，请重试");
		} 
      
	}

}
