package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..Label;
import com.google.inject.internal.asm..Type;
import java.lang.reflect.Method;

public abstract class $KeyFactory
{
  private static final .Signature GET_NAME = .TypeUtils.parseSignature("String getName()");
  private static final .Signature GET_CLASS = .TypeUtils.parseSignature("Class getClass()");
  private static final .Signature HASH_CODE = .TypeUtils.parseSignature("int hashCode()");
  private static final .Signature EQUALS = .TypeUtils.parseSignature("boolean equals(Object)");
  private static final .Signature TO_STRING = .TypeUtils.parseSignature("String toString()");
  private static final .Signature APPEND_STRING = .TypeUtils.parseSignature("StringBuffer append(String)");
  private static final .Type KEY_FACTORY = .TypeUtils.parseType("com.google.inject.internal.cglib.core.$KeyFactory");
  private static final int[] PRIMES = { 11, 73, 179, 331, 521, 787, 1213, 1823, 2609, 3691, 5189, 7247, 10037, 13931, 19289, 26627, 36683, 50441, 69403, 95401, 131129, 180179, 247501, 340057, 467063, 641371, 880603, 1209107, 1660097, 2279161, 3129011, 4295723, 5897291, 8095873, 11114263, 15257791, 20946017, 28754629, 39474179, 54189869, 74391461, 102123817, 140194277, 192456917, 264202273, 362693231, 497900099, 683510293, 938313161, 1288102441, 1768288259 };
  public static final .Customizer CLASS_BY_NAME = new .Customizer()
  {
    public void customize(.CodeEmitter paramAnonymousCodeEmitter, .Type paramAnonymousType)
    {
      if (paramAnonymousType.equals(.Constants.TYPE_CLASS)) {
        paramAnonymousCodeEmitter.invoke_virtual(.Constants.TYPE_CLASS, .KeyFactory.GET_NAME);
      }
    }
  };
  public static final .Customizer OBJECT_BY_CLASS = new .Customizer()
  {
    public void customize(.CodeEmitter paramAnonymousCodeEmitter, .Type paramAnonymousType)
    {
      paramAnonymousCodeEmitter.invoke_virtual(.Constants.TYPE_OBJECT, .KeyFactory.GET_CLASS);
    }
  };
  
  public static KeyFactory create(Class paramClass)
  {
    return create(paramClass, null);
  }
  
  public static KeyFactory create(Class paramClass, .Customizer paramCustomizer)
  {
    return create(paramClass.getClassLoader(), paramClass, paramCustomizer);
  }
  
  public static KeyFactory create(ClassLoader paramClassLoader, Class paramClass, .Customizer paramCustomizer)
  {
    Generator localGenerator = new Generator();
    localGenerator.setInterface(paramClass);
    localGenerator.setCustomizer(paramCustomizer);
    localGenerator.setClassLoader(paramClassLoader);
    return localGenerator.create();
  }
  
  public static class Generator
    extends .AbstractClassGenerator
  {
    private static final .AbstractClassGenerator.Source SOURCE = new .AbstractClassGenerator.Source(.KeyFactory.class.getName());
    private Class keyInterface;
    private .Customizer customizer;
    private int constant;
    private int multiplier;
    
    public Generator()
    {
      super();
    }
    
    protected ClassLoader getDefaultClassLoader()
    {
      return this.keyInterface.getClassLoader();
    }
    
    public void setCustomizer(.Customizer paramCustomizer)
    {
      this.customizer = paramCustomizer;
    }
    
    public void setInterface(Class paramClass)
    {
      this.keyInterface = paramClass;
    }
    
    public .KeyFactory create()
    {
      setNamePrefix(this.keyInterface.getName());
      return (.KeyFactory)super.create(this.keyInterface.getName());
    }
    
    public void setHashConstant(int paramInt)
    {
      this.constant = paramInt;
    }
    
    public void setHashMultiplier(int paramInt)
    {
      this.multiplier = paramInt;
    }
    
    protected Object firstInstance(Class paramClass)
    {
      return .ReflectUtils.newInstance(paramClass);
    }
    
    protected Object nextInstance(Object paramObject)
    {
      return paramObject;
    }
    
    public void generateClass(.ClassVisitor paramClassVisitor)
    {
      .ClassEmitter localClassEmitter = new .ClassEmitter(paramClassVisitor);
      Method localMethod = .ReflectUtils.findNewInstance(this.keyInterface);
      if (!localMethod.getReturnType().equals(Object.class)) {
        throw new IllegalArgumentException("newInstance method must return Object");
      }
      .Type[] arrayOfType = .TypeUtils.getTypes(localMethod.getParameterTypes());
      localClassEmitter.begin_class(46, 1, getClassName(), .KeyFactory.KEY_FACTORY, new .Type[] { .Type.getType(this.keyInterface) }, "<generated>");
      .EmitUtils.null_constructor(localClassEmitter);
      .EmitUtils.factory_method(localClassEmitter, .ReflectUtils.getSignature(localMethod));
      int i = 0;
      .CodeEmitter localCodeEmitter = localClassEmitter.begin_method(1, .TypeUtils.parseConstructor(arrayOfType), null);
      localCodeEmitter.load_this();
      localCodeEmitter.super_invoke_constructor();
      localCodeEmitter.load_this();
      for (int j = 0; j < arrayOfType.length; j++)
      {
        i += arrayOfType[j].hashCode();
        localClassEmitter.declare_field(18, getFieldName(j), arrayOfType[j], null);
        localCodeEmitter.dup();
        localCodeEmitter.load_arg(j);
        localCodeEmitter.putfield(getFieldName(j));
      }
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
      localCodeEmitter = localClassEmitter.begin_method(1, .KeyFactory.HASH_CODE, null);
      j = this.constant != 0 ? this.constant : .KeyFactory.PRIMES[(Math.abs(i) % .KeyFactory.PRIMES.length)];
      int k = this.multiplier != 0 ? this.multiplier : .KeyFactory.PRIMES[(Math.abs(i * 13) % .KeyFactory.PRIMES.length)];
      localCodeEmitter.push(j);
      for (int m = 0; m < arrayOfType.length; m++)
      {
        localCodeEmitter.load_this();
        localCodeEmitter.getfield(getFieldName(m));
        .EmitUtils.hash_code(localCodeEmitter, arrayOfType[m], k, this.customizer);
      }
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
      localCodeEmitter = localClassEmitter.begin_method(1, .KeyFactory.EQUALS, null);
      .Label localLabel = localCodeEmitter.make_label();
      localCodeEmitter.load_arg(0);
      localCodeEmitter.instance_of_this();
      localCodeEmitter.if_jump(153, localLabel);
      for (int n = 0; n < arrayOfType.length; n++)
      {
        localCodeEmitter.load_this();
        localCodeEmitter.getfield(getFieldName(n));
        localCodeEmitter.load_arg(0);
        localCodeEmitter.checkcast_this();
        localCodeEmitter.getfield(getFieldName(n));
        .EmitUtils.not_equals(localCodeEmitter, arrayOfType[n], localLabel, this.customizer);
      }
      localCodeEmitter.push(1);
      localCodeEmitter.return_value();
      localCodeEmitter.mark(localLabel);
      localCodeEmitter.push(0);
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
      localCodeEmitter = localClassEmitter.begin_method(1, .KeyFactory.TO_STRING, null);
      localCodeEmitter.new_instance(.Constants.TYPE_STRING_BUFFER);
      localCodeEmitter.dup();
      localCodeEmitter.invoke_constructor(.Constants.TYPE_STRING_BUFFER);
      for (n = 0; n < arrayOfType.length; n++)
      {
        if (n > 0)
        {
          localCodeEmitter.push(", ");
          localCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, .KeyFactory.APPEND_STRING);
        }
        localCodeEmitter.load_this();
        localCodeEmitter.getfield(getFieldName(n));
        .EmitUtils.append_string(localCodeEmitter, arrayOfType[n], .EmitUtils.DEFAULT_DELIMITERS, this.customizer);
      }
      localCodeEmitter.invoke_virtual(.Constants.TYPE_STRING_BUFFER, .KeyFactory.TO_STRING);
      localCodeEmitter.return_value();
      localCodeEmitter.end_method();
      localClassEmitter.end_class();
    }
    
    private String getFieldName(int paramInt)
    {
      return "FIELD_" + paramInt;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$KeyFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */