package net.lingala.zip4j.io;

import java.io.IOException;
import java.io.InputStream;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.unzip.UnzipEngine;

public class ZipInputStream
  extends InputStream
{
  private BaseInputStream is;
  
  public ZipInputStream(BaseInputStream paramBaseInputStream)
  {
    this.is = paramBaseInputStream;
  }
  
  public int read()
    throws IOException
  {
    int i = this.is.read();
    if (i != -1) {
      this.is.getUnzipEngine().updateCRC(i);
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = this.is.read(paramArrayOfByte, paramInt1, paramInt2);
    if ((i > 0) && (this.is.getUnzipEngine() != null)) {
      this.is.getUnzipEngine().updateCRC(paramArrayOfByte, paramInt1, i);
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    close(false);
  }
  
  public void close(boolean paramBoolean)
    throws IOException
  {
    try
    {
      this.is.close();
      if ((!paramBoolean) && (this.is.getUnzipEngine() != null)) {
        this.is.getUnzipEngine().checkCRC();
      }
    }
    catch (ZipException localZipException)
    {
      throw new IOException(localZipException.getMessage());
    }
  }
  
  public int available()
    throws IOException
  {
    return this.is.available();
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return this.is.skip(paramLong);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\io\ZipInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */