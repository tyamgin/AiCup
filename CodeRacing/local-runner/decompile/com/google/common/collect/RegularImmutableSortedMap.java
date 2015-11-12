package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map.Entry;

@GwtCompatible(emulated=true)
final class RegularImmutableSortedMap
  extends ImmutableSortedMap
{
  private final transient RegularImmutableSortedSet keySet;
  private final transient ImmutableList valueList;
  
  RegularImmutableSortedMap(RegularImmutableSortedSet paramRegularImmutableSortedSet, ImmutableList paramImmutableList)
  {
    this.keySet = paramRegularImmutableSortedSet;
    this.valueList = paramImmutableList;
  }
  
  RegularImmutableSortedMap(RegularImmutableSortedSet paramRegularImmutableSortedSet, ImmutableList paramImmutableList, ImmutableSortedMap paramImmutableSortedMap)
  {
    super(paramImmutableSortedMap);
    this.keySet = paramRegularImmutableSortedSet;
    this.valueList = paramImmutableList;
  }
  
  ImmutableSet createEntrySet()
  {
    return new EntrySet(null);
  }
  
  public ImmutableSortedSet keySet()
  {
    return this.keySet;
  }
  
  public ImmutableCollection values()
  {
    return this.valueList;
  }
  
  public Object get(Object paramObject)
  {
    int i = this.keySet.indexOf(paramObject);
    return i == -1 ? null : this.valueList.get(i);
  }
  
  private ImmutableSortedMap getSubMap(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == size())) {
      return this;
    }
    if (paramInt1 == paramInt2) {
      return emptyMap(comparator());
    }
    return from(this.keySet.getSubSet(paramInt1, paramInt2), this.valueList.subList(paramInt1, paramInt2));
  }
  
  public ImmutableSortedMap headMap(Object paramObject, boolean paramBoolean)
  {
    return getSubMap(0, this.keySet.headIndex(Preconditions.checkNotNull(paramObject), paramBoolean));
  }
  
  public ImmutableSortedMap tailMap(Object paramObject, boolean paramBoolean)
  {
    return getSubMap(this.keySet.tailIndex(Preconditions.checkNotNull(paramObject), paramBoolean), size());
  }
  
  ImmutableSortedMap createDescendingMap()
  {
    return new RegularImmutableSortedMap((RegularImmutableSortedSet)this.keySet.descendingSet(), this.valueList.reverse(), this);
  }
  
  private class EntrySet
    extends ImmutableMapEntrySet
  {
    private EntrySet() {}
    
    public UnmodifiableIterator iterator()
    {
      return asList().iterator();
    }
    
    ImmutableList createAsList()
    {
      new ImmutableAsList()
      {
        private final ImmutableList keyList = RegularImmutableSortedMap.this.keySet().asList();
        
        public Map.Entry get(int paramAnonymousInt)
        {
          return Maps.immutableEntry(this.keyList.get(paramAnonymousInt), RegularImmutableSortedMap.this.valueList.get(paramAnonymousInt));
        }
        
        ImmutableCollection delegateCollection()
        {
          return RegularImmutableSortedMap.EntrySet.this;
        }
      };
    }
    
    ImmutableMap map()
    {
      return RegularImmutableSortedMap.this;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableSortedMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */