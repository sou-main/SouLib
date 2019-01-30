package soulib.angou;

import java.security.GeneralSecurityException;

public interface KeyAngou{

	byte[] angou(byte[] raw) throws GeneralSecurityException;

	byte[] fukugou(byte[] angou) throws GeneralSecurityException;

}
