package org.apache.commons.codec.binary;

import java.nio.charset.Charset;
import org.apache.commons.codec.Charsets;

public class StringUtils
{
  private static byte[] getBytes(String paramString, Charset paramCharset)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.getBytes(paramCharset);
  }
  
  public static byte[] getBytesUtf8(String paramString)
  {
    return getBytes(paramString, Charsets.UTF_8);
  }
  
  private static String newString(byte[] paramArrayOfByte, Charset paramCharset)
  {
    return paramArrayOfByte == null ? null : new String(paramArrayOfByte, paramCharset);
  }
  
  public static String newStringUtf8(byte[] paramArrayOfByte)
  {
    return newString(paramArrayOfByte, Charsets.UTF_8);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\codec\binary\StringUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */