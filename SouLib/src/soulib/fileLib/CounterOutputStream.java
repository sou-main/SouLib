package soulib.fileLib;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/***/
public class CounterOutputStream extends FilterOutputStream{

	private long counter=0;
	public CounterOutputStream(OutputStream out){
		super(out);
	}
	public void write(int b) throws IOException {
		out.write(b);
		counter++;
	}
	public void write(byte b[], int off, int len) throws IOException {
		super.write(b,off,len);
		counter+=len-off;
	}
	public void setCounter(long c) {
		counter=c;
	}
	public long getCount() {
		return counter;
	}
}
