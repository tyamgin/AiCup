package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract interface State
{
  public static final State NONE = new State()
  {
    public State parent()
    {
      throw new UnsupportedOperationException();
    }
    
    public BindingImpl getExplicitBinding(Key paramAnonymousKey)
    {
      return null;
    }
    
    public Map getExplicitBindingsThisLevel()
    {
      throw new UnsupportedOperationException();
    }
    
    public void putBinding(Key paramAnonymousKey, BindingImpl paramAnonymousBindingImpl)
    {
      throw new UnsupportedOperationException();
    }
    
    public ScopeBinding getScopeBinding(Class paramAnonymousClass)
    {
      return null;
    }
    
    public void putScopeBinding(Class paramAnonymousClass, ScopeBinding paramAnonymousScopeBinding)
    {
      throw new UnsupportedOperationException();
    }
    
    public void addConverter(TypeConverterBinding paramAnonymousTypeConverterBinding)
    {
      throw new UnsupportedOperationException();
    }
    
    public TypeConverterBinding getConverter(String paramAnonymousString, TypeLiteral paramAnonymousTypeLiteral, Errors paramAnonymousErrors, Object paramAnonymousObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public Iterable getConvertersThisLevel()
    {
      return ImmutableSet.of();
    }
    
    public void addMethodAspect(MethodAspect paramAnonymousMethodAspect)
    {
      throw new UnsupportedOperationException();
    }
    
    public ImmutableList getMethodAspects()
    {
      return ImmutableList.of();
    }
    
    public void addTypeListener(TypeListenerBinding paramAnonymousTypeListenerBinding)
    {
      throw new UnsupportedOperationException();
    }
    
    public List getTypeListenerBindings()
    {
      return ImmutableList.of();
    }
    
    public void addProvisionListener(ProvisionListenerBinding paramAnonymousProvisionListenerBinding)
    {
      throw new UnsupportedOperationException();
    }
    
    public List getProvisionListenerBindings()
    {
      return ImmutableList.of();
    }
    
    public void addScanner(ModuleAnnotatedMethodScannerBinding paramAnonymousModuleAnnotatedMethodScannerBinding)
    {
      throw new UnsupportedOperationException();
    }
    
    public List getScannerBindings()
    {
      return ImmutableList.of();
    }
    
    public void blacklist(Key paramAnonymousKey, State paramAnonymousState, Object paramAnonymousObject) {}
    
    public boolean isBlacklisted(Key paramAnonymousKey)
    {
      return true;
    }
    
    public Set getSourcesForBlacklistedKey(Key paramAnonymousKey)
    {
      throw new UnsupportedOperationException();
    }
    
    public Object lock()
    {
      throw new UnsupportedOperationException();
    }
    
    public Object singletonCreationLock()
    {
      throw new UnsupportedOperationException();
    }
    
    public Map getScopes()
    {
      return ImmutableMap.of();
    }
  };
  
  public abstract State parent();
  
  public abstract BindingImpl getExplicitBinding(Key paramKey);
  
  public abstract Map getExplicitBindingsThisLevel();
  
  public abstract void putBinding(Key paramKey, BindingImpl paramBindingImpl);
  
  public abstract ScopeBinding getScopeBinding(Class paramClass);
  
  public abstract void putScopeBinding(Class paramClass, ScopeBinding paramScopeBinding);
  
  public abstract void addConverter(TypeConverterBinding paramTypeConverterBinding);
  
  public abstract TypeConverterBinding getConverter(String paramString, TypeLiteral paramTypeLiteral, Errors paramErrors, Object paramObject);
  
  public abstract Iterable getConvertersThisLevel();
  
  public abstract void addMethodAspect(MethodAspect paramMethodAspect);
  
  public abstract ImmutableList getMethodAspects();
  
  public abstract void addTypeListener(TypeListenerBinding paramTypeListenerBinding);
  
  public abstract List getTypeListenerBindings();
  
  public abstract void addProvisionListener(ProvisionListenerBinding paramProvisionListenerBinding);
  
  public abstract List getProvisionListenerBindings();
  
  public abstract void addScanner(ModuleAnnotatedMethodScannerBinding paramModuleAnnotatedMethodScannerBinding);
  
  public abstract List getScannerBindings();
  
  public abstract void blacklist(Key paramKey, State paramState, Object paramObject);
  
  public abstract boolean isBlacklisted(Key paramKey);
  
  public abstract Set getSourcesForBlacklistedKey(Key paramKey);
  
  public abstract Object lock();
  
  public abstract Map getScopes();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\State.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */