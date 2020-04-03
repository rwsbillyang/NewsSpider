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
	 * 该数组存放待抽取的各种值，
	 * 数组中第一个值为key，即保存到map中对应的key，
	 * 第二个是匹配方式，分别为前缀匹配、包含匹配、后缀匹配，以及多行匹配；
	 * 第3个元素是待匹配的特征字符串，
	 * 第4和第5个则是提取值时，值前面和后面的特征字符串；若是多行模式，第6个元素则为多行结束特征符匹配方式
	 * 注意：最好按照待解析内容中各字段值出现的顺序排列数组元素
	 * 
	 * 比如：
	 * 下面的user提起规则为：某单行通过包含（PATTERN_CONTAIN）找到一行，即包含了"profile_nickname"的行，提取的值位于第四个值">"和第五个值"<"之间
	 * 
	 * 下面的title提起规则为：某单行通过startsWith的方式（PATTERN_PREFIX）即以"var msg_title"开头的行，提取位于"和"之间的内容
	 * 
	 * 下面的content的提取规则为：第二个参数PATTERN_MULTIPlE_LINES_SUFFIX与第三个参数"id=\"js_content\">" 后缀匹配方式作为开始行, 
	 * 以第四个参数"</div>"和第五个参数PATTERN_MULTIPlE_LINES_EQUAL作为结束行，中间的内容为待提取的内容
	 * 开始行和结束行支持前缀匹配、后缀匹配、包含、完全相等。 TODO：支持正则表达式方式
	 * 
	 * 最后两个参数值表示多行模式中排除掉此两种标签的内容，若没有排除的，则为null
	 * 
	 * <code>
	 * 		String[][] infoArray = {
	 * 			{"user",PATTERN_CONTAIN,"profile_nickname",">","<"},
	 * 			{"content",PATTERN_MULTIPlE_LINES_SUFFIX,"id=\"js_content\">",null,"</div>",PATTERN_MULTIPlE_LINES_EQUAL, "<section class=\"cps_inner cps_inner_list js_list_container js_product_container\">","</section>"},
	 * 			//{"date",PATTERN_PREFIX,"var publish_time","\"","\""},
	 * 			{"title",PATTERN_PREFIX,"var msg_title","\"","\""},
	 * 			{"brief",PATTERN_PREFIX,"var msg_desc","\"","\""},
	 * 			{"imgUrl",PATTERN_PREFIX,"var msg_cdn_url","\"","\""},
	 * 			{"link",PATTERN_PREFIX,"var msg_link","\"","\""},
	 * 		};
	 * </code>
	 * */
	@Override
	public String[][] getInfoArray(){
		String[][] infoArray = {
				{"user",PATTERN_CONTAIN,"profile_nickname",">","<"},
				{"content",PATTERN_MULTIPlE_LINES_CONTAIN,"id=\"js_content\"",null,"</div>",PATTERN_MULTIPlE_LINES_EQUAL, "<section class=\"cps_inner cps_inner_list js_list_container js_product_container\">","</section>"},
				{"ogurl",PATTERN_PREFIX,"<meta property=\"og:url\"","content=\"","\""},
				//{"title",PATTERN_PREFIX,"var msg_title","\"","\""},
				{"title",PATTERN_PREFIX,"<meta property=\"og:title\"","content=\"","\""},
				{"brief",PATTERN_PREFIX,"var msg_desc","\"","\""},
				{"imgUrl",PATTERN_PREFIX,"var msg_cdn_url","\"","\""},
				{"link",PATTERN_PREFIX,"var msg_link","\"","\""},
				};
		return infoArray;
	}
	
	public void doParse(String url, Map<String, String> map) {
		getPageAndParse(url,map);
	}

	public static void main(String[] args) {
		
	}
}