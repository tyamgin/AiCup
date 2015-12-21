package com.google.inject.internal.asm;

final class $FieldWriter
  extends .FieldVisitor
{
  private final .ClassWriter b;
  private final int c;
  private final int d;
  private final int e;
  private int f;
  private int g;
  private .AnnotationWriter h;
  private .AnnotationWriter i;
  private .AnnotationWriter k;
  private .AnnotationWriter l;
  private .Attribute j;
  
  $FieldWriter(.ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    super(327680);
    if (paramClassWriter.B == null) {
      paramClassWriter.B = this;
    } else {
      paramClassWriter.C.fv = this;
    }
    paramClassWriter.C = this;
    this.b = paramClassWriter;
    this.c = paramInt;
    this.d = paramClassWriter.newUTF8(paramString1);
    this.e = paramClassWriter.newUTF8(paramString2);
    if (paramString3 != null) {
      this.f = paramClassWriter.newUTF8(paramString3);
    }
    if (paramObject != null) {
      this.g = paramClassWriter.a(paramObject).a;
    }
  }
  
  public .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    .ByteVector localByteVector = new .ByteVector();
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    .AnnotationWriter localAnnotationWriter = new .AnnotationWriter(this.b, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.h;
      this.h = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.i;
      this.i = localAnnotationWriter;
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
      localAnnotationWriter.g = this.k;
      this.k = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.l;
      this.l = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitAttribute(.Attribute paramAttribute)
  {
    paramAttribute.a = this.j;
    this.j = paramAttribute;
  }
  
  public void visitEnd() {}
  
  int a()
  {
    int m = 8;
    if (this.g != 0)
    {
      this.b.newUTF8("ConstantValue");
      m += 8;
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0)))
    {
      this.b.newUTF8("Synthetic");
      m += 6;
    }
    if ((this.c & 0x20000) != 0)
    {
      this.b.newUTF8("Deprecated");
      m += 6;
    }
    if (this.f != 0)
    {
      this.b.newUTF8("Signature");
      m += 8;
    }
    if (this.h != null)
    {
      this.b.newUTF8("RuntimeVisibleAnnotations");
      m += 8 + this.h.a();
    }
    if (this.i != null)
    {
      this.b.newUTF8("RuntimeInvisibleAnnotations");
      m += 8 + this.i.a();
    }
    if (this.k != null)
    {
      this.b.newUTF8("RuntimeVisibleTypeAnnotations");
      m += 8 + this.k.a();
    }
    if (this.l != null)
    {
      this.b.newUTF8("RuntimeInvisibleTypeAnnotations");
      m += 8 + this.l.a();
    }
    if (this.j != null) {
      m += this.j.a(this.b, null, 0, -1, -1);
    }
    return m;
  }
  
  void a(.ByteVector paramByteVector)
  {
    int m = 64;
    int n = 0x60000 | (this.c & 0x40000) / 64;
    paramByteVector.putShort(this.c & (n ^ 0xFFFFFFFF)).putShort(this.d).putShort(this.e);
    int i1 = 0;
    if (this.g != 0) {
      i1++;
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0))) {
      i1++;
    }
    if ((this.c & 0x20000) != 0) {
      i1++;
    }
    if (this.f != 0) {
      i1++;
    }
    if (this.h != null) {
      i1++;
    }
    if (this.i != null) {
      i1++;
    }
    if (this.k != null) {
      i1++;
    }
    if (this.l != null) {
      i1++;
    }
    if (this.j != null) {
      i1 += this.j.a();
    }
    paramByteVector.putShort(i1);
    if (this.g != 0)
    {
      paramByteVector.putShort(this.b.newUTF8("ConstantValue"));
      paramByteVector.putInt(2).putShort(this.g);
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0))) {
      paramByteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
    }
    if ((this.c & 0x20000) != 0) {
      paramByteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
    }
    if (this.f != 0)
    {
      paramByteVector.putShort(this.b.newUTF8("Signature"));
      paramByteVector.putInt(2).putShort(this.f);
    }
    if (this.h != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
      this.h.a(paramByteVector);
    }
    if (this.i != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
      this.i.a(paramByteVector);
    }
    if (this.k != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleTypeAnnotations"));
      this.k.a(paramByteVector);
    }
    if (this.l != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.l.a(paramByteVector);
    }
    if (this.j != null) {
      this.j.a(this.b, null, 0, -1, -1, paramByteVector);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$FieldWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */