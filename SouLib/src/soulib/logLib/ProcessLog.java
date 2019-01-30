package soulib.logLib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class ProcessLog extends Thread{
	public Reader in;
	public boolean end=true;
	private StringWriter ststringw;

	public ProcessLog(Process p,String Encoding) throws UnsupportedEncodingException{
		this.in = new BufferedReader(new InputStreamReader(p.getInputStream(), Encoding));
		end=false;
		this.start();
	}

	public ProcessLog(Process p){
		this.in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		end=false;
		this.start();
	}

	public void run(){
		try{
			ststringw=new StringWriter();
			BufferedWriter bw = new BufferedWriter(ststringw);
			int size;
			char[] buf=new char[128];
			while ((size=in.read(buf))>=0){
				ststringw.write(buf, 0, size);
			}
			bw.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		end=true;
	}
	public String toString() {
		return get();
	}
	public String get(){
		return ststringw!=null?ststringw.toString():"";
	}
	/**終了するまで待機*/
	public void waitFor(){
		while(!end) {
			try{
				Thread.sleep(200);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}