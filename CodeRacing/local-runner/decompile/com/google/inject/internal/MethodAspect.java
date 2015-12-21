package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.matcher.Matcher;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

final class MethodAspect
{
  private final Matcher classMatcher;
  private final Matcher methodMatcher;
  private final List interceptors;
  
  MethodAspect(Matcher paramMatcher1, Matcher paramMatcher2, List paramList)
  {
    this.classMatcher = ((Matcher)Preconditions.checkNotNull(paramMatcher1, "class matcher"));
    this.methodMatcher = ((Matcher)Preconditions.checkNotNull(paramMatcher2, "method matcher"));
    this.interceptors = ((List)Preconditions.checkNotNull(paramList, "interceptors"));
  }
  
  MethodAspect(Matcher paramMatcher1, Matcher paramMatcher2, MethodInterceptor... paramVarArgs)
  {
    this(paramMatcher1, paramMatcher2, Arrays.asList(paramVarArgs));
  }
  
  boolean matches(Class paramClass)
  {
    return this.classMatcher.matches(paramClass);
  }
  
  boolean matches(Method paramMethod)
  {
    return this.methodMatcher.matches(paramMethod);
  }
  
  List interceptors()
  {
    return this.interceptors;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\MethodAspect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */