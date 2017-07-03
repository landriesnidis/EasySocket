package pers.landriesnidis.easysocket_example.server;

import java.net.Socket;

import pers.landriesnidis.easysocket.server.BaseListener;
import pers.landriesnidis.easysocket.server.BaseServerSocketThread;
import pers.landriesnidis.easysocket.server.manager.ChatroomSocketManager;


public class Example_01_CreateServerListener {
	
	public static void main(String[] args) {
		My2Listener listener = new My2Listener(12345);
		listener.start();
	}
}

class MyListener extends BaseListener<BaseServerSocketThread>{

	public MyListener(int port) {
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
				System.out.println("新终端已接入服务�?" + this.getSocket().getRemoteSocketAddress());
			}
			
			@Override
			public void onClose() {
				getSocketManager().delSocketThread(this);
				System.out.println("终端与服务器失去连接:" + this.getSocket().getRemoteSocketAddress());
			}
			
			@Override
			public void onReceiveData(String strline) {
				System.out.println(getSocket().getRemoteSocketAddress() + "\t" + strline);
				for(BaseServerSocketThread bs:getSocketManager().getSocketThreads()){
					if(!bs.equals(this)){
						System.out.println("消息转发至[" + bs.getSocket().getRemoteSocketAddress() +"]");
						bs.sendMessage(strline);
					}
				}
			}
		};
		return bsst;
	}
}

class My2Listener extends BaseListener<BaseServerSocketThread>{
	ChatroomSocketManager<BaseServerSocketThread> manager = new ChatroomSocketManager<BaseServerSocketThread>();
	public My2Listener(int port) {
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