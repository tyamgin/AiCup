package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Attribute;
import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..MethodVisitor;
import com.google.inject.internal.asm..Type;
import java.util.Arrays;

public class $CodeEmitter
  extends .LocalVariablesSorter
{
  private static final .Signature BOOLEAN_VALUE = .TypeUtils.parseSignature("boolean booleanValue()");
  private static final .Signature CHAR_VALUE = .TypeUtils.parseSignature("char charValue()");
  private static final .Signature LONG_VALUE = .TypeUtils.parseSignature("long longValue()");
  private static final .Signature DOUBLE_VALUE = .TypeUtils.parseSignature("double doubleValue()");
  private static final .Signature FLOAT_VALUE = .TypeUtils.parseSignature("float floatValue()");
  private static final .Signature INT_VALUE = .TypeUtils.parseSignature("int intValue()");
  private static final .Signature CSTRUCT_NULL = .TypeUtils.parseConstructor("");
  private static final .Signature CSTRUCT_STRING = .TypeUtils.parseConstructor("String");
  public static final int ADD = 96;
  public static final int MUL = 104;
  public static final int XOR = 130;
  public static final int USHR = 124;
  public static final int SUB = 100;
  public static final int DIV = 108;
  public static final int NEG = 116;
  public static final int REM = 112;
  public static final int AND = 126;
  public static final int OR = 128;
  public static final int GT = 157;
  public static final int LT = 155;
  public static final int GE = 156;
  public static final int LE = 158;
  public static final int NE = 154;
  public static final int EQ = 153;
  private .ClassEmitter ce;
  private State state;
  
  $CodeEmitter(.ClassEmitter paramClassEmitter, .MethodVisitor paramMethodVisitor, int paramInt, .Signature paramSignature, .Type[] paramArrayOfType)
  {
    super(paramInt, paramSignature.getDescriptor(), paramMethodVisitor);
    this.ce = paramClassEmitter;
    this.state = new State(paramClassEmitter.getClassInfo(), paramInt, paramSignature, paramArrayOfType);
  }
  
  public $CodeEmitter(CodeEmitter paramCodeEmitter)
  {
    super(paramCodeEmitter);
    this.ce = paramCodeEmitter.ce;
    this.state = paramCodeEmitter.state;
  }
  
  public boolean isStaticHook()
  {
    return false;
  }
  
  public .Signature getSignature()
  {
    return this.state.sig;
  }
  
  public .Type getReturnType()
  {
    return this.state.sig.getReturnType();
  }
  
  public .MethodInfo getMethodInfo()
  {
    return this.state;
  }
  
  public .ClassEmitter getClassEmitter()
  {
    return this.ce;
  }
  
  public void end_method()
  {
    visitMaxs(0, 0);
  }
  
  public .Block begin_block()
  {
    return new .Block(this);
  }
  
  public void catch_exception(.Block paramBlock, .Type paramType)
  {
    if (paramBlock.getEnd() == null) {
      throw new IllegalStateException("end of block is unset");
    }
    this.mv.visitTryCatchBlock(paramBlock.getStart(), paramBlock.getEnd(), mark(), paramType.getInternalName());
  }
  
  public void goTo(.Label paramLabel)
  {
    this.mv.visitJumpInsn(167, paramLabel);
  }
  
  public void ifnull(.Label paramLabel)
  {
    this.mv.visitJumpInsn(198, paramLabel);
  }
  
  public void ifnonnull(.Label paramLabel)
  {
    this.mv.visitJumpInsn(199, paramLabel);
  }
  
  public void if_jump(int paramInt, .Label paramLabel)
  {
    this.mv.visitJumpInsn(paramInt, paramLabel);
  }
  
  public void if_icmp(int paramInt, .Label paramLabel)
  {
    if_cmp(.Type.INT_TYPE, paramInt, paramLabel);
  }
  
  public void if_cmp(.Type paramType, int paramInt, .Label paramLabel)
  {
    int i = -1;
    int j = paramInt;
    switch (paramInt)
    {
    case 156: 
      j = 155;
      break;
    case 158: 
      j = 157;
    }
    switch (paramType.getSort())
    {
    case 7: 
      this.mv.visitInsn(148);
      break;
    case 8: 
      this.mv.visitInsn(152);
      break;
    case 6: 
      this.mv.visitInsn(150);
      break;
    case 9: 
    case 10: 
      switch (paramInt)
      {
      case 153: 
        this.mv.visitJumpInsn(165, paramLabel);
        return;
      case 154: 
        this.mv.visitJumpInsn(166, paramLabel);
        return;
      }
      throw new IllegalArgumentException("Bad comparison for type " + paramType);
    default: 
      switch (paramInt)
      {
      case 153: 
        i = 159;
        break;
      case 154: 
        i = 160;
        break;
      case 156: 
        swap();
      case 155: 
        i = 161;
        break;
      case 158: 
        swap();
      case 157: 
        i = 163;
      }
      this.mv.visitJumpInsn(i, paramLabel);
      return;
    }
    if_jump(j, paramLabel);
  }
  
  public void pop()
  {
    this.mv.visitInsn(87);
  }
  
  public void pop2()
  {
    this.mv.visitInsn(88);
  }
  
  public void dup()
  {
    this.mv.visitInsn(89);
  }
  
  public void dup2()
  {
    this.mv.visitInsn(92);
  }
  
  public void dup_x1()
  {
    this.mv.visitInsn(90);
  }
  
  public void dup_x2()
  {
    this.mv.visitInsn(91);
  }
  
  public void dup2_x1()
  {
    this.mv.visitInsn(93);
  }
  
  public void dup2_x2()
  {
    this.mv.visitInsn(94);
  }
  
  public void swap()
  {
    this.mv.visitInsn(95);
  }
  
  public void aconst_null()
  {
    this.mv.visitInsn(1);
  }
  
  public void swap(.Type paramType1, .Type paramType2)
  {
    if (paramType2.getSize() == 1)
    {
      if (paramType1.getSize() == 1)
      {
        swap();
      }
      else
      {
        dup_x2();
        pop();
      }
    }
    else if (paramType1.getSize() == 1)
    {
      dup2_x1();
      pop2();
    }
    else
    {
      dup2_x2();
      pop2();
    }
  }
  
  public void monitorenter()
  {
    this.mv.visitInsn(194);
  }
  
  public void monitorexit()
  {
    this.mv.visitInsn(195);
  }
  
  public void math(int paramInt, .Type paramType)
  {
    this.mv.visitInsn(paramType.getOpcode(paramInt));
  }
  
  public void array_load(.Type paramType)
  {
    this.mv.visitInsn(paramType.getOpcode(46));
  }
  
  public void array_store(.Type paramType)
  {
    this.mv.visitInsn(paramType.getOpcode(79));
  }
  
  public void cast_numeric(.Type paramType1, .Type paramType2)
  {
    if (paramType1 != paramType2) {
      if (paramType1 == .Type.DOUBLE_TYPE)
      {
        if (paramType2 == .Type.FLOAT_TYPE)
        {
          this.mv.visitInsn(144);
        }
        else if (paramType2 == .Type.LONG_TYPE)
        {
          this.mv.visitInsn(143);
        }
        else
        {
          this.mv.visitInsn(142);
          cast_numeric(.Type.INT_TYPE, paramType2);
        }
      }
      else if (paramType1 == .Type.FLOAT_TYPE)
      {
        if (paramType2 == .Type.DOUBLE_TYPE)
        {
          this.mv.visitInsn(141);
        }
        else if (paramType2 == .Type.LONG_TYPE)
        {
          this.mv.visitInsn(140);
        }
        else
        {
          this.mv.visitInsn(139);
          cast_numeric(.Type.INT_TYPE, paramType2);
        }
      }
      else if (paramType1 == .Type.LONG_TYPE)
      {
        if (paramType2 == .Type.DOUBLE_TYPE)
        {
          this.mv.visitInsn(138);
        }
        else if (paramType2 == .Type.FLOAT_TYPE)
        {
          this.mv.visitInsn(137);
        }
        else
        {
          this.mv.visitInsn(136);
          cast_numeric(.Type.INT_TYPE, paramType2);
        }
      }
      else if (paramType2 == .Type.BYTE_TYPE) {
        this.mv.visitInsn(145);
      } else if (paramType2 == .Type.CHAR_TYPE) {
        this.mv.visitInsn(146);
      } else if (paramType2 == .Type.DOUBLE_TYPE) {
        this.mv.visitInsn(135);
      } else if (paramType2 == .Type.FLOAT_TYPE) {
        this.mv.visitInsn(134);
      } else if (paramType2 == .Type.LONG_TYPE) {
        this.mv.visitInsn(133);
      } else if (paramType2 == .Type.SHORT_TYPE) {
        this.mv.visitInsn(147);
      }
    }
  }
  
  public void push(int paramInt)
  {
    if (paramInt < -1) {
      this.mv.visitLdcInsn(new Integer(paramInt));
    } else if (paramInt <= 5) {
      this.mv.visitInsn(.TypeUtils.ICONST(paramInt));
    } else if (paramInt <= 127) {
      this.mv.visitIntInsn(16, paramInt);
    } else if (paramInt <= 32767) {
      this.mv.visitIntInsn(17, paramInt);
    } else {
      this.mv.visitLdcInsn(new Integer(paramInt));
    }
  }
  
  public void push(long paramLong)
  {
    if ((paramLong == 0L) || (paramLong == 1L)) {
      this.mv.visitInsn(.TypeUtils.LCONST(paramLong));
    } else {
      this.mv.visitLdcInsn(new Long(paramLong));
    }
  }
  
  public void push(float paramFloat)
  {
    if ((paramFloat == 0.0F) || (paramFloat == 1.0F) || (paramFloat == 2.0F)) {
      this.mv.visitInsn(.TypeUtils.FCONST(paramFloat));
    } else {
      this.mv.visitLdcInsn(new Float(paramFloat));
    }
  }
  
  public void push(double paramDouble)
  {
    if ((paramDouble == 0.0D) || (paramDouble == 1.0D)) {
      this.mv.visitInsn(.TypeUtils.DCONST(paramDouble));
    } else {
      this.mv.visitLdcInsn(new Double(paramDouble));
    }
  }
  
  public void push(String paramString)
  {
    this.mv.visitLdcInsn(paramString);
  }
  
  public void newarray()
  {
    newarray(.Constants.TYPE_OBJECT);
  }
  
  public void newarray(.Type paramType)
  {
    if (.TypeUtils.isPrimitive(paramType)) {
      this.mv.visitIntInsn(188, .TypeUtils.NEWARRAY(paramType));
    } else {
      emit_type(189, paramType);
    }
  }
  
  public void arraylength()
  {
    this.mv.visitInsn(190);
  }
  
  public void load_this()
  {
    if (.TypeUtils.isStatic(this.state.access)) {
      throw new IllegalStateException("no 'this' pointer within static method");
    }
    this.mv.visitVarInsn(25, 0);
  }
  
  public void load_args()
  {
    load_args(0, this.state.argumentTypes.length);
  }
  
  public void load_arg(int paramInt)
  {
    load_local(this.state.argumentTypes[paramInt], this.state.localOffset + skipArgs(paramInt));
  }
  
  public void load_args(int paramInt1, int paramInt2)
  {
    int i = this.state.localOffset + skipArgs(paramInt1);
    for (int j = 0; j < paramInt2; j++)
    {
      .Type localType = this.state.argumentTypes[(paramInt1 + j)];
      load_local(localType, i);
      i += localType.getSize();
    }
  }
  
  private int skipArgs(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++) {
      i += this.state.argumentTypes[j].getSize();
    }
    return i;
  }
  
  private void load_local(.Type paramType, int paramInt)
  {
    this.mv.visitVarInsn(paramType.getOpcode(21), paramInt);
  }
  
  private void store_local(.Type paramType, int paramInt)
  {
    this.mv.visitVarInsn(paramType.getOpcode(54), paramInt);
  }
  
  public void iinc(.Local paramLocal, int paramInt)
  {
    this.mv.visitIincInsn(paramLocal.getIndex(), paramInt);
  }
  
  public void store_local(.Local paramLocal)
  {
    store_local(paramLocal.getType(), paramLocal.getIndex());
  }
  
  public void load_local(.Local paramLocal)
  {
    load_local(paramLocal.getType(), paramLocal.getIndex());
  }
  
  public void return_value()
  {
    this.mv.visitInsn(this.state.sig.getReturnType().getOpcode(172));
  }
  
  public void getfield(String paramString)
  {
    .ClassEmitter.FieldInfo localFieldInfo = this.ce.getFieldInfo(paramString);
    int i = .TypeUtils.isStatic(localFieldInfo.access) ? 178 : 180;
    emit_field(i, this.ce.getClassType(), paramString, localFieldInfo.type);
  }
  
  public void putfield(String paramString)
  {
    .ClassEmitter.FieldInfo localFieldInfo = this.ce.getFieldInfo(paramString);
    int i = .TypeUtils.isStatic(localFieldInfo.access) ? 179 : 181;
    emit_field(i, this.ce.getClassType(), paramString, localFieldInfo.type);
  }
  
  public void super_getfield(String paramString, .Type paramType)
  {
    emit_field(180, this.ce.getSuperType(), paramString, paramType);
  }
  
  public void super_putfield(String paramString, .Type paramType)
  {
    emit_field(181, this.ce.getSuperType(), paramString, paramType);
  }
  
  public void super_getstatic(String paramString, .Type paramType)
  {
    emit_field(178, this.ce.getSuperType(), paramString, paramType);
  }
  
  public void super_putstatic(String paramString, .Type paramType)
  {
    emit_field(179, this.ce.getSuperType(), paramString, paramType);
  }
  
  public void getfield(.Type paramType1, String paramString, .Type paramType2)
  {
    emit_field(180, paramType1, paramString, paramType2);
  }
  
  public void putfield(.Type paramType1, String paramString, .Type paramType2)
  {
    emit_field(181, paramType1, paramString, paramType2);
  }
  
  public void getstatic(.Type paramType1, String paramString, .Type paramType2)
  {
    emit_field(178, paramType1, paramString, paramType2);
  }
  
  public void putstatic(.Type paramType1, String paramString, .Type paramType2)
  {
    emit_field(179, paramType1, paramString, paramType2);
  }
  
  void emit_field(int paramInt, .Type paramType1, String paramString, .Type paramType2)
  {
    this.mv.visitFieldInsn(paramInt, paramType1.getInternalName(), paramString, paramType2.getDescriptor());
  }
  
  public void super_invoke()
  {
    super_invoke(this.state.sig);
  }
  
  public void super_invoke(.Signature paramSignature)
  {
    emit_invoke(183, this.ce.getSuperType(), paramSignature);
  }
  
  public void invoke_constructor(.Type paramType)
  {
    invoke_constructor(paramType, CSTRUCT_NULL);
  }
  
  public void super_invoke_constructor()
  {
    invoke_constructor(this.ce.getSuperType());
  }
  
  public void invoke_constructor_this()
  {
    invoke_constructor(this.ce.getClassType());
  }
  
  private void emit_invoke(int paramInt, .Type paramType, .Signature paramSignature)
  {
    if ((paramSignature.getName().equals("<init>")) && (paramInt != 182) && (paramInt == 184)) {}
    this.mv.visitMethodInsn(paramInt, paramType.getInternalName(), paramSignature.getName(), paramSignature.getDescriptor());
  }
  
  public void invoke_interface(.Type paramType, .Signature paramSignature)
  {
    emit_invoke(185, paramType, paramSignature);
  }
  
  public void invoke_virtual(.Type paramType, .Signature paramSignature)
  {
    emit_invoke(182, paramType, paramSignature);
  }
  
  public void invoke_static(.Type paramType, .Signature paramSignature)
  {
    emit_invoke(184, paramType, paramSignature);
  }
  
  public void invoke_virtual_this(.Signature paramSignature)
  {
    invoke_virtual(this.ce.getClassType(), paramSignature);
  }
  
  public void invoke_static_this(.Signature paramSignature)
  {
    invoke_static(this.ce.getClassType(), paramSignature);
  }
  
  public void invoke_constructor(.Type paramType, .Signature paramSignature)
  {
    emit_invoke(183, paramType, paramSignature);
  }
  
  public void invoke_constructor_this(.Signature paramSignature)
  {
    invoke_constructor(this.ce.getClassType(), paramSignature);
  }
  
  public void super_invoke_constructor(.Signature paramSignature)
  {
    invoke_constructor(this.ce.getSuperType(), paramSignature);
  }
  
  public void new_instance_this()
  {
    new_instance(this.ce.getClassType());
  }
  
  public void new_instance(.Type paramType)
  {
    emit_type(187, paramType);
  }
  
  private void emit_type(int paramInt, .Type paramType)
  {
    String str;
    if (.TypeUtils.isArray(paramType)) {
      str = paramType.getDescriptor();
    } else {
      str = paramType.getInternalName();
    }
    this.mv.visitTypeInsn(paramInt, str);
  }
  
  public void aaload(int paramInt)
  {
    push(paramInt);
    aaload();
  }
  
  public void aaload()
  {
    this.mv.visitInsn(50);
  }
  
  public void aastore()
  {
    this.mv.visitInsn(83);
  }
  
  public void athrow()
  {
    this.mv.visitInsn(191);
  }
  
  public .Label make_label()
  {
    return new .Label();
  }
  
  public .Local make_local()
  {
    return make_local(.Constants.TYPE_OBJECT);
  }
  
  public .Local make_local(.Type paramType)
  {
    return new .Local(newLocal(paramType.getSize()), paramType);
  }
  
  public void checkcast_this()
  {
    checkcast(this.ce.getClassType());
  }
  
  public void checkcast(.Type paramType)
  {
    if (!paramType.equals(.Constants.TYPE_OBJECT)) {
      emit_type(192, paramType);
    }
  }
  
  public void instance_of(.Type paramType)
  {
    emit_type(193, paramType);
  }
  
  public void instance_of_this()
  {
    instance_of(this.ce.getClassType());
  }
  
  public void process_switch(int[] paramArrayOfInt, .ProcessSwitchCallback paramProcessSwitchCallback)
  {
    float f;
    if (paramArrayOfInt.length == 0) {
      f = 0.0F;
    } else {
      f = paramArrayOfInt.length / (paramArrayOfInt[(paramArrayOfInt.length - 1)] - paramArrayOfInt[0] + 1);
    }
    process_switch(paramArrayOfInt, paramProcessSwitchCallback, f >= 0.5F);
  }
  
  public void process_switch(int[] paramArrayOfInt, .ProcessSwitchCallback paramProcessSwitchCallback, boolean paramBoolean)
  {
    if (!isSorted(paramArrayOfInt)) {
      throw new IllegalArgumentException("keys to switch must be sorted ascending");
    }
    .Label localLabel1 = make_label();
    .Label localLabel2 = make_label();
    try
    {
      if (paramArrayOfInt.length > 0)
      {
        int i = paramArrayOfInt.length;
        int j = paramArrayOfInt[0];
        int k = paramArrayOfInt[(i - 1)];
        int m = k - j + 1;
        .Label[] arrayOfLabel;
        int n;
        if (paramBoolean)
        {
          arrayOfLabel = new .Label[m];
          Arrays.fill(arrayOfLabel, localLabel1);
          for (n = 0; n < i; n++) {
            arrayOfLabel[(paramArrayOfInt[n] - j)] = make_label();
          }
          this.mv.visitTableSwitchInsn(j, k, localLabel1, arrayOfLabel);
          for (n = 0; n < m; n++)
          {
            .Label localLabel3 = arrayOfLabel[n];
            if (localLabel3 != localLabel1)
            {
              mark(localLabel3);
              paramProcessSwitchCallback.processCase(n + j, localLabel2);
            }
          }
        }
        else
        {
          arrayOfLabel = new .Label[i];
          for (n = 0; n < i; n++) {
            arrayOfLabel[n] = make_label();
          }
          this.mv.visitLookupSwitchInsn(localLabel1, paramArrayOfInt, arrayOfLabel);
          for (n = 0; n < i; n++)
          {
            mark(arrayOfLabel[n]);
            paramProcessSwitchCallback.processCase(paramArrayOfInt[n], localLabel2);
          }
        }
      }
      mark(localLabel1);
      paramProcessSwitchCallback.processDefault();
      mark(localLabel2);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Error localError)
    {
      throw localError;
    }
    catch (Exception localException)
    {
      throw new .CodeGenerationException(localException);
    }
  }
  
  private static boolean isSorted(int[] paramArrayOfInt)
  {
    for (int i = 1; i < paramArrayOfInt.length; i++) {
      if (paramArrayOfInt[i] < paramArrayOfInt[(i - 1)]) {
        return false;
      }
    }
    return true;
  }
  
  public void mark(.Label paramLabel)
  {
    this.mv.visitLabel(paramLabel);
  }
  
  .Label mark()
  {
    .Label localLabel = make_label();
    this.mv.visitLabel(localLabel);
    return localLabel;
  }
  
  public void push(boolean paramBoolean)
  {
    push(paramBoolean ? 1 : 0);
  }
  
  public void not()
  {
    push(1);
    math(130, .Type.INT_TYPE);
  }
  
  public void throw_exception(.Type paramType, String paramString)
  {
    new_instance(paramType);
    dup();
    push(paramString);
    invoke_constructor(paramType, CSTRUCT_STRING);
    athrow();
  }
  
  public void box(.Type paramType)
  {
    if (.TypeUtils.isPrimitive(paramType)) {
      if (paramType == .Type.VOID_TYPE)
      {
        aconst_null();
      }
      else
      {
        .Type localType = .TypeUtils.getBoxedType(paramType);
        new_instance(localType);
        if (paramType.getSize() == 2)
        {
          dup_x2();
          dup_x2();
          pop();
        }
        else
        {
          dup_x1();
          swap();
        }
        invoke_constructor(localType, new .Signature("<init>", .Type.VOID_TYPE, new .Type[] { paramType }));
      }
    }
  }
  
  public void unbox(.Type paramType)
  {
    .Type localType = .Constants.TYPE_NUMBER;
    .Signature localSignature = null;
    switch (paramType.getSort())
    {
    case 0: 
      return;
    case 2: 
      localType = .Constants.TYPE_CHARACTER;
      localSignature = CHAR_VALUE;
      break;
    case 1: 
      localType = .Constants.TYPE_BOOLEAN;
      localSignature = BOOLEAN_VALUE;
      break;
    case 8: 
      localSignature = DOUBLE_VALUE;
      break;
    case 6: 
      localSignature = FLOAT_VALUE;
      break;
    case 7: 
      localSignature = LONG_VALUE;
      break;
    case 3: 
    case 4: 
    case 5: 
      localSignature = INT_VALUE;
    }
    if (localSignature == null)
    {
      checkcast(paramType);
    }
    else
    {
      checkcast(localType);
      invoke_virtual(localType, localSignature);
    }
  }
  
  public void create_arg_array()
  {
    push(this.state.argumentTypes.length);
    newarray();
    for (int i = 0; i < this.state.argumentTypes.length; i++)
    {
      dup();
      push(i);
      load_arg(i);
      box(this.state.argumentTypes[i]);
      aastore();
    }
  }
  
  public void zero_or_null(.Type paramType)
  {
    if (.TypeUtils.isPrimitive(paramType)) {
      switch (paramType.getSort())
      {
      case 8: 
        push(0.0D);
        break;
      case 7: 
        push(0L);
        break;
      case 6: 
        push(0.0F);
        break;
      case 0: 
        aconst_null();
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      default: 
        push(0);
        break;
      }
    } else {
      aconst_null();
    }
  }
  
  public void unbox_or_zero(.Type paramType)
  {
    if (.TypeUtils.isPrimitive(paramType))
    {
      if (paramType != .Type.VOID_TYPE)
      {
        .Label localLabel1 = make_label();
        .Label localLabel2 = make_label();
        dup();
        ifnonnull(localLabel1);
        pop();
        zero_or_null(paramType);
        goTo(localLabel2);
        mark(localLabel1);
        unbox(paramType);
        mark(localLabel2);
      }
    }
    else {
      checkcast(paramType);
    }
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    if (!.TypeUtils.isAbstract(this.state.access)) {
      this.mv.visitMaxs(0, 0);
    }
  }
  
  public void invoke(.MethodInfo paramMethodInfo, .Type paramType)
  {
    .ClassInfo localClassInfo = paramMethodInfo.getClassInfo();
    .Type localType = localClassInfo.getType();
    .Signature localSignature = paramMethodInfo.getSignature();
    if (localSignature.getName().equals("<init>")) {
      invoke_constructor(localType, localSignature);
    } else if (.TypeUtils.isInterface(localClassInfo.getModifiers())) {
      invoke_interface(localType, localSignature);
    } else if (.TypeUtils.isStatic(paramMethodInfo.getModifiers())) {
      invoke_static(localType, localSignature);
    } else {
      invoke_virtual(paramType, localSignature);
    }
  }
  
  public void invoke(.MethodInfo paramMethodInfo)
  {
    invoke(paramMethodInfo, paramMethodInfo.getClassInfo().getType());
  }
  
  private static class State
    extends .MethodInfo
  {
    .ClassInfo classInfo;
    int access;
    .Signature sig;
    .Type[] argumentTypes;
    int localOffset;
    .Type[] exceptionTypes;
    
    State(.ClassInfo paramClassInfo, int paramInt, .Signature paramSignature, .Type[] paramArrayOfType)
    {
      this.classInfo = paramClassInfo;
      this.access = paramInt;
      this.sig = paramSignature;
      this.exceptionTypes = paramArrayOfType;
      this.localOffset = (.TypeUtils.isStatic(paramInt) ? 0 : 1);
      this.argumentTypes = paramSignature.getArgumentTypes();
    }
    
    public .ClassInfo getClassInfo()
    {
      return this.classInfo;
    }
    
    public int getModifiers()
    {
      return this.access;
    }
    
    public .Signature getSignature()
    {
      return this.sig;
    }
    
    public .Type[] getExceptionTypes()
    {
      return this.exceptionTypes;
    }
    
    public .Attribute getAttribute()
    {
      return null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$CodeEmitter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */