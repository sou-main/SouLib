package soulib.fileLib;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class ClearByteArrayOutputStream extends ByteArrayOutputStream{
	public ClearByteArrayOutputStream(int size){
		super(size);
	}
	public ClearByteArrayOutputStream(){
		
	}
	public void clear(int size){
		if(size<1)size=32;
		buf=new byte[size];
		reset();
	}
	public boolean reSize(int newLength) {
		if(newLength<buf.length)return false;
		buf = Arrays.copyOf(buf,newLength);
		return true;
	}
}