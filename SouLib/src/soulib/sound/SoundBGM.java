package soulib.sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class SoundBGM extends SoundBase{

	protected Thread thread;
	protected long playFrame;
	public SoundBGM(File f) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		super(f);
	}
	public SoundBGM(URL url) throws UnsupportedAudioFileException,IOException,LineUnavailableException{
		super(url);
	}
	protected SoundBGM() {}
	public long getPlayFrame(){
		return playFrame;
	}
	@Override
	public boolean stop(boolean b){
		try {
			if(!b)thread.interrupt();
			super.stop(b);
			return true;
		}catch(Exception e) {}
		return false;
	}
}
