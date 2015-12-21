package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..ClassInfo;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..Constants;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..TypeUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class $LazyLoaderGenerator
  implements .CallbackGenerator
{
  public static final LazyLoaderGenerator INSTANCE = new LazyLoaderGenerator();
  private static final .Signature LOAD_OBJECT = .TypeUtils.parseSignature("Object loadObject()");
  private static final .Type LAZY_LOADER = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$LazyLoader");
  
  public void generate(.ClassEmitter paramClassEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramList.iterator();
    .CodeEmitter localCodeEmitter;
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      if (!.TypeUtils.isProtected(localMethodInfo.getModifiers()))
      {
        int j = paramContext.getIndex(localMethodInfo);
        localHashSet.add(new Integer(j));
        localCodeEmitter = paramContext.beginMethod(paramClassEmitter, localMethodInfo);
        localCodeEmitter.load_this();
        localCodeEmitter.dup();
        localCodeEmitter.invoke_virtual_this(loadMethod(j));
        localCodeEmitter.checkcast(localMethodInfo.getClassInfo().getType());
        localCodeEmitter.load_args();
        localCodeEmitter.invoke(localMethodInfo);
        localCodeEmitter.return_value();
        localCodeEmitter.end_method();
      }
    }
    localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      int i = ((Integer)localIterator.next()).intValue();
      String str = "CGLIB$LAZY_LOADER_" + i;
      paramClassEmitter.declare_field(2, str, .Constants.TYPE_OBJECT, null);
      localCodeEmitter = paramClassEmitter.begin_method(50, loadMethod(i), null);
      localCodeEmitter.load_this();
      localCodeEmitter.getfield(str);
      localCodeEmitter.dup();
      .Label localLabel = localCodeEmitter.make_label();
      localCodeEmitter.ifnonnull(localLabel);
      localCodeEmitter.pop();
      localCodeEmitter.load_this();
      paramContext.emitCallback(localCodeEmitter, i);
      localCodeEmitter.invoke_interface(LAZY_LOADER, LOAD_OBJECT);
      localCodeEmitter.dup_x1();
      localCodeEmitter.putfield(str);
      localCodeEmitter.mark(localLabel);
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
    }
  }
  
  private .Signature loadMethod(int paramInt)
  {
    return new .Signature("CGLIB$LOAD_PRIVATE_" + paramInt, .Constants.TYPE_OBJECT, .Constants.TYPES_EMPTY);
  }
  
  public void generateStatic(.CodeEmitter paramCodeEmitter, .CallbackGenerator.Context paramContext, List paramList) {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$LazyLoaderGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */