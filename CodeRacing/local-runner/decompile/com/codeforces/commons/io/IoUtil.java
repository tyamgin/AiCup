package com.codeforces.commons.io;

import com.codeforces.commons.math.NumberUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class IoUtil
{
  public static final int BUFFER_SIZE = NumberUtil.toInt(1048576L);
  
  public static byte[] toByteArray(InputStream paramInputStream)
    throws IOException
  {
    return toByteArray(paramInputStream, Integer.MAX_VALUE);
  }
  
  public static byte[] toByteArray(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    return toByteArray(paramInputStream, paramInt, true);
  }
  
  public static byte[] toByteArray(InputStream paramInputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    LimitedByteArrayOutputStream localLimitedByteArrayOutputStream = new LimitedByteArrayOutputStream(paramInt, paramBoolean);
    copy(paramInputStream, localLimitedByteArrayOutputStream, true, true);
    return localLimitedByteArrayOutputStream.toByteArray();
  }
  
  public static long copy(InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    throws IOException
  {
    try
    {
      long l = IOUtils.copyLarge(paramInputStream, paramOutputStream, 0L, paramInt, new byte[BUFFER_SIZE]);
      if (paramBoolean1) {
        paramInputStream.close();
      }
      if (paramBoolean2) {
        paramOutputStream.close();
      }
      return l;
    }
    catch (IOException localIOException)
    {
      if (paramBoolean1) {
        closeQuietly(paramInputStream);
      }
      if (paramBoolean2) {
        closeQuietly(paramOutputStream);
      }
      throw localIOException;
    }
  }
  
  public static long copy(InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    return copy(paramInputStream, paramOutputStream, paramBoolean1, paramBoolean2, Integer.MAX_VALUE);
  }
  
  public static long copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    return copy(paramInputStream, paramOutputStream, true, false);
  }
  
  public static void closeQuietly(AutoCloseable paramAutoCloseable)
  {
    if (paramAutoCloseable != null) {
      try
      {
        paramAutoCloseable.close();
      }
      catch (Exception localException) {}
    }
  }
  
  public static void closeQuietly(AutoCloseable... paramVarArgs)
  {
    for (AutoCloseable localAutoCloseable : paramVarArgs) {
      closeQuietly(localAutoCloseable);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\IoUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */