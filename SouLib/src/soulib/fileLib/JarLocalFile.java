package soulib.fileLib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JarLocalFile{

	private URL url=null;
	private String path=null;
	public JarLocalFile(String path){
		this.path=path;
		url=ClassLoader.getSystemResource(path);
		//url=JarLocalFile.class.getClassLoader().getResource(path);
	}
	public boolean exists(){
		return url!=null;
	}
	public String toString() {
		return getClass().getName() + "@" +path;
	}
	public long SaveFileNoException(File f){
		try{
			return SaveFile(f);
		}catch(IOException e){
			e.printStackTrace();
		}
		return -1;
	}
	public long SaveFile(File to) throws IOException{
		InputStream is;
		try{
			if(url==null)throw new IOException("NotLocalFile");
			is=url.openStream();
		}catch(IOException e){
			throw new IOException("NotLocalFile",e);
		}
		try{
			return FileEditor.copy(is,new FileOutputStream(to!=null ? to : new File(path)));
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return -1;
	}
	public static long CopyJarLocalFile(String from,File to) throws Exception{
		return FileEditor.copy(getJarReader(from),new FileOutputStream(to));
	}
	public static InputStream getJarReader(String path){
		return JarLocalFile.class.getClassLoader().getResourceAsStream(path);
	}
	public URL getURL(){
		return url;
	}
	public String getPath(){
		return path;
	}
}
