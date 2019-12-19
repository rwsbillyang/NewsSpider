package com.github.rwsbillyang.spider;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;


/**
 *  收藏全网链接时用到，只检查其url是否合法，提起文章title，第一章图片，以及前100个字作为brief
 * */
public class NewsSpiderAny  extends NewsSpiderStreamHandle implements NewsSpiderInterface{

	public NewsSpiderAny() {
		super();
	}

	public String regPattern() {
		return "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	}

	public String errMsg() {
		return "请确认粘贴的是否是网页链接";
	}
	
	@Override
	public String[][] getInfoArray() {
		String[][] infoArray = {
				{"brief",PATTERN_PREFIX,"<meta name=\"description\"","content=\"","\">"},
				{"imgUrl",PATTERN_PREFIX,"<meta property=\"og:image\"","content=\"","\""},
				{"title",PATTERN_PREFIX,"<title",">","<"}
				};
		return infoArray;
	}
	

	public void doParse(String url, Map<String, String> map) {
		InputStream is= getPage(url);
		if(is==null)
		{
			map.put(Constants.RET, Constants.KO);
   		 	map.put(Constants.MSG, "获取内容失败");
   		 	return;
		}
		try {
			Document doc=Jsoup.parse(is, "UTF-8", url);
			String text = doc.select("head > title").text();
			if(!StringUtil.isBlank(text)) map.put("title", text); 
			
			text = doc.select("head > meta[name=description]").attr("content");
			if(!StringUtil.isBlank(text)) map.put("brief", text);
			
			text = doc.select("head > meta[property=og:image]").attr("content");
			if(!StringUtil.isBlank(text)) map.put("imgUrl", text);
			if(StringUtil.isBlank(text)|| !text.startsWith("http"))
			{
				//图片地址为空，从正文内容中寻找第一张
				List<String> list = HtmlImgSrcUtil.getImageSrc2( doc.select("body").html());
				if(list != null && list.size()>0)
				{
					if(!StringUtil.isBlank(text)) map.put("imgUrl", list.get(0));
				}
			}
			//map.put("body", doc.html());
			map.put(Constants.RET, Constants.OK);
			map.put(Constants.MSG, "experimental to parse brief and image");
		} catch (IOException e) {
			e.printStackTrace();
			map.put(Constants.RET, Constants.KO);
   		 	map.put(Constants.MSG, "获取内容失败:IOException");
   		 	return;
		}
	}
	
}
