package com.google.inject.internal.asm;

final class $AnnotationWriter
  extends .AnnotationVisitor
{
  private final .ClassWriter a;
  private int b;
  private final boolean c;
  private final .ByteVector d;
  private final .ByteVector e;
  private final int f;
  AnnotationWriter g;
  AnnotationWriter h;
  
  $AnnotationWriter(.ClassWriter paramClassWriter, boolean paramBoolean, .ByteVector paramByteVector1, .ByteVector paramByteVector2, int paramInt)
  {
    super(327680);
    this.a = paramClassWriter;
    this.c = paramBoolean;
    this.d = paramByteVector1;
    this.e = paramByteVector2;
    this.f = paramInt;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    this.b += 1;
    if (this.c) {
      this.d.putShort(this.a.newUTF8(paramString));
    }
    if ((paramObject instanceof String))
    {
      this.d.b(115, this.a.newUTF8((String)paramObject));
    }
    else if ((paramObject instanceof Byte))
    {
      this.d.b(66, this.a.a(((Byte)paramObject).byteValue()).a);
    }
    else if ((paramObject instanceof Boolean))
    {
      int i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      this.d.b(90, this.a.a(i).a);
    }
    else if ((paramObject instanceof Character))
    {
      this.d.b(67, this.a.a(((Character)paramObject).charValue()).a);
    }
    else if ((paramObject instanceof Short))
    {
      this.d.b(83, this.a.a(((Short)paramObject).shortValue()).a);
    }
    else if ((paramObject instanceof .Type))
    {
      this.d.b(99, this.a.newUTF8(((.Type)paramObject).getDescriptor()));
    }
    else
    {
      Object localObject;
      int j;
      if ((paramObject instanceof byte[]))
      {
        localObject = (byte[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(66, this.a.a(localObject[j]).a);
        }
      }
      else if ((paramObject instanceof boolean[]))
      {
        localObject = (boolean[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(90, this.a.a(localObject[j] != 0 ? 1 : 0).a);
        }
      }
      else if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(83, this.a.a(localObject[j]).a);
        }
      }
      else if ((paramObject instanceof char[]))
      {
        localObject = (char[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(67, this.a.a(localObject[j]).a);
        }
      }
      else if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(73, this.a.a(localObject[j]).a);
        }
      }
      else if ((paramObject instanceof long[]))
      {
        localObject = (long[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(74, this.a.a(localObject[j]).a);
        }
      }
      else if ((paramObject instanceof float[]))
      {
        localObject = (float[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(70, this.a.a(localObject[j]).a);
        }
      }
      else if ((paramObject instanceof double[]))
      {
        localObject = (double[])paramObject;
        this.d.b(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.d.b(68, this.a.a(localObject[j]).a);
        }
      }
      else
      {
        localObject = this.a.a(paramObject);
        this.d.b(".s.IFJDCS".charAt(((.Item)localObject).b), ((.Item)localObject).a);
      }
    }
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    this.b += 1;
    if (this.c) {
      this.d.putShort(this.a.newUTF8(paramString1));
    }
    this.d.b(101, this.a.newUTF8(paramString2)).putShort(this.a.newUTF8(paramString3));
  }
  
  public .AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    this.b += 1;
    if (this.c) {
      this.d.putShort(this.a.newUTF8(paramString1));
    }
    this.d.b(64, this.a.newUTF8(paramString2)).putShort(0);
    return new AnnotationWriter(this.a, true, this.d, this.d, this.d.b - 2);
  }
  
  public .AnnotationVisitor visitArray(String paramString)
  {
    this.b += 1;
    if (this.c) {
      this.d.putShort(this.a.newUTF8(paramString));
    }
    this.d.b(91, 0);
    return new AnnotationWriter(this.a, false, this.d, this.d, this.d.b - 2);
  }
  
  public void visitEnd()
  {
    if (this.e != null)
    {
      byte[] arrayOfByte = this.e.a;
      arrayOfByte[this.f] = ((byte)(this.b >>> 8));
      arrayOfByte[(this.f + 1)] = ((byte)this.b);
    }
  }
  
  int a()
  {
    int i = 0;
    for (AnnotationWriter localAnnotationWriter = this; localAnnotationWriter != null; localAnnotationWriter = localAnnotationWriter.g) {
      i += localAnnotationWriter.d.b;
    }
    return i;
  }
  
  void a(.ByteVector paramByteVector)
  {
    int i = 0;
    int j = 2;
    Object localObject1 = this;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      i++;
      j += ((AnnotationWriter)localObject1).d.b;
      ((AnnotationWriter)localObject1).visitEnd();
      ((AnnotationWriter)localObject1).h = ((AnnotationWriter)localObject2);
      localObject2 = localObject1;
      localObject1 = ((AnnotationWriter)localObject1).g;
    }
    paramByteVector.putInt(j);
    paramByteVector.putShort(i);
    for (localObject1 = localObject2; localObject1 != null; localObject1 = ((AnnotationWriter)localObject1).h) {
      paramByteVector.putByteArray(((AnnotationWriter)localObject1).d.a, 0, ((AnnotationWriter)localObject1).d.b);
    }
  }
  
  static void a(AnnotationWriter[] paramArrayOfAnnotationWriter, int paramInt, .ByteVector paramByteVector)
  {
    int i = 1 + 2 * (paramArrayOfAnnotationWriter.length - paramInt);
    for (int j = paramInt; j < paramArrayOfAnnotationWriter.length; j++) {
      i += (paramArrayOfAnnotationWriter[j] == null ? 0 : paramArrayOfAnnotationWriter[j].a());
    }
    paramByteVector.putInt(i).putByte(paramArrayOfAnnotationWriter.length - paramInt);
    for (j = paramInt; j < paramArrayOfAnnotationWriter.length; j++)
    {
      Object localObject1 = paramArrayOfAnnotationWriter[j];
      Object localObject2 = null;
      int k = 0;
      while (localObject1 != null)
      {
        k++;
        ((AnnotationWriter)localObject1).visitEnd();
        ((AnnotationWriter)localObject1).h = ((AnnotationWriter)localObject2);
        localObject2 = localObject1;
        localObject1 = ((AnnotationWriter)localObject1).g;
      }
      paramByteVector.putShort(k);
      for (localObject1 = localObject2; localObject1 != null; localObject1 = ((AnnotationWriter)localObject1).h) {
        paramByteVector.putByteArray(((AnnotationWriter)localObject1).d.a, 0, ((AnnotationWriter)localObject1).d.b);
      }
    }
  }
  
  static void a(int paramInt, .TypePath paramTypePath, .ByteVector paramByteVector)
  {
    switch (paramInt >>> 24)
    {
    case 0: 
    case 1: 
    case 22: 
      paramByteVector.putShort(paramInt >>> 16);
      break;
    case 19: 
    case 20: 
    case 21: 
      paramByteVector.putByte(paramInt >>> 24);
      break;
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
      paramByteVector.putInt(paramInt);
      break;
    default: 
      paramByteVector.b(paramInt >>> 24, (paramInt & 0xFFFF00) >> 8);
    }
    if (paramTypePath == null)
    {
      paramByteVector.putByte(0);
    }
    else
    {
      int i = paramTypePath.a[paramTypePath.b] * 2 + 1;
      paramByteVector.putByteArray(paramTypePath.a, paramTypePath.b, i);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$AnnotationWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */