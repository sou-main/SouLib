package soulib.angou;

import java.io.EOFException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

public class KeyAngouOutputStream extends FilterOutputStream{

	private KeyAngou a;
	private byte[] buf;
	private int index;
	public KeyAngouOutputStream(KeyAngou a,OutputStream os){
		super(os);
		this.a=a;
		setBufferSize(512);
	}
	public KeyAngouOutputStream(KeyAngou a,int bufSize,OutputStream os){
		super(os);
		this.a=a;
		setBufferSize(bufSize);
	}
	public void setBufferSize(int size){
		buf=new byte[size];
	}
	@Override
	public void write(int b) throws IOException{
		index++;
		if(index>buf.length){
			write(buf);
			index=0;
		}
		buf[index]=(byte)b;
	}
	@Override
	public void flush() throws IOException{
		write(buf,0,index+1);
	}
	@Override
	public void write(byte b[],int off,int len) throws IOException{
		byte[] copy=new byte[len];
		System.arraycopy(b,off,copy,0,Math.min(b.length,len));
		try{
			write0(copy,buf.length);
		}catch(GeneralSecurityException e){
			throw new IOException(e);
		}
	}
	private void write0(byte[] data,int maxSize) throws IOException,GeneralSecurityException{
		if(data.length>maxSize){
			int last=0;
			byte[] buf=new byte[maxSize];
			while(true){
				int j=0;
				int end=last+maxSize;
				if(end>data.length){
					end=data.length;
					buf=new byte[end-last];
				}
				for(int i=last;i<end;i++){
					buf[j]=data[i];
					j++;
				}
				out.write(a.angou(buf));
				if(buf.length<maxSize) break;
				last+=maxSize;
			}
		}else out.write(data);
		writeInt(out,-1);
	}
	@Override
	public void write(byte b[]) throws IOException{
		write(b,0,b.length);
	}
	public static void writeInt(OutputStream out,int v) throws IOException{
		out.write((v>>>24)&0xFF);
		out.write((v>>>16)&0xFF);
		out.write((v>>>8)&0xFF);
		out.write((v>>>0)&0xFF);
	}
	public static void writeLong(OutputStream out,long v) throws IOException{
		byte[] writeBuffer=new byte[8];
		writeBuffer[0]=(byte)(v>>>56);
		writeBuffer[1]=(byte)(v>>>48);
		writeBuffer[2]=(byte)(v>>>40);
		writeBuffer[3]=(byte)(v>>>32);
		writeBuffer[4]=(byte)(v>>>24);
		writeBuffer[5]=(byte)(v>>>16);
		writeBuffer[6]=(byte)(v>>>8);
		writeBuffer[7]=(byte)(v>>>0);
		out.write(writeBuffer);
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
}
