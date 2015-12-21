package com.google.inject.internal.asm;

public class $Attribute
{
  public final String type;
  byte[] b;
  Attribute a;
  
  protected $Attribute(String paramString)
  {
    this.type = paramString;
  }
  
  public boolean isUnknown()
  {
    return true;
  }
  
  public boolean isCodeAttribute()
  {
    return false;
  }
  
  protected .Label[] getLabels()
  {
    return null;
  }
  
  protected Attribute read(.ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, .Label[] paramArrayOfLabel)
  {
    Attribute localAttribute = new Attribute(this.type);
    localAttribute.b = new byte[paramInt2];
    System.arraycopy(paramClassReader.b, paramInt1, localAttribute.b, 0, paramInt2);
    return localAttribute;
  }
  
  protected .ByteVector write(.ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    .ByteVector localByteVector = new .ByteVector();
    localByteVector.a = this.b;
    localByteVector.b = this.b.length;
    return localByteVector;
  }
  
  final int a()
  {
    int i = 0;
    for (Attribute localAttribute = this; localAttribute != null; localAttribute = localAttribute.a) {
      i++;
    }
    return i;
  }
  
  final int a(.ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    Attribute localAttribute = this;
    int i = 0;
    while (localAttribute != null)
    {
      paramClassWriter.newUTF8(localAttribute.type);
      i += localAttribute.write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3).b + 6;
      localAttribute = localAttribute.a;
    }
    return i;
  }
  
  final void a(.ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, .ByteVector paramByteVector)
  {
    for (Attribute localAttribute = this; localAttribute != null; localAttribute = localAttribute.a)
    {
      .ByteVector localByteVector = localAttribute.write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      paramByteVector.putShort(paramClassWriter.newUTF8(localAttribute.type)).putInt(localByteVector.b);
      paramByteVector.putByteArray(localByteVector.a, 0, localByteVector.b);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$Attribute.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */