package com.google.inject;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;
import org.aopalliance.intercept.MethodInterceptor;

public abstract interface Binder
{
  public abstract void bindInterceptor(Matcher paramMatcher1, Matcher paramMatcher2, MethodInterceptor... paramVarArgs);
  
  public abstract void bindScope(Class paramClass, Scope paramScope);
  
  public abstract LinkedBindingBuilder bind(Key paramKey);
  
  public abstract AnnotatedBindingBuilder bind(TypeLiteral paramTypeLiteral);
  
  public abstract AnnotatedBindingBuilder bind(Class paramClass);
  
  public abstract AnnotatedConstantBindingBuilder bindConstant();
  
  public abstract void requestInjection(TypeLiteral paramTypeLiteral, Object paramObject);
  
  public abstract void requestInjection(Object paramObject);
  
  public abstract void requestStaticInjection(Class... paramVarArgs);
  
  public abstract void install(Module paramModule);
  
  public abstract Stage currentStage();
  
  public abstract void addError(String paramString, Object... paramVarArgs);
  
  public abstract void addError(Throwable paramThrowable);
  
  public abstract void addError(Message paramMessage);
  
  public abstract Provider getProvider(Key paramKey);
  
  public abstract Provider getProvider(Dependency paramDependency);
  
  public abstract Provider getProvider(Class paramClass);
  
  public abstract MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral);
  
  public abstract MembersInjector getMembersInjector(Class paramClass);
  
  public abstract void convertToTypes(Matcher paramMatcher, TypeConverter paramTypeConverter);
  
  public abstract void bindListener(Matcher paramMatcher, TypeListener paramTypeListener);
  
  public abstract void bindListener(Matcher paramMatcher, ProvisionListener... paramVarArgs);
  
  public abstract Binder withSource(Object paramObject);
  
  public abstract Binder skipSources(Class... paramVarArgs);
  
  public abstract PrivateBinder newPrivateBinder();
  
  public abstract void requireExplicitBindings();
  
  public abstract void disableCircularProxies();
  
  public abstract void requireAtInjectOnConstructors();
  
  public abstract void requireExactBindingAnnotations();
  
  public abstract void scanModulesForAnnotatedMethods(ModuleAnnotatedMethodScanner paramModuleAnnotatedMethodScanner);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\Binder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */