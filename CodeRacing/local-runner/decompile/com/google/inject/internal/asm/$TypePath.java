package com.google.inject.internal.asm;

public class $TypePath
{
  public static final int ARRAY_ELEMENT = 0;
  public static final int INNER_TYPE = 1;
  public static final int WILDCARD_BOUND = 2;
  public static final int TYPE_ARGUMENT = 3;
  byte[] a;
  int b;
  
  $TypePath(byte[] paramArrayOfByte, int paramInt)
  {
    this.a = paramArrayOfByte;
    this.b = paramInt;
  }
  
  public int getLength()
  {
    return this.a[this.b];
  }
  
  public int getStep(int paramInt)
  {
    return this.a[(this.b + 2 * paramInt + 1)];
  }
  
  public int getStepArgument(int paramInt)
  {
    return this.a[(this.b + 2 * paramInt + 2)];
  }
  
  public static TypePath fromString(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    int i = paramString.length();
    .ByteVector localByteVector = new .ByteVector(i);
    localByteVector.putByte(0);
    int j = 0;
    while (j < i)
    {
      int k = paramString.charAt(j++);
      if (k == 91)
      {
        localByteVector.a(0, 0);
      }
      else if (k == 46)
      {
        localByteVector.a(1, 0);
      }
      else if (k == 42)
      {
        localByteVector.a(2, 0);
      }
      else if ((k >= 48) && (k <= 57))
      {
        int m = k - 48;
        while ((j < i) && ((k = paramString.charAt(j)) >= '0') && (k <= 57))
        {
          m = m * 10 + k - 48;
          j++;
        }
        localByteVector.a(3, m);
      }
    }
    localByteVector.a[0] = ((byte)(localByteVector.b / 2));
    return new TypePath(localByteVector.a, 0);
  }
  
  public String toString()
  {
    int i = getLength();
    StringBuffer localStringBuffer = new StringBuffer(i * 2);
    for (int j = 0; j < i; j++) {
      switch (getStep(j))
      {
      case 0: 
        localStringBuffer.append('[');
        break;
      case 1: 
        localStringBuffer.append('.');
        break;
      case 2: 
        localStringBuffer.append('*');
        break;
      case 3: 
        localStringBuffer.append(getStepArgument(j));
        break;
      default: 
        localStringBuffer.append('_');
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$TypePath.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */