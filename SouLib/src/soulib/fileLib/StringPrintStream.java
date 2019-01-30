package soulib.fileLib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringPrintStream extends PrintStream implements CharSequence{

	protected Charset cs;

	public StringPrintStream(Charset c) throws UnsupportedEncodingException{
		this(new ClearByteArrayOutputStream(),c);
	}
	public StringPrintStream(OutputStream out,Charset c) throws UnsupportedEncodingException{
		super(out,false,c.name());
		cs=c;
	}
	public StringPrintStream() throws UnsupportedEncodingException{
		this(new ClearByteArrayOutputStream(),StandardCharsets.UTF_8);
	}
	public StringPrintStream(int size) throws UnsupportedEncodingException{
		this(new ClearByteArrayOutputStream(size),StandardCharsets.UTF_8);
	}
	public static StringPrintStream makeStringPrintStream(int size){
		try{
			return new StringPrintStream(size);
		}catch(Exception e){
			// ここは実行されないはず
		}
		return null;
	}
	public static StringPrintStream makeStringPrintStream(){
		try{
			return new StringPrintStream();
		}catch(Exception e){
			// ここは実行されないはず
		}
		return null;// nullは返さないはず
	}
	public static StringPrintStream makeStringPrintStream(OutputStream out){
		try{
			return new StringPrintStream(out,StandardCharsets.UTF_8);
		}catch(Exception e){
			// ここは実行されないはず
		}
		return null;// nullは返さないはず
	}
	/** 現在の文字を返す。失敗したらnull */
	@Override
	public String toString(){
		try{
			if(out instanceof ByteArrayOutputStream){
				return new String(((ByteArrayOutputStream) out).toByteArray(),cs);
			}else System.err.println("StringPrintStream-toString-NotByteArrayOutputStream");
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/** 現在の生のbyteを返す。失敗したらnull */
	public byte[] toByteArray(){
		try{
			return ((ByteArrayOutputStream) out).toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public boolean reSize(int newLength) {
		if(out instanceof ClearByteArrayOutputStream)return ((ClearByteArrayOutputStream) out).reSize(newLength);
		return false;
	}
	public OutputStream getOutputStream(){
		return out;
	}
	public Charset getCharset(){
		return cs;
	}
	@Override
	public int length(){
		return getCharArray().length;
	}
	@Override
	public char charAt(int index){
		return getCharArray()[index];
	}
	@Override
	public CharSequence subSequence(int start,int end){
		char[] arr=getCharArray();
		if(start<0)throw new StringIndexOutOfBoundsException(start);
		if(end>arr.length)throw new StringIndexOutOfBoundsException(end);
		if(start>end)throw new StringIndexOutOfBoundsException(end-start);
		return new String(arr,start,end-start);
	}
	private char[] array;
	private byte[] raw;
	public char[] getCharArray(){
		byte[] b=toByteArray();
		if(!Arrays.equals(b,raw)){
			raw=b;
			array=new String(b,cs).toCharArray();
		}
		return array;
	}
	public void clear() throws IOException{
		if(out instanceof ClearByteArrayOutputStream) {
			((ClearByteArrayOutputStream)out).clear(32);
		}else out.flush();
	}
	public void close() {
		if(!(out instanceof ByteArrayOutputStream))super.close();
	}
}
