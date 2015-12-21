package net.lingala.zip4j.util;

import java.io.DataInput;
import java.io.IOException;
import net.lingala.zip4j.exception.ZipException;

public class Raw
{
  public static long readLongLittleEndian(byte[] paramArrayOfByte, int paramInt)
  {
    long l = 0L;
    l |= paramArrayOfByte[(paramInt + 7)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[(paramInt + 6)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[(paramInt + 5)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[(paramInt + 4)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[(paramInt + 3)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[(paramInt + 2)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[(paramInt + 1)] & 0xFF;
    l <<= 8;
    l |= paramArrayOfByte[paramInt] & 0xFF;
    return l;
  }
  
  public static int readLeInt(DataInput paramDataInput, byte[] paramArrayOfByte)
    throws ZipException
  {
    try
    {
      paramDataInput.readFully(paramArrayOfByte, 0, 4);
    }
    catch (IOException localIOException)
    {
      throw new ZipException(localIOException);
    }
    return paramArrayOfByte[0] & 0xFF | (paramArrayOfByte[1] & 0xFF) << 8 | (paramArrayOfByte[2] & 0xFF | (paramArrayOfByte[3] & 0xFF) << 8) << 16;
  }
  
  public static int readShortLittleEndian(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8;
  }
  
  public static final short readShortBigEndian(byte[] paramArrayOfByte, int paramInt)
  {
    short s = 0;
    s = (short)(s | paramArrayOfByte[paramInt] & 0xFF);
    s = (short)(s << 8);
    s = (short)(s | paramArrayOfByte[(paramInt + 1)] & 0xFF);
    return s;
  }
  
  public static int readIntLittleEndian(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 2)] & 0xFF | (paramArrayOfByte[(paramInt + 3)] & 0xFF) << 8) << 16;
  }
  
  public static void prepareBuffAESIVBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[0] = ((byte)paramInt1);
    paramArrayOfByte[1] = ((byte)(paramInt1 >> 8));
    paramArrayOfByte[2] = ((byte)(paramInt1 >> 16));
    paramArrayOfByte[3] = ((byte)(paramInt1 >> 24));
    paramArrayOfByte[4] = 0;
    paramArrayOfByte[5] = 0;
    paramArrayOfByte[6] = 0;
    paramArrayOfByte[7] = 0;
    paramArrayOfByte[8] = 0;
    paramArrayOfByte[9] = 0;
    paramArrayOfByte[10] = 0;
    paramArrayOfByte[11] = 0;
    paramArrayOfByte[12] = 0;
    paramArrayOfByte[13] = 0;
    paramArrayOfByte[14] = 0;
    paramArrayOfByte[15] = 0;
  }
  
  public static byte[] convertCharArrayToByteArray(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null) {
      throw new NullPointerException();
    }
    byte[] arrayOfByte = new byte[paramArrayOfChar.length];
    for (int i = 0; i < paramArrayOfChar.length; i++) {
      arrayOfByte[i] = ((byte)paramArrayOfChar[i]);
    }
    return arrayOfByte;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\util\Raw.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */