package soulib.sound;

import java.awt.AWTError;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class SoundBase implements Runnable{

	/**出力先*/
	protected SourceDataLine line;
	/**データのフォーマット*/
	protected AudioFormat format;
	/**ファイルの場所*/
	protected URL url;
	/**音量*/
	protected float vol=50;
	protected AudioInputStream ais;
	/**停止状態*/
	protected boolean stop;
	/**繰り返し再生*/
	private boolean roop;
	public void End() {
		
	}
	public SoundBase(File f) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		this(f.toURI().toURL());
	}
	public SoundBase() {}
	public SoundBase(URL url) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		if(url==null)return;
		this.url=url;
		AudioInputStream fis=AudioSystem.getAudioInputStream(url);
		ais=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,fis);
		format=ais.getFormat();
		line=AudioSystem.getSourceDataLine(format);
		line.open(format);
		line.start();
		setVol(50f,line);
		init();
	}
	public SoundBase(SoundBase se){
		this.url=se.url;
		this.ais=se.ais;
		this.format=se.format;
		this.line=se.line;
		this.vol=se.vol;
	}
	public void load() {}
	public abstract int getBufferSize();
	protected abstract void init();
	/**読み込み終ったか*/
	public abstract boolean loadEnd();
	public long getFrameLength(){
		if(ais==null)return 0;
		return ais.getFrameLength();
	}
	/**再生する
	 * @param nt trueで新規スレッドを作成する*/
	public abstract void play(boolean nt);
	/**新しいスレッドで再生する*/
	public void play(){
		play(true);
	}
	/**次に停止可能な時に停止する*/
	public boolean stop(boolean b){
		return stop=b;
	}
	/**停止状態*/
	public boolean isStop(){
		return stop;
	}
	/**ループ再生*/
	public boolean isRoop(){
		return roop;
	}
	/**最後まで再生した時もう一度する*/
	public void setLoop(boolean roop){
		this.roop=roop;
	}
	/**音量設定
	 * @return 実際に設定された音量*/
	public float setVol(float vol){
		return setVol(vol,line);
	}
	/**音量設定
	 * @return 実際に設定された音量*/
	public float setVol(float vol,SourceDataLine line){
		if(line==null)return this.vol;
		try{
			FloatControl control=(FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
			float f=(float) Math.log10(vol/100d)*20f;
			if(vol<=0) {
				f=control.getMinimum();
				vol=0;
			}else if(vol>=200) {
				f=control.getMaximum();
				vol=200;
			}
			if(f>control.getMaximum()) return vol;
			if(f<control.getMinimum()) return vol;
			if(f==Float.NaN) return vol;
			control.setValue(f);
			this.vol=vol;
			return vol;
		}catch(Exception n){
			n.printStackTrace();
		}
		return this.vol;
	}
	/**現在の音量*/
	public float getVol(){
		return vol;
	}
	public abstract long getLoadFrame();
	/**このオブジェクトに参照できない時<br>リソースを開放する*/
	@Override
	protected void finalize(){
		close();
	}
	/** これを呼び出した後再生することはできません */
	public void close(){
		try{
			ais.close();
		}catch(Exception e){}
		try{
			line.drain();
		}catch(Exception e){}
		try{
			line.stop();
		}catch(Exception e){}
		try{
			line.close();
		}catch(Exception e){}
	}
	/**オーディオフォーマットを取得する*/
	public AudioFormat getFormat(){
		return format;
	}
	/**フォーマットを文字として取得する*/
	public String getFormatString(){
		if(format==null)return "";
		StringBuilder sb=new StringBuilder();
		sb.append("[AudioFormat] frameSize:"+format.getFrameSize()+"byte");
		sb.append(" Fs:"+format.getSampleRate()+"Hz");
		sb.append(" "+format.getSampleSizeInBits()+"bit");
		sb.append(" "+format.getChannels()+"ch");
		if(format.isBigEndian()){
			sb.append(" BigEndian");
		}else{
			sb.append(" LittleEndian");
		}
		if(format.getEncoding()==AudioFormat.Encoding.PCM_SIGNED){
			sb.append(" SignedPCM");
		}else if(format.getEncoding()==AudioFormat.Encoding.PCM_UNSIGNED){
			sb.append(" UnsignedPCM");
		}else{
			sb.append(" NoPCM");
		}
		return sb.toString();
	}
	public static SoundBase SoundEmpty() {
		return new empty();
	}
	public static SoundBase SoundBeep() {
		return new beep();
	}
}
class empty extends SoundBase{
	@Override
	public void run(){}
	@Override
	public int getBufferSize(){
		return 0;
	}
	@Override
	protected void init(){}
	@Override
	public boolean loadEnd(){
		return true;
	}
	@Override
	public void play(boolean nt){}
	@Override
	public long getLoadFrame(){
		return 0;
	}
}
/**ビープ音を鳴らす*/
class beep extends SoundBase{
	@Override
	public void run(){
		try{
			Toolkit.getDefaultToolkit().beep();//ビープ音を鳴らす
		}catch(AWTError e) {
			e.printStackTrace();
		}
	}
	@Override
	public int getBufferSize(){
		return 0;
	}
	@Override
	protected void init(){}
	@Override
	public boolean loadEnd(){
		return true;
	}
	@Override
	public void play(boolean nt){
		if(nt)new Thread(this,"beepSound").start();
		else run();
	}
	@Override
	public long getLoadFrame(){
		return 0;
	}
}