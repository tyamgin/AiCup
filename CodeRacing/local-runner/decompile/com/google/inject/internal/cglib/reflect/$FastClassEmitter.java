package com.google.inject.internal.cglib.reflect;

import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..Block;
import com.google.inject.internal.cglib.core..ClassEmitter;
import com.google.inject.internal.cglib.core..CodeEmitter;
import com.google.inject.internal.cglib.core..CollectionUtils;
import com.google.inject.internal.cglib.core..Constants;
import com.google.inject.internal.cglib.core..DuplicatesPredicate;
import com.google.inject.internal.cglib.core..EmitUtils;
import com.google.inject.internal.cglib.core..MethodInfo;
import com.google.inject.internal.cglib.core..MethodInfoTransformer;
import com.google.inject.internal.cglib.core..ObjectSwitchCallback;
import com.google.inject.internal.cglib.core..ProcessSwitchCallback;
import com.google.inject.internal.cglib.core..ReflectUtils;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.core..Transformer;
import com.google.inject.internal.cglib.core..TypeUtils;
import com.google.inject.internal.cglib.core..VisibilityPredicate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class $FastClassEmitter
  extends .ClassEmitter
{
  private static final .Signature CSTRUCT_CLASS = .TypeUtils.parseConstructor("Class");
  private static final .Signature METHOD_GET_INDEX = .TypeUtils.parseSignature("int getIndex(String, Class[])");
  private static final .Signature SIGNATURE_GET_INDEX = new .Signature("getIndex", .Type.INT_TYPE, new .Type[] { .Constants.TYPE_SIGNATURE });
  private static final .Signature TO_STRING = .TypeUtils.parseSignature("String toString()");
  private static final .Signature CONSTRUCTOR_GET_INDEX = .TypeUtils.parseSignature("int getIndex(Class[])");
  private static final .Signature INVOKE = .TypeUtils.parseSignature("Object invoke(int, Object, Object[])");
  private static final .Signature NEW_INSTANCE = .TypeUtils.parseSignature("Object newInstance(int, Object[])");
  private static final .Signature GET_MAX_INDEX = .TypeUtils.parseSignature("int getMaxIndex()");
  private static final .Signature GET_SIGNATURE_WITHOUT_RETURN_TYPE = .TypeUtils.parseSignature("String getSignatureWithoutReturnType(String, Class[])");
  private static final .Type FAST_CLASS = .TypeUtils.parseType("com.google.inject.internal.cglib.reflect.$FastClass");
  private static final .Type ILLEGAL_ARGUMENT_EXCEPTION = .TypeUtils.parseType("IllegalArgumentException");
  private static final .Type INVOCATION_TARGET_EXCEPTION = .TypeUtils.parseType("java.lang.reflect.InvocationTargetException");
  private static final .Type[] INVOCATION_TARGET_EXCEPTION_ARRAY = { INVOCATION_TARGET_EXCEPTION };
  private static final int TOO_MANY_METHODS = 100;
  
  public $FastClassEmitter(.ClassVisitor paramClassVisitor, String paramString, Class paramClass)
  {
    super(paramClassVisitor);
    .Type localType = .Type.getType(paramClass);
    begin_class(46, 1, paramString, FAST_CLASS, null, "<generated>");
    .CodeEmitter localCodeEmitter = begin_method(1, CSTRUCT_CLASS, null);
    localCodeEmitter.load_this();
    localCodeEmitter.load_args();
    localCodeEmitter.super_invoke_constructor(CSTRUCT_CLASS);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
    .VisibilityPredicate localVisibilityPredicate = new .VisibilityPredicate(paramClass, false);
    List localList1 = .ReflectUtils.addAllMethods(paramClass, new ArrayList());
    .CollectionUtils.filter(localList1, localVisibilityPredicate);
    .CollectionUtils.filter(localList1, new .DuplicatesPredicate());
    ArrayList localArrayList = new ArrayList(Arrays.asList(paramClass.getDeclaredConstructors()));
    .CollectionUtils.filter(localArrayList, localVisibilityPredicate);
    emitIndexBySignature(localList1);
    emitIndexByClassArray(localList1);
    localCodeEmitter = begin_method(1, CONSTRUCTOR_GET_INDEX, null);
    localCodeEmitter.load_args();
    List localList2 = .CollectionUtils.transform(localArrayList, .MethodInfoTransformer.getInstance());
    .EmitUtils.constructor_switch(localCodeEmitter, localList2, new GetIndexCallback(localCodeEmitter, localList2));
    localCodeEmitter.end_method();
    localCodeEmitter = begin_method(1, INVOKE, INVOCATION_TARGET_EXCEPTION_ARRAY);
    localCodeEmitter.load_arg(1);
    localCodeEmitter.checkcast(localType);
    localCodeEmitter.load_arg(0);
    invokeSwitchHelper(localCodeEmitter, localList1, 2, localType);
    localCodeEmitter.end_method();
    localCodeEmitter = begin_method(1, NEW_INSTANCE, INVOCATION_TARGET_EXCEPTION_ARRAY);
    localCodeEmitter.new_instance(localType);
    localCodeEmitter.dup();
    localCodeEmitter.load_arg(0);
    invokeSwitchHelper(localCodeEmitter, localArrayList, 1, localType);
    localCodeEmitter.end_method();
    localCodeEmitter = begin_method(1, GET_MAX_INDEX, null);
    localCodeEmitter.push(localList1.size() - 1);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
    end_class();
  }
  
  private void emitIndexBySignature(List paramList)
  {
    .CodeEmitter localCodeEmitter = begin_method(1, SIGNATURE_GET_INDEX, null);
    List localList = .CollectionUtils.transform(paramList, new .Transformer()
    {
      public Object transform(Object paramAnonymousObject)
      {
        return .ReflectUtils.getSignature((Method)paramAnonymousObject).toString();
      }
    });
    localCodeEmitter.load_arg(0);
    localCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, TO_STRING);
    signatureSwitchHelper(localCodeEmitter, localList);
    localCodeEmitter.end_method();
  }
  
  private void emitIndexByClassArray(List paramList)
  {
    .CodeEmitter localCodeEmitter = begin_method(1, METHOD_GET_INDEX, null);
    List localList;
    if (paramList.size() > 100)
    {
      localList = .CollectionUtils.transform(paramList, new .Transformer()
      {
        public Object transform(Object paramAnonymousObject)
        {
          String str = .ReflectUtils.getSignature((Method)paramAnonymousObject).toString();
          return str.substring(0, str.lastIndexOf(')') + 1);
        }
      });
      localCodeEmitter.load_args();
      localCodeEmitter.invoke_static(FAST_CLASS, GET_SIGNATURE_WITHOUT_RETURN_TYPE);
      signatureSwitchHelper(localCodeEmitter, localList);
    }
    else
    {
      localCodeEmitter.load_args();
      localList = .CollectionUtils.transform(paramList, .MethodInfoTransformer.getInstance());
      .EmitUtils.method_switch(localCodeEmitter, localList, new GetIndexCallback(localCodeEmitter, localList));
    }
    localCodeEmitter.end_method();
  }
  
  private void signatureSwitchHelper(.CodeEmitter paramCodeEmitter, List paramList)
  {
    .ObjectSwitchCallback local3 = new .ObjectSwitchCallback()
    {
      private final .CodeEmitter val$e;
      private final List val$signatures;
      
      public void processCase(Object paramAnonymousObject, .Label paramAnonymousLabel)
      {
        this.val$e.push(this.val$signatures.indexOf(paramAnonymousObject));
        this.val$e.return_value();
      }
      
      public void processDefault()
      {
        this.val$e.push(-1);
        this.val$e.return_value();
      }
    };
    .EmitUtils.string_switch(paramCodeEmitter, (String[])paramList.toArray(new String[paramList.size()]), 1, local3);
  }
  
  private static void invokeSwitchHelper(.CodeEmitter paramCodeEmitter, List paramList, int paramInt, .Type paramType)
  {
    List localList = .CollectionUtils.transform(paramList, .MethodInfoTransformer.getInstance());
    .Label localLabel = paramCodeEmitter.make_label();
    .Block localBlock = paramCodeEmitter.begin_block();
    paramCodeEmitter.process_switch(getIntRange(localList.size()), new .ProcessSwitchCallback()
    {
      private final List val$info;
      private final .CodeEmitter val$e;
      private final int val$arg;
      private final .Type val$base;
      private final .Label val$illegalArg;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
      {
        .MethodInfo localMethodInfo = (.MethodInfo)this.val$info.get(paramAnonymousInt);
        .Type[] arrayOfType = localMethodInfo.getSignature().getArgumentTypes();
        for (int i = 0; i < arrayOfType.length; i++)
        {
          this.val$e.load_arg(this.val$arg);
          this.val$e.aaload(i);
          this.val$e.unbox(arrayOfType[i]);
        }
        this.val$e.invoke(localMethodInfo, this.val$base);
        if (!.TypeUtils.isConstructor(localMethodInfo)) {
          this.val$e.box(localMethodInfo.getSignature().getReturnType());
        }
        this.val$e.return_value();
      }
      
      public void processDefault()
      {
        this.val$e.goTo(this.val$illegalArg);
      }
    });
    localBlock.end();
    .EmitUtils.wrap_throwable(localBlock, INVOCATION_TARGET_EXCEPTION);
    paramCodeEmitter.mark(localLabel);
    paramCodeEmitter.throw_exception(ILLEGAL_ARGUMENT_EXCEPTION, "Cannot find matching method/constructor");
  }
  
  private static int[] getIntRange(int paramInt)
  {
    int[] arrayOfInt = new int[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfInt[i] = i;
    }
    return arrayOfInt;
  }
  
  private static class GetIndexCallback
    implements .ObjectSwitchCallback
  {
    private .CodeEmitter e;
    private Map indexes = new HashMap();
    
    public GetIndexCallback(.CodeEmitter paramCodeEmitter, List paramList)
    {
      this.e = paramCodeEmitter;
      int i = 0;
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        this.indexes.put(localIterator.next(), new Integer(i++));
      }
    }
    
    public void processCase(Object paramObject, .Label paramLabel)
    {
      this.e.push(((Integer)this.indexes.get(paramObject)).intValue());
      this.e.return_value();
    }
    
    public void processDefault()
    {
      this.e.push(-1);
      this.e.return_value();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\reflect\$FastClassEmitter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */