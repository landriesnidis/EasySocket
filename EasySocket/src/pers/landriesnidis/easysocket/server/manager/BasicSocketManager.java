package pers.landriesnidis.easysocket.server.manager;

import java.util.Vector;

import pers.landriesnidis.easysocket.server.BaseServerSocketThread;

/**
 * 基本的Socket通信管理器
 * @author Landriesnidis
 * @param <S> 基于BaseServerSocketThread拓展的类
 */
public class BasicSocketManager<S extends BaseServerSocketThread> {

	// Socket通信线程队列
	protected Vector<S> vecSocketThreads = new Vector<S>();
	
	/**
	 * 获取Socket队列
	 * @return
	 */
	public Vector<S> getSocketThreads() {
		return vecSocketThreads;
	}
	
	/**
	 * 从队列移除指定项
	 * @param s
	 */
	public void delSocketThread(S s) {
		vecSocketThreads.remove(s);
	}
	
	/**
	 * 向队列中添加元素
	 * @param s
	 */
	public void addSocketThread(S s){
		vecSocketThreads.add(s);
	}
}
