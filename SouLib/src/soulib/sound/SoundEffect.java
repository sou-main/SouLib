package soulib.sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import soulib.fileLib.JarLocalFile;

public class SoundEffect extends SoundBase{

	private byte[][] data;
	private int[] bytesRead;
	private int size;
	/** 単位はフレーム */
	private long loadEnd;
	private boolean loadStart;
	/** 単位はフレーム */
	private long DataSize;
	private ArrayList<SourceDataLine> list=new ArrayList<SourceDataLine>();
	public SoundEffect(File f) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		super(f);
	}
	public SoundEffect(URL url) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		super(url);
	}
	public SoundEffect(JarLocalFile jlf) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		this(jlf.getURL());
	}
	public SoundEffect(SoundEffect se) {
		super(se);
		for(int i=0;i<data.length;i++) {
			this.data[i]=Arrays.copyOf(se.data[i],se.data[i].length);
		}
		this.bytesRead=Arrays.copyOf(bytesRead,bytesRead.length);
		this.size=se.size;
		this.loadEnd=se.loadEnd;
		this.loadStart=se.loadStart;
		this.DataSize=se.DataSize;
	}
	@Override
	public void run(){
		if(loadEnd!=DataSize) return;
		try{
			SourceDataLine l=AudioSystem.getSourceDataLine(format);
			l.open(format);
			l.start();
			setVol(getVol(),l);
			list.add(l);
			boolean a=true;
			while(isRoop()||a){
				a=false;
				for(int i=0;i<bytesRead.length;i++){
					if(bytesRead[i]!=-1) l.write(data[i],0,bytesRead[i]);
					//playFrame+=bytesRead[i]/format.getFrameSize();
					if(isStop()) {
						list.remove(l);
						break;
					}
				}
				if(isStop()) {
					break;
				}
			}
			l.drain();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public float getLoadEnd(){
		return loadEnd;
	}
	@Override
	public boolean loadEnd(){
		return loadEnd==DataSize;
	}
	/**これから再生される音量を変更する*/
	@Override
	public float setVol(float vol){
		return this.vol=vol;
	}
	/**初期化処理は何もしない*/
	@Override
	protected void init(){}
	/**すべて読み込みする*/
	@Override
	public synchronized void load(){
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
					//System.out.println("loadMiss");
					break;
				}
			}
			ais.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**再生する(複数回呼び出せる)*/
	@Override
	public synchronized void play(boolean nt){
		if(!loadStart){
			loadStart=true;
			load();
		}
		if(nt)new Thread(this,"sound="+url).start();
		else run();
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
}
