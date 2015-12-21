package com.google.inject.internal.asm;

public class $ClassWriter
  extends .ClassVisitor
{
  public static final int COMPUTE_MAXS = 1;
  public static final int COMPUTE_FRAMES = 2;
  static final byte[] a;
  .ClassReader M;
  int b;
  int c = 1;
  final .ByteVector d = new .ByteVector();
  .Item[] e = new .Item['Ā'];
  int f = (int)(0.75D * this.e.length);
  final .Item g = new .Item();
  final .Item h = new .Item();
  final .Item i = new .Item();
  final .Item j = new .Item();
  .Item[] H;
  private short G;
  private int k;
  private int l;
  String I;
  private int m;
  private int n;
  private int o;
  private int[] p;
  private int q;
  private .ByteVector r;
  private int s;
  private int t;
  private .AnnotationWriter u;
  private .AnnotationWriter v;
  private .AnnotationWriter N;
  private .AnnotationWriter O;
  private .Attribute w;
  private int x;
  private .ByteVector y;
  int z;
  .ByteVector A;
  .FieldWriter B;
  .FieldWriter C;
  .MethodWriter D;
  .MethodWriter E;
  private boolean K;
  private boolean J;
  boolean L;
  
  public $ClassWriter(int paramInt)
  {
    super(327680);
    this.K = ((paramInt & 0x1) != 0);
    this.J = ((paramInt & 0x2) != 0);
  }
  
  public $ClassWriter(.ClassReader paramClassReader, int paramInt)
  {
    this(paramInt);
    paramClassReader.a(this);
    this.M = paramClassReader;
  }
  
  public final void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this.b = paramInt1;
    this.k = paramInt2;
    this.l = newClass(paramString1);
    this.I = paramString1;
    if (paramString2 != null) {
      this.m = newUTF8(paramString2);
    }
    this.n = (paramString3 == null ? 0 : newClass(paramString3));
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      this.o = paramArrayOfString.length;
      this.p = new int[this.o];
      for (int i1 = 0; i1 < this.o; i1++) {
        this.p[i1] = newClass(paramArrayOfString[i1]);
      }
    }
  }
  
  public final void visitSource(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      this.q = newUTF8(paramString1);
    }
    if (paramString2 != null) {
      this.r = new .ByteVector().encodeUTF8(paramString2, 0, Integer.MAX_VALUE);
    }
  }
  
  public final void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    this.s = newClass(paramString1);
    if ((paramString2 != null) && (paramString3 != null)) {
      this.t = newNameType(paramString2, paramString3);
    }
  }
  
  public final .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    localByteVector.putShort(newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.u;
      this.u = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.v;
      this.v = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public final .AnnotationVisitor visitTypeAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    .AnnotationWriter.a(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this, true, localByteVector, localByteVector, localByteVector.b - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.N;
      this.N = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.O;
      this.O = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public final void visitAttribute(.Attribute paramAttribute)
  {
    paramAttribute.a = this.w;
    this.w = paramAttribute;
  }
  
  public final void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (this.y == null) {
      this.y = new .ByteVector();
    }
    .Item localItem = a(paramString1);
    if (localItem.c == 0)
    {
      this.x += 1;
      this.y.putShort(localItem.a);
      this.y.putShort(paramString2 == null ? 0 : newClass(paramString2));
      this.y.putShort(paramString3 == null ? 0 : newUTF8(paramString3));
      this.y.putShort(paramInt);
      localItem.c = this.x;
    }
  }
  
  public final .FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    return new .FieldWriter(this, paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public final .MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    return new .MethodWriter(this, paramInt, paramString1, paramString2, paramString3, paramArrayOfString, this.K, this.J);
  }
  
  public final void visitEnd() {}
  
  public byte[] toByteArray()
  {
    if (this.c > 65535) {
      throw new RuntimeException("Class file too large!");
    }
    int i1 = 24 + 2 * this.o;
    int i2 = 0;
    for (.FieldWriter localFieldWriter = this.B; localFieldWriter != null; localFieldWriter = (.FieldWriter)localFieldWriter.fv)
    {
      i2++;
      i1 += localFieldWriter.a();
    }
    int i3 = 0;
    for (.MethodWriter localMethodWriter = this.D; localMethodWriter != null; localMethodWriter = (.MethodWriter)localMethodWriter.mv)
    {
      i3++;
      i1 += localMethodWriter.a();
    }
    int i4 = 0;
    if (this.A != null)
    {
      i4++;
      i1 += 8 + this.A.b;
      newUTF8("BootstrapMethods");
    }
    if (this.m != 0)
    {
      i4++;
      i1 += 8;
      newUTF8("Signature");
    }
    if (this.q != 0)
    {
      i4++;
      i1 += 8;
      newUTF8("SourceFile");
    }
    if (this.r != null)
    {
      i4++;
      i1 += this.r.b + 6;
      newUTF8("SourceDebugExtension");
    }
    if (this.s != 0)
    {
      i4++;
      i1 += 10;
      newUTF8("EnclosingMethod");
    }
    if ((this.k & 0x20000) != 0)
    {
      i4++;
      i1 += 6;
      newUTF8("Deprecated");
    }
    if (((this.k & 0x1000) != 0) && (((this.b & 0xFFFF) < 49) || ((this.k & 0x40000) != 0)))
    {
      i4++;
      i1 += 6;
      newUTF8("Synthetic");
    }
    if (this.y != null)
    {
      i4++;
      i1 += 8 + this.y.b;
      newUTF8("InnerClasses");
    }
    if (this.u != null)
    {
      i4++;
      i1 += 8 + this.u.a();
      newUTF8("RuntimeVisibleAnnotations");
    }
    if (this.v != null)
    {
      i4++;
      i1 += 8 + this.v.a();
      newUTF8("RuntimeInvisibleAnnotations");
    }
    if (this.N != null)
    {
      i4++;
      i1 += 8 + this.N.a();
      newUTF8("RuntimeVisibleTypeAnnotations");
    }
    if (this.O != null)
    {
      i4++;
      i1 += 8 + this.O.a();
      newUTF8("RuntimeInvisibleTypeAnnotations");
    }
    if (this.w != null)
    {
      i4 += this.w.a();
      i1 += this.w.a(this, null, 0, -1, -1);
    }
    i1 += this.d.b;
    .ByteVector localByteVector = new .ByteVector(i1);
    localByteVector.putInt(-889275714).putInt(this.b);
    localByteVector.putShort(this.c).putByteArray(this.d.a, 0, this.d.b);
    int i5 = 0x60000 | (this.k & 0x40000) / 64;
    localByteVector.putShort(this.k & (i5 ^ 0xFFFFFFFF)).putShort(this.l).putShort(this.n);
    localByteVector.putShort(this.o);
    for (int i6 = 0; i6 < this.o; i6++) {
      localByteVector.putShort(this.p[i6]);
    }
    localByteVector.putShort(i2);
    for (localFieldWriter = this.B; localFieldWriter != null; localFieldWriter = (.FieldWriter)localFieldWriter.fv) {
      localFieldWriter.a(localByteVector);
    }
    localByteVector.putShort(i3);
    for (localMethodWriter = this.D; localMethodWriter != null; localMethodWriter = (.MethodWriter)localMethodWriter.mv) {
      localMethodWriter.a(localByteVector);
    }
    localByteVector.putShort(i4);
    if (this.A != null)
    {
      localByteVector.putShort(newUTF8("BootstrapMethods"));
      localByteVector.putInt(this.A.b + 2).putShort(this.z);
      localByteVector.putByteArray(this.A.a, 0, this.A.b);
    }
    if (this.m != 0) {
      localByteVector.putShort(newUTF8("Signature")).putInt(2).putShort(this.m);
    }
    if (this.q != 0) {
      localByteVector.putShort(newUTF8("SourceFile")).putInt(2).putShort(this.q);
    }
    if (this.r != null)
    {
      i6 = this.r.b;
      localByteVector.putShort(newUTF8("SourceDebugExtension")).putInt(i6);
      localByteVector.putByteArray(this.r.a, 0, i6);
    }
    if (this.s != 0)
    {
      localByteVector.putShort(newUTF8("EnclosingMethod")).putInt(4);
      localByteVector.putShort(this.s).putShort(this.t);
    }
    if ((this.k & 0x20000) != 0) {
      localByteVector.putShort(newUTF8("Deprecated")).putInt(0);
    }
    if (((this.k & 0x1000) != 0) && (((this.b & 0xFFFF) < 49) || ((this.k & 0x40000) != 0))) {
      localByteVector.putShort(newUTF8("Synthetic")).putInt(0);
    }
    if (this.y != null)
    {
      localByteVector.putShort(newUTF8("InnerClasses"));
      localByteVector.putInt(this.y.b + 2).putShort(this.x);
      localByteVector.putByteArray(this.y.a, 0, this.y.b);
    }
    if (this.u != null)
    {
      localByteVector.putShort(newUTF8("RuntimeVisibleAnnotations"));
      this.u.a(localByteVector);
    }
    if (this.v != null)
    {
      localByteVector.putShort(newUTF8("RuntimeInvisibleAnnotations"));
      this.v.a(localByteVector);
    }
    if (this.N != null)
    {
      localByteVector.putShort(newUTF8("RuntimeVisibleTypeAnnotations"));
      this.N.a(localByteVector);
    }
    if (this.O != null)
    {
      localByteVector.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.O.a(localByteVector);
    }
    if (this.w != null) {
      this.w.a(this, null, 0, -1, -1, localByteVector);
    }
    if (this.L)
    {
      this.u = null;
      this.v = null;
      this.w = null;
      this.x = 0;
      this.y = null;
      this.z = 0;
      this.A = null;
      this.B = null;
      this.C = null;
      this.D = null;
      this.E = null;
      this.K = false;
      this.J = true;
      this.L = false;
      new .ClassReader(localByteVector.a).accept(this, 4);
      return toByteArray();
    }
    return localByteVector.a;
  }
  
  .Item a(Object paramObject)
  {
    int i1;
    if ((paramObject instanceof Integer))
    {
      i1 = ((Integer)paramObject).intValue();
      return a(i1);
    }
    if ((paramObject instanceof Byte))
    {
      i1 = ((Byte)paramObject).intValue();
      return a(i1);
    }
    if ((paramObject instanceof Character))
    {
      i1 = ((Character)paramObject).charValue();
      return a(i1);
    }
    if ((paramObject instanceof Short))
    {
      i1 = ((Short)paramObject).intValue();
      return a(i1);
    }
    if ((paramObject instanceof Boolean))
    {
      i1 = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      return a(i1);
    }
    if ((paramObject instanceof Float))
    {
      float f1 = ((Float)paramObject).floatValue();
      return a(f1);
    }
    if ((paramObject instanceof Long))
    {
      long l1 = ((Long)paramObject).longValue();
      return a(l1);
    }
    if ((paramObject instanceof Double))
    {
      double d1 = ((Double)paramObject).doubleValue();
      return a(d1);
    }
    if ((paramObject instanceof String)) {
      return b((String)paramObject);
    }
    Object localObject;
    if ((paramObject instanceof .Type))
    {
      localObject = (.Type)paramObject;
      int i2 = ((.Type)localObject).getSort();
      if (i2 == 10) {
        return a(((.Type)localObject).getInternalName());
      }
      if (i2 == 11) {
        return c(((.Type)localObject).getDescriptor());
      }
      return a(((.Type)localObject).getDescriptor());
    }
    if ((paramObject instanceof .Handle))
    {
      localObject = (.Handle)paramObject;
      return a(((.Handle)localObject).a, ((.Handle)localObject).b, ((.Handle)localObject).c, ((.Handle)localObject).d);
    }
    throw new IllegalArgumentException("value " + paramObject);
  }
  
  public int newConst(Object paramObject)
  {
    return a(paramObject).a;
  }
  
  public int newUTF8(String paramString)
  {
    this.g.a(1, paramString, null, null);
    .Item localItem = a(this.g);
    if (localItem == null)
    {
      this.d.putByte(1).putUTF8(paramString);
      localItem = new .Item(this.c++, this.g);
      b(localItem);
    }
    return localItem.a;
  }
  
  .Item a(String paramString)
  {
    this.h.a(7, paramString, null, null);
    .Item localItem = a(this.h);
    if (localItem == null)
    {
      this.d.b(7, newUTF8(paramString));
      localItem = new .Item(this.c++, this.h);
      b(localItem);
    }
    return localItem;
  }
  
  public int newClass(String paramString)
  {
    return a(paramString).a;
  }
  
  .Item c(String paramString)
  {
    this.h.a(16, paramString, null, null);
    .Item localItem = a(this.h);
    if (localItem == null)
    {
      this.d.b(16, newUTF8(paramString));
      localItem = new .Item(this.c++, this.h);
      b(localItem);
    }
    return localItem;
  }
  
  public int newMethodType(String paramString)
  {
    return c(paramString).a;
  }
  
  .Item a(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.j.a(20 + paramInt, paramString1, paramString2, paramString3);
    .Item localItem = a(this.j);
    if (localItem == null)
    {
      if (paramInt <= 4) {
        b(15, paramInt, newField(paramString1, paramString2, paramString3));
      } else {
        b(15, paramInt, newMethod(paramString1, paramString2, paramString3, paramInt == 9));
      }
      localItem = new .Item(this.c++, this.j);
      b(localItem);
    }
    return localItem;
  }
  
  public int newHandle(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    return a(paramInt, paramString1, paramString2, paramString3).a;
  }
  
  .Item a(String paramString1, String paramString2, .Handle paramHandle, Object... paramVarArgs)
  {
    .ByteVector localByteVector = this.A;
    if (localByteVector == null) {
      localByteVector = this.A = new .ByteVector();
    }
    int i1 = localByteVector.b;
    int i2 = paramHandle.hashCode();
    localByteVector.putShort(newHandle(paramHandle.a, paramHandle.b, paramHandle.c, paramHandle.d));
    int i3 = paramVarArgs.length;
    localByteVector.putShort(i3);
    for (int i4 = 0; i4 < i3; i4++)
    {
      Object localObject = paramVarArgs[i4];
      i2 ^= localObject.hashCode();
      localByteVector.putShort(newConst(localObject));
    }
    byte[] arrayOfByte = localByteVector.a;
    int i5 = 2 + i3 << 1;
    i2 &= 0x7FFFFFFF;
    .Item localItem = this.e[(i2 % this.e.length)];
    int i6;
    label246:
    while (localItem != null) {
      if ((localItem.b != 33) || (localItem.j != i2))
      {
        localItem = localItem.k;
      }
      else
      {
        i6 = localItem.c;
        for (int i7 = 0;; i7++)
        {
          if (i7 >= i5) {
            break label246;
          }
          if (arrayOfByte[(i1 + i7)] != arrayOfByte[(i6 + i7)])
          {
            localItem = localItem.k;
            break;
          }
        }
      }
    }
    if (localItem != null)
    {
      i6 = localItem.a;
      localByteVector.b = i1;
    }
    else
    {
      i6 = this.z++;
      localItem = new .Item(i6);
      localItem.a(i1, i2);
      b(localItem);
    }
    this.i.a(paramString1, paramString2, i6);
    localItem = a(this.i);
    if (localItem == null)
    {
      a(18, i6, newNameType(paramString1, paramString2));
      localItem = new .Item(this.c++, this.i);
      b(localItem);
    }
    return localItem;
  }
  
  public int newInvokeDynamic(String paramString1, String paramString2, .Handle paramHandle, Object... paramVarArgs)
  {
    return a(paramString1, paramString2, paramHandle, paramVarArgs).a;
  }
  
  .Item a(String paramString1, String paramString2, String paramString3)
  {
    this.i.a(9, paramString1, paramString2, paramString3);
    .Item localItem = a(this.i);
    if (localItem == null)
    {
      a(9, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new .Item(this.c++, this.i);
      b(localItem);
    }
    return localItem;
  }
  
  public int newField(String paramString1, String paramString2, String paramString3)
  {
    return a(paramString1, paramString2, paramString3).a;
  }
  
  .Item a(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    int i1 = paramBoolean ? 11 : 10;
    this.i.a(i1, paramString1, paramString2, paramString3);
    .Item localItem = a(this.i);
    if (localItem == null)
    {
      a(i1, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new .Item(this.c++, this.i);
      b(localItem);
    }
    return localItem;
  }
  
  public int newMethod(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    return a(paramString1, paramString2, paramString3, paramBoolean).a;
  }
  
  .Item a(int paramInt)
  {
    this.g.a(paramInt);
    .Item localItem = a(this.g);
    if (localItem == null)
    {
      this.d.putByte(3).putInt(paramInt);
      localItem = new .Item(this.c++, this.g);
      b(localItem);
    }
    return localItem;
  }
  
  .Item a(float paramFloat)
  {
    this.g.a(paramFloat);
    .Item localItem = a(this.g);
    if (localItem == null)
    {
      this.d.putByte(4).putInt(this.g.c);
      localItem = new .Item(this.c++, this.g);
      b(localItem);
    }
    return localItem;
  }
  
  .Item a(long paramLong)
  {
    this.g.a(paramLong);
    .Item localItem = a(this.g);
    if (localItem == null)
    {
      this.d.putByte(5).putLong(paramLong);
      localItem = new .Item(this.c, this.g);
      this.c += 2;
      b(localItem);
    }
    return localItem;
  }
  
  .Item a(double paramDouble)
  {
    this.g.a(paramDouble);
    .Item localItem = a(this.g);
    if (localItem == null)
    {
      this.d.putByte(6).putLong(this.g.d);
      localItem = new .Item(this.c, this.g);
      this.c += 2;
      b(localItem);
    }
    return localItem;
  }
  
  private .Item b(String paramString)
  {
    this.h.a(8, paramString, null, null);
    .Item localItem = a(this.h);
    if (localItem == null)
    {
      this.d.b(8, newUTF8(paramString));
      localItem = new .Item(this.c++, this.h);
      b(localItem);
    }
    return localItem;
  }
  
  public int newNameType(String paramString1, String paramString2)
  {
    return a(paramString1, paramString2).a;
  }
  
  .Item a(String paramString1, String paramString2)
  {
    this.h.a(12, paramString1, paramString2, null);
    .Item localItem = a(this.h);
    if (localItem == null)
    {
      a(12, newUTF8(paramString1), newUTF8(paramString2));
      localItem = new .Item(this.c++, this.h);
      b(localItem);
    }
    return localItem;
  }
  
  int c(String paramString)
  {
    this.g.a(30, paramString, null, null);
    .Item localItem = a(this.g);
    if (localItem == null) {
      localItem = c(this.g);
    }
    return localItem.a;
  }
  
  int a(String paramString, int paramInt)
  {
    this.g.b = 31;
    this.g.c = paramInt;
    this.g.g = paramString;
    this.g.j = (0x7FFFFFFF & 31 + paramString.hashCode() + paramInt);
    .Item localItem = a(this.g);
    if (localItem == null) {
      localItem = c(this.g);
    }
    return localItem.a;
  }
  
  private .Item c(.Item paramItem)
  {
    this.G = ((short)(this.G + 1));
    .Item localItem = new .Item(this.G, this.g);
    b(localItem);
    if (this.H == null) {
      this.H = new .Item[16];
    }
    if (this.G == this.H.length)
    {
      .Item[] arrayOfItem = new .Item[2 * this.H.length];
      System.arraycopy(this.H, 0, arrayOfItem, 0, this.H.length);
      this.H = arrayOfItem;
    }
    this.H[this.G] = localItem;
    return localItem;
  }
  
  int a(int paramInt1, int paramInt2)
  {
    this.h.b = 32;
    this.h.d = (paramInt1 | paramInt2 << 32);
    this.h.j = (0x7FFFFFFF & 32 + paramInt1 + paramInt2);
    .Item localItem = a(this.h);
    if (localItem == null)
    {
      String str1 = this.H[paramInt1].g;
      String str2 = this.H[paramInt2].g;
      this.h.c = c(getCommonSuperClass(str1, str2));
      localItem = new .Item(0, this.h);
      b(localItem);
    }
    return localItem.c;
  }
  
  protected String getCommonSuperClass(String paramString1, String paramString2)
  {
    ClassLoader localClassLoader = getClass().getClassLoader();
    Class localClass1;
    Class localClass2;
    try
    {
      localClass1 = Class.forName(paramString1.replace('/', '.'), false, localClassLoader);
      localClass2 = Class.forName(paramString2.replace('/', '.'), false, localClassLoader);
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
    if (localClass1.isAssignableFrom(localClass2)) {
      return paramString1;
    }
    if (localClass2.isAssignableFrom(localClass1)) {
      return paramString2;
    }
    if ((localClass1.isInterface()) || (localClass2.isInterface())) {
      return "java/lang/Object";
    }
    do
    {
      localClass1 = localClass1.getSuperclass();
    } while (!localClass1.isAssignableFrom(localClass2));
    return localClass1.getName().replace('.', '/');
  }
  
  private .Item a(.Item paramItem)
  {
    for (.Item localItem = this.e[(paramItem.j % this.e.length)]; (localItem != null) && ((localItem.b != paramItem.b) || (!paramItem.a(localItem))); localItem = localItem.k) {}
    return localItem;
  }
  
  private void b(.Item paramItem)
  {
    if (this.c + this.G > this.f)
    {
      i1 = this.e.length;
      int i2 = i1 * 2 + 1;
      .Item[] arrayOfItem = new .Item[i2];
      for (int i3 = i1 - 1; i3 >= 0; i3--)
      {
        .Item localItem;
        for (Object localObject = this.e[i3]; localObject != null; localObject = localItem)
        {
          int i4 = ((.Item)localObject).j % arrayOfItem.length;
          localItem = ((.Item)localObject).k;
          ((.Item)localObject).k = arrayOfItem[i4];
          arrayOfItem[i4] = localObject;
        }
      }
      this.e = arrayOfItem;
      this.f = ((int)(i2 * 0.75D));
    }
    int i1 = paramItem.j % this.e.length;
    paramItem.k = this.e[i1];
    this.e[i1] = paramItem;
  }
  
  private void a(int paramInt1, int paramInt2, int paramInt3)
  {
    this.d.b(paramInt1, paramInt2).putShort(paramInt3);
  }
  
  private void b(int paramInt1, int paramInt2, int paramInt3)
  {
    this.d.a(paramInt1, paramInt2).putShort(paramInt3);
  }
  
  static
  {
    _clinit_();
    byte[] arrayOfByte = new byte['Ü'];
    String str = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
    for (int i1 = 0; i1 < arrayOfByte.length; i1++) {
      arrayOfByte[i1] = ((byte)(str.charAt(i1) - 'A'));
    }
    a = arrayOfByte;
  }
  
  static void _clinit_() {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$ClassWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */