package org.apache.commons.codec.binary;

import java.util.Arrays;

public abstract class BaseNCodec
{
  @Deprecated
  protected final byte PAD = 61;
  protected final byte pad;
  private final int unencodedBlockSize;
  private final int encodedBlockSize;
  protected final int lineLength;
  private final int chunkSeparatorLength;
  
  protected BaseNCodec(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (byte)61);
  }
  
  protected BaseNCodec(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte paramByte)
  {
    this.unencodedBlockSize = paramInt1;
    this.encodedBlockSize = paramInt2;
    int i = (paramInt3 > 0) && (paramInt4 > 0) ? 1 : 0;
    this.lineLength = (i != 0 ? paramInt3 / paramInt2 * paramInt2 : 0);
    this.chunkSeparatorLength = paramInt4;
    this.pad = paramByte;
  }
  
  int available(Context paramContext)
  {
    return paramContext.buffer != null ? paramContext.pos - paramContext.readPos : 0;
  }
  
  protected int getDefaultBufferSize()
  {
    return 8192;
  }
  
  private byte[] resizeBuffer(Context paramContext)
  {
    if (paramContext.buffer == null)
    {
      paramContext.buffer = new byte[getDefaultBufferSize()];
      paramContext.pos = 0;
      paramContext.readPos = 0;
    }
    else
    {
      byte[] arrayOfByte = new byte[paramContext.buffer.length * 2];
      System.arraycopy(paramContext.buffer, 0, arrayOfByte, 0, paramContext.buffer.length);
      paramContext.buffer = arrayOfByte;
    }
    return paramContext.buffer;
  }
  
  protected byte[] ensureBufferSize(int paramInt, Context paramContext)
  {
    if ((paramContext.buffer == null) || (paramContext.buffer.length < paramContext.pos + paramInt)) {
      return resizeBuffer(paramContext);
    }
    return paramContext.buffer;
  }
  
  int readResults(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Context paramContext)
  {
    if (paramContext.buffer != null)
    {
      int i = Math.min(available(paramContext), paramInt2);
      System.arraycopy(paramContext.buffer, paramContext.readPos, paramArrayOfByte, paramInt1, i);
      paramContext.readPos += i;
      if (paramContext.readPos >= paramContext.pos) {
        paramContext.buffer = null;
      }
      return i;
    }
    return paramContext.eof ? -1 : 0;
  }
  
  public byte[] encode(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return paramArrayOfByte;
    }
    Context localContext = new Context();
    encode(paramArrayOfByte, 0, paramArrayOfByte.length, localContext);
    encode(paramArrayOfByte, 0, -1, localContext);
    byte[] arrayOfByte = new byte[localContext.pos - localContext.readPos];
    readResults(arrayOfByte, 0, arrayOfByte.length, localContext);
    return arrayOfByte;
  }
  
  abstract void encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Context paramContext);
  
  protected abstract boolean isInAlphabet(byte paramByte);
  
  protected boolean containsAlphabetOrPad(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return false;
    }
    for (byte b : paramArrayOfByte) {
      if ((this.pad == b) || (isInAlphabet(b))) {
        return true;
      }
    }
    return false;
  }
  
  public long getEncodedLength(byte[] paramArrayOfByte)
  {
    long l = (paramArrayOfByte.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize * this.encodedBlockSize;
    if (this.lineLength > 0) {
      l += (l + this.lineLength - 1L) / this.lineLength * this.chunkSeparatorLength;
    }
    return l;
  }
  
  static class Context
  {
    int ibitWorkArea;
    long lbitWorkArea;
    byte[] buffer;
    int pos;
    int readPos;
    boolean eof;
    int currentLinePos;
    int modulus;
    
    public String toString()
    {
      return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", new Object[] { getClass().getSimpleName(), Arrays.toString(this.buffer), Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Long.valueOf(this.lbitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos) });
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\codec\binary\BaseNCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */