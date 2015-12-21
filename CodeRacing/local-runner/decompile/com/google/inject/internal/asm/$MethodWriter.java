package com.google.inject.internal.asm;

class $MethodWriter
  extends .MethodVisitor
{
  final .ClassWriter b;
  private int c;
  private final int d;
  private final int e;
  private final String f;
  String g;
  int h;
  int i;
  int j;
  int[] k;
  private .ByteVector l;
  private .AnnotationWriter m;
  private .AnnotationWriter n;
  private .AnnotationWriter U;
  private .AnnotationWriter V;
  private .AnnotationWriter[] o;
  private .AnnotationWriter[] p;
  private int S;
  private .Attribute q;
  private .ByteVector r = new .ByteVector();
  private int s;
  private int t;
  private int T;
  private int u;
  private .ByteVector v;
  private int w;
  private int[] x;
  private int[] z;
  private int A;
  private .Handler B;
  private .Handler C;
  private int Z;
  private .ByteVector $;
  private int D;
  private .ByteVector E;
  private int F;
  private .ByteVector G;
  private int H;
  private .ByteVector I;
  private int Y;
  private .AnnotationWriter W;
  private .AnnotationWriter X;
  private .Attribute J;
  private boolean K;
  private int L;
  private final int M;
  private .Label N;
  private .Label O;
  private .Label P;
  private int Q;
  private int R;
  
  $MethodWriter(.ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(327680);
    if (paramClassWriter.D == null) {
      paramClassWriter.D = this;
    } else {
      paramClassWriter.E.mv = this;
    }
    paramClassWriter.E = this;
    this.b = paramClassWriter;
    this.c = paramInt;
    if ("<init>".equals(paramString1)) {
      this.c |= 0x80000;
    }
    this.d = paramClassWriter.newUTF8(paramString1);
    this.e = paramClassWriter.newUTF8(paramString2);
    this.f = paramString2;
    this.g = paramString3;
    int i1;
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      this.j = paramArrayOfString.length;
      this.k = new int[this.j];
      for (i1 = 0; i1 < this.j; i1++) {
        this.k[i1] = paramClassWriter.newClass(paramArrayOfString[i1]);
      }
    }
    this.M = (paramBoolean1 ? 1 : paramBoolean2 ? 0 : 2);
    if ((paramBoolean1) || (paramBoolean2))
    {
      i1 = .Type.getArgumentsAndReturnSizes(this.f) >> 2;
      if ((paramInt & 0x8) != 0) {
        i1--;
      }
      this.t = i1;
      this.T = i1;
      this.N = new .Label();
      this.N.a |= 0x8;
      visitLabel(this.N);
    }
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (this.$ == null) {
      this.$ = new .ByteVector();
    }
    this.Z += 1;
    this.$.putShort(paramString == null ? 0 : this.b.newUTF8(paramString)).putShort(paramInt);
  }
  
  public .AnnotationVisitor visitAnnotationDefault()
  {
    this.l = new .ByteVector();
    return new .AnnotationWriter(this.b, false, this.l, null, 0);
  }
  
  public .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.m;
      this.m = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.n;
      this.n = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public .AnnotationVisitor visitTypeAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    .AnnotationWriter.a(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, localByteVector.b - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.U;
      this.U = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.V;
      this.V = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public .AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    if ("Ljava/lang/Synthetic;".equals(paramString))
    {
      this.S = Math.max(this.S, paramInt + 1);
      return new .AnnotationWriter(this.b, false, localByteVector, null, 0);
    }
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      if (this.o == null) {
        this.o = new .AnnotationWriter[.Type.getArgumentTypes(this.f).length];
      }
      localAnnotationWriter.g = this.o[paramInt];
      this.o[paramInt] = localAnnotationWriter;
    }
    else
    {
      if (this.p == null) {
        this.p = new .AnnotationWriter[.Type.getArgumentTypes(this.f).length];
      }
      localAnnotationWriter.g = this.p[paramInt];
      this.p[paramInt] = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitAttribute(.Attribute paramAttribute)
  {
    if (paramAttribute.isCodeAttribute())
    {
      paramAttribute.a = this.J;
      this.J = paramAttribute;
    }
    else
    {
      paramAttribute.a = this.q;
      this.q = paramAttribute;
    }
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (this.M == 0) {
      return;
    }
    int i1;
    int i2;
    if (paramInt1 == -1)
    {
      if (this.x == null) {
        f();
      }
      this.T = paramInt2;
      i1 = a(this.r.b, paramInt2, paramInt3);
      for (i2 = 0; i2 < paramInt2; i2++) {
        if ((paramArrayOfObject1[i2] instanceof String)) {
          this.z[(i1++)] = (0x1700000 | this.b.c((String)paramArrayOfObject1[i2]));
        } else if ((paramArrayOfObject1[i2] instanceof Integer)) {
          this.z[(i1++)] = ((Integer)paramArrayOfObject1[i2]).intValue();
        } else {
          this.z[(i1++)] = (0x1800000 | this.b.a("", ((.Label)paramArrayOfObject1[i2]).c));
        }
      }
      for (i2 = 0; i2 < paramInt3; i2++) {
        if ((paramArrayOfObject2[i2] instanceof String)) {
          this.z[(i1++)] = (0x1700000 | this.b.c((String)paramArrayOfObject2[i2]));
        } else if ((paramArrayOfObject2[i2] instanceof Integer)) {
          this.z[(i1++)] = ((Integer)paramArrayOfObject2[i2]).intValue();
        } else {
          this.z[(i1++)] = (0x1800000 | this.b.a("", ((.Label)paramArrayOfObject2[i2]).c));
        }
      }
      b();
    }
    else
    {
      if (this.v == null)
      {
        this.v = new .ByteVector();
        i1 = this.r.b;
      }
      else
      {
        i1 = this.r.b - this.w - 1;
        if (i1 < 0)
        {
          if (paramInt1 == 3) {
            return;
          }
          throw new IllegalStateException();
        }
      }
      switch (paramInt1)
      {
      case 0: 
        this.T = paramInt2;
        this.v.putByte(255).putShort(i1).putShort(paramInt2);
        for (i2 = 0; i2 < paramInt2; i2++) {
          a(paramArrayOfObject1[i2]);
        }
        this.v.putShort(paramInt3);
        for (i2 = 0; i2 < paramInt3; i2++) {
          a(paramArrayOfObject2[i2]);
        }
        break;
      case 1: 
        this.T += paramInt2;
        this.v.putByte(251 + paramInt2).putShort(i1);
        for (i2 = 0; i2 < paramInt2; i2++) {
          a(paramArrayOfObject1[i2]);
        }
        break;
      case 2: 
        this.T -= paramInt2;
        this.v.putByte(251 - paramInt2).putShort(i1);
        break;
      case 3: 
        if (i1 < 64) {
          this.v.putByte(i1);
        } else {
          this.v.putByte(251).putShort(i1);
        }
        break;
      case 4: 
        if (i1 < 64) {
          this.v.putByte(64 + i1);
        } else {
          this.v.putByte(247).putShort(i1);
        }
        a(paramArrayOfObject2[0]);
      }
      this.w = this.r.b;
      this.u += 1;
    }
    this.s = Math.max(this.s, paramInt3);
    this.t = Math.max(this.t, this.T);
  }
  
  public void visitInsn(int paramInt)
  {
    this.Y = this.r.b;
    this.r.putByte(paramInt);
    if (this.P != null)
    {
      if (this.M == 0)
      {
        this.P.h.a(paramInt, 0, null, null);
      }
      else
      {
        int i1 = this.Q + .Frame.a[paramInt];
        if (i1 > this.R) {
          this.R = i1;
        }
        this.Q = i1;
      }
      if (((paramInt >= 172) && (paramInt <= 177)) || (paramInt == 191)) {
        e();
      }
    }
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    this.Y = this.r.b;
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(paramInt1, paramInt2, null, null);
      }
      else if (paramInt1 != 188)
      {
        int i1 = this.Q + 1;
        if (i1 > this.R) {
          this.R = i1;
        }
        this.Q = i1;
      }
    }
    if (paramInt1 == 17) {
      this.r.b(paramInt1, paramInt2);
    } else {
      this.r.a(paramInt1, paramInt2);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    this.Y = this.r.b;
    int i1;
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(paramInt1, paramInt2, null, null);
      }
      else if (paramInt1 == 169)
      {
        this.P.a |= 0x100;
        this.P.f = this.Q;
        e();
      }
      else
      {
        i1 = this.Q + .Frame.a[paramInt1];
        if (i1 > this.R) {
          this.R = i1;
        }
        this.Q = i1;
      }
    }
    if (this.M != 2)
    {
      if ((paramInt1 == 22) || (paramInt1 == 24) || (paramInt1 == 55) || (paramInt1 == 57)) {
        i1 = paramInt2 + 2;
      } else {
        i1 = paramInt2 + 1;
      }
      if (i1 > this.t) {
        this.t = i1;
      }
    }
    if ((paramInt2 < 4) && (paramInt1 != 169))
    {
      if (paramInt1 < 54) {
        i1 = 26 + (paramInt1 - 21 << 2) + paramInt2;
      } else {
        i1 = 59 + (paramInt1 - 54 << 2) + paramInt2;
      }
      this.r.putByte(i1);
    }
    else if (paramInt2 >= 256)
    {
      this.r.putByte(196).b(paramInt1, paramInt2);
    }
    else
    {
      this.r.a(paramInt1, paramInt2);
    }
    if ((paramInt1 >= 54) && (this.M == 0) && (this.A > 0)) {
      visitLabel(new .Label());
    }
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    this.Y = this.r.b;
    .Item localItem = this.b.a(paramString);
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(paramInt, this.r.b, this.b, localItem);
      }
      else if (paramInt == 187)
      {
        int i1 = this.Q + 1;
        if (i1 > this.R) {
          this.R = i1;
        }
        this.Q = i1;
      }
    }
    this.r.b(paramInt, localItem.a);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.Y = this.r.b;
    .Item localItem = this.b.a(paramString1, paramString2, paramString3);
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(paramInt, 0, this.b, localItem);
      }
      else
      {
        int i1 = paramString3.charAt(0);
        int i2;
        switch (paramInt)
        {
        case 178: 
          i2 = this.Q + ((i1 == 68) || (i1 == 74) ? 2 : 1);
          break;
        case 179: 
          i2 = this.Q + ((i1 == 68) || (i1 == 74) ? -2 : -1);
          break;
        case 180: 
          i2 = this.Q + ((i1 == 68) || (i1 == 74) ? 1 : 0);
          break;
        default: 
          i2 = this.Q + ((i1 == 68) || (i1 == 74) ? -3 : -2);
        }
        if (i2 > this.R) {
          this.R = i2;
        }
        this.Q = i2;
      }
    }
    this.r.b(paramInt, localItem.a);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    this.Y = this.r.b;
    .Item localItem = this.b.a(paramString1, paramString2, paramString3, paramBoolean);
    int i1 = localItem.c;
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(paramInt, 0, this.b, localItem);
      }
      else
      {
        if (i1 == 0)
        {
          i1 = .Type.getArgumentsAndReturnSizes(paramString3);
          localItem.c = i1;
        }
        int i2;
        if (paramInt == 184) {
          i2 = this.Q - (i1 >> 2) + (i1 & 0x3) + 1;
        } else {
          i2 = this.Q - (i1 >> 2) + (i1 & 0x3);
        }
        if (i2 > this.R) {
          this.R = i2;
        }
        this.Q = i2;
      }
    }
    if (paramInt == 185)
    {
      if (i1 == 0)
      {
        i1 = .Type.getArgumentsAndReturnSizes(paramString3);
        localItem.c = i1;
      }
      this.r.b(185, localItem.a).a(i1 >> 2, 0);
    }
    else
    {
      this.r.b(paramInt, localItem.a);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, .Handle paramHandle, Object... paramVarArgs)
  {
    this.Y = this.r.b;
    .Item localItem = this.b.a(paramString1, paramString2, paramHandle, paramVarArgs);
    int i1 = localItem.c;
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(186, 0, this.b, localItem);
      }
      else
      {
        if (i1 == 0)
        {
          i1 = .Type.getArgumentsAndReturnSizes(paramString2);
          localItem.c = i1;
        }
        int i2 = this.Q - (i1 >> 2) + (i1 & 0x3) + 1;
        if (i2 > this.R) {
          this.R = i2;
        }
        this.Q = i2;
      }
    }
    this.r.b(186, localItem.a);
    this.r.putShort(0);
  }
  
  public void visitJumpInsn(int paramInt, .Label paramLabel)
  {
    this.Y = this.r.b;
    .Label localLabel = null;
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(paramInt, 0, null, null);
        paramLabel.a().a |= 0x10;
        a(0, paramLabel);
        if (paramInt != 167) {
          localLabel = new .Label();
        }
      }
      else if (paramInt == 168)
      {
        if ((paramLabel.a & 0x200) == 0)
        {
          paramLabel.a |= 0x200;
          this.L += 1;
        }
        this.P.a |= 0x80;
        a(this.Q + 1, paramLabel);
        localLabel = new .Label();
      }
      else
      {
        this.Q += .Frame.a[paramInt];
        a(this.Q, paramLabel);
      }
    }
    if (((paramLabel.a & 0x2) != 0) && (paramLabel.c - this.r.b < 32768))
    {
      if (paramInt == 167)
      {
        this.r.putByte(200);
      }
      else if (paramInt == 168)
      {
        this.r.putByte(201);
      }
      else
      {
        if (localLabel != null) {
          localLabel.a |= 0x10;
        }
        this.r.putByte(paramInt <= 166 ? (paramInt + 1 ^ 0x1) - 1 : paramInt ^ 0x1);
        this.r.putShort(8);
        this.r.putByte(200);
      }
      paramLabel.a(this, this.r, this.r.b - 1, true);
    }
    else
    {
      this.r.putByte(paramInt);
      paramLabel.a(this, this.r, this.r.b - 1, false);
    }
    if (this.P != null)
    {
      if (localLabel != null) {
        visitLabel(localLabel);
      }
      if (paramInt == 167) {
        e();
      }
    }
  }
  
  public void visitLabel(.Label paramLabel)
  {
    this.K |= paramLabel.a(this, this.r.b, this.r.a);
    if ((paramLabel.a & 0x1) != 0) {
      return;
    }
    if (this.M == 0)
    {
      if (this.P != null)
      {
        if (paramLabel.c == this.P.c)
        {
          this.P.a |= paramLabel.a & 0x10;
          paramLabel.h = this.P.h;
          return;
        }
        a(0, paramLabel);
      }
      this.P = paramLabel;
      if (paramLabel.h == null)
      {
        paramLabel.h = new .Frame();
        paramLabel.h.b = paramLabel;
      }
      if (this.O != null)
      {
        if (paramLabel.c == this.O.c)
        {
          this.O.a |= paramLabel.a & 0x10;
          paramLabel.h = this.O.h;
          this.P = this.O;
          return;
        }
        this.O.i = paramLabel;
      }
      this.O = paramLabel;
    }
    else if (this.M == 1)
    {
      if (this.P != null)
      {
        this.P.g = this.R;
        a(this.Q, paramLabel);
      }
      this.P = paramLabel;
      this.Q = 0;
      this.R = 0;
      if (this.O != null) {
        this.O.i = paramLabel;
      }
      this.O = paramLabel;
    }
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    this.Y = this.r.b;
    .Item localItem = this.b.a(paramObject);
    if (this.P != null) {
      if (this.M == 0)
      {
        this.P.h.a(18, 0, this.b, localItem);
      }
      else
      {
        if ((localItem.b == 5) || (localItem.b == 6)) {
          i1 = this.Q + 2;
        } else {
          i1 = this.Q + 1;
        }
        if (i1 > this.R) {
          this.R = i1;
        }
        this.Q = i1;
      }
    }
    int i1 = localItem.a;
    if ((localItem.b == 5) || (localItem.b == 6)) {
      this.r.b(20, i1);
    } else if (i1 >= 256) {
      this.r.b(19, i1);
    } else {
      this.r.a(18, i1);
    }
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    this.Y = this.r.b;
    if ((this.P != null) && (this.M == 0)) {
      this.P.h.a(132, paramInt1, null, null);
    }
    if (this.M != 2)
    {
      int i1 = paramInt1 + 1;
      if (i1 > this.t) {
        this.t = i1;
      }
    }
    if ((paramInt1 > 255) || (paramInt2 > 127) || (paramInt2 < -128)) {
      this.r.putByte(196).b(132, paramInt1).putShort(paramInt2);
    } else {
      this.r.putByte(132).a(paramInt1, paramInt2);
    }
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, .Label paramLabel, .Label... paramVarArgs)
  {
    this.Y = this.r.b;
    int i1 = this.r.b;
    this.r.putByte(170);
    this.r.putByteArray(null, 0, (4 - this.r.b % 4) % 4);
    paramLabel.a(this, this.r, i1, true);
    this.r.putInt(paramInt1).putInt(paramInt2);
    for (int i2 = 0; i2 < paramVarArgs.length; i2++) {
      paramVarArgs[i2].a(this, this.r, i1, true);
    }
    a(paramLabel, paramVarArgs);
  }
  
  public void visitLookupSwitchInsn(.Label paramLabel, int[] paramArrayOfInt, .Label[] paramArrayOfLabel)
  {
    this.Y = this.r.b;
    int i1 = this.r.b;
    this.r.putByte(171);
    this.r.putByteArray(null, 0, (4 - this.r.b % 4) % 4);
    paramLabel.a(this, this.r, i1, true);
    this.r.putInt(paramArrayOfLabel.length);
    for (int i2 = 0; i2 < paramArrayOfLabel.length; i2++)
    {
      this.r.putInt(paramArrayOfInt[i2]);
      paramArrayOfLabel[i2].a(this, this.r, i1, true);
    }
    a(paramLabel, paramArrayOfLabel);
  }
  
  private void a(.Label paramLabel, .Label[] paramArrayOfLabel)
  {
    if (this.P != null)
    {
      int i1;
      if (this.M == 0)
      {
        this.P.h.a(171, 0, null, null);
        a(0, paramLabel);
        paramLabel.a().a |= 0x10;
        for (i1 = 0; i1 < paramArrayOfLabel.length; i1++)
        {
          a(0, paramArrayOfLabel[i1]);
          paramArrayOfLabel[i1].a().a |= 0x10;
        }
      }
      else
      {
        this.Q -= 1;
        a(this.Q, paramLabel);
        for (i1 = 0; i1 < paramArrayOfLabel.length; i1++) {
          a(this.Q, paramArrayOfLabel[i1]);
        }
      }
      e();
    }
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    this.Y = this.r.b;
    .Item localItem = this.b.a(paramString);
    if (this.P != null) {
      if (this.M == 0) {
        this.P.h.a(197, paramInt, this.b, localItem);
      } else {
        this.Q += 1 - paramInt;
      }
    }
    this.r.b(197, localItem.a).putByte(paramInt);
  }
  
  public .AnnotationVisitor visitInsnAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    paramInt = paramInt & 0xFF0000FF | this.Y << 8;
    .AnnotationWriter.a(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, localByteVector.b - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.W;
      this.W = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.X;
      this.X = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitTryCatchBlock(.Label paramLabel1, .Label paramLabel2, .Label paramLabel3, String paramString)
  {
    this.A += 1;
    .Handler localHandler = new .Handler();
    localHandler.a = paramLabel1;
    localHandler.b = paramLabel2;
    localHandler.c = paramLabel3;
    localHandler.d = paramString;
    localHandler.e = (paramString != null ? this.b.newClass(paramString) : 0);
    if (this.C == null) {
      this.B = localHandler;
    } else {
      this.C.f = localHandler;
    }
    this.C = localHandler;
  }
  
  public .AnnotationVisitor visitTryCatchAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    .AnnotationWriter.a(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, localByteVector.b - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.W;
      this.W = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.X;
      this.X = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, .Label paramLabel1, .Label paramLabel2, int paramInt)
  {
    if (paramString3 != null)
    {
      if (this.G == null) {
        this.G = new .ByteVector();
      }
      this.F += 1;
      this.G.putShort(paramLabel1.c).putShort(paramLabel2.c - paramLabel1.c).putShort(this.b.newUTF8(paramString1)).putShort(this.b.newUTF8(paramString3)).putShort(paramInt);
    }
    if (this.E == null) {
      this.E = new .ByteVector();
    }
    this.D += 1;
    this.E.putShort(paramLabel1.c).putShort(paramLabel2.c - paramLabel1.c).putShort(this.b.newUTF8(paramString1)).putShort(this.b.newUTF8(paramString2)).putShort(paramInt);
    if (this.M != 2)
    {
      int i1 = paramString2.charAt(0);
      int i2 = paramInt + ((i1 == 74) || (i1 == 68) ? 2 : 1);
      if (i2 > this.t) {
        this.t = i2;
      }
    }
  }
  
  public .AnnotationVisitor visitLocalVariableAnnotation(int paramInt, .TypePath paramTypePath, .Label[] paramArrayOfLabel1, .Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    localByteVector.putByte(paramInt >>> 24).putShort(paramArrayOfLabel1.length);
    for (int i1 = 0; i1 < paramArrayOfLabel1.length; i1++) {
      localByteVector.putShort(paramArrayOfLabel1[i1].c).putShort(paramArrayOfLabel2[i1].c - paramArrayOfLabel1[i1].c).putShort(paramArrayOfInt[i1]);
    }
    if (paramTypePath == null)
    {
      localByteVector.putByte(0);
    }
    else
    {
      i1 = paramTypePath.a[paramTypePath.b] * 2 + 1;
      localByteVector.putByteArray(paramTypePath.a, paramTypePath.b, i1);
    }
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, localByteVector.b - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.W;
      this.W = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.X;
      this.X = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitLineNumber(int paramInt, .Label paramLabel)
  {
    if (this.I == null) {
      this.I = new .ByteVector();
    }
    this.H += 1;
    this.I.putShort(paramLabel.c);
    this.I.putShort(paramInt);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    if (this.K) {
      d();
    }
    .Handler localHandler;
    Object localObject1;
    Object localObject2;
    Object localObject4;
    Object localObject6;
    if (this.M == 0)
    {
      for (localHandler = this.B; localHandler != null; localHandler = localHandler.f)
      {
        localObject1 = localHandler.a.a();
        localObject2 = localHandler.c.a();
        .Label localLabel1 = localHandler.b.a();
        localObject4 = localHandler.d == null ? "java/lang/Throwable" : localHandler.d;
        int i4 = 0x1700000 | this.b.c((String)localObject4);
        localObject2.a |= 0x10;
        while (localObject1 != localLabel1)
        {
          .Edge localEdge1 = new .Edge();
          localEdge1.a = i4;
          localEdge1.b = ((.Label)localObject2);
          localEdge1.c = ((.Label)localObject1).j;
          ((.Label)localObject1).j = localEdge1;
          localObject1 = ((.Label)localObject1).i;
        }
      }
      localObject1 = this.N.h;
      localObject2 = .Type.getArgumentTypes(this.f);
      ((.Frame)localObject1).a(this.b, this.c, (.Type[])localObject2, this.t);
      b((.Frame)localObject1);
      int i2 = 0;
      localObject4 = this.N;
      int i9;
      while (localObject4 != null)
      {
        localObject5 = localObject4;
        localObject4 = ((.Label)localObject4).k;
        ((.Label)localObject5).k = null;
        localObject1 = ((.Label)localObject5).h;
        if ((((.Label)localObject5).a & 0x10) != 0) {
          localObject5.a |= 0x20;
        }
        localObject5.a |= 0x40;
        int i6 = ((.Frame)localObject1).d.length + ((.Label)localObject5).g;
        if (i6 > i2) {
          i2 = i6;
        }
        for (.Edge localEdge2 = ((.Label)localObject5).j; localEdge2 != null; localEdge2 = localEdge2.c)
        {
          .Label localLabel2 = localEdge2.b.a();
          i9 = ((.Frame)localObject1).a(this.b, localLabel2.h, localEdge2.a);
          if ((i9 != 0) && (localLabel2.k == null))
          {
            localLabel2.k = ((.Label)localObject4);
            localObject4 = localLabel2;
          }
        }
      }
      for (Object localObject5 = this.N; localObject5 != null; localObject5 = ((.Label)localObject5).i)
      {
        localObject1 = ((.Label)localObject5).h;
        if ((((.Label)localObject5).a & 0x20) != 0) {
          b((.Frame)localObject1);
        }
        if ((((.Label)localObject5).a & 0x40) == 0)
        {
          localObject6 = ((.Label)localObject5).i;
          int i7 = ((.Label)localObject5).c;
          int i8 = (localObject6 == null ? this.r.b : ((.Label)localObject6).c) - 1;
          if (i8 >= i7)
          {
            i2 = Math.max(i2, 1);
            for (i9 = i7; i9 < i8; i9++) {
              this.r.a[i9] = 0;
            }
            this.r.a[i8] = -65;
            int i10 = a(i7, 0, 1);
            this.z[i10] = (0x1700000 | this.b.c("java/lang/Throwable"));
            b();
            this.B = .Handler.a(this.B, (.Label)localObject5, (.Label)localObject6);
          }
        }
      }
      localHandler = this.B;
      this.A = 0;
      while (localHandler != null)
      {
        this.A += 1;
        localHandler = localHandler.f;
      }
      this.s = i2;
    }
    else if (this.M == 1)
    {
      Object localObject3;
      for (localHandler = this.B; localHandler != null; localHandler = localHandler.f)
      {
        localObject1 = localHandler.a;
        localObject2 = localHandler.c;
        localObject3 = localHandler.b;
        while (localObject1 != localObject3)
        {
          localObject4 = new .Edge();
          ((.Edge)localObject4).a = Integer.MAX_VALUE;
          ((.Edge)localObject4).b = ((.Label)localObject2);
          if ((((.Label)localObject1).a & 0x80) == 0)
          {
            ((.Edge)localObject4).c = ((.Label)localObject1).j;
            ((.Label)localObject1).j = ((.Edge)localObject4);
          }
          else
          {
            ((.Edge)localObject4).c = ((.Label)localObject1).j.c.c;
            ((.Label)localObject1).j.c.c = ((.Edge)localObject4);
          }
          localObject1 = ((.Label)localObject1).i;
        }
      }
      if (this.L > 0)
      {
        i1 = 0;
        this.N.b(null, 1L, this.L);
        for (localObject2 = this.N; localObject2 != null; localObject2 = ((.Label)localObject2).i) {
          if ((((.Label)localObject2).a & 0x80) != 0)
          {
            localObject3 = ((.Label)localObject2).j.c.b;
            if ((((.Label)localObject3).a & 0x400) == 0)
            {
              i1++;
              ((.Label)localObject3).b(null, i1 / 32L << 32 | 1L << i1 % 32, this.L);
            }
          }
        }
        for (localObject2 = this.N; localObject2 != null; localObject2 = ((.Label)localObject2).i) {
          if ((((.Label)localObject2).a & 0x80) != 0)
          {
            for (localObject3 = this.N; localObject3 != null; localObject3 = ((.Label)localObject3).i) {
              localObject3.a &= 0xF7FF;
            }
            localObject4 = ((.Label)localObject2).j.c.b;
            ((.Label)localObject4).b((.Label)localObject2, 0L, this.L);
          }
        }
      }
      int i1 = 0;
      localObject2 = this.N;
      while (localObject2 != null)
      {
        localObject3 = localObject2;
        localObject2 = ((.Label)localObject2).k;
        int i3 = ((.Label)localObject3).f;
        int i5 = i3 + ((.Label)localObject3).g;
        if (i5 > i1) {
          i1 = i5;
        }
        localObject6 = ((.Label)localObject3).j;
        if ((((.Label)localObject3).a & 0x80) != 0) {}
        for (localObject6 = ((.Edge)localObject6).c; localObject6 != null; localObject6 = ((.Edge)localObject6).c)
        {
          localObject3 = ((.Edge)localObject6).b;
          if ((((.Label)localObject3).a & 0x8) == 0)
          {
            ((.Label)localObject3).f = (((.Edge)localObject6).a == Integer.MAX_VALUE ? 1 : i3 + ((.Edge)localObject6).a);
            localObject3.a |= 0x8;
            ((.Label)localObject3).k = ((.Label)localObject2);
            localObject2 = localObject3;
          }
        }
      }
      this.s = Math.max(paramInt1, i1);
    }
    else
    {
      this.s = paramInt1;
      this.t = paramInt2;
    }
  }
  
  public void visitEnd() {}
  
  private void a(int paramInt, .Label paramLabel)
  {
    .Edge localEdge = new .Edge();
    localEdge.a = paramInt;
    localEdge.b = paramLabel;
    localEdge.c = this.P.j;
    this.P.j = localEdge;
  }
  
  private void e()
  {
    if (this.M == 0)
    {
      .Label localLabel = new .Label();
      localLabel.h = new .Frame();
      localLabel.h.b = localLabel;
      localLabel.a(this, this.r.b, this.r.a);
      this.O.i = localLabel;
      this.O = localLabel;
    }
    else
    {
      this.P.g = this.R;
    }
    this.P = null;
  }
  
  private void b(.Frame paramFrame)
  {
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int[] arrayOfInt1 = paramFrame.c;
    int[] arrayOfInt2 = paramFrame.d;
    int i5;
    for (int i4 = 0; i4 < arrayOfInt1.length; i4++)
    {
      i5 = arrayOfInt1[i4];
      if (i5 == 16777216)
      {
        i1++;
      }
      else
      {
        i2 += i1 + 1;
        i1 = 0;
      }
      if ((i5 == 16777220) || (i5 == 16777219)) {
        i4++;
      }
    }
    for (i4 = 0; i4 < arrayOfInt2.length; i4++)
    {
      i5 = arrayOfInt2[i4];
      i3++;
      if ((i5 == 16777220) || (i5 == 16777219)) {
        i4++;
      }
    }
    int i6 = a(paramFrame.b.c, i2, i3);
    i4 = 0;
    while (i2 > 0)
    {
      i5 = arrayOfInt1[i4];
      this.z[(i6++)] = i5;
      if ((i5 == 16777220) || (i5 == 16777219)) {
        i4++;
      }
      i4++;
      i2--;
    }
    for (i4 = 0; i4 < arrayOfInt2.length; i4++)
    {
      i5 = arrayOfInt2[i4];
      this.z[(i6++)] = i5;
      if ((i5 == 16777220) || (i5 == 16777219)) {
        i4++;
      }
    }
    b();
  }
  
  private void f()
  {
    int i1 = a(0, this.f.length() + 1, 0);
    if ((this.c & 0x8) == 0) {
      if ((this.c & 0x80000) == 0) {
        this.z[(i1++)] = (0x1700000 | this.b.c(this.b.I));
      } else {
        this.z[(i1++)] = 6;
      }
    }
    int i2 = 1;
    for (;;)
    {
      int i3 = i2;
      switch (this.f.charAt(i2++))
      {
      case 'B': 
      case 'C': 
      case 'I': 
      case 'S': 
      case 'Z': 
        this.z[(i1++)] = 1;
        break;
      case 'F': 
        this.z[(i1++)] = 2;
        break;
      case 'J': 
        this.z[(i1++)] = 4;
        break;
      case 'D': 
        this.z[(i1++)] = 3;
        break;
      case '[': 
        while (this.f.charAt(i2) == '[') {
          i2++;
        }
        if (this.f.charAt(i2) == 'L')
        {
          i2++;
          while (this.f.charAt(i2) != ';') {
            i2++;
          }
        }
        this.z[(i1++)] = (0x1700000 | this.b.c(this.f.substring(i3, ++i2)));
        break;
      case 'L': 
        while (this.f.charAt(i2) != ';') {
          i2++;
        }
        this.z[(i1++)] = (0x1700000 | this.b.c(this.f.substring(i3 + 1, i2++)));
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
        break label409;
      }
    }
    label409:
    this.z[1] = (i1 - 3);
    b();
  }
  
  private int a(int paramInt1, int paramInt2, int paramInt3)
  {
    int i1 = 3 + paramInt2 + paramInt3;
    if ((this.z == null) || (this.z.length < i1)) {
      this.z = new int[i1];
    }
    this.z[0] = paramInt1;
    this.z[1] = paramInt2;
    this.z[2] = paramInt3;
    return 3;
  }
  
  private void b()
  {
    if (this.x != null)
    {
      if (this.v == null) {
        this.v = new .ByteVector();
      }
      c();
      this.u += 1;
    }
    this.x = this.z;
    this.z = null;
  }
  
  private void c()
  {
    int i1 = this.z[1];
    int i2 = this.z[2];
    if ((this.b.b & 0xFFFF) < 50)
    {
      this.v.putShort(this.z[0]).putShort(i1);
      a(3, 3 + i1);
      this.v.putShort(i2);
      a(3 + i1, 3 + i1 + i2);
      return;
    }
    int i3 = this.x[1];
    int i4 = 255;
    int i5 = 0;
    int i6;
    if (this.u == 0) {
      i6 = this.z[0];
    } else {
      i6 = this.z[0] - this.x[0] - 1;
    }
    if (i2 == 0)
    {
      i5 = i1 - i3;
      switch (i5)
      {
      case -3: 
      case -2: 
      case -1: 
        i4 = 248;
        i3 = i1;
        break;
      case 0: 
        i4 = i6 < 64 ? 0 : 251;
        break;
      case 1: 
      case 2: 
      case 3: 
        i4 = 252;
      }
    }
    else if ((i1 == i3) && (i2 == 1))
    {
      i4 = i6 < 63 ? 64 : 247;
    }
    if (i4 != 255)
    {
      int i7 = 3;
      for (int i8 = 0; i8 < i3; i8++)
      {
        if (this.z[i7] != this.x[i7])
        {
          i4 = 255;
          break;
        }
        i7++;
      }
    }
    switch (i4)
    {
    case 0: 
      this.v.putByte(i6);
      break;
    case 64: 
      this.v.putByte(64 + i6);
      a(3 + i1, 4 + i1);
      break;
    case 247: 
      this.v.putByte(247).putShort(i6);
      a(3 + i1, 4 + i1);
      break;
    case 251: 
      this.v.putByte(251).putShort(i6);
      break;
    case 248: 
      this.v.putByte(251 + i5).putShort(i6);
      break;
    case 252: 
      this.v.putByte(251 + i5).putShort(i6);
      a(3 + i3, 3 + i1);
      break;
    default: 
      this.v.putByte(255).putShort(i6).putShort(i1);
      a(3, 3 + i1);
      this.v.putShort(i2);
      a(3 + i1, 3 + i1 + i2);
    }
  }
  
  private void a(int paramInt1, int paramInt2)
  {
    for (int i1 = paramInt1; i1 < paramInt2; i1++)
    {
      int i2 = this.z[i1];
      int i3 = i2 & 0xF0000000;
      if (i3 == 0)
      {
        int i4 = i2 & 0xFFFFF;
        switch (i2 & 0xFF00000)
        {
        case 24117248: 
          this.v.putByte(7).putShort(this.b.newClass(this.b.H[i4].g));
          break;
        case 25165824: 
          this.v.putByte(8).putShort(this.b.H[i4].c);
          break;
        default: 
          this.v.putByte(i4);
        }
      }
      else
      {
        StringBuffer localStringBuffer = new StringBuffer();
        i3 >>= 28;
        while (i3-- > 0) {
          localStringBuffer.append('[');
        }
        if ((i2 & 0xFF00000) == 24117248)
        {
          localStringBuffer.append('L');
          localStringBuffer.append(this.b.H[(i2 & 0xFFFFF)].g);
          localStringBuffer.append(';');
        }
        else
        {
          switch (i2 & 0xF)
          {
          case 1: 
            localStringBuffer.append('I');
            break;
          case 2: 
            localStringBuffer.append('F');
            break;
          case 3: 
            localStringBuffer.append('D');
            break;
          case 9: 
            localStringBuffer.append('Z');
            break;
          case 10: 
            localStringBuffer.append('B');
            break;
          case 11: 
            localStringBuffer.append('C');
            break;
          case 12: 
            localStringBuffer.append('S');
            break;
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          default: 
            localStringBuffer.append('J');
          }
        }
        this.v.putByte(7).putShort(this.b.newClass(localStringBuffer.toString()));
      }
    }
  }
  
  private void a(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      this.v.putByte(7).putShort(this.b.newClass((String)paramObject));
    } else if ((paramObject instanceof Integer)) {
      this.v.putByte(((Integer)paramObject).intValue());
    } else {
      this.v.putByte(8).putShort(((.Label)paramObject).c);
    }
  }
  
  final int a()
  {
    if (this.h != 0) {
      return 6 + this.i;
    }
    int i1 = 8;
    int i2;
    if (this.r.b > 0)
    {
      if (this.r.b > 65536) {
        throw new RuntimeException("Method code too large!");
      }
      this.b.newUTF8("Code");
      i1 += 18 + this.r.b + 8 * this.A;
      if (this.E != null)
      {
        this.b.newUTF8("LocalVariableTable");
        i1 += 8 + this.E.b;
      }
      if (this.G != null)
      {
        this.b.newUTF8("LocalVariableTypeTable");
        i1 += 8 + this.G.b;
      }
      if (this.I != null)
      {
        this.b.newUTF8("LineNumberTable");
        i1 += 8 + this.I.b;
      }
      if (this.v != null)
      {
        i2 = (this.b.b & 0xFFFF) >= 50 ? 1 : 0;
        this.b.newUTF8(i2 != 0 ? "StackMapTable" : "StackMap");
        i1 += 8 + this.v.b;
      }
      if (this.W != null)
      {
        this.b.newUTF8("RuntimeVisibleTypeAnnotations");
        i1 += 8 + this.W.a();
      }
      if (this.X != null)
      {
        this.b.newUTF8("RuntimeInvisibleTypeAnnotations");
        i1 += 8 + this.X.a();
      }
      if (this.J != null) {
        i1 += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
      }
    }
    if (this.j > 0)
    {
      this.b.newUTF8("Exceptions");
      i1 += 8 + 2 * this.j;
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0)))
    {
      this.b.newUTF8("Synthetic");
      i1 += 6;
    }
    if ((this.c & 0x20000) != 0)
    {
      this.b.newUTF8("Deprecated");
      i1 += 6;
    }
    if (this.g != null)
    {
      this.b.newUTF8("Signature");
      this.b.newUTF8(this.g);
      i1 += 8;
    }
    if (this.$ != null)
    {
      this.b.newUTF8("MethodParameters");
      i1 += 7 + this.$.b;
    }
    if (this.l != null)
    {
      this.b.newUTF8("AnnotationDefault");
      i1 += 6 + this.l.b;
    }
    if (this.m != null)
    {
      this.b.newUTF8("RuntimeVisibleAnnotations");
      i1 += 8 + this.m.a();
    }
    if (this.n != null)
    {
      this.b.newUTF8("RuntimeInvisibleAnnotations");
      i1 += 8 + this.n.a();
    }
    if (this.U != null)
    {
      this.b.newUTF8("RuntimeVisibleTypeAnnotations");
      i1 += 8 + this.U.a();
    }
    if (this.V != null)
    {
      this.b.newUTF8("RuntimeInvisibleTypeAnnotations");
      i1 += 8 + this.V.a();
    }
    if (this.o != null)
    {
      this.b.newUTF8("RuntimeVisibleParameterAnnotations");
      i1 += 7 + 2 * (this.o.length - this.S);
      for (i2 = this.o.length - 1; i2 >= this.S; i2--) {
        i1 += (this.o[i2] == null ? 0 : this.o[i2].a());
      }
    }
    if (this.p != null)
    {
      this.b.newUTF8("RuntimeInvisibleParameterAnnotations");
      i1 += 7 + 2 * (this.p.length - this.S);
      for (i2 = this.p.length - 1; i2 >= this.S; i2--) {
        i1 += (this.p[i2] == null ? 0 : this.p[i2].a());
      }
    }
    if (this.q != null) {
      i1 += this.q.a(this.b, null, 0, -1, -1);
    }
    return i1;
  }
  
  final void a(.ByteVector paramByteVector)
  {
    int i1 = 64;
    int i2 = 0xE0000 | (this.c & 0x40000) / 64;
    paramByteVector.putShort(this.c & (i2 ^ 0xFFFFFFFF)).putShort(this.d).putShort(this.e);
    if (this.h != 0)
    {
      paramByteVector.putByteArray(this.b.M.b, this.h, this.i);
      return;
    }
    int i3 = 0;
    if (this.r.b > 0) {
      i3++;
    }
    if (this.j > 0) {
      i3++;
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0))) {
      i3++;
    }
    if ((this.c & 0x20000) != 0) {
      i3++;
    }
    if (this.g != null) {
      i3++;
    }
    if (this.$ != null) {
      i3++;
    }
    if (this.l != null) {
      i3++;
    }
    if (this.m != null) {
      i3++;
    }
    if (this.n != null) {
      i3++;
    }
    if (this.U != null) {
      i3++;
    }
    if (this.V != null) {
      i3++;
    }
    if (this.o != null) {
      i3++;
    }
    if (this.p != null) {
      i3++;
    }
    if (this.q != null) {
      i3 += this.q.a();
    }
    paramByteVector.putShort(i3);
    int i4;
    if (this.r.b > 0)
    {
      i4 = 12 + this.r.b + 8 * this.A;
      if (this.E != null) {
        i4 += 8 + this.E.b;
      }
      if (this.G != null) {
        i4 += 8 + this.G.b;
      }
      if (this.I != null) {
        i4 += 8 + this.I.b;
      }
      if (this.v != null) {
        i4 += 8 + this.v.b;
      }
      if (this.W != null) {
        i4 += 8 + this.W.a();
      }
      if (this.X != null) {
        i4 += 8 + this.X.a();
      }
      if (this.J != null) {
        i4 += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
      }
      paramByteVector.putShort(this.b.newUTF8("Code")).putInt(i4);
      paramByteVector.putShort(this.s).putShort(this.t);
      paramByteVector.putInt(this.r.b).putByteArray(this.r.a, 0, this.r.b);
      paramByteVector.putShort(this.A);
      if (this.A > 0) {
        for (.Handler localHandler = this.B; localHandler != null; localHandler = localHandler.f) {
          paramByteVector.putShort(localHandler.a.c).putShort(localHandler.b.c).putShort(localHandler.c.c).putShort(localHandler.e);
        }
      }
      i3 = 0;
      if (this.E != null) {
        i3++;
      }
      if (this.G != null) {
        i3++;
      }
      if (this.I != null) {
        i3++;
      }
      if (this.v != null) {
        i3++;
      }
      if (this.W != null) {
        i3++;
      }
      if (this.X != null) {
        i3++;
      }
      if (this.J != null) {
        i3 += this.J.a();
      }
      paramByteVector.putShort(i3);
      if (this.E != null)
      {
        paramByteVector.putShort(this.b.newUTF8("LocalVariableTable"));
        paramByteVector.putInt(this.E.b + 2).putShort(this.D);
        paramByteVector.putByteArray(this.E.a, 0, this.E.b);
      }
      if (this.G != null)
      {
        paramByteVector.putShort(this.b.newUTF8("LocalVariableTypeTable"));
        paramByteVector.putInt(this.G.b + 2).putShort(this.F);
        paramByteVector.putByteArray(this.G.a, 0, this.G.b);
      }
      if (this.I != null)
      {
        paramByteVector.putShort(this.b.newUTF8("LineNumberTable"));
        paramByteVector.putInt(this.I.b + 2).putShort(this.H);
        paramByteVector.putByteArray(this.I.a, 0, this.I.b);
      }
      if (this.v != null)
      {
        int i5 = (this.b.b & 0xFFFF) >= 50 ? 1 : 0;
        paramByteVector.putShort(this.b.newUTF8(i5 != 0 ? "StackMapTable" : "StackMap"));
        paramByteVector.putInt(this.v.b + 2).putShort(this.u);
        paramByteVector.putByteArray(this.v.a, 0, this.v.b);
      }
      if (this.W != null)
      {
        paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleTypeAnnotations"));
        this.W.a(paramByteVector);
      }
      if (this.X != null)
      {
        paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleTypeAnnotations"));
        this.X.a(paramByteVector);
      }
      if (this.J != null) {
        this.J.a(this.b, this.r.a, this.r.b, this.t, this.s, paramByteVector);
      }
    }
    if (this.j > 0)
    {
      paramByteVector.putShort(this.b.newUTF8("Exceptions")).putInt(2 * this.j + 2);
      paramByteVector.putShort(this.j);
      for (i4 = 0; i4 < this.j; i4++) {
        paramByteVector.putShort(this.k[i4]);
      }
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0))) {
      paramByteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
    }
    if ((this.c & 0x20000) != 0) {
      paramByteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
    }
    if (this.g != null) {
      paramByteVector.putShort(this.b.newUTF8("Signature")).putInt(2).putShort(this.b.newUTF8(this.g));
    }
    if (this.$ != null)
    {
      paramByteVector.putShort(this.b.newUTF8("MethodParameters"));
      paramByteVector.putInt(this.$.b + 1).putByte(this.Z);
      paramByteVector.putByteArray(this.$.a, 0, this.$.b);
    }
    if (this.l != null)
    {
      paramByteVector.putShort(this.b.newUTF8("AnnotationDefault"));
      paramByteVector.putInt(this.l.b);
      paramByteVector.putByteArray(this.l.a, 0, this.l.b);
    }
    if (this.m != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
      this.m.a(paramByteVector);
    }
    if (this.n != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
      this.n.a(paramByteVector);
    }
    if (this.U != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleTypeAnnotations"));
      this.U.a(paramByteVector);
    }
    if (this.V != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.V.a(paramByteVector);
    }
    if (this.o != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleParameterAnnotations"));
      .AnnotationWriter.a(this.o, this.S, paramByteVector);
    }
    if (this.p != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleParameterAnnotations"));
      .AnnotationWriter.a(this.p, this.S, paramByteVector);
    }
    if (this.q != null) {
      this.q.a(this.b, null, 0, -1, -1, paramByteVector);
    }
  }
  
  private void d()
  {
    byte[] arrayOfByte = this.r.a;
    Object localObject1 = new int[0];
    Object localObject2 = new int[0];
    boolean[] arrayOfBoolean = new boolean[this.r.b];
    int i1 = 3;
    int i4;
    int i5;
    int i6;
    Object localObject5;
    do
    {
      if (i1 == 3) {
        i1 = 2;
      }
      i2 = 0;
      while (i2 < arrayOfByte.length)
      {
        int i3 = arrayOfByte[i2] & 0xFF;
        i4 = 0;
        switch (.ClassWriter.a[i3])
        {
        case 0: 
        case 4: 
          i2++;
          break;
        case 9: 
          if (i3 > 201)
          {
            i3 = i3 < 218 ? i3 - 49 : i3 - 20;
            i5 = i2 + c(arrayOfByte, i2 + 1);
          }
          else
          {
            i5 = i2 + b(arrayOfByte, i2 + 1);
          }
          i6 = a((int[])localObject1, (int[])localObject2, i2, i5);
          if (((i6 < 32768) || (i6 > 32767)) && (arrayOfBoolean[i2] == 0))
          {
            if ((i3 == 167) || (i3 == 168)) {
              i4 = 2;
            } else {
              i4 = 5;
            }
            arrayOfBoolean[i2] = true;
          }
          i2 += 3;
          break;
        case 10: 
          i2 += 5;
          break;
        case 14: 
          if (i1 == 1)
          {
            i6 = a((int[])localObject1, (int[])localObject2, 0, i2);
            i4 = -(i6 & 0x3);
          }
          else if (arrayOfBoolean[i2] == 0)
          {
            i4 = i2 & 0x3;
            arrayOfBoolean[i2] = true;
          }
          i2 = i2 + 4 - (i2 & 0x3);
          i2 += 4 * (a(arrayOfByte, i2 + 8) - a(arrayOfByte, i2 + 4) + 1) + 12;
          break;
        case 15: 
          if (i1 == 1)
          {
            i6 = a((int[])localObject1, (int[])localObject2, 0, i2);
            i4 = -(i6 & 0x3);
          }
          else if (arrayOfBoolean[i2] == 0)
          {
            i4 = i2 & 0x3;
            arrayOfBoolean[i2] = true;
          }
          i2 = i2 + 4 - (i2 & 0x3);
          i2 += 8 * a(arrayOfByte, i2 + 4) + 8;
          break;
        case 17: 
          i3 = arrayOfByte[(i2 + 1)] & 0xFF;
          if (i3 == 132) {
            i2 += 6;
          } else {
            i2 += 4;
          }
          break;
        case 1: 
        case 3: 
        case 11: 
          i2 += 2;
          break;
        case 2: 
        case 5: 
        case 6: 
        case 12: 
        case 13: 
          i2 += 3;
          break;
        case 7: 
        case 8: 
          i2 += 5;
          break;
        case 16: 
        default: 
          i2 += 4;
        }
        if (i4 != 0)
        {
          localObject4 = new int[localObject1.length + 1];
          localObject5 = new int[localObject2.length + 1];
          System.arraycopy(localObject1, 0, localObject4, 0, localObject1.length);
          System.arraycopy(localObject2, 0, localObject5, 0, localObject2.length);
          localObject4[localObject1.length] = i2;
          localObject5[localObject2.length] = i4;
          localObject1 = localObject4;
          localObject2 = localObject5;
          if (i4 > 0) {
            i1 = 3;
          }
        }
      }
      if (i1 < 3) {
        i1--;
      }
    } while (i1 != 0);
    .ByteVector localByteVector = new .ByteVector(this.r.b);
    int i2 = 0;
    while (i2 < this.r.b)
    {
      i4 = arrayOfByte[i2] & 0xFF;
      int i7;
      int i8;
      switch (.ClassWriter.a[i4])
      {
      case 0: 
      case 4: 
        localByteVector.putByte(i4);
        i2++;
        break;
      case 9: 
        if (i4 > 201)
        {
          i4 = i4 < 218 ? i4 - 49 : i4 - 20;
          i5 = i2 + c(arrayOfByte, i2 + 1);
        }
        else
        {
          i5 = i2 + b(arrayOfByte, i2 + 1);
        }
        i6 = a((int[])localObject1, (int[])localObject2, i2, i5);
        if (arrayOfBoolean[i2] != 0)
        {
          if (i4 == 167)
          {
            localByteVector.putByte(200);
          }
          else if (i4 == 168)
          {
            localByteVector.putByte(201);
          }
          else
          {
            localByteVector.putByte(i4 <= 166 ? (i4 + 1 ^ 0x1) - 1 : i4 ^ 0x1);
            localByteVector.putShort(8);
            localByteVector.putByte(200);
            i6 -= 3;
          }
          localByteVector.putInt(i6);
        }
        else
        {
          localByteVector.putByte(i4);
          localByteVector.putShort(i6);
        }
        i2 += 3;
        break;
      case 10: 
        i5 = i2 + a(arrayOfByte, i2 + 1);
        i6 = a((int[])localObject1, (int[])localObject2, i2, i5);
        localByteVector.putByte(i4);
        localByteVector.putInt(i6);
        i2 += 5;
        break;
      case 14: 
        i7 = i2;
        i2 = i2 + 4 - (i7 & 0x3);
        localByteVector.putByte(170);
        localByteVector.putByteArray(null, 0, (4 - localByteVector.b % 4) % 4);
        i5 = i7 + a(arrayOfByte, i2);
        i2 += 4;
        i6 = a((int[])localObject1, (int[])localObject2, i7, i5);
        localByteVector.putInt(i6);
        i8 = a(arrayOfByte, i2);
        i2 += 4;
        localByteVector.putInt(i8);
        i8 = a(arrayOfByte, i2) - i8 + 1;
        i2 += 4;
        localByteVector.putInt(a(arrayOfByte, i2 - 4));
      case 15: 
      case 17: 
      case 1: 
      case 3: 
      case 11: 
      case 2: 
      case 5: 
      case 6: 
      case 12: 
      case 13: 
      case 7: 
      case 8: 
      case 16: 
      default: 
        while (i8 > 0)
        {
          i5 = i7 + a(arrayOfByte, i2);
          i2 += 4;
          i6 = a((int[])localObject1, (int[])localObject2, i7, i5);
          localByteVector.putInt(i6);
          i8--;
          continue;
          i7 = i2;
          i2 = i2 + 4 - (i7 & 0x3);
          localByteVector.putByte(171);
          localByteVector.putByteArray(null, 0, (4 - localByteVector.b % 4) % 4);
          i5 = i7 + a(arrayOfByte, i2);
          i2 += 4;
          i6 = a((int[])localObject1, (int[])localObject2, i7, i5);
          localByteVector.putInt(i6);
          i8 = a(arrayOfByte, i2);
          i2 += 4;
          localByteVector.putInt(i8);
          while (i8 > 0)
          {
            localByteVector.putInt(a(arrayOfByte, i2));
            i2 += 4;
            i5 = i7 + a(arrayOfByte, i2);
            i2 += 4;
            i6 = a((int[])localObject1, (int[])localObject2, i7, i5);
            localByteVector.putInt(i6);
            i8--;
            continue;
            i4 = arrayOfByte[(i2 + 1)] & 0xFF;
            if (i4 == 132)
            {
              localByteVector.putByteArray(arrayOfByte, i2, 6);
              i2 += 6;
            }
            else
            {
              localByteVector.putByteArray(arrayOfByte, i2, 4);
              i2 += 4;
              break;
              localByteVector.putByteArray(arrayOfByte, i2, 2);
              i2 += 2;
              break;
              localByteVector.putByteArray(arrayOfByte, i2, 3);
              i2 += 3;
              break;
              localByteVector.putByteArray(arrayOfByte, i2, 5);
              i2 += 5;
              break;
              localByteVector.putByteArray(arrayOfByte, i2, 4);
              i2 += 4;
            }
          }
        }
      }
    }
    if (this.M == 0)
    {
      for (localObject3 = this.N; localObject3 != null; localObject3 = ((.Label)localObject3).i)
      {
        i2 = ((.Label)localObject3).c - 3;
        if ((i2 >= 0) && (arrayOfBoolean[i2] != 0)) {
          localObject3.a |= 0x10;
        }
        a((int[])localObject1, (int[])localObject2, (.Label)localObject3);
      }
      for (i9 = 0; i9 < this.b.H.length; i9++)
      {
        localObject4 = this.b.H[i9];
        if ((localObject4 != null) && (((.Item)localObject4).b == 31)) {
          ((.Item)localObject4).c = a((int[])localObject1, (int[])localObject2, 0, ((.Item)localObject4).c);
        }
      }
    }
    else if (this.u > 0)
    {
      this.b.L = true;
    }
    for (Object localObject3 = this.B; localObject3 != null; localObject3 = ((.Handler)localObject3).f)
    {
      a((int[])localObject1, (int[])localObject2, ((.Handler)localObject3).a);
      a((int[])localObject1, (int[])localObject2, ((.Handler)localObject3).b);
      a((int[])localObject1, (int[])localObject2, ((.Handler)localObject3).c);
    }
    for (int i9 = 0; i9 < 2; i9++)
    {
      localObject4 = i9 == 0 ? this.E : this.G;
      if (localObject4 != null)
      {
        arrayOfByte = ((.ByteVector)localObject4).a;
        for (i2 = 0; i2 < ((.ByteVector)localObject4).b; i2 += 10)
        {
          i5 = c(arrayOfByte, i2);
          i6 = a((int[])localObject1, (int[])localObject2, 0, i5);
          a(arrayOfByte, i2, i6);
          i5 += c(arrayOfByte, i2 + 2);
          i6 = a((int[])localObject1, (int[])localObject2, 0, i5) - i6;
          a(arrayOfByte, i2 + 2, i6);
        }
      }
    }
    if (this.I != null)
    {
      arrayOfByte = this.I.a;
      for (i2 = 0; i2 < this.I.b; i2 += 4) {
        a(arrayOfByte, i2, a((int[])localObject1, (int[])localObject2, 0, c(arrayOfByte, i2)));
      }
    }
    for (Object localObject4 = this.J; localObject4 != null; localObject4 = ((.Attribute)localObject4).a)
    {
      localObject5 = ((.Attribute)localObject4).getLabels();
      if (localObject5 != null) {
        for (i9 = localObject5.length - 1; i9 >= 0; i9--) {
          a((int[])localObject1, (int[])localObject2, localObject5[i9]);
        }
      }
    }
    this.r = localByteVector;
  }
  
  static int c(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  static short b(byte[] paramArrayOfByte, int paramInt)
  {
    return (short)((paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  static int a(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  static void a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)(paramInt2 >>> 8));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)paramInt2);
  }
  
  static int a(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2)
  {
    int i1 = paramInt2 - paramInt1;
    for (int i2 = 0; i2 < paramArrayOfInt1.length; i2++) {
      if ((paramInt1 < paramArrayOfInt1[i2]) && (paramArrayOfInt1[i2] <= paramInt2)) {
        i1 += paramArrayOfInt2[i2];
      } else if ((paramInt2 < paramArrayOfInt1[i2]) && (paramArrayOfInt1[i2] <= paramInt1)) {
        i1 -= paramArrayOfInt2[i2];
      }
    }
    return i1;
  }
  
  static void a(int[] paramArrayOfInt1, int[] paramArrayOfInt2, .Label paramLabel)
  {
    if ((paramLabel.a & 0x4) == 0)
    {
      paramLabel.c = a(paramArrayOfInt1, paramArrayOfInt2, 0, paramLabel.c);
      paramLabel.a |= 0x4;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$MethodWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */