package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Map.Entry;

@GwtCompatible(emulated=true)
final class ImmutableMapKeySet
  extends ImmutableSet
{
  private final ImmutableMap map;
  
  ImmutableMapKeySet(ImmutableMap paramImmutableMap)
  {
    this.map = paramImmutableMap;
  }
  
  public int size()
  {
    return this.map.size();
  }
  
  public UnmodifiableIterator iterator()
  {
    return asList().iterator();
  }
  
  public boolean contains(Object paramObject)
  {
    return this.map.containsKey(paramObject);
  }
  
  ImmutableList createAsList()
  {
    final ImmutableList localImmutableList = this.map.entrySet().asList();
    new ImmutableAsList()
    {
      public Object get(int paramAnonymousInt)
      {
        return ((Map.Entry)localImmutableList.get(paramAnonymousInt)).getKey();
      }
      
      ImmutableCollection delegateCollection()
      {
        return ImmutableMapKeySet.this;
      }
    };
  }
  
  boolean isPartialView()
  {
    return true;
  }
  
  @GwtIncompatible("serialization")
  Object writeReplace()
  {
    return new KeySetSerializedForm(this.map);
  }
  
  @GwtIncompatible("serialization")
  private static class KeySetSerializedForm
    implements Serializable
  {
    final ImmutableMap map;
    private static final long serialVersionUID = 0L;
    
    KeySetSerializedForm(ImmutableMap paramImmutableMap)
    {
      this.map = paramImmutableMap;
    }
    
    Object readResolve()
    {
      return this.map.keySet();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableMapKeySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */