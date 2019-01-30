package soulib.angou;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CommonKeyAngou implements KeyAngou{
	private byte[] key;
	protected SecretKeySpec skey;
	protected Cipher cipher;
	private int mode;
	
	public CommonKeyAngou(byte[] key) throws GeneralSecurityException{
		setKey(key);
	}
	public CommonKeyAngou(String pass) throws GeneralSecurityException{
		if(pass==null) throw new NullPointerException();
		setKey(make(pass));
	}
	public void init() throws NoSuchAlgorithmException,NoSuchPaddingException{
		// DotNETで受け取る場合は AES/CBC/PKCK7Padding となるが、
		// PKCS5とPKCS7は同じpaddingアルゴリズムであるため問題ない.
		// PKCS7 http://www.rfc-editor.org/rfc/rfc2315.txt
		// PKCS5 http://www.rfc-editor.org/rfc/rfc2898.txt
		skey=new SecretKeySpec(key,"AES");
		cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
	}
	/**パスワードから暗号化キーを生成する*/
	public static byte[] make(String pass) throws NoSuchAlgorithmException,InvalidKeySpecException{
		if(pass==null||pass.isEmpty())throw new NullPointerException("null or empty Pass");
		// 共通暗号化のための、パスワードとソルト
		char[] password=pass.toCharArray();
		byte[] salt=new byte[]{1};
		// 共通暗号化キーをパスワードとソルトから生成する.
		// ※ JCE（Java Cryptography Extension）の無制限強度の管轄ポリシーファイルを
		// 設定しないと、標準ではAES256は使えないのでキーの長さは128にとどめる必要あり。
		SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec=new PBEKeySpec(password,salt,65536,128); // キーは128Bit
		SecretKey tmp=factory.generateSecret(spec);
		return tmp.getEncoded();
	}
	public OutputStream outStream(OutputStream out) throws InvalidKeyException, InvalidAlgorithmParameterException {
		if(mode!=Cipher.ENCRYPT_MODE){
			mode=Cipher.ENCRYPT_MODE;
			IvParameterSpec iv = new IvParameterSpec(key);
			cipher.init(mode,skey,iv);
		}
		return new CipherOutputStream(out,cipher);
	}
	public InputStream inStream(InputStream in) throws InvalidKeyException, InvalidAlgorithmParameterException {
		if(mode!=Cipher.DECRYPT_MODE){
			mode=Cipher.DECRYPT_MODE;
			IvParameterSpec iv = new IvParameterSpec(key);
			cipher.init(mode,skey,iv);
		}
		return new CipherInputStream(in,cipher);
	}
	@Override
	public byte[] angou(byte[] raw) throws GeneralSecurityException{
		if(mode!=Cipher.ENCRYPT_MODE){
			mode=Cipher.ENCRYPT_MODE;
			cipher.init(mode,skey);
		}
		try{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			byte[] iv=cipher.getIV();
			KeyAngouOutputStream.writeInt(bos,iv.length);
			bos.write(iv);
			ByteArrayOutputStream bos0=new ByteArrayOutputStream();
			bos0.write(cipher.update(raw));
			bos0.write(cipher.doFinal());
			byte[] encrypted=bos0.toByteArray();
			KeyAngouOutputStream.writeInt(bos,encrypted.length);
			bos.write(encrypted);
			return bos.toByteArray();
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] fukugou(byte[] angou) throws GeneralSecurityException{
		try{
			byte[] iv=null;
			byte[] encrypted=null;
			ByteArrayInputStream is=new ByteArrayInputStream(angou);
			DataInputStream dis=new DataInputStream(is);
			int ivSize=dis.readInt();
			if(ivSize<0) throw new EOFException("ivSize<0");
			iv=new byte[ivSize];
			dis.readFully(iv);
			encrypted=new byte[dis.readInt()];
			dis.readFully(encrypted);
			cipher.init(Cipher.DECRYPT_MODE,skey,new IvParameterSpec(iv));
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			bos.write(cipher.update(encrypted));
			bos.write(cipher.doFinal());
			return bos.toByteArray();
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getKey(){
		return key;
	}

	public void setKey(byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException{
		if(key==null)throw new NullPointerException("nullKey");
		this.key=key;
		init();
	}
}
