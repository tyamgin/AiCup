package com.google.inject.internal.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.MapMaker;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Map;

public class StackTraceElements
{
  private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
  private static final InMemoryStackTraceElement[] EMPTY_INMEMORY_STACK_TRACE = new InMemoryStackTraceElement[0];
  static final LoadingCache lineNumbersCache = CacheBuilder.newBuilder().weakKeys().softValues().build(new CacheLoader()
  {
    public LineNumbers load(Class paramAnonymousClass)
    {
      try
      {
        return new LineNumbers(paramAnonymousClass);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    }
  });
  private static Map cache = new MapMaker().makeMap();
  private static final String UNKNOWN_SOURCE = "Unknown Source";
  
  public static Object forMember(Member paramMember)
  {
    if (paramMember == null) {
      return SourceProvider.UNKNOWN_SOURCE;
    }
    Class localClass1 = paramMember.getDeclaringClass();
    LineNumbers localLineNumbers = (LineNumbers)lineNumbersCache.getUnchecked(localClass1);
    String str1 = localLineNumbers.getSource();
    Integer localInteger = localLineNumbers.getLineNumber(paramMember);
    int i = localInteger == null ? localLineNumbers.getFirstLine() : localInteger.intValue();
    Class localClass2 = Classes.memberType(paramMember);
    String str2 = localClass2 == Constructor.class ? "<init>" : paramMember.getName();
    return new StackTraceElement(localClass1.getName(), str2, str1, i);
  }
  
  public static Object forType(Class paramClass)
  {
    LineNumbers localLineNumbers = (LineNumbers)lineNumbersCache.getUnchecked(paramClass);
    int i = localLineNumbers.getFirstLine();
    String str = localLineNumbers.getSource();
    return new StackTraceElement(paramClass.getName(), "class", str, i);
  }
  
  public static void clearCache()
  {
    cache.clear();
  }
  
  public static InMemoryStackTraceElement[] convertToInMemoryStackTraceElement(StackTraceElement[] paramArrayOfStackTraceElement)
  {
    if (paramArrayOfStackTraceElement.length == 0) {
      return EMPTY_INMEMORY_STACK_TRACE;
    }
    InMemoryStackTraceElement[] arrayOfInMemoryStackTraceElement = new InMemoryStackTraceElement[paramArrayOfStackTraceElement.length];
    for (int i = 0; i < paramArrayOfStackTraceElement.length; i++) {
      arrayOfInMemoryStackTraceElement[i] = weakIntern(new InMemoryStackTraceElement(paramArrayOfStackTraceElement[i]));
    }
    return arrayOfInMemoryStackTraceElement;
  }
  
  public static StackTraceElement[] convertToStackTraceElement(InMemoryStackTraceElement[] paramArrayOfInMemoryStackTraceElement)
  {
    if (paramArrayOfInMemoryStackTraceElement.length == 0) {
      return EMPTY_STACK_TRACE;
    }
    StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[paramArrayOfInMemoryStackTraceElement.length];
    for (int i = 0; i < paramArrayOfInMemoryStackTraceElement.length; i++)
    {
      String str1 = paramArrayOfInMemoryStackTraceElement[i].getClassName();
      String str2 = paramArrayOfInMemoryStackTraceElement[i].getMethodName();
      int j = paramArrayOfInMemoryStackTraceElement[i].getLineNumber();
      arrayOfStackTraceElement[i] = new StackTraceElement(str1, str2, "Unknown Source", j);
    }
    return arrayOfStackTraceElement;
  }
  
  private static InMemoryStackTraceElement weakIntern(InMemoryStackTraceElement paramInMemoryStackTraceElement)
  {
    InMemoryStackTraceElement localInMemoryStackTraceElement = (InMemoryStackTraceElement)cache.get(paramInMemoryStackTraceElement);
    if (localInMemoryStackTraceElement != null) {
      return localInMemoryStackTraceElement;
    }
    paramInMemoryStackTraceElement = new InMemoryStackTraceElement(weakIntern(paramInMemoryStackTraceElement.getClassName()), weakIntern(paramInMemoryStackTraceElement.getMethodName()), paramInMemoryStackTraceElement.getLineNumber());
    cache.put(paramInMemoryStackTraceElement, paramInMemoryStackTraceElement);
    return paramInMemoryStackTraceElement;
  }
  
  private static String weakIntern(String paramString)
  {
    String str = (String)cache.get(paramString);
    if (str != null) {
      return str;
    }
    cache.put(paramString, paramString);
    return paramString;
  }
  
  public static class InMemoryStackTraceElement
  {
    private String declaringClass;
    private String methodName;
    private int lineNumber;
    
    InMemoryStackTraceElement(StackTraceElement paramStackTraceElement)
    {
      this(paramStackTraceElement.getClassName(), paramStackTraceElement.getMethodName(), paramStackTraceElement.getLineNumber());
    }
    
    InMemoryStackTraceElement(String paramString1, String paramString2, int paramInt)
    {
      this.declaringClass = paramString1;
      this.methodName = paramString2;
      this.lineNumber = paramInt;
    }
    
    String getClassName()
    {
      return this.declaringClass;
    }
    
    String getMethodName()
    {
      return this.methodName;
    }
    
    int getLineNumber()
    {
      return this.lineNumber;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof InMemoryStackTraceElement)) {
        return false;
      }
      InMemoryStackTraceElement localInMemoryStackTraceElement = (InMemoryStackTraceElement)paramObject;
      return (localInMemoryStackTraceElement.declaringClass.equals(this.declaringClass)) && (localInMemoryStackTraceElement.lineNumber == this.lineNumber) && (this.methodName.equals(localInMemoryStackTraceElement.methodName));
    }
    
    public int hashCode()
    {
      int i = 31 * this.declaringClass.hashCode() + this.methodName.hashCode();
      i = 31 * i + this.lineNumber;
      return i;
    }
    
    public String toString()
    {
      String str1 = String.valueOf(String.valueOf(this.declaringClass));
      String str2 = String.valueOf(String.valueOf(this.methodName));
      int i = this.lineNumber;
      return 14 + str1.length() + str2.length() + str1 + "." + str2 + "(" + i + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\util\StackTraceElements.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */