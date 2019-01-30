package soulib.fileLib;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import soulib.lib.DataEditor;
import soulib.lib.SystemState;

public class FileEditor extends DataEditor{
	public static final FileEditor FE=new FileEditor();
	public static final String USERHOME=System.getProperty("user.home");
	public static final String DIR=System.getProperty("file.separator");
	public int MaxBuf=50;
	public String Dir=Dir();
	public int ErrorType=0;
	public ArrayList<Throwable> error=new ArrayList<Throwable>();
	public String DefaultEncode="UTF-8";
	/** すべての情報を出力 */
	public static final byte Level_All=-3;
	/** ほぼすべての情報 */
	public static final byte Level_Debug=-2;
	/** あったら便利な情報 */
	public static final byte Level_Info=-1;
	/** 問題があるかもしれない場合 */
	public static final byte Level_Warning=0;
	/** 正常に処理を完了できなかった場合 */
	public static final byte Level_Error=1;
	/** 継続不可能なほど深刻な問題 */
	public static final byte Level_Exit=2;
	/** どの程度のログを出力するか */
	public byte LogLevel=Level_Warning;
	public long all=0;
	public long load=0;
	public static String Dir(){
		return DIR;
	}
	public FileEditor(){
		Dir=Dir();
		setFileEncoding("UTF-8");
	}
	public static synchronized String setFileEncoding(String s){
		final String s0=s;
		return AccessController.doPrivileged(
				new PrivilegedAction<String>(){
					@Override
					public String run(){
						System.setProperty("file.encoding",s0);
						return null;
					}
				});
	}
	public String[] StringToStringsOLD(String s){
		return StringToStringsOLD(s,MaxBuf);
	}
	public String[] StringToStrings(String s){
		try{
			return StringToStringsT(s,MaxBuf);
		}catch(IOException e){
			e.printStackTrace();
		}
		return new String[0];
	}
	public String StringsToStringT(String[] FileData) throws IOException{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		for(int j=0;j<FileData.length;j++){
			try{
				if(FileData[j]!=null){
					bw.write(FileData[j]);
				}
			}catch(Throwable t){
				Exception(t);
			}finally{
			}
			try{
				bw.newLine();
			}catch(Throwable t){
				Exception(t);
			}finally{
			}
		}
		bw.close();
		return sw.toString();
	}
	public String[] StringToStringsT(String Data,int MaxBuf) throws IOException{
		StringReader sw=new StringReader(Data);
		try{
			BufferedReader br=new BufferedReader(sw);
			ArrayList<String> FileData=new ArrayList<String>();
			while(true){
				try{
					String buf0=br.readLine();
					if(buf0==null) break;
					//String buf1=new String(buf0.getBytes());// "Shift_JIS"
					FileData.add(buf0);
				}catch(Throwable t){
					if(LogLevel<=Level_Debug){
						System.out.println(b(t));
					}else if(LogLevel<=Level_Info){
						System.out.println(t);
					}
				}
			}
			//FileData=Arrays.copyOf(FileData,FileData.length);// 新
			// FileData=cut(FileData);//旧
			return FileData.toArray(new String[FileData.size()]);
		}finally{
			sw.close();
		}
	}
	public String[] StringToStrings(String s,int MaxBuf){
		try{
			return StringToStringsT(s,MaxBuf);
		}catch(IOException e){
			e.printStackTrace();
		}
		return new String[0];
	}
	public String[] StringToStringsOLD(String s,int MaxBuf){
		File file=new File(System.getProperty("java.io.tmpdir")+Dir+"buf.tmp");
		try{
			file=File.createTempFile("StringToStrings",null);
		}catch(IOException e){
		}
		file.deleteOnExit();
		SaveFile(s,file);
		String[] t=ReadFile0(file,MaxBuf);
		file.delete();
		return t;
	}
	public String StringsToString(String[] s){
		try{
			return StringsToStringT(s);
		}catch(IOException e){
			e.printStackTrace();
		}
		return "";
	}
	public String StringsToStringOLD(String[] s){
		File file=new File(System.getProperty("java.io.tmpdir")+Dir+"buf.tmp");
		try{
			file=File.createTempFile("StringsToString",null);
		}catch(IOException e){

		}
		SaveFile(s,file,1);
		String t=ReadFile(file);
		file.delete();
		return t;
	}
	public boolean SaveFileJP(String[] Data,File file){
		return SaveFileJP(Data,file,false);
	}
	public boolean SaveFileJP(String[] Data,File file,boolean add){
		try{
			if(file.exists()) file.delete();
		}catch(Throwable t){
			System.out.println("ファイルの削除に失敗しました"+t.toString());
		}
		try{
			String Buf=System.getProperty("line.separator");
			OutputStream os=new FileOutputStream(file,add);
			for(int j=0;j<Data.length;j++){
				if(Data[j]!=null){
					int size;
					byte[] buf=new byte[1024];
					size=Data[j].getBytes(DefaultEncode).length;
					buf=Data[j].getBytes(DefaultEncode);
					os.write(buf,0,size);
					os.write(Buf.getBytes());
				}
			}
			os.close();
			if(file.createNewFile()) return true;
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	public boolean SaveFile(String[] FileData,String Dir,String FileName){
		try{
			File Savedir=new File(Dir);
			if(!Savedir.exists()) if(Savedir.mkdirs()) ;
			File newfile=new File(Dir+this.Dir+FileName+".txt");
			return SaveFile(FileData,newfile);
		}catch(Throwable t){
			Exception(t);
			ErrorType=3;
			return false;
		}
	}
	public boolean SaveFileT(String[] FileData,File file,int skip) throws IOException{
		try{
			if(file.exists()) file.delete();
		}catch(Throwable t){
			System.out.println("ファイルの削除に失敗しました"+t.toString());
		}
		file.createNewFile();
		OutputStreamWriter osw=new OutputStreamWriter(new FileOutputStream(file),DefaultEncode);
		BufferedWriter bw=new BufferedWriter(osw);
		for(int j=0;j<FileData.length;j++){
			if(skip>0||FileData[j]!=null){
				try{
					if(FileData[j]!=null){
						bw.write(FileData[j]);
					}
				}catch(Throwable t){
					Exception(t);
				}finally{
				}
				try{
					bw.newLine();
				}catch(Throwable t){
					Exception(t);
				}finally{
				}
			}
		}
		bw.close();
		return true;
	}
	public boolean SaveFile(String[] FileData,File file){
		return SaveFile(FileData,file,0);
	}
	public boolean SaveFile(String[] FileData,File file,int skip){
		try{
			return SaveFileT(FileData,file,skip);
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	private static boolean checkBeforeWritefile(File file){
		if(file.exists()){
			if(file.isFile()&&file.canWrite()){
				return true;
			}
		}
		return false;
	}
	public void Exception(Throwable t){
		// crash_report(t,"FileEditor");
		this.error.add(t);
		t.printStackTrace();
	}
	public String[] ReadFile(String Dir,String FileName){
		try{
			File Readdir=new File(Dir);
			if(!Readdir.exists()) if(Readdir.mkdirs()) ;
			File Readfile=new File(Readdir.getAbsolutePath()+this.Dir+FileName+".txt");
			return ReadFile0(Readfile);
		}catch(Throwable t){
			Exception(t);
			ErrorType=5;
			return null;
		}finally{
		}
	}
	public String[] ReadFile0(File file){
		return ReadFile0(file,MaxBuf);
	}
	public String[] ReadFile0(File file,int MaxBuf){
		BufferedReader br=null;
		try{
			ArrayList<String> FileData=new ArrayList<String>();
			if(checkBeforeWritefile(file)){
				all=file.length();
				br=new BufferedReader(new InputStreamReader(new FileInputStream(file),DefaultEncode));
				while(true){
					try{
						String buf0=br.readLine();
						if(buf0==null) break;
						load=buf0.getBytes().length;
						FileData.add(buf0);
					}catch(Throwable t){
						
					}
				}
				br.close();
				return FileData.toArray(new String[FileData.size()]);
			}
			ErrorType=4;
			return null;// return false;
		}catch(Throwable t){
			Exception(t);
			ErrorType=5;
			return null;
		}finally{
			if(br!=null) try{
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public String[] ReadFileJP(File file){
		return ReadFileJP(file,DefaultEncode,MaxBuf);
	}
	public String[] ReadFileJP(File file,String Encode,int MaxBuf){
		BufferedReader b_reader=null;
		try{
			b_reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),Encode));
			String s;
			String[] t=new String[MaxBuf];
			int i=0;
			while(i<t.length&&(s=b_reader.readLine())!=null){
				t[i]=s;
				i++;
			}
			b_reader.close();
			return t;
		}catch(Throwable t){
			Exception(t);
			ErrorType=5;
			return null;
		}finally{
			if(b_reader!=null)try{
				b_reader.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public Appendable ReadFile(File file,Appendable out){
		return ReadFile(file,DefaultEncode,out);
	}
	public Appendable ReadFile(File file,String Encode,Appendable out){
		BufferedReader b_reader=null;
		try{
			b_reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),Encode));
			char[] c= new char[32];
			int len;
			int i=0;
			while(true){
				len=b_reader.read(c,0,c.length);
				if(len<1)break;
				for(i=0;i<len;i++)out.append(c[i]);
			}
			b_reader.close();
		}catch(Throwable t){
			t.printStackTrace();
		}finally{
			if(b_reader!=null)try{
				b_reader.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return out;
	}
	public String ReadFile(File file){
		InputStream is=null;
		try{
			if(file.isDirectory()) {
				System.err.println("ファイルを開けません "+file.getPath()+" はディレクトリです");
				return null;
			}
			is=new FileInputStream(file);
			return new String(toByteArray(is),DefaultEncode);
		}catch(Throwable t){
			Exception(t);
			ErrorType=5;
			return null;
		}finally{
			if(is!=null) try{
				is.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public int GetFiles(String Dir){
		try{
			File Readdir=new File(Dir);
			return Readdir.list().length;
		}catch(Throwable t){
			return 0;
		}finally{
		}
	}
	public File crash_report(Throwable t,File FileName){
		try{
			t.getStackTrace();
			String FileData="";
			StringWriter stringwriter=null;
			PrintWriter printwriter=null;
			try{
				stringwriter=new StringWriter();
				printwriter=new PrintWriter(stringwriter);
				t.printStackTrace(printwriter);
				FileData=stringwriter.toString();
			}catch(Throwable u){
				u.printStackTrace();
			}finally{
			}
			String[] buf=StringToStrings(FileData,50);
			SystemState s=new SystemState();
			String[] code={"--------SystemData---------",
					"JVM_Version="+s.JVM_Version,
					"CPUCores="+s.CPUCores,
					"maxMemory="+s.maxMemory+"("+s.maxMemoryMB+"MB)",
					"usedMemory="+s.usedMemory+"("+s.usedMemoryMB+"MB)",
					"UserName="+s.UserName,
					"OS_Name="+s.OS_Name,
					"OS_Version"+s.OS_Version,
					"tmpDir="+s.tmpDir,
					"UserName="+s.UserName,
					"UserHome="+s.UserHome};
			if(SaveFile(addString(buf,code),FileName)) return FileName;
		}catch(Throwable u){
			ErrorType=6;
		}
		return null;
	}
	public File crash_report(Throwable t,String Name){
		if(Name==null){
			Name="FileName";
		}
		Date date=new Date();
		SimpleDateFormat Format=new SimpleDateFormat("yy-MM-dd-HH-mm");
		File file=new File("crash-reports"+FileEditor.Dir()+Name+"-"+Format.format(date)+".txt");
		if(t.getMessage()==null){
			if(t instanceof NullPointerException){
				t=new NullPointerException(Name);
			}else if(t instanceof StackOverflowError){
				t=new StackOverflowError(Name);
			}else if(t instanceof OutOfMemoryError){
				t=new OutOfMemoryError(Name);
			}
			t.setStackTrace(t.getStackTrace());
		}
		return crash_report(t,file);
	}
	public boolean SaveFile(String FileData,File file){
		return SaveFileA(FileData,file)!=null;
	}
	public File SaveFileA(String FileData,File file){
		try{
			try{
				if(file.exists()) file.delete();
			}catch(Throwable t){
				System.out.println("ファイルの削除に失敗しました"+t.toString());
			}
			if(file.createNewFile()){
				if(checkBeforeWritefile(file)){
					BufferedWriter bw=null;
					try{
						bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),DefaultEncode));
						bw.write(FileData);
					}catch(Throwable t){
						Exception(t);
					}
					if(bw!=null)bw.close();
					return file;
				}else ErrorType=1;
			}else ErrorType=2;
		}catch(Throwable t){
			Exception(t);
			ErrorType=3;
		}finally{
		}
		return null;
	}
	// -------------tofucraft(tsuteto)-------------
	public boolean copyResourceToFile(URL src,File dest){
		return copyResourceToFile(src,dest,0);
	}
	public boolean copyResourceToFile(URL src,File dest,int buff){
		InputStream is=null;
		OutputStream os=null;
		if(buff<1) buff=1024;
		try{
			is=src.openStream();
			File pf=dest.getParentFile();
			if(pf!=null) if(!pf.exists()) pf.mkdirs();
			dest.createNewFile();
			os=new FileOutputStream(dest);
			int size;
			byte[] buf=new byte[buff];
			while((size=is.read(buf))>=0){
				os.write(buf,0,size);
			}
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}finally{
			if(is!=null) try{
				is.close();
			}catch(IOException e){
			}
			if(os!=null) try{
				os.close();
			}catch(IOException e){
			}
		}
	}
	public boolean SaveByte(byte[] b,File dest){
		FileOutputStream os=null;
		try{
			os=new FileOutputStream(dest);
			os.write(b);
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}finally{
			if(os!=null) try{
				os.close();
			}catch(IOException e){
			}
		}
	}
	public long FileSize(File f) throws IOException{
		return Files.readAttributes(f.toPath(),BasicFileAttributes.class).size();
	}
	public byte[] ReadByte(File org,int buf){
		FileInputStream is=null;
		ByteArrayOutputStream os=null;
		try{
			is=new FileInputStream(org);
			os=new ByteArrayOutputStream();
			if(buf>8192*4)buf=8192*4;
			else if(buf<256)buf=256;
			byte[] b=new byte[buf];
			int size;
			while((size=is.read(b))!=-1) {
				os.write(b,0,size);
			}
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			if(is!=null) try{
				is.close();
			}catch(IOException e){

			}
			if(os!=null)return os.toByteArray();
		}
		return null;
	}
	public boolean copyFileToFile(File org,File dest){
		try{
			return copyFileToFileT(org,dest);
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	public boolean copyFileToFileT(File org,File dest) throws IOException{
		if(org.isDirectory()){
			File[] buf=org.listFiles();
			for(int i=0;i<buf.length;i++){
				File file=new File(dest.getAbsolutePath()+Dir+buf[i].getName());
				if(buf[i].isDirectory()) file.mkdir();
				copyFileToFileT(buf[i],file);
			}
			return true;
		}
		if(!dest.exists()) dest.createNewFile();
		else{
			dest.delete();
			dest.createNewFile();
		}
		FileInputStream is=null;
		FileOutputStream os=null;
		try{
			is=new FileInputStream(org);
			os=new FileOutputStream(dest);
			FileChannel chInput=null,chOutput=null;
			chInput=is.getChannel();
			chOutput=os.getChannel();
			chInput.transferTo(0,chInput.size(),chOutput);
			return true;
		}finally{
			if(is!=null) try{
				is.close();
			}catch(IOException e){
			}
			if(os!=null) try{
				os.close();
			}catch(IOException e){
			}
		}
	}
	public String getMessageDigest(URL file){
		MessageDigest md;
		try{
			md=MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
		InputStream in=null;
		try{
			in=file.openStream();
			byte[] dat=new byte[1024];
			int len;
			while((len=in.read(dat))>=0){
				md.update(dat,0,len);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(in!=null) try{
				in.close();
			}catch(IOException e){
			}
		}
		return this.digestToString(md);
	}
	public String getMessageDigest(File file){
		MessageDigest md;
		try{
			md=MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
		FileInputStream in=null;
		try{
			in=new FileInputStream(file);
			byte[] dat=new byte[1024];
			int len;
			while((len=in.read(dat))>=0){
				md.update(dat,0,len);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(in!=null) try{
				in.close();
			}catch(IOException e){
			}
		}
		return this.digestToString(md);
	}
	public String digestToString(MessageDigest md){
		byte[] digestBytes=md.digest();
		StringBuilder sb=new StringBuilder();
		for(byte b:digestBytes){
			sb.append(String.format("%02x",b));
		}
		return sb.toString();
	}
	// ---------------tofucrraft-END------------------------
	public void delete(String dir,String FileName){
		delete(new File(dir+this.Dir+FileName+".txt"));
	}
	public void delete(File file){
		try{
			if(file.exists()){
				if(file.delete()){
					// System.out.println("ファイルを削除しました");
				}else{
					System.out.println("ファイルの削除に失敗しました");
				}
			}else{
				System.out.println("ファイルが見つかりません");
			}
		}catch(Throwable t){
			System.out.println("ファイルの削除に失敗しました"+t.toString());
		}
	}
	/**指定されたURLからデータを読み込んで文字として返します*/
	public String ReadFileURL(String s) throws IOException{
		URL url=new URL(s);
		InputStream con=url.openStream();
		String Data=new String(toByteArray(con),DefaultEncode);
		con.close();
		return Data;
	}
	public String[] ReadFileURL0(String s) throws IOException{
		String[] Data=StringToStrings(ReadFileURL(s));
		return Data;
	}

	/**
	 * 指定されたディレクトリ内のファイルを ZIP アーカイブし、指定されたパスに作成します。 デフォルト文字コードは Shift_JIS
	 * ですので、日本語ファイル名も対応できます。
	 *
	 * @param filePath
	 *            圧縮後の出力ファイル名をフルパスで指定 ( 例: C:/sample.zip )
	 * @param directory
	 *            圧縮するディレクトリ ( 例; C:/sample )
	 * @return 処理結果 true:圧縮成功 false:圧縮失敗
	 */
	public boolean compressDirectory(String filePath,String directory,int level){
		File baseFile=new File(filePath);
		File file=new File(directory);
		return compressDirectory(baseFile,file,level);
	}
	/**
	 * @param baseFile
	 *            圧縮後の出力ファイル名をフルパスで指定 ( 例: C:/sample.zip )
	 * @param file
	 *            圧縮するディレクトリ ( 例; C:/sample )
	 * @return 処理結果 true:圧縮成功 false:圧縮失敗
	 */
	public boolean compressDirectory(File baseFile,File file,int level){
		ZipOutputStream outZip=null;
		try{
			// ZIPファイル出力オブジェクト作成
			outZip=new ZipOutputStream(new FileOutputStream(baseFile));
			archive(outZip,baseFile,file,level);
		}catch(Exception e){
			// ZIP圧縮失敗
			return false;
		}finally{
			// ZIPエントリクローズ
			if(outZip!=null){
				try{
					outZip.closeEntry();
				}catch(Exception e){
				}
				try{
					outZip.flush();
				}catch(Exception e){
				}
				try{
					outZip.close();
				}catch(Exception e){
				}
			}
		}
		return true;
	}

	/**
	 * 指定された ArrayList のファイルを ZIP アーカイブし、指定されたパスに作成します。 デフォルト文字コードは UTF-8
	 * ですので、日本語ファイル名も対応できます。
	 *
	 * @param baseFile 圧縮後のファイルを指定 ( 例: C:/sample.zip )
	 * @param fileList 圧縮するファイルリスト ( 例; {C:/sample1.txt, C:/sample2.txt} )
	 * @return 処理結果 true:圧縮成功 false:圧縮失敗
	 * @throws FileNotFoundException
	 */
	public boolean compressFileList(File baseFile,ArrayList<String> fileList,int level,ArrayList<String> entryName)
			throws FileNotFoundException{
		ZipOutputStream outZip=null;
		// File baseFile=new File(filePath);
		try{
			// ZIPファイル出力オブジェクト作成
			outZip=new ZipOutputStream(new FileOutputStream(baseFile));
			// 圧縮ファイルリストのファイルを連続圧縮
			for(int i=0;i<fileList.size();i++){
				// ファイルオブジェクト作成
				File file=new File(fileList.get(i));
				String entry;
				if(entryName.size()>i) entry=entryName.get(i);
				else entry=file.getName();
				archive(outZip,baseFile,file,entry,"UTF-8",level,file.lastModified());
			}
		}finally{
			// ZIPエントリクローズ
			if(outZip!=null){
				try{
					outZip.closeEntry();
				}catch(Exception e){
				}
				try{
					outZip.flush();
				}catch(Exception e){
				}
				try{
					outZip.close();
				}catch(Exception e){
				}
			}
		}
		return true;
	}

	/**
	 * ディレクトリ圧縮のための再帰処理
	 *
	 * @param outZip
	 *            ZipOutputStream
	 * @param baseFile
	 *            File 保存先ファイル
	 * @param targetFile
	 *            File 圧縮したいファイル
	 */
	private void archive(ZipOutputStream outZip,File baseFile,File targetFile,int level){
		if(targetFile.isDirectory()){
			File[] files=targetFile.listFiles();
			for(File f:files){
				if(f.isDirectory()){
					archive(outZip,baseFile,f,level);
				}else{
					if(!f.getAbsoluteFile().equals(baseFile)){
						// 圧縮処理
						archive(outZip,baseFile,f,f.getAbsolutePath().replace(baseFile.getParent(),"").substring(1),
								"UTF-8",level);
					}
				}
			}
		}
	}

	/**
	 * 圧縮処理
	 *
	 * @param outZip
	 *            ZipOutputStream
	 * @param baseFile
	 *            File 保存先ファイル
	 * @param targetFile
	 *            File 圧縮したいファイル
	 * @param entryName 保存ファイル名
	 * @param enc
	 *            文字コード
	 */
	private boolean archive(ZipOutputStream outZip,File baseFile,File targetFile,String entryName,String enc,int level){
		return archive(outZip,baseFile,targetFile,entryName,enc,level,-1);
	}
	private boolean archive(ZipOutputStream outZip,File baseFile,File targetFile,String entryName,String enc,int level,
			long lastModified){
		// 圧縮レベル設定
		outZip.setLevel(level<10&&level>0?level:5);
		// 文字コードを指定
		// outZip.setEncoding(enc);
		try{
			ZipEntry e=new ZipEntry(entryName);
			System.out.println("AddZipEntry="+entryName);
			// SimpleDateFormat sdf=new SimpleDateFormat("
			// 最終更新日yyyy年MM月dd日HH時mm分ss秒");
			// System.out.println(targetFile.getAbsolutePath()+sdf.format(new
			// Date(targetFile.lastModified())));
			if(lastModified>0)e.setTime(lastModified);
			// ZIPエントリ作成
			outZip.putNextEntry(e);
			// 圧縮ファイル読み込みストリーム取得
			BufferedInputStream in=new BufferedInputStream(new FileInputStream(targetFile));
			all+=targetFile.length();
			// 圧縮ファイルをZIPファイルに出力
			int readSize=0;
			byte buffer[]=new byte[1024]; // 読み込みバッファ
			while((readSize=in.read(buffer,0,buffer.length))!=-1){
				outZip.write(buffer,0,readSize);
				load+=readSize;
			}
			// クローズ処理
			in.close();
			// ZIPエントリクローズ
			outZip.closeEntry();
		}catch(Exception e){
			// ZIP圧縮失敗
			return false;
		}
		return true;
	}
	public boolean deleteDir(File f){
		if(f.isDirectory()){
			for(File buf:f.listFiles()){
				deleteDir(buf);
			}
			f.delete();
		}else f.delete();
		return false;
	}
	public void decode(File aZipFile,String aOutDir){
		decode(aZipFile,aOutDir,256,false);
	}
	/**
	 * Zipファイルを展開します
	 *
	 * @param aZipFile
	 *            zipファイル
	 * @param aOutDir
	 *            出力先ディレクトリ
	 */
	public void decode(File aZipFile,String aOutDir,int buff){
		decode(aZipFile,aOutDir,buff,false);
	}
	public void decode(File aZipFile,String aOutDir,int buff,boolean del){
		if(buff<1) buff=512;
		FileInputStream fileIn=null;
		FileOutputStream fileOut=null;
		try{
			// -------------------------------
			// 出力先ディレクトリを作る
			// -------------------------------
			File outDir=new File(aOutDir);
			outDir.mkdirs();
			// -------------------------------
			// zipファイルをオープンする
			// -------------------------------
			fileIn=new FileInputStream(aZipFile);
			ZipInputStream zipIn=new ZipInputStream(fileIn);
			ZipEntry entry=null;
			while((entry=zipIn.getNextEntry())!=null){
				if(entry.isDirectory()){
					// ------------------------------
					// ディレクトリだった場合は、
					// 出力先ディレクトリを作成する
					// ------------------------------
					String relativePath=entry.getName();
					outDir=new File(outDir,relativePath);
					outDir.mkdirs();
					if(LogLevel<=Level_All){
						System.out.println("ディレクトリ作成　"+outDir.getAbsolutePath());
					}
				}else{
					// ------------------------------
					// ファイルの場合は出力する
					// 出力先は、現在の outDirの下
					// ------------------------------
					String relativePath=entry.getName();
					File outFile=new File(outDir,relativePath);
					// 出力先のディレクトリを作成する
					File parentFile=outFile.getParentFile();
					parentFile.mkdirs();
					if(LogLevel<=Level_All){
						System.out.println("ファイル作成　"+parentFile.getAbsolutePath());
					}
					if(del) outFile.delete();
					// SimpleDateFormat sdf=new SimpleDateFormat("
					// 最終更新日yyyy年MM月dd日HH時mm分ss秒");
					// System.out.println(outFile.getAbsolutePath()+sdf.format(new
					// Date(entry.getLastModifiedTime().toMillis())));
					// ファイルを出力する
					fileOut=new FileOutputStream(outFile);
					byte[] buf=new byte[buff];
					int size=0;
					while((size=zipIn.read(buf))>0){
						fileOut.write(buf,0,size);
					}
					fileOut.close();
					outFile.setLastModified(entry.getTime());
					//System.out.println("decode&"+relativePath+"&Time="+entry.getTime());
					if(LogLevel<=Level_All){
						System.out.println("ファイル書き込み完了　"+parentFile.getAbsolutePath());
					}
					fileOut=null;
				}
				zipIn.closeEntry();
			}
			try{
				fileIn.close();
			}catch(Exception e){
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void SaveImage(File file,Image img,long sleep){
		try{
			ImageIO.write((RenderedImage)img,"png",file);
			Thread.sleep(sleep);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	/**
	 * java.beans.XMLEncoderを使用し、オブジェクト[object]を、 指定したパス[path]に、XMLファイルとして保存します。
	 * ※オブジェクト[object]がJavaBeansの慣例に適合している場合、 プライベートなフィールドのデータも保存できます。
	 * ※保存したXMLファイルはjava.beans.XMLDecoderで復元する事ができます。
	 *
	 * @param path
	 *            オブジェクトを保存するパス。 存在しない場合可能であれば作成します。
	 * @param object
	 *            保存するオブジェクト。
	 * @throws FileNotFoundException
	 *             指定されたパス名で示されるファイルが開けなかった場合
	 */
	public static synchronized void writeXML(String path,Object object)
			throws IOException{
		XMLEncoder enc=null;
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		FileOutputStream fileStream=null;
		try{
			// 先にメモリ中にXMLデータを出力する
			enc=new XMLEncoder(out);
			enc.writeObject(object);
			enc.close(); // close()の中でflush()が呼ばれる。
			byte[] xmlbuff=out.toByteArray();
			// バイトデータをファイルに出力
			fileStream=new FileOutputStream(path);
			fileStream.write(xmlbuff);
			fileStream.flush();
		}finally{
			if(enc!=null){
				enc.close();
			}
			if(out!=null){
				out.close();
			}
			if(fileStream!=null){
				fileStream.close();
			}
		}
	}
	/**
	 * 指定したパス[path]のXMLファイルから、オブジェクトを復元します。
	 * ※XMLファイルはjava.beans.XMLEncoderで保存している必要があります。
	 *
	 * @param path
	 *            オブジェクトが保存されているパス。
	 * @throws FileNotFoundException
	 *             指定されたパス名で示されるファイルが存在しない場合
	 */
	public static Object readXML(String path) throws FileNotFoundException{
		XMLDecoder d=null;
		try{
			d=new XMLDecoder(new BufferedInputStream(
					new FileInputStream(path)));
			return d.readObject();
		}finally{
			if(d!=null){
				d.close();
			}
		}
	}
	public static void SaveObjectT(File f,Object object) throws FileNotFoundException,IOException{
		if(f.exists()) f.delete();
		ObjectOutputStream objOutStream=new ObjectOutputStream(new FileOutputStream(f));
		objOutStream.writeObject(object);
		objOutStream.close();
	}
	public static void SaveObject(File f,Object object){
		try{
			SaveObjectT(f,object);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static Object ReadObjectT(File f) throws FileNotFoundException,IOException,ClassNotFoundException{
		ObjectInputStream objInStream=new ObjectInputStream(new FileInputStream(f));
		Object object=objInStream.readObject();
		objInStream.close();
		return object;
	}
	public static Object ReadObject(File f){
		try{
			return ReadObjectT(f);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static String[] b(Throwable t){
		return ThrowableToStrings(t);
	}
	public static String[] ThrowableToStrings(Throwable t){
		return FileEditor.FE.StringToStrings(ThrowableToString(t));
	}
	public static String ThrowableToString(Throwable t){
		StringWriter stringwriter=new StringWriter();
		PrintWriter printwriter=new PrintWriter(stringwriter);
		t.printStackTrace(printwriter);
		return stringwriter.toString();
	}
	public byte[] ReadZip(File zip,String filename){
		byte[] b=null;
		try{
			b=ReadZipT(zip,filename);
		}catch(IOException e){
			e.printStackTrace();
		}
		return b;
	}
	public byte[] ReadZipT(File zip,String filename) throws IOException{
		ZipFile zf=new ZipFile(zip);
		ZipEntry zipentry=zf.getEntry(filename+".txt");
		int length=(int)zipentry.getSize();
		InputStream is=zf.getInputStream(zipentry);
		byte[] read=new byte[length];
		is.read(read);
		is.close();
		zf.close();
		return read;
	}
	private static String line;
	/**改行文字を取得*/
	public static String Line(){
		if(line==null) line=System.getProperty("line.separator");
		return line;
	}
	public static void createLink(File link,File existing){
		try{
			Files.createLink(link.toPath(),existing.toPath());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void createLinkT(File link,File existing) throws IOException{
		Files.createLink(link.toPath(),existing.toPath());
	}
	public static boolean checkReadAndWrite(File f){
		return Files.isReadable(f.toPath())&&Files.isWritable(f.toPath());
	}
	public static long creationTime(File f) {
		try{
			return creationTimeFT(f).toMillis();
		}catch(IOException e){
			e.printStackTrace();
		}
		return 0;
	}
	public static FileTime creationTimeFT(File f) throws IOException {
		BasicFileAttributes attributes = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
		return attributes.creationTime();
	}
	public String[] readStream0(InputStream is){
		try{
			return readStream0T(is);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public String[] readStream0T(InputStream is) throws IOException{
		InputStreamReader isr=new InputStreamReader(is,DefaultEncode);
		BufferedReader br=new BufferedReader(isr);
		ArrayList<String> list=new ArrayList<String>();
		String line;
		while(br.ready()) {
			line=br.readLine();
			if(line==null)break;
			list.add(line);
		}
		return list.toArray(new String[list.size()]);
	}
	public String readStream(InputStream is) {
		try{
			return readStreamT(is);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public String readStreamT(InputStream is) throws IOException {
		return new String(toByteArray(is),DefaultEncode);
	}
}