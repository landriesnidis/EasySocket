package pers.landriesnidis.easysocket.server.filter;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 
 * @author Landriesnidis
 * @version 2017��10��10��18:42:42
 */
public class KMPFilter {

	private final BufferedReader reader;
	private final byte[] arrStartTag,arrEndTag;
	private DataFilterListener listener;
	
	
	public interface DataFilterListener{
		/**
		 * ͨ������������ַ�
		 * @param str
		 */
		void onPassed(String str);
		/**
		 * �����񵽵��ַ�
		 * @param str
		 */
		void onCaptured(String str);
		/**
		 * ������ȫ����ݵĶ�ȡ
		 * @param str
		 */
		void onFinished();
	}

	public KMPFilter(BufferedReader bufferedReader,byte[] arrStartTag,byte[] arrEndTag) {
		this.reader = bufferedReader;
		this.arrStartTag = arrStartTag.clone();
		this.arrEndTag = arrEndTag.clone();
	}
	
	/**
	 * ���ü�����
	 * @param listener
	 */
	public void setListener(DataFilterListener listener) {
		this.listener = listener;
	}
	
	public void filtrate() throws IOException{
		
		char[] arrCharStartTag = new String(arrStartTag).toCharArray();
		char[] arrCharEndTag = new String(arrEndTag).toCharArray();
		
		int index = 0;
		String content = reader.readLine();
		
		try {
			while(content!=null){
				//��ѯʣ���ַ����Ƿ������ǣ��ҵ������ѭ��
				while((index = CharsKMP(content.toCharArray(), arrCharStartTag))==-1){
					//δ�ҵ���ʼ���
					//ͨ��ص��ӿڷ�����ͨ��Ϣ
					listener.onPassed(content);
					//�ٴΰ��ж�ȡ�ַ�
					content = reader.readLine();
				}
				listener.onPassed(content.substring(0,index-1));
				content = new String(content.substring(index+arrCharStartTag.length-1));
				
				//��ѯʣ���ַ����Ƿ������ǣ��ҵ������ѭ��
				while((index = CharsKMP(content.toCharArray(), arrCharEndTag))==-1){
					//δ�ҵ�������
					//ͨ��ص��ӿڷ��ز��񵽵�����
					listener.onCaptured(content);
					//�ٴΰ��ж�ȡ�ַ�
					content = reader.readLine();
				}
				//ͨ��ص��ӿڷ��ؽ�����֮ǰ���ַ�
				listener.onCaptured(content.substring(0,index-1));
				content = new String(content.substring(index+arrCharEndTag.length-1));
			}
		} catch (NullPointerException e) {
			//����content = reader.readLine()��Ŀ�ָ���쳣
			if(content!=null){
				e.printStackTrace();
			}
		}
		
		listener.onFinished();
		return;
	}
	
	/**
	 * ʹ��KMP�㷨���ұ����Դ�����е�λ��
	 * @param source
	 * @param tag
	 * @return
	 */
	private int CharsKMP(char[] source,char[] tag){
		int i=0,j=0;
		while(i<=source.length-tag.length+1 && j<tag.length){
			if(source[i]==tag[j]){
				i++;j++;
			}else{
				i=i-j+1;
				j=0;
			}
		}
		if(i<=source.length && j==tag.length){
			return i-j+1;
		}else{
			return -1;
		}
	}
	
}


