package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;
import org.aopalliance.intercept.MethodInterceptor;

public abstract class AbstractModule
  implements Module
{
  Binder binder;
  
  public final synchronized void configure(Binder paramBinder)
  {
    Preconditions.checkState(this.binder == null, "Re-entry is not allowed.");
    this.binder = ((Binder)Preconditions.checkNotNull(paramBinder, "builder"));
    try
    {
      configure();
    }
    finally
    {
      this.binder = null;
    }
  }
  
  protected abstract void configure();
  
  protected Binder binder()
  {
    Preconditions.checkState(this.binder != null, "The binder can only be used inside configure()");
    return this.binder;
  }
  
  protected void bindScope(Class paramClass, Scope paramScope)
  {
    binder().bindScope(paramClass, paramScope);
  }
  
  protected LinkedBindingBuilder bind(Key paramKey)
  {
    return binder().bind(paramKey);
  }
  
  protected AnnotatedBindingBuilder bind(TypeLiteral paramTypeLiteral)
  {
    return binder().bind(paramTypeLiteral);
  }
  
  protected AnnotatedBindingBuilder bind(Class paramClass)
  {
    return binder().bind(paramClass);
  }
  
  protected AnnotatedConstantBindingBuilder bindConstant()
  {
    return binder().bindConstant();
  }
  
  protected void install(Module paramModule)
  {
    binder().install(paramModule);
  }
  
  protected void addError(String paramString, Object... paramVarArgs)
  {
    binder().addError(paramString, paramVarArgs);
  }
  
  protected void addError(Throwable paramThrowable)
  {
    binder().addError(paramThrowable);
  }
  
  protected void addError(Message paramMessage)
  {
    binder().addError(paramMessage);
  }
  
  protected void requestInjection(Object paramObject)
  {
    binder().requestInjection(paramObject);
  }
  
  protected void requestStaticInjection(Class... paramVarArgs)
  {
    binder().requestStaticInjection(paramVarArgs);
  }
  
  protected void bindInterceptor(Matcher paramMatcher1, Matcher paramMatcher2, MethodInterceptor... paramVarArgs)
  {
    binder().bindInterceptor(paramMatcher1, paramMatcher2, paramVarArgs);
  }
  
  protected void requireBinding(Key paramKey)
  {
    binder().getProvider(paramKey);
  }
  
  protected void requireBinding(Class paramClass)
  {
    binder().getProvider(paramClass);
  }
  
  protected Provider getProvider(Key paramKey)
  {
    return binder().getProvider(paramKey);
  }
  
  protected Provider getProvider(Class paramClass)
  {
    return binder().getProvider(paramClass);
  }
  
  protected void convertToTypes(Matcher paramMatcher, TypeConverter paramTypeConverter)
  {
    binder().convertToTypes(paramMatcher, paramTypeConverter);
  }
  
  protected Stage currentStage()
  {
    return binder().currentStage();
  }
  
  protected MembersInjector getMembersInjector(Class paramClass)
  {
    return binder().getMembersInjector(paramClass);
  }
  
  protected MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral)
  {
    return binder().getMembersInjector(paramTypeLiteral);
  }
  
  protected void bindListener(Matcher paramMatcher, TypeListener paramTypeListener)
  {
    binder().bindListener(paramMatcher, paramTypeListener);
  }
  
  protected void bindListener(Matcher paramMatcher, ProvisionListener... paramVarArgs)
  {
    binder().bindListener(paramMatcher, paramVarArgs);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\AbstractModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */