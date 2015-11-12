package com.google.inject.spi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.Nullability;
import com.google.inject.internal.util.Classes;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class InjectionPoint
{
  private static final Logger logger = Logger.getLogger(InjectionPoint.class.getName());
  private final boolean optional;
  private final Member member;
  private final TypeLiteral declaringType;
  private final ImmutableList dependencies;
  
  InjectionPoint(TypeLiteral paramTypeLiteral, Method paramMethod, boolean paramBoolean)
  {
    this.member = paramMethod;
    this.declaringType = paramTypeLiteral;
    this.optional = paramBoolean;
    this.dependencies = forMember(paramMethod, paramTypeLiteral, paramMethod.getParameterAnnotations());
  }
  
  InjectionPoint(TypeLiteral paramTypeLiteral, Constructor paramConstructor)
  {
    this.member = paramConstructor;
    this.declaringType = paramTypeLiteral;
    this.optional = false;
    this.dependencies = forMember(paramConstructor, paramTypeLiteral, paramConstructor.getParameterAnnotations());
  }
  
  InjectionPoint(TypeLiteral paramTypeLiteral, Field paramField, boolean paramBoolean)
  {
    this.member = paramField;
    this.declaringType = paramTypeLiteral;
    this.optional = paramBoolean;
    Annotation[] arrayOfAnnotation = paramField.getAnnotations();
    Errors localErrors = new Errors(paramField);
    Key localKey = null;
    try
    {
      localKey = Annotations.getKey(paramTypeLiteral.getFieldType(paramField), paramField, arrayOfAnnotation, localErrors);
    }
    catch (ConfigurationException localConfigurationException)
    {
      localErrors.merge(localConfigurationException.getErrorMessages());
    }
    catch (ErrorsException localErrorsException)
    {
      localErrors.merge(localErrorsException.getErrors());
    }
    localErrors.throwConfigurationExceptionIfErrorsExist();
    this.dependencies = ImmutableList.of(newDependency(localKey, Nullability.allowsNull(arrayOfAnnotation), -1));
  }
  
  private ImmutableList forMember(Member paramMember, TypeLiteral paramTypeLiteral, Annotation[][] paramArrayOfAnnotation)
  {
    Errors localErrors = new Errors(paramMember);
    Iterator localIterator1 = Arrays.asList(paramArrayOfAnnotation).iterator();
    ArrayList localArrayList = Lists.newArrayList();
    int i = 0;
    Iterator localIterator2 = paramTypeLiteral.getParameterTypes(paramMember).iterator();
    while (localIterator2.hasNext())
    {
      TypeLiteral localTypeLiteral = (TypeLiteral)localIterator2.next();
      try
      {
        Annotation[] arrayOfAnnotation = (Annotation[])localIterator1.next();
        Key localKey = Annotations.getKey(localTypeLiteral, paramMember, arrayOfAnnotation, localErrors);
        localArrayList.add(newDependency(localKey, Nullability.allowsNull(arrayOfAnnotation), i));
        i++;
      }
      catch (ConfigurationException localConfigurationException)
      {
        localErrors.merge(localConfigurationException.getErrorMessages());
      }
      catch (ErrorsException localErrorsException)
      {
        localErrors.merge(localErrorsException.getErrors());
      }
    }
    localErrors.throwConfigurationExceptionIfErrorsExist();
    return ImmutableList.copyOf(localArrayList);
  }
  
  private Dependency newDependency(Key paramKey, boolean paramBoolean, int paramInt)
  {
    return new Dependency(this, paramKey, paramBoolean, paramInt);
  }
  
  public Member getMember()
  {
    return this.member;
  }
  
  public List getDependencies()
  {
    return this.dependencies;
  }
  
  public boolean isOptional()
  {
    return this.optional;
  }
  
  public boolean isToolable()
  {
    return ((AnnotatedElement)this.member).isAnnotationPresent(Toolable.class);
  }
  
  public TypeLiteral getDeclaringType()
  {
    return this.declaringType;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof InjectionPoint)) && (this.member.equals(((InjectionPoint)paramObject).member)) && (this.declaringType.equals(((InjectionPoint)paramObject).declaringType));
  }
  
  public int hashCode()
  {
    return this.member.hashCode() ^ this.declaringType.hashCode();
  }
  
  public String toString()
  {
    return Classes.toString(this.member);
  }
  
  public static InjectionPoint forConstructor(Constructor paramConstructor)
  {
    return new InjectionPoint(TypeLiteral.get(paramConstructor.getDeclaringClass()), paramConstructor);
  }
  
  public static InjectionPoint forConstructor(Constructor paramConstructor, TypeLiteral paramTypeLiteral)
  {
    if (paramTypeLiteral.getRawType() != paramConstructor.getDeclaringClass()) {
      new Errors(paramTypeLiteral).constructorNotDefinedByType(paramConstructor, paramTypeLiteral).throwConfigurationExceptionIfErrorsExist();
    }
    return new InjectionPoint(paramTypeLiteral, paramConstructor);
  }
  
  public static InjectionPoint forConstructorOf(TypeLiteral paramTypeLiteral)
  {
    Class localClass = MoreTypes.getRawType(paramTypeLiteral.getType());
    Errors localErrors = new Errors(localClass);
    Object localObject1 = null;
    for (Constructor localConstructor : localClass.getDeclaredConstructors())
    {
      com.google.inject.Inject localInject = (com.google.inject.Inject)localConstructor.getAnnotation(com.google.inject.Inject.class);
      boolean bool;
      if (localInject == null)
      {
        javax.inject.Inject localInject1 = (javax.inject.Inject)localConstructor.getAnnotation(javax.inject.Inject.class);
        if (localInject1 == null) {
          continue;
        }
        bool = false;
      }
      else
      {
        bool = localInject.optional();
      }
      if (bool) {
        localErrors.optionalConstructor(localConstructor);
      }
      if (localObject1 != null) {
        localErrors.tooManyConstructors(localClass);
      }
      localObject1 = localConstructor;
      checkForMisplacedBindingAnnotations((Member)localObject1, localErrors);
    }
    localErrors.throwConfigurationExceptionIfErrorsExist();
    if (localObject1 != null) {
      return new InjectionPoint(paramTypeLiteral, (Constructor)localObject1);
    }
    try
    {
      ??? = localClass.getDeclaredConstructor(new Class[0]);
      if ((Modifier.isPrivate(((Constructor)???).getModifiers())) && (!Modifier.isPrivate(localClass.getModifiers())))
      {
        localErrors.missingConstructor(localClass);
        throw new ConfigurationException(localErrors.getMessages());
      }
      checkForMisplacedBindingAnnotations((Member)???, localErrors);
      return new InjectionPoint(paramTypeLiteral, (Constructor)???);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localErrors.missingConstructor(localClass);
      throw new ConfigurationException(localErrors.getMessages());
    }
  }
  
  public static InjectionPoint forConstructorOf(Class paramClass)
  {
    return forConstructorOf(TypeLiteral.get(paramClass));
  }
  
  public static InjectionPoint forMethod(Method paramMethod, TypeLiteral paramTypeLiteral)
  {
    return new InjectionPoint(paramTypeLiteral, paramMethod, false);
  }
  
  public static Set forStaticMethodsAndFields(TypeLiteral paramTypeLiteral)
  {
    Errors localErrors = new Errors();
    Set localSet;
    if (paramTypeLiteral.getRawType().isInterface())
    {
      localErrors.staticInjectionOnInterface(paramTypeLiteral.getRawType());
      localSet = null;
    }
    else
    {
      localSet = getInjectionPoints(paramTypeLiteral, true, localErrors);
    }
    if (localErrors.hasErrors()) {
      throw new ConfigurationException(localErrors.getMessages()).withPartialValue(localSet);
    }
    return localSet;
  }
  
  public static Set forStaticMethodsAndFields(Class paramClass)
  {
    return forStaticMethodsAndFields(TypeLiteral.get(paramClass));
  }
  
  public static Set forInstanceMethodsAndFields(TypeLiteral paramTypeLiteral)
  {
    Errors localErrors = new Errors();
    Set localSet = getInjectionPoints(paramTypeLiteral, false, localErrors);
    if (localErrors.hasErrors()) {
      throw new ConfigurationException(localErrors.getMessages()).withPartialValue(localSet);
    }
    return localSet;
  }
  
  public static Set forInstanceMethodsAndFields(Class paramClass)
  {
    return forInstanceMethodsAndFields(TypeLiteral.get(paramClass));
  }
  
  private static boolean checkForMisplacedBindingAnnotations(Member paramMember, Errors paramErrors)
  {
    Annotation localAnnotation = Annotations.findBindingAnnotation(paramErrors, paramMember, ((AnnotatedElement)paramMember).getAnnotations());
    if (localAnnotation == null) {
      return false;
    }
    if ((paramMember instanceof Method)) {
      try
      {
        if (paramMember.getDeclaringClass().getDeclaredField(paramMember.getName()) != null) {
          return false;
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException) {}
    }
    paramErrors.misplacedBindingAnnotation(paramMember, localAnnotation);
    return true;
  }
  
  static Annotation getAtInject(AnnotatedElement paramAnnotatedElement)
  {
    Annotation localAnnotation = paramAnnotatedElement.getAnnotation(javax.inject.Inject.class);
    return localAnnotation == null ? paramAnnotatedElement.getAnnotation(com.google.inject.Inject.class) : localAnnotation;
  }
  
  private static Set getInjectionPoints(TypeLiteral paramTypeLiteral, boolean paramBoolean, Errors paramErrors)
  {
    InjectableMembers localInjectableMembers = new InjectableMembers();
    OverrideIndex localOverrideIndex = null;
    List localList = hierarchyFor(paramTypeLiteral);
    int i = localList.size() - 1;
    for (int j = i; j >= 0; j--)
    {
      if ((localOverrideIndex != null) && (j < i)) {
        if (j == 0) {
          localOverrideIndex.position = Position.BOTTOM;
        } else {
          localOverrideIndex.position = Position.MIDDLE;
        }
      }
      localObject1 = (TypeLiteral)localList.get(j);
      AnnotatedElement localAnnotatedElement;
      Annotation localAnnotation;
      Object localObject3;
      for (localAnnotatedElement : ((TypeLiteral)localObject1).getRawType().getDeclaredFields()) {
        if (Modifier.isStatic(localAnnotatedElement.getModifiers()) == paramBoolean)
        {
          localAnnotation = getAtInject(localAnnotatedElement);
          if (localAnnotation != null)
          {
            localObject3 = new InjectableField((TypeLiteral)localObject1, localAnnotatedElement, localAnnotation);
            if ((((InjectableField)localObject3).jsr330) && (Modifier.isFinal(localAnnotatedElement.getModifiers()))) {
              paramErrors.cannotInjectFinalField(localAnnotatedElement);
            }
            localInjectableMembers.add((InjectableMember)localObject3);
          }
        }
      }
      for (localAnnotatedElement : ((TypeLiteral)localObject1).getRawType().getDeclaredMethods()) {
        if (isEligibleForInjection(localAnnotatedElement, paramBoolean))
        {
          localAnnotation = getAtInject(localAnnotatedElement);
          if (localAnnotation != null)
          {
            localObject3 = new InjectableMethod((TypeLiteral)localObject1, localAnnotatedElement, localAnnotation);
            if ((checkForMisplacedBindingAnnotations(localAnnotatedElement, paramErrors)) || (!isValidMethod((InjectableMethod)localObject3, paramErrors)))
            {
              if (localOverrideIndex != null)
              {
                boolean bool2 = localOverrideIndex.removeIfOverriddenBy(localAnnotatedElement, false, (InjectableMethod)localObject3);
                if (bool2) {
                  logger.log(Level.WARNING, "Method: {0} is not a valid injectable method (because it either has misplaced binding annotations or specifies type parameters) but is overriding a method that is valid. Because it is not valid, the method will not be injected. To fix this, make the method a valid injectable method.", localAnnotatedElement);
                }
              }
            }
            else if (paramBoolean)
            {
              localInjectableMembers.add((InjectableMember)localObject3);
            }
            else
            {
              if (localOverrideIndex == null) {
                localOverrideIndex = new OverrideIndex(localInjectableMembers);
              } else {
                localOverrideIndex.removeIfOverriddenBy(localAnnotatedElement, true, (InjectableMethod)localObject3);
              }
              localOverrideIndex.add((InjectableMethod)localObject3);
            }
          }
          else if (localOverrideIndex != null)
          {
            boolean bool1 = localOverrideIndex.removeIfOverriddenBy(localAnnotatedElement, false, null);
            if (bool1) {
              logger.log(Level.WARNING, "Method: {0} is not annotated with @Inject but is overriding a method that is annotated with @javax.inject.Inject.  Because it is not annotated with @Inject, the method will not be injected. To fix this, annotate the method with @Inject.", localAnnotatedElement);
            }
          }
        }
      }
    }
    if (localInjectableMembers.isEmpty()) {
      return Collections.emptySet();
    }
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    for (Object localObject1 = localInjectableMembers.head; localObject1 != null; localObject1 = ((InjectableMember)localObject1).next) {
      try
      {
        localBuilder.add(((InjectableMember)localObject1).toInjectionPoint());
      }
      catch (ConfigurationException localConfigurationException)
      {
        if (!((InjectableMember)localObject1).optional) {
          paramErrors.merge(localConfigurationException.getErrorMessages());
        }
      }
    }
    return localBuilder.build();
  }
  
  private static boolean isEligibleForInjection(Method paramMethod, boolean paramBoolean)
  {
    return (Modifier.isStatic(paramMethod.getModifiers()) == paramBoolean) && (!paramMethod.isBridge()) && (!paramMethod.isSynthetic());
  }
  
  private static boolean isValidMethod(InjectableMethod paramInjectableMethod, Errors paramErrors)
  {
    boolean bool = true;
    if (paramInjectableMethod.jsr330)
    {
      Method localMethod = paramInjectableMethod.method;
      if (Modifier.isAbstract(localMethod.getModifiers()))
      {
        paramErrors.cannotInjectAbstractMethod(localMethod);
        bool = false;
      }
      if (localMethod.getTypeParameters().length > 0)
      {
        paramErrors.cannotInjectMethodWithTypeParameters(localMethod);
        bool = false;
      }
    }
    return bool;
  }
  
  private static List hierarchyFor(TypeLiteral paramTypeLiteral)
  {
    ArrayList localArrayList = new ArrayList();
    for (TypeLiteral localTypeLiteral = paramTypeLiteral; localTypeLiteral.getRawType() != Object.class; localTypeLiteral = localTypeLiteral.getSupertype(localTypeLiteral.getRawType().getSuperclass())) {
      localArrayList.add(localTypeLiteral);
    }
    return localArrayList;
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
  
  static class Signature
  {
    final String name;
    final Class[] parameterTypes;
    final int hash;
    
    Signature(Method paramMethod)
    {
      this.name = paramMethod.getName();
      this.parameterTypes = paramMethod.getParameterTypes();
      int i = this.name.hashCode();
      i = i * 31 + this.parameterTypes.length;
      for (Class localClass : this.parameterTypes) {
        i = i * 31 + localClass.hashCode();
      }
      this.hash = i;
    }
    
    public int hashCode()
    {
      return this.hash;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Signature)) {
        return false;
      }
      Signature localSignature = (Signature)paramObject;
      if (!this.name.equals(localSignature.name)) {
        return false;
      }
      if (this.parameterTypes.length != localSignature.parameterTypes.length) {
        return false;
      }
      for (int i = 0; i < this.parameterTypes.length; i++) {
        if (this.parameterTypes[i] != localSignature.parameterTypes[i]) {
          return false;
        }
      }
      return true;
    }
  }
  
  static class OverrideIndex
  {
    final InjectionPoint.InjectableMembers injectableMembers;
    Map bySignature;
    InjectionPoint.Position position = InjectionPoint.Position.TOP;
    Method lastMethod;
    InjectionPoint.Signature lastSignature;
    
    OverrideIndex(InjectionPoint.InjectableMembers paramInjectableMembers)
    {
      this.injectableMembers = paramInjectableMembers;
    }
    
    boolean removeIfOverriddenBy(Method paramMethod, boolean paramBoolean, InjectionPoint.InjectableMethod paramInjectableMethod)
    {
      if (this.position == InjectionPoint.Position.TOP) {
        return false;
      }
      if (this.bySignature == null)
      {
        this.bySignature = new HashMap();
        for (localObject1 = this.injectableMembers.head; localObject1 != null; localObject1 = ((InjectionPoint.InjectableMember)localObject1).next) {
          if ((localObject1 instanceof InjectionPoint.InjectableMethod))
          {
            localObject2 = (InjectionPoint.InjectableMethod)localObject1;
            if (!((InjectionPoint.InjectableMethod)localObject2).isFinal())
            {
              ArrayList localArrayList = new ArrayList();
              localArrayList.add(localObject2);
              this.bySignature.put(new InjectionPoint.Signature(((InjectionPoint.InjectableMethod)localObject2).method), localArrayList);
            }
          }
        }
      }
      this.lastMethod = paramMethod;
      Object localObject1 = this.lastSignature = new InjectionPoint.Signature(paramMethod);
      Object localObject2 = (List)this.bySignature.get(localObject1);
      boolean bool1 = false;
      if (localObject2 != null)
      {
        Iterator localIterator = ((List)localObject2).iterator();
        while (localIterator.hasNext())
        {
          InjectionPoint.InjectableMethod localInjectableMethod = (InjectionPoint.InjectableMethod)localIterator.next();
          if (InjectionPoint.overrides(paramMethod, localInjectableMethod.method))
          {
            boolean bool2 = (!localInjectableMethod.jsr330) || (localInjectableMethod.overrodeGuiceInject);
            if (paramInjectableMethod != null) {
              paramInjectableMethod.overrodeGuiceInject = bool2;
            }
            if ((paramBoolean) || (!bool2))
            {
              bool1 = true;
              localIterator.remove();
              this.injectableMembers.remove(localInjectableMethod);
            }
          }
        }
      }
      return bool1;
    }
    
    void add(InjectionPoint.InjectableMethod paramInjectableMethod)
    {
      this.injectableMembers.add(paramInjectableMethod);
      if ((this.position == InjectionPoint.Position.BOTTOM) || (paramInjectableMethod.isFinal())) {
        return;
      }
      if (this.bySignature != null)
      {
        InjectionPoint.Signature localSignature = paramInjectableMethod.method == this.lastMethod ? this.lastSignature : new InjectionPoint.Signature(paramInjectableMethod.method);
        Object localObject = (List)this.bySignature.get(localSignature);
        if (localObject == null)
        {
          localObject = new ArrayList();
          this.bySignature.put(localSignature, localObject);
        }
        ((List)localObject).add(paramInjectableMethod);
      }
    }
  }
  
  static enum Position
  {
    TOP,  MIDDLE,  BOTTOM;
  }
  
  static class InjectableMembers
  {
    InjectionPoint.InjectableMember head;
    InjectionPoint.InjectableMember tail;
    
    void add(InjectionPoint.InjectableMember paramInjectableMember)
    {
      if (this.head == null)
      {
        this.head = (this.tail = paramInjectableMember);
      }
      else
      {
        paramInjectableMember.previous = this.tail;
        this.tail.next = paramInjectableMember;
        this.tail = paramInjectableMember;
      }
    }
    
    void remove(InjectionPoint.InjectableMember paramInjectableMember)
    {
      if (paramInjectableMember.previous != null) {
        paramInjectableMember.previous.next = paramInjectableMember.next;
      }
      if (paramInjectableMember.next != null) {
        paramInjectableMember.next.previous = paramInjectableMember.previous;
      }
      if (this.head == paramInjectableMember) {
        this.head = paramInjectableMember.next;
      }
      if (this.tail == paramInjectableMember) {
        this.tail = paramInjectableMember.previous;
      }
    }
    
    boolean isEmpty()
    {
      return this.head == null;
    }
  }
  
  static class InjectableMethod
    extends InjectionPoint.InjectableMember
  {
    final Method method;
    boolean overrodeGuiceInject;
    
    InjectableMethod(TypeLiteral paramTypeLiteral, Method paramMethod, Annotation paramAnnotation)
    {
      super(paramAnnotation);
      this.method = paramMethod;
    }
    
    InjectionPoint toInjectionPoint()
    {
      return new InjectionPoint(this.declaringType, this.method, this.optional);
    }
    
    public boolean isFinal()
    {
      return Modifier.isFinal(this.method.getModifiers());
    }
  }
  
  static class InjectableField
    extends InjectionPoint.InjectableMember
  {
    final Field field;
    
    InjectableField(TypeLiteral paramTypeLiteral, Field paramField, Annotation paramAnnotation)
    {
      super(paramAnnotation);
      this.field = paramField;
    }
    
    InjectionPoint toInjectionPoint()
    {
      return new InjectionPoint(this.declaringType, this.field, this.optional);
    }
  }
  
  static abstract class InjectableMember
  {
    final TypeLiteral declaringType;
    final boolean optional;
    final boolean jsr330;
    InjectableMember previous;
    InjectableMember next;
    
    InjectableMember(TypeLiteral paramTypeLiteral, Annotation paramAnnotation)
    {
      this.declaringType = paramTypeLiteral;
      if (paramAnnotation.annotationType() == javax.inject.Inject.class)
      {
        this.optional = false;
        this.jsr330 = true;
        return;
      }
      this.jsr330 = false;
      this.optional = ((com.google.inject.Inject)paramAnnotation).optional();
    }
    
    abstract InjectionPoint toInjectionPoint();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\InjectionPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */