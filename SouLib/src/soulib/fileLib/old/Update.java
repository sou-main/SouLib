package soulib.fileLib.old;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import soulib.fileLib.ConfigFile;
import soulib.fileLib.FileEditor;
import soulib.lib.Version;
import soulib.windowLib.StandardWindow;

public class Update{
	public static String path = System.getProperty("user.home")+FileEditor.Dir()+"sou"+FileEditor.Dir()+"update";
	private ConfigFile cf;
	private String filename="";
	private String version="1.0.0";
	private String DLFileName;
	private String LastVersion;
	private String url;
	private File fi=null;
	public Update(String URL,String version) throws IOException{
		this(URL);
		this.version=version;
	}
	private Update(String URL) throws IOException{
		String[] f = FileEditor.FE.ReadFileURL0(URL);
		cf = new ConfigFile(f);
		url=cf.getConfigDataString("path");
		DLFileName=cf.getConfigDataString("filename");
		LastVersion=cf.getConfigDataString("version","1.0.0");
	}
	public Update(File f) throws IOException{
		this(new ConfigFile(f).getConfigDataString("url"));
		fi=f;
		ConfigFile c = new ConfigFile(f);
		filename=c.getConfigDataString("Filename");
		version=c.getConfigDataString("version","1.0.0");
	}
	public static void MakeServerSide(File f, String path, String filename, String version) throws IOException {
		f.createNewFile();
		ConfigFile c = new ConfigFile(f);
		c.setConfigDataString("path",path);
		c.setConfigDataString("filename",filename);
		c.setConfigDataString("version",version);
		c.saveConfigT(f);
	}
	public static void Make(File f, String url, String filename, String version) throws IOException {
		f.createNewFile();
		ConfigFile c = new ConfigFile(f);
		c.setConfigDataString("url",url);
		c.setConfigDataString("Filename",filename);
		c.setConfigDataString("version",version);
		c.saveConfigT(f);
	}
	public void update() throws IOException{
		if(!check())return;
		if(dl()&&fi!=null) {
			ConfigFile c = new ConfigFile(fi);
			c.setConfigDataString("Filename",DLFileName);
			c.saveConfigT(fi);
			del();
		}
	}
	public boolean dl() throws MalformedURLException {
		return FileEditor.FE.copyResourceToFile(new URL(url),new File(DLFileName));
	}
	public boolean check(){
		Version v = new Version(version);
		return v.isNew(new Version(LastVersion));
	}
	private void del() {
		if(!filename.isEmpty()) {
			File f = new File(filename);
			if(f.exists())f.delete();
		}
	}
	public static void main(String[] args){
		ConfigFile cf = new ConfigFile(args);
		if(cf.getConfigDataBoolean("make")) {
			StandardWindow.setLookAndFeel(null,null);
			File f=StandardWindow.SaveFileWindow(new File(path+FileEditor.Dir()+"update.upd"));
			if(f==null)System.exit(0);
			try {
				if(StandardWindow.Yes_NoWindow("ServerSide","Side")) {
					String url=StandardWindow.InputWindow("FileURL","http://sample.com/sample-1.0.0.jar");
					if(url.isEmpty())System.exit(0);
					String fn=StandardWindow.InputWindow("SaveFileName","sample-1.0.0.jar");
					if(fn.isEmpty())System.exit(0);
					String v=StandardWindow.InputWindow("LastVersion","1.0.0");
					if(v.isEmpty())System.exit(0);
					MakeServerSide(f,url,fn,v);
				}else {
					String url=StandardWindow.InputWindow("UpdateCheckURL","http://sample.com/sample.upd");
					if(url.isEmpty())System.exit(0);
					String fn=StandardWindow.InputWindow("DeleteFilePath","c:\\sample-1.0.0.jar");
					if(fn==null)fn="";
					String v=StandardWindow.InputWindow("NowVersion","1.0.0");
					if(v.isEmpty())System.exit(0);
					Make(f,url,fn,v);
				}
			}catch (IOException e){
				e.printStackTrace();
			}
			System.exit(0);
		}
		path=cf.getConfigDataString("path", path);
		File dir=new File(path);
		if(dir.isDirectory()){
			File[] lf = dir.listFiles();
			for(int i=0;i<lf.length;i++) {
				try{
					if(lf[i].getName().endsWith(".upd")) {
						Update u = new Update(lf[i]);
						u.update();
					}
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}
