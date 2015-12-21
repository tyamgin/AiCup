package net.lingala.zip4j.crypto.PBKDF2;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MacBasedPRF
  implements PRF
{
  protected Mac mac;
  protected int hLen;
  protected String macAlgorithm;
  
  public MacBasedPRF(String paramString)
  {
    this.macAlgorithm = paramString;
    try
    {
      this.mac = Mac.getInstance(paramString);
      this.hLen = this.mac.getMacLength();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new RuntimeException(localNoSuchAlgorithmException);
    }
  }
  
  public byte[] doFinal(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = this.mac.doFinal(paramArrayOfByte);
    return arrayOfByte;
  }
  
  public byte[] doFinal()
  {
    byte[] arrayOfByte = this.mac.doFinal();
    return arrayOfByte;
  }
  
  public int getHLen()
  {
    return this.hLen;
  }
  
  public void init(byte[] paramArrayOfByte)
  {
    try
    {
      this.mac.init(new SecretKeySpec(paramArrayOfByte, this.macAlgorithm));
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new RuntimeException(localInvalidKeyException);
    }
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      this.mac.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new RuntimeException(localIllegalStateException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\crypto\PBKDF2\MacBasedPRF.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */