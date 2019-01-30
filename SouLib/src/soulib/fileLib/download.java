package soulib.fileLib;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class download{
	public String Extension(){
		return ".jar";
	};

	public download(String URL,String DLFileName){
		dl(DLFileName, URL);
	}

	public download(String[] URL,String[] DLFileName){
		if(URL.length != DLFileName.length)
			return;
		for(int i = 0;i < URL.length;i++){
			dl(DLFileName[i], URL[i]);
		}
	}

	public static boolean dl(String DLFileName,String URL){
		File file = new File("." + FileEditor.Dir() + DLFileName);
		return dl(file, URL);
	}

	public static boolean dl(File file,String uRL){
		try{
			if(dlT(file, uRL))return true;
		}catch (IOException e){
			e.printStackTrace();
		}
		return false;
	}

	public static boolean dlT(File DLFile,String URL) throws IOException{
			URL url = new URL(URL);
			url.openConnection().connect();
			FileEditor f = new FileEditor();
			return f.copyResourceToFile(url, DLFile);
	}
}
