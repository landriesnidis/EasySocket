package pers.landriesnidis.easysocket.server.adapter;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class BaseDataAdapter {
	
	// 匹配状态
    public static enum Match_State {  
    	MATCH_FAILURE, 		//匹配失败
    	MATCH_CONTINUE, 	//匹配持续
    	MATCH_SUCCEED		//匹配成功
      }  
	
    // 匹配模式
    public static enum Check_Mode {  
    	CHECKMODE_START,	//匹配起始符模式
    	CHECKMODE_END		//匹配结束符模式
    }
	
	private char[] startFlag	= "<##START##>".toCharArray();
	private char[] endFlag		= "<###END###>".toCharArray();
	private int index_flag;
	
	/**
	 * 匹配标识符
	 * 注：这里的算法很简陋，但是在实际情况下，只要标识符不是太过简短，基本不会出现ABABC中匹配ABC会出现的错误
	 * @param c
	 * @return
	 */
	public Match_State checkFlag(char c, Check_Mode cm){
		//要匹配的标识符
		char[] flag = (cm==Check_Mode.CHECKMODE_START)?startFlag:endFlag;
		//匹配状态
		Match_State ms = Match_State.MATCH_FAILURE;
		//开始匹配
		if(flag[index_flag] == c){
			ms = Match_State.MATCH_CONTINUE;
			index_flag++;
			if(index_flag == flag.length){
				ms = Match_State.MATCH_SUCCEED;
				index_flag=0;
			}
		}else{
			index_flag=0;
		}
		return ms;
	}
	
	/**
	 * 托管BufferedReader，当遇到结束符时调用complete()，收到的数据传入execute()
	 * @param br
	 * @throws IOException 
	 */
	public void trusteeship(BufferedReader br) throws IOException{
		char c;
		Match_State ms = Match_State.MATCH_FAILURE;
		char[] temp = new char[endFlag.length+1];		//预留一个空位
		int index = 0;
		int continueCount = 0;
		
		while(ms != Match_State.MATCH_SUCCEED){
			c = (char)br.read();
			ms = checkFlag(c,Check_Mode.CHECKMODE_END);
			switch(ms){
			case MATCH_CONTINUE:
				if(continueCount<=endFlag.length){
//					temp[index++] = c;
					continueCount++;
				}else{
					execute(getCharArray(endFlag, continueCount));
					continueCount=0;
				}
				break;
			case MATCH_FAILURE:
				//匹配状态为MATCH_CONTINUE的计数个数如果大于0
				if(continueCount > 0){
					execute(getCharArray(endFlag, continueCount));
					continueCount=0;
				}
				//导致匹配失败的字符是否是
				if(checkFlag(c,Check_Mode.CHECKMODE_END) != Match_State.MATCH_CONTINUE){
					execute(new char[]{c});
					index = 0;
					continueCount=0;
				}else{
					continueCount++;
				}
				break;
			case MATCH_SUCCEED:
				complete(br);
				break;
			}
		}
	}
	
	/**
	 * 从数组开始截取一定长度的子数组
	 * @param src	数据源
	 * @param effectiveLength	有效长度
	 * @return
	 */
	private char[] getCharArray(char[] src,int effectiveLength){
		char[] ac = new char[effectiveLength];
		for(int i=0;i<effectiveLength;i++){
			ac[i] = src[i];
		}
		return ac;
	}
	
	/**
	 * 匹配成功后执行该程序
	 * @param br
	 */
	public abstract void execute(char[] ac);
	
	/**
	 * 适配器处理已完成
	 */
	public abstract void complete(BufferedReader br);
	
	/**
	 * 设置起始标识符
	 * @param startFlag
	 */
	public void setStartFlag(char[] startFlag) {
		this.startFlag = startFlag;
	}
	
	/**
	 * 设置结束标识符
	 * @param endFlag
	 */
	public void setEndFlag(char[] endFlag) {
		this.endFlag = endFlag;
	}
	
	public char[] getStartFlag() {
		return startFlag;
	}
	
	public char[] getEndFlag() {
		return endFlag;
	}
}
