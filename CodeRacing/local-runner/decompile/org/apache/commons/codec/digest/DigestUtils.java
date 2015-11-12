package org.apache.commons.codec.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

public class DigestUtils
{
  public static MessageDigest getDigest(String paramString)
  {
    try
    {
      return MessageDigest.getInstance(paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new IllegalArgumentException(localNoSuchAlgorithmException);
    }
  }
  
  public static MessageDigest getSha1Digest()
  {
    return getDigest("SHA-1");
  }
  
  public static byte[] sha1(byte[] paramArrayOfByte)
  {
    return getSha1Digest().digest(paramArrayOfByte);
  }
  
  public static byte[] sha1(String paramString)
  {
    return sha1(StringUtils.getBytesUtf8(paramString));
  }
  
  public static String sha1Hex(byte[] paramArrayOfByte)
  {
    return Hex.encodeHexString(sha1(paramArrayOfByte));
  }
  
  public static String sha1Hex(String paramString)
  {
    return Hex.encodeHexString(sha1(paramString));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\codec\digest\DigestUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */