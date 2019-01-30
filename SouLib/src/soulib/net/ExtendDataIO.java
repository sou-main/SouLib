package soulib.net;

import java.io.IOException;

public interface ExtendDataIO{
	public byte[] read() throws IOException;
	public void writeALL(byte[] b)throws IOException;
	public void writeEX(byte[] b,int off,int len) throws IOException;
}
