package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class $EmitUtils
{
  private static final .Signature CSTRUCT_NULL = .TypeUtils.parseConstructor("");
  private static final .Signature CSTRUCT_THROWABLE = .TypeUtils.parseConstructor("Throwable");
  private static final .Signature GET_NAME = .TypeUtils.parseSignature("String getName()");
  private static final .Signature HASH_CODE = .TypeUtils.parseSignature("int hashCode()");
  private static final .Signature EQUALS = .TypeUtils.parseSignature("boolean equals(Object)");
  private static final .Signature STRING_LENGTH = .TypeUtils.parseSignature("int length()");
  private static final .Signature STRING_CHAR_AT = .TypeUtils.parseSignature("char charAt(int)");
  private static final .Signature FOR_NAME = .TypeUtils.parseSignature("Class forName(String)");
  private static final .Signature DOUBLE_TO_LONG_BITS = .TypeUtils.parseSignature("long doubleToLongBits(double)");
  private static final .Signature FLOAT_TO_INT_BITS = .TypeUtils.parseSignature("int floatToIntBits(float)");
  private static final .Signature TO_STRING = .TypeUtils.parseSignature("String toString()");
  private static final .Signature APPEND_STRING = .TypeUtils.parseSignature("StringBuffer append(String)");
  private static final .Signature APPEND_INT = .TypeUtils.parseSignature("StringBuffer append(int)");
  private static final .Signature APPEND_DOUBLE = .TypeUtils.parseSignature("StringBuffer append(double)");
  private static final .Signature APPEND_FLOAT = .TypeUtils.parseSignature("StringBuffer append(float)");
  private static final .Signature APPEND_CHAR = .TypeUtils.parseSignature("StringBuffer append(char)");
  private static final .Signature APPEND_LONG = .TypeUtils.parseSignature("StringBuffer append(long)");
  private static final .Signature APPEND_BOOLEAN = .TypeUtils.parseSignature("StringBuffer append(boolean)");
  private static final .Signature LENGTH = .TypeUtils.parseSignature("int length()");
  private static final .Signature SET_LENGTH = .TypeUtils.parseSignature("void setLength(int)");
  private static final .Signature GET_DECLARED_METHOD = .TypeUtils.parseSignature("java.lang.reflect.Method getDeclaredMethod(String, Class[])");
  public static final ArrayDelimiters DEFAULT_DELIMITERS = new ArrayDelimiters("{", ", ", "}");
  
  public static void factory_method(.ClassEmitter paramClassEmitter, .Signature paramSignature)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, paramSignature, null);
    localCodeEmitter.new_instance_this();
    localCodeEmitter.dup();
    localCodeEmitter.load_args();
    localCodeEmitter.invoke_constructor_this(.TypeUtils.parseConstructor(paramSignature.getArgumentTypes()));
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  public static void null_constructor(.ClassEmitter paramClassEmitter)
  {
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, CSTRUCT_NULL, null);
    localCodeEmitter.load_this();
    localCodeEmitter.super_invoke_constructor();
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  public static void process_array(.CodeEmitter paramCodeEmitter, .Type paramType, .ProcessArrayCallback paramProcessArrayCallback)
  {
    .Type localType = .TypeUtils.getComponentType(paramType);
    .Local localLocal1 = paramCodeEmitter.make_local();
    .Local localLocal2 = paramCodeEmitter.make_local(.Type.INT_TYPE);
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    paramCodeEmitter.store_local(localLocal1);
    paramCodeEmitter.push(0);
    paramCodeEmitter.store_local(localLocal2);
    paramCodeEmitter.goTo(localLabel2);
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.load_local(localLocal1);
    paramCodeEmitter.load_local(localLocal2);
    paramCodeEmitter.array_load(localType);
    paramProcessArrayCallback.processElement(localType);
    paramCodeEmitter.iinc(localLocal2, 1);
    paramCodeEmitter.mark(localLabel2);
    paramCodeEmitter.load_local(localLocal2);
    paramCodeEmitter.load_local(localLocal1);
    paramCodeEmitter.arraylength();
    paramCodeEmitter.if_icmp(155, localLabel1);
  }
  
  public static void process_arrays(.CodeEmitter paramCodeEmitter, .Type paramType, .ProcessArrayCallback paramProcessArrayCallback)
  {
    .Type localType = .TypeUtils.getComponentType(paramType);
    .Local localLocal1 = paramCodeEmitter.make_local();
    .Local localLocal2 = paramCodeEmitter.make_local();
    .Local localLocal3 = paramCodeEmitter.make_local(.Type.INT_TYPE);
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    paramCodeEmitter.store_local(localLocal1);
    paramCodeEmitter.store_local(localLocal2);
    paramCodeEmitter.push(0);
    paramCodeEmitter.store_local(localLocal3);
    paramCodeEmitter.goTo(localLabel2);
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.load_local(localLocal1);
    paramCodeEmitter.load_local(localLocal3);
    paramCodeEmitter.array_load(localType);
    paramCodeEmitter.load_local(localLocal2);
    paramCodeEmitter.load_local(localLocal3);
    paramCodeEmitter.array_load(localType);
    paramProcessArrayCallback.processElement(localType);
    paramCodeEmitter.iinc(localLocal3, 1);
    paramCodeEmitter.mark(localLabel2);
    paramCodeEmitter.load_local(localLocal3);
    paramCodeEmitter.load_local(localLocal1);
    paramCodeEmitter.arraylength();
    paramCodeEmitter.if_icmp(155, localLabel1);
  }
  
  public static void string_switch(.CodeEmitter paramCodeEmitter, String[] paramArrayOfString, int paramInt, .ObjectSwitchCallback paramObjectSwitchCallback)
  {
    try
    {
      switch (paramInt)
      {
      case 0: 
        string_switch_trie(paramCodeEmitter, paramArrayOfString, paramObjectSwitchCallback);
        break;
      case 1: 
        string_switch_hash(paramCodeEmitter, paramArrayOfString, paramObjectSwitchCallback, false);
        break;
      case 2: 
        string_switch_hash(paramCodeEmitter, paramArrayOfString, paramObjectSwitchCallback, true);
        break;
      default: 
        throw new IllegalArgumentException("unknown switch style " + paramInt);
      }
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
  
  private static void string_switch_trie(.CodeEmitter paramCodeEmitter, String[] paramArrayOfString, .ObjectSwitchCallback paramObjectSwitchCallback)
    throws Exception
  {
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    Map localMap = .CollectionUtils.bucket(Arrays.asList(paramArrayOfString), new .Transformer()
    {
      public Object transform(Object paramAnonymousObject)
      {
        return new Integer(((String)paramAnonymousObject).length());
      }
    });
    paramCodeEmitter.dup();
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING, STRING_LENGTH);
    paramCodeEmitter.process_switch(getSwitchKeys(localMap), new .ProcessSwitchCallback()
    {
      private final Map val$buckets;
      private final .CodeEmitter val$e;
      private final .ObjectSwitchCallback val$callback;
      private final .Label val$def;
      private final .Label val$end;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
        throws Exception
      {
        List localList = (List)this.val$buckets.get(new Integer(paramAnonymousInt));
        .EmitUtils.stringSwitchHelper(this.val$e, localList, this.val$callback, this.val$def, this.val$end, 0);
      }
      
      public void processDefault()
      {
        this.val$e.goTo(this.val$def);
      }
    });
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.pop();
    paramObjectSwitchCallback.processDefault();
    paramCodeEmitter.mark(localLabel2);
  }
  
  private static void stringSwitchHelper(.CodeEmitter paramCodeEmitter, List paramList, .ObjectSwitchCallback paramObjectSwitchCallback, .Label paramLabel1, .Label paramLabel2, int paramInt)
    throws Exception
  {
    int i = ((String)paramList.get(0)).length();
    Map localMap = .CollectionUtils.bucket(paramList, new .Transformer()
    {
      private final int val$index;
      
      public Object transform(Object paramAnonymousObject)
      {
        return new Integer(((String)paramAnonymousObject).charAt(this.val$index));
      }
    });
    paramCodeEmitter.dup();
    paramCodeEmitter.push(paramInt);
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING, STRING_CHAR_AT);
    paramCodeEmitter.process_switch(getSwitchKeys(localMap), new .ProcessSwitchCallback()
    {
      private final Map val$buckets;
      private final int val$index;
      private final int val$len;
      private final .CodeEmitter val$e;
      private final .ObjectSwitchCallback val$callback;
      private final .Label val$end;
      private final .Label val$def;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
        throws Exception
      {
        List localList = (List)this.val$buckets.get(new Integer(paramAnonymousInt));
        if (this.val$index + 1 == this.val$len)
        {
          this.val$e.pop();
          this.val$callback.processCase(localList.get(0), this.val$end);
        }
        else
        {
          .EmitUtils.stringSwitchHelper(this.val$e, localList, this.val$callback, this.val$def, this.val$end, this.val$index + 1);
        }
      }
      
      public void processDefault()
      {
        this.val$e.goTo(this.val$def);
      }
    });
  }
  
  static int[] getSwitchKeys(Map paramMap)
  {
    int[] arrayOfInt = new int[paramMap.size()];
    int i = 0;
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext()) {
      arrayOfInt[(i++)] = ((Integer)localIterator.next()).intValue();
    }
    Arrays.sort(arrayOfInt);
    return arrayOfInt;
  }
  
  private static void string_switch_hash(.CodeEmitter paramCodeEmitter, String[] paramArrayOfString, .ObjectSwitchCallback paramObjectSwitchCallback, boolean paramBoolean)
    throws Exception
  {
    Map localMap = .CollectionUtils.bucket(Arrays.asList(paramArrayOfString), new .Transformer()
    {
      public Object transform(Object paramAnonymousObject)
      {
        return new Integer(paramAnonymousObject.hashCode());
      }
    });
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    paramCodeEmitter.dup();
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, HASH_CODE);
    paramCodeEmitter.process_switch(getSwitchKeys(localMap), new .ProcessSwitchCallback()
    {
      private final Map val$buckets;
      private final boolean val$skipEquals;
      private final .CodeEmitter val$e;
      private final .ObjectSwitchCallback val$callback;
      private final .Label val$end;
      private final .Label val$def;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
        throws Exception
      {
        List localList = (List)this.val$buckets.get(new Integer(paramAnonymousInt));
        .Label localLabel = null;
        if ((this.val$skipEquals) && (localList.size() == 1))
        {
          if (this.val$skipEquals) {
            this.val$e.pop();
          }
          this.val$callback.processCase((String)localList.get(0), this.val$end);
        }
        else
        {
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            String str = (String)localIterator.next();
            if (localLabel != null) {
              this.val$e.mark(localLabel);
            }
            if (localIterator.hasNext()) {
              this.val$e.dup();
            }
            this.val$e.push(str);
            this.val$e.invoke_virtual(.Constants.TYPE_OBJECT, .EmitUtils.EQUALS);
            if (localIterator.hasNext())
            {
              this.val$e.if_jump(153, localLabel = this.val$e.make_label());
              this.val$e.pop();
            }
            else
            {
              this.val$e.if_jump(153, this.val$def);
            }
            this.val$callback.processCase(str, this.val$end);
          }
        }
      }
      
      public void processDefault()
      {
        this.val$e.pop();
      }
    });
    paramCodeEmitter.mark(localLabel1);
    paramObjectSwitchCallback.processDefault();
    paramCodeEmitter.mark(localLabel2);
  }
  
  public static void load_class_this(.CodeEmitter paramCodeEmitter)
  {
    load_class_helper(paramCodeEmitter, paramCodeEmitter.getClassEmitter().getClassType());
  }
  
  public static void load_class(.CodeEmitter paramCodeEmitter, .Type paramType)
  {
    if (.TypeUtils.isPrimitive(paramType))
    {
      if (paramType == .Type.VOID_TYPE) {
        throw new IllegalArgumentException("cannot load void type");
      }
      paramCodeEmitter.getstatic(.TypeUtils.getBoxedType(paramType), "TYPE", .Constants.TYPE_CLASS);
    }
    else
    {
      load_class_helper(paramCodeEmitter, paramType);
    }
  }
  
  private static void load_class_helper(.CodeEmitter paramCodeEmitter, .Type paramType)
  {
    if (paramCodeEmitter.isStaticHook())
    {
      paramCodeEmitter.push(.TypeUtils.emulateClassGetName(paramType));
      paramCodeEmitter.invoke_static(.Constants.TYPE_CLASS, FOR_NAME);
    }
    else
    {
      .ClassEmitter localClassEmitter = paramCodeEmitter.getClassEmitter();
      String str1 = .TypeUtils.emulateClassGetName(paramType);
      String str2 = "CGLIB$load_class$" + .TypeUtils.escapeType(str1);
      if (!localClassEmitter.isFieldDeclared(str2))
      {
        localClassEmitter.declare_field(26, str2, .Constants.TYPE_CLASS, null);
        .CodeEmitter localCodeEmitter = localClassEmitter.getStaticHook();
        localCodeEmitter.push(str1);
        localCodeEmitter.invoke_static(.Constants.TYPE_CLASS, FOR_NAME);
        localCodeEmitter.putstatic(localClassEmitter.getClassType(), str2, .Constants.TYPE_CLASS);
      }
      paramCodeEmitter.getfield(str2);
    }
  }
  
  public static void push_array(.CodeEmitter paramCodeEmitter, Object[] paramArrayOfObject)
  {
    paramCodeEmitter.push(paramArrayOfObject.length);
    paramCodeEmitter.newarray(.Type.getType(remapComponentType(paramArrayOfObject.getClass().getComponentType())));
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      paramCodeEmitter.dup();
      paramCodeEmitter.push(i);
      push_object(paramCodeEmitter, paramArrayOfObject[i]);
      paramCodeEmitter.aastore();
    }
  }
  
  private static Class remapComponentType(Class paramClass)
  {
    if (paramClass.equals(.Type.class)) {
      return Class.class;
    }
    return paramClass;
  }
  
  public static void push_object(.CodeEmitter paramCodeEmitter, Object paramObject)
  {
    if (paramObject == null)
    {
      paramCodeEmitter.aconst_null();
    }
    else
    {
      Class localClass = paramObject.getClass();
      if (localClass.isArray())
      {
        push_array(paramCodeEmitter, (Object[])paramObject);
      }
      else if ((paramObject instanceof String))
      {
        paramCodeEmitter.push((String)paramObject);
      }
      else if ((paramObject instanceof .Type))
      {
        load_class(paramCodeEmitter, (.Type)paramObject);
      }
      else if ((paramObject instanceof Class))
      {
        load_class(paramCodeEmitter, .Type.getType((Class)paramObject));
      }
      else if ((paramObject instanceof BigInteger))
      {
        paramCodeEmitter.new_instance(.Constants.TYPE_BIG_INTEGER);
        paramCodeEmitter.dup();
        paramCodeEmitter.push(paramObject.toString());
        paramCodeEmitter.invoke_constructor(.Constants.TYPE_BIG_INTEGER);
      }
      else if ((paramObject instanceof BigDecimal))
      {
        paramCodeEmitter.new_instance(.Constants.TYPE_BIG_DECIMAL);
        paramCodeEmitter.dup();
        paramCodeEmitter.push(paramObject.toString());
        paramCodeEmitter.invoke_constructor(.Constants.TYPE_BIG_DECIMAL);
      }
      else
      {
        throw new IllegalArgumentException("unknown type: " + paramObject.getClass());
      }
    }
  }
  
  public static void hash_code(.CodeEmitter paramCodeEmitter, .Type paramType, int paramInt, .Customizer paramCustomizer)
  {
    if (.TypeUtils.isArray(paramType))
    {
      hash_array(paramCodeEmitter, paramType, paramInt, paramCustomizer);
    }
    else
    {
      paramCodeEmitter.swap(.Type.INT_TYPE, paramType);
      paramCodeEmitter.push(paramInt);
      paramCodeEmitter.math(104, .Type.INT_TYPE);
      paramCodeEmitter.swap(paramType, .Type.INT_TYPE);
      if (.TypeUtils.isPrimitive(paramType)) {
        hash_primitive(paramCodeEmitter, paramType);
      } else {
        hash_object(paramCodeEmitter, paramType, paramCustomizer);
      }
      paramCodeEmitter.math(96, .Type.INT_TYPE);
    }
  }
  
  private static void hash_array(.CodeEmitter paramCodeEmitter, .Type paramType, int paramInt, .Customizer paramCustomizer)
  {
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    paramCodeEmitter.dup();
    paramCodeEmitter.ifnull(localLabel1);
    process_array(paramCodeEmitter, paramType, new .ProcessArrayCallback()
    {
      private final .CodeEmitter val$e;
      private final int val$multiplier;
      private final .Customizer val$customizer;
      
      public void processElement(.Type paramAnonymousType)
      {
        .EmitUtils.hash_code(this.val$e, paramAnonymousType, this.val$multiplier, this.val$customizer);
      }
    });
    paramCodeEmitter.goTo(localLabel2);
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.pop();
    paramCodeEmitter.mark(localLabel2);
  }
  
  private static void hash_object(.CodeEmitter paramCodeEmitter, .Type paramType, .Customizer paramCustomizer)
  {
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    paramCodeEmitter.dup();
    paramCodeEmitter.ifnull(localLabel1);
    if (paramCustomizer != null) {
      paramCustomizer.customize(paramCodeEmitter, paramType);
    }
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, HASH_CODE);
    paramCodeEmitter.goTo(localLabel2);
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.pop();
    paramCodeEmitter.push(0);
    paramCodeEmitter.mark(localLabel2);
  }
  
  private static void hash_primitive(.CodeEmitter paramCodeEmitter, .Type paramType)
  {
    switch (paramType.getSort())
    {
    case 1: 
      paramCodeEmitter.push(1);
      paramCodeEmitter.math(130, .Type.INT_TYPE);
      break;
    case 6: 
      paramCodeEmitter.invoke_static(.Constants.TYPE_FLOAT, FLOAT_TO_INT_BITS);
      break;
    case 8: 
      paramCodeEmitter.invoke_static(.Constants.TYPE_DOUBLE, DOUBLE_TO_LONG_BITS);
    case 7: 
      hash_long(paramCodeEmitter);
    }
  }
  
  private static void hash_long(.CodeEmitter paramCodeEmitter)
  {
    paramCodeEmitter.dup2();
    paramCodeEmitter.push(32);
    paramCodeEmitter.math(124, .Type.LONG_TYPE);
    paramCodeEmitter.math(130, .Type.LONG_TYPE);
    paramCodeEmitter.cast_numeric(.Type.LONG_TYPE, .Type.INT_TYPE);
  }
  
  public static void not_equals(.CodeEmitter paramCodeEmitter, .Type paramType, .Label paramLabel, .Customizer paramCustomizer)
  {
    new .ProcessArrayCallback()
    {
      private final .CodeEmitter val$e;
      private final .Label val$notEquals;
      private final .Customizer val$customizer;
      
      public void processElement(.Type paramAnonymousType)
      {
        .EmitUtils.not_equals_helper(this.val$e, paramAnonymousType, this.val$notEquals, this.val$customizer, this);
      }
    }.processElement(paramType);
  }
  
  private static void not_equals_helper(.CodeEmitter paramCodeEmitter, .Type paramType, .Label paramLabel, .Customizer paramCustomizer, .ProcessArrayCallback paramProcessArrayCallback)
  {
    if (.TypeUtils.isPrimitive(paramType))
    {
      paramCodeEmitter.if_cmp(paramType, 154, paramLabel);
    }
    else
    {
      .Label localLabel1 = paramCodeEmitter.make_label();
      nullcmp(paramCodeEmitter, paramLabel, localLabel1);
      if (.TypeUtils.isArray(paramType))
      {
        .Label localLabel2 = paramCodeEmitter.make_label();
        paramCodeEmitter.dup2();
        paramCodeEmitter.arraylength();
        paramCodeEmitter.swap();
        paramCodeEmitter.arraylength();
        paramCodeEmitter.if_icmp(153, localLabel2);
        paramCodeEmitter.pop2();
        paramCodeEmitter.goTo(paramLabel);
        paramCodeEmitter.mark(localLabel2);
        process_arrays(paramCodeEmitter, paramType, paramProcessArrayCallback);
      }
      else
      {
        if (paramCustomizer != null)
        {
          paramCustomizer.customize(paramCodeEmitter, paramType);
          paramCodeEmitter.swap();
          paramCustomizer.customize(paramCodeEmitter, paramType);
        }
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, EQUALS);
        paramCodeEmitter.if_jump(153, paramLabel);
      }
      paramCodeEmitter.mark(localLabel1);
    }
  }
  
  private static void nullcmp(.CodeEmitter paramCodeEmitter, .Label paramLabel1, .Label paramLabel2)
  {
    paramCodeEmitter.dup2();
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    .Label localLabel3 = paramCodeEmitter.make_label();
    paramCodeEmitter.ifnonnull(localLabel1);
    paramCodeEmitter.ifnonnull(localLabel2);
    paramCodeEmitter.pop2();
    paramCodeEmitter.goTo(paramLabel2);
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.ifnull(localLabel2);
    paramCodeEmitter.goTo(localLabel3);
    paramCodeEmitter.mark(localLabel2);
    paramCodeEmitter.pop2();
    paramCodeEmitter.goTo(paramLabel1);
    paramCodeEmitter.mark(localLabel3);
  }
  
  public static void append_string(.CodeEmitter paramCodeEmitter, .Type paramType, ArrayDelimiters paramArrayDelimiters, .Customizer paramCustomizer)
  {
    ArrayDelimiters localArrayDelimiters = paramArrayDelimiters != null ? paramArrayDelimiters : DEFAULT_DELIMITERS;
    .ProcessArrayCallback local9 = new .ProcessArrayCallback()
    {
      private final .CodeEmitter val$e;
      private final .EmitUtils.ArrayDelimiters val$d;
      private final .Customizer val$customizer;
      
      public void processElement(.Type paramAnonymousType)
      {
        .EmitUtils.append_string_helper(this.val$e, paramAnonymousType, this.val$d, this.val$customizer, this);
        this.val$e.push(this.val$d.inside);
        this.val$e.invoke_virtual(.Constants.TYPE_STRING_BUFFER, .EmitUtils.APPEND_STRING);
      }
    };
    append_string_helper(paramCodeEmitter, paramType, localArrayDelimiters, paramCustomizer, local9);
  }
  
  private static void append_string_helper(.CodeEmitter paramCodeEmitter, .Type paramType, ArrayDelimiters paramArrayDelimiters, .Customizer paramCustomizer, .ProcessArrayCallback paramProcessArrayCallback)
  {
    .Label localLabel1 = paramCodeEmitter.make_label();
    .Label localLabel2 = paramCodeEmitter.make_label();
    if (.TypeUtils.isPrimitive(paramType))
    {
      switch (paramType.getSort())
      {
      case 3: 
      case 4: 
      case 5: 
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_INT);
        break;
      case 8: 
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_DOUBLE);
        break;
      case 6: 
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_FLOAT);
        break;
      case 7: 
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_LONG);
        break;
      case 1: 
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_BOOLEAN);
        break;
      case 2: 
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_CHAR);
      }
    }
    else if (.TypeUtils.isArray(paramType))
    {
      paramCodeEmitter.dup();
      paramCodeEmitter.ifnull(localLabel1);
      paramCodeEmitter.swap();
      if ((paramArrayDelimiters != null) && (paramArrayDelimiters.before != null) && (!"".equals(paramArrayDelimiters.before)))
      {
        paramCodeEmitter.push(paramArrayDelimiters.before);
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_STRING);
        paramCodeEmitter.swap();
      }
      process_array(paramCodeEmitter, paramType, paramProcessArrayCallback);
      shrinkStringBuffer(paramCodeEmitter, 2);
      if ((paramArrayDelimiters != null) && (paramArrayDelimiters.after != null) && (!"".equals(paramArrayDelimiters.after)))
      {
        paramCodeEmitter.push(paramArrayDelimiters.after);
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_STRING);
      }
    }
    else
    {
      paramCodeEmitter.dup();
      paramCodeEmitter.ifnull(localLabel1);
      if (paramCustomizer != null) {
        paramCustomizer.customize(paramCodeEmitter, paramType);
      }
      paramCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, TO_STRING);
      paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_STRING);
    }
    paramCodeEmitter.goTo(localLabel2);
    paramCodeEmitter.mark(localLabel1);
    paramCodeEmitter.pop();
    paramCodeEmitter.push("null");
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, APPEND_STRING);
    paramCodeEmitter.mark(localLabel2);
  }
  
  private static void shrinkStringBuffer(.CodeEmitter paramCodeEmitter, int paramInt)
  {
    paramCodeEmitter.dup();
    paramCodeEmitter.dup();
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, LENGTH);
    paramCodeEmitter.push(paramInt);
    paramCodeEmitter.math(100, .Type.INT_TYPE);
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, SET_LENGTH);
  }
  
  public static void load_method(.CodeEmitter paramCodeEmitter, .MethodInfo paramMethodInfo)
  {
    load_class(paramCodeEmitter, paramMethodInfo.getClassInfo().getType());
    paramCodeEmitter.push(paramMethodInfo.getSignature().getName());
    push_object(paramCodeEmitter, paramMethodInfo.getSignature().getArgumentTypes());
    paramCodeEmitter.invoke_virtual(.Constants.TYPE_CLASS, GET_DECLARED_METHOD);
  }
  
  public static void method_switch(.CodeEmitter paramCodeEmitter, List paramList, .ObjectSwitchCallback paramObjectSwitchCallback)
  {
    member_switch_helper(paramCodeEmitter, paramList, paramObjectSwitchCallback, true);
  }
  
  public static void constructor_switch(.CodeEmitter paramCodeEmitter, List paramList, .ObjectSwitchCallback paramObjectSwitchCallback)
  {
    member_switch_helper(paramCodeEmitter, paramList, paramObjectSwitchCallback, false);
  }
  
  private static void member_switch_helper(.CodeEmitter paramCodeEmitter, List paramList, .ObjectSwitchCallback paramObjectSwitchCallback, boolean paramBoolean)
  {
    try
    {
      HashMap localHashMap = new HashMap();
      ParameterTyper local10 = new ParameterTyper()
      {
        private final Map val$cache;
        
        public .Type[] getParameterTypes(.MethodInfo paramAnonymousMethodInfo)
        {
          .Type[] arrayOfType = (.Type[])this.val$cache.get(paramAnonymousMethodInfo);
          if (arrayOfType == null) {
            this.val$cache.put(paramAnonymousMethodInfo, arrayOfType = paramAnonymousMethodInfo.getSignature().getArgumentTypes());
          }
          return arrayOfType;
        }
      };
      .Label localLabel1 = paramCodeEmitter.make_label();
      .Label localLabel2 = paramCodeEmitter.make_label();
      if (paramBoolean)
      {
        paramCodeEmitter.swap();
        Map localMap = .CollectionUtils.bucket(paramList, new .Transformer()
        {
          public Object transform(Object paramAnonymousObject)
          {
            return ((.MethodInfo)paramAnonymousObject).getSignature().getName();
          }
        });
        String[] arrayOfString = (String[])localMap.keySet().toArray(new String[localMap.size()]);
        string_switch(paramCodeEmitter, arrayOfString, 1, new .ObjectSwitchCallback()
        {
          private final .CodeEmitter val$e;
          private final Map val$buckets;
          private final .ObjectSwitchCallback val$callback;
          private final .EmitUtils.ParameterTyper val$cached;
          private final .Label val$def;
          private final .Label val$end;
          
          public void processCase(Object paramAnonymousObject, .Label paramAnonymousLabel)
            throws Exception
          {
            .EmitUtils.member_helper_size(this.val$e, (List)this.val$buckets.get(paramAnonymousObject), this.val$callback, this.val$cached, this.val$def, this.val$end);
          }
          
          public void processDefault()
            throws Exception
          {
            this.val$e.goTo(this.val$def);
          }
        });
      }
      else
      {
        member_helper_size(paramCodeEmitter, paramList, paramObjectSwitchCallback, local10, localLabel1, localLabel2);
      }
      paramCodeEmitter.mark(localLabel1);
      paramCodeEmitter.pop();
      paramObjectSwitchCallback.processDefault();
      paramCodeEmitter.mark(localLabel2);
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
  
  private static void member_helper_size(.CodeEmitter paramCodeEmitter, List paramList, .ObjectSwitchCallback paramObjectSwitchCallback, ParameterTyper paramParameterTyper, .Label paramLabel1, .Label paramLabel2)
    throws Exception
  {
    Map localMap = .CollectionUtils.bucket(paramList, new .Transformer()
    {
      private final .EmitUtils.ParameterTyper val$typer;
      
      public Object transform(Object paramAnonymousObject)
      {
        return new Integer(this.val$typer.getParameterTypes((.MethodInfo)paramAnonymousObject).length);
      }
    });
    paramCodeEmitter.dup();
    paramCodeEmitter.arraylength();
    paramCodeEmitter.process_switch(getSwitchKeys(localMap), new .ProcessSwitchCallback()
    {
      private final Map val$buckets;
      private final .CodeEmitter val$e;
      private final .ObjectSwitchCallback val$callback;
      private final .EmitUtils.ParameterTyper val$typer;
      private final .Label val$def;
      private final .Label val$end;
      
      public void processCase(int paramAnonymousInt, .Label paramAnonymousLabel)
        throws Exception
      {
        List localList = (List)this.val$buckets.get(new Integer(paramAnonymousInt));
        .EmitUtils.member_helper_type(this.val$e, localList, this.val$callback, this.val$typer, this.val$def, this.val$end, new BitSet());
      }
      
      public void processDefault()
        throws Exception
      {
        this.val$e.goTo(this.val$def);
      }
    });
  }
  
  private static void member_helper_type(.CodeEmitter paramCodeEmitter, List paramList, .ObjectSwitchCallback paramObjectSwitchCallback, ParameterTyper paramParameterTyper, .Label paramLabel1, .Label paramLabel2, BitSet paramBitSet)
    throws Exception
  {
    Object localObject1;
    Object localObject2;
    int i;
    if (paramList.size() == 1)
    {
      localObject1 = (.MethodInfo)paramList.get(0);
      localObject2 = paramParameterTyper.getParameterTypes((.MethodInfo)localObject1);
      for (i = 0; i < localObject2.length; i++) {
        if ((paramBitSet == null) || (!paramBitSet.get(i)))
        {
          paramCodeEmitter.dup();
          paramCodeEmitter.aaload(i);
          paramCodeEmitter.invoke_virtual(.Constants.TYPE_CLASS, GET_NAME);
          paramCodeEmitter.push(.TypeUtils.emulateClassGetName(localObject2[i]));
          paramCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, EQUALS);
          paramCodeEmitter.if_jump(153, paramLabel1);
        }
      }
      paramCodeEmitter.pop();
      paramObjectSwitchCallback.processCase(localObject1, paramLabel2);
    }
    else
    {
      localObject1 = paramParameterTyper.getParameterTypes((.MethodInfo)paramList.get(0));
      localObject2 = null;
      i = -1;
      for (int j = 0; j < localObject1.length; j++)
      {
        int k = j;
        Map localMap = .CollectionUtils.bucket(paramList, new .Transformer()
        {
          private final .EmitUtils.ParameterTyper val$typer;
          private final int val$j;
          
          public Object transform(Object paramAnonymousObject)
          {
            return .TypeUtils.emulateClassGetName(this.val$typer.getParameterTypes((.MethodInfo)paramAnonymousObject)[this.val$j]);
          }
        });
        if ((localObject2 == null) || (localMap.size() > ((Map)localObject2).size()))
        {
          localObject2 = localMap;
          i = j;
        }
      }
      if ((localObject2 == null) || (((Map)localObject2).size() == 1))
      {
        paramCodeEmitter.goTo(paramLabel1);
      }
      else
      {
        paramBitSet.set(i);
        paramCodeEmitter.dup();
        paramCodeEmitter.aaload(i);
        paramCodeEmitter.invoke_virtual(.Constants.TYPE_CLASS, GET_NAME);
        Object localObject3 = localObject2;
        String[] arrayOfString = (String[])((Map)localObject2).keySet().toArray(new String[((Map)localObject2).size()]);
        string_switch(paramCodeEmitter, arrayOfString, 1, new .ObjectSwitchCallback()
        {
          private final .CodeEmitter val$e;
          private final Map val$fbuckets;
          private final .ObjectSwitchCallback val$callback;
          private final .EmitUtils.ParameterTyper val$typer;
          private final .Label val$def;
          private final .Label val$end;
          private final BitSet val$checked;
          
          public void processCase(Object paramAnonymousObject, .Label paramAnonymousLabel)
            throws Exception
          {
            .EmitUtils.member_helper_type(this.val$e, (List)this.val$fbuckets.get(paramAnonymousObject), this.val$callback, this.val$typer, this.val$def, this.val$end, this.val$checked);
          }
          
          public void processDefault()
            throws Exception
          {
            this.val$e.goTo(this.val$def);
          }
        });
      }
    }
  }
  
  public static void wrap_throwable(.Block paramBlock, .Type paramType)
  {
    .CodeEmitter localCodeEmitter = paramBlock.getCodeEmitter();
    localCodeEmitter.catch_exception(paramBlock, .Constants.TYPE_THROWABLE);
    localCodeEmitter.new_instance(paramType);
    localCodeEmitter.dup_x1();
    localCodeEmitter.swap();
    localCodeEmitter.invoke_constructor(paramType, CSTRUCT_THROWABLE);
    localCodeEmitter.athrow();
  }
  
  public static void add_properties(.ClassEmitter paramClassEmitter, String[] paramArrayOfString, .Type[] paramArrayOfType)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = "$cglib_prop_" + paramArrayOfString[i];
      paramClassEmitter.declare_field(2, str, paramArrayOfType[i], null);
      add_property(paramClassEmitter, paramArrayOfString[i], paramArrayOfType[i], str);
    }
  }
  
  public static void add_property(.ClassEmitter paramClassEmitter, String paramString1, .Type paramType, String paramString2)
  {
    String str = .TypeUtils.upperFirst(paramString1);
    .CodeEmitter localCodeEmitter = paramClassEmitter.begin_method(1, new .Signature("get" + str, paramType, .Constants.TYPES_EMPTY), null);
    localCodeEmitter.load_this();
    localCodeEmitter.getfield(paramString2);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
    localCodeEmitter = paramClassEmitter.begin_method(1, new .Signature("set" + str, .Type.VOID_TYPE, new .Type[] { paramType }), null);
    localCodeEmitter.load_this();
    localCodeEmitter.load_arg(0);
    localCodeEmitter.putfield(paramString2);
    localCodeEmitter.return_value();
    localCodeEmitter.end_method();
  }
  
  public static void wrap_undeclared_throwable(.CodeEmitter paramCodeEmitter, .Block paramBlock, .Type[] paramArrayOfType, .Type paramType)
  {
    HashSet localHashSet = paramArrayOfType == null ? Collections.EMPTY_SET : new HashSet(Arrays.asList(paramArrayOfType));
    if (localHashSet.contains(.Constants.TYPE_THROWABLE)) {
      return;
    }
    int i = paramArrayOfType != null ? 1 : 0;
    if (!localHashSet.contains(.Constants.TYPE_RUNTIME_EXCEPTION))
    {
      paramCodeEmitter.catch_exception(paramBlock, .Constants.TYPE_RUNTIME_EXCEPTION);
      i = 1;
    }
    if (!localHashSet.contains(.Constants.TYPE_ERROR))
    {
      paramCodeEmitter.catch_exception(paramBlock, .Constants.TYPE_ERROR);
      i = 1;
    }
    if (paramArrayOfType != null) {
      for (int j = 0; j < paramArrayOfType.length; j++) {
        paramCodeEmitter.catch_exception(paramBlock, paramArrayOfType[j]);
      }
    }
    if (i != 0) {
      paramCodeEmitter.athrow();
    }
    paramCodeEmitter.catch_exception(paramBlock, .Constants.TYPE_THROWABLE);
    paramCodeEmitter.new_instance(paramType);
    paramCodeEmitter.dup_x1();
    paramCodeEmitter.swap();
    paramCodeEmitter.invoke_constructor(paramType, CSTRUCT_THROWABLE);
    paramCodeEmitter.athrow();
  }
  
  public static .CodeEmitter begin_method(.ClassEmitter paramClassEmitter, .MethodInfo paramMethodInfo)
  {
    return begin_method(paramClassEmitter, paramMethodInfo, paramMethodInfo.getModifiers());
  }
  
  public static .CodeEmitter begin_method(.ClassEmitter paramClassEmitter, .MethodInfo paramMethodInfo, int paramInt)
  {
    return paramClassEmitter.begin_method(paramInt, paramMethodInfo.getSignature(), paramMethodInfo.getExceptionTypes());
  }
  
  private static abstract interface ParameterTyper
  {
    public abstract .Type[] getParameterTypes(.MethodInfo paramMethodInfo);
  }
  
  public static class ArrayDelimiters
  {
    private String before;
    private String inside;
    private String after;
    
    public ArrayDelimiters(String paramString1, String paramString2, String paramString3)
    {
      this.before = paramString1;
      this.inside = paramString2;
      this.after = paramString3;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$EmitUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */