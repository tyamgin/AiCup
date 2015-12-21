package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..FieldVisitor;
import com.google.inject.internal.asm..MethodVisitor;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.transform..ClassTransformer;
import java.util.HashMap;
import java.util.Map;

public class $ClassEmitter
  extends .ClassTransformer
{
  private .ClassInfo classInfo;
  private Map fieldInfo;
  private static int hookCounter;
  private .MethodVisitor rawStaticInit;
  private .CodeEmitter staticInit;
  private .CodeEmitter staticHook;
  private .Signature staticHookSig;
  
  public $ClassEmitter(.ClassVisitor paramClassVisitor)
  {
    setTarget(paramClassVisitor);
  }
  
  public $ClassEmitter()
  {
    super(262144);
  }
  
  public void setTarget(.ClassVisitor paramClassVisitor)
  {
    this.cv = paramClassVisitor;
    this.fieldInfo = new HashMap();
    this.staticInit = (this.staticHook = null);
    this.staticHookSig = null;
  }
  
  private static synchronized int getNextHook()
  {
    return ++hookCounter;
  }
  
  public .ClassInfo getClassInfo()
  {
    return this.classInfo;
  }
  
  public void begin_class(int paramInt1, int paramInt2, String paramString1, .Type paramType, .Type[] paramArrayOfType, String paramString2)
  {
    .Type localType = .Type.getType("L" + paramString1.replace('.', '/') + ";");
    this.classInfo = new .ClassInfo()
    {
      private final .Type val$classType;
      private final .Type val$superType;
      private final .Type[] val$interfaces;
      private final int val$access;
      
      public .Type getType()
      {
        return this.val$classType;
      }
      
      public .Type getSuperType()
      {
        return this.val$superType != null ? this.val$superType : .Constants.TYPE_OBJECT;
      }
      
      public .Type[] getInterfaces()
      {
        return this.val$interfaces;
      }
      
      public int getModifiers()
      {
        return this.val$access;
      }
    };
    this.cv.visit(paramInt1, paramInt2, this.classInfo.getType().getInternalName(), null, this.classInfo.getSuperType().getInternalName(), .TypeUtils.toInternalNames(paramArrayOfType));
    if (paramString2 != null) {
      this.cv.visitSource(paramString2, null);
    }
    init();
  }
  
  public .CodeEmitter getStaticHook()
  {
    if (.TypeUtils.isInterface(getAccess())) {
      throw new IllegalStateException("static hook is invalid for this class");
    }
    if (this.staticHook == null)
    {
      this.staticHookSig = new .Signature("CGLIB$STATICHOOK" + getNextHook(), "()V");
      this.staticHook = begin_method(8, this.staticHookSig, null);
      if (this.staticInit != null) {
        this.staticInit.invoke_static_this(this.staticHookSig);
      }
    }
    return this.staticHook;
  }
  
  protected void init() {}
  
  public int getAccess()
  {
    return this.classInfo.getModifiers();
  }
  
  public .Type getClassType()
  {
    return this.classInfo.getType();
  }
  
  public .Type getSuperType()
  {
    return this.classInfo.getSuperType();
  }
  
  public void end_class()
  {
    if ((this.staticHook != null) && (this.staticInit == null)) {
      begin_static();
    }
    if (this.staticInit != null)
    {
      this.staticHook.return_value();
      this.staticHook.end_method();
      this.rawStaticInit.visitInsn(177);
      this.rawStaticInit.visitMaxs(0, 0);
      this.staticInit = (this.staticHook = null);
      this.staticHookSig = null;
    }
    this.cv.visitEnd();
  }
  
  public .CodeEmitter begin_method(int paramInt, .Signature paramSignature, .Type[] paramArrayOfType)
  {
    if (this.classInfo == null) {
      throw new IllegalStateException("classInfo is null! " + this);
    }
    .MethodVisitor localMethodVisitor = this.cv.visitMethod(paramInt, paramSignature.getName(), paramSignature.getDescriptor(), null, .TypeUtils.toInternalNames(paramArrayOfType));
    if ((paramSignature.equals(.Constants.SIG_STATIC)) && (!.TypeUtils.isInterface(getAccess())))
    {
      this.rawStaticInit = localMethodVisitor;
      .MethodVisitor local2 = new .MethodVisitor(262144, localMethodVisitor)
      {
        public void visitMaxs(int paramAnonymousInt1, int paramAnonymousInt2) {}
        
        public void visitInsn(int paramAnonymousInt)
        {
          if (paramAnonymousInt != 177) {
            super.visitInsn(paramAnonymousInt);
          }
        }
      };
      this.staticInit = new .CodeEmitter(this, local2, paramInt, paramSignature, paramArrayOfType);
      if (this.staticHook == null) {
        getStaticHook();
      } else {
        this.staticInit.invoke_static_this(this.staticHookSig);
      }
      return this.staticInit;
    }
    if (paramSignature.equals(this.staticHookSig)) {
      new .CodeEmitter(this, localMethodVisitor, paramInt, paramSignature, paramArrayOfType)
      {
        public boolean isStaticHook()
        {
          return true;
        }
      };
    }
    return new .CodeEmitter(this, localMethodVisitor, paramInt, paramSignature, paramArrayOfType);
  }
  
  public .CodeEmitter begin_static()
  {
    return begin_method(8, .Constants.SIG_STATIC, null);
  }
  
  public void declare_field(int paramInt, String paramString, .Type paramType, Object paramObject)
  {
    FieldInfo localFieldInfo1 = (FieldInfo)this.fieldInfo.get(paramString);
    FieldInfo localFieldInfo2 = new FieldInfo(paramInt, paramString, paramType, paramObject);
    if (localFieldInfo1 != null)
    {
      if (!localFieldInfo2.equals(localFieldInfo1)) {
        throw new IllegalArgumentException("Field \"" + paramString + "\" has been declared differently");
      }
    }
    else
    {
      this.fieldInfo.put(paramString, localFieldInfo2);
      this.cv.visitField(paramInt, paramString, paramType.getDescriptor(), null, paramObject);
    }
  }
  
  boolean isFieldDeclared(String paramString)
  {
    return this.fieldInfo.get(paramString) != null;
  }
  
  FieldInfo getFieldInfo(String paramString)
  {
    FieldInfo localFieldInfo = (FieldInfo)this.fieldInfo.get(paramString);
    if (localFieldInfo == null) {
      throw new IllegalArgumentException("Field " + paramString + " is not declared in " + getClassType().getClassName());
    }
    return localFieldInfo;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    begin_class(paramInt1, paramInt2, paramString1.replace('/', '.'), .TypeUtils.fromInternalName(paramString3), .TypeUtils.fromInternalNames(paramArrayOfString), null);
  }
  
  public void visitEnd()
  {
    end_class();
  }
  
  public .FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    declare_field(paramInt, paramString1, .Type.getType(paramString2), paramObject);
    return null;
  }
  
  public .MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    return begin_method(paramInt, new .Signature(paramString1, paramString2), .TypeUtils.fromInternalNames(paramArrayOfString));
  }
  
  static class FieldInfo
  {
    int access;
    String name;
    .Type type;
    Object value;
    
    public FieldInfo(int paramInt, String paramString, .Type paramType, Object paramObject)
    {
      this.access = paramInt;
      this.name = paramString;
      this.type = paramType;
      this.value = paramObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (!(paramObject instanceof FieldInfo)) {
        return false;
      }
      FieldInfo localFieldInfo = (FieldInfo)paramObject;
      if ((this.access != localFieldInfo.access) || (!this.name.equals(localFieldInfo.name)) || (!this.type.equals(localFieldInfo.type))) {
        return false;
      }
      if (((this.value == null ? 1 : 0) ^ (localFieldInfo.value == null ? 1 : 0)) != 0) {
        return false;
      }
      return (this.value == null) || (this.value.equals(localFieldInfo.value));
    }
    
    public int hashCode()
    {
      return this.access ^ this.name.hashCode() ^ this.type.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$ClassEmitter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */