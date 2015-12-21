package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Map.Entry;

@GwtCompatible(emulated=true)
abstract class ImmutableMapEntrySet
  extends ImmutableSet
{
  abstract ImmutableMap map();
  
  public int size()
  {
    return map().size();
  }
  
  public boolean contains(Object paramObject)
  {
    if ((paramObject instanceof Map.Entry))
    {
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = map().get(localEntry.getKey());
      return (localObject != null) && (localObject.equals(localEntry.getValue()));
    }
    return false;
  }
  
  boolean isPartialView()
  {
    return map().isPartialView();
  }
  
  @GwtIncompatible("serialization")
  Object writeReplace()
  {
    return new EntrySetSerializedForm(map());
  }
  
  @GwtIncompatible("serialization")
  private static class EntrySetSerializedForm
    implements Serializable
  {
    final ImmutableMap map;
    private static final long serialVersionUID = 0L;
    
    EntrySetSerializedForm(ImmutableMap paramImmutableMap)
    {
      this.map = paramImmutableMap;
    }
    
    Object readResolve()
    {
      return this.map.entrySet();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableMapEntrySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */