package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..MethodVisitor;
import com.google.inject.internal.asm..Type;

public class $LocalVariablesSorter
  extends .MethodVisitor
{
  protected final int firstLocal;
  private final State state;
  
  public $LocalVariablesSorter(int paramInt, String paramString, .MethodVisitor paramMethodVisitor)
  {
    super(262144, paramMethodVisitor);
    this.state = new State(null);
    .Type[] arrayOfType = .Type.getArgumentTypes(paramString);
    this.state.nextLocal = ((0x8 & paramInt) != 0 ? 0 : 1);
    for (int i = 0; i < arrayOfType.length; i++) {
      this.state.nextLocal += arrayOfType[i].getSize();
    }
    this.firstLocal = this.state.nextLocal;
  }
  
  public $LocalVariablesSorter(LocalVariablesSorter paramLocalVariablesSorter)
  {
    super(262144, paramLocalVariablesSorter.mv);
    this.state = paramLocalVariablesSorter.state;
    this.firstLocal = paramLocalVariablesSorter.firstLocal;
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    int i;
    switch (paramInt1)
    {
    case 22: 
    case 24: 
    case 55: 
    case 57: 
      i = 2;
      break;
    default: 
      i = 1;
    }
    this.mv.visitVarInsn(paramInt1, remap(paramInt2, i));
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    this.mv.visitIincInsn(remap(paramInt1, 1), paramInt2);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    this.mv.visitMaxs(paramInt1, this.state.nextLocal);
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, .Label paramLabel1, .Label paramLabel2, int paramInt)
  {
    this.mv.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, remap(paramInt));
  }
  
  protected int newLocal(int paramInt)
  {
    int i = this.state.nextLocal;
    this.state.nextLocal += paramInt;
    return i;
  }
  
  private int remap(int paramInt1, int paramInt2)
  {
    if (paramInt1 < this.firstLocal) {
      return paramInt1;
    }
    int i = 2 * paramInt1 + paramInt2 - 1;
    int j = this.state.mapping.length;
    if (i >= j)
    {
      int[] arrayOfInt = new int[Math.max(2 * j, i + 1)];
      System.arraycopy(this.state.mapping, 0, arrayOfInt, 0, j);
      this.state.mapping = arrayOfInt;
    }
    int k = this.state.mapping[i];
    if (k == 0)
    {
      k = this.state.nextLocal + 1;
      this.state.mapping[i] = k;
      this.state.nextLocal += paramInt2;
    }
    return k - 1;
  }
  
  private int remap(int paramInt)
  {
    if (paramInt < this.firstLocal) {
      return paramInt;
    }
    int i = 2 * paramInt;
    int j = i < this.state.mapping.length ? this.state.mapping[i] : 0;
    if (j == 0) {
      j = i + 1 < this.state.mapping.length ? this.state.mapping[(i + 1)] : 0;
    }
    if (j == 0) {
      throw new IllegalStateException("Unknown local variable " + paramInt);
    }
    return j - 1;
  }
  
  private static class State
  {
    int[] mapping = new int[40];
    int nextLocal;
    
    private State() {}
    
    State(.LocalVariablesSorter.1 param1)
    {
      this();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$LocalVariablesSorter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */