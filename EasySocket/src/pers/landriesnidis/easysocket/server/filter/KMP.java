package pers.landriesnidis.easysocket.server.filter;

public class KMP {

	/**
	 * 使用KMP算法查找标记在源数组中的位置
	 * @param source
	 * @param tag
	 * @return
	 */
	public static int kmp(char[] source,char[] tag){
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
	
	public static int kmp(int[] source,int[] tag){
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
	
	public static int kmp(byte[] source,byte[] tag){
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
