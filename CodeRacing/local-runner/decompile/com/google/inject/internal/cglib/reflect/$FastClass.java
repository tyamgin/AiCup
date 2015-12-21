package com.google.inject.internal.cglib.reflect;

import com.google.inject.internal.asm..ClassVisitor;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..AbstractClassGenerator;
import com.google.inject.internal.cglib.core..AbstractClassGenerator.Source;
import com.google.inject.internal.cglib.core..Constants;
import com.google.inject.internal.cglib.core..ReflectUtils;
import com.google.inject.internal.cglib.core..Signature;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class $FastClass
{
  private Class type;
  
  protected $FastClass()
  {
    throw new Error("Using the FastClass empty constructor--please report to the cglib-devel mailing list");
  }
  
  protected $FastClass(Class paramClass)
  {
    this.type = paramClass;
  }
  
  public static FastClass create(Class paramClass)
  {
    return create(paramClass.getClassLoader(), paramClass);
  }
  
  public static FastClass create(ClassLoader paramClassLoader, Class paramClass)
  {
    Generator localGenerator = new Generator();
    localGenerator.setType(paramClass);
    localGenerator.setClassLoader(paramClassLoader);
    return localGenerator.create();
  }
  
  public Object invoke(String paramString, Class[] paramArrayOfClass, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    return invoke(getIndex(paramString, paramArrayOfClass), paramObject, paramArrayOfObject);
  }
  
  public Object newInstance()
    throws InvocationTargetException
  {
    return newInstance(getIndex(.Constants.EMPTY_CLASS_ARRAY), null);
  }
  
  public Object newInstance(Class[] paramArrayOfClass, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    return newInstance(getIndex(paramArrayOfClass), paramArrayOfObject);
  }
  
  public .FastMethod getMethod(Method paramMethod)
  {
    return new .FastMethod(this, paramMethod);
  }
  
  public .FastConstructor getConstructor(Constructor paramConstructor)
  {
    return new .FastConstructor(this, paramConstructor);
  }
  
  public .FastMethod getMethod(String paramString, Class[] paramArrayOfClass)
  {
    try
    {
      return getMethod(this.type.getMethod(paramString, paramArrayOfClass));
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
  }
  
  public .FastConstructor getConstructor(Class[] paramArrayOfClass)
  {
    try
    {
      return getConstructor(this.type.getConstructor(paramArrayOfClass));
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
  }
  
  public String getName()
  {
    return this.type.getName();
  }
  
  public Class getJavaClass()
  {
    return this.type;
  }
  
  public String toString()
  {
    return this.type.toString();
  }
  
  public int hashCode()
  {
    return this.type.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof FastClass))) {
      return false;
    }
    return this.type.equals(((FastClass)paramObject).type);
  }
  
  public abstract int getIndex(String paramString, Class[] paramArrayOfClass);
  
  public abstract int getIndex(Class[] paramArrayOfClass);
  
  public abstract Object invoke(int paramInt, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException;
  
  public abstract Object newInstance(int paramInt, Object[] paramArrayOfObject)
    throws InvocationTargetException;
  
  public abstract int getIndex(.Signature paramSignature);
  
  public abstract int getMaxIndex();
  
  protected static String getSignatureWithoutReturnType(String paramString, Class[] paramArrayOfClass)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramString);
    localStringBuffer.append('(');
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      localStringBuffer.append(.Type.getDescriptor(paramArrayOfClass[i]));
    }
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }
  
  public static class Generator
    extends .AbstractClassGenerator
  {
    private static final .AbstractClassGenerator.Source SOURCE = new .AbstractClassGenerator.Source(.FastClass.class.getName());
    private Class type;
    
    public Generator()
    {
      super();
    }
    
    public void setType(Class paramClass)
    {
      this.type = paramClass;
    }
    
    public .FastClass create()
    {
      setNamePrefix(this.type.getName());
      return (.FastClass)super.create(this.type.getName());
    }
    
    protected ClassLoader getDefaultClassLoader()
    {
      return this.type.getClassLoader();
    }
    
    public void generateClass(.ClassVisitor paramClassVisitor)
      throws Exception
    {
      new .FastClassEmitter(paramClassVisitor, getClassName(), this.type);
    }
    
    protected Object firstInstance(Class paramClass)
    {
      return .ReflectUtils.newInstance(paramClass, new Class[] { Class.class }, new Object[] { this.type });
    }
    
    protected Object nextInstance(Object paramObject)
    {
      return paramObject;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\reflect\$FastClass.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */