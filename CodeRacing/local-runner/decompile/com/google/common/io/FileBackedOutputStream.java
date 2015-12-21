package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Beta
public final class FileBackedOutputStream
  extends OutputStream
{
  private final int fileThreshold;
  private final boolean resetOnFinalize;
  private final InputSupplier supplier;
  private OutputStream out;
  private MemoryOutput memory;
  private File file;
  
  @VisibleForTesting
  synchronized File getFile()
  {
    return this.file;
  }
  
  public FileBackedOutputStream(int paramInt)
  {
    this(paramInt, false);
  }
  
  public FileBackedOutputStream(int paramInt, boolean paramBoolean)
  {
    this.fileThreshold = paramInt;
    this.resetOnFinalize = paramBoolean;
    this.memory = new MemoryOutput(null);
    this.out = this.memory;
    if (paramBoolean) {
      this.supplier = new InputSupplier()
      {
        public InputStream getInput()
          throws IOException
        {
          return FileBackedOutputStream.this.openStream();
        }
        
        protected void finalize()
        {
          try
          {
            FileBackedOutputStream.this.reset();
          }
          catch (Throwable localThrowable)
          {
            localThrowable.printStackTrace(System.err);
          }
        }
      };
    } else {
      this.supplier = new InputSupplier()
      {
        public InputStream getInput()
          throws IOException
        {
          return FileBackedOutputStream.this.openStream();
        }
      };
    }
  }
  
  public InputSupplier getSupplier()
  {
    return this.supplier;
  }
  
  private synchronized InputStream openStream()
    throws IOException
  {
    if (this.file != null) {
      return new FileInputStream(this.file);
    }
    return new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount());
  }
  
  public synchronized void reset()
    throws IOException
  {
    try
    {
      close();
    }
    finally
    {
      File localFile1;
      if (this.memory == null) {
        this.memory = new MemoryOutput(null);
      } else {
        this.memory.reset();
      }
      this.out = this.memory;
      if (this.file != null)
      {
        File localFile2 = this.file;
        this.file = null;
        if (!localFile2.delete()) {
          throw new IOException("Could not delete: " + localFile2);
        }
      }
    }
  }
  
  public synchronized void write(int paramInt)
    throws IOException
  {
    update(1);
    this.out.write(paramInt);
  }
  
  public synchronized void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    update(paramInt2);
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public synchronized void close()
    throws IOException
  {
    this.out.close();
  }
  
  public synchronized void flush()
    throws IOException
  {
    this.out.flush();
  }
  
  private void update(int paramInt)
    throws IOException
  {
    if ((this.file == null) && (this.memory.getCount() + paramInt > this.fileThreshold))
    {
      File localFile = File.createTempFile("FileBackedOutputStream", null);
      if (this.resetOnFinalize) {
        localFile.deleteOnExit();
      }
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      localFileOutputStream.write(this.memory.getBuffer(), 0, this.memory.getCount());
      localFileOutputStream.flush();
      this.out = localFileOutputStream;
      this.file = localFile;
      this.memory = null;
    }
  }
  
  private static class MemoryOutput
    extends ByteArrayOutputStream
  {
    byte[] getBuffer()
    {
      return this.buf;
    }
    
    int getCount()
    {
      return this.count;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\FileBackedOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */