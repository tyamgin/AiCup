package de.schlichtherle.truezip.rof;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadOnlyFileInputStream
  extends InputStream
{
  protected ReadOnlyFile rof;
  private long mark = -1L;
  
  public ReadOnlyFileInputStream(ReadOnlyFile paramReadOnlyFile)
  {
    this.rof = paramReadOnlyFile;
  }
  
  public int read()
    throws IOException
  {
    return this.rof.read();
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return this.rof.read(paramArrayOfByte);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return this.rof.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong <= 0L) {
      return 0L;
    }
    long l1 = this.rof.getFilePointer();
    long l2 = this.rof.length();
    long l3 = l2 - l1;
    if (paramLong > l3) {
      paramLong = (int)l3;
    }
    this.rof.seek(l1 + paramLong);
    return paramLong;
  }
  
  public int available()
    throws IOException
  {
    long l = this.rof.length() - this.rof.getFilePointer();
    return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
  }
  
  public void close()
    throws IOException
  {
    this.rof.close();
  }
  
  public void mark(int paramInt)
  {
    try
    {
      this.mark = this.rof.getFilePointer();
    }
    catch (IOException localIOException)
    {
      Logger.getLogger(ReadOnlyFileInputStream.class.getName()).log(Level.WARNING, localIOException.getLocalizedMessage(), localIOException);
      this.mark = -2L;
    }
  }
  
  public void reset()
    throws IOException
  {
    if (this.mark < 0L) {
      throw new IOException(this.mark == -1L ? "no mark set" : "mark()/reset() not supported by underlying file");
    }
    this.rof.seek(this.mark);
  }
  
  public boolean markSupported()
  {
    try
    {
      this.rof.seek(this.rof.getFilePointer());
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\rof\ReadOnlyFileInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */