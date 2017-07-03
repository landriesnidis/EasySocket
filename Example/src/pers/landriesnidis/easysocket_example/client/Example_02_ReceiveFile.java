package pers.landriesnidis.easysocket_example.client;

import java.io.IOException;
import java.net.ServerSocket;

import pers.landriesnidis.easysocket.client.SocketFileReceiver;

public class Example_02_ReceiveFile {
	
	public static void main(String[] args) throws IOException{
		SocketFileReceiver sfr = null;
		try {
			ServerSocket s = new ServerSocket(0);
			int port = s.getLocalPort();
			s.close();
			sfr = new SocketFileReceiver("./newfile.jpg",port);
		} catch (IOException e) {
			System.out.println("端口号已被占用.");
			return;
		}
		sfr.setFileReceiveListener(new SocketFileReceiver.FileReceiveListener() {
			
			@Override
			public void onFinish() {
				System.out.println("接收完毕!");
			}
		});
		System.out.println("端口号为:" + sfr.getLocalPort());
		sfr.start();
		System.out.println("开始接受文件.");
	}
}