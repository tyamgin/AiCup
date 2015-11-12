package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.internal.asm..AnnotationVisitor;
import com.google.inject.internal.asm..ClassReader;
import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..FieldVisitor;
import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..MethodVisitor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.Map;

final class LineNumbers
{
  private final Class type;
  private final Map lines = Maps.newHashMap();
  private String source;
  private int firstLine = Integer.MAX_VALUE;
  
  public LineNumbers(Class paramClass)
    throws IOException
  {
    this.type = paramClass;
    if (!paramClass.isArray())
    {
      String str = String.valueOf(String.valueOf(paramClass.getName().replace('.', '/')));
      InputStream localInputStream = paramClass.getResourceAsStream(7 + str.length() + "/" + str + ".class");
      if (localInputStream != null) {
        new .ClassReader(localInputStream).accept(new LineNumberReader(), 4);
      }
    }
  }
  
  public String getSource()
  {
    return this.source;
  }
  
  public Integer getLineNumber(Member paramMember)
  {
    Preconditions.checkArgument(this.type == paramMember.getDeclaringClass(), "Member %s belongs to %s, not %s", new Object[] { paramMember, paramMember.getDeclaringClass(), this.type });
    return (Integer)this.lines.get(memberKey(paramMember));
  }
  
  public int getFirstLine()
  {
    return this.firstLine == Integer.MAX_VALUE ? 1 : this.firstLine;
  }
  
  /* Error */
  private String memberKey(Member arg1)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc 8
    //   3: invokestatic 33	com/google/common/base/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   6: pop
    //   7: aload_1
    //   8: instanceof 24
    //   11: ifeq +10 -> 21
    //   14: aload_1
    //   15: invokeinterface 58 1 0
    //   20: areturn
    //   21: aload_1
    //   22: instanceof 26
    //   25: ifeq +45 -> 70
    //   28: aload_1
    //   29: invokeinterface 58 1 0
    //   34: invokestatic 51	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   37: aload_1
    //   38: checkcast 26	java/lang/reflect/Method
    //   41: invokestatic 38	com/google/inject/internal/asm/$Type:getMethodDescriptor	(Ljava/lang/reflect/Method;)Ljava/lang/String;
    //   44: invokestatic 51	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   47: dup
    //   48: invokevirtual 49	java/lang/String:length	()I
    //   51: ifeq +9 -> 60
    //   54: invokevirtual 48	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   57: goto +12 -> 69
    //   60: pop
    //   61: new 21	java/lang/String
    //   64: dup_x1
    //   65: swap
    //   66: invokespecial 47	java/lang/String:<init>	(Ljava/lang/String;)V
    //   69: areturn
    //   70: aload_1
    //   71: instanceof 23
    //   74: ifeq +70 -> 144
    //   77: new 22	java/lang/StringBuilder
    //   80: dup
    //   81: invokespecial 52	java/lang/StringBuilder:<init>	()V
    //   84: ldc 5
    //   86: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: astore_2
    //   90: aload_1
    //   91: checkcast 23	java/lang/reflect/Constructor
    //   94: invokevirtual 56	java/lang/reflect/Constructor:getParameterTypes	()[Ljava/lang/Class;
    //   97: astore_3
    //   98: aload_3
    //   99: arraylength
    //   100: istore 4
    //   102: iconst_0
    //   103: istore 5
    //   105: iload 5
    //   107: iload 4
    //   109: if_icmpge +25 -> 134
    //   112: aload_3
    //   113: iload 5
    //   115: aaload
    //   116: astore 6
    //   118: aload_2
    //   119: aload 6
    //   121: invokestatic 37	com/google/inject/internal/asm/$Type:getDescriptor	(Ljava/lang/Class;)Ljava/lang/String;
    //   124: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: pop
    //   128: iinc 5 1
    //   131: goto -26 -> 105
    //   134: aload_2
    //   135: ldc 2
    //   137: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: invokevirtual 55	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   143: areturn
    //   144: new 18	java/lang/IllegalArgumentException
    //   147: dup
    //   148: aload_1
    //   149: invokevirtual 46	java/lang/Object:getClass	()Ljava/lang/Class;
    //   152: invokestatic 51	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   155: invokestatic 51	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   158: astore_2
    //   159: new 22	java/lang/StringBuilder
    //   162: dup
    //   163: bipush 45
    //   165: aload_2
    //   166: invokevirtual 49	java/lang/String:length	()I
    //   169: iadd
    //   170: invokespecial 53	java/lang/StringBuilder:<init>	(I)V
    //   173: ldc 7
    //   175: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload_2
    //   179: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: invokevirtual 55	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   185: invokespecial 44	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   188: athrow
  }
  
  private class LineNumberReader
    extends .ClassVisitor
  {
    private int line = -1;
    private String pendingMethod;
    private String name;
    
    LineNumberReader()
    {
      super();
    }
    
    public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
    {
      this.name = paramString1;
    }
    
    /* Error */
    public .MethodVisitor visitMethod(int arg1, String arg2, String arg3, String arg4, String[] arg5)
    {
      // Byte code:
      //   0: iload_1
      //   1: iconst_2
      //   2: iand
      //   3: ifeq +5 -> 8
      //   6: aconst_null
      //   7: areturn
      //   8: aload_0
      //   9: aload_2
      //   10: invokestatic 27	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   13: aload_3
      //   14: invokestatic 27	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   17: dup
      //   18: invokevirtual 26	java/lang/String:length	()I
      //   21: ifeq +9 -> 30
      //   24: invokevirtual 25	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
      //   27: goto +12 -> 39
      //   30: pop
      //   31: new 10	java/lang/String
      //   34: dup_x1
      //   35: swap
      //   36: invokespecial 24	java/lang/String:<init>	(Ljava/lang/String;)V
      //   39: putfield 14	com/google/inject/internal/util/LineNumbers$LineNumberReader:pendingMethod	Ljava/lang/String;
      //   42: aload_0
      //   43: iconst_m1
      //   44: putfield 12	com/google/inject/internal/util/LineNumbers$LineNumberReader:line	I
      //   47: new 8	com/google/inject/internal/util/LineNumbers$LineNumberReader$LineNumberMethodVisitor
      //   50: dup
      //   51: aload_0
      //   52: invokespecial 22	com/google/inject/internal/util/LineNumbers$LineNumberReader$LineNumberMethodVisitor:<init>	(Lcom/google/inject/internal/util/LineNumbers$LineNumberReader;)V
      //   55: areturn
    }
    
    public void visitSource(String paramString1, String paramString2)
    {
      LineNumbers.this.source = paramString1;
    }
    
    public void visitLineNumber(int paramInt, .Label paramLabel)
    {
      if (paramInt < LineNumbers.this.firstLine) {
        LineNumbers.this.firstLine = paramInt;
      }
      this.line = paramInt;
      if (this.pendingMethod != null)
      {
        LineNumbers.this.lines.put(this.pendingMethod, Integer.valueOf(paramInt));
        this.pendingMethod = null;
      }
    }
    
    public .FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
    {
      return null;
    }
    
    public .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
    {
      return new LineNumberAnnotationVisitor();
    }
    
    public .AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
    {
      return new LineNumberAnnotationVisitor();
    }
    
    class LineNumberAnnotationVisitor
      extends .AnnotationVisitor
    {
      LineNumberAnnotationVisitor()
      {
        super();
      }
      
      public .AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
      {
        return this;
      }
      
      public .AnnotationVisitor visitArray(String paramString)
      {
        return this;
      }
      
      public void visitLocalVariable(String paramString1, String paramString2, String paramString3, .Label paramLabel1, .Label paramLabel2, int paramInt) {}
    }
    
    class LineNumberMethodVisitor
      extends .MethodVisitor
    {
      LineNumberMethodVisitor()
      {
        super();
      }
      
      public .AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
      {
        return new LineNumbers.LineNumberReader.LineNumberAnnotationVisitor(LineNumbers.LineNumberReader.this);
      }
      
      public .AnnotationVisitor visitAnnotationDefault()
      {
        return new LineNumbers.LineNumberReader.LineNumberAnnotationVisitor(LineNumbers.LineNumberReader.this);
      }
      
      public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
      {
        if ((paramInt == 181) && (LineNumbers.LineNumberReader.this.name.equals(paramString1)) && (!LineNumbers.this.lines.containsKey(paramString2)) && (LineNumbers.LineNumberReader.this.line != -1)) {
          LineNumbers.this.lines.put(paramString2, Integer.valueOf(LineNumbers.LineNumberReader.this.line));
        }
      }
      
      public void visitLineNumber(int paramInt, .Label paramLabel)
      {
        LineNumbers.LineNumberReader.this.visitLineNumber(paramInt, paramLabel);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\util\LineNumbers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */