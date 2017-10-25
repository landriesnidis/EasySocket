package pers.landriesnidis.easysocket.server.filter;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class BaseFilter {

	protected BufferedReader reader;
	
	public BaseFilter(BufferedReader reader) {
		this.reader = reader;
	}
	
	public BufferedReader getReader() {
		return reader;
	}
	
	//数据过滤
	public abstract void filtrate(BufferedReader br,String content) throws IOException;

	//查询是否包含标记符
	public abstract boolean checkTag(String content);
}
