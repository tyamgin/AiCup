package org.apache.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.output.StringBuilderWriter;

public class IOUtils
{
  public static final char DIR_SEPARATOR = File.separatorChar;
  public static final String LINE_SEPARATOR;
  private static byte[] SKIP_BYTE_BUFFER;
  
  public static void closeQuietly(InputStream paramInputStream)
  {
    closeQuietly(paramInputStream);
  }
  
  public static void closeQuietly(OutputStream paramOutputStream)
  {
    closeQuietly(paramOutputStream);
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    try
    {
      if (paramCloseable != null) {
        paramCloseable.close();
      }
    }
    catch (IOException localIOException) {}
  }
  
  public static BufferedReader toBufferedReader(Reader paramReader)
  {
    return (paramReader instanceof BufferedReader) ? (BufferedReader)paramReader : new BufferedReader(paramReader);
  }
  
  public static byte[] toByteArray(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong > 2147483647L) {
      throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + paramLong);
    }
    return toByteArray(paramInputStream, (int)paramLong);
  }
  
  public static byte[] toByteArray(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Size must be equal or greater than zero: " + paramInt);
    }
    if (paramInt == 0) {
      return new byte[0];
    }
    byte[] arrayOfByte = new byte[paramInt];
    int i = 0;
    int j;
    while ((i < paramInt) && ((j = paramInputStream.read(arrayOfByte, i, paramInt - i)) != -1)) {
      i += j;
    }
    if (i != paramInt) {
      throw new IOException("Unexpected readed size. current: " + i + ", excepted: " + paramInt);
    }
    return arrayOfByte;
  }
  
  public static List readLines(InputStream paramInputStream, Charset paramCharset)
    throws IOException
  {
    InputStreamReader localInputStreamReader = new InputStreamReader(paramInputStream, Charsets.toCharset(paramCharset));
    return readLines(localInputStreamReader);
  }
  
  public static List readLines(Reader paramReader)
    throws IOException
  {
    BufferedReader localBufferedReader = toBufferedReader(paramReader);
    ArrayList localArrayList = new ArrayList();
    for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine()) {
      localArrayList.add(str);
    }
    return localArrayList;
  }
  
  public static int copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    long l = copyLarge(paramInputStream, paramOutputStream);
    if (l > 2147483647L) {
      return -1;
    }
    return (int)l;
  }
  
  public static long copyLarge(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    return copyLarge(paramInputStream, paramOutputStream, new byte['က']);
  }
  
  public static long copyLarge(InputStream paramInputStream, OutputStream paramOutputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    long l = 0L;
    int i = 0;
    while (-1 != (i = paramInputStream.read(paramArrayOfByte)))
    {
      paramOutputStream.write(paramArrayOfByte, 0, i);
      l += i;
    }
    return l;
  }
  
  public static long copyLarge(InputStream paramInputStream, OutputStream paramOutputStream, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramLong1 > 0L) {
      skipFully(paramInputStream, paramLong1);
    }
    if (paramLong2 == 0L) {
      return 0L;
    }
    int i = paramArrayOfByte.length;
    int j = i;
    if ((paramLong2 > 0L) && (paramLong2 < i)) {
      j = (int)paramLong2;
    }
    long l = 0L;
    int k;
    while ((j > 0) && (-1 != (k = paramInputStream.read(paramArrayOfByte, 0, j))))
    {
      paramOutputStream.write(paramArrayOfByte, 0, k);
      l += k;
      if (paramLong2 > 0L) {
        j = (int)Math.min(paramLong2 - l, i);
      }
    }
    return l;
  }
  
  public static boolean contentEquals(InputStream paramInputStream1, InputStream paramInputStream2)
    throws IOException
  {
    if (!(paramInputStream1 instanceof BufferedInputStream)) {
      paramInputStream1 = new BufferedInputStream(paramInputStream1);
    }
    if (!(paramInputStream2 instanceof BufferedInputStream)) {
      paramInputStream2 = new BufferedInputStream(paramInputStream2);
    }
    for (int i = paramInputStream1.read(); -1 != i; i = paramInputStream1.read())
    {
      j = paramInputStream2.read();
      if (i != j) {
        return false;
      }
    }
    int j = paramInputStream2.read();
    return j == -1;
  }
  
  public static long skip(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Skip count must be non-negative, actual: " + paramLong);
    }
    if (SKIP_BYTE_BUFFER == null) {
      SKIP_BYTE_BUFFER = new byte['ࠀ'];
    }
    long l2;
    for (long l1 = paramLong; l1 > 0L; l1 -= l2)
    {
      l2 = paramInputStream.read(SKIP_BYTE_BUFFER, 0, (int)Math.min(l1, 2048L));
      if (l2 < 0L) {
        break;
      }
    }
    return paramLong - l1;
  }
  
  public static void skipFully(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Bytes to skip must not be negative: " + paramLong);
    }
    long l = skip(paramInputStream, paramLong);
    if (l != paramLong) {
      throw new EOFException("Bytes to skip: " + paramLong + " actual: " + l);
    }
  }
  
  public static int read(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("Length must not be negative: " + paramInt2);
    }
    int i = paramInt2;
    while (i > 0)
    {
      int j = paramInt2 - i;
      int k = paramInputStream.read(paramArrayOfByte, paramInt1 + j, i);
      if (-1 == k) {
        break;
      }
      i -= k;
    }
    return paramInt2 - i;
  }
  
  public static int read(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramInputStream, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  static
  {
    StringBuilderWriter localStringBuilderWriter = new StringBuilderWriter(4);
    PrintWriter localPrintWriter = new PrintWriter(localStringBuilderWriter);
    localPrintWriter.println();
    LINE_SEPARATOR = localStringBuilderWriter.toString();
    localPrintWriter.close();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\io\IOUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */