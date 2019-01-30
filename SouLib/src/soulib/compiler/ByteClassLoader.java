package soulib.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteClassLoader extends ClassLoader{

	private byte[] data;
	public ByteClassLoader(){
		super(Thread.currentThread().getContextClassLoader());
	}
	public Class<?> findClass(String name){
		return defineClass(null,data,0,data.length);
	}
	@SuppressWarnings("unchecked")
	public synchronized <T> Class<T> load(InputStream is) throws ClassNotFoundException {
		try{
			ByteArrayOutputStream bb=new ByteArrayOutputStream(4096);
			byte[] buf=new byte[1024];
			int readedBytes=-1;
			while((readedBytes=is.read(buf))!=-1){
				bb.write(buf,0,readedBytes);
			}
			data=bb.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try{
				is.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return (Class<T>) loadClass("");
	}
}