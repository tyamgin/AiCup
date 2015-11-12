package com.google.common.io;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

@GwtCompatible(emulated=true)
final class GwtWorkarounds
{
  @GwtIncompatible("Reader")
  static CharInput asCharInput(Reader paramReader)
  {
    Preconditions.checkNotNull(paramReader);
    new CharInput()
    {
      public int read()
        throws IOException
      {
        return this.val$reader.read();
      }
      
      public void close()
        throws IOException
      {
        this.val$reader.close();
      }
    };
  }
  
  static CharInput asCharInput(CharSequence paramCharSequence)
  {
    Preconditions.checkNotNull(paramCharSequence);
    new CharInput()
    {
      int index = 0;
      
      public int read()
      {
        if (this.index < this.val$chars.length()) {
          return this.val$chars.charAt(this.index++);
        }
        return -1;
      }
      
      public void close()
      {
        this.index = this.val$chars.length();
      }
    };
  }
  
  @GwtIncompatible("InputStream")
  static InputStream asInputStream(ByteInput paramByteInput)
  {
    Preconditions.checkNotNull(paramByteInput);
    new InputStream()
    {
      public int read()
        throws IOException
      {
        return this.val$input.read();
      }
      
      public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        Preconditions.checkNotNull(paramAnonymousArrayOfByte);
        Preconditions.checkPositionIndexes(paramAnonymousInt1, paramAnonymousInt1 + paramAnonymousInt2, paramAnonymousArrayOfByte.length);
        if (paramAnonymousInt2 == 0) {
          return 0;
        }
        int i = read();
        if (i == -1) {
          return -1;
        }
        paramAnonymousArrayOfByte[paramAnonymousInt1] = ((byte)i);
        for (int j = 1; j < paramAnonymousInt2; j++)
        {
          int k = read();
          if (k == -1) {
            return j;
          }
          paramAnonymousArrayOfByte[(paramAnonymousInt1 + j)] = ((byte)k);
        }
        return paramAnonymousInt2;
      }
      
      public void close()
        throws IOException
      {
        this.val$input.close();
      }
    };
  }
  
  @GwtIncompatible("OutputStream")
  static OutputStream asOutputStream(ByteOutput paramByteOutput)
  {
    Preconditions.checkNotNull(paramByteOutput);
    new OutputStream()
    {
      public void write(int paramAnonymousInt)
        throws IOException
      {
        this.val$output.write((byte)paramAnonymousInt);
      }
      
      public void flush()
        throws IOException
      {
        this.val$output.flush();
      }
      
      public void close()
        throws IOException
      {
        this.val$output.close();
      }
    };
  }
  
  @GwtIncompatible("Writer")
  static CharOutput asCharOutput(Writer paramWriter)
  {
    Preconditions.checkNotNull(paramWriter);
    new CharOutput()
    {
      public void write(char paramAnonymousChar)
        throws IOException
      {
        this.val$writer.append(paramAnonymousChar);
      }
      
      public void flush()
        throws IOException
      {
        this.val$writer.flush();
      }
      
      public void close()
        throws IOException
      {
        this.val$writer.close();
      }
    };
  }
  
  static CharOutput stringBuilderOutput(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramInt);
    new CharOutput()
    {
      public void write(char paramAnonymousChar)
      {
        this.val$builder.append(paramAnonymousChar);
      }
      
      public void flush() {}
      
      public void close() {}
      
      public String toString()
      {
        return this.val$builder.toString();
      }
    };
  }
  
  static abstract interface CharOutput
  {
    public abstract void write(char paramChar)
      throws IOException;
    
    public abstract void flush()
      throws IOException;
    
    public abstract void close()
      throws IOException;
  }
  
  static abstract interface ByteOutput
  {
    public abstract void write(byte paramByte)
      throws IOException;
    
    public abstract void flush()
      throws IOException;
    
    public abstract void close()
      throws IOException;
  }
  
  static abstract interface ByteInput
  {
    public abstract int read()
      throws IOException;
    
    public abstract void close()
      throws IOException;
  }
  
  static abstract interface CharInput
  {
    public abstract int read()
      throws IOException;
    
    public abstract void close()
      throws IOException;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\GwtWorkarounds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */