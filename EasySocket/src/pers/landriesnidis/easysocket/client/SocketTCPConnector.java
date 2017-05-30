package pers.landriesnidis.easysocket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTCPConnector extends Thread {

	public static final int STATUS_CODE_CLOSED = 0;
	public static final int STATUS_CODE_UNKNOWNHOST = 1;
	public static final int STATUS_CODE_EXCEPTION = 2;
	public static final int STATUS_CODE_CONNECTED = 3;
	public static final int STATUS_CODE_INTERRUPTION = 4;
	public static final int STATUS_CODE_UNCONNECTION = 5;

	final private String ip;
	final private int port;
	final private OnSocketReceiveListener listener;

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private boolean isRun;

	public SocketTCPConnector(String ip, int port,
			OnSocketReceiveListener listener) {
		this.ip = ip;
		this.port = port;
		this.listener = listener;
	}

    @Override
    public synchronized void start() {
        if (!isRun){
            super.start();
            isRun = true;
        }
    }
    
    //关闭
    public void close(){
        isRun = false;
        try {
			reader.close();
			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        socket = null;
    }
    
    /**
     * 发送数据
     *
     * @param str 要发送的字符串
     */
    public void send(String str) {
        if (writer != null) {
            writer.write(str + "\n");
            writer.flush();
        } else {
        	listener.onStateChanged(STATUS_CODE_UNCONNECTION);
        }
    }

	@Override
	public void run() {
		String line;
		try {
			socket = new Socket(ip, port);
			writer = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			listener.onStateChanged(STATUS_CODE_CONNECTED);
			try {
				while (isRun && (line = reader.readLine()) != null) {
//					System.out.println(socket.getRemoteSocketAddress() + " : " + line);
					if (listener != null) {
						listener.onReceiveData(line);
					}
				}
			} catch (UnknownHostException e) {
				listener.onStateChanged(STATUS_CODE_UNKNOWNHOST);
			} catch (IOException e) {
				listener.onStateChanged(STATUS_CODE_EXCEPTION);
			}
			if (isRun)
				listener.onStateChanged(STATUS_CODE_INTERRUPTION);
			else
				listener.onStateChanged(STATUS_CODE_CLOSED);
		} catch (IOException e) {
			listener.onStateChanged(STATUS_CODE_EXCEPTION);
		}
	}

	/**
	 * Socket数据接受监听器
	 * @author landriesnidis
	 */
	public interface OnSocketReceiveListener {
		/**
	     * 接收到数据
	     * @param str
	     */
	    void onReceiveData(String str);

	    /**
	     * 当状态发生变化
	     * @param STATUS_CODE
	     */
	    void onStateChanged(int STATUS_CODE);
	}
}
