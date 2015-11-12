package com.google.inject.internal.asm;

public abstract class $FieldVisitor
{
  protected final int api;
  protected FieldVisitor fv;
  
  public $FieldVisitor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public $FieldVisitor(int paramInt, FieldVisitor paramFieldVisitor)
  {
    if ((paramInt != 262144) && (paramInt != 327680)) {
      throw new IllegalArgumentException();
    }
    this.api = paramInt;
    this.fv = paramFieldVisitor;
  }
  
  public .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    if (this.fv != null) {
      return this.fv.visitAnnotation(paramString, paramBoolean);
    }
    return null;
  }
  
  public .AnnotationVisitor visitTypeAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (this.api < 327680) {
      throw new RuntimeException();
    }
    if (this.fv != null) {
      return this.fv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitAttribute(.Attribute paramAttribute)
  {
    if (this.fv != null) {
      this.fv.visitAttribute(paramAttribute);
    }
  }
  
  public void visitEnd()
  {
    if (this.fv != null) {
      this.fv.visitEnd();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$FieldVisitor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */