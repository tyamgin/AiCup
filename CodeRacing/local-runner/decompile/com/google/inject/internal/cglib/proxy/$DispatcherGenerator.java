package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..ClassInfo;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..TypeUtils;
import java.util.Iterator;
import java.util.List;

class $DispatcherGenerator
  implements .CallbackGenerator
{
  public static final DispatcherGenerator INSTANCE = new DispatcherGenerator(false);
  public static final DispatcherGenerator PROXY_REF_INSTANCE = new DispatcherGenerator(true);
  private static final .Type DISPATCHER = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$Dispatcher");
  private static final .Type PROXY_REF_DISPATCHER = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$ProxyRefDispatcher");
  private static final .Signature LOAD_OBJECT = .TypeUtils.parseSignature("Object loadObject()");
  private static final .Signature PROXY_REF_LOAD_OBJECT = .TypeUtils.parseSignature("Object loadObject(Object)");
  private boolean proxyRef;
  
  private $DispatcherGenerator(boolean paramBoolean)
  {
    this.proxyRef = paramBoolean;
  }
  
  public void generate(.ClassEmitter paramClassEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      if (!.TypeUtils.isProtected(localMethodInfo.getModifiers()))
      {
        .CodeEmitter localCodeEmitter = paramContext.beginMethod(paramClassEmitter, localMethodInfo);
        paramContext.emitCallback(localCodeEmitter, paramContext.getIndex(localMethodInfo));
        if (this.proxyRef)
        {
          localCodeEmitter.load_this();
          localCodeEmitter.invoke_interface(PROXY_REF_DISPATCHER, PROXY_REF_LOAD_OBJECT);
        }
        else
        {
          localCodeEmitter.invoke_interface(DISPATCHER, LOAD_OBJECT);
        }
        localCodeEmitter.checkcast(localMethodInfo.getClassInfo().getType());
        localCodeEmitter.load_args();
        localCodeEmitter.invoke(localMethodInfo);
        localCodeEmitter.return_value();
        localCodeEmitter.end_method();
      }
    }
  }
  
  public void generateStatic(.CodeEmitter paramCodeEmitter, .CallbackGenerator.Context paramContext, List paramList) {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$DispatcherGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */