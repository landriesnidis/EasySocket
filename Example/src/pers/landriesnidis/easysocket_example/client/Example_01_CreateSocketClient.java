package pers.landriesnidis.easysocket_example.client;

import java.util.Scanner;

import pers.landriesnidis.easysocket.client.SocketTCPConnector;
import pers.landriesnidis.easysocket.client.SocketTCPConnector.OnSocketReceiveListener;

public class Example_01_CreateSocketClient {
	
	public static final String 	SERVER_IP 		= "127.0.0.1";
	public static final int 	SERVER_PORT 	= 12345;
	
	public static void main(String[] args) {
		SocketTCPConnector stc = new SocketTCPConnector(SERVER_IP, SERVER_PORT, new OnSocketReceiveListener() {
			@Override
			public void onStateChanged(int code) {
				switch(code){
				case SocketTCPConnector.STATUS_CODE_CLOSED:
					System.err.println("Socket连接已断开.");
					break;
				case SocketTCPConnector.STATUS_CODE_UNKNOWNHOST:
					System.err.println("未知主机地址.");
					break;
				case SocketTCPConnector.STATUS_CODE_EXCEPTION:
					System.err.println("通信时发生异常.");
					break;
				case SocketTCPConnector.STATUS_CODE_CONNECTED:
					System.err.println("成功建立连接.");
					break;
				case SocketTCPConnector.STATUS_CODE_INTERRUPTION:
					System.err.println("已终止通信.");
					break;
				case SocketTCPConnector.STATUS_CODE_UNCONNECTION:
					System.err.println("尚未建立连接.");
					break;
				}
			}
			
			@Override
			public void onReceiveData(String message) {
				System.out.println("接收到消息：" + message);
			}
		});
		stc.start();
		Scanner scanner = new Scanner(System.in);
		System.out.println("输入'EXIT'即可退出程序.");
		while(true){
			try{
				String str = scanner.next();
				if(str.equals("EXIT"))break;
				stc.send(str);
			}catch(Exception e){
				break;
			}
		}
		scanner.close();
		stc.close();
	}
}
