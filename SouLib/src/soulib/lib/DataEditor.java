package soulib.lib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import soulib.fileLib.StringPrintStream;

public class DataEditor{
	private static DecimalFormat fomat;
	private static String[] si= {"K","M","G","T","P","E","Z","Y"};

	public static String[] Gyaku(String[] s){
		String[] t=new String[s.length];
		int j=0;
		for(int i=s.length;i>0;i--){
			t[i-1]=s[j];
			j++;
		}
		return t;
	}
	public static String[] cut(String[] Data){
		ArrayList<String> al=new ArrayList<String>(1);
		for(int i=0;i<Data.length;i++){
			if(Data[i]!=null)al.add(Data[i]);
		}
		String[] out=new String[al.size()];
		for(int i=0;i<al.size();i++){
			out[i]=al.get(i);
		}
		return out;
	}
	public static <T> T[] cut(T[] Data){
		ArrayList<T> al=new ArrayList<T>(1);
		for(int i=0;i<Data.length;i++){
			if(Data[i]!=null)al.add(Data[i]);
		}
		@SuppressWarnings("unchecked")
		T[] out=(T[]) Array.newInstance(Data.getClass().getComponentType(), Data.length);
		for(int i=0;i<al.size();i++){
			out[i]=al.get(i);
		}
		return out;
	}
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}
	public static String[] addStringSimple(String[] s,String[] code){
		int lastnotnull=getLastNotNull(s);
		for(int i=0;i<code.length;i++){
			int id = lastnotnull+1+i;
			if(lastnotnull<0) {
				lastnotnull=getLastNotNull(s)+1;
				id=0;
			}
			s[id]=code[i];
		}
		return s;
	}
	public static int getFirstNull(Object[] o) {
		for(int i=0;i<o.length;i++){
			if(o[i]!=null) {
				return i;
			}
		}
		return -1;
	}
	public static int getLastNotNull(Object[] o) {
		int lastnotnull=-1;
		for(int i=0;i<o.length;i++){
			if(o[i]!=null)lastnotnull=i;
		}
		return lastnotnull;
	}
	public static String[] addString(String[] s,String[] code){
		return cut(addStringSimple(copy(s,s.length+code.length), code));
	}
	public static String[] copy(String[] s){
		return copy(s,s.length);
	}
	public static String[] copy(String[] s, int j){
		String[] r=new String[j];
		for(int i=0;i<s.length;i++){
			r[i]=s[i];
		}
		return r;
	}
	public static long copy(InputStream from, OutputStream to)throws IOException {
		checkNotNull(from);
		checkNotNull(to);
		byte[] buf = new byte[0x1000];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}
	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}
	/**オブジェクトをシリアライズし、byteの配列を得る**/
	public static byte[] toByte(Object obj) {
		ByteArrayOutputStream bos = null;
		try{
			//オブジェクトをシリアライズし、バイト配列にする
			bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			byte b[] = bos.toByteArray();
			return b;
		}catch(Throwable th){
			th.printStackTrace();
			return null;
		}finally{
			if(bos!=null){
				try{
					bos.close();
				}catch(IOException e){

				}
				bos = null;
			}
		}

	}
	/**byte配列からオブジェクトに復元する**/
	public static Object toObject(byte b[]) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
			return ois.readObject();
		} catch (Exception e) {
			return null;
		}
	}
	/**オブジェクトをシリアライズし、zip圧縮したbyteの配列を得る**/
	public static byte[] toZipByte(Object obj) {
		if(obj instanceof Serializable);
		else return null;
		ByteArrayOutputStream bos = null;
		GZIPOutputStream gos = null;
		try{
			//オブジェクトをシリアライズし、バイト配列にする
			bos = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(bos);
			ObjectOutputStream oos = new ObjectOutputStream(gos);
			oos.writeObject(obj);
			gos.finish();
			return bos.toByteArray();
		}catch (Throwable th){
			th.printStackTrace();
			return null;
		}finally{
			if (bos!=null){
				try{
					bos.close();
				}catch(IOException e){

				}
				bos = null;
			}
			if(gos!=null){
				try{
					gos.close();
				}catch(IOException e){

				}
				gos = null;
			}
		}
	}
	/**zipのbyte配列からオブジェクトに復元する**/
	public static Object toObjectZip(byte b[]) {
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		GZIPInputStream gis = null;
		try{
			bis = new ByteArrayInputStream(b);
			gis = new GZIPInputStream(bis);
			bos = new ByteArrayOutputStream();
			int length = 0;
			byte[] tmp = new byte[4096];
			while((length = gis.read(tmp, 0, tmp.length)) != -1){
				bos.write(tmp, 0, length);
			}
			byte[] bobj = bos.toByteArray();
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bobj));
			return ois.readObject();
		}catch (Exception e) {
			return null;
		}finally{
			if (bis!=null){
				try{
					bis.close();
				}catch(Exception e){

				}
				bis = null;
			}
			if(bos!=null){
				try{
					bos.close();
				}catch(Exception e){

				}
				bos = null;
			}
			if(gis!=null){
				try{
					gis.close();
				}catch(Exception e){

				}
				gis = null;
			}
		}
	}
	public static String[] ToStrings(ArrayList<String> bac){
		String[] r=new String[bac.size()];
		for(int i=0;i<r.length;i++)r[i]=bac.get(i);
		return r;
	}
	public static String[] ListToStrings(List<?> bac){
		String[] r=new String[bac.size()];
		for(int i=0;i<r.length;i++)r[i]=bac.get(i).toString();
		return r;
	}
	public static void printThread(){
		printThread("PrintThread",System.err);
	}
	public static void printThread(String string){
		printThread(string,System.err);
	}
	public static void printThread(String string,Appendable sb){
		StringPrintStream sps=StringPrintStream.makeStringPrintStream();
		printThread(string,sps);
		try{
			sb.append(sps);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void printThread(String string,PrintStream ps){
		try{
			throw new Error(string);
		}catch(Error e) {
			e.printStackTrace(ps);
		}
	}
	public static byte[] GZipToByte(byte[] zip) throws IOException{
		ByteArrayInputStream bis=new ByteArrayInputStream(zip);
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		byte[] buf=new byte[1024];
		GZIPInputStream in=new GZIPInputStream(bis);
		int size;
		while((size=in.read(buf,0,buf.length))!=-1){
			out.write(buf,0,size);
		}
		in.close();
		return out.toByteArray();
	}
	public static byte[] ByteToGZip(byte[] raw) throws IOException{
		ByteArrayOutputStream out0=new ByteArrayOutputStream();
		GZIPOutputStream out=new GZIPOutputStream(out0);
		ByteArrayInputStream in=new ByteArrayInputStream(raw);
		byte[] buf=new byte[1024];
		int size;
		while((size=in.read(buf,0,buf.length))!=-1){
			out.write(buf,0,size);
		}
		out.finish();
		out.close();
		return out0.toByteArray();
	}
	/**
	 * バイト列を16進数文字列に変換する.
	 *
	 * @param data バイト列
	 * @return 16進数文字列
	 */
	public static String toHexString(byte[] data){
		StringBuilder buf=new StringBuilder();
		for(byte d:data){
			buf.append(String.format("%02X",d));
		}
		return buf.toString();
	}
	public static <T extends Appendable> T formatComma(long num,T a){
		if(fomat==null)fomat=new DecimalFormat("#,##0.##");
		try{
			a.append(fomat.format(num));
		}catch(IOException e){
			throw new UncheckedIOException(e);
		}
		return a;
	}
	/**@param a 追加する対象*/
	public static <T extends Appendable> T format(long num,T a){
		return format(num,a,1000);
	}
	/**@param a 追加する対象*/
	public static <T extends Appendable> T format(long num,T a,int base){
		if(fomat==null)fomat=new DecimalFormat("#,##0.##");
		try{
			for(int i=si.length-1;i>=0;i--) {
				if(num>=Math.pow(base,i+1)) {
					a.append(fomat.format(num/Math.pow(base,i+1))).append(si[i]);
					return a;
				}
			}
			a.append(fomat.format(num));
			/*
			if(num>=Math.pow(base,4))a.append(fomat.format(num/Math.pow(base,4))).append("T");
			else if(num>=Math.pow(base,3))a.append(fomat.format(num/Math.pow(base,3))).append("G");
			else if(num>=Math.pow(base,2))a.append(fomat.format(num/Math.pow(base,2))).append("M");
			else if(num>=Math.pow(base,1))a.append(fomat.format(num/Math.pow(base,1))).append("K");
			else a.append(fomat.format(num));
			*/
		}catch(IOException e){
			throw new UncheckedIOException(e);
		}
		return a;
	}
	public static String base64Encode(byte[] raw) throws IOException {
		return Base64.getEncoder().encodeToString(raw);
	}
	public static void base64Encode(InputStream raw,OutputStream to) throws IOException {
		//StandardCharsets.ISO_8859_1
		Encoder e=Base64.getEncoder();
		OutputStream w=e.wrap(to);
		byte[] buf=new byte[24];
		int size;
		while((size=raw.read(buf))>0) {
			w.write(buf,0,size);
		}
	}
	public static void base64Encode(InputStream raw,Writer to) throws IOException {
		Encoder e=Base64.getEncoder();
		ByteBuffer buf=ByteBuffer.allocate(24);
		if(!buf.hasArray())throw new IOException("hasArray=false");
		while(raw.read(buf.array())>0) {
			ByteBuffer ret=e.encode(buf);
			to.write(new String(ret.array(),0,ret.remaining(),StandardCharsets.ISO_8859_1));
		}
	}
	public static byte[] base64Decode(String raw) throws IOException {
		return Base64.getDecoder().decode(raw);
	}
	public static void base64Decode(InputStream raw,OutputStream to) throws IOException {
		//StandardCharsets.ISO_8859_1
		Decoder e=Base64.getDecoder();
		InputStream i=e.wrap(raw);
		byte[] buf=new byte[24];
		int size;
		while((size=i.read(buf))>0) {
			to.write(buf,0,size);
		}
	}
	public static void base64Decode(Reader raw,OutputStream to) throws IOException {
		Decoder e=Base64.getDecoder();
		CharBuffer buf=CharBuffer.allocate(24);
		if(!buf.hasArray())throw new IOException("hasArray=false");
		while(raw.read(buf.array())>0) {
			to.write(e.decode(buf.toString()));
		}
	}
	/**失敗したらそのまま帰ってくる*/
	public static String GZIPbase64Decode(String raw){
		try{
			return GZIPbase64Decode(raw,null);
		}catch(IOException e){
			e.printStackTrace();
		}
		return raw;
	}
	public static String GZIPbase64Decode(String raw,Charset c) throws IOException,UnsupportedEncodingException{
		ByteArrayInputStream in=new ByteArrayInputStream(raw.getBytes(StandardCharsets.ISO_8859_1));
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		GZIPbase64Decode(in,out);
		return out.toString(c!=null?c.name():"UTF-8");
	}
	public static void GZIPbase64Decode(InputStream raw,OutputStream to) throws IOException {
		//StandardCharsets.ISO_8859_1
		Decoder e=Base64.getDecoder();
		InputStream i=e.wrap(new GZIPInputStream(raw));
		byte[] buf=new byte[24];
		int size;
		while((size=i.read(buf))>0) {
			to.write(buf,0,size);
		}
	}
	/**失敗したらそのまま帰ってくる*/
	public static String GZIPbase64Encode(String raw){
		try{
			return GZIPbase64Encode(raw,null);
		}catch(IOException e){
			e.printStackTrace();
		}
		return raw;
	}
	/**@param r 元のデータをバイト配列に変換する時のエンコード*/
	public static String GZIPbase64Encode(String raw,Charset r) throws IOException,UnsupportedEncodingException{
		ByteArrayInputStream in=new ByteArrayInputStream(raw.getBytes(r!=null?r:StandardCharsets.UTF_8));
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		GZIPbase64Encode(in,out);
		return out.toString(StandardCharsets.ISO_8859_1.name());
	}
	public static void GZIPbase64Encode(InputStream raw,OutputStream to) throws IOException {
		//StandardCharsets.ISO_8859_1
		Encoder e=Base64.getEncoder();
		GZIPOutputStream w=new GZIPOutputStream(e.wrap(to));
		byte[] buf=new byte[24];
		int size;
		while((size=raw.read(buf))>0) {
			w.write(buf,0,size);
		}
		w.finish();
	}
	public static boolean contains(Object o,Object[] array){
		try {
			return index(o, array)>=0;
		}catch(NullPointerException npe) {
			
		}
		return false;
	}
	public static int index(Object o,Object[] array){
		if(array==null)throw new NullPointerException();
		for(int i=0;i<array.length;i++) {
			if(o==null) {
				if(array[i]==null)return i;
			}else if(o.equals(array[i]))return i;
		}
		return -1;
	}
	public static BufferedImage toBufferedImage(Image img){
		return toBufferedImage(img,0,0);
	}
	public static BufferedImage toBufferedImage(Image img,int type){
		return toBufferedImage(img,0,0,type);
	}
	public static BufferedImage toBufferedImage(Image img,int width,int height){
		return toBufferedImage(img,width,height,BufferedImage.TYPE_4BYTE_ABGR);
	}
	//BufferedImage.TYPE_3BYTE_BGR
	public static BufferedImage toBufferedImage(Image img,int width,int height,int type){
		if(width<1)width=img.getWidth(null);
		if(height<1)height=img.getHeight(null);
		BufferedImage bi=new BufferedImage(width,height,type);
		return toBufferedImage(img,bi,width,height);
	}
	public static BufferedImage toBufferedImage(Image img,BufferedImage to,int width,int height){
		Graphics2D g=to.createGraphics();
		g.setColor(new Color(0,0,0,0));
		g.drawRect(0,0,width,height);
		g.drawImage(img,0,0,width,height,null);
		g.dispose();
		return to;
	}
	public static byte[] toByteArray(int i){
		byte[] b=new byte[4];
		b[0]=(byte) ((i>>>24)&0xFF);
		b[1]=(byte) ((i>>>16)&0xFF);
		b[2]=(byte) ((i>>>8)&0xFF);
		b[3]=(byte) ((i>>>0)&0xFF);
		return b;
	}
	public static void writeInt(OutputStream out,int v) throws IOException{
		out.write((v>>>24)&0xFF);
		out.write((v>>>16)&0xFF);
		out.write((v>>>8)&0xFF);
		out.write((v>>>0)&0xFF);
	}
	public static int toInt(byte[] b) throws IOException{
		int ch1=b[0]+128;
		int ch2=b[1]+128;
		int ch3=b[2]+128;
		int ch4=b[3]+128;
		return((ch1<<24)+(ch2<<16)+(ch3<<8)+(ch4<<0));
	}
	public static int readInt(InputStream in) throws IOException{
		int ch1=in.read();
		int ch2=in.read();
		int ch3=in.read();
		int ch4=in.read();
		if((ch1|ch2|ch3|ch4)<0)
			throw new EOFException();
		return((ch1<<24)+(ch2<<16)+(ch3<<8)+(ch4<<0));
	}
	public static String replaceHTML(String s){
		StringReader r=new StringReader(s);
		StringWriter w=new StringWriter();
		BufferedWriter bw=new BufferedWriter(w);
		try{
			replaceHTML(r,bw);
			bw.flush();
			return w.toString();
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public static void BufferedReplaceHTML(Reader r,Writer w) throws IOException {
		w=new BufferedWriter(w);
		r=new BufferedReader(r);
		replaceHTML(r,w);
		w.flush();
	}
	private static final char[] amp="&amp;".toCharArray();//&
	private static final char[] quot="&quot;".toCharArray();//"
	private static final char[] sq="&#39;".toCharArray();//'
	private static final char[] lt="&lt;".toCharArray();//<
	private static final char[] gt="&gt;".toCharArray();//>
	/**バッファリング推奨*/
	public static void replaceHTML(Reader r,Writer w) throws IOException{
		/*
		x=x.replaceAll(Matcher.quoteReplacement("&"),"&amp;");
		x=x.replaceAll(Matcher.quoteReplacement("\""),"&quot;");
		x=x.replaceAll(Matcher.quoteReplacement("'"),"&#39;");
		x=x.replaceAll(Matcher.quoteReplacement("<"),"&lt;");
		x=x.replaceAll(Matcher.quoteReplacement(">"),"&gt;");
		*/
		char[] cbuf=new char[512];
		int size;
		while(r.ready()) {
			size=r.read(cbuf);
			if(size<0)break;
			for(int i=0;i<size;i++) {
				switch(cbuf[i]) {
					case '&':
						w.write(amp);
						break;
					case '"':
						w.write(quot);
						break;
					case '\'':
						w.write(sq);
						break;
					case '<':
						w.write(lt);
						break;
					case '>':
						w.write(gt);
						break;
					default:
						w.write(cbuf[i]);
						break;
				}
			}
		}
	}
	/**数字のみ認識*/
	public static double parseNumber(String size,double miss){
		try {
			return parseNumber(size);
		}catch(NumberFormatException nfe) {
			return miss;
		}
	}
	/**数字のみ認識*/
	public static double parseNumber(String size) throws NumberFormatException{
		Matcher m=Pattern.compile("[0-9]+").matcher(size);
		m.reset();
		boolean result=m.find();
		if(result) return Double.parseDouble(m.group());
		throw new NumberFormatException("数値が含まれていません");
	}
}
