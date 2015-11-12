package com.google.inject.internal.asm;

public class $ByteVector
{
  byte[] a;
  int b;
  
  public $ByteVector()
  {
    this.a = new byte[64];
  }
  
  public $ByteVector(int paramInt)
  {
    this.a = new byte[paramInt];
  }
  
  public ByteVector putByte(int paramInt)
  {
    int i = this.b;
    if (i + 1 > this.a.length) {
      a(1);
    }
    this.a[(i++)] = ((byte)paramInt);
    this.b = i;
    return this;
  }
  
  ByteVector a(int paramInt1, int paramInt2)
  {
    int i = this.b;
    if (i + 2 > this.a.length) {
      a(2);
    }
    byte[] arrayOfByte = this.a;
    arrayOfByte[(i++)] = ((byte)paramInt1);
    arrayOfByte[(i++)] = ((byte)paramInt2);
    this.b = i;
    return this;
  }
  
  public ByteVector putShort(int paramInt)
  {
    int i = this.b;
    if (i + 2 > this.a.length) {
      a(2);
    }
    byte[] arrayOfByte = this.a;
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 8));
    arrayOfByte[(i++)] = ((byte)paramInt);
    this.b = i;
    return this;
  }
  
  ByteVector b(int paramInt1, int paramInt2)
  {
    int i = this.b;
    if (i + 3 > this.a.length) {
      a(3);
    }
    byte[] arrayOfByte = this.a;
    arrayOfByte[(i++)] = ((byte)paramInt1);
    arrayOfByte[(i++)] = ((byte)(paramInt2 >>> 8));
    arrayOfByte[(i++)] = ((byte)paramInt2);
    this.b = i;
    return this;
  }
  
  public ByteVector putInt(int paramInt)
  {
    int i = this.b;
    if (i + 4 > this.a.length) {
      a(4);
    }
    byte[] arrayOfByte = this.a;
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 24));
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 16));
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 8));
    arrayOfByte[(i++)] = ((byte)paramInt);
    this.b = i;
    return this;
  }
  
  public ByteVector putLong(long paramLong)
  {
    int i = this.b;
    if (i + 8 > this.a.length) {
      a(8);
    }
    byte[] arrayOfByte = this.a;
    int j = (int)(paramLong >>> 32);
    arrayOfByte[(i++)] = ((byte)(j >>> 24));
    arrayOfByte[(i++)] = ((byte)(j >>> 16));
    arrayOfByte[(i++)] = ((byte)(j >>> 8));
    arrayOfByte[(i++)] = ((byte)j);
    j = (int)paramLong;
    arrayOfByte[(i++)] = ((byte)(j >>> 24));
    arrayOfByte[(i++)] = ((byte)(j >>> 16));
    arrayOfByte[(i++)] = ((byte)(j >>> 8));
    arrayOfByte[(i++)] = ((byte)j);
    this.b = i;
    return this;
  }
  
  public ByteVector putUTF8(String paramString)
  {
    int i = paramString.length();
    if (i > 65535) {
      throw new IllegalArgumentException();
    }
    int j = this.b;
    if (j + 2 + i > this.a.length) {
      a(2 + i);
    }
    byte[] arrayOfByte = this.a;
    arrayOfByte[(j++)] = ((byte)(i >>> 8));
    arrayOfByte[(j++)] = ((byte)i);
    for (int k = 0; k < i; k++)
    {
      int m = paramString.charAt(k);
      if ((m >= 1) && (m <= 127))
      {
        arrayOfByte[(j++)] = ((byte)m);
      }
      else
      {
        this.b = j;
        return encodeUTF8(paramString, k, 65535);
      }
    }
    this.b = j;
    return this;
  }
  
  ByteVector encodeUTF8(String paramString, int paramInt1, int paramInt2)
  {
    int i = paramString.length();
    int j = paramInt1;
    int m;
    for (int k = paramInt1; k < i; k++)
    {
      m = paramString.charAt(k);
      if ((m >= 1) && (m <= 127)) {
        j++;
      } else if (m > 2047) {
        j += 3;
      } else {
        j += 2;
      }
    }
    if (j > paramInt2) {
      throw new IllegalArgumentException();
    }
    k = this.b - paramInt1 - 2;
    if (k >= 0)
    {
      this.a[k] = ((byte)(j >>> 8));
      this.a[(k + 1)] = ((byte)j);
    }
    if (this.b + j - paramInt1 > this.a.length) {
      a(j - paramInt1);
    }
    int n = this.b;
    for (int i1 = paramInt1; i1 < i; i1++)
    {
      m = paramString.charAt(i1);
      if ((m >= 1) && (m <= 127))
      {
        this.a[(n++)] = ((byte)m);
      }
      else if (m > 2047)
      {
        this.a[(n++)] = ((byte)(0xE0 | m >> 12 & 0xF));
        this.a[(n++)] = ((byte)(0x80 | m >> 6 & 0x3F));
        this.a[(n++)] = ((byte)(0x80 | m & 0x3F));
      }
      else
      {
        this.a[(n++)] = ((byte)(0xC0 | m >> 6 & 0x1F));
        this.a[(n++)] = ((byte)(0x80 | m & 0x3F));
      }
    }
    this.b = n;
    return this;
  }
  
  public ByteVector putByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.b + paramInt2 > this.a.length) {
      a(paramInt2);
    }
    if (paramArrayOfByte != null) {
      System.arraycopy(paramArrayOfByte, paramInt1, this.a, this.b, paramInt2);
    }
    this.b += paramInt2;
    return this;
  }
  
  private void a(int paramInt)
  {
    int i = 2 * this.a.length;
    int j = this.b + paramInt;
    byte[] arrayOfByte = new byte[i > j ? i : j];
    System.arraycopy(this.a, 0, arrayOfByte, 0, this.b);
    this.a = arrayOfByte;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$ByteVector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */