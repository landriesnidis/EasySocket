package pers.landriesnidis.easysocket.server.manager;


import java.net.Socket;

import pers.landriesnidis.easysocket.server.BaseServerSocketThread;

/**
 * 通用聊天室Socket通信管理器
 * @author Landriesnidis
 *
 * @param <S>
 */
public class ChatroomSocketManager<S extends BaseServerSocketThread> extends BasicSocketManager<S>{
	
	/**
	 * 向聊天室内所有用户广播消息
	 * @param messgae
	 */
	public void Broadcast(String messgae){
		for(S s:vecSocketThreads){
			try{
				((BaseServerSocketThread)s).sendString(messgae);
			}catch (Exception e) {
				delSocketThread(s);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public S createSocketThread(Socket socket){
		
		S st = (S) new BaseServerSocketThread(socket) {
			
			@Override
			public void onReceiveData(String strline) {
				Broadcast(strline);
			}
			
			@Override
			public void onConnected() {
				addSocketThread((S) this);
			}
			
			@Override
			public void onClosed() {
				delSocketThread((S) this);
			}

			@Override
			public void onReceiveData(byte[] arr, int length) {
				// TODO Auto-generated method stub
				
			}
		};
		return st;
	}
}
