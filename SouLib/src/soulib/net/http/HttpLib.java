package soulib.net.http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpLib{

	public static String post(String urlString,File body){
		String ret=null;
		FileInputStream fis=null;
		try{
			URL url=new URL(urlString);
			URLConnection uc=url.openConnection();
			uc.setDoOutput(true);//POST可能にする
			uc.setRequestProperty("Accept-Language","ja");// ヘッダを設定
			OutputStream os=uc.getOutputStream();//POST用のOutputStreamを取得
			os.write("RUN=true&CODE=".getBytes(StandardCharsets.UTF_8));
			fis=new FileInputStream(body);
			BufferedReader reader0=new BufferedReader(new InputStreamReader(fis));
			PrintStream pw0=new PrintStream(os);
			String s0;
			while((s0=reader0.readLine())!=null){
				pw0.println(URLEncoder.encode(s0,"utf-8"));
			}
			reader0.close();
			InputStream is=uc.getInputStream();//POSTした結果を取得
			BufferedReader reader=new BufferedReader(new InputStreamReader(is));
			String s;
			StringWriter sw=new StringWriter();
			PrintWriter pw=new PrintWriter(sw);
			while((s=reader.readLine())!=null){
				pw.println(s);
			}
			ret=sw.toString();
			reader.close();
		}catch(MalformedURLException e){
			System.err.println("Invalid URL format: "+urlString);
		}catch(IOException e){
			System.err.println("Can't connect to "+urlString);
		}
		close(fis);
		return ret;
	}
	public static void close(Closeable... ca){
		for(Closeable c:ca)close(c);
	}
	public static void close(Closeable c){
		if(c!=null) try{
			c.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static String post(String urlString,String body){
		String ret=null;
		FileInputStream fis=null;
		try{
			URL url=new URL(urlString);
			URLConnection uc=url.openConnection();
			uc.setDoOutput(true);//POST可能にする
			uc.setRequestProperty("Accept-Language","ja");// ヘッダを設定
			if(body!=null){
				OutputStream os=uc.getOutputStream();//POST用のOutputStreamを取得
				//body=URLEncoder.encode(body,"utf-8");
				os.write(body.getBytes(StandardCharsets.UTF_8));
			}
			InputStream is=uc.getInputStream();//POSTした結果を取得
			BufferedReader reader=new BufferedReader(new InputStreamReader(is));
			String s;
			StringWriter sw=new StringWriter();
			PrintWriter pw=new PrintWriter(sw);
			while((s=reader.readLine())!=null){
				pw.println(s);
			}
			ret=sw.toString();
			reader.close();
		}catch(MalformedURLException e){
			System.err.println("Invalid URL format: "+urlString);
		}catch(IOException e){
			System.err.println("Can't connect to "+urlString);
		}
		close(fis);
		return ret;
	}
}
