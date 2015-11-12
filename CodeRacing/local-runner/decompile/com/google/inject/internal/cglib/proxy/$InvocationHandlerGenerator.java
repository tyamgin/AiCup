package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..Block;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..EmitUtils;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..TypeUtils;
import java.util.Iterator;
import java.util.List;

class $InvocationHandlerGenerator
  implements .CallbackGenerator
{
  public static final InvocationHandlerGenerator INSTANCE = new InvocationHandlerGenerator();
  private static final .Type INVOCATION_HANDLER = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$InvocationHandler");
  private static final .Type UNDECLARED_THROWABLE_EXCEPTION = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$UndeclaredThrowableException");
  private static final .Type METHOD = .TypeUtils.parseType("java.lang.reflect.Method");
  private static final .Signature INVOKE = .TypeUtils.parseSignature("Object invoke(Object, java.lang.reflect.Method, Object[])");
  
  public void generate(.ClassEmitter paramClassEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      .Signature localSignature = paramContext.getImplSignature(localMethodInfo);
      paramClassEmitter.declare_field(26, localSignature.getName(), METHOD, null);
      .CodeEmitter localCodeEmitter = paramContext.beginMethod(paramClassEmitter, localMethodInfo);
      .Block localBlock = localCodeEmitter.begin_block();
      paramContext.emitCallback(localCodeEmitter, paramContext.getIndex(localMethodInfo));
      localCodeEmitter.load_this();
      localCodeEmitter.getfield(localSignature.getName());
      localCodeEmitter.create_arg_array();
      localCodeEmitter.invoke_interface(INVOCATION_HANDLER, INVOKE);
      localCodeEmitter.unbox(localMethodInfo.getSignature().getReturnType());
      localCodeEmitter.return_value();
      localBlock.end();
      .EmitUtils.wrap_undeclared_throwable(localCodeEmitter, localBlock, localMethodInfo.getExceptionTypes(), UNDECLARED_THROWABLE_EXCEPTION);
      localCodeEmitter.end_method();
    }
  }
  
  public void generateStatic(.CodeEmitter paramCodeEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      .EmitUtils.load_method(paramCodeEmitter, localMethodInfo);
      paramCodeEmitter.putfield(paramContext.getImplSignature(localMethodInfo).getName());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$InvocationHandlerGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */