package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..TypeUtils;
import java.util.Iterator;
import java.util.List;

class $FixedValueGenerator
  implements .CallbackGenerator
{
  public static final FixedValueGenerator INSTANCE = new FixedValueGenerator();
  private static final .Type FIXED_VALUE = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$FixedValue");
  private static final .Signature LOAD_OBJECT = .TypeUtils.parseSignature("Object loadObject()");
  
  public void generate(.ClassEmitter paramClassEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      .CodeEmitter localCodeEmitter = paramContext.beginMethod(paramClassEmitter, localMethodInfo);
      paramContext.emitCallback(localCodeEmitter, paramContext.getIndex(localMethodInfo));
      localCodeEmitter.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
      localCodeEmitter.unbox_or_zero(localCodeEmitter.getReturnType());
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
    }
  }
  
  public void generateStatic(.CodeEmitter paramCodeEmitter, .CallbackGenerator.Context paramContext, List paramList) {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$FixedValueGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */