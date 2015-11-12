package de.schlichtherle.truezip.rof;

import java.io.Closeable;
import java.io.IOException;

public abstract interface ReadOnlyFile
  extends Closeable
{
  public abstract long length()
    throws IOException;
  
  public abstract long getFilePointer()
    throws IOException;
  
  public abstract void seek(long paramLong)
    throws IOException;
  
  public abstract int read()
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void close()
    throws IOException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\rof\ReadOnlyFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */