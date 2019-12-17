package com.github.rwsbillyang.spider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.helper.StringUtil;


/**
 * 获取img标签中src属性值得工具类
 * 
 * 参考: http://blog.csdn.net/yuan8080/article/details/6899211
 * */
public class HtmlImgSrcUtil {
	private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签

	private final static String regxpForImgTag = "<\\s*img\\s+([^>]*)\\s*>"; // 找出IMG标签

	private final static String regxpForImaTagSrcAttrib = "src=\"([^\"]+)\""; // 找出IMG标签的SRC属性

	//String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";   红色的 tag 是动态的变量（指定标签）
	
	
	public static final Pattern  PATTERN = Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)",
			Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);  
	
	
	/**
	 * 获取新闻内容中的第一张图片，即第一个img标签的src属性的值
	 * @param htmlContent 通常为新闻内容，如百度编辑器编辑后保存的新闻详情字段，将从中提取出第一个img标签
	 * @return 返回第一张图片的路径，即img标签中的src属性值。如果没有图片，返回null
	 * */
	public static List<String> getImageSrc(String htmlContent)
	{
		if(StringUtil.isBlank(htmlContent))return null;
		 Matcher matcher = PATTERN.matcher(htmlContent); 
		 ArrayList<String> list =  new ArrayList<String>();  
		 while(matcher.find())   
		 {			 
			 String group = matcher.group(1);   
	         if(group == null)   {   
	                continue;   
	         }   
	            //   这里可能还需要更复杂的判断,用以处理src="...."内的一些转义符   
	          if (group.startsWith("'")){   
	                list.add(group.substring(1,group.indexOf("'",   1)));   
	          }else if(group.startsWith("\"")) {   
	                list.add(group.substring(1,   group.indexOf("\"",   1)));   
	          }else{   
	                list.add(group.split("\\s")[0]);  
	         } 	        
	
		 }
		 return list;
		 
	}
	
	
	public static List<String> getImageSrc2(String htmlCode) {
	    List<String> imageSrcList = new ArrayList<String>();
	    Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(htmlCode);
	    String quote = null;
	    String src = null;
	    while (m.find()) {
	        quote = m.group(1);
	 
	        // src=https://sms.reyo.cn:443/temp/screenshot/zY9Ur-KcyY6-2fVB1-1FSH4.png
	        src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2);
	        imageSrcList.add(src);
	 
	    }
	    return imageSrcList;
	}
}
