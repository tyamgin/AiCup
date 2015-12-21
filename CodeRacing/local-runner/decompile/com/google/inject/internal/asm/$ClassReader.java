package com.google.inject.internal.asm;

import java.io.IOException;
import java.io.InputStream;

public class $ClassReader
{
  public static final int SKIP_CODE = 1;
  public static final int SKIP_DEBUG = 2;
  public static final int SKIP_FRAMES = 4;
  public static final int EXPAND_FRAMES = 8;
  public final byte[] b;
  private final int[] a;
  private final String[] c;
  private final int d;
  public final int header;
  
  public $ClassReader(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public $ClassReader(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.b = paramArrayOfByte;
    if (readShort(paramInt1 + 6) > 52) {
      throw new IllegalArgumentException();
    }
    this.a = new int[readUnsignedShort(paramInt1 + 8)];
    int i = this.a.length;
    this.c = new String[i];
    int j = 0;
    int k = paramInt1 + 10;
    for (int m = 1; m < i; m++)
    {
      this.a[m] = (k + 1);
      int n;
      switch (paramArrayOfByte[k])
      {
      case 3: 
      case 4: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 18: 
        n = 5;
        break;
      case 5: 
      case 6: 
        n = 9;
        m++;
        break;
      case 1: 
        n = 3 + readUnsignedShort(k + 1);
        if (n > j) {
          j = n;
        }
        break;
      case 15: 
        n = 4;
        break;
      case 2: 
      case 7: 
      case 8: 
      case 13: 
      case 14: 
      case 16: 
      case 17: 
      default: 
        n = 3;
      }
      k += n;
    }
    this.d = j;
    this.header = k;
  }
  
  public int getAccess()
  {
    return readUnsignedShort(this.header);
  }
  
  public String getClassName()
  {
    return readClass(this.header + 2, new char[this.d]);
  }
  
  public String getSuperName()
  {
    return readClass(this.header + 4, new char[this.d]);
  }
  
  public String[] getInterfaces()
  {
    int i = this.header + 6;
    int j = readUnsignedShort(i);
    String[] arrayOfString = new String[j];
    if (j > 0)
    {
      char[] arrayOfChar = new char[this.d];
      for (int k = 0; k < j; k++)
      {
        i += 2;
        arrayOfString[k] = readClass(i, arrayOfChar);
      }
    }
    return arrayOfString;
  }
  
  void a(.ClassWriter paramClassWriter)
  {
    char[] arrayOfChar = new char[this.d];
    int i = this.a.length;
    .Item[] arrayOfItem = new .Item[i];
    for (int j = 1; j < i; j++)
    {
      int k = this.a[j];
      int m = this.b[(k - 1)];
      .Item localItem = new .Item(j);
      int n;
      switch (m)
      {
      case 9: 
      case 10: 
      case 11: 
        n = this.a[readUnsignedShort(k + 2)];
        localItem.a(m, readClass(k, arrayOfChar), readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar));
        break;
      case 3: 
        localItem.a(readInt(k));
        break;
      case 4: 
        localItem.a(Float.intBitsToFloat(readInt(k)));
        break;
      case 12: 
        localItem.a(m, readUTF8(k, arrayOfChar), readUTF8(k + 2, arrayOfChar), null);
        break;
      case 5: 
        localItem.a(readLong(k));
        j++;
        break;
      case 6: 
        localItem.a(Double.longBitsToDouble(readLong(k)));
        j++;
        break;
      case 1: 
        String str = this.c[j];
        if (str == null)
        {
          k = this.a[j];
          str = this.c[j] = a(k + 2, readUnsignedShort(k), arrayOfChar);
        }
        localItem.a(m, str, null, null);
        break;
      case 15: 
        i1 = this.a[readUnsignedShort(k + 1)];
        n = this.a[readUnsignedShort(i1 + 2)];
        localItem.a(20 + readByte(k), readClass(i1, arrayOfChar), readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar));
        break;
      case 18: 
        if (paramClassWriter.A == null) {
          a(paramClassWriter, arrayOfItem, arrayOfChar);
        }
        n = this.a[readUnsignedShort(k + 2)];
        localItem.a(readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar), readUnsignedShort(k));
        break;
      case 2: 
      case 7: 
      case 8: 
      case 13: 
      case 14: 
      case 16: 
      case 17: 
      default: 
        localItem.a(m, readUTF8(k, arrayOfChar), null, null);
      }
      int i1 = localItem.j % arrayOfItem.length;
      localItem.k = arrayOfItem[i1];
      arrayOfItem[i1] = localItem;
    }
    j = this.a[1] - 1;
    paramClassWriter.d.putByteArray(this.b, j, this.header - j);
    paramClassWriter.e = arrayOfItem;
    paramClassWriter.f = ((int)(0.75D * i));
    paramClassWriter.c = i;
  }
  
  private void a(.ClassWriter paramClassWriter, .Item[] paramArrayOfItem, char[] paramArrayOfChar)
  {
    int i = a();
    int j = 0;
    for (int k = readUnsignedShort(i); k > 0; k--)
    {
      String str = readUTF8(i + 2, paramArrayOfChar);
      if ("BootstrapMethods".equals(str))
      {
        j = 1;
        break;
      }
      i += 6 + readInt(i + 4);
    }
    if (j == 0) {
      return;
    }
    k = readUnsignedShort(i + 8);
    int m = 0;
    int n = i + 10;
    while (m < k)
    {
      int i1 = n - i - 10;
      int i2 = readConst(readUnsignedShort(n), paramArrayOfChar).hashCode();
      for (int i3 = readUnsignedShort(n + 2); i3 > 0; i3--)
      {
        i2 ^= readConst(readUnsignedShort(n + 4), paramArrayOfChar).hashCode();
        n += 2;
      }
      n += 4;
      .Item localItem = new .Item(m);
      localItem.a(i1, i2 & 0x7FFFFFFF);
      int i4 = localItem.j % paramArrayOfItem.length;
      localItem.k = paramArrayOfItem[i4];
      paramArrayOfItem[i4] = localItem;
      m++;
    }
    m = readInt(i + 4);
    .ByteVector localByteVector = new .ByteVector(m + 62);
    localByteVector.putByteArray(this.b, i + 10, m - 2);
    paramClassWriter.z = k;
    paramClassWriter.A = localByteVector;
  }
  
  public $ClassReader(InputStream paramInputStream)
    throws IOException
  {
    this(a(paramInputStream, false));
  }
  
  public $ClassReader(String paramString)
    throws IOException
  {
    this(a(ClassLoader.getSystemResourceAsStream(paramString.replace('.', '/') + ".class"), true));
  }
  
  /* Error */
  private static byte[] a(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: new 55	java/io/IOException
    //   7: dup
    //   8: ldc 15
    //   10: invokespecial 226	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aload_0
    //   15: invokevirtual 227	java/io/InputStream:available	()I
    //   18: newarray <illegal type>
    //   20: astore_2
    //   21: iconst_0
    //   22: istore_3
    //   23: aload_0
    //   24: aload_2
    //   25: iload_3
    //   26: aload_2
    //   27: arraylength
    //   28: iload_3
    //   29: isub
    //   30: invokevirtual 230	java/io/InputStream:read	([BII)I
    //   33: istore 4
    //   35: iload 4
    //   37: iconst_m1
    //   38: if_icmpne +40 -> 78
    //   41: iload_3
    //   42: aload_2
    //   43: arraylength
    //   44: if_icmpge +20 -> 64
    //   47: iload_3
    //   48: newarray <illegal type>
    //   50: astore 5
    //   52: aload_2
    //   53: iconst_0
    //   54: aload 5
    //   56: iconst_0
    //   57: iload_3
    //   58: invokestatic 252	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   61: aload 5
    //   63: astore_2
    //   64: aload_2
    //   65: astore 5
    //   67: iload_1
    //   68: ifeq +7 -> 75
    //   71: aload_0
    //   72: invokevirtual 228	java/io/InputStream:close	()V
    //   75: aload 5
    //   77: areturn
    //   78: iload_3
    //   79: iload 4
    //   81: iadd
    //   82: istore_3
    //   83: iload_3
    //   84: aload_2
    //   85: arraylength
    //   86: if_icmpne +60 -> 146
    //   89: aload_0
    //   90: invokevirtual 229	java/io/InputStream:read	()I
    //   93: istore 5
    //   95: iload 5
    //   97: ifge +17 -> 114
    //   100: aload_2
    //   101: astore 6
    //   103: iload_1
    //   104: ifeq +7 -> 111
    //   107: aload_0
    //   108: invokevirtual 228	java/io/InputStream:close	()V
    //   111: aload 6
    //   113: areturn
    //   114: aload_2
    //   115: arraylength
    //   116: sipush 1000
    //   119: iadd
    //   120: newarray <illegal type>
    //   122: astore 6
    //   124: aload_2
    //   125: iconst_0
    //   126: aload 6
    //   128: iconst_0
    //   129: iload_3
    //   130: invokestatic 252	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   133: aload 6
    //   135: iload_3
    //   136: iinc 3 1
    //   139: iload 5
    //   141: i2b
    //   142: bastore
    //   143: aload 6
    //   145: astore_2
    //   146: goto -123 -> 23
    //   149: astore 7
    //   151: iload_1
    //   152: ifeq +7 -> 159
    //   155: aload_0
    //   156: invokevirtual 228	java/io/InputStream:close	()V
    //   159: aload 7
    //   161: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	162	0	paramInputStream	InputStream
    //   0	162	1	paramBoolean	boolean
    //   20	126	2	localObject1	Object
    //   22	114	3	i	int
    //   33	49	4	j	int
    //   50	26	5	localObject2	Object
    //   93	47	5	k	int
    //   101	43	6	localObject3	Object
    //   149	11	7	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   14	67	149	finally
    //   78	103	149	finally
    //   114	151	149	finally
  }
  
  public void accept(.ClassVisitor paramClassVisitor, int paramInt)
  {
    accept(paramClassVisitor, new .Attribute[0], paramInt);
  }
  
  public void accept(.ClassVisitor paramClassVisitor, .Attribute[] paramArrayOfAttribute, int paramInt)
  {
    int i = this.header;
    char[] arrayOfChar = new char[this.d];
    .Context localContext = new .Context();
    localContext.a = paramArrayOfAttribute;
    localContext.b = paramInt;
    localContext.c = arrayOfChar;
    int j = readUnsignedShort(i);
    String str1 = readClass(i + 2, arrayOfChar);
    String str2 = readClass(i + 4, arrayOfChar);
    String[] arrayOfString = new String[readUnsignedShort(i + 6)];
    i += 8;
    for (int k = 0; k < arrayOfString.length; k++)
    {
      arrayOfString[k] = readClass(i, arrayOfChar);
      i += 2;
    }
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    Object localObject1 = null;
    i = a();
    for (int i4 = readUnsignedShort(i); i4 > 0; i4--)
    {
      String str9 = readUTF8(i + 2, arrayOfChar);
      if ("SourceFile".equals(str9))
      {
        str4 = readUTF8(i + 8, arrayOfChar);
      }
      else if ("InnerClasses".equals(str9))
      {
        i3 = i + 8;
      }
      else
      {
        int i7;
        if ("EnclosingMethod".equals(str9))
        {
          str6 = readClass(i + 8, arrayOfChar);
          i7 = readUnsignedShort(i + 10);
          if (i7 != 0)
          {
            str7 = readUTF8(this.a[i7], arrayOfChar);
            str8 = readUTF8(this.a[i7] + 2, arrayOfChar);
          }
        }
        else if ("Signature".equals(str9))
        {
          str3 = readUTF8(i + 8, arrayOfChar);
        }
        else if ("RuntimeVisibleAnnotations".equals(str9))
        {
          m = i + 8;
        }
        else if ("RuntimeVisibleTypeAnnotations".equals(str9))
        {
          i1 = i + 8;
        }
        else if ("Deprecated".equals(str9))
        {
          j |= 0x20000;
        }
        else if ("Synthetic".equals(str9))
        {
          j |= 0x41000;
        }
        else if ("SourceDebugExtension".equals(str9))
        {
          i7 = readInt(i + 4);
          str5 = a(i + 8, i7, new char[i7]);
        }
        else if ("RuntimeInvisibleAnnotations".equals(str9))
        {
          n = i + 8;
        }
        else if ("RuntimeInvisibleTypeAnnotations".equals(str9))
        {
          i2 = i + 8;
        }
        else
        {
          Object localObject2;
          if ("BootstrapMethods".equals(str9))
          {
            localObject2 = new int[readUnsignedShort(i + 8)];
            int i8 = 0;
            int i9 = i + 10;
            while (i8 < localObject2.length)
            {
              localObject2[i8] = i9;
              i9 += (2 + readUnsignedShort(i9 + 2) << 1);
              i8++;
            }
            localContext.d = ((int[])localObject2);
          }
          else
          {
            localObject2 = a(paramArrayOfAttribute, str9, i + 8, readInt(i + 4), arrayOfChar, -1, null);
            if (localObject2 != null)
            {
              ((.Attribute)localObject2).a = ((.Attribute)localObject1);
              localObject1 = localObject2;
            }
          }
        }
      }
      i += 6 + readInt(i + 4);
    }
    paramClassVisitor.visit(readInt(this.a[1] - 7), j, str1, str3, str2, arrayOfString);
    if (((paramInt & 0x2) == 0) && ((str4 != null) || (str5 != null))) {
      paramClassVisitor.visitSource(str4, str5);
    }
    if (str6 != null) {
      paramClassVisitor.visitOuterClass(str6, str7, str8);
    }
    int i6;
    if (m != 0)
    {
      i4 = readUnsignedShort(m);
      i6 = m + 2;
      while (i4 > 0)
      {
        i6 = a(i6 + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(i6, arrayOfChar), true));
        i4--;
      }
    }
    if (n != 0)
    {
      i4 = readUnsignedShort(n);
      i6 = n + 2;
      while (i4 > 0)
      {
        i6 = a(i6 + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(i6, arrayOfChar), false));
        i4--;
      }
    }
    if (i1 != 0)
    {
      i4 = readUnsignedShort(i1);
      i6 = i1 + 2;
      while (i4 > 0)
      {
        i6 = a(localContext, i6);
        i6 = a(i6 + 2, arrayOfChar, true, paramClassVisitor.visitTypeAnnotation(localContext.i, localContext.j, readUTF8(i6, arrayOfChar), true));
        i4--;
      }
    }
    if (i2 != 0)
    {
      i4 = readUnsignedShort(i2);
      i6 = i2 + 2;
      while (i4 > 0)
      {
        i6 = a(localContext, i6);
        i6 = a(i6 + 2, arrayOfChar, true, paramClassVisitor.visitTypeAnnotation(localContext.i, localContext.j, readUTF8(i6, arrayOfChar), false));
        i4--;
      }
    }
    while (localObject1 != null)
    {
      .Attribute localAttribute = ((.Attribute)localObject1).a;
      ((.Attribute)localObject1).a = null;
      paramClassVisitor.visitAttribute((.Attribute)localObject1);
      localObject1 = localAttribute;
    }
    if (i3 != 0)
    {
      i5 = i3 + 2;
      for (i6 = readUnsignedShort(i3); i6 > 0; i6--)
      {
        paramClassVisitor.visitInnerClass(readClass(i5, arrayOfChar), readClass(i5 + 2, arrayOfChar), readUTF8(i5 + 4, arrayOfChar), readUnsignedShort(i5 + 6));
        i5 += 8;
      }
    }
    i = this.header + 10 + 2 * arrayOfString.length;
    for (int i5 = readUnsignedShort(i - 2); i5 > 0; i5--) {
      i = a(paramClassVisitor, localContext, i);
    }
    i += 2;
    for (i5 = readUnsignedShort(i - 2); i5 > 0; i5--) {
      i = b(paramClassVisitor, localContext, i);
    }
    paramClassVisitor.visitEnd();
  }
  
  private int a(.ClassVisitor paramClassVisitor, .Context paramContext, int paramInt)
  {
    char[] arrayOfChar = paramContext.c;
    int i = readUnsignedShort(paramInt);
    String str1 = readUTF8(paramInt + 2, arrayOfChar);
    String str2 = readUTF8(paramInt + 4, arrayOfChar);
    paramInt += 6;
    String str3 = null;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    for (int i1 = readUnsignedShort(paramInt); i1 > 0; i1--)
    {
      String str4 = readUTF8(paramInt + 2, arrayOfChar);
      if ("ConstantValue".equals(str4))
      {
        int i3 = readUnsignedShort(paramInt + 8);
        localObject1 = i3 == 0 ? null : readConst(i3, arrayOfChar);
      }
      else if ("Signature".equals(str4))
      {
        str3 = readUTF8(paramInt + 8, arrayOfChar);
      }
      else if ("Deprecated".equals(str4))
      {
        i |= 0x20000;
      }
      else if ("Synthetic".equals(str4))
      {
        i |= 0x41000;
      }
      else if ("RuntimeVisibleAnnotations".equals(str4))
      {
        j = paramInt + 8;
      }
      else if ("RuntimeVisibleTypeAnnotations".equals(str4))
      {
        m = paramInt + 8;
      }
      else if ("RuntimeInvisibleAnnotations".equals(str4))
      {
        k = paramInt + 8;
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(str4))
      {
        n = paramInt + 8;
      }
      else
      {
        .Attribute localAttribute2 = a(paramContext.a, str4, paramInt + 8, readInt(paramInt + 4), arrayOfChar, -1, null);
        if (localAttribute2 != null)
        {
          localAttribute2.a = ((.Attribute)localObject2);
          localObject2 = localAttribute2;
        }
      }
      paramInt += 6 + readInt(paramInt + 4);
    }
    paramInt += 2;
    .FieldVisitor localFieldVisitor = paramClassVisitor.visitField(i, str1, str2, str3, localObject1);
    if (localFieldVisitor == null) {
      return paramInt;
    }
    int i2;
    int i4;
    if (j != 0)
    {
      i2 = readUnsignedShort(j);
      i4 = j + 2;
      while (i2 > 0)
      {
        i4 = a(i4 + 2, arrayOfChar, true, localFieldVisitor.visitAnnotation(readUTF8(i4, arrayOfChar), true));
        i2--;
      }
    }
    if (k != 0)
    {
      i2 = readUnsignedShort(k);
      i4 = k + 2;
      while (i2 > 0)
      {
        i4 = a(i4 + 2, arrayOfChar, true, localFieldVisitor.visitAnnotation(readUTF8(i4, arrayOfChar), false));
        i2--;
      }
    }
    if (m != 0)
    {
      i2 = readUnsignedShort(m);
      i4 = m + 2;
      while (i2 > 0)
      {
        i4 = a(paramContext, i4);
        i4 = a(i4 + 2, arrayOfChar, true, localFieldVisitor.visitTypeAnnotation(paramContext.i, paramContext.j, readUTF8(i4, arrayOfChar), true));
        i2--;
      }
    }
    if (n != 0)
    {
      i2 = readUnsignedShort(n);
      i4 = n + 2;
      while (i2 > 0)
      {
        i4 = a(paramContext, i4);
        i4 = a(i4 + 2, arrayOfChar, true, localFieldVisitor.visitTypeAnnotation(paramContext.i, paramContext.j, readUTF8(i4, arrayOfChar), false));
        i2--;
      }
    }
    while (localObject2 != null)
    {
      .Attribute localAttribute1 = ((.Attribute)localObject2).a;
      ((.Attribute)localObject2).a = null;
      localFieldVisitor.visitAttribute((.Attribute)localObject2);
      localObject2 = localAttribute1;
    }
    localFieldVisitor.visitEnd();
    return paramInt;
  }
  
  private int b(.ClassVisitor paramClassVisitor, .Context paramContext, int paramInt)
  {
    char[] arrayOfChar = paramContext.c;
    paramContext.e = readUnsignedShort(paramInt);
    paramContext.f = readUTF8(paramInt + 2, arrayOfChar);
    paramContext.g = readUTF8(paramInt + 4, arrayOfChar);
    paramInt += 6;
    int i = 0;
    int j = 0;
    String[] arrayOfString = null;
    String str = null;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = paramInt;
    Object localObject1 = null;
    Object localObject2;
    for (int i7 = readUnsignedShort(paramInt); i7 > 0; i7--)
    {
      localObject2 = readUTF8(paramInt + 2, arrayOfChar);
      if ("Code".equals(localObject2))
      {
        if ((paramContext.b & 0x1) == 0) {
          i = paramInt + 8;
        }
      }
      else if ("Exceptions".equals(localObject2))
      {
        arrayOfString = new String[readUnsignedShort(paramInt + 8)];
        j = paramInt + 10;
        for (int i10 = 0; i10 < arrayOfString.length; i10++)
        {
          arrayOfString[i10] = readClass(j, arrayOfChar);
          j += 2;
        }
      }
      else if ("Signature".equals(localObject2))
      {
        str = readUTF8(paramInt + 8, arrayOfChar);
      }
      else if ("Deprecated".equals(localObject2))
      {
        paramContext.e |= 0x20000;
      }
      else if ("RuntimeVisibleAnnotations".equals(localObject2))
      {
        m = paramInt + 8;
      }
      else if ("RuntimeVisibleTypeAnnotations".equals(localObject2))
      {
        i1 = paramInt + 8;
      }
      else if ("AnnotationDefault".equals(localObject2))
      {
        i3 = paramInt + 8;
      }
      else if ("Synthetic".equals(localObject2))
      {
        paramContext.e |= 0x41000;
      }
      else if ("RuntimeInvisibleAnnotations".equals(localObject2))
      {
        n = paramInt + 8;
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(localObject2))
      {
        i2 = paramInt + 8;
      }
      else if ("RuntimeVisibleParameterAnnotations".equals(localObject2))
      {
        i4 = paramInt + 8;
      }
      else if ("RuntimeInvisibleParameterAnnotations".equals(localObject2))
      {
        i5 = paramInt + 8;
      }
      else if ("MethodParameters".equals(localObject2))
      {
        k = paramInt + 8;
      }
      else
      {
        .Attribute localAttribute2 = a(paramContext.a, (String)localObject2, paramInt + 8, readInt(paramInt + 4), arrayOfChar, -1, null);
        if (localAttribute2 != null)
        {
          localAttribute2.a = ((.Attribute)localObject1);
          localObject1 = localAttribute2;
        }
      }
      paramInt += 6 + readInt(paramInt + 4);
    }
    paramInt += 2;
    .MethodVisitor localMethodVisitor = paramClassVisitor.visitMethod(paramContext.e, paramContext.f, paramContext.g, str, arrayOfString);
    if (localMethodVisitor == null) {
      return paramInt;
    }
    int i11;
    if ((localMethodVisitor instanceof .MethodWriter))
    {
      localObject2 = (.MethodWriter)localMethodVisitor;
      if ((((.MethodWriter)localObject2).b.M == this) && (str == ((.MethodWriter)localObject2).g))
      {
        i11 = 0;
        if (arrayOfString == null)
        {
          i11 = ((.MethodWriter)localObject2).j == 0 ? 1 : 0;
        }
        else if (arrayOfString.length == ((.MethodWriter)localObject2).j)
        {
          i11 = 1;
          for (int i12 = arrayOfString.length - 1; i12 >= 0; i12--)
          {
            j -= 2;
            if (localObject2.k[i12] != readUnsignedShort(j))
            {
              i11 = 0;
              break;
            }
          }
        }
        if (i11 != 0)
        {
          ((.MethodWriter)localObject2).h = i6;
          ((.MethodWriter)localObject2).i = (paramInt - i6);
          return paramInt;
        }
      }
    }
    if (k != 0)
    {
      int i8 = this.b[k] & 0xFF;
      i11 = k + 1;
      while (i8 > 0)
      {
        localMethodVisitor.visitParameter(readUTF8(i11, arrayOfChar), readUnsignedShort(i11 + 2));
        i8--;
        i11 += 4;
      }
    }
    if (i3 != 0)
    {
      .AnnotationVisitor localAnnotationVisitor = localMethodVisitor.visitAnnotationDefault();
      a(i3, arrayOfChar, null, localAnnotationVisitor);
      if (localAnnotationVisitor != null) {
        localAnnotationVisitor.visitEnd();
      }
    }
    int i9;
    if (m != 0)
    {
      i9 = readUnsignedShort(m);
      i11 = m + 2;
      while (i9 > 0)
      {
        i11 = a(i11 + 2, arrayOfChar, true, localMethodVisitor.visitAnnotation(readUTF8(i11, arrayOfChar), true));
        i9--;
      }
    }
    if (n != 0)
    {
      i9 = readUnsignedShort(n);
      i11 = n + 2;
      while (i9 > 0)
      {
        i11 = a(i11 + 2, arrayOfChar, true, localMethodVisitor.visitAnnotation(readUTF8(i11, arrayOfChar), false));
        i9--;
      }
    }
    if (i1 != 0)
    {
      i9 = readUnsignedShort(i1);
      i11 = i1 + 2;
      while (i9 > 0)
      {
        i11 = a(paramContext, i11);
        i11 = a(i11 + 2, arrayOfChar, true, localMethodVisitor.visitTypeAnnotation(paramContext.i, paramContext.j, readUTF8(i11, arrayOfChar), true));
        i9--;
      }
    }
    if (i2 != 0)
    {
      i9 = readUnsignedShort(i2);
      i11 = i2 + 2;
      while (i9 > 0)
      {
        i11 = a(paramContext, i11);
        i11 = a(i11 + 2, arrayOfChar, true, localMethodVisitor.visitTypeAnnotation(paramContext.i, paramContext.j, readUTF8(i11, arrayOfChar), false));
        i9--;
      }
    }
    if (i4 != 0) {
      b(localMethodVisitor, paramContext, i4, true);
    }
    if (i5 != 0) {
      b(localMethodVisitor, paramContext, i5, false);
    }
    while (localObject1 != null)
    {
      .Attribute localAttribute1 = ((.Attribute)localObject1).a;
      ((.Attribute)localObject1).a = null;
      localMethodVisitor.visitAttribute((.Attribute)localObject1);
      localObject1 = localAttribute1;
    }
    if (i != 0)
    {
      localMethodVisitor.visitCode();
      a(localMethodVisitor, paramContext, i);
    }
    localMethodVisitor.visitEnd();
    return paramInt;
  }
  
  private void a(.MethodVisitor paramMethodVisitor, .Context paramContext, int paramInt)
  {
    byte[] arrayOfByte = this.b;
    char[] arrayOfChar = paramContext.c;
    int i = readUnsignedShort(paramInt);
    int j = readUnsignedShort(paramInt + 2);
    int k = readInt(paramInt + 4);
    paramInt += 8;
    int m = paramInt;
    int n = paramInt + k;
    .Label[] arrayOfLabel = paramContext.h = new .Label[k + 2];
    readLabel(k + 1, arrayOfLabel);
    while (paramInt < n)
    {
      i1 = paramInt - m;
      int i2 = arrayOfByte[paramInt] & 0xFF;
      int i3;
      switch (.ClassWriter.a[i2])
      {
      case 0: 
      case 4: 
        paramInt++;
        break;
      case 9: 
        readLabel(i1 + readShort(paramInt + 1), arrayOfLabel);
        paramInt += 3;
        break;
      case 10: 
        readLabel(i1 + readInt(paramInt + 1), arrayOfLabel);
        paramInt += 5;
        break;
      case 17: 
        i2 = arrayOfByte[(paramInt + 1)] & 0xFF;
        if (i2 == 132) {
          paramInt += 6;
        } else {
          paramInt += 4;
        }
        break;
      case 14: 
        paramInt = paramInt + 4 - (i1 & 0x3);
        readLabel(i1 + readInt(paramInt), arrayOfLabel);
        for (i3 = readInt(paramInt + 8) - readInt(paramInt + 4) + 1; i3 > 0; i3--)
        {
          readLabel(i1 + readInt(paramInt + 12), arrayOfLabel);
          paramInt += 4;
        }
        paramInt += 12;
        break;
      case 15: 
        paramInt = paramInt + 4 - (i1 & 0x3);
        readLabel(i1 + readInt(paramInt), arrayOfLabel);
        for (i3 = readInt(paramInt + 4); i3 > 0; i3--)
        {
          readLabel(i1 + readInt(paramInt + 12), arrayOfLabel);
          paramInt += 8;
        }
        paramInt += 8;
        break;
      case 1: 
      case 3: 
      case 11: 
        paramInt += 2;
        break;
      case 2: 
      case 5: 
      case 6: 
      case 12: 
      case 13: 
        paramInt += 3;
        break;
      case 7: 
      case 8: 
        paramInt += 5;
        break;
      case 16: 
      default: 
        paramInt += 4;
      }
    }
    for (int i1 = readUnsignedShort(paramInt); i1 > 0; i1--)
    {
      localObject1 = readLabel(readUnsignedShort(paramInt + 2), arrayOfLabel);
      .Label localLabel1 = readLabel(readUnsignedShort(paramInt + 4), arrayOfLabel);
      .Label localLabel2 = readLabel(readUnsignedShort(paramInt + 6), arrayOfLabel);
      String str1 = readUTF8(this.a[readUnsignedShort(paramInt + 8)], arrayOfChar);
      paramMethodVisitor.visitTryCatchBlock((.Label)localObject1, localLabel1, localLabel2, str1);
      paramInt += 8;
    }
    paramInt += 2;
    int[] arrayOfInt1 = null;
    Object localObject1 = null;
    int i4 = 0;
    int i5 = 0;
    int i6 = -1;
    int i7 = -1;
    int i8 = 0;
    int i9 = 0;
    boolean bool1 = true;
    boolean bool2 = (paramContext.b & 0x8) != 0;
    int i10 = 0;
    int i11 = 0;
    int i12 = 0;
    .Context localContext = null;
    Object localObject2 = null;
    int i17;
    int i20;
    for (int i13 = readUnsignedShort(paramInt); i13 > 0; i13--)
    {
      String str2 = readUTF8(paramInt + 2, arrayOfChar);
      int i18;
      if ("LocalVariableTable".equals(str2))
      {
        if ((paramContext.b & 0x2) == 0)
        {
          i8 = paramInt + 8;
          i17 = readUnsignedShort(paramInt + 8);
          i18 = paramInt;
          while (i17 > 0)
          {
            i20 = readUnsignedShort(i18 + 10);
            if (arrayOfLabel[i20] == null) {
              readLabel(i20, arrayOfLabel).a |= 0x1;
            }
            i20 += readUnsignedShort(i18 + 12);
            if (arrayOfLabel[i20] == null) {
              readLabel(i20, arrayOfLabel).a |= 0x1;
            }
            i18 += 10;
            i17--;
          }
        }
      }
      else if ("LocalVariableTypeTable".equals(str2))
      {
        i9 = paramInt + 8;
      }
      else if ("LineNumberTable".equals(str2))
      {
        if ((paramContext.b & 0x2) == 0)
        {
          i17 = readUnsignedShort(paramInt + 8);
          i18 = paramInt;
          while (i17 > 0)
          {
            i20 = readUnsignedShort(i18 + 10);
            if (arrayOfLabel[i20] == null) {
              readLabel(i20, arrayOfLabel).a |= 0x1;
            }
            arrayOfLabel[i20].b = readUnsignedShort(i18 + 12);
            i18 += 4;
            i17--;
          }
        }
      }
      else if ("RuntimeVisibleTypeAnnotations".equals(str2))
      {
        arrayOfInt1 = a(paramMethodVisitor, paramContext, paramInt + 8, true);
        i6 = (arrayOfInt1.length == 0) || (readByte(arrayOfInt1[0]) < 67) ? -1 : readUnsignedShort(arrayOfInt1[0] + 1);
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(str2))
      {
        localObject1 = a(paramMethodVisitor, paramContext, paramInt + 8, false);
        i7 = (localObject1.length == 0) || (readByte(localObject1[0]) < 67) ? -1 : readUnsignedShort(localObject1[0] + 1);
      }
      else if ("StackMapTable".equals(str2))
      {
        if ((paramContext.b & 0x4) == 0)
        {
          i10 = paramInt + 10;
          i11 = readInt(paramInt + 4);
          i12 = readUnsignedShort(paramInt + 8);
        }
      }
      else if ("StackMap".equals(str2))
      {
        if ((paramContext.b & 0x4) == 0)
        {
          bool1 = false;
          i10 = paramInt + 10;
          i11 = readInt(paramInt + 4);
          i12 = readUnsignedShort(paramInt + 8);
        }
      }
      else
      {
        for (i17 = 0; i17 < paramContext.a.length; i17++) {
          if (paramContext.a[i17].type.equals(str2))
          {
            .Attribute localAttribute2 = paramContext.a[i17].read(this, paramInt + 8, readInt(paramInt + 4), arrayOfChar, m - 8, arrayOfLabel);
            if (localAttribute2 != null)
            {
              localAttribute2.a = ((.Attribute)localObject2);
              localObject2 = localAttribute2;
            }
          }
        }
      }
      paramInt += 6 + readInt(paramInt + 4);
    }
    paramInt += 2;
    if (i10 != 0)
    {
      localContext = paramContext;
      localContext.o = -1;
      localContext.p = 0;
      localContext.q = 0;
      localContext.r = 0;
      localContext.t = 0;
      localContext.s = new Object[j];
      localContext.u = new Object[i];
      if (bool2) {
        a(paramContext);
      }
      for (i13 = i10; i13 < i10 + i11 - 2; i13++) {
        if (arrayOfByte[i13] == 8)
        {
          int i15 = readUnsignedShort(i13 + 1);
          if ((i15 >= 0) && (i15 < k) && ((arrayOfByte[(m + i15)] & 0xFF) == 187)) {
            readLabel(i15, arrayOfLabel);
          }
        }
      }
    }
    paramInt = m;
    int i19;
    Object localObject3;
    int i21;
    int i23;
    while (paramInt < n)
    {
      i13 = paramInt - m;
      .Label localLabel3 = arrayOfLabel[i13];
      if (localLabel3 != null)
      {
        paramMethodVisitor.visitLabel(localLabel3);
        if (((paramContext.b & 0x2) == 0) && (localLabel3.b > 0)) {
          paramMethodVisitor.visitLineNumber(localLabel3.b, localLabel3);
        }
      }
      while ((localContext != null) && ((localContext.o == i13) || (localContext.o == -1)))
      {
        if (localContext.o != -1) {
          if ((!bool1) || (bool2)) {
            paramMethodVisitor.visitFrame(-1, localContext.q, localContext.s, localContext.t, localContext.u);
          } else {
            paramMethodVisitor.visitFrame(localContext.p, localContext.r, localContext.s, localContext.t, localContext.u);
          }
        }
        if (i12 > 0)
        {
          i10 = a(i10, bool1, bool2, localContext);
          i12--;
        }
        else
        {
          localContext = null;
        }
      }
      i17 = arrayOfByte[paramInt] & 0xFF;
      Object localObject4;
      int i24;
      Object localObject5;
      switch (.ClassWriter.a[i17])
      {
      case 0: 
        paramMethodVisitor.visitInsn(i17);
        paramInt++;
        break;
      case 4: 
        if (i17 > 54)
        {
          i17 -= 59;
          paramMethodVisitor.visitVarInsn(54 + (i17 >> 2), i17 & 0x3);
        }
        else
        {
          i17 -= 26;
          paramMethodVisitor.visitVarInsn(21 + (i17 >> 2), i17 & 0x3);
        }
        paramInt++;
        break;
      case 9: 
        paramMethodVisitor.visitJumpInsn(i17, arrayOfLabel[(i13 + readShort(paramInt + 1))]);
        paramInt += 3;
        break;
      case 10: 
        paramMethodVisitor.visitJumpInsn(i17 - 33, arrayOfLabel[(i13 + readInt(paramInt + 1))]);
        paramInt += 5;
        break;
      case 17: 
        i17 = arrayOfByte[(paramInt + 1)] & 0xFF;
        if (i17 == 132)
        {
          paramMethodVisitor.visitIincInsn(readUnsignedShort(paramInt + 2), readShort(paramInt + 4));
          paramInt += 6;
        }
        else
        {
          paramMethodVisitor.visitVarInsn(i17, readUnsignedShort(paramInt + 2));
          paramInt += 4;
        }
        break;
      case 14: 
        paramInt = paramInt + 4 - (i13 & 0x3);
        i19 = i13 + readInt(paramInt);
        i20 = readInt(paramInt + 4);
        int i22 = readInt(paramInt + 8);
        localObject4 = new .Label[i22 - i20 + 1];
        paramInt += 12;
        for (i24 = 0; i24 < localObject4.length; i24++)
        {
          localObject4[i24] = arrayOfLabel[(i13 + readInt(paramInt))];
          paramInt += 4;
        }
        paramMethodVisitor.visitTableSwitchInsn(i20, i22, arrayOfLabel[i19], (.Label[])localObject4);
        break;
      case 15: 
        paramInt = paramInt + 4 - (i13 & 0x3);
        i19 = i13 + readInt(paramInt);
        i20 = readInt(paramInt + 4);
        localObject3 = new int[i20];
        localObject4 = new .Label[i20];
        paramInt += 8;
        for (i24 = 0; i24 < i20; i24++)
        {
          localObject3[i24] = readInt(paramInt);
          localObject4[i24] = arrayOfLabel[(i13 + readInt(paramInt + 4))];
          paramInt += 8;
        }
        paramMethodVisitor.visitLookupSwitchInsn(arrayOfLabel[i19], (int[])localObject3, (.Label[])localObject4);
        break;
      case 3: 
        paramMethodVisitor.visitVarInsn(i17, arrayOfByte[(paramInt + 1)] & 0xFF);
        paramInt += 2;
        break;
      case 1: 
        paramMethodVisitor.visitIntInsn(i17, arrayOfByte[(paramInt + 1)]);
        paramInt += 2;
        break;
      case 2: 
        paramMethodVisitor.visitIntInsn(i17, readShort(paramInt + 1));
        paramInt += 3;
        break;
      case 11: 
        paramMethodVisitor.visitLdcInsn(readConst(arrayOfByte[(paramInt + 1)] & 0xFF, arrayOfChar));
        paramInt += 2;
        break;
      case 12: 
        paramMethodVisitor.visitLdcInsn(readConst(readUnsignedShort(paramInt + 1), arrayOfChar));
        paramInt += 3;
        break;
      case 6: 
      case 7: 
        i19 = this.a[readUnsignedShort(paramInt + 1)];
        i20 = arrayOfByte[(i19 - 1)] == 11 ? 1 : 0;
        localObject3 = readClass(i19, arrayOfChar);
        i19 = this.a[readUnsignedShort(i19 + 2)];
        localObject4 = readUTF8(i19, arrayOfChar);
        localObject5 = readUTF8(i19 + 2, arrayOfChar);
        if (i17 < 182) {
          paramMethodVisitor.visitFieldInsn(i17, (String)localObject3, (String)localObject4, (String)localObject5);
        } else {
          paramMethodVisitor.visitMethodInsn(i17, (String)localObject3, (String)localObject4, (String)localObject5, i20);
        }
        if (i17 == 185) {
          paramInt += 5;
        } else {
          paramInt += 3;
        }
        break;
      case 8: 
        i19 = this.a[readUnsignedShort(paramInt + 1)];
        i21 = paramContext.d[readUnsignedShort(i19)];
        localObject3 = (.Handle)readConst(readUnsignedShort(i21), arrayOfChar);
        i23 = readUnsignedShort(i21 + 2);
        localObject5 = new Object[i23];
        i21 += 4;
        for (int i25 = 0; i25 < i23; i25++)
        {
          localObject5[i25] = readConst(readUnsignedShort(i21), arrayOfChar);
          i21 += 2;
        }
        i19 = this.a[readUnsignedShort(i19 + 2)];
        String str3 = readUTF8(i19, arrayOfChar);
        String str4 = readUTF8(i19 + 2, arrayOfChar);
        paramMethodVisitor.visitInvokeDynamicInsn(str3, str4, (.Handle)localObject3, (Object[])localObject5);
        paramInt += 5;
        break;
      case 5: 
        paramMethodVisitor.visitTypeInsn(i17, readClass(paramInt + 1, arrayOfChar));
        paramInt += 3;
        break;
      case 13: 
        paramMethodVisitor.visitIincInsn(arrayOfByte[(paramInt + 1)] & 0xFF, arrayOfByte[(paramInt + 2)]);
        paramInt += 3;
        break;
      case 16: 
      default: 
        paramMethodVisitor.visitMultiANewArrayInsn(readClass(paramInt + 1, arrayOfChar), arrayOfByte[(paramInt + 3)] & 0xFF);
        paramInt += 4;
      }
      while ((arrayOfInt1 != null) && (i4 < arrayOfInt1.length) && (i6 <= i13))
      {
        if (i6 == i13)
        {
          i19 = a(paramContext, arrayOfInt1[i4]);
          a(i19 + 2, arrayOfChar, true, paramMethodVisitor.visitInsnAnnotation(paramContext.i, paramContext.j, readUTF8(i19, arrayOfChar), true));
        }
        i4++;
        i6 = (i4 >= arrayOfInt1.length) || (readByte(arrayOfInt1[i4]) < 67) ? -1 : readUnsignedShort(arrayOfInt1[i4] + 1);
      }
      while ((localObject1 != null) && (i5 < localObject1.length) && (i7 <= i13))
      {
        if (i7 == i13)
        {
          i19 = a(paramContext, localObject1[i5]);
          a(i19 + 2, arrayOfChar, true, paramMethodVisitor.visitInsnAnnotation(paramContext.i, paramContext.j, readUTF8(i19, arrayOfChar), false));
        }
        i5++;
        i7 = (i5 >= localObject1.length) || (readByte(localObject1[i5]) < 67) ? -1 : readUnsignedShort(localObject1[i5] + 1);
      }
    }
    if (arrayOfLabel[k] != null) {
      paramMethodVisitor.visitLabel(arrayOfLabel[k]);
    }
    int i16;
    if (((paramContext.b & 0x2) == 0) && (i8 != 0))
    {
      int[] arrayOfInt2 = null;
      if (i9 != 0)
      {
        paramInt = i9 + 2;
        arrayOfInt2 = new int[readUnsignedShort(i9) * 3];
        i16 = arrayOfInt2.length;
        while (i16 > 0)
        {
          arrayOfInt2[(--i16)] = (paramInt + 6);
          arrayOfInt2[(--i16)] = readUnsignedShort(paramInt + 8);
          arrayOfInt2[(--i16)] = readUnsignedShort(paramInt);
          paramInt += 10;
        }
      }
      paramInt = i8 + 2;
      for (i16 = readUnsignedShort(i8); i16 > 0; i16--)
      {
        i17 = readUnsignedShort(paramInt);
        i19 = readUnsignedShort(paramInt + 2);
        i21 = readUnsignedShort(paramInt + 8);
        localObject3 = null;
        if (arrayOfInt2 != null) {
          for (i23 = 0; i23 < arrayOfInt2.length; i23 += 3) {
            if ((arrayOfInt2[i23] == i17) && (arrayOfInt2[(i23 + 1)] == i21))
            {
              localObject3 = readUTF8(arrayOfInt2[(i23 + 2)], arrayOfChar);
              break;
            }
          }
        }
        paramMethodVisitor.visitLocalVariable(readUTF8(paramInt + 4, arrayOfChar), readUTF8(paramInt + 6, arrayOfChar), (String)localObject3, arrayOfLabel[i17], arrayOfLabel[(i17 + i19)], i21);
        paramInt += 10;
      }
    }
    int i14;
    if (arrayOfInt1 != null) {
      for (i14 = 0; i14 < arrayOfInt1.length; i14++) {
        if (readByte(arrayOfInt1[i14]) >> 1 == 32)
        {
          i16 = a(paramContext, arrayOfInt1[i14]);
          i16 = a(i16 + 2, arrayOfChar, true, paramMethodVisitor.visitLocalVariableAnnotation(paramContext.i, paramContext.j, paramContext.l, paramContext.m, paramContext.n, readUTF8(i16, arrayOfChar), true));
        }
      }
    }
    if (localObject1 != null) {
      for (i14 = 0; i14 < localObject1.length; i14++) {
        if (readByte(localObject1[i14]) >> 1 == 32)
        {
          i16 = a(paramContext, localObject1[i14]);
          i16 = a(i16 + 2, arrayOfChar, true, paramMethodVisitor.visitLocalVariableAnnotation(paramContext.i, paramContext.j, paramContext.l, paramContext.m, paramContext.n, readUTF8(i16, arrayOfChar), false));
        }
      }
    }
    while (localObject2 != null)
    {
      .Attribute localAttribute1 = ((.Attribute)localObject2).a;
      ((.Attribute)localObject2).a = null;
      paramMethodVisitor.visitAttribute((.Attribute)localObject2);
      localObject2 = localAttribute1;
    }
    paramMethodVisitor.visitMaxs(i, j);
  }
  
  private int[] a(.MethodVisitor paramMethodVisitor, .Context paramContext, int paramInt, boolean paramBoolean)
  {
    char[] arrayOfChar = paramContext.c;
    int[] arrayOfInt = new int[readUnsignedShort(paramInt)];
    paramInt += 2;
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      arrayOfInt[i] = paramInt;
      int j = readInt(paramInt);
      switch (j >>> 24)
      {
      case 0: 
      case 1: 
      case 22: 
        paramInt += 2;
        break;
      case 19: 
      case 20: 
      case 21: 
        paramInt++;
        break;
      case 64: 
      case 65: 
        for (k = readUnsignedShort(paramInt + 1); k > 0; k--)
        {
          int m = readUnsignedShort(paramInt + 3);
          int n = readUnsignedShort(paramInt + 5);
          readLabel(m, paramContext.h);
          readLabel(m + n, paramContext.h);
          paramInt += 6;
        }
        paramInt += 3;
        break;
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
        paramInt += 4;
        break;
      default: 
        paramInt += 3;
      }
      int k = readByte(paramInt);
      if (j >>> 24 == 66)
      {
        .TypePath localTypePath = k == 0 ? null : new .TypePath(this.b, paramInt);
        paramInt += 1 + 2 * k;
        paramInt = a(paramInt + 2, arrayOfChar, true, paramMethodVisitor.visitTryCatchAnnotation(j, localTypePath, readUTF8(paramInt, arrayOfChar), paramBoolean));
      }
      else
      {
        paramInt = a(paramInt + 3 + 2 * k, arrayOfChar, true, null);
      }
    }
    return arrayOfInt;
  }
  
  private int a(.Context paramContext, int paramInt)
  {
    int i = readInt(paramInt);
    switch (i >>> 24)
    {
    case 0: 
    case 1: 
    case 22: 
      i &= 0xFFFF0000;
      paramInt += 2;
      break;
    case 19: 
    case 20: 
    case 21: 
      i &= 0xFF000000;
      paramInt++;
      break;
    case 64: 
    case 65: 
      i &= 0xFF000000;
      j = readUnsignedShort(paramInt + 1);
      paramContext.l = new .Label[j];
      paramContext.m = new .Label[j];
      paramContext.n = new int[j];
      paramInt += 3;
      for (int k = 0; k < j; k++)
      {
        int m = readUnsignedShort(paramInt);
        int n = readUnsignedShort(paramInt + 2);
        paramContext.l[k] = readLabel(m, paramContext.h);
        paramContext.m[k] = readLabel(m + n, paramContext.h);
        paramContext.n[k] = readUnsignedShort(paramInt + 4);
        paramInt += 6;
      }
      break;
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
      i &= 0xFF0000FF;
      paramInt += 4;
      break;
    default: 
      i &= (i >>> 24 < 67 ? 65280 : -16777216);
      paramInt += 3;
    }
    int j = readByte(paramInt);
    paramContext.i = i;
    paramContext.j = (j == 0 ? null : new .TypePath(this.b, paramInt));
    return paramInt + 1 + 2 * j;
  }
  
  private void b(.MethodVisitor paramMethodVisitor, .Context paramContext, int paramInt, boolean paramBoolean)
  {
    int i = this.b[(paramInt++)] & 0xFF;
    int j = .Type.getArgumentTypes(paramContext.g).length - i;
    .AnnotationVisitor localAnnotationVisitor;
    for (int k = 0; k < j; k++)
    {
      localAnnotationVisitor = paramMethodVisitor.visitParameterAnnotation(k, "Ljava/lang/Synthetic;", false);
      if (localAnnotationVisitor != null) {
        localAnnotationVisitor.visitEnd();
      }
    }
    char[] arrayOfChar = paramContext.c;
    while (k < i + j)
    {
      int m = readUnsignedShort(paramInt);
      paramInt += 2;
      while (m > 0)
      {
        localAnnotationVisitor = paramMethodVisitor.visitParameterAnnotation(k, readUTF8(paramInt, arrayOfChar), paramBoolean);
        paramInt = a(paramInt + 2, arrayOfChar, true, localAnnotationVisitor);
        m--;
      }
      k++;
    }
  }
  
  private int a(int paramInt, char[] paramArrayOfChar, boolean paramBoolean, .AnnotationVisitor paramAnnotationVisitor)
  {
    int i = readUnsignedShort(paramInt);
    paramInt += 2;
    if (paramBoolean) {
      while (i > 0)
      {
        paramInt = a(paramInt + 2, paramArrayOfChar, readUTF8(paramInt, paramArrayOfChar), paramAnnotationVisitor);
        i--;
      }
    }
    while (i > 0)
    {
      paramInt = a(paramInt, paramArrayOfChar, null, paramAnnotationVisitor);
      i--;
    }
    if (paramAnnotationVisitor != null) {
      paramAnnotationVisitor.visitEnd();
    }
    return paramInt;
  }
  
  private int a(int paramInt, char[] paramArrayOfChar, String paramString, .AnnotationVisitor paramAnnotationVisitor)
  {
    if (paramAnnotationVisitor == null)
    {
      switch (this.b[paramInt] & 0xFF)
      {
      case 101: 
        return paramInt + 5;
      case 64: 
        return a(paramInt + 3, paramArrayOfChar, true, null);
      case 91: 
        return a(paramInt + 1, paramArrayOfChar, false, null);
      }
      return paramInt + 3;
    }
    switch (this.b[(paramInt++)] & 0xFF)
    {
    case 68: 
    case 70: 
    case 73: 
    case 74: 
      paramAnnotationVisitor.visit(paramString, readConst(readUnsignedShort(paramInt), paramArrayOfChar));
      paramInt += 2;
      break;
    case 66: 
      paramAnnotationVisitor.visit(paramString, new Byte((byte)readInt(this.a[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 90: 
      paramAnnotationVisitor.visit(paramString, readInt(this.a[readUnsignedShort(paramInt)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
      paramInt += 2;
      break;
    case 83: 
      paramAnnotationVisitor.visit(paramString, new Short((short)readInt(this.a[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 67: 
      paramAnnotationVisitor.visit(paramString, new Character((char)readInt(this.a[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 115: 
      paramAnnotationVisitor.visit(paramString, readUTF8(paramInt, paramArrayOfChar));
      paramInt += 2;
      break;
    case 101: 
      paramAnnotationVisitor.visitEnum(paramString, readUTF8(paramInt, paramArrayOfChar), readUTF8(paramInt + 2, paramArrayOfChar));
      paramInt += 4;
      break;
    case 99: 
      paramAnnotationVisitor.visit(paramString, .Type.getType(readUTF8(paramInt, paramArrayOfChar)));
      paramInt += 2;
      break;
    case 64: 
      paramInt = a(paramInt + 2, paramArrayOfChar, true, paramAnnotationVisitor.visitAnnotation(paramString, readUTF8(paramInt, paramArrayOfChar)));
      break;
    case 91: 
      int i = readUnsignedShort(paramInt);
      paramInt += 2;
      if (i == 0) {
        return a(paramInt - 2, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
      }
      int j;
      switch (this.b[(paramInt++)] & 0xFF)
      {
      case 66: 
        byte[] arrayOfByte = new byte[i];
        for (j = 0; j < i; j++)
        {
          arrayOfByte[j] = ((byte)readInt(this.a[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfByte);
        paramInt--;
        break;
      case 90: 
        boolean[] arrayOfBoolean = new boolean[i];
        for (j = 0; j < i; j++)
        {
          arrayOfBoolean[j] = (readInt(this.a[readUnsignedShort(paramInt)]) != 0 ? 1 : false);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfBoolean);
        paramInt--;
        break;
      case 83: 
        short[] arrayOfShort = new short[i];
        for (j = 0; j < i; j++)
        {
          arrayOfShort[j] = ((short)readInt(this.a[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfShort);
        paramInt--;
        break;
      case 67: 
        char[] arrayOfChar = new char[i];
        for (j = 0; j < i; j++)
        {
          arrayOfChar[j] = ((char)readInt(this.a[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfChar);
        paramInt--;
        break;
      case 73: 
        int[] arrayOfInt = new int[i];
        for (j = 0; j < i; j++)
        {
          arrayOfInt[j] = readInt(this.a[readUnsignedShort(paramInt)]);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfInt);
        paramInt--;
        break;
      case 74: 
        long[] arrayOfLong = new long[i];
        for (j = 0; j < i; j++)
        {
          arrayOfLong[j] = readLong(this.a[readUnsignedShort(paramInt)]);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfLong);
        paramInt--;
        break;
      case 70: 
        float[] arrayOfFloat = new float[i];
        for (j = 0; j < i; j++)
        {
          arrayOfFloat[j] = Float.intBitsToFloat(readInt(this.a[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfFloat);
        paramInt--;
        break;
      case 68: 
        double[] arrayOfDouble = new double[i];
        for (j = 0; j < i; j++)
        {
          arrayOfDouble[j] = Double.longBitsToDouble(readLong(this.a[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfDouble);
        paramInt--;
        break;
      case 69: 
      case 71: 
      case 72: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      default: 
        paramInt = a(paramInt - 3, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
      }
      break;
    }
    return paramInt;
  }
  
  private void a(.Context paramContext)
  {
    String str = paramContext.g;
    Object[] arrayOfObject = paramContext.s;
    int i = 0;
    if ((paramContext.e & 0x8) == 0) {
      if ("<init>".equals(paramContext.f)) {
        arrayOfObject[(i++)] = .Opcodes.UNINITIALIZED_THIS;
      } else {
        arrayOfObject[(i++)] = readClass(this.header + 2, paramContext.c);
      }
    }
    int j = 1;
    for (;;)
    {
      int k = j;
      switch (str.charAt(j++))
      {
      case 'B': 
      case 'C': 
      case 'I': 
      case 'S': 
      case 'Z': 
        arrayOfObject[(i++)] = .Opcodes.INTEGER;
        break;
      case 'F': 
        arrayOfObject[(i++)] = .Opcodes.FLOAT;
        break;
      case 'J': 
        arrayOfObject[(i++)] = .Opcodes.LONG;
        break;
      case 'D': 
        arrayOfObject[(i++)] = .Opcodes.DOUBLE;
        break;
      case '[': 
        while (str.charAt(j) == '[') {
          j++;
        }
        if (str.charAt(j) == 'L')
        {
          j++;
          while (str.charAt(j) != ';') {
            j++;
          }
        }
        arrayOfObject[(i++)] = str.substring(k, ++j);
        break;
      case 'L': 
        while (str.charAt(j) != ';') {
          j++;
        }
        arrayOfObject[(i++)] = str.substring(k + 1, j++);
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'T': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        break label371;
      }
    }
    label371:
    paramContext.q = i;
  }
  
  private int a(int paramInt, boolean paramBoolean1, boolean paramBoolean2, .Context paramContext)
  {
    char[] arrayOfChar = paramContext.c;
    .Label[] arrayOfLabel = paramContext.h;
    int i;
    if (paramBoolean1)
    {
      i = this.b[(paramInt++)] & 0xFF;
    }
    else
    {
      i = 255;
      paramContext.o = -1;
    }
    paramContext.r = 0;
    int j;
    if (i < 64)
    {
      j = i;
      paramContext.p = 3;
      paramContext.t = 0;
    }
    else if (i < 128)
    {
      j = i - 64;
      paramInt = a(paramContext.u, 0, paramInt, arrayOfChar, arrayOfLabel);
      paramContext.p = 4;
      paramContext.t = 1;
    }
    else
    {
      j = readUnsignedShort(paramInt);
      paramInt += 2;
      if (i == 247)
      {
        paramInt = a(paramContext.u, 0, paramInt, arrayOfChar, arrayOfLabel);
        paramContext.p = 4;
        paramContext.t = 1;
      }
      else if ((i >= 248) && (i < 251))
      {
        paramContext.p = 2;
        paramContext.r = (251 - i);
        paramContext.q -= paramContext.r;
        paramContext.t = 0;
      }
      else if (i == 251)
      {
        paramContext.p = 3;
        paramContext.t = 0;
      }
      else
      {
        int k;
        int m;
        if (i < 255)
        {
          k = paramBoolean2 ? paramContext.q : 0;
          for (m = i - 251; m > 0; m--) {
            paramInt = a(paramContext.s, k++, paramInt, arrayOfChar, arrayOfLabel);
          }
          paramContext.p = 1;
          paramContext.r = (i - 251);
          paramContext.q += paramContext.r;
          paramContext.t = 0;
        }
        else
        {
          paramContext.p = 0;
          k = readUnsignedShort(paramInt);
          paramInt += 2;
          paramContext.r = k;
          paramContext.q = k;
          m = 0;
          while (k > 0)
          {
            paramInt = a(paramContext.s, m++, paramInt, arrayOfChar, arrayOfLabel);
            k--;
          }
          k = readUnsignedShort(paramInt);
          paramInt += 2;
          paramContext.t = k;
          m = 0;
          while (k > 0)
          {
            paramInt = a(paramContext.u, m++, paramInt, arrayOfChar, arrayOfLabel);
            k--;
          }
        }
      }
    }
    paramContext.o += j + 1;
    readLabel(paramContext.o, arrayOfLabel);
    return paramInt;
  }
  
  private int a(Object[] paramArrayOfObject, int paramInt1, int paramInt2, char[] paramArrayOfChar, .Label[] paramArrayOfLabel)
  {
    int i = this.b[(paramInt2++)] & 0xFF;
    switch (i)
    {
    case 0: 
      paramArrayOfObject[paramInt1] = .Opcodes.TOP;
      break;
    case 1: 
      paramArrayOfObject[paramInt1] = .Opcodes.INTEGER;
      break;
    case 2: 
      paramArrayOfObject[paramInt1] = .Opcodes.FLOAT;
      break;
    case 3: 
      paramArrayOfObject[paramInt1] = .Opcodes.DOUBLE;
      break;
    case 4: 
      paramArrayOfObject[paramInt1] = .Opcodes.LONG;
      break;
    case 5: 
      paramArrayOfObject[paramInt1] = .Opcodes.NULL;
      break;
    case 6: 
      paramArrayOfObject[paramInt1] = .Opcodes.UNINITIALIZED_THIS;
      break;
    case 7: 
      paramArrayOfObject[paramInt1] = readClass(paramInt2, paramArrayOfChar);
      paramInt2 += 2;
      break;
    default: 
      paramArrayOfObject[paramInt1] = readLabel(readUnsignedShort(paramInt2), paramArrayOfLabel);
      paramInt2 += 2;
    }
    return paramInt2;
  }
  
  protected .Label readLabel(int paramInt, .Label[] paramArrayOfLabel)
  {
    if (paramArrayOfLabel[paramInt] == null) {
      paramArrayOfLabel[paramInt] = new .Label();
    }
    return paramArrayOfLabel[paramInt];
  }
  
  private int a()
  {
    int i = this.header + 8 + readUnsignedShort(this.header + 6) * 2;
    int k;
    for (int j = readUnsignedShort(i); j > 0; j--)
    {
      for (k = readUnsignedShort(i + 8); k > 0; k--) {
        i += 6 + readInt(i + 12);
      }
      i += 8;
    }
    i += 2;
    for (j = readUnsignedShort(i); j > 0; j--)
    {
      for (k = readUnsignedShort(i + 8); k > 0; k--) {
        i += 6 + readInt(i + 12);
      }
      i += 8;
    }
    return i + 2;
  }
  
  private .Attribute a(.Attribute[] paramArrayOfAttribute, String paramString, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, .Label[] paramArrayOfLabel)
  {
    for (int i = 0; i < paramArrayOfAttribute.length; i++) {
      if (paramArrayOfAttribute[i].type.equals(paramString)) {
        return paramArrayOfAttribute[i].read(this, paramInt1, paramInt2, paramArrayOfChar, paramInt3, paramArrayOfLabel);
      }
    }
    return new .Attribute(paramString).read(this, paramInt1, paramInt2, null, -1, null);
  }
  
  public int getItemCount()
  {
    return this.a.length;
  }
  
  public int getItem(int paramInt)
  {
    return this.a[paramInt];
  }
  
  public int getMaxStringLength()
  {
    return this.d;
  }
  
  public int readByte(int paramInt)
  {
    return this.b[paramInt] & 0xFF;
  }
  
  public int readUnsignedShort(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  public short readShort(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (short)((arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  public int readInt(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 24 | (arrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (arrayOfByte[(paramInt + 2)] & 0xFF) << 8 | arrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  public long readLong(int paramInt)
  {
    long l1 = readInt(paramInt);
    long l2 = readInt(paramInt + 4) & 0xFFFFFFFF;
    return l1 << 32 | l2;
  }
  
  public String readUTF8(int paramInt, char[] paramArrayOfChar)
  {
    int i = readUnsignedShort(paramInt);
    if ((paramInt == 0) || (i == 0)) {
      return null;
    }
    String str = this.c[i];
    if (str != null) {
      return str;
    }
    paramInt = this.a[i];
    return this.c[i] = a(paramInt + 2, readUnsignedShort(paramInt), paramArrayOfChar);
  }
  
  private String a(int paramInt1, int paramInt2, char[] paramArrayOfChar)
  {
    int i = paramInt1 + paramInt2;
    byte[] arrayOfByte = this.b;
    int j = 0;
    int k = 0;
    int m = 0;
    while (paramInt1 < i)
    {
      int n = arrayOfByte[(paramInt1++)];
      switch (k)
      {
      case 0: 
        n &= 0xFF;
        if (n < 128)
        {
          paramArrayOfChar[(j++)] = ((char)n);
        }
        else if ((n < 224) && (n > 191))
        {
          m = (char)(n & 0x1F);
          k = 1;
        }
        else
        {
          m = (char)(n & 0xF);
          k = 2;
        }
        break;
      case 1: 
        paramArrayOfChar[(j++)] = ((char)(m << 6 | n & 0x3F));
        k = 0;
        break;
      case 2: 
        m = (char)(m << 6 | n & 0x3F);
        k = 1;
      }
    }
    return new String(paramArrayOfChar, 0, j);
  }
  
  public String readClass(int paramInt, char[] paramArrayOfChar)
  {
    return readUTF8(this.a[readUnsignedShort(paramInt)], paramArrayOfChar);
  }
  
  public Object readConst(int paramInt, char[] paramArrayOfChar)
  {
    int i = this.a[paramInt];
    switch (this.b[(i - 1)])
    {
    case 3: 
      return new Integer(readInt(i));
    case 4: 
      return new Float(Float.intBitsToFloat(readInt(i)));
    case 5: 
      return new Long(readLong(i));
    case 6: 
      return new Double(Double.longBitsToDouble(readLong(i)));
    case 7: 
      return .Type.getObjectType(readUTF8(i, paramArrayOfChar));
    case 8: 
      return readUTF8(i, paramArrayOfChar);
    case 16: 
      return .Type.getMethodType(readUTF8(i, paramArrayOfChar));
    }
    int j = readByte(i);
    int[] arrayOfInt = this.a;
    int k = arrayOfInt[readUnsignedShort(i + 1)];
    String str1 = readClass(k, paramArrayOfChar);
    k = arrayOfInt[readUnsignedShort(k + 2)];
    String str2 = readUTF8(k, paramArrayOfChar);
    String str3 = readUTF8(k + 2, paramArrayOfChar);
    return new .Handle(j, str1, str2, str3);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$ClassReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */