package com.github.rwsbillyang.spider;

import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NewsSpider {
	private static final Logger log = LoggerFactory.getLogger(NewsSpider.class);
	
	//"Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2"
	public final static String UserAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
	public final static String[] UAs= {
			"Mozilla/5.0 (iPhone; CPU iPhone OS 12_4_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 NetType/WIFI Language/zh_CN",
			"Mozilla/5.0 (Linux; Android 7.1.1; PRO 6s Build/NMF26O; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045008 Mobile Safari/537.36 MMWEBID/2921",
			"Mozilla/5.0 (Linux; Android 9; COR-AL10 Build/HUAWEICOR-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045008 Mobile Safari/537.36 MMWEBID/1039"
	};
	

	private NewsSpiderWechat newsSpiderWechat;

	private NewsSpiderToutiao newsSpiderToutiao;

	private NewsSpider163 newsSpider163;
	
	
	
	public  NewsSpiderInterface getSpiderByUrl(String url)
	{
		//if(StringUtils.isEmpty(url))
		//	return null;
		if(url.contains("mp.weixin.qq.com")) {
			if(newsSpiderWechat==null) newsSpiderWechat = new NewsSpiderWechat();
			return newsSpiderWechat;
		}else if(url.contains("toutiao.com"))
		{
			if(newsSpiderToutiao == null) newsSpiderToutiao = new NewsSpiderToutiao();
			return newsSpiderToutiao;
		}
		else if(url.contains("163.com"))
		{
			if(newsSpider163==null) newsSpider163 = new NewsSpider163();
			return newsSpider163;
		}else
		{
			return null;
		}
	}
	
	public void parse(String url,Map<String,String> map) {
		NewsSpiderInterface spider = getSpiderByUrl(url);
		if(spider==null)
		{
			 map.put(Constants.RET, Constants.KO);
			 map.put(Constants.MSG, "暂只支持微信、今日头条和163！请确认链接域名是否正确");
			 return;
		}
		boolean isInvalid = Pattern.matches(spider.regPattern(), url);
		if (!isInvalid) {
			log.warn("url not match regPattern="+spider.regPattern() + ",url="+url);
			map.put(Constants.RET, Constants.KO);
			map.put(Constants.MSG, spider.errMsg());
			return;
		}
		try {
			spider.doParse(url,map);
		}catch(Exception e) {
			 log.error("exception: "+e.getMessage()+ ",url="+url);
			 return;
		}
		
	
		//request请求中已解码，无需再解一次
//		try {
//			String decodedUrl = URLDecoder.decode(url,"UTF-8");
//			log.info("url="+url+",after decoded:"+decodedUrl);
//			spider.doParse(decodedUrl,map);
//		} catch (UnsupportedEncodingException e) {
//			map.put(Constants.RET, Constants.KO);
//			map.put(Constants.MSG, "请先用encodeURIComponent对link值参数进行编码");
//			return;
//		}
		
	}
}
