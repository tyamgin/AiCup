package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.OutputStream;

@Beta
public final class Funnels
{
  public static Funnel byteArrayFunnel()
  {
    return ByteArrayFunnel.INSTANCE;
  }
  
  public static Funnel stringFunnel()
  {
    return StringFunnel.INSTANCE;
  }
  
  public static Funnel integerFunnel()
  {
    return IntegerFunnel.INSTANCE;
  }
  
  public static Funnel longFunnel()
  {
    return LongFunnel.INSTANCE;
  }
  
  public static OutputStream asOutputStream(PrimitiveSink paramPrimitiveSink)
  {
    return new SinkAsStream(paramPrimitiveSink);
  }
  
  private static class SinkAsStream
    extends OutputStream
  {
    final PrimitiveSink sink;
    
    SinkAsStream(PrimitiveSink paramPrimitiveSink)
    {
      this.sink = ((PrimitiveSink)Preconditions.checkNotNull(paramPrimitiveSink));
    }
    
    public void write(int paramInt)
    {
      this.sink.putByte((byte)paramInt);
    }
    
    public void write(byte[] paramArrayOfByte)
    {
      this.sink.putBytes(paramArrayOfByte);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      this.sink.putBytes(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public String toString()
    {
      return "Funnels.asOutputStream(" + this.sink + ")";
    }
  }
  
  private static enum LongFunnel
    implements Funnel
  {
    INSTANCE;
    
    public void funnel(Long paramLong, PrimitiveSink paramPrimitiveSink)
    {
      paramPrimitiveSink.putLong(paramLong.longValue());
    }
    
    public String toString()
    {
      return "Funnels.longFunnel()";
    }
  }
  
  private static enum IntegerFunnel
    implements Funnel
  {
    INSTANCE;
    
    public void funnel(Integer paramInteger, PrimitiveSink paramPrimitiveSink)
    {
      paramPrimitiveSink.putInt(paramInteger.intValue());
    }
    
    public String toString()
    {
      return "Funnels.integerFunnel()";
    }
  }
  
  private static enum StringFunnel
    implements Funnel
  {
    INSTANCE;
    
    public void funnel(CharSequence paramCharSequence, PrimitiveSink paramPrimitiveSink)
    {
      paramPrimitiveSink.putString(paramCharSequence);
    }
    
    public String toString()
    {
      return "Funnels.stringFunnel()";
    }
  }
  
  private static enum ByteArrayFunnel
    implements Funnel
  {
    INSTANCE;
    
    public void funnel(byte[] paramArrayOfByte, PrimitiveSink paramPrimitiveSink)
    {
      paramPrimitiveSink.putBytes(paramArrayOfByte);
    }
    
    public String toString()
    {
      return "Funnels.byteArrayFunnel()";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\Funnels.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */