package com.github.rwsbillyang.spider;

import java.util.Map;


//音频源处理：https://www.zhihuichengshi.cn/post_wecaht_to_news.php

public class NewsSpiderWechat extends NewsSpiderStreamHandle implements NewsSpiderInterface {
	//private static final Logger log = LoggerFactory.getLogger(NewsSpiderWechat.class);

	public String regPattern() {
		return "http(s)?://mp\\.weixin\\.qq\\.com/\\S+";
	}


	public String errMsg() {
		return "非mp.weixin.qq.com开头的文章链接无法上传";
	}
	
	/**
	 * 该数组存放待抽取的各种值，数组中第一个值为key，即保存到map中对应的key，第二个是匹配方式，分别为前缀匹配、包含匹配、后缀匹配，以及多行匹配；
	 * 第3个元素是待匹配的特征字符串，第4和第5个则是提取值时，值前面和后面的特征字符串；若是多行模式，第6个元素则为多行结束特征符匹配方式
	 * 注意：最好按照待解析内容中各字段值出现的顺序排列数组元素
	 * */
	@Override
	public String[][] getInfoArray(){
		String[][] infoArray = {
				
				{"user",PATTERN_CONTAIN,"profile_nickname",">","<"},
			
				//文中有广告，有投票的情景: https://mp.weixin.qq.com/s/FBC-LVBOphzKYdXrIQzbvg
				//最后两项（索引值为INDEX_MULTILINE_EXCLUSEIVE_START_KEY=6和INDEX_MULTILINE_EXCLUSEIVE_END_KEY=7）用于排除掉此两种标签页（开始和结束，包含此标签）内容,
				//当前采用严格匹配的包含模式进行判断。TODO：改为正则表达式方式
				//无须排除中间内容的，则将设置为null
				{"content",PATTERN_MULTIPlE_LINES_SUFFIX,"id=\"js_content\">",null,"</div>",PATTERN_MULTIPlE_LINES_EQUAL, "<section class=\"cps_inner cps_inner_list js_list_container js_product_container\">","</section>"},
				//{"date",PATTERN_PREFIX,"var publish_time","\"","\""},
				{"title",PATTERN_PREFIX,"var msg_title","\"","\""},
				{"brief",PATTERN_PREFIX,"var msg_desc","\"","\""},
				{"imgUrl",PATTERN_PREFIX,"var msg_cdn_url","\"","\""},
				{"link",PATTERN_PREFIX,"var msg_link","\"","\""},
				
				};
		return infoArray;
	}
	
	public void doParse(String url, Map<String, String> map) {
		getPageAndParse(url,map);
	}

}