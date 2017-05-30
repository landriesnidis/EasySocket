package pers.landriesnidis.easysocket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public abstract class BaseServerSocketThread extends Thread {

	private final Socket socket;
	private boolean isRun = true;
	private String encode = "UTF-8";
	
	public BaseServerSocketThread(Socket socket) {
		this.socket = socket;
	}

	/**
	 * 结束线程
	 */
	public void close(){
		isRun = false;
		onClose();
		interrupt();	//结束线程
	}
	
	/**
	 * 接收Socket客户端发来的数据
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
	public abstract void onClose();
	
	/**
	 * 发送数据
	 * @param messgae
	 */
	public void sendMessage(String messgae) {
		try {
			// 返回此套接字的输出流
			getSocket().getOutputStream()
					.write((messgae + "\n").getBytes(encode));
		} catch (UnsupportedEncodingException e) {
			System.err.println("字符编码设置有误!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	@Override
	public void run() {
		
		onConnected();
		BufferedReader br = null;
		try {
			// 从字符输入流读取文本,创建一个InputStreamReader使用指定的字符集
			 br = new BufferedReader(
								new InputStreamReader(
										getSocket().getInputStream(),
										encode)); 
			// 返回此套接字的输入流
			String line = null;
			while (((line = br.readLine()) != null) && isRun) {
//				System.out.println(socket.getRemoteSocketAddress() + " : " + line);
				onReceiveData(line);
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("字符编码设置有误!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			close();
		}
	}

	public Socket getSocket() {
		return socket;
	}
}