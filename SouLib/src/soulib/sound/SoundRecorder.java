package soulib.sound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class SoundRecorder extends Thread{

    // リニアPCM 16bit 8000Hz x １秒
   private byte[] voice;
    private TargetDataLine target;
    private AudioInputStream stream;

    public boolean g_bRecorder = false;
	private float HZ;
	private int BITS;
	private int MONO;
	private float LONG;

	// コンストラクタ
	public SoundRecorder(float HZ, int BITS, int MONO,float LONG){
		super("Recorder");
		this.HZ=HZ;
		this.BITS=BITS;
		this.MONO=MONO;
		this.LONG=LONG;
		init();
		try{
			// オーディオフォーマットの指定
			AudioFormat linear = new AudioFormat( HZ, BITS, MONO, true,  false );

			// ターゲットデータラインを取得
			DataLine.Info info = new DataLine.Info( TargetDataLine.class, linear );
			target = (TargetDataLine)AudioSystem.getLine( info );

			// ターゲットデータラインを開く
			target.open( linear );

			// マイク入力開始
			target.start();

			// 入力ストリームを取得
			stream = new AudioInputStream( target );

		} catch (LineUnavailableException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g_bRecorder = true;
	}

	private void init(){
		voice = new byte[ (int) (HZ * BITS / 8 * MONO*LONG) ];
	}

	// スレッド実行
	public void run(){
		while( true ){
			if( !g_bRecorder ) return;
			try{
				// ストリームから音声データを取得
				stream.read( voice , 0, voice.length );

			} catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 一応、ウエイト
			try{
				Thread.sleep( 100 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// データ取得
	public  byte[] getVoice(){
		return voice ;
	}

	// 終了
	public  void end(){
		g_bRecorder = false;

		// ターゲットデータラインを停止
		target.stop();

		// ターゲットデータラインを閉じる
		target.close();
	}
}