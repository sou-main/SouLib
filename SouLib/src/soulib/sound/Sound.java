package soulib.sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

import soulib.fileLib.JarLocalFile;
import soulib.logLib.GameLog;

public class Sound{

	private Clip[] clip=new Clip[512];
	private int buf;
	private boolean close=true;
	public Sound(String pathname,int buf){
		this(new File(pathname),buf);
	}
	public Sound(File SoundFile,int buf){
		close=true;
		if(buf<1) buf=1;
		this.buf=buf;
		try{
			for(int i=0;i<buf;i++){
				// GameLog.Log(i);//TODO Log
				AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(SoundFile);
				AudioFormat audioFormat=audioInputStream.getFormat();
				Info info=new DataLine.Info(Clip.class,audioFormat);
				clip[i]=(Clip)AudioSystem.getLine(info);
				clip[i].open(audioInputStream);
			}
			close=false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Sound(AudioInputStream audioInputStream) throws LineUnavailableException,IOException{
		close=true;
		buf=1;
		AudioFormat audioFormat=audioInputStream.getFormat();
		Info info=new DataLine.Info(Clip.class,audioFormat);
		clip[0]=(Clip)AudioSystem.getLine(info);
		clip[0].open(audioInputStream);
		close=false;
	}
	public Sound(){}
	public Sound(URL inputStream,int buf){
		close=true;
		if(buf<1) buf=1;
		this.buf=buf;
		try{
			for(int i=0;i<buf;i++){
				// GameLog.Log(i);//TODO Log
				AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(inputStream.openStream());
				AudioFormat audioFormat=audioInputStream.getFormat();
				Info info=new DataLine.Info(Clip.class,audioFormat);
				clip[i]=(Clip)AudioSystem.getLine(info);
				clip[i].open(audioInputStream);
			}
			close=false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Sound(JarLocalFile jarLocalFile,int buf){
		this(jarLocalFile.getURL(),buf);
	}
	public Sound(JarLocalFile jarLocalFile){
		this(jarLocalFile,1);
	}
	public void play(int i){
		if(!close){
			reset(i);
			clip[i].start();
		}
	}
	public int play(){
		// System.out.println("PlaySound");
		if(close){
			return -1;
		}else{
			for(int i=0;i<buf;i++){
				if(!clip[i].isRunning()){
					reset(i);
					clip[i].start();
					return i;
				}
			}
		}
		return -1;
	}
	public int roop(){
		GameLog.Log("PlaySound");
		if(close){
			return -1;
		}else{
			for(int i=0;i<buf;i++){
				if(!clip[i].isRunning()){
					reset(i);
					clip[i].loop(Clip.LOOP_CONTINUOUSLY);// TODO Play
					return i;
				}
			}
		}
		return -1;
	}
	public void stopAll(){
		for(int i=0;i<buf;i++){
			stop(i);
		}
	}
	public void stop(int id){
		try{
			FloatControl control=(FloatControl)clip[id].getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(0);
			reset(id);
			// clip[id].flush();
		}catch(Throwable n){
			n.printStackTrace();
		}
	}
	public void close(){
		close=true;
		for(int j=0;j<clip.length;j++){
			try{
				clip[j].close();
				clip[j].flush();
			}catch(NullPointerException n){

			}
		}
	}
	public void reset(int id){
		if(id>-1){
			clip[id].stop();
			clip[id].setMicrosecondPosition(0);
			clip[id].setFramePosition(0);
		}else{
			for(int i=0;i<clip.length;i++){
				if(clip[i]!=null) reset(i);
			}
		}
	}
	public Clip getClip(int id){
		return clip[id];
	}
	public Clip[] getClip(){
		return clip;
	}
	public void setVol(int i,int id){
		setVol((double)i,id);
	}
	public void setVol(double i,int id){
		if(id<0){
			for(int j=0;j<clip.length;j++){
				try{
					FloatControl control=(FloatControl)clip[j].getControl(FloatControl.Type.MASTER_GAIN);
					control.setValue((float)Math.log10(i/100)*20);
				}catch(NullPointerException n){

				}
			}
		}else{
			try{
				FloatControl control=(FloatControl)clip[id].getControl(FloatControl.Type.MASTER_GAIN);
				control.setValue((float)Math.log10(i/100)*20);
			}catch(NullPointerException n){

			}
		}
	}
	public boolean isEnd(int id){
		try{
			return !clip[id].isActive();
		}catch(NullPointerException n){

		}
		return false;
	}
}
