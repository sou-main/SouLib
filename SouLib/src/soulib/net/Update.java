package soulib.net;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import soulib.fileLib.ConfigFile;
import soulib.fileLib.FileEditor;
import soulib.lib.Version;

public class Update{
	protected FileEditor fe;
	protected File dlFile;

	/**サーバーアドレスのファイルには<br>
	 * version:1.3.0<br>
	 * downloadUrl:http://sousoft.dip.jp/index.html<br>
	 * の様にversionの行とdownloadUrlの行が必要<br>
	 * */
	public Update(String URL,Version verion,File dlf) {
		dlFile=dlf;
		fe=new FileEditor();
		try{
			String[] urlData=fe.ReadFileURL0(URL);
			ConfigFile cf=new ConfigFile(urlData);
			String ver=cf.getConfigDataString("version","0.0.0");
			Version v=new Version(ver);
			boolean dl=verion.isNew(v);
			if(dl) {
				String url=cf.getConfigDataString("downloadUrl");
				if(!url.isEmpty())dl(url);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	protected boolean dl(String url) throws MalformedURLException {
		return fe.copyResourceToFile(new URL(url),dlFile);
	}
}
