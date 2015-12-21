package com.google.inject.internal.asm;

public class $Label
{
  public Object info;
  int a;
  int b;
  int c;
  private int d;
  private int[] e;
  int f;
  int g;
  .Frame h;
  Label i;
  .Edge j;
  Label k;
  
  public int getOffset()
  {
    if ((this.a & 0x2) == 0) {
      throw new IllegalStateException("Label offset position has not been resolved yet");
    }
    return this.c;
  }
  
  void a(.MethodWriter paramMethodWriter, .ByteVector paramByteVector, int paramInt, boolean paramBoolean)
  {
    if ((this.a & 0x2) == 0)
    {
      if (paramBoolean)
      {
        a(-1 - paramInt, paramByteVector.b);
        paramByteVector.putInt(-1);
      }
      else
      {
        a(paramInt, paramByteVector.b);
        paramByteVector.putShort(-1);
      }
    }
    else if (paramBoolean) {
      paramByteVector.putInt(this.c - paramInt);
    } else {
      paramByteVector.putShort(this.c - paramInt);
    }
  }
  
  private void a(int paramInt1, int paramInt2)
  {
    if (this.e == null) {
      this.e = new int[6];
    }
    if (this.d >= this.e.length)
    {
      int[] arrayOfInt = new int[this.e.length + 6];
      System.arraycopy(this.e, 0, arrayOfInt, 0, this.e.length);
      this.e = arrayOfInt;
    }
    this.e[(this.d++)] = paramInt1;
    this.e[(this.d++)] = paramInt2;
  }
  
  boolean a(.MethodWriter paramMethodWriter, int paramInt, byte[] paramArrayOfByte)
  {
    boolean bool = false;
    this.a |= 0x2;
    this.c = paramInt;
    int m = 0;
    while (m < this.d)
    {
      int n = this.e[(m++)];
      int i1 = this.e[(m++)];
      int i2;
      if (n >= 0)
      {
        i2 = paramInt - n;
        if ((i2 < 32768) || (i2 > 32767))
        {
          int i3 = paramArrayOfByte[(i1 - 1)] & 0xFF;
          if (i3 <= 168) {
            paramArrayOfByte[(i1 - 1)] = ((byte)(i3 + 49));
          } else {
            paramArrayOfByte[(i1 - 1)] = ((byte)(i3 + 20));
          }
          bool = true;
        }
        paramArrayOfByte[(i1++)] = ((byte)(i2 >>> 8));
        paramArrayOfByte[i1] = ((byte)i2);
      }
      else
      {
        i2 = paramInt + n + 1;
        paramArrayOfByte[(i1++)] = ((byte)(i2 >>> 24));
        paramArrayOfByte[(i1++)] = ((byte)(i2 >>> 16));
        paramArrayOfByte[(i1++)] = ((byte)(i2 >>> 8));
        paramArrayOfByte[i1] = ((byte)i2);
      }
    }
    return bool;
  }
  
  Label a()
  {
    return this.h == null ? this : this.h.b;
  }
  
  boolean a(long paramLong)
  {
    if ((this.a & 0x400) != 0) {
      return (this.e[((int)(paramLong >>> 32))] & (int)paramLong) != 0;
    }
    return false;
  }
  
  boolean a(Label paramLabel)
  {
    if (((this.a & 0x400) == 0) || ((paramLabel.a & 0x400) == 0)) {
      return false;
    }
    for (int m = 0; m < this.e.length; m++) {
      if ((this.e[m] & paramLabel.e[m]) != 0) {
        return true;
      }
    }
    return false;
  }
  
  void a(long paramLong, int paramInt)
  {
    if ((this.a & 0x400) == 0)
    {
      this.a |= 0x400;
      this.e = new int[paramInt / 32 + 1];
    }
    this.e[((int)(paramLong >>> 32))] |= (int)paramLong;
  }
  
  void b(Label paramLabel, long paramLong, int paramInt)
  {
    Label localLabel1 = this;
    while (localLabel1 != null)
    {
      Label localLabel2 = localLabel1;
      localLabel1 = localLabel2.k;
      localLabel2.k = null;
      if (paramLabel != null)
      {
        if ((localLabel2.a & 0x800) != 0) {
          continue;
        }
        localLabel2.a |= 0x800;
        if (((localLabel2.a & 0x100) != 0) && (!localLabel2.a(paramLabel)))
        {
          localEdge = new .Edge();
          localEdge.a = localLabel2.f;
          localEdge.b = paramLabel.j.b;
          localEdge.c = localLabel2.j;
          localLabel2.j = localEdge;
        }
      }
      else
      {
        if (localLabel2.a(paramLong)) {
          continue;
        }
        localLabel2.a(paramLong, paramInt);
      }
      for (.Edge localEdge = localLabel2.j; localEdge != null; localEdge = localEdge.c) {
        if ((((localLabel2.a & 0x80) == 0) || (localEdge != localLabel2.j.c)) && (localEdge.b.k == null))
        {
          localEdge.b.k = localLabel1;
          localLabel1 = localEdge.b;
        }
      }
    }
  }
  
  public String toString()
  {
    return "L" + System.identityHashCode(this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$Label.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */