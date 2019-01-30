package soulib.angou;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import soulib.net.ExtendDataIO;

/**
 * RSAによるキー交換とAES暗号化のサンプル
 *
 * @author seraphy
 */
public class Angou implements DataOutput,DataInput,ExtendDataIO{
	private Socket socket;
	private boolean debug;
	private byte[] digest;
	// private boolean serverSide;
	private final String Pass;
	private SecretKey skey;
	private DataInputStream socIS;
	private DataOutputStream socOS;
	public byte[] getDigest(){
		return this.digest;
	}
	/**
	 * @param server ServerSide=true
	 * @throws Exception
	 */
	public Angou(Socket socket,boolean server) throws Exception{
		this(socket,server,"f8TYi6W7g3H5dy2i3u3y4");
	}
	public Angou(Socket soc,byte[] key){
		this.socket=soc;
		this.digest=key;
		Pass="ttttttt";
	}
	public Angou(Socket socket,boolean server,String pass) throws Exception{
		this.socket=socket;
		// this.serverSide=server;
		this.Pass=pass;
		if(server){
			sSide();
		}else{
			cSide();
		}
		// DotNETで受け取る場合は AES/CBC/PKCK7Padding となるが、
		// PKCS5とPKCS7は同じpaddingアルゴリズムであるため問題ない.
		// PKCS7 http://www.rfc-editor.org/rfc/rfc2315.txt
		// PKCS5 http://www.rfc-editor.org/rfc/rfc2898.txt
		skey=new SecretKeySpec(digest,"AES");
	}
	/**
	 * バイト列を16進数文字列に変換する.
	 *
	 * @param data バイト列
	 * @return 16進数文字列
	 */
	private static String toHexString(byte[] data){
		StringBuilder buf=new StringBuilder();
		for(byte d:data){
			buf.append(String.format("%02X",d));
		}
		return buf.toString();
	}
	public static synchronized void A(byte[] Bytes,Socket socket) throws Exception{
		Angou a=new Angou(socket,false);
		a.write(Bytes);
	}
	/**暗号化して送信*/
	@Override
	public void write(byte[] bytes) throws IOException{
		// if(serverSide) return;
		// 共通暗号化キーを用いてAES/CBC/PKCS5Paddingで長いデータを暗号化する.
		byte[] encrypted2;
		byte[] iv;
		Cipher cipher;
		try{
			cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE,skey);
		}catch(Exception e){
			e.printStackTrace();
			throw new IOException(e.getMessage(),e);
		}
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		iv=cipher.getIV();
		bos.write(cipher.update(bytes));
		try{
			bos.write(cipher.doFinal());
		}catch(Exception e){
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		encrypted2=bos.toByteArray();
		// BigEndian
		socOS.writeInt(iv.length);
		socOS.write(iv);
		socOS.writeInt(encrypted2.length);
		socOS.write(encrypted2);
	}
	private synchronized void cSide() throws Exception{
		// 暗号化キーを安全に二点間で交換するためのRSA暗号化キーを生成する.
		KeyPairGenerator keygen=KeyPairGenerator.getInstance("RSA");
		keygen.initialize(2048); // 1024bit - 88bit = 117byte (最大平文サイズ)
		KeyPair keyPair=keygen.generateKeyPair();
		// 秘密キー
		RSAPrivateKey privateKey=(RSAPrivateKey)keyPair.getPrivate();
		// 公開キー
		RSAPublicKey publicKey=(RSAPublicKey)keyPair.getPublic();
		// 秘密キーと公開キーを表示
		if(debug){
			Key[] keys=new Key[]{privateKey,publicKey};
			for(Key key:keys){
				String algo=key.getAlgorithm();
				String format=key.getFormat();
				byte[] bin=key.getEncoded();
				String encoded=toHexString(bin);
				System.out.println("algo="+algo+"/format="+format+"/key="+encoded);
			}
		}
		// RSA PublicKeyをファイルに保存する.
		byte[] byteModules=publicKey.getModulus().toByteArray();
		byte[] bytePublicExponent=publicKey.getPublicExponent().toByteArray();
		// public-keyをバイナリで転送する場合、BigEndianであることに注意.
		// TODO ここで受信マシンに公開キーを転送する
		socOS=new DataOutputStream(socket.getOutputStream());
		// BigEndian
		socOS.writeInt(byteModules.length);
		socOS.write(byteModules);
		socOS.writeInt(bytePublicExponent.length);
		socOS.write(bytePublicExponent);
		// TODO ここで受信マシンから共通暗号キーを受信
		byte[] encryptedData=null;
		socIS=new DataInputStream(socket.getInputStream());
		encryptedData=new byte[socIS.readInt()];
		socIS.readFully(encryptedData);
		// 秘密キーで共通暗号キーを復号化する.
		Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE,privateKey);
		digest=cipher.doFinal(encryptedData);
		// 元の共通暗号キーと、RSA暗号化経由で転送された共通暗号キーを表示
		if(debug) System.out.println("digest(dec)="+toHexString(digest));
	}
	public static synchronized byte[] B(String PW,Socket socket) throws Exception{
		Angou a=new Angou(socket,true,PW);
		return a.read();
	}
	/**@param mode Cipher.ENCRYPT_MODEとCipher.DECRYPT_MODE<br>*/
	public CipherOutputStream getCipherOutputStream(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		//skey=new SecretKeySpec(key,"AES");
		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(mode,skey);
		return new CipherOutputStream(socOS,cipher);
	}
	/**@param mode Cipher.ENCRYPT_MODEとCipher.DECRYPT_MODE<br>*/
	public CipherInputStream getCipherInputStream(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		//skey=new SecretKeySpec(key,"AES");
		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(mode,skey);
		return new CipherInputStream(socIS,cipher);
	}
	private synchronized void sSide() throws Exception{
		// 公開キーを他方に転送した場合、まず公開キーのエンコード値をもとにKeySpecに復元する
		// openssl rsa -in ./key.pem -pubout -out ./key.x509
		// (opensslのbase64化したバイト列と互換)
		// X509EncodedKeySpec publicKeySpec = new
		// X509EncodedKeySpec(publicKey.getEncoded());
		byte[] byteModules=null;
		byte[] bytePublicExponent=null;
		socIS=new DataInputStream(socket.getInputStream());
		int in=socIS.readInt();
		byteModules=new byte[in];
		socIS.readFully(byteModules);
		bytePublicExponent=new byte[socIS.readInt()];
		socIS.readFully(bytePublicExponent);
		// 公開キーを他方に転送した場合、まず公開キーのmodulesとexponents値をもとにPublicKeyを復元する.
		BigInteger modules=new BigInteger(byteModules);
		BigInteger publicExponent=new BigInteger(bytePublicExponent);
		RSAPublicKeySpec publicKeySpec=new RSAPublicKeySpec(modules,publicExponent);
		// KeySpecから、公開RSAキーを復元する.
		KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		RSAPublicKey publicKey2=(RSAPublicKey)keyFactory.generatePublic(publicKeySpec);
		// 共通暗号化のための、パスワードとソルト
		char[] password=Pass.toCharArray();
		byte[] salt=new byte[]{1};
		// 共通暗号化キーをパスワードとソルトから生成する.
		// ※ JCE（Java Cryptography Extension）の無制限強度の管轄ポリシーファイルを
		// 設定しないと、標準ではAES256は使えないのでキーの長さは128にとどめる必要あり。
		SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec=new PBEKeySpec(password,salt,65536,128); // キーは128Bit
		SecretKey tmp=factory.generateSecret(spec);
		digest=tmp.getEncoded();
		// 公開キーで共通暗号キーを暗号化する.
		byte[] encryptedData;
		{
			Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE,publicKey2);
			encryptedData=cipher.doFinal(digest);
		}
		if(debug) System.out.println("digest(org)="+toHexString(digest));
		socOS=new DataOutputStream(socket.getOutputStream());
		// BigEndian
		socOS.writeInt(encryptedData.length);
		socOS.write(encryptedData);
		// ※※※ ここでRSA暗号化された共通暗号化キーをマシンAに転送すると想定 ※※※
		// 共通暗号化キーと、IVを用いて、長いデータを復号化する

		// マシンAからマシンBに転送されたデータを表示する.
		// System.out.println("ret="+new String(bytes,0,bytes.length,"UTF-8"));
	}
	@Override
	public byte[] read() throws IOException{
		try{
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv=null;
			byte[] encrypted2=null;
			int ivSize=socIS.readInt();
			if(ivSize<0)throw new EOFException("ivSize<0");
			iv=new byte[ivSize];
			socIS.readFully(iv);
			encrypted2=new byte[socIS.readInt()];
			socIS.readFully(encrypted2);
			cipher.init(Cipher.DECRYPT_MODE,skey,new IvParameterSpec(iv));
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			bos.write(cipher.update(encrypted2));
			bos.write(cipher.doFinal());
			return bos.toByteArray();
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}
	@Override
	public void write(int b) throws IOException{
		byte[] by={(byte)b};
		write(by);
	}
	@Override
	public void writeEX(byte[] b,int off,int len) throws IOException{
		write(b,off,len);
	}
	@Override
	public void write(byte[] b,int off,int len) throws IOException{
		if(b==null){
			throw new NullPointerException();
		}else if((off<0)||(off>b.length)||(len<0)||
				((off+len)>b.length)||((off+len)<0)){
			throw new IndexOutOfBoundsException();
		}else if(len==0){
			return;
		}
		if(b.length==len){
			write(b);
		}else{
			byte[] by=new byte[len];
			for(int i=0;i<len;i++){
				by[i]=b[off+i];
			}
			write(by);
		}
	}
	/**単純なデータの為暗号化されません*/
	@Override
	public void writeBoolean(boolean v) throws IOException{
		socOS.writeBoolean(v);
	}
	/**暗号化されません*/
	@Override
	public void writeByte(int v) throws IOException{
		socOS.writeByte(v);
	}
	/**暗号化しない入力ストリームを取得します。*/
	public DataInputStream getIS() {
		return socIS;
	}
	/**暗号化しない出力ストリームを取得します。*/
	public DataOutputStream getOS() {
		return socOS;
	}
	@Override
	public void writeInt(int v) throws IOException{
		byte[] b={
				(byte)((v>>>24)&0xFF),
				(byte)((v>>>16)&0xFF),
				(byte)((v>>>8)&0xFF),
				(byte)((v>>>0)&0xFF)
		};
		write(b);
	}
	@Override
	public void writeLong(long v) throws IOException{
		byte[] b={(byte)(v>>>56),
				(byte)(v>>>48),
				(byte)(v>>>40),
				(byte)(v>>>32),
				(byte)(v>>>24),
				(byte)(v>>>16),
				(byte)(v>>>8),
				(byte)(v>>>0)
		};
		write(b);
	}
	@Override
	public final void writeFloat(float v) throws IOException{
		writeInt(Float.floatToIntBits(v));
	}
	@Override
	public final void writeDouble(double v) throws IOException{
		writeLong(Double.doubleToLongBits(v));
	}
	@Override
	public void writeShort(int v) throws IOException{
		byte[] b={
				(byte)((v>>>8)&0xFF),
				(byte)((v>>>0)&0xFF)
		};
		write(b);
	}
	@Override
	public void writeChar(int v) throws IOException{
		byte[] b={
				(byte)((v>>>8)&0xFF),
				(byte)((v>>>0)&0xFF)
		};
		write(b);
	}
	@Override
	public void writeBytes(String s) throws IOException{
		int len=s.length();
		byte[] b=new byte[len];
		for(int i=0;i<len;i++){
			b[i]=(byte)s.charAt(i);
		}
		write(b);
	}
	@Override
	public void writeChars(String s) throws IOException{
		int len=s.length();
		byte[] b=new byte[len*2];
		int id=0;
		for(int i=0;i<len;i++){
			int v=s.charAt(i);
			b[id]=(byte)((v>>>8)&0xFF);
			b[id+1]=(byte)((v>>>0)&0xFF);
			id+=2;
		}
		write(b);
	}
	/**
	 * 文字列を「UTF-8」でbyte配列に変換して
	 * 「writeALL(byte[] b)」で書き込みます
	 */
	@Override
	public void writeUTF(String s) throws IOException{
		writeALL(s.getBytes("UTF-8"));
	}
	@Deprecated
	@Override
	public void readFully(byte[] b) throws IOException{
		b=read();
	}
	/** <font size=5 color=red>利用不可</font> */
	@Override
	public void readFully(byte[] b,int off,int len) throws IOException{
		throw new IOException("利用不可");
	}
	/** <font size=5 color=red>利用不可</font> */
	@Override
	public int skipBytes(int n) throws IOException{
		throw new IOException("利用不可");
	}
	/**暗号化されません*/
	@Override
	public boolean readBoolean() throws IOException{
		return socIS.readBoolean();
	}
	/**暗号化されません*/
	@Override
	public byte readByte() throws IOException{
		return socIS.readByte();
	}
	/** <font size=5 color=red>利用不可</font> */
	@Override
	public int readUnsignedByte() throws IOException{
		throw new IOException("利用不可");
	}
	@Override
	public short readShort() throws IOException{
		byte[] ch=read();
		if((ch[0]|ch[1])<0) throw new EOFException();
		return (short)((ch[0]<<8)+(ch[1]<<0));
	}
	/** <font size=5 color=red>利用不可</font> */
	@Override
	public int readUnsignedShort() throws IOException{
		throw new IOException("利用不可");
	}
	@Override
	public char readChar() throws IOException{
		byte[] ch=read();
		if((ch[0]|ch[1])<0) throw new EOFException();
		return (char)((ch[0]<<8)+(ch[1]<<0));
	}
	@Override
	public int readInt() throws IOException{
		byte[] ch=read();
		if((ch[0]|ch[1]|ch[2]|ch[3])<0) throw new EOFException();
		return((ch[0]<<24)+(ch[1]<<16)+(ch[2]<<8)+(ch[3]<<0));
	}
	@Override
	public long readLong() throws IOException{
		byte[] readBuffer=read();
		return(((long)readBuffer[0]<<56)+
				((long)(readBuffer[1]&255)<<48)+
				((long)(readBuffer[2]&255)<<40)+
				((long)(readBuffer[3]&255)<<32)+
				((long)(readBuffer[4]&255)<<24)+
				((readBuffer[5]&255)<<16)+
				((readBuffer[6]&255)<<8)+
				((readBuffer[7]&255)<<0));
	}
	@Override
	public float readFloat() throws IOException{
		return Float.intBitsToFloat(readInt());
	}
	@Override
	public double readDouble() throws IOException{
		return Double.longBitsToDouble(readLong());
	}
	/** <font size=5 color=red>利用不可</font> */
	@Override
	public String readLine() throws IOException{
		throw new IOException("利用不可");
	}
	/**
	 * 「read()」でbyte配列を読み込んで
	 * 「UTF-8」で文字列に変換して返します。
	 */
	@Override
	public String readUTF() throws IOException{
		return new String(read(),"UTF-8");
	}
	@Override
	public void writeALL(byte[] b) throws IOException{
		write(b);
	}
	/**write(byte[] b)<br>
	 * の処理を終了します*/
	public void writeEnd() throws IOException{
		socOS.writeInt(-1);
	}
	public void write(byte[] data,int maxSize) throws IOException{
		if(data.length>maxSize){
			int last=0;
			byte[] buf=new byte[maxSize];
			while(true){
				int j=0;
				int end=last+maxSize;
				if(end>data.length){
					end=data.length;
					buf=new byte[end-last];
				}
				for(int i=last;i<end;i++){
					buf[j]=data[i];
					j++;
				}
				write(buf);
				if(buf.length<maxSize) break;
				last+=maxSize;
			}
		}else write(data);
		writeEnd();
	}
}
