package pers.landriesnidis.easysocket.server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import pers.landriesnidis.easysocket.server.manager.BasicSocketManager;

/**
 * 基本的Socket侦听类
 * @author Landriesnidis
 * @param <S>
 */
public abstract class BaseListener<S extends BaseServerSocketThread> extends Thread {

	private final int localPort;
	private ServerSocket serverSocket;
	private boolean isRun = true;
	private BasicSocketManager<S> SocketManager = new BasicSocketManager<>();

	/**
	 * 构造方法
	 * @param port	用于监听的端口号
	 * @throws PortOccupiedException 
	 * @throws IOException 
	 */
	public BaseListener(int port) throws BindException{
		this.localPort = port;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new BindException(String.format("Port %d is in use, or the requested local address could not be assigned.", port));
		}
	}

	public void run() {
		try {
			// 当开始侦听时触发该方法(抽象方法)
			onStart();

			// 循环的监听来自客户端的链接
			while (isRun) {

				// 等待客户端连接(阻塞)
				Socket socket = serverSocket.accept();

				// 当建立连接后触发该方法(抽象方法)
				onConnection(socket);

				// 从子类中获得用于Socket通信的线程对象
				S newServerSocket = onHandOver(socket);
				newServerSocket.start();
				socket = null;
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 当建立通信后，将该Socket连接移交给处理具体事务的对象(实现方法的BaseServerSocketThread对象)并返回
	 * @return
	 */
	protected abstract S onHandOver(Socket socket);

	/**
	 * 当开始监听的时候触发该方法
	 */
	protected abstract void onStart();

	/**
	 * 当接收到Socket请求并建立通信的时候触发该方法
	 */
	protected abstract void onConnection(Socket socket);
	
	/**
	 * 获取端口号
	 * @return
	 */
	public int getLocalPort() {
		return localPort;
	}
	
	/**
	 * 获取Socket通信管理器
	 * @return
	 */
	public BasicSocketManager<S> getSocketManager() {
		return SocketManager;
	}
	
	/**
	 * 设置Socket通信管理器
	 * @param socketManager
	 */
	public void setSocketManager(BasicSocketManager<S> socketManager) {
		SocketManager = socketManager;
	}
}
