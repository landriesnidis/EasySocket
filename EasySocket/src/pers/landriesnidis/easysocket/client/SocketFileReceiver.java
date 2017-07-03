package pers.landriesnidis.easysocket.client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketFileReceiver{

	private ServerSocket serversocket;
	private FileOutputStream fos = null;
	private FileReceiveListener listener = null;
	private String filename;
	/**
	 * 构造一个文件接收器(随机端口号)
	 * @param  filename					文件存放位置及文件名
	 * @throws FileNotFoundException	文件不存在
	 * @throws IOException				创建Socket监听发生异常
	 */
	public SocketFileReceiver(String filename) throws IOException{
		serversocket = new ServerSocket(0);
		this.filename = filename;
	}
	
	/**
	 * 构造一个文件接收器(指定端口号)
	 * @param port						指定端口
	 * @param filename					文件存放位置及文件名
	 * @throws FileNotFoundException	文件不存在
	 * @throws IOException				创建Socket监听发生异常
	 */
	public SocketFileReceiver(String filename,int port) throws FileNotFoundException,IOException{
		serversocket = new ServerSocket(port);
		this.filename = filename;
	}
	
	/**
	 * 为文件接收器添加一个回调接口
	 * @param listener
	 */
	public void setFileReceiveListener(FileReceiveListener listener){
		this.listener = listener;
	}
	
	/**
	 * 获取随机端口号
	 * @return 端口号
	 */
	public int getLocalPort(){
		return serversocket.getLocalPort();
	}
	
	/**
	 * 开始监听
	 * @throws IOException
	 */
	public void start() throws IOException{
		File file = new File(filename);
		if(file.exists())file.delete();
		receiveFile(serversocket.accept());
	}
	
	/**
	 * 文件接收方法
	 * @param socket 接收数据的来源
	 */
	private void receiveFile(Socket socket) {
        byte[] inputByte = null;
        int length = 0;
        DataInputStream dis = null;
        try {
            try {
                dis = new DataInputStream(socket.getInputStream());
                inputByte = new byte[512];
                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
                    if(fos==null)fos = new FileOutputStream(new File(filename));
                    fos.write(inputByte, 0, length);
                    fos.flush();
                }
            } finally {
                if (fos != null)
                    fos.close();
                if (dis != null)
                    dis.close();
                if (socket != null)
                    socket.close();
                
                if(listener!=null){
                	listener.onFinish();
                }
            }
        } catch (Exception e) {
        }
    }
	
	public interface FileReceiveListener{
		void onFinish();
	}

}
