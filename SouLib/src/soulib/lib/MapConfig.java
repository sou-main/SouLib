package soulib.lib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**従来のConfigFileの置き換え用で従来よりも軽量です。
 * Mapは標準ではHashMapが使用されます。変更するにはmakeMap()をOverrideしてください。*/
public class MapConfig extends ConfigBase{

	public String keyOnlyValue="true";
	public Map<String,String> map=null;
	public Charset defaultCharset=StandardCharsets.UTF_8;
	/**変更があったか*/
	private boolean mod;
	{
		if(map==null)map=makeMap();
	}
	public Map<String,String> makeMap(){
		return new HashMap<String,String>();
		//return new ListMap<String,String>();//順序を固定したいとき
	}
	public MapConfig(File fi){
		read(fi);
	}
	public MapConfig(String[] s) {
		read(s);
	}
	public MapConfig(Map<String,String> map){
		this.map=map;
	}
	/**後で読み込む必要がある*/
	public MapConfig(){

	}
	public boolean read(File f) {
		return read(f,null);
	}
	public boolean read(File f,Charset c) {
		if(f==null)return false;
		file=f.getAbsolutePath();
		if(!f.isFile())return false;
		//read(f.ReadFile0(f));
		FileInputStream fis=null;
		try{
			read(fis=new FileInputStream(f),c);
		}catch(Exception e){
			System.err.println("MapConfigReadFileError(ClearMap)");
			e.printStackTrace();
			map.clear();
			return true;
		}finally {
			if(fis!=null) try{
				fis.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return false;
	}
	public void read(String[] s) {
		if(s==null)return;
		for(int i=0;i<s.length;i++) {
			readLine(s[i]);
		}
	}
	/**@param is 入力
	 * @param c nullでも可*/
	public void read(InputStream is,Charset c) throws IOException {
		if(c==null)c=defaultCharset;
		read(new InputStreamReader(is,c));
	}
	public void read(Reader r) throws IOException {
		BufferedReader br=new BufferedReader(r);
		String read=br.readLine();
		if(read==null)return;
		if(read.charAt(0)==0xfeff)read=read.substring(1);
		readLine(read);
		while(br.ready()) {
			read=br.readLine();
			if(read==null)break;
			readLine(read);
		}
	}
	public void readLine(String s) {
		int index=s.indexOf('#');
		String[] spr;
		if(index>=0) {
			if(index==0)return;
			else spr=s.substring(0,index).split(Pattern.quote(Kugiri));
		}else spr=s.split(Pattern.quote(Kugiri));
		if(spr.length<1)return;
		if(spr.length<2) {
			map.put(spr[0],keyOnlyValue);
			return;
		}
		StringBuffer sb=new StringBuffer();
		if(spr[0].length()==1&&spr.length>2) {
			for(int j=2;j<spr.length;j++) {
				if(j!=2)sb.append(":");
				sb.append(spr[j]);
			}
			map.put(spr[1],sb.toString());
		}else {
			for(int j=1;j<spr.length;j++) {
				if(j!=1)sb.append(":");
				sb.append(spr[j]);
			}
			map.put(spr[0],sb.toString());
		}
	}
	@Override
	public boolean isMod(){
		return mod;
	}
	private PrintStream toList(Map<String,String> map,PrintStream ps) {
		if(ps==null)return null;
		for (Map.Entry<String,String> entry : map.entrySet()) {
			ps.println(entry.getKey()+Kugiri+entry.getValue());
		}
		return ps;
	}
	@Override
	public boolean saveConfig(File file){
		if(!mod)return false;
		FileOutputStream fos=null;
		PrintStream ps=null;
		try{
			if(file==null) {
				if(this.file==null)return false;
				file=new File(this.file);
			}
			fos=new FileOutputStream(file);
			ps=new PrintStream(fos,true,StandardCharsets.UTF_8.name());
			toList(map,ps).close();
			return !ps.checkError();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(fos!=null)try{
				fos.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			if(ps!=null)ps.close();
		}
		return false;
	}
	public boolean saveConfig(File file,boolean modCheck){
		if(modCheck&&!mod)return false;
		try{
			if(file==null)file=new File(this.file);
			FileOutputStream fos=new FileOutputStream(file);
			toList(map,new PrintStream(fos,true,StandardCharsets.UTF_8.name())).close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public String toString() {
		try{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			PrintStream ps=new PrintStream(bos,true,StandardCharsets.UTF_8.name());
			toList(map,ps).close();
			return bos.toString(StandardCharsets.UTF_8.name());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public void write(OutputStream s) {
		write(s,true);
	}
	public void write(OutputStream s,boolean autoFlush) {
		if(s instanceof PrintStream)write((PrintStream) s);
		else try{
			write(new PrintStream(s,autoFlush,StandardCharsets.UTF_8.name()));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}
	public void write(PrintStream ps) {
		toList(map,ps);
		ps.flush();
	}
	@Override
	public String getConfigDataString(String s,String DD){
		if(map.containsKey(s))return map.get(s);
		else return DD;
	}
	@Override
	public boolean setConfigDataString(String s,String Data){
		if(Data!=null&&Data.equals(map.get(s)))return false;//同じデータを登録しようとした場合
		if(notNewConfig) {
			if(map.containsKey(s))map.put(s,Data);
		}else map.put(s,Data);
		mod=true;
		return true;
	}
	@Override
	public void remove(String cate){
		map.remove(cate);
	}
	public boolean contains(String key) {
		return map.containsKey(key);
	}
	public ArrayList<String> getAllKeys(){
		return getAllKeys(map);
	}
	public static <K,V> ArrayList<K> getAllKeys(Map<K,V> map){
		ArrayList<K> list=new ArrayList<K>(map.size());
		for (Map.Entry<K,V> entry : map.entrySet()) {
			list.add(entry.getKey());
		}
		return list;
	}
}