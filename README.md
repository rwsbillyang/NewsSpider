# NewsSpider
news spider to extract some news content
## compile & install
```
mvn package install
```

## add dependency
in your pom.xml
```
<dependency>
    <groupId>com.github.rwsbillyang</groupId>
    <artifactId>spider</artifactId>
    <version>1.0.0</version>
</dependency>
```

or build.gradle:
```
 compile 'com.github.rwsbillyang:spider:1.0.0'
```

eg:
```java
try {
			link = URLDecoder.decode(link, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			//e1.printStackTrace();
			log.error("UnsupportedEncodingException:{},link={}",e1.getMessage(),link);
			return new ResponseBox("UnsupportedEncodingException, please check the encode of link");
		}
		newsSpider.parse(link, map);
		
		if(map.get(Constants.RET).equals(Constants.OK))
		{
			if(StringUtils.isAnyBlank(map.get("title"),map.get("content")))
				return new ResponseBox(ResponseBox.PROMPT,"获取正文失败，请重试！也请确保在文章详情页面中进行收藏");
			
			News news = new News();
			news.setTitle(map.get("title"));
			news.setImage1(map.get("imgUrl"));
			news.setBrief(map.get("brief"));
			news.setDetail(map.get("content"));
			news.setSource(map.get("user"));
			
			String sourceUrl = map.get("link");
			if(sourceUrl==null) sourceUrl = link;
				return new ResponseBox(news);
			
		}else
		{
			return new ResponseBox(ResponseBox.PROMPT,Constants.MSG);
		}
```
