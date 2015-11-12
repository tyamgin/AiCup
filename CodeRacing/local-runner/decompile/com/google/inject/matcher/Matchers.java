package com.google.inject.matcher;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class Matchers
{
  private static final Matcher ANY = new Any(null);
  
  public static Matcher any()
  {
    return ANY;
  }
  
  public static Matcher not(Matcher paramMatcher)
  {
    return new Not(paramMatcher, null);
  }
  
  private static void checkForRuntimeRetention(Class paramClass)
  {
    Retention localRetention = (Retention)paramClass.getAnnotation(Retention.class);
    Preconditions.checkArgument((localRetention != null) && (localRetention.value() == RetentionPolicy.RUNTIME), "Annotation %s is missing RUNTIME retention", new Object[] { paramClass.getSimpleName() });
  }
  
  public static Matcher annotatedWith(Class paramClass)
  {
    return new AnnotatedWithType(paramClass);
  }
  
  public static Matcher annotatedWith(Annotation paramAnnotation)
  {
    return new AnnotatedWith(paramAnnotation);
  }
  
  public static Matcher subclassesOf(Class paramClass)
  {
    return new SubclassesOf(paramClass);
  }
  
  public static Matcher only(Object paramObject)
  {
    return new Only(paramObject);
  }
  
  public static Matcher identicalTo(Object paramObject)
  {
    return new IdenticalTo(paramObject);
  }
  
  public static Matcher inPackage(Package paramPackage)
  {
    return new InPackage(paramPackage);
  }
  
  public static Matcher inSubpackage(String paramString)
  {
    return new InSubpackage(paramString);
  }
  
  public static Matcher returns(Matcher paramMatcher)
  {
    return new Returns(paramMatcher);
  }
  
  private static class Returns
    extends AbstractMatcher
    implements Serializable
  {
    private final Matcher returnType;
    private static final long serialVersionUID = 0L;
    
    public Returns(Matcher paramMatcher)
    {
      this.returnType = ((Matcher)Preconditions.checkNotNull(paramMatcher, "return type matcher"));
    }
    
    public boolean matches(Method paramMethod)
    {
      return this.returnType.matches(paramMethod.getReturnType());
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Returns)) && (((Returns)paramObject).returnType.equals(this.returnType));
    }
    
    public int hashCode()
    {
      return 37 * this.returnType.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.returnType));
      return 9 + str.length() + "returns(" + str + ")";
    }
  }
  
  private static class InSubpackage
    extends AbstractMatcher
    implements Serializable
  {
    private final String targetPackageName;
    private static final long serialVersionUID = 0L;
    
    public InSubpackage(String paramString)
    {
      this.targetPackageName = paramString;
    }
    
    public boolean matches(Class paramClass)
    {
      String str = paramClass.getPackage().getName();
      return (str.equals(this.targetPackageName)) || (str.startsWith(String.valueOf(this.targetPackageName).concat(".")));
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof InSubpackage)) && (((InSubpackage)paramObject).targetPackageName.equals(this.targetPackageName));
    }
    
    public int hashCode()
    {
      return 37 * this.targetPackageName.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.targetPackageName));
      return 14 + str.length() + "inSubpackage(" + str + ")";
    }
  }
  
  private static class InPackage
    extends AbstractMatcher
    implements Serializable
  {
    private final transient Package targetPackage;
    private final String packageName;
    private static final long serialVersionUID = 0L;
    
    public InPackage(Package paramPackage)
    {
      this.targetPackage = ((Package)Preconditions.checkNotNull(paramPackage, "package"));
      this.packageName = paramPackage.getName();
    }
    
    public boolean matches(Class paramClass)
    {
      return paramClass.getPackage().equals(this.targetPackage);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof InPackage)) && (((InPackage)paramObject).targetPackage.equals(this.targetPackage));
    }
    
    public int hashCode()
    {
      return 37 * this.targetPackage.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.targetPackage.getName()));
      return 11 + str.length() + "inPackage(" + str + ")";
    }
    
    public Object readResolve()
    {
      return Matchers.inPackage(Package.getPackage(this.packageName));
    }
  }
  
  private static class IdenticalTo
    extends AbstractMatcher
    implements Serializable
  {
    private final Object value;
    private static final long serialVersionUID = 0L;
    
    public IdenticalTo(Object paramObject)
    {
      this.value = Preconditions.checkNotNull(paramObject, "value");
    }
    
    public boolean matches(Object paramObject)
    {
      return this.value == paramObject;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof IdenticalTo)) && (((IdenticalTo)paramObject).value == this.value);
    }
    
    public int hashCode()
    {
      return 37 * System.identityHashCode(this.value);
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.value));
      return 13 + str.length() + "identicalTo(" + str + ")";
    }
  }
  
  private static class Only
    extends AbstractMatcher
    implements Serializable
  {
    private final Object value;
    private static final long serialVersionUID = 0L;
    
    public Only(Object paramObject)
    {
      this.value = Preconditions.checkNotNull(paramObject, "value");
    }
    
    public boolean matches(Object paramObject)
    {
      return this.value.equals(paramObject);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Only)) && (((Only)paramObject).value.equals(this.value));
    }
    
    public int hashCode()
    {
      return 37 * this.value.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.value));
      return 6 + str.length() + "only(" + str + ")";
    }
  }
  
  private static class SubclassesOf
    extends AbstractMatcher
    implements Serializable
  {
    private final Class superclass;
    private static final long serialVersionUID = 0L;
    
    public SubclassesOf(Class paramClass)
    {
      this.superclass = ((Class)Preconditions.checkNotNull(paramClass, "superclass"));
    }
    
    public boolean matches(Class paramClass)
    {
      return this.superclass.isAssignableFrom(paramClass);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof SubclassesOf)) && (((SubclassesOf)paramObject).superclass.equals(this.superclass));
    }
    
    public int hashCode()
    {
      return 37 * this.superclass.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.superclass.getSimpleName()));
      return 20 + str.length() + "subclassesOf(" + str + ".class)";
    }
  }
  
  private static class AnnotatedWith
    extends AbstractMatcher
    implements Serializable
  {
    private final Annotation annotation;
    private static final long serialVersionUID = 0L;
    
    public AnnotatedWith(Annotation paramAnnotation)
    {
      this.annotation = ((Annotation)Preconditions.checkNotNull(paramAnnotation, "annotation"));
      Matchers.checkForRuntimeRetention(paramAnnotation.annotationType());
    }
    
    public boolean matches(AnnotatedElement paramAnnotatedElement)
    {
      Annotation localAnnotation = paramAnnotatedElement.getAnnotation(this.annotation.annotationType());
      return (localAnnotation != null) && (this.annotation.equals(localAnnotation));
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof AnnotatedWith)) && (((AnnotatedWith)paramObject).annotation.equals(this.annotation));
    }
    
    public int hashCode()
    {
      return 37 * this.annotation.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.annotation));
      return 15 + str.length() + "annotatedWith(" + str + ")";
    }
  }
  
  private static class AnnotatedWithType
    extends AbstractMatcher
    implements Serializable
  {
    private final Class annotationType;
    private static final long serialVersionUID = 0L;
    
    public AnnotatedWithType(Class paramClass)
    {
      this.annotationType = ((Class)Preconditions.checkNotNull(paramClass, "annotation type"));
      Matchers.checkForRuntimeRetention(paramClass);
    }
    
    public boolean matches(AnnotatedElement paramAnnotatedElement)
    {
      return paramAnnotatedElement.isAnnotationPresent(this.annotationType);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof AnnotatedWithType)) && (((AnnotatedWithType)paramObject).annotationType.equals(this.annotationType));
    }
    
    public int hashCode()
    {
      return 37 * this.annotationType.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.annotationType.getSimpleName()));
      return 21 + str.length() + "annotatedWith(" + str + ".class)";
    }
  }
  
  private static class Not
    extends AbstractMatcher
    implements Serializable
  {
    final Matcher delegate;
    private static final long serialVersionUID = 0L;
    
    private Not(Matcher paramMatcher)
    {
      this.delegate = ((Matcher)Preconditions.checkNotNull(paramMatcher, "delegate"));
    }
    
    public boolean matches(Object paramObject)
    {
      return !this.delegate.matches(paramObject);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Not)) && (((Not)paramObject).delegate.equals(this.delegate));
    }
    
    public int hashCode()
    {
      return -this.delegate.hashCode();
    }
    
    public String toString()
    {
      String str = String.valueOf(String.valueOf(this.delegate));
      return 5 + str.length() + "not(" + str + ")";
    }
  }
  
  private static class Any
    extends AbstractMatcher
    implements Serializable
  {
    private static final long serialVersionUID = 0L;
    
    public boolean matches(Object paramObject)
    {
      return true;
    }
    
    public String toString()
    {
      return "any()";
    }
    
    public Object readResolve()
    {
      return Matchers.any();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\matcher\Matchers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */