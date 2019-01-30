package soulib.angou;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PublicKeyAngou implements KeyAngou{
	private RSAPrivateKey privateKey;
	private Cipher cipher;
	private RSAPublicKey publicKey;
	/** 復号化する*/
	public PublicKeyAngou(byte[] byteModules,byte[] bytePublicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
		BigInteger modules=new BigInteger(byteModules);
		BigInteger publicExponent=new BigInteger(bytePublicExponent);
		RSAPublicKeySpec publicKeySpec=new RSAPublicKeySpec(modules,publicExponent);
		cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
		// KeySpecから、公開RSAキーを復元する.
		KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		publicKey=(RSAPublicKey)keyFactory.generatePublic(publicKeySpec);
		cipher.init(Cipher.DECRYPT_MODE,publicKey);
	}
	/** 暗号化する*/
	public PublicKeyAngou() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		// 暗号化キーを安全に二点間で交換するためのRSA暗号化キーを生成する.
		KeyPairGenerator keygen=KeyPairGenerator.getInstance("RSA");
		keygen.initialize(2048); // 1024bit - 88bit = 117byte (最大平文サイズ)
		KeyPair keyPair=keygen.generateKeyPair();
		cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
		privateKey=(RSAPrivateKey)keyPair.getPrivate();
		cipher.init(Cipher.ENCRYPT_MODE,privateKey);
		publicKey=(RSAPublicKey)keyPair.getPublic();
	}
	public byte[] byteModules() {
		return publicKey.getModulus().toByteArray();
	}
	public byte[] bytePublicExponent() {
		return publicKey.getPublicExponent().toByteArray();
	}
	public void writePublicKey(DataOutputStream dos) throws IOException {
		byte[] modules=byteModules();
		dos.writeInt(modules.length);
		dos.write(modules);
		byte[] exponent=bytePublicExponent();
		dos.writeInt(exponent.length);
		dos.write(exponent);
	}
	public static PublicKeyAngou readPublicKey(DataInputStream dis) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeySpecException {
		byte[] byteModules=new byte[dis.readInt()];
		dis.readFully(byteModules);
		byte[] bytePublicExponent=new byte[dis.readInt()];
		dis.readFully(bytePublicExponent);
		return new PublicKeyAngou(byteModules,bytePublicExponent);
	}
	@Override
	public byte[] angou(byte[] raw) throws IllegalBlockSizeException, BadPaddingException {
		return cipher.doFinal(raw);
	}
	public void writeAngou(byte[] raw,OutputStream dos) throws IllegalBlockSizeException, BadPaddingException, IOException {
		byte[] angou=angou(raw);
		KeyAngouOutputStream.writeInt(dos,angou.length);
		dos.write(angou);
	}
	@Override
	public byte[] fukugou(byte[] angou) throws IllegalBlockSizeException, BadPaddingException {
		return cipher.doFinal(angou);
	}
	public byte[] readAngou(DataInputStream dis) throws IllegalBlockSizeException, BadPaddingException, IOException {
		int size=dis.readInt();
		if(size<0)throw new EOFException("Size<0");
		byte[] angou=new byte[size];
		dis.read(angou);
		return fukugou(angou);
	}
	public void write(OutputStream os,byte[] data,int maxSize) throws IOException, IllegalBlockSizeException, BadPaddingException{
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
				writeAngou(buf,os);
				if(buf.length<maxSize) break;
				last+=maxSize;
			}
		}else writeAngou(data,os);
		KeyAngouOutputStream.writeInt(os,-1);
	}
}
