package net.lingala.zip4j.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.unzip.UnzipEngine;

public class InflaterInputStream
  extends PartInputStream
{
  private Inflater inflater = new Inflater(true);
  private byte[] buff = new byte['က'];
  private byte[] oneByteBuff = new byte[1];
  private UnzipEngine unzipEngine;
  private long bytesWritten;
  private long uncompressedSize;
  
  public InflaterInputStream(RandomAccessFile paramRandomAccessFile, long paramLong1, long paramLong2, UnzipEngine paramUnzipEngine)
  {
    super(paramRandomAccessFile, paramLong1, paramLong2, paramUnzipEngine);
    this.unzipEngine = paramUnzipEngine;
    this.bytesWritten = 0L;
    this.uncompressedSize = paramUnzipEngine.getFileHeader().getUncompressedSize();
  }
  
  public int read()
    throws IOException
  {
    return read(this.oneByteBuff, 0, 1) == -1 ? -1 : this.oneByteBuff[0] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("input buffer is null");
    }
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("input buffer is null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    try
    {
      if (this.bytesWritten >= this.uncompressedSize)
      {
        finishInflating();
        return -1;
      }
      int i;
      while ((i = this.inflater.inflate(paramArrayOfByte, paramInt1, paramInt2)) == 0)
      {
        if ((this.inflater.finished()) || (this.inflater.needsDictionary()))
        {
          finishInflating();
          return -1;
        }
        if (this.inflater.needsInput()) {
          fill();
        }
      }
      this.bytesWritten += i;
      return i;
    }
    catch (DataFormatException localDataFormatException)
    {
      String str = "Invalid ZLIB data format";
      if (localDataFormatException.getMessage() != null) {
        str = localDataFormatException.getMessage();
      }
      if ((this.unzipEngine != null) && (this.unzipEngine.getLocalFileHeader().isEncrypted()) && (this.unzipEngine.getLocalFileHeader().getEncryptionMethod() == 0)) {
        str = str + " - Wrong Password?";
      }
      throw new IOException(str);
    }
  }
  
  private void finishInflating()
    throws IOException
  {
    byte[] arrayOfByte = new byte['Ѐ'];
    while (super.read(arrayOfByte, 0, 1024) != -1) {}
    checkAndReadAESMacBytes();
  }
  
  private void fill()
    throws IOException
  {
    int i = super.read(this.buff, 0, this.buff.length);
    if (i == -1) {
      throw new EOFException("Unexpected end of ZLIB input stream");
    }
    this.inflater.setInput(this.buff, 0, i);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("negative skip length");
    }
    int i = (int)Math.min(paramLong, 2147483647L);
    int j = 0;
    byte[] arrayOfByte = new byte['Ȁ'];
    while (j < i)
    {
      int k = i - j;
      if (k > arrayOfByte.length) {
        k = arrayOfByte.length;
      }
      k = read(arrayOfByte, 0, k);
      if (k == -1) {
        break;
      }
      j += k;
    }
    return j;
  }
  
  public int available()
  {
    return this.inflater.finished() ? 0 : 1;
  }
  
  public void close()
    throws IOException
  {
    this.inflater.end();
    super.close();
  }
  
  public UnzipEngine getUnzipEngine()
  {
    return super.getUnzipEngine();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\io\InflaterInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */