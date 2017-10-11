package pers.landriesnidis.easysocket_example.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import pers.landriesnidis.easysocket.client.SocketTCPConnector;
import pers.landriesnidis.easysocket.client.SocketTCPConnector.OnSocketReceiveListener;

public class Example_03_SendAdapterFile {

	public static final String 	SERVER_IP 		= "127.0.0.1";
	public static final int 	SERVER_PORT 	= 12345;
	
	public static void main(String[] args) throws IOException {
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
		System.out.println("\t(1)输入'FILE'发送测试文件.");
		System.out.println("\t(2)输入'EXIT'即可退出程序.");
		while(true){
			try{
				System.out.print("请输入文本信息或命令：");
				String str = scanner.next();
				if(str.equals("EXIT")){
					break;
				}
				if(str.equals("FILE")){
					System.out.print("输入要发送的JPG文件路径：");
					try{
						File file = new File(scanner.next());
						FileInputStream fis = new FileInputStream(file);
						int len = fis.available();
						byte[] ab = new byte[len];
						fis.read(ab);
						stc.sendString("<File>");
						stc.send(ab);
						stc.sendString("</File>");
						fis.close();
						continue;
					}catch(Exception e){
						System.err.println("文件不存在或无法打开，退出文件发送模式...");
						continue;
					}
					
				}
				stc.sendString(str);
			}catch(Exception e){
				e.printStackTrace();
				break;
			}
		}
		scanner.close();
		stc.close();
	}
}
