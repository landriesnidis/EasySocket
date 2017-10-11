package pers.landriesnidis.easysocket.server.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Test {

	
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();    //��ȡ��ʼʱ��
		//--------------------------------------------------------------------------------------------------------------------
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("F:\\input.txt")),"utf-8"));
		KMPFilter filter = new KMPFilter(br, "chapterName=\"".getBytes(), "\" chapterL".getBytes());
		
		File outFile = new File("F:\\Test.txt");
		if(outFile.exists()){
			outFile.delete();
		}
		outFile.createNewFile();
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
		
		//���ü���
		filter.setListener(new KMPFilter.DataFilterListener() {

			@Override
			public void onPassed(String str) {
				//System.out.println("������Ϣ��" + str);
			}

			@Override
			public void onCaptured(String str) {
				try {
					//System.out.println("������Ϣ��" + str);
					bw.write(str);
					bw.write("\r\n");
					bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinished() {
				System.out.println("��ݶ�ȡ��ϣ�");
			}
		});
		filter.filtrate();
		
		bw.flush();
		bw.close();
		
		//--------------------------------------------------------------------------------------------------------------------
		long endTime = System.currentTimeMillis();    //��ȡ����ʱ��
		System.out.println("��������ʱ�䣺" + (endTime - startTime)/1000.0 + "s");    //�����������ʱ��
	}
}
