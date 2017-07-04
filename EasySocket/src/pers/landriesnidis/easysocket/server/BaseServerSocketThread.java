package pers.landriesnidis.easysocket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.CharBuffer;

import pers.landriesnidis.easysocket.server.adapter.BaseDataAdapter;
import pers.landriesnidis.easysocket.server.adapter.BaseDataAdapter.Check_Mode;
import pers.landriesnidis.easysocket.server.adapter.BaseDataAdapter.Match_State;

public abstract class BaseServerSocketThread extends Thread {

	private final Socket socket;
	private boolean isRun = true;
	private String encode = "UTF-8";
	
	/**
	 * BaseServerSocketThread是抽象类，如果要直接构造，需要立即实现抽象方法
	 * 
	 * @param socket
	 *            用于通信的socket对象
	 */
	public BaseServerSocketThread(Socket socket) {
		this.socket = socket;
		try {
			socket.setKeepAlive(true); 	// 启用心跳检测
			socket.setTcpNoDelay(true); // 禁用nagle算法
		} catch (SocketException e) {

		}
	}

	/**
	 * 结束线程
	 */
	public void close() {
		isRun = false;
		try {
			if (!socket.isClosed()||socket.isConnected())
				socket.close();
		} catch (IOException e) {
		}
		interrupt(); // 结束线程
		onClosed();	
	}

	/**
	 * 接收Socket客户端发来的数据
	 * 
	 * @param strline
	 */
	public abstract void onReceiveData(String strline);

	/**
	 * 成功建立连接
	 */
	public abstract void onConnected();

	/**
	 * 断开连接
	 */
	public abstract void onClosed();

	/**
	 * 发送字符串
	 * 
	 * @param messgae
	 */
	public void sendString(String messgae) {
		try {
			// 返回此套接字的输出流
			getSocket().getOutputStream().write(
					(messgae + "\n").getBytes(encode));
		} catch (UnsupportedEncodingException e) {
			System.err.println("字符编码设置有误!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	public void send(byte[] bytes) {
		try {
			getSocket().getOutputStream().write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	BaseDataAdapter adapter = new BaseDataAdapter() {
		StringBuffer sb = new StringBuffer();
		@Override
		public void execute(char[] ac) {
			sb.append(ac);
		}
		
		@Override
		public void complete(BufferedReader br) {
			System.out.println("数据适配器捕获到的数据:" + sb.toString());
			sb.delete(0,sb.length());
		}
	};
	
	

	@Override
	public void run() {

		onConnected();
		BufferedReader br = null;
		// 从字符输入流读取文本,创建一个InputStreamReader使用指定的字符集
		try {
			br = new BufferedReader(new InputStreamReader(getSocket()
					.getInputStream(), encode));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			close();
			return;
		}
		
		
		try {
			
			
			// 返回此套接字的输入流
			
			
			//按行读取
//			String line = "";
//			while (((line = br.readLine()) != null) && isRun) {
//				onReceiveData(line);
//			}
			
			//按字读取行
//			StringBuffer sb = new StringBuffer();
//			char c;
//			while(isRun){
//				c = (char)br.read();
//				if(c=='\n'){
//					onReceiveData(sb.toString());
//					sb.delete(0,sb.length());
//				}else{
//					sb.append(c);
//				}
//			}
			
			//按字读取行（加数据适配器）
			StringBuffer sb = new StringBuffer();
			char c;
			while(isRun){
				c = (char)br.read();
				Match_State ms = adapter.checkFlag(c, Check_Mode.CHECKMODE_START);
				if(ms == Match_State.MATCH_SUCCEED){
					adapter.trusteeship(br);
					sb.delete(sb.length()+1-adapter.getStartFlag().length,sb.length());
					continue;
				}
				if(c==(char)10){
					onReceiveData(sb.toString());
					sb.delete(0,sb.length());
				}else{
					sb.append(c);
				}
			}
		} catch (IOException e) {
			if(isRun){
				//连接意外中断导致关闭
			}else{
				//主动关闭
			}
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
			close();
		}
	}

	/**
	 * 获取Socket对象
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * 设置字符编码
	 * 
	 * @param encode
	 */
	public void setEncode(String encode) {
		this.encode = encode;
	}
}