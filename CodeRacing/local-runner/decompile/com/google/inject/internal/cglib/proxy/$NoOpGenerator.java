package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..EmitUtils;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..TypeUtils;
import java.util.Iterator;
import java.util.List;

class $NoOpGenerator
  implements .CallbackGenerator
{
  public static final NoOpGenerator INSTANCE = new NoOpGenerator();
  
  public void generate(.ClassEmitter paramClassEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      if ((.TypeUtils.isBridge(localMethodInfo.getModifiers())) || ((.TypeUtils.isProtected(paramContext.getOriginalModifiers(localMethodInfo))) && (.TypeUtils.isPublic(localMethodInfo.getModifiers()))))
      {
        .CodeEmitter localCodeEmitter = .EmitUtils.begin_method(paramClassEmitter, localMethodInfo);
        localCodeEmitter.load_this();
        localCodeEmitter.load_args();
        paramContext.emitInvoke(localCodeEmitter, localMethodInfo);
        localCodeEmitter.return_value();
        localCodeEmitter.end_method();
      }
    }
  }
  
  public void generateStatic(.CodeEmitter paramCodeEmitter, .CallbackGenerator.Context paramContext, List paramList) {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$NoOpGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */