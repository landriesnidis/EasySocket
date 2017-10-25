package pers.landriesnidis.easysocket.server.filter;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 
 * @author Landriesnidis
 * @version 2017年10月10日18:42:42
 */
public class StringFilter extends BaseFilter{

	private DataFilterListener listener;
	private char[] arrCharStartTag;
	private char[] arrCharEndTag;
	
	/**
	 * 数据过滤器接口
	 * @author Landriesnidis
	 */
	public interface DataFilterListener{
		/**
		 * 通过过滤器的正常字符串
		 * @param str
		 */
		void onPassed(String str);
		/**
		 * 被捕获到的字符串
		 * @param str
		 */
		void onCaptured(String str);
		/**
		 * 当结束全部数据的读取
		 * @param str
		 */
		void onFinished();
	}

	/**
	 * @param bufferedReader
	 * @param arrStartTag
	 * @param arrEndTag
	 */
	public StringFilter(BufferedReader bufferedReader,byte[] arrStartTag,byte[] arrEndTag) {
		super(bufferedReader);
		arrCharStartTag = new String(arrStartTag).toCharArray();
		arrCharEndTag = new String(arrEndTag).toCharArray();
	}
	
	/**
	 * 设置监听器
	 * @param listener
	 */
	public void setListener(DataFilterListener listener) {
		this.listener = listener;
	}
	
	public void filtrate(BufferedReader br) throws IOException{
		filtrate(br,reader.readLine());
	}
	
	public void filtrate(BufferedReader br,String content) throws IOException{
		int index = 0;
		try {
			while(content!=null){
				//查询剩余字符串中是否包含结束标记，找到后跳出循环
				while((index = KMP.kmp(content.toCharArray(), arrCharStartTag))==-1){
					//未找到开始标记
					//通过回调接口返回普通信息
					listener.onPassed(content);
					//再次按行读取字符串
					content = reader.readLine();
				}
				listener.onPassed(content.substring(0,index-1));
				content = new String(content.substring(index+arrCharStartTag.length-1));
				
				//查询剩余字符串中是否包含结束标记，找到后跳出循环
				while((index = KMP.kmp(content.toCharArray(), arrCharEndTag))==-1){
					//未找到结束标记
					//通过回调接口返回捕获到的内容
					listener.onCaptured(content);
					//再次按行读取字符串
					content = reader.readLine();
				}
				//通过回调接口返回结束标记之前的字符串
				listener.onCaptured(content.substring(0,index-1));
				content = new String(content.substring(index+arrCharEndTag.length-1));
			}
		} catch (NullPointerException e) {
			//忽略content = reader.readLine()引发的空指针异常
			if(content!=null){
				e.printStackTrace();
			}
		}
		listener.onFinished();
		return;
	}

	@Override
	public boolean checkTag(String content) {
		return content.indexOf(new String(arrCharStartTag))>=0;
		//return KMP.kmp(arrCharStartTag, new String(content).toCharArray())>=0;
	}
	
}


