package soulib.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ExtendDataIOStream implements ExtendDataIO{

	private DataInputStream dis;
	private DataOutputStream dos;
	public ExtendDataIOStream(DataOutputStream dos,DataInputStream dis){
		this.dis=dis;
		this.dos=dos;
	}
	@Override
	public byte[] read() throws IOException{
		byte[] b=new byte[dis.readInt()];
		int l=dis.read(b);
		if(l<0)return null;
		return Arrays.copyOf(b,l);
	}
	@Override
	public void writeALL(byte[] b) throws IOException{
		dos.writeInt(b.length);
		dos.write(b);
	}
	@Override
	public void writeEX(byte[] b,int off,int len) throws IOException{
		dos.writeInt(len);
		dos.write(b,off,len);
	}
}