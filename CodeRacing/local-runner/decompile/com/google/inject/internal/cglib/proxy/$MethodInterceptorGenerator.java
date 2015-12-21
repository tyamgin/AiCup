package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..ClassInfo;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..CollectionUtils;
import com.google.inject.internal.cglib.core..Constants;
import com.google.inject.internal.cglib.core..EmitUtils;
import com.google.inject.internal.cglib.core..Local;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..ObjectSwitchCallback;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..Transformer;
import com.google.inject.internal.cglib.core..TypeUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class $MethodInterceptorGenerator
  implements .CallbackGenerator
{
  public static final MethodInterceptorGenerator INSTANCE = new MethodInterceptorGenerator();
  static final String EMPTY_ARGS_NAME = "CGLIB$emptyArgs";
  static final String FIND_PROXY_NAME = "CGLIB$findMethodProxy";
  static final Class[] FIND_PROXY_TYPES = { .Signature.class };
  private static final .Type ABSTRACT_METHOD_ERROR = .TypeUtils.parseType("AbstractMethodError");
  private static final .Type METHOD = .TypeUtils.parseType("java.lang.reflect.Method");
  private static final .Type REFLECT_UTILS = .TypeUtils.parseType("com.google.inject.internal.cglib.core.$ReflectUtils");
  private static final .Type METHOD_PROXY = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$MethodProxy");
  private static final .Type METHOD_INTERCEPTOR = .TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$MethodInterceptor");
  private static final .Signature GET_DECLARED_METHODS = .TypeUtils.parseSignature("java.lang.reflect.Method[] getDeclaredMethods()");
  private static final .Signature GET_DECLARING_CLASS = .TypeUtils.parseSignature("Class getDeclaringClass()");
  private static final .Signature FIND_METHODS = .TypeUtils.parseSignature("java.lang.reflect.Method[] findMethods(String[], java.lang.reflect.Method[])");
  private static final .Signature MAKE_PROXY = new .Signature("create", METHOD_PROXY, new .Type[] { .Constants.TYPE_CLASS, .Constants.TYPE_CLASS, .Constants.TYPE_STRING, .Constants.TYPE_STRING, .Constants.TYPE_STRING });
  private static final .Signature INTERCEPT = new .Signature("intercept", .Constants.TYPE_OBJECT, new .Type[] { .Constants.TYPE_OBJECT, METHOD, .Constants.TYPE_OBJECT_ARRAY, METHOD_PROXY });
  private static final .Signature FIND_PROXY = new .Signature("CGLIB$findMethodProxy", METHOD_PROXY, new .Type[] { .Constants.TYPE_SIGNATURE });
  private static final .Signature TO_STRING = .TypeUtils.parseSignature("String toString()");
  private static final .Transformer METHOD_TO_CLASS = new .Transformer()
  {
    public Object transform(Object paramAnonymousObject)
    {
      return ((.MethodInfo)paramAnonymousObject).getClassInfo();
    }
  };
  private static final .Signature CSTRUCT_SIGNATURE = .TypeUtils.parseConstructor("String, String");
  
  private String getMethodField(.Signature paramSignature)
  {
    return paramSignature.getName() + "$Method";
  }
  
  private String getMethodProxyField(.Signature paramSignature)
  {
    return paramSignature.getName() + "$Proxy";
  }
  
  public void generate(.ClassEmitter paramClassEmitter, .CallbackGenerator.Context paramContext, List paramList)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      .MethodInfo localMethodInfo = (.MethodInfo)localIterator.next();
      .Signature localSignature1 = localMethodInfo.getSignature();
      .Signature localSignature2 = paramContext.getImplSignature(localMethodInfo);
      String str1 = getMethodField(localSignature2);
      String str2 = getMethodProxyField(localSignature2);
      localHashMap.put(localSignature1.toString(), str2);
      paramClassEmitter.declare_field(26, str1, METHOD, null);
      paramClassEmitter.declare_field(26, str2, METHOD_PROXY, null);
      paramClassEmitter.declare_field(26, "CGLIB$emptyArgs", .Constants.TYPE_OBJECT_ARRAY, null);
      .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(16, localSignature2, localMethodInfo.getExceptionTypes());
      superHelper(localCodeEmitter, localMethodInfo, paramContext);
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
      localCodeEmitter = paramContext.beginMethod(paramClassEmitter, localMethodInfo);
      .Label localLabel = localCodeEmitter.make_label();
      paramContext.emitCallback(localCodeEmitter, paramContext.getIndex(localMethodInfo));
      localCodeEmitter.dup();
      localCodeEmitter.ifnull(localLabel);
      localCodeEmitter.load_this();
      localCodeEmitter.getfield(str1);
      if (localSignature1.getArgumentTypes().length == 0) {
        localCodeEmitter.getfield("CGLIB$emptyArgs");
      } else {
        localCodeEmitter.create_arg_array();
      }
      localCodeEmitter.getfield(str2);
      localCodeEmitter.invoke_interface(METHOD_INTERCEPTOR, INTERCEPT);
      localCodeEmitter.unbox_or_zero(localSignature1.getReturnType());
      localCodeEmitter.return_value();
      localCodeEmitter.mark(localLabel);
      superHelper(localCodeEmitter, localMethodInfo, paramContext);
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
    }
    generateFindProxy(paramClassEmitter, localHashMap);
  }
  
  private static void superHelper(.CodeEmitter paramCodeEmitter, .MethodInfo paramMethodInfo, .CallbackGenerator.Context paramContext)
  {
    if (.TypeUtils.isAbstract(paramMethodInfo.getModifiers()))
    {
      paramCodeEmitter.throw_exception(ABSTRACT_METHOD_ERROR, paramMethodInfo.toString() + " is abstract");
    }
    else
    {
      paramCodeEmitter.load_this();
      paramCodeEmitter.load_args();
      paramContext.emitInvoke(paramCodeEmitter, paramMethodInfo);
    }
  }
  
  public void generateStatic(.CodeEmitter paramCodeEmitter, .CallbackGenerator.Context paramContext, List paramList)
    throws Exception
  {
    paramCodeEmitter.push(0);
    paramCodeEmitter.newarray();
    paramCodeEmitter.putfield("CGLIB$emptyArgs");
    .Local localLocal1 = paramCodeEmitter.make_local();
    .Local localLocal2 = paramCodeEmitter.make_local();
    .EmitUtils.load_class_this(paramCodeEmitter);
    paramCodeEmitter.store_local(localLocal1);
    Map localMap = .CollectionUtils.bucket(paramList, METHOD_TO_CLASS);
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      .ClassInfo localClassInfo = (.ClassInfo)localIterator.next();
      List localList = (List)localMap.get(localClassInfo);
      paramCodeEmitter.push(2 * localList.size());
      paramCodeEmitter.newarray(.Constants.TYPE_STRING);
      .MethodInfo localMethodInfo;
      .Signature localSignature1;
      for (int i = 0; i < localList.size(); i++)
      {
        localMethodInfo = (.MethodInfo)localList.get(i);
        localSignature1 = localMethodInfo.getSignature();
        paramCodeEmitter.dup();
        paramCodeEmitter.push(2 * i);
        paramCodeEmitter.push(localSignature1.getName());
        paramCodeEmitter.aastore();
        paramCodeEmitter.dup();
        paramCodeEmitter.push(2 * i + 1);
        paramCodeEmitter.push(localSignature1.getDescriptor());
        paramCodeEmitter.aastore();
      }
      .EmitUtils.load_class(paramCodeEmitter, localClassInfo.getType());
      paramCodeEmitter.dup();
      paramCodeEmitter.store_local(localLocal2);
      paramCodeEmitter.invoke_virtual(.Constants.TYPE_CLASS, GET_DECLARED_METHODS);
      paramCodeEmitter.invoke_static(REFLECT_UTILS, FIND_METHODS);
      for (i = 0; i < localList.size(); i++)
      {
        localMethodInfo = (.MethodInfo)localList.get(i);
        localSignature1 = localMethodInfo.getSignature();
        .Signature localSignature2 = paramContext.getImplSignature(localMethodInfo);
        paramCodeEmitter.dup();
        paramCodeEmitter.push(i);
        paramCodeEmitter.array_load(METHOD);
        paramCodeEmitter.putfield(getMethodField(localSignature2));
        paramCodeEmitter.load_local(localLocal2);
        paramCodeEmitter.load_local(localLocal1);
        paramCodeEmitter.push(localSignature1.getDescriptor());
        paramCodeEmitter.push(localSignature1.getName());
        paramCodeEmitter.push(localSignature2.getName());
        paramCodeEmitter.invoke_static(METHOD_PROXY, MAKE_PROXY);
        paramCodeEmitter.putfield(getMethodProxyField(localSignature2));
      }
      paramCodeEmitter.pop();
    }
  }
  
  public void generateFindProxy(.ClassEmitter paramClassEmitter, Map paramMap)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(9, FIND_PROXY, null);
    localCodeEmitter.load_arg(0);
    localCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, TO_STRING);
    .ObjectSwitchCallback local2 = new .ObjectSwitchCallback()
    {
      private final .CodeEmitter val$e;
      private final Map val$sigMap;
      
      public void processCase(Object paramAnonymousObject, .Label paramAnonymousLabel)
      {
        this.val$e.getfield((String)this.val$sigMap.get(paramAnonymousObject));
        this.val$e.return_value();
      }
      
      public void processDefault()
      {
        this.val$e.aconst_null();
        this.val$e.return_value();
      }
    };
    .EmitUtils.string_switch(localCodeEmitter, (String[])paramMap.keySet().toArray(new String[0]), 1, local2);
    localCodeEmitter.end_method();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$MethodInterceptorGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */