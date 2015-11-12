package com.google.inject.internal;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import com.google.inject.util.Modules;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ProviderMethodsModule
  implements Module
{
  private static ModuleAnnotatedMethodScanner PROVIDES_BUILDER = new ModuleAnnotatedMethodScanner()
  {
    public Key prepareMethod(Binder paramAnonymousBinder, Annotation paramAnonymousAnnotation, Key paramAnonymousKey, InjectionPoint paramAnonymousInjectionPoint)
    {
      return paramAnonymousKey;
    }
    
    public Set annotationClasses()
    {
      return ImmutableSet.of(Provides.class);
    }
  };
  private final Object delegate;
  private final TypeLiteral typeLiteral;
  private final boolean skipFastClassGeneration;
  private final ModuleAnnotatedMethodScanner scanner;
  
  private ProviderMethodsModule(Object paramObject, boolean paramBoolean, ModuleAnnotatedMethodScanner paramModuleAnnotatedMethodScanner)
  {
    this.delegate = Preconditions.checkNotNull(paramObject, "delegate");
    this.typeLiteral = TypeLiteral.get(this.delegate.getClass());
    this.skipFastClassGeneration = paramBoolean;
    this.scanner = paramModuleAnnotatedMethodScanner;
  }
  
  public static Module forModule(Module paramModule)
  {
    return forObject(paramModule, false, PROVIDES_BUILDER);
  }
  
  public static Module forModule(Object paramObject, ModuleAnnotatedMethodScanner paramModuleAnnotatedMethodScanner)
  {
    return forObject(paramObject, false, paramModuleAnnotatedMethodScanner);
  }
  
  public static Module forObject(Object paramObject)
  {
    return forObject(paramObject, true, PROVIDES_BUILDER);
  }
  
  private static Module forObject(Object paramObject, boolean paramBoolean, ModuleAnnotatedMethodScanner paramModuleAnnotatedMethodScanner)
  {
    if ((paramObject instanceof ProviderMethodsModule)) {
      return Modules.EMPTY_MODULE;
    }
    return new ProviderMethodsModule(paramObject, paramBoolean, paramModuleAnnotatedMethodScanner);
  }
  
  public Object getDelegateModule()
  {
    return this.delegate;
  }
  
  public synchronized void configure(Binder paramBinder)
  {
    Iterator localIterator = getProviderMethods(paramBinder).iterator();
    while (localIterator.hasNext())
    {
      ProviderMethod localProviderMethod = (ProviderMethod)localIterator.next();
      localProviderMethod.configure(paramBinder);
    }
  }
  
  /* Error */
  public List getProviderMethods(Binder arg1)
  {
    // Byte code:
    //   0: invokestatic 60	com/google/common/collect/Lists:newArrayList	()Ljava/util/ArrayList;
    //   3: astore_2
    //   4: invokestatic 58	com/google/common/collect/HashMultimap:create	()Lcom/google/common/collect/HashMultimap;
    //   7: astore_3
    //   8: aload_0
    //   9: getfield 48	com/google/inject/internal/ProviderMethodsModule:delegate	Ljava/lang/Object;
    //   12: invokevirtual 93	java/lang/Object:getClass	()Ljava/lang/Class;
    //   15: astore 4
    //   17: aload 4
    //   19: ldc 34
    //   21: if_acmpeq +133 -> 154
    //   24: aload 4
    //   26: invokevirtual 87	java/lang/Class:getDeclaredMethods	()[Ljava/lang/reflect/Method;
    //   29: astore 5
    //   31: aload 5
    //   33: arraylength
    //   34: istore 6
    //   36: iconst_0
    //   37: istore 7
    //   39: iload 7
    //   41: iload 6
    //   43: if_icmpge +101 -> 144
    //   46: aload 5
    //   48: iload 7
    //   50: aaload
    //   51: astore 8
    //   53: aload 8
    //   55: invokevirtual 105	java/lang/reflect/Method:getModifiers	()I
    //   58: bipush 10
    //   60: iand
    //   61: ifne +38 -> 99
    //   64: aload 8
    //   66: invokevirtual 106	java/lang/reflect/Method:isBridge	()Z
    //   69: ifne +30 -> 99
    //   72: aload 8
    //   74: invokevirtual 107	java/lang/reflect/Method:isSynthetic	()Z
    //   77: ifne +22 -> 99
    //   80: aload_3
    //   81: new 27	com/google/inject/internal/ProviderMethodsModule$Signature
    //   84: dup
    //   85: aload_0
    //   86: aload 8
    //   88: invokespecial 81	com/google/inject/internal/ProviderMethodsModule$Signature:<init>	(Lcom/google/inject/internal/ProviderMethodsModule;Ljava/lang/reflect/Method;)V
    //   91: aload 8
    //   93: invokeinterface 112 3 0
    //   98: pop
    //   99: aload_0
    //   100: aload_1
    //   101: aload 8
    //   103: invokespecial 78	com/google/inject/internal/ProviderMethodsModule:isProvider	(Lcom/google/inject/Binder;Ljava/lang/reflect/Method;)Lcom/google/common/base/Optional;
    //   106: astore 9
    //   108: aload 9
    //   110: invokevirtual 56	com/google/common/base/Optional:isPresent	()Z
    //   113: ifeq +25 -> 138
    //   116: aload_2
    //   117: aload_0
    //   118: aload_1
    //   119: aload 8
    //   121: aload 9
    //   123: invokevirtual 55	com/google/common/base/Optional:get	()Ljava/lang/Object;
    //   126: checkcast 38	java/lang/annotation/Annotation
    //   129: invokespecial 74	com/google/inject/internal/ProviderMethodsModule:createProviderMethod	(Lcom/google/inject/Binder;Ljava/lang/reflect/Method;Ljava/lang/annotation/Annotation;)Lcom/google/inject/internal/ProviderMethod;
    //   132: invokeinterface 122 2 0
    //   137: pop
    //   138: iinc 7 1
    //   141: goto -102 -> 39
    //   144: aload 4
    //   146: invokevirtual 89	java/lang/Class:getSuperclass	()Ljava/lang/Class;
    //   149: astore 4
    //   151: goto -134 -> 17
    //   154: aload_2
    //   155: invokeinterface 123 1 0
    //   160: astore 4
    //   162: aload 4
    //   164: invokeinterface 120 1 0
    //   169: ifeq +261 -> 430
    //   172: aload 4
    //   174: invokeinterface 121 1 0
    //   179: checkcast 24	com/google/inject/internal/ProviderMethod
    //   182: astore 5
    //   184: aload 5
    //   186: invokevirtual 72	com/google/inject/internal/ProviderMethod:getMethod	()Ljava/lang/reflect/Method;
    //   189: astore 6
    //   191: aload_3
    //   192: new 27	com/google/inject/internal/ProviderMethodsModule$Signature
    //   195: dup
    //   196: aload_0
    //   197: aload 6
    //   199: invokespecial 81	com/google/inject/internal/ProviderMethodsModule$Signature:<init>	(Lcom/google/inject/internal/ProviderMethodsModule;Ljava/lang/reflect/Method;)V
    //   202: invokeinterface 111 2 0
    //   207: invokeinterface 119 1 0
    //   212: astore 7
    //   214: aload 7
    //   216: invokeinterface 120 1 0
    //   221: ifeq +206 -> 427
    //   224: aload 7
    //   226: invokeinterface 121 1 0
    //   231: checkcast 40	java/lang/reflect/Method
    //   234: astore 8
    //   236: aload 8
    //   238: invokevirtual 104	java/lang/reflect/Method:getDeclaringClass	()Ljava/lang/Class;
    //   241: aload 6
    //   243: invokevirtual 104	java/lang/reflect/Method:getDeclaringClass	()Ljava/lang/Class;
    //   246: invokevirtual 90	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   249: ifeq +6 -> 255
    //   252: goto -38 -> 214
    //   255: aload 8
    //   257: aload 6
    //   259: invokestatic 79	com/google/inject/internal/ProviderMethodsModule:overrides	(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Z
    //   262: ifeq +162 -> 424
    //   265: aload 5
    //   267: invokevirtual 71	com/google/inject/internal/ProviderMethod:getAnnotation	()Ljava/lang/annotation/Annotation;
    //   270: invokeinterface 118 1 0
    //   275: ldc 20
    //   277: if_acmpne +8 -> 285
    //   280: ldc 5
    //   282: goto +43 -> 325
    //   285: ldc 4
    //   287: aload 5
    //   289: invokevirtual 71	com/google/inject/internal/ProviderMethod:getAnnotation	()Ljava/lang/annotation/Annotation;
    //   292: invokeinterface 118 1 0
    //   297: invokevirtual 86	java/lang/Class:getCanonicalName	()Ljava/lang/String;
    //   300: invokestatic 98	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   303: dup
    //   304: invokevirtual 97	java/lang/String:length	()I
    //   307: ifeq +9 -> 316
    //   310: invokevirtual 96	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   313: goto +12 -> 325
    //   316: pop
    //   317: new 35	java/lang/String
    //   320: dup_x1
    //   321: swap
    //   322: invokespecial 95	java/lang/String:<init>	(Ljava/lang/String;)V
    //   325: astore 9
    //   327: aload_1
    //   328: aload 9
    //   330: invokestatic 98	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   333: invokestatic 98	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   336: astore 10
    //   338: aload 9
    //   340: invokestatic 98	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   343: invokestatic 98	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   346: astore 11
    //   348: new 36	java/lang/StringBuilder
    //   351: dup
    //   352: bipush 67
    //   354: aload 10
    //   356: invokevirtual 97	java/lang/String:length	()I
    //   359: iadd
    //   360: aload 11
    //   362: invokevirtual 97	java/lang/String:length	()I
    //   365: iadd
    //   366: invokespecial 99	java/lang/StringBuilder:<init>	(I)V
    //   369: ldc 7
    //   371: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   374: aload 10
    //   376: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   379: ldc 3
    //   381: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: ldc 1
    //   386: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   389: aload 11
    //   391: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   394: ldc 2
    //   396: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   399: invokevirtual 101	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   402: iconst_2
    //   403: anewarray 34	java/lang/Object
    //   406: dup
    //   407: iconst_0
    //   408: aload 6
    //   410: aastore
    //   411: dup
    //   412: iconst_1
    //   413: aload 8
    //   415: aastore
    //   416: invokeinterface 114 3 0
    //   421: goto +6 -> 427
    //   424: goto -210 -> 214
    //   427: goto -265 -> 162
    //   430: aload_2
    //   431: areturn
  }
  
  private Optional isProvider(Binder paramBinder, Method paramMethod)
  {
    if ((paramMethod.isBridge()) || (paramMethod.isSynthetic())) {
      return Optional.absent();
    }
    Object localObject = null;
    Iterator localIterator = this.scanner.annotationClasses().iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      Annotation localAnnotation = paramMethod.getAnnotation(localClass);
      if (localAnnotation != null)
      {
        if (localObject != null)
        {
          paramBinder.addError("More than one annotation claimed by %s on method %s. Methods can only have one annotation claimed per scanner.", new Object[] { this.scanner, paramMethod });
          return Optional.absent();
        }
        localObject = localAnnotation;
      }
    }
    return Optional.fromNullable(localObject);
  }
  
  private static boolean overrides(Method paramMethod1, Method paramMethod2)
  {
    int i = paramMethod2.getModifiers();
    if ((Modifier.isPublic(i)) || (Modifier.isProtected(i))) {
      return true;
    }
    if (Modifier.isPrivate(i)) {
      return false;
    }
    return paramMethod1.getDeclaringClass().getPackage().equals(paramMethod2.getDeclaringClass().getPackage());
  }
  
  private ProviderMethod createProviderMethod(Binder paramBinder, Method paramMethod, Annotation paramAnnotation)
  {
    paramBinder = paramBinder.withSource(paramMethod);
    Errors localErrors = new Errors(paramMethod);
    InjectionPoint localInjectionPoint = InjectionPoint.forMethod(paramMethod, this.typeLiteral);
    List localList = localInjectionPoint.getDependencies();
    ArrayList localArrayList = Lists.newArrayList();
    Object localObject1 = localInjectionPoint.getDependencies().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Dependency)((Iterator)localObject1).next();
      localArrayList.add(paramBinder.getProvider((Dependency)localObject2));
    }
    localObject1 = this.typeLiteral.getReturnType(paramMethod);
    Object localObject2 = getKey(localErrors, (TypeLiteral)localObject1, paramMethod, paramMethod.getAnnotations());
    try
    {
      localObject2 = this.scanner.prepareMethod(paramBinder, paramAnnotation, (Key)localObject2, localInjectionPoint);
    }
    catch (Throwable localThrowable)
    {
      paramBinder.addError(localThrowable);
    }
    Class localClass = Annotations.findScopeAnnotation(localErrors, paramMethod.getAnnotations());
    Iterator localIterator = localErrors.getMessages().iterator();
    while (localIterator.hasNext())
    {
      Message localMessage = (Message)localIterator.next();
      paramBinder.addError(localMessage);
    }
    return ProviderMethod.create((Key)localObject2, paramMethod, this.delegate, ImmutableSet.copyOf(localList), localArrayList, localClass, this.skipFastClassGeneration, paramAnnotation);
  }
  
  Key getKey(Errors paramErrors, TypeLiteral paramTypeLiteral, Member paramMember, Annotation[] paramArrayOfAnnotation)
  {
    Annotation localAnnotation = Annotations.findBindingAnnotation(paramErrors, paramMember, paramArrayOfAnnotation);
    return localAnnotation == null ? Key.get(paramTypeLiteral) : Key.get(paramTypeLiteral, localAnnotation);
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof ProviderMethodsModule)) && (((ProviderMethodsModule)paramObject).delegate == this.delegate) && (((ProviderMethodsModule)paramObject).scanner == this.scanner);
  }
  
  public int hashCode()
  {
    return this.delegate.hashCode();
  }
  
  private final class Signature
  {
    final Class[] parameters;
    final String name;
    final int hashCode;
    
    Signature(Method paramMethod)
    {
      this.name = paramMethod.getName();
      List localList = ProviderMethodsModule.this.typeLiteral.getParameterTypes(paramMethod);
      this.parameters = new Class[localList.size()];
      int i = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        TypeLiteral localTypeLiteral = (TypeLiteral)localIterator.next();
        this.parameters[i] = localTypeLiteral.getRawType();
      }
      this.hashCode = (this.name.hashCode() + 31 * Arrays.hashCode(this.parameters));
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Signature))
      {
        Signature localSignature = (Signature)paramObject;
        return (localSignature.name.equals(this.name)) && (Arrays.equals(this.parameters, localSignature.parameters));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.hashCode;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProviderMethodsModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */