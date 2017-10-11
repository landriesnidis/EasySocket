package pers.landriesnidis.easysocket_example.server;

import java.net.BindException;
import java.net.Socket;

import pers.landriesnidis.easysocket.server.BaseListener;
import pers.landriesnidis.easysocket.server.BaseServerSocketThread;
import pers.landriesnidis.easysocket.server.manager.ChatroomSocketManager;


public class Example_01_CreateServerListener {
	
	public static void main(String[] args) {
		MyListener listener;
		try {
			listener = new MyListener(12345);
			listener.start();
		} catch (BindException e) {
			e.printStackTrace();
		}
		
	}
}

class MyListener extends BaseListener<BaseServerSocketThread>{

	public MyListener(int port) throws BindException {
		super(port);
	}
	
	@Override
	protected void onStart() {
		System.out.println("服务器已启动,监听端口号为:" + getLocalPort());
	}
	
	@Override
	protected void onConnection(Socket socket) {
		
	}

	@Override
	protected BaseServerSocketThread onHandOver(Socket socket) {
		BaseServerSocketThread bsst = new BaseServerSocketThread(socket) {
			
			@Override
			public void onConnected() {
				getSocketManager().addSocketThread(this);
				System.out.println("新终端已接入服务器：" + getRemoteSocketAddress());
			}
			
			@Override
			public void onClosed() {
				getSocketManager().delSocketThread(this);
				System.out.println("终端与服务器失去连接:" + getRemoteSocketAddress());
			}
			
			@Override
			public void onReceiveData(String strline) {
				System.out.println(getSocket().getRemoteSocketAddress() + "\t" + strline);
				for(BaseServerSocketThread bs:getSocketManager().getSocketThreads()){
					if(!bs.equals(this)){
						System.out.println("消息转发至[" + bs.getSocket().getRemoteSocketAddress() +"]");
						bs.sendString(strline);
					}
				}
			}
		};
		return bsst;
	}
}

class My2Listener extends BaseListener<BaseServerSocketThread>{
	ChatroomSocketManager<BaseServerSocketThread> manager = new ChatroomSocketManager<BaseServerSocketThread>();
	public My2Listener(int port) throws BindException {
		super(port);
		setSocketManager(manager);
	}
	
	@Override
	protected void onStart() {
		System.out.println("服务器已启动,监听端口号为:" + getLocalPort());
	}
	
	@Override
	protected void onConnection(Socket socket) {
	}

	@Override
	protected BaseServerSocketThread onHandOver(Socket socket) {
		BaseServerSocketThread bsst = manager.createSocketThread(socket);
		return bsst;
	}
}
