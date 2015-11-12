package com.google.inject.internal.asm;

public abstract class $MethodVisitor
{
  protected final int api;
  protected MethodVisitor mv;
  
  public $MethodVisitor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public $MethodVisitor(int paramInt, MethodVisitor paramMethodVisitor)
  {
    if ((paramInt != 262144) && (paramInt != 327680)) {
      throw new IllegalArgumentException();
    }
    this.api = paramInt;
    this.mv = paramMethodVisitor;
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (this.api < 327680) {
      throw new RuntimeException();
    }
    if (this.mv != null) {
      this.mv.visitParameter(paramString, paramInt);
    }
  }
  
  public .AnnotationVisitor visitAnnotationDefault()
  {
    if (this.mv != null) {
      return this.mv.visitAnnotationDefault();
    }
    return null;
  }
  
  public .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    if (this.mv != null) {
      return this.mv.visitAnnotation(paramString, paramBoolean);
    }
    return null;
  }
  
  public .AnnotationVisitor visitTypeAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (this.api < 327680) {
      throw new RuntimeException();
    }
    if (this.mv != null) {
      return this.mv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public .AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    if (this.mv != null) {
      return this.mv.visitParameterAnnotation(paramInt, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitAttribute(.Attribute paramAttribute)
  {
    if (this.mv != null) {
      this.mv.visitAttribute(paramAttribute);
    }
  }
  
  public void visitCode()
  {
    if (this.mv != null) {
      this.mv.visitCode();
    }
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (this.mv != null) {
      this.mv.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
    }
  }
  
  public void visitInsn(int paramInt)
  {
    if (this.mv != null) {
      this.mv.visitInsn(paramInt);
    }
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    if (this.mv != null) {
      this.mv.visitIntInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    if (this.mv != null) {
      this.mv.visitVarInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    if (this.mv != null) {
      this.mv.visitTypeInsn(paramInt, paramString);
    }
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (this.mv != null) {
      this.mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    }
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (this.api >= 327680)
    {
      boolean bool = paramInt == 185;
      visitMethodInsn(paramInt, paramString1, paramString2, paramString3, bool);
      return;
    }
    if (this.mv != null) {
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
    }
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (this.api < 327680)
    {
      if (paramBoolean != (paramInt == 185)) {
        throw new IllegalArgumentException("INVOKESPECIAL/STATIC on interfaces require ASM 5");
      }
      visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    if (this.mv != null) {
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, .Handle paramHandle, Object... paramVarArgs)
  {
    if (this.mv != null) {
      this.mv.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
    }
  }
  
  public void visitJumpInsn(int paramInt, .Label paramLabel)
  {
    if (this.mv != null) {
      this.mv.visitJumpInsn(paramInt, paramLabel);
    }
  }
  
  public void visitLabel(.Label paramLabel)
  {
    if (this.mv != null) {
      this.mv.visitLabel(paramLabel);
    }
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    if (this.mv != null) {
      this.mv.visitLdcInsn(paramObject);
    }
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    if (this.mv != null) {
      this.mv.visitIincInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, .Label paramLabel, .Label... paramVarArgs)
  {
    if (this.mv != null) {
      this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
    }
  }
  
  public void visitLookupSwitchInsn(.Label paramLabel, int[] paramArrayOfInt, .Label[] paramArrayOfLabel)
  {
    if (this.mv != null) {
      this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
    }
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    if (this.mv != null) {
      this.mv.visitMultiANewArrayInsn(paramString, paramInt);
    }
  }
  
  public .AnnotationVisitor visitInsnAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (this.api < 327680) {
      throw new RuntimeException();
    }
    if (this.mv != null) {
      return this.mv.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitTryCatchBlock(.Label paramLabel1, .Label paramLabel2, .Label paramLabel3, String paramString)
  {
    if (this.mv != null) {
      this.mv.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    }
  }
  
  public .AnnotationVisitor visitTryCatchAnnotation(int paramInt, .TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (this.api < 327680) {
      throw new RuntimeException();
    }
    if (this.mv != null) {
      return this.mv.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, .Label paramLabel1, .Label paramLabel2, int paramInt)
  {
    if (this.mv != null) {
      this.mv.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
    }
  }
  
  public .AnnotationVisitor visitLocalVariableAnnotation(int paramInt, .TypePath paramTypePath, .Label[] paramArrayOfLabel1, .Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    if (this.api < 327680) {
      throw new RuntimeException();
    }
    if (this.mv != null) {
      return this.mv.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitLineNumber(int paramInt, .Label paramLabel)
  {
    if (this.mv != null) {
      this.mv.visitLineNumber(paramInt, paramLabel);
    }
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    if (this.mv != null) {
      this.mv.visitMaxs(paramInt1, paramInt2);
    }
  }
  
  public void visitEnd()
  {
    if (this.mv != null) {
      this.mv.visitEnd();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$MethodVisitor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */