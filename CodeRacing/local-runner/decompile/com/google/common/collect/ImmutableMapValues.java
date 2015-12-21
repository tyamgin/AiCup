package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Map.Entry;

@GwtCompatible(emulated=true)
final class ImmutableMapValues
  extends ImmutableCollection
{
  private final ImmutableMap map;
  
  ImmutableMapValues(ImmutableMap paramImmutableMap)
  {
    this.map = paramImmutableMap;
  }
  
  public int size()
  {
    return this.map.size();
  }
  
  public UnmodifiableIterator iterator()
  {
    return Maps.valueIterator(this.map.entrySet().iterator());
  }
  
  public boolean contains(Object paramObject)
  {
    return this.map.containsValue(paramObject);
  }
  
  boolean isPartialView()
  {
    return true;
  }
  
  ImmutableList createAsList()
  {
    final ImmutableList localImmutableList = this.map.entrySet().asList();
    new ImmutableAsList()
    {
      public Object get(int paramAnonymousInt)
      {
        return ((Map.Entry)localImmutableList.get(paramAnonymousInt)).getValue();
      }
      
      ImmutableCollection delegateCollection()
      {
        return ImmutableMapValues.this;
      }
    };
  }
  
  @GwtIncompatible("serialization")
  Object writeReplace()
  {
    return new SerializedForm(this.map);
  }
  
  @GwtIncompatible("serialization")
  private static class SerializedForm
    implements Serializable
  {
    final ImmutableMap map;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableMap paramImmutableMap)
    {
      this.map = paramImmutableMap;
    }
    
    Object readResolve()
    {
      return this.map.values();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableMapValues.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */