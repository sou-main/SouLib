package soulib.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import soulib.fileLib.JarLocalFile;

public class SoundFileBGM extends SoundBGM{
	private byte[] data;
	protected boolean nowStop=true;
	public int playNow;
	protected boolean end;
	public SoundFileBGM(File f) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		this(f.toURI().toURL());
	}
	public SoundFileBGM(URL url) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		this.url=url;
		AudioInputStream fis=AudioSystem.getAudioInputStream(url);
		format=fis.getFormat();
		AudioFormat decodedFormat=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				format.getSampleRate(),
				format.getSampleSizeInBits()==AudioSystem.NOT_SPECIFIED?16:format.getSampleSizeInBits(),
				format.getChannels(),
				format.getFrameSize()==AudioSystem.NOT_SPECIFIED?format.getChannels()*2:format.getFrameSize(),
				format.getSampleRate(),
				false); // PCMフォーマットを指定
		// 指定された形式へ変換
		//System.out.println(decodedFormat);
		ais = AudioSystem.getAudioInputStream(decodedFormat, fis);
		//ais=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,fis);
		format=ais.getFormat();
		DataLine.Info info = new DataLine.Info (SourceDataLine.class,
				decodedFormat);
		// ラインを取得
		line = (SourceDataLine) AudioSystem.getLine (info);
		//line=AudioSystem.getSourceDataLine(format);
		/*
		try{
			line=AudioSystem.getSourceDataLine(format);
		}catch(IllegalArgumentException e) {
			Mixer mixer=AudioSystem.getMixer(null); // default mixer
			mixer.open();
			System.err.printf("Supported SourceDataLines of default mixer (%s):\n\n",mixer.getMixerInfo().getName());
			for(Line.Info info:mixer.getSourceLineInfo()){
				if(SourceDataLine.class.isAssignableFrom(info.getLineClass())){
					SourceDataLine.Info info2=(SourceDataLine.Info) info;
					System.err.println(info2);
					System.err.printf("  max buffer size: \t%d\n",info2.getMaxBufferSize());
					System.err.printf("  min buffer size: \t%d\n",info2.getMinBufferSize());
					AudioFormat[] formats=info2.getFormats();
					System.err.println("  Supported Audio formats: ");
					for(AudioFormat format:formats){
						System.err.println("    "+format);
						if(line==null)try{
							line=AudioSystem.getSourceDataLine(format);
						}catch(IllegalArgumentException e2) {

						}
					}
					System.err.println();
				}else{
					System.err.println(info.toString());
				}
				System.err.println();
			}
		}
		*/
		line.open(format);
		mute(false);
		line.start();
		setVol(50f,line);
		init();
	}
	public void mute(boolean state){
		BooleanControl control = (BooleanControl)line.getControl(BooleanControl.Type.MUTE);
		if(control.getValue()!=state){
			control.setValue(state);
		}
	}
	public SoundFileBGM(JarLocalFile jlf) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		this(jlf.getURL());
	}
	/** 1回に出力するバイト数 */
	@Override
	public int getBufferSize(){
		if(data!=null) return data.length;
		return 0;
	}
	@Override
	public void run(){
		playNow++;
		try{
			data=new byte[line.getBufferSize()];
			int bytesRead;
			try{
				ais.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			stop=false;
			do{
				AudioInputStream fis=AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
				//ais=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,fis);
				ais = AudioSystem.getAudioInputStream(format, fis);
				playFrame=0;
				while((bytesRead=ais.read(data,0,data.length))!=-1){
					playFrame+=bytesRead/format.getFrameSize();
					line.write(data,0,bytesRead);
					//if(getFrameLength()-playFrame<60000)break;
					if(super.stop) try{
						nowStop=true;
						thread.join();
					}catch(InterruptedException t){
						nowStop=false;
					}
					if(end)break;
				}
				if(end)break;
				line.drain();
			}while(isRoop());
			end=true;
			this.stop=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			playNow--;
			try{
				ais.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			thread=new Thread(this,"sound="+url);
		}
	}
	public void End() {
		end=true;
		if(playNow>0&&stop)thread.interrupt();
		while(playNow>0) {
			try{
				Thread.sleep(50);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	@Override
	public boolean isStop(){
		return nowStop;
	}
	/** スレッドを初期化する */
	@Override
	protected void init(){
		thread=new Thread(this,"sound="+url);
	}
	@Override
	public void play(boolean nt){
		end=false;
		if(nt) thread.start();
		else run();
	}
	/** 今までに再生したフレーム数 */
	@Override
	public long getLoadFrame(){
		return playFrame;
	}
	@Override
	public boolean loadEnd(){
		return true;
	}
	public boolean isEnd(){
		return end;
	}
}
