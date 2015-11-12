package com.google.inject.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.ProvisionListenerBinding;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

final class ProvisionListenerCallbackStore
{
  private static final Set INTERNAL_BINDINGS = ImmutableSet.of(Key.get(Injector.class), Key.get(Stage.class), Key.get(Logger.class));
  private final ImmutableList listenerBindings;
  private final LoadingCache cache = CacheBuilder.newBuilder().build(new CacheLoader()
  {
    public ProvisionListenerStackCallback load(ProvisionListenerCallbackStore.KeyBinding paramAnonymousKeyBinding)
    {
      return ProvisionListenerCallbackStore.this.create(paramAnonymousKeyBinding.binding);
    }
  });
  
  ProvisionListenerCallbackStore(List paramList)
  {
    this.listenerBindings = ImmutableList.copyOf(paramList);
  }
  
  public ProvisionListenerStackCallback get(Binding paramBinding)
  {
    if (!INTERNAL_BINDINGS.contains(paramBinding.getKey())) {
      return (ProvisionListenerStackCallback)this.cache.getUnchecked(new KeyBinding(paramBinding.getKey(), paramBinding));
    }
    return ProvisionListenerStackCallback.emptyListener();
  }
  
  boolean remove(Binding paramBinding)
  {
    return this.cache.asMap().remove(paramBinding) != null;
  }
  
  private ProvisionListenerStackCallback create(Binding paramBinding)
  {
    ArrayList localArrayList = null;
    Iterator localIterator = this.listenerBindings.iterator();
    while (localIterator.hasNext())
    {
      ProvisionListenerBinding localProvisionListenerBinding = (ProvisionListenerBinding)localIterator.next();
      if (localProvisionListenerBinding.getBindingMatcher().matches(paramBinding))
      {
        if (localArrayList == null) {
          localArrayList = Lists.newArrayList();
        }
        localArrayList.addAll(localProvisionListenerBinding.getListeners());
      }
    }
    if ((localArrayList == null) || (localArrayList.isEmpty())) {
      return ProvisionListenerStackCallback.emptyListener();
    }
    return new ProvisionListenerStackCallback(paramBinding, localArrayList);
  }
  
  private static class KeyBinding
  {
    final Key key;
    final Binding binding;
    
    KeyBinding(Key paramKey, Binding paramBinding)
    {
      this.key = paramKey;
      this.binding = paramBinding;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof KeyBinding)) && (this.key.equals(((KeyBinding)paramObject).key));
    }
    
    public int hashCode()
    {
      return this.key.hashCode();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProvisionListenerCallbackStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */