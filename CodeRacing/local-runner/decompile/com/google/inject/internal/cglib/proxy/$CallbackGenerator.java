package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..Signature;
import java.util.List;

abstract interface $CallbackGenerator
{
  public abstract void generate(.ClassEmitter paramClassEmitter, Context paramContext, List paramList)
    throws Exception;
  
  public abstract void generateStatic(.CodeEmitter paramCodeEmitter, Context paramContext, List paramList)
    throws Exception;
  
  public static abstract interface Context
  {
    public abstract ClassLoader getClassLoader();
    
    public abstract .CodeEmitter beginMethod(.ClassEmitter paramClassEmitter, .MethodInfo paramMethodInfo);
    
    public abstract int getOriginalModifiers(.MethodInfo paramMethodInfo);
    
    public abstract int getIndex(.MethodInfo paramMethodInfo);
    
    public abstract void emitCallback(.CodeEmitter paramCodeEmitter, int paramInt);
    
    public abstract .Signature getImplSignature(.MethodInfo paramMethodInfo);
    
    public abstract void emitInvoke(.CodeEmitter paramCodeEmitter, .MethodInfo paramMethodInfo);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$CallbackGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */