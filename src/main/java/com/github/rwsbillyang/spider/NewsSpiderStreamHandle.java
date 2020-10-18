package com.github.rwsbillyang.spider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对请求获取的网页字符串流进行处理，逐行检查获取所需的值
 * */
public abstract class NewsSpiderStreamHandle {
	private static final Logger log = LoggerFactory.getLogger(NewsSpiderStreamHandle.class);
	

	public final String PATTERN_PREFIX="P";
	public final String PATTERN_CONTAIN="C";
	public final String PATTERN_SUFFIX="S";

	public final String PATTERN_MULTIPlE_LINES_PREFIX="MP";
	public final String PATTERN_MULTIPlE_LINES_CONTAIN="MC";
	public final String PATTERN_MULTIPlE_LINES_SUFFIX="MS";
	public final String PATTERN_MULTIPlE_LINES_EQUAL="ME";
	
	
	public final static int INDEX_KEY=0;
	public final static int INDEX_PATTERN=1;
	public final static int INDEX_PATTERN_KEY=2;
	public final static int INDEX_START_KEY=3;
	public final static int INDEX_END_KEY=4;
	public final static int INDEX_MULTILINE_END_KEY_PATTERN=5;//多行模式适用
	public final static int INDEX_MULTILINE_EXCLUSEIVE_START_KEY=6;//多行模式适用
	public final static int INDEX_MULTILINE_EXCLUSEIVE_END_KEY=7;//多行模式适用
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
	public abstract String[][] getInfoArray();

	
	public final static int PARSE_SINGLELINE_KO=0;
	public final static int PARSE_SINGLELINE_OK=1;
	public final static int PARSE_MULTILINE=2;
	/**
	 * 抽取行中的值，结果放入map中，只对单行模式使用，当需要抽取的值处在多行时，需另行处理。
	 * 
	 * @param line待抽取的行
	 * @param index 针对getInfoArray返回数组的索引
	 * @param map 存放抽取结果
	 * @return 不是匹配的行，返回0，是匹配的单行，抽取后返回1，多行模式返回2
	 * */
	public int extractValueFromLine(String line,int index, Map<String, String> map)
	{
		boolean isTargetLine=false;
		String[] array = getInfoArray()[index];
		switch(array[INDEX_PATTERN])
		{
		case PATTERN_PREFIX:
			isTargetLine = line.startsWith(array[INDEX_PATTERN_KEY]);
			break;
		case PATTERN_CONTAIN:
			isTargetLine = line.contains(array[INDEX_PATTERN_KEY]);
			break;
		case PATTERN_SUFFIX:
			isTargetLine = line.endsWith(array[INDEX_PATTERN_KEY]);
			break;
		case PATTERN_MULTIPlE_LINES_PREFIX:
			if(line.startsWith(array[INDEX_PATTERN_KEY])) return PARSE_MULTILINE;
			else return PARSE_SINGLELINE_KO;
		case PATTERN_MULTIPlE_LINES_CONTAIN:
			if(line.contains(array[INDEX_PATTERN_KEY])) return PARSE_MULTILINE;
			else return PARSE_SINGLELINE_KO;
		case PATTERN_MULTIPlE_LINES_SUFFIX:
			if(line.endsWith(array[INDEX_PATTERN_KEY])) return PARSE_MULTILINE;
			else return PARSE_SINGLELINE_KO;
		case PATTERN_MULTIPlE_LINES_EQUAL:
			if(line.equalsIgnoreCase(array[INDEX_PATTERN_KEY])) return PARSE_MULTILINE;
			else return PARSE_SINGLELINE_KO;	
		default:
			log.warn("Not supported pattern="+ array[INDEX_PATTERN]);
		}
		
		if(isTargetLine)
		{
			int start=line.indexOf(array[INDEX_START_KEY])+array[INDEX_START_KEY].length();
			int end=line.indexOf(array[INDEX_END_KEY], start);
			if(start<end)
			{
				String value=line.substring(start, end);
				map.put(array[INDEX_KEY], value);
				//log.info("get value for key="+array[INDEX_KEY]+",value="+value);
			}else
			{
				log.warn("Not found value for key="+array[INDEX_KEY]+",line="+line);
			}
		}
		//log.info("isTargetLine="+isTargetLine+", array[INDEX_CHARACTERS])="+array[INDEX_CHARACTERS]+",line="+line);
		return isTargetLine?PARSE_SINGLELINE_OK:PARSE_SINGLELINE_KO;
	}
	
	protected InputStream getPage(String url)
	{
		log.info("parse url="+url);
		
		 try{
		        //建立请求链接
		        URLConnection conn = new URL(url).openConnection();
		        conn.setDoInput(true);
		        conn.setDoOutput(true);
		        
		        conn.setConnectTimeout(10000);
		        conn.setDefaultUseCaches(true);
		        conn.setRequestProperty("User-agent",NewsSpider.UAs[0]);
		        conn.setRequestProperty("Charset", "UTF-8"); 
		        conn.connect();//本方法不会自动重连 
		        
		        InputStream is = conn.getInputStream();  
		        if(is==null)
		        {
		   		 	return null;
		        }
		     
//		        BufferedReader in=new BufferedReader(new InputStreamReader(is,"UTF-8"));
//		        StringBuilder sb = new StringBuilder();
//		        String line=null;
//		        while ((line = in.readLine()) != null) 
//		        {
//		        		if((line=line.trim())==null)
//		        			continue;
//		        		else
//		        			sb.append(line);
//		        }
//		        
//				is.close();//关闭InputStream
		        
		      //  conn.disconnect();
		      return is;
		    }catch(java.io.FileNotFoundException e) {
		    	log.warn("FileNotFoundException, url="+url);
		    }catch(Exception e){
		        e.printStackTrace();
		        
		    }
		 return null;
	}
	
	
	protected void parseStream(String url,InputStream is, Map<String, String> map)
	{
		if(is==null)
		{
			map.put(SpiderConstants.RET, SpiderConstants.KO);
   		 	map.put(SpiderConstants.MSG, "获取内容失败");
   		 	return;
		}
		 try{		 
			 	BufferedReader in=new BufferedReader(new InputStreamReader(is,"UTF-8"));
		        
			 	String line=null;
		        int count=0;
		        int ret=0;
		        String[] array = null;
		        String[][] infoArray=getInfoArray();
		        boolean inMultiLineMode=false;
		        boolean inMultilineExclusiveMode=false;
		        StringBuilder sb = new StringBuilder();
		        int[] flagArray = new int[infoArray.length];//finding result flag results array, initialized 0
		        
		        // 循环读取流	
		        LoopWhile: while ((line = in.readLine()) != null) 
		        {
		        		
		        		if((line=line.trim())==null)
		        			continue;
		            
		        		//	log.info("after trim,line="+line);
		            	if(!inMultiLineMode)
		            	{
		            		LoopFor: for(int i=0;i<infoArray.length;i++)
			            	{
		            			//has found,skip
		            			if(flagArray[i]==1)
		            				continue LoopFor;
		            			
			            		ret=extractValueFromLine(line,i,map);
			            		if(ret==PARSE_SINGLELINE_KO)
			            		{
			            			continue LoopFor;
			            		}else if(ret==PARSE_SINGLELINE_OK)
			            		{
			            			flagArray[i]=1;
			            			count++;
			            			continue LoopWhile;
			            		}else if(ret==PARSE_MULTILINE)
			            		{
			            			inMultiLineMode=true;
			            			array = infoArray[i];
			            			flagArray[i]=1;
			            			log.info("enter multiline mode...");
				            		continue LoopWhile;
			            		}else
			            		{
			            			log.warn("NOT support result="+ret);
			            			continue LoopFor;
			            		}
			            	}
		            	 	
		            	}else
		            	{
		            		
		            		if(inMultilineExclusiveMode)
	            			{
	            				if(isMultiLineExlusiveEnd(line,array[INDEX_MULTILINE_EXCLUSEIVE_END_KEY]))
	            				{
	            					inMultilineExclusiveMode=false;
			            			//必须是完整的元素标签，并将其替换掉。TODO：以后考虑将其改为正则表达式方式
			            			line=line.replace(array[INDEX_MULTILINE_EXCLUSEIVE_END_KEY], "").trim();
		            				if(!StringUtil.isBlank(line))
		            					sb.append(line);
			            			
		            				continue;
	            				}else
	            					continue;
	            			}else
	            			{
	            				if(isMultiLineEnd(line,array[INDEX_END_KEY],array[INDEX_MULTILINE_END_KEY_PATTERN]))
			            		{
			            			inMultiLineMode=false;
			            			log.info("......exit multiline mode");
			            			
			            			map.put(array[INDEX_KEY], sb.toString());
			            			count++;
				            		continue;
			            		}
	            				if(isMultiLineExlusiveStart(line,array[INDEX_MULTILINE_EXCLUSEIVE_START_KEY]))
			            		{
		            				inMultilineExclusiveMode=true;
		            				//必须是完整的元素标签，并将其替换掉。TODO：以后考虑将其改为正则表达式方式
		            				line=line.replace(array[INDEX_MULTILINE_EXCLUSEIVE_START_KEY], "").trim();
		            				if(!StringUtil.isBlank(line))
		            					sb.append(line);
		            				
		            				continue;
			            		}else
			            		{
			            			sb.append(line);
			            			continue;
			            		}
	            			}
		            		
		            		
		            		
		            	}
	            }//while-loop
		        // still inMultiLineMode
	            if(inMultiLineMode)
	            {
	            		log.warn("still inMultiLineMode when in whileloop end");
	            		map.put(array[INDEX_KEY], sb.toString());
         			count++;
	            }
	            
				if(count==infoArray.length)
				{
					map.put(SpiderConstants.RET, SpiderConstants.OK);
					map.put(SpiderConstants.MSG, "恭喜，解析成功，请编辑保存！");
				}else
				{
					log.warn("got results number:"+count+",less than "+infoArray.length+",url="+url);
					map.put(SpiderConstants.RET, SpiderConstants.OK);
					map.put(SpiderConstants.MSG, "得到部分解析结果！请耐心等候系统升级！");
				}
				
				in.close();
				is.close();//关闭InputStream
		        
		      //  conn.disconnect();
		      return;
		    }catch(Exception e){
		        e.printStackTrace();
		        log.error("caused Exception, the url="+url);
		        map.put(SpiderConstants.RET, SpiderConstants.KO);
				map.put(SpiderConstants.MSG, "获取内容时网络超时，请重试");
				
				return;
		    }
	}
	//TODO:使用contains模式，以后正则表达式方式
	private boolean isMultiLineExlusiveEnd(String line, String patternKey) {
		if(patternKey==null) return false;
		return line.contains(patternKey);
	}
	//TODO:使用contains模式，以后正则表达式方式
	private boolean isMultiLineExlusiveStart(String line, String patternKey) {
		if(patternKey==null) return false;
		return line.contains(patternKey);
	}

	public void getPageAndParse(String url, Map<String, String> map) {
		parseStream(url,getPage(url),map);
	}
	
	public boolean isMultiLineEnd(String line,String end,String pattern)
	{
		boolean isTargetLine=false;
		switch(pattern)
		{
		case PATTERN_MULTIPlE_LINES_PREFIX:
			isTargetLine=line.startsWith(end);
			break;
		case PATTERN_MULTIPlE_LINES_CONTAIN:
			isTargetLine=line.contains(end);
			break;
		case PATTERN_MULTIPlE_LINES_SUFFIX:
			isTargetLine=line.endsWith(end);
			break;
		case PATTERN_MULTIPlE_LINES_EQUAL:
			isTargetLine=line.equalsIgnoreCase(end);
			break;
		default:
			log.warn("It's not multiline pattern="+pattern);
			break;
		}
		return isTargetLine;
	}
	

}
