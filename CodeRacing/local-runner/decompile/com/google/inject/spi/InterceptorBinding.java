package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.matcher.Matcher;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

public final class InterceptorBinding
  implements Element
{
  private final Object source;
  private final Matcher classMatcher;
  private final Matcher methodMatcher;
  private final ImmutableList interceptors;
  
  InterceptorBinding(Object paramObject, Matcher paramMatcher1, Matcher paramMatcher2, MethodInterceptor[] paramArrayOfMethodInterceptor)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
    this.classMatcher = ((Matcher)Preconditions.checkNotNull(paramMatcher1, "classMatcher"));
    this.methodMatcher = ((Matcher)Preconditions.checkNotNull(paramMatcher2, "methodMatcher"));
    this.interceptors = ImmutableList.copyOf(paramArrayOfMethodInterceptor);
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Matcher getClassMatcher()
  {
    return this.classMatcher;
  }
  
  public Matcher getMethodMatcher()
  {
    return this.methodMatcher;
  }
  
  public List getInterceptors()
  {
    return this.interceptors;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).bindInterceptor(this.classMatcher, this.methodMatcher, (MethodInterceptor[])this.interceptors.toArray(new MethodInterceptor[this.interceptors.size()]));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\InterceptorBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */