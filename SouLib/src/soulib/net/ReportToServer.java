package soulib.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;

import soulib.angou.Angou;
import soulib.fileLib.Crash_report;

public class ReportToServer{
	public String Status;
	public long block;
	public long ALLblock;
	public int blockSize=1024;
	private long start;
	private DecimalFormat df;
	private int counter;
	public ReportToServer() {

	}
	public void ToServer(File file,String Code,String IP,int Port,String dir) throws Exception{
		if(Port<0) Port=Crash_report.getDefaultPort(Port);
		if(IP==null) IP=Crash_report.getServerIP(false);
		if(IP=="") IP=Crash_report.getServerIP(false);
		if(IP==null)throw new NullPointerException("サーバーアドレス取得失敗");
		if(Code.getBytes("Unicode").length!="00000".getBytes("Unicode").length) Code="auto1";
		// ソケットの準備
		Status="接続中";
		Socket socket=new Socket(IP,Port);
		ToServer(file, Code, socket, dir);
		// 終了処理
		Status="切断中";
		socket.close();
	}
	public String getStringP() {
		if(df==null)df = new DecimalFormat("0.0000");
		return df.format(getP())+"%";
	}
	public double getP() {
		if(block<1)return 0;
		return (double)block/ALLblock*100d;
	}
	public String toString(long Byte) {
		if(df==null)df = new DecimalFormat("0.0000");
		return toString(Byte,df);
	}
	public String toString(long Byte,DecimalFormat df) {
		if(Byte>1073741824)return df.format(Byte/1073741824d)+"GiB";
		if(Byte>1048576)return df.format(Byte/1048576d)+"MiB";
		if(Byte>1024)return df.format(Byte/1024d)+"KiB";
		return Byte+"Byte";
	}
	public String getSpeedString() {
		return getSpeedString(getSpeed());
	}
	public String getSpeedString(long speed) {
		if(df==null)df = new DecimalFormat("0.0000");
		return toString(speed,df)+"/s";
	}
	public long getSpeed() {
		return getSpeedMS()*1000;
	}
	/**@return 1ミリ秒単位での速度*/
	public long getSpeedMS() {
		long time=System.currentTimeMillis()-start;
		try{
			return counter/time;
		}catch(ArithmeticException e) {
			return 0;
		}
	}
	public void ToServer(File file,String Code,Socket socket,String dir) throws Exception{
		start=System.currentTimeMillis();
		ALLblock=file.length();
		counter=0;
		// ストリームの準備
		InputStream inputStream=new FileInputStream(file);
		OutputStream outputStream=socket.getOutputStream();
		// ファイルをストリームで送信
		int fileLength;
		Status="種類情報";
		outputStream.write(Code.getBytes("Unicode"),0,"00000".getBytes("Unicode").length);
		if(Code.equals("auto0")||Code.equals("auto1")){
			//最大２０桁
			Status="名前送信";
			String name=file.getName()+"#####################################";
			outputStream.write(name.getBytes("UTF-8"),0,Crash_report.AutoK);
		}else if(Code.equals("auto2")||Code.equals("auto3")){
			Status="名前送信";
			DataOutputStream dos=new DataOutputStream(outputStream);
			byte[] name=file.getName().getBytes("UTF-8");
			dos.writeInt(name.length);
			dos.write(name);
		}
		byte angou=0;
		if(Code.equals("auto1")||Code.equals("auto3")){
			angou=1;
		}else if(Code.equals("auto4")){
			angou=2;
		}else if(Code.equals("auto5")){
			angou=3;
		}
		start=System.currentTimeMillis();
		if(angou==1){
			Status="読込中";
			byte[] buffer=new byte[blockSize]; // ファイル送信時のバッファ
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			while((fileLength=inputStream.read(buffer))>0){
				bos.write(buffer,0,fileLength);
			}
			byte[] Bytes=bos.toByteArray();
			Status="暗号化";
			Angou.A(Bytes,socket);
		}else if(angou==2||angou==3){
			Status="暗号鍵";
			Angou ango=new Angou(socket,false);
			nc(ango,ango, ango, dir, file,angou==3, inputStream);
		}else if(Code.equals("auto6")){
			DataOutputStream dos=new DataOutputStream(outputStream);
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			ExtendDataIO exio=new ExtendDataIOStream(dos,dis);
			nc(dos,dis,exio, dir, file,true, inputStream);
		}else{
			Status="送信中";
			byte[] buffer=new byte[blockSize]; // ファイル送信時のバッファ
			while((fileLength=inputStream.read(buffer))>0){
				outputStream.write(buffer,0,fileLength);
				block+=fileLength;
				if(counter==0)counter=fileLength;
				start=System.currentTimeMillis();
			}
		}
		start=0;
		outputStream.flush();
		inputStream.close();
	}
	private void nc(DataOutput out,DataInput in,ExtendDataIO exio,String dir,File file,boolean update,InputStream is) throws IOException {
		Status="名前送信";
		byte[] name=(dir+file.getName()).getBytes("UTF-8");
		exio.writeALL(name);
		if(update) {
			out.writeLong(file.lastModified());
			if(in.readBoolean()) {
				System.out.println("Skip="+dir+file.getName());
				return;
			}
		}
		Status="送信中";
		byte[] buffer=new byte[blockSize]; // ファイル送信時のバッファ
		start=System.currentTimeMillis();
		int fileLength;
		while((fileLength=is.read(buffer))>0){
			exio.writeEX(buffer,0,fileLength);
			block+=fileLength;
			if(counter==0)counter=fileLength;
			start=System.currentTimeMillis();
		}
	}
	public String yosou(){
		if(ALLblock-block<1)return "0秒";
		long speed;
		try{
			speed=(ALLblock-block)/getSpeed();
		}catch(ArithmeticException a) {
			speed=0;
		}
		if(speed>86400)return speed/86400+"日"+speed%86400/3600+"時間";
		if(speed>3600)return speed/3600+"時間"+speed%3600/60+"分";
		if(speed>60)return speed/60+"分"+speed%60+"秒";
		return speed+"秒";
	}
}
