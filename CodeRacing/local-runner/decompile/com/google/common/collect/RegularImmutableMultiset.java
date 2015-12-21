package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map.Entry;

@GwtCompatible(serializable=true)
class RegularImmutableMultiset
  extends ImmutableMultiset
{
  private final transient ImmutableMap map;
  private final transient int size;
  
  RegularImmutableMultiset(ImmutableMap paramImmutableMap, int paramInt)
  {
    this.map = paramImmutableMap;
    this.size = paramInt;
  }
  
  boolean isPartialView()
  {
    return this.map.isPartialView();
  }
  
  public int count(Object paramObject)
  {
    Integer localInteger = (Integer)this.map.get(paramObject);
    return localInteger == null ? 0 : localInteger.intValue();
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean contains(Object paramObject)
  {
    return this.map.containsKey(paramObject);
  }
  
  public ImmutableSet elementSet()
  {
    return this.map.keySet();
  }
  
  private static Multiset.Entry entryFromMapEntry(Map.Entry paramEntry)
  {
    return Multisets.immutableEntry(paramEntry.getKey(), ((Integer)paramEntry.getValue()).intValue());
  }
  
  ImmutableSet createEntrySet()
  {
    return new EntrySet(null);
  }
  
  public int hashCode()
  {
    return this.map.hashCode();
  }
  
  private class EntrySet
    extends ImmutableMultiset.EntrySet
  {
    private EntrySet()
    {
      super();
    }
    
    public int size()
    {
      return RegularImmutableMultiset.this.map.size();
    }
    
    public UnmodifiableIterator iterator()
    {
      return asList().iterator();
    }
    
    ImmutableList createAsList()
    {
      final ImmutableList localImmutableList = RegularImmutableMultiset.this.map.entrySet().asList();
      new ImmutableAsList()
      {
        public Multiset.Entry get(int paramAnonymousInt)
        {
          return RegularImmutableMultiset.entryFromMapEntry((Map.Entry)localImmutableList.get(paramAnonymousInt));
        }
        
        ImmutableCollection delegateCollection()
        {
          return RegularImmutableMultiset.EntrySet.this;
        }
      };
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */