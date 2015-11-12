package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Attribute;
import com.google.inject.internal.asm..Type;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class $ReflectUtils
{
  private static final Map primitives = new HashMap(8);
  private static final Map transforms = new HashMap(8);
  private static final ClassLoader defaultLoader = ReflectUtils.class.getClassLoader();
  private static Method DEFINE_CLASS;
  private static final ProtectionDomain PROTECTION_DOMAIN = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Object run()
    {
      return .ReflectUtils.class.getProtectionDomain();
    }
  });
  private static final String[] CGLIB_PACKAGES;
  
  public static .Type[] getExceptionTypes(Member paramMember)
  {
    if ((paramMember instanceof Method)) {
      return .TypeUtils.getTypes(((Method)paramMember).getExceptionTypes());
    }
    if ((paramMember instanceof Constructor)) {
      return .TypeUtils.getTypes(((Constructor)paramMember).getExceptionTypes());
    }
    throw new IllegalArgumentException("Cannot get exception types of a field");
  }
  
  public static .Signature getSignature(Member paramMember)
  {
    if ((paramMember instanceof Method)) {
      return new .Signature(paramMember.getName(), .Type.getMethodDescriptor((Method)paramMember));
    }
    if ((paramMember instanceof Constructor))
    {
      .Type[] arrayOfType = .TypeUtils.getTypes(((Constructor)paramMember).getParameterTypes());
      return new .Signature("<init>", .Type.getMethodDescriptor(.Type.VOID_TYPE, arrayOfType));
    }
    throw new IllegalArgumentException("Cannot get signature of a field");
  }
  
  public static Constructor findConstructor(String paramString)
  {
    return findConstructor(paramString, defaultLoader);
  }
  
  public static Constructor findConstructor(String paramString, ClassLoader paramClassLoader)
  {
    try
    {
      int i = paramString.indexOf('(');
      String str = paramString.substring(0, i).trim();
      return getClass(str, paramClassLoader).getConstructor(parseTypes(paramString, paramClassLoader));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new .CodeGenerationException(localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new .CodeGenerationException(localNoSuchMethodException);
    }
  }
  
  public static Method findMethod(String paramString)
  {
    return findMethod(paramString, defaultLoader);
  }
  
  public static Method findMethod(String paramString, ClassLoader paramClassLoader)
  {
    try
    {
      int i = paramString.indexOf('(');
      int j = paramString.lastIndexOf('.', i);
      String str1 = paramString.substring(0, j).trim();
      String str2 = paramString.substring(j + 1, i).trim();
      return getClass(str1, paramClassLoader).getDeclaredMethod(str2, parseTypes(paramString, paramClassLoader));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new .CodeGenerationException(localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new .CodeGenerationException(localNoSuchMethodException);
    }
  }
  
  private static Class[] parseTypes(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    int i = paramString.indexOf('(');
    int j = paramString.indexOf(')', i);
    ArrayList localArrayList = new ArrayList();
    int m;
    for (int k = i + 1;; k = m + 1)
    {
      m = paramString.indexOf(',', k);
      if (m < 0) {
        break;
      }
      localArrayList.add(paramString.substring(k, m).trim());
    }
    if (k < j) {
      localArrayList.add(paramString.substring(k, j).trim());
    }
    Class[] arrayOfClass = new Class[localArrayList.size()];
    for (int n = 0; n < arrayOfClass.length; n++) {
      arrayOfClass[n] = getClass((String)localArrayList.get(n), paramClassLoader);
    }
    return arrayOfClass;
  }
  
  private static Class getClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    return getClass(paramString, paramClassLoader, CGLIB_PACKAGES);
  }
  
  private static Class getClass(String paramString, ClassLoader paramClassLoader, String[] paramArrayOfString)
    throws ClassNotFoundException
  {
    String str1 = paramString;
    int i = 0;
    int j = 0;
    while ((j = paramString.indexOf("[]", j) + 1) > 0) {
      i++;
    }
    StringBuffer localStringBuffer = new StringBuffer(paramString.length() - i);
    for (int k = 0; k < i; k++) {
      localStringBuffer.append('[');
    }
    paramString = paramString.substring(0, paramString.length() - 2 * i);
    String str2 = i > 0 ? localStringBuffer + "L" : "";
    String str3 = i > 0 ? ";" : "";
    try
    {
      return Class.forName(str2 + paramString + str3, false, paramClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      int m = 0;
      while (m < paramArrayOfString.length) {
        try
        {
          return Class.forName(str2 + paramArrayOfString[m] + '.' + paramString + str3, false, paramClassLoader);
        }
        catch (ClassNotFoundException localClassNotFoundException2)
        {
          m++;
        }
      }
      Object localObject;
      if (i == 0)
      {
        localObject = (Class)primitives.get(paramString);
        if (localObject != null) {
          return (Class)localObject;
        }
      }
      else
      {
        localObject = (String)transforms.get(paramString);
        if (localObject != null) {
          try
          {
            return Class.forName(localStringBuffer + (String)localObject, false, paramClassLoader);
          }
          catch (ClassNotFoundException localClassNotFoundException3) {}
        }
      }
      throw new ClassNotFoundException(str1);
    }
  }
  
  public static Object newInstance(Class paramClass)
  {
    return newInstance(paramClass, .Constants.EMPTY_CLASS_ARRAY, null);
  }
  
  public static Object newInstance(Class paramClass, Class[] paramArrayOfClass, Object[] paramArrayOfObject)
  {
    return newInstance(getConstructor(paramClass, paramArrayOfClass), paramArrayOfObject);
  }
  
  public static Object newInstance(Constructor paramConstructor, Object[] paramArrayOfObject)
  {
    boolean bool = paramConstructor.isAccessible();
    try
    {
      paramConstructor.setAccessible(true);
      Object localObject1 = paramConstructor.newInstance(paramArrayOfObject);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new .CodeGenerationException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new .CodeGenerationException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new .CodeGenerationException(localInvocationTargetException.getTargetException());
    }
    finally
    {
      paramConstructor.setAccessible(bool);
    }
  }
  
  public static Constructor getConstructor(Class paramClass, Class[] paramArrayOfClass)
  {
    try
    {
      Constructor localConstructor = paramClass.getDeclaredConstructor(paramArrayOfClass);
      localConstructor.setAccessible(true);
      return localConstructor;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new .CodeGenerationException(localNoSuchMethodException);
    }
  }
  
  public static String[] getNames(Class[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null) {
      return null;
    }
    String[] arrayOfString = new String[paramArrayOfClass.length];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = paramArrayOfClass[i].getName();
    }
    return arrayOfString;
  }
  
  public static Class[] getClasses(Object[] paramArrayOfObject)
  {
    Class[] arrayOfClass = new Class[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      arrayOfClass[i] = paramArrayOfObject[i].getClass();
    }
    return arrayOfClass;
  }
  
  public static Method findNewInstance(Class paramClass)
  {
    Method localMethod = findInterfaceMethod(paramClass);
    if (!localMethod.getName().equals("newInstance")) {
      throw new IllegalArgumentException(paramClass + " missing newInstance method");
    }
    return localMethod;
  }
  
  public static Method[] getPropertyMethods(PropertyDescriptor[] paramArrayOfPropertyDescriptor, boolean paramBoolean1, boolean paramBoolean2)
  {
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < paramArrayOfPropertyDescriptor.length; i++)
    {
      PropertyDescriptor localPropertyDescriptor = paramArrayOfPropertyDescriptor[i];
      if (paramBoolean1) {
        localHashSet.add(localPropertyDescriptor.getReadMethod());
      }
      if (paramBoolean2) {
        localHashSet.add(localPropertyDescriptor.getWriteMethod());
      }
    }
    localHashSet.remove(null);
    return (Method[])localHashSet.toArray(new Method[localHashSet.size()]);
  }
  
  public static PropertyDescriptor[] getBeanProperties(Class paramClass)
  {
    return getPropertiesHelper(paramClass, true, true);
  }
  
  public static PropertyDescriptor[] getBeanGetters(Class paramClass)
  {
    return getPropertiesHelper(paramClass, true, false);
  }
  
  public static PropertyDescriptor[] getBeanSetters(Class paramClass)
  {
    return getPropertiesHelper(paramClass, false, true);
  }
  
  private static PropertyDescriptor[] getPropertiesHelper(Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      BeanInfo localBeanInfo = Introspector.getBeanInfo(paramClass, Object.class);
      PropertyDescriptor[] arrayOfPropertyDescriptor = localBeanInfo.getPropertyDescriptors();
      if ((paramBoolean1) && (paramBoolean2)) {
        return arrayOfPropertyDescriptor;
      }
      ArrayList localArrayList = new ArrayList(arrayOfPropertyDescriptor.length);
      for (int i = 0; i < arrayOfPropertyDescriptor.length; i++)
      {
        PropertyDescriptor localPropertyDescriptor = arrayOfPropertyDescriptor[i];
        if (((paramBoolean1) && (localPropertyDescriptor.getReadMethod() != null)) || ((paramBoolean2) && (localPropertyDescriptor.getWriteMethod() != null))) {
          localArrayList.add(localPropertyDescriptor);
        }
      }
      return (PropertyDescriptor[])localArrayList.toArray(new PropertyDescriptor[localArrayList.size()]);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      throw new .CodeGenerationException(localIntrospectionException);
    }
  }
  
  public static Method findDeclaredMethod(Class paramClass, String paramString, Class[] paramArrayOfClass)
    throws NoSuchMethodException
  {
    Class localClass = paramClass;
    while (localClass != null) {
      try
      {
        return localClass.getDeclaredMethod(paramString, paramArrayOfClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localClass = localClass.getSuperclass();
      }
    }
    throw new NoSuchMethodException(paramString);
  }
  
  public static List addAllMethods(Class paramClass, List paramList)
  {
    paramList.addAll(Arrays.asList(paramClass.getDeclaredMethods()));
    Class localClass = paramClass.getSuperclass();
    if (localClass != null) {
      addAllMethods(localClass, paramList);
    }
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++) {
      addAllMethods(arrayOfClass[i], paramList);
    }
    return paramList;
  }
  
  public static List addAllInterfaces(Class paramClass, List paramList)
  {
    Class localClass = paramClass.getSuperclass();
    if (localClass != null)
    {
      paramList.addAll(Arrays.asList(paramClass.getInterfaces()));
      addAllInterfaces(localClass, paramList);
    }
    return paramList;
  }
  
  public static Method findInterfaceMethod(Class paramClass)
  {
    if (!paramClass.isInterface()) {
      throw new IllegalArgumentException(paramClass + " is not an interface");
    }
    Method[] arrayOfMethod = paramClass.getDeclaredMethods();
    if (arrayOfMethod.length != 1) {
      throw new IllegalArgumentException("expecting exactly 1 method in " + paramClass);
    }
    return arrayOfMethod[0];
  }
  
  public static Class defineClass(String paramString, byte[] paramArrayOfByte, ClassLoader paramClassLoader)
    throws Exception
  {
    Object[] arrayOfObject = { paramString, paramArrayOfByte, new Integer(0), new Integer(paramArrayOfByte.length), PROTECTION_DOMAIN };
    Class localClass = (Class)DEFINE_CLASS.invoke(paramClassLoader, arrayOfObject);
    Class.forName(paramString, true, paramClassLoader);
    return localClass;
  }
  
  public static int findPackageProtected(Class[] paramArrayOfClass)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      if (!Modifier.isPublic(paramArrayOfClass[i].getModifiers())) {
        return i;
      }
    }
    return 0;
  }
  
  public static .MethodInfo getMethodInfo(Member paramMember, int paramInt)
  {
    .Signature localSignature = getSignature(paramMember);
    new .MethodInfo()
    {
      private .ClassInfo ci;
      private final Member val$member;
      private final int val$modifiers;
      private final .Signature val$sig;
      
      public .ClassInfo getClassInfo()
      {
        if (this.ci == null) {
          this.ci = .ReflectUtils.getClassInfo(this.val$member.getDeclaringClass());
        }
        return this.ci;
      }
      
      public int getModifiers()
      {
        return this.val$modifiers;
      }
      
      public .Signature getSignature()
      {
        return this.val$sig;
      }
      
      public .Type[] getExceptionTypes()
      {
        return .ReflectUtils.getExceptionTypes(this.val$member);
      }
      
      public .Attribute getAttribute()
      {
        return null;
      }
    };
  }
  
  public static .MethodInfo getMethodInfo(Member paramMember)
  {
    return getMethodInfo(paramMember, paramMember.getModifiers());
  }
  
  public static .ClassInfo getClassInfo(Class paramClass)
  {
    .Type localType1 = .Type.getType(paramClass);
    .Type localType2 = paramClass.getSuperclass() == null ? null : .Type.getType(paramClass.getSuperclass());
    new .ClassInfo()
    {
      private final .Type val$type;
      private final .Type val$sc;
      private final Class val$clazz;
      
      public .Type getType()
      {
        return this.val$type;
      }
      
      public .Type getSuperType()
      {
        return this.val$sc;
      }
      
      public .Type[] getInterfaces()
      {
        return .TypeUtils.getTypes(this.val$clazz.getInterfaces());
      }
      
      public int getModifiers()
      {
        return this.val$clazz.getModifiers();
      }
    };
  }
  
  public static Method[] findMethods(String[] paramArrayOfString, Method[] paramArrayOfMethod)
  {
    HashMap localHashMap = new HashMap();
    for (int i = 0; i < paramArrayOfMethod.length; i++)
    {
      Method localMethod = paramArrayOfMethod[i];
      localHashMap.put(localMethod.getName() + .Type.getMethodDescriptor(localMethod), localMethod);
    }
    Method[] arrayOfMethod = new Method[paramArrayOfString.length / 2];
    for (int j = 0; j < arrayOfMethod.length; j++)
    {
      arrayOfMethod[j] = ((Method)localHashMap.get(paramArrayOfString[(j * 2)] + paramArrayOfString[(j * 2 + 1)]));
      if (arrayOfMethod[j] != null) {}
    }
    return arrayOfMethod;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          Class localClass = Class.forName("java.lang.ClassLoader");
          .ReflectUtils.access$002(localClass.getDeclaredMethod("defineClass", new Class[] { String.class, new byte[0].getClass(), Integer.TYPE, Integer.TYPE, ProtectionDomain.class }));
          .ReflectUtils.DEFINE_CLASS.setAccessible(true);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new .CodeGenerationException(localClassNotFoundException);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw new .CodeGenerationException(localNoSuchMethodException);
        }
        return null;
      }
    });
    CGLIB_PACKAGES = new String[] { "java.lang" };
    primitives.put("byte", Byte.TYPE);
    primitives.put("char", Character.TYPE);
    primitives.put("double", Double.TYPE);
    primitives.put("float", Float.TYPE);
    primitives.put("int", Integer.TYPE);
    primitives.put("long", Long.TYPE);
    primitives.put("short", Short.TYPE);
    primitives.put("boolean", Boolean.TYPE);
    transforms.put("byte", "B");
    transforms.put("char", "C");
    transforms.put("double", "D");
    transforms.put("float", "F");
    transforms.put("int", "I");
    transforms.put("long", "J");
    transforms.put("short", "S");
    transforms.put("boolean", "Z");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$ReflectUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */