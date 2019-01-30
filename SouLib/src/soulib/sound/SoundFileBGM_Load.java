package soulib.sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import soulib.fileLib.JarLocalFile;

public class SoundFileBGM_Load extends  SoundBGM{

	private byte[][] data;
	private int[] bytesRead;
	private int size;
	/** 単位はフレーム */
	private long loadEnd;
	private boolean loadStart;
	/** 単位はフレーム */
	private long DataSize;
	private boolean end;
	public SoundFileBGM_Load(File f) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		super(f);
	}
	public SoundFileBGM_Load(URL url) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		super(url);
	}
	public SoundFileBGM_Load(JarLocalFile jlf) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		this(jlf.getURL());
	}
	@Override
	public void run(){
		if(loadEnd!=DataSize) return;
		try{
			boolean a=true;
			while(isRoop()||a){
				a=false;
				for(int i=0;i<bytesRead.length;i++){
					if(bytesRead[i]!=-1) line.write(data[i],0,bytesRead[i]);
					playFrame+=bytesRead[i]/format.getFrameSize();
					if(isStop()) try{
						thread.join();
					}catch(InterruptedException t){

					}
					if(end)break;
				}
				if(isStop()) {
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void End() {
		if(stop) {
			end=true;
			thread.interrupt();
		}else{
			stop=true;
			end=true;
			thread.interrupt();
		}
	}
	/**読み込み済のフレーム数*/
	public float getLoadEnd(){
		return loadEnd;
	}
	/**スレッドを初期化する*/
	@Override
	protected void init(){
		thread=new Thread(this,"sound="+url);
	}
	/**すべて読み込みする*/
	@Override
	public synchronized void load(){
		if(loadStart)return;
		loadStart=true;
		try{
			size=line.getBufferSize();
			int l=(int)(ais.getFrameLength()*format.getFrameSize()/size)+1;
			data=new byte[l][size];
			bytesRead=new int[l];
			DataSize=ais.getFrameLength();
			byte[] buf=new byte[size];
			int i=0;
			while((bytesRead[i]=ais.read(buf,0,buf.length))!=-1){
				data[i]=Arrays.copyOf(buf,buf.length);
				loadEnd+=bytesRead[i]/format.getFrameSize();
				i++;
				if(i>=l){
					System.out.println("loadMiss");
					break;
				}
			}
			ais.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public synchronized void play(boolean nt){
		if(!loadStart){
			loadStart=true;
			load();
		}
		if(nt)thread.start();
		else thread.run();
	}
	/**読み込み済のフレーム数*/
	@Override
	public long getLoadFrame() {
		return loadEnd;
	}
	/**読み込み済のフレーム数(0～1のdouble)*/
	public double getLoad(){
		return (double)loadEnd/(double)ais.getFrameLength();
	}
	/**1回に出力するバイト数*/
	@Override
	public int getBufferSize(){
		return size;
	}
	@Override
	public float getVol() {
		FloatControl control=(FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		return vol=(float)Math.pow(10,control.getValue()/20)*100;
	}
	@Override
	public boolean loadEnd(){
		return loadEnd==DataSize;
	}
}
