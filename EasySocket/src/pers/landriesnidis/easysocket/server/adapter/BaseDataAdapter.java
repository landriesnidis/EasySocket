package pers.landriesnidis.easysocket.server.adapter;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class BaseDataAdapter {
	
	// 匹配状态
    public static enum MATCH_STATE {  
    	MATCH_FAILURE, 		//匹配失败
    	MATCH_CONTINUE, 	//匹配持续
    	MATCH_SUCCEED		//匹配成功
      }  
	
    // 匹配模式
    public static enum CHECK_MODE {  
    	CHECKMODE_START,	//匹配起始符模式
    	CHECKMODE_END		//匹配结束符模式
    }
	
	private char[] startFlag	= "<##START##>".toCharArray();
	private char[] endFlag		= "<###END###>".toCharArray();
	private int index_flag;
	
	/**
	 * 根据传入的字节匹配是否标识符
	 * 
	 * @param c
	 * @return
	 */
	public MATCH_STATE checkFlag(char c, CHECK_MODE cm){
		//要匹配的标识符
		char[] flag = (cm==CHECK_MODE.CHECKMODE_START)?startFlag:endFlag;
		//匹配状态
		MATCH_STATE ms = MATCH_STATE.MATCH_FAILURE;
		//如果新传入的字符与该匹配的位置内容一致
		if(flag[index_flag] == c){
			//匹配状态为：匹配继续
			ms = MATCH_STATE.MATCH_CONTINUE;
			//匹配位后移
			index_flag++;
			//如果已匹配的位数与标识符位数一致
			if(index_flag == flag.length){
				//匹配状态为：匹配成功
				ms = MATCH_STATE.MATCH_SUCCEED;
				index_flag=0;
			}
		}else{
			index_flag=0;
		}
		return ms;
	}
	
	/**
	 * 托管BufferedReader，当遇到结束符时调用complete()，收到的数据传入execute()
	 * 阻塞方法：只有当收到结束标识符时才会结束
	 * @param br
	 * @throws IOException 
	 */
	public void trusteeship(BufferedReader br) throws IOException{
		char c;
		MATCH_STATE ms = MATCH_STATE.MATCH_FAILURE;
		int continueCount = 0;
		
		while(ms != MATCH_STATE.MATCH_SUCCEED){
			c = (char)br.read();
			ms = checkFlag(c,CHECK_MODE.CHECKMODE_END);
			switch(ms){
			case MATCH_CONTINUE:
				if(continueCount<=endFlag.length){
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
				if(checkFlag(c,CHECK_MODE.CHECKMODE_END) != MATCH_STATE.MATCH_CONTINUE){
					execute(new char[]{c});
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
	
	/**
	 * 获取起始标识符
	 * @return
	 */
	public char[] getStartFlag() {
		return startFlag;
	}
	
	/**
	 * 获取结束标识符
	 * @return
	 */
	public char[] getEndFlag() {
		return endFlag;
	}
}
