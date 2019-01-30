package soulib.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class InputCommand extends Thread{
	public static Charset charset=StandardCharsets.UTF_8;
	private BufferedReader is;
	public boolean autoClose;
	public InputCommand(){
		this(null,null);
	}
	public InputCommand(String threadname){
		this(threadname,null);
	}
	public InputCommand(String threadname,InputStream in){
		super(threadname!=null ? threadname : "InputCommand");
		InputStreamReader isr;
		if(in==null) isr=new InputStreamReader(System.in,charset);
		else isr=new InputStreamReader(in,charset);
		is=new BufferedReader(isr);
	}
	public abstract void command(String command,String[] parm);
	@Override
	public void run(){
		while(one()!=null) {
			try{
				Thread.sleep(200);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		if(autoClose) try{
			is.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public String one(){
		try{
			String com=is.readLine();
			if(com==null)return null;
			if(com.isEmpty())return com;
			String[] arr=com.split(" ");
			String[] parm=new String[arr.length-1];
			for(int i=0;i<parm.length;i++){
				parm[i]=arr[i+1];
			}
			command(arr[0],parm);
			return com;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
