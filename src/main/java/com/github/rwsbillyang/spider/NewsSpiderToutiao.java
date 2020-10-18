package com.github.rwsbillyang.spider;

import java.util.Map;



public class NewsSpiderToutiao  extends NewsSpiderStreamHandle implements NewsSpiderInterface {
	//private static final Logger log = LoggerFactory.getLogger(NewsSpiderToutiao.class);
	
	//https://m.toutiao.com/i6499704968549761550/
	//https://www.toutiao.com/i6499704968549761550/
	//https://www.toutiao.com/i6525188057665110531/
	//https://m.toutiao.com/i6525188057665110531/

	public String regPattern() {
		return "http(s)?://(m|www)\\.toutiao\\.com/\\S+";
	}


	public String errMsg() {
		return "请确认链接是否以开头： https://m.toutiao.com/ 或 https://www.toutiao.com/";
	}
	@Override
	public String[][] getInfoArray() {
		String[][] infoArray = {
				{SpiderConstants.BRIEF, PATTERN_CONTAIN,"<meta name=description content","<meta name=description content=",">"},
				{SpiderConstants.TITLE, PATTERN_PREFIX,"title:","'","'"},
				{SpiderConstants.CONTENT, PATTERN_PREFIX,"content:","'","'"},
				
				//{"date",PATTERN_PREFIX,"<em id=\"post-date\"",">","<"},
				//{"user",PATTERN_CONTAIN,"id=\"post-user\"",">","<"},
				//{"imgUrl",PATTERN_PREFIX,"var msg_cdn_url","\"","\""},
				//{"link",PATTERN_PREFIX,"var msg_link","\"","\""},
				
				};
		return infoArray;
	}

	public void doParse(String url, Map<String, String> map) {
		url=url.replace("//m.", "//www.");
		getPageAndParse(url,map);
//		String content = map.get("content");
//		if(content!=null)
//		{
//			
//			String src=ImgSrcUtil.getImageSrc(content);
//			if(src!=null)
//				map.put("imgUrl", src);
//			else
//				log.warn("no imgUrl in toutiao:"+url);
//		}
		map.put(SpiderConstants.LINK, url);
		map.put(SpiderConstants.USER, "今日头条");
	}


}
