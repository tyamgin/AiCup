package com.google.inject.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.Classes;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.inject.Qualifier;
import javax.inject.Scope;

public class Annotations
{
  private static final LoadingCache cache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader()
  {
    public Annotation load(Class paramAnonymousClass)
    {
      return Annotations.generateAnnotationImpl(paramAnonymousClass);
    }
  });
  private static final Joiner.MapJoiner JOINER = Joiner.on(", ").withKeyValueSeparator("=");
  private static final Function DEEP_TO_STRING_FN = new Function()
  {
    public String apply(Object paramAnonymousObject)
    {
      String str = Arrays.deepToString(new Object[] { paramAnonymousObject });
      return str.substring(1, str.length() - 1);
    }
  };
  private static final AnnotationChecker scopeChecker = new AnnotationChecker(Arrays.asList(new Class[] { ScopeAnnotation.class, Scope.class }));
  private static final AnnotationChecker bindingAnnotationChecker = new AnnotationChecker(Arrays.asList(new Class[] { BindingAnnotation.class, Qualifier.class }));
  
  public static boolean isMarker(Class paramClass)
  {
    return paramClass.getDeclaredMethods().length == 0;
  }
  
  public static boolean isAllDefaultMethods(Class paramClass)
  {
    boolean bool = false;
    for (Method localMethod : paramClass.getDeclaredMethods())
    {
      bool = true;
      if (localMethod.getDefaultValue() == null) {
        return false;
      }
    }
    return bool;
  }
  
  public static Annotation generateAnnotation(Class paramClass)
  {
    Preconditions.checkState(isAllDefaultMethods(paramClass), "%s is not all default methods", new Object[] { paramClass });
    return (Annotation)cache.getUnchecked(paramClass);
  }
  
  private static Annotation generateAnnotationImpl(Class paramClass)
  {
    final ImmutableMap localImmutableMap = resolveMembers(paramClass);
    (Annotation)paramClass.cast(Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, new InvocationHandler()
    {
      public Object invoke(Object paramAnonymousObject, Method paramAnonymousMethod, Object[] paramAnonymousArrayOfObject)
        throws Exception
      {
        String str = paramAnonymousMethod.getName();
        if (str.equals("annotationType")) {
          return this.val$annotationType;
        }
        if (str.equals("toString")) {
          return Annotations.annotationToString(this.val$annotationType, localImmutableMap);
        }
        if (str.equals("hashCode")) {
          return Integer.valueOf(Annotations.annotationHashCode(this.val$annotationType, localImmutableMap));
        }
        if (str.equals("equals")) {
          return Boolean.valueOf(Annotations.annotationEquals(this.val$annotationType, localImmutableMap, paramAnonymousArrayOfObject[0]));
        }
        return localImmutableMap.get(str);
      }
    }));
  }
  
  private static ImmutableMap resolveMembers(Class paramClass)
  {
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    for (Method localMethod : paramClass.getDeclaredMethods()) {
      localBuilder.put(localMethod.getName(), localMethod.getDefaultValue());
    }
    return localBuilder.build();
  }
  
  private static boolean annotationEquals(Class paramClass, Map paramMap, Object paramObject)
    throws Exception
  {
    if (!paramClass.isInstance(paramObject)) {
      return false;
    }
    for (Method localMethod : paramClass.getDeclaredMethods())
    {
      String str = localMethod.getName();
      if (!Arrays.deepEquals(new Object[] { localMethod.invoke(paramObject, new Object[0]) }, new Object[] { paramMap.get(str) })) {
        return false;
      }
    }
    return true;
  }
  
  private static int annotationHashCode(Class paramClass, Map paramMap)
    throws Exception
  {
    int i = 0;
    for (Method localMethod : paramClass.getDeclaredMethods())
    {
      String str = localMethod.getName();
      Object localObject = paramMap.get(str);
      i += (127 * str.hashCode() ^ Arrays.deepHashCode(new Object[] { localObject }) - 31);
    }
    return i;
  }
  
  private static String annotationToString(Class paramClass, Map paramMap)
    throws Exception
  {
    StringBuilder localStringBuilder = new StringBuilder().append("@").append(paramClass.getName()).append("(");
    JOINER.appendTo(localStringBuilder, Maps.transformValues(paramMap, DEEP_TO_STRING_FN));
    return ")";
  }
  
  public static boolean isRetainedAtRuntime(Class paramClass)
  {
    Retention localRetention = (Retention)paramClass.getAnnotation(Retention.class);
    return (localRetention != null) && (localRetention.value() == RetentionPolicy.RUNTIME);
  }
  
  public static Class findScopeAnnotation(Errors paramErrors, Class paramClass)
  {
    return findScopeAnnotation(paramErrors, paramClass.getAnnotations());
  }
  
  public static Class findScopeAnnotation(Errors paramErrors, Annotation[] paramArrayOfAnnotation)
  {
    Object localObject = null;
    for (Annotation localAnnotation : paramArrayOfAnnotation)
    {
      Class localClass = localAnnotation.annotationType();
      if (isScopeAnnotation(localClass)) {
        if (localObject != null) {
          paramErrors.duplicateScopeAnnotations((Class)localObject, localClass);
        } else {
          localObject = localClass;
        }
      }
    }
    return (Class)localObject;
  }
  
  static boolean containsComponentAnnotation(Annotation[] paramArrayOfAnnotation)
  {
    for (Annotation localAnnotation : paramArrayOfAnnotation) {
      if (localAnnotation.annotationType().getSimpleName().equals("Component")) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isScopeAnnotation(Class paramClass)
  {
    return scopeChecker.hasAnnotations(paramClass);
  }
  
  public static void checkForMisplacedScopeAnnotations(Class paramClass, Object paramObject, Errors paramErrors)
  {
    if (Classes.isConcrete(paramClass)) {
      return;
    }
    Class localClass = findScopeAnnotation(paramErrors, paramClass);
    if ((localClass != null) && (!containsComponentAnnotation(paramClass.getAnnotations()))) {
      paramErrors.withSource(paramClass).scopeAnnotationOnAbstractType(localClass, paramClass, paramObject);
    }
  }
  
  public static Key getKey(TypeLiteral paramTypeLiteral, Member paramMember, Annotation[] paramArrayOfAnnotation, Errors paramErrors)
    throws ErrorsException
  {
    int i = paramErrors.size();
    Annotation localAnnotation = findBindingAnnotation(paramErrors, paramMember, paramArrayOfAnnotation);
    paramErrors.throwIfNewErrors(i);
    return localAnnotation == null ? Key.get(paramTypeLiteral) : Key.get(paramTypeLiteral, localAnnotation);
  }
  
  public static Annotation findBindingAnnotation(Errors paramErrors, Member paramMember, Annotation[] paramArrayOfAnnotation)
  {
    Object localObject = null;
    for (Annotation localAnnotation : paramArrayOfAnnotation)
    {
      Class localClass = localAnnotation.annotationType();
      if (isBindingAnnotation(localClass)) {
        if (localObject != null) {
          paramErrors.duplicateBindingAnnotations(paramMember, ((Annotation)localObject).annotationType(), localClass);
        } else {
          localObject = localAnnotation;
        }
      }
    }
    return (Annotation)localObject;
  }
  
  public static boolean isBindingAnnotation(Class paramClass)
  {
    return bindingAnnotationChecker.hasAnnotations(paramClass);
  }
  
  public static Annotation canonicalizeIfNamed(Annotation paramAnnotation)
  {
    if ((paramAnnotation instanceof javax.inject.Named)) {
      return Names.named(((javax.inject.Named)paramAnnotation).value());
    }
    return paramAnnotation;
  }
  
  public static Class canonicalizeIfNamed(Class paramClass)
  {
    if (paramClass == javax.inject.Named.class) {
      return com.google.inject.name.Named.class;
    }
    return paramClass;
  }
  
  static class AnnotationChecker
  {
    private final Collection annotationTypes;
    private CacheLoader hasAnnotations = new CacheLoader()
    {
      public Boolean load(Class paramAnonymousClass)
      {
        for (Annotation localAnnotation : paramAnonymousClass.getAnnotations()) {
          if (Annotations.AnnotationChecker.this.annotationTypes.contains(localAnnotation.annotationType())) {
            return Boolean.valueOf(true);
          }
        }
        return Boolean.valueOf(false);
      }
    };
    final LoadingCache cache = CacheBuilder.newBuilder().weakKeys().build(this.hasAnnotations);
    
    AnnotationChecker(Collection paramCollection)
    {
      this.annotationTypes = paramCollection;
    }
    
    boolean hasAnnotations(Class paramClass)
    {
      return ((Boolean)this.cache.getUnchecked(paramClass)).booleanValue();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Annotations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */