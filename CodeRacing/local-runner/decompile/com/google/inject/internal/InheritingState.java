package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class InheritingState
  implements State
{
  private final State parent;
  private final Map explicitBindingsMutable = Maps.newLinkedHashMap();
  private final Map explicitBindings = Collections.unmodifiableMap(this.explicitBindingsMutable);
  private final Map scopes = Maps.newHashMap();
  private final List converters = Lists.newArrayList();
  private final List methodAspects = Lists.newArrayList();
  private final List typeListenerBindings = Lists.newArrayList();
  private final List provisionListenerBindings = Lists.newArrayList();
  private final List scannerBindings = Lists.newArrayList();
  private final WeakKeySet blacklistedKeys;
  private final Object lock;
  
  InheritingState(State paramState)
  {
    this.parent = ((State)Preconditions.checkNotNull(paramState, "parent"));
    this.lock = (paramState == State.NONE ? this : paramState.lock());
    this.blacklistedKeys = new WeakKeySet(this.lock);
  }
  
  public State parent()
  {
    return this.parent;
  }
  
  public BindingImpl getExplicitBinding(Key paramKey)
  {
    Binding localBinding = (Binding)this.explicitBindings.get(paramKey);
    return localBinding != null ? (BindingImpl)localBinding : this.parent.getExplicitBinding(paramKey);
  }
  
  public Map getExplicitBindingsThisLevel()
  {
    return this.explicitBindings;
  }
  
  public void putBinding(Key paramKey, BindingImpl paramBindingImpl)
  {
    this.explicitBindingsMutable.put(paramKey, paramBindingImpl);
  }
  
  public ScopeBinding getScopeBinding(Class paramClass)
  {
    ScopeBinding localScopeBinding = (ScopeBinding)this.scopes.get(paramClass);
    return localScopeBinding != null ? localScopeBinding : this.parent.getScopeBinding(paramClass);
  }
  
  public void putScopeBinding(Class paramClass, ScopeBinding paramScopeBinding)
  {
    this.scopes.put(paramClass, paramScopeBinding);
  }
  
  public Iterable getConvertersThisLevel()
  {
    return this.converters;
  }
  
  public void addConverter(TypeConverterBinding paramTypeConverterBinding)
  {
    this.converters.add(paramTypeConverterBinding);
  }
  
  public TypeConverterBinding getConverter(String paramString, TypeLiteral paramTypeLiteral, Errors paramErrors, Object paramObject)
  {
    Object localObject1 = null;
    for (Object localObject2 = this; localObject2 != State.NONE; localObject2 = ((State)localObject2).parent())
    {
      Iterator localIterator = ((State)localObject2).getConvertersThisLevel().iterator();
      while (localIterator.hasNext())
      {
        TypeConverterBinding localTypeConverterBinding = (TypeConverterBinding)localIterator.next();
        if (localTypeConverterBinding.getTypeMatcher().matches(paramTypeLiteral))
        {
          if (localObject1 != null) {
            paramErrors.ambiguousTypeConversion(paramString, paramObject, paramTypeLiteral, (TypeConverterBinding)localObject1, localTypeConverterBinding);
          }
          localObject1 = localTypeConverterBinding;
        }
      }
    }
    return (TypeConverterBinding)localObject1;
  }
  
  public void addMethodAspect(MethodAspect paramMethodAspect)
  {
    this.methodAspects.add(paramMethodAspect);
  }
  
  public ImmutableList getMethodAspects()
  {
    return new ImmutableList.Builder().addAll(this.parent.getMethodAspects()).addAll(this.methodAspects).build();
  }
  
  public void addTypeListener(TypeListenerBinding paramTypeListenerBinding)
  {
    this.typeListenerBindings.add(paramTypeListenerBinding);
  }
  
  public List getTypeListenerBindings()
  {
    List localList = this.parent.getTypeListenerBindings();
    ArrayList localArrayList = Lists.newArrayListWithCapacity(localList.size() + this.typeListenerBindings.size());
    localArrayList.addAll(localList);
    localArrayList.addAll(this.typeListenerBindings);
    return localArrayList;
  }
  
  public void addProvisionListener(ProvisionListenerBinding paramProvisionListenerBinding)
  {
    this.provisionListenerBindings.add(paramProvisionListenerBinding);
  }
  
  public List getProvisionListenerBindings()
  {
    List localList = this.parent.getProvisionListenerBindings();
    ArrayList localArrayList = Lists.newArrayListWithCapacity(localList.size() + this.provisionListenerBindings.size());
    localArrayList.addAll(localList);
    localArrayList.addAll(this.provisionListenerBindings);
    return localArrayList;
  }
  
  public void addScanner(ModuleAnnotatedMethodScannerBinding paramModuleAnnotatedMethodScannerBinding)
  {
    this.scannerBindings.add(paramModuleAnnotatedMethodScannerBinding);
  }
  
  public List getScannerBindings()
  {
    List localList = this.parent.getScannerBindings();
    ArrayList localArrayList = Lists.newArrayListWithCapacity(localList.size() + this.scannerBindings.size());
    localArrayList.addAll(localList);
    localArrayList.addAll(this.scannerBindings);
    return localArrayList;
  }
  
  public void blacklist(Key paramKey, State paramState, Object paramObject)
  {
    this.parent.blacklist(paramKey, paramState, paramObject);
    this.blacklistedKeys.add(paramKey, paramState, paramObject);
  }
  
  public boolean isBlacklisted(Key paramKey)
  {
    return this.blacklistedKeys.contains(paramKey);
  }
  
  public Set getSourcesForBlacklistedKey(Key paramKey)
  {
    return this.blacklistedKeys.getSources(paramKey);
  }
  
  public Object lock()
  {
    return this.lock;
  }
  
  public Map getScopes()
  {
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    Iterator localIterator = this.scopes.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localBuilder.put(localEntry.getKey(), ((ScopeBinding)localEntry.getValue()).getScope());
    }
    return localBuilder.build();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InheritingState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */