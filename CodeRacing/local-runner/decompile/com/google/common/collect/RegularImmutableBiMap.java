package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

@GwtCompatible(serializable=true, emulated=true)
class RegularImmutableBiMap
  extends ImmutableBiMap
{
  static final double MAX_LOAD_FACTOR = 1.2D;
  private final transient BiMapEntry[] kToVTable;
  private final transient BiMapEntry[] vToKTable;
  private final transient BiMapEntry[] entries;
  private final transient int mask;
  private final transient int hashCode;
  private transient ImmutableBiMap inverse;
  
  RegularImmutableBiMap(Collection paramCollection)
  {
    int i = paramCollection.size();
    int j = Hashing.closedTableSize(i, 1.2D);
    this.mask = (j - 1);
    BiMapEntry[] arrayOfBiMapEntry1 = createEntryArray(j);
    BiMapEntry[] arrayOfBiMapEntry2 = createEntryArray(j);
    BiMapEntry[] arrayOfBiMapEntry3 = createEntryArray(i);
    int k = 0;
    int m = 0;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject1 = Preconditions.checkNotNull(localEntry.getKey());
      Object localObject2 = Preconditions.checkNotNull(localEntry.getValue());
      int n = localObject1.hashCode();
      int i1 = localObject2.hashCode();
      int i2 = Hashing.smear(n) & this.mask;
      int i3 = Hashing.smear(i1) & this.mask;
      BiMapEntry localBiMapEntry1 = arrayOfBiMapEntry1[i2];
      for (BiMapEntry localBiMapEntry2 = localBiMapEntry1; localBiMapEntry2 != null; localBiMapEntry2 = localBiMapEntry2.getNextInKToVBucket()) {
        if (localObject1.equals(localBiMapEntry2.getKey())) {
          throw new IllegalArgumentException("Multiple entries with same key: " + localEntry + " and " + localBiMapEntry2);
        }
      }
      localBiMapEntry2 = arrayOfBiMapEntry2[i3];
      for (Object localObject3 = localBiMapEntry2; localObject3 != null; localObject3 = ((BiMapEntry)localObject3).getNextInVToKBucket()) {
        if (localObject2.equals(((BiMapEntry)localObject3).getValue())) {
          throw new IllegalArgumentException("Multiple entries with same value: " + localEntry + " and " + localObject3);
        }
      }
      localObject3 = (localBiMapEntry1 == null) && (localBiMapEntry2 == null) ? new BiMapEntry(localObject1, localObject2) : new NonTerminalBiMapEntry(localObject1, localObject2, localBiMapEntry1, localBiMapEntry2);
      arrayOfBiMapEntry1[i2] = localObject3;
      arrayOfBiMapEntry2[i3] = localObject3;
      arrayOfBiMapEntry3[(k++)] = localObject3;
      m += (n ^ i1);
    }
    this.kToVTable = arrayOfBiMapEntry1;
    this.vToKTable = arrayOfBiMapEntry2;
    this.entries = arrayOfBiMapEntry3;
    this.hashCode = m;
  }
  
  private static BiMapEntry[] createEntryArray(int paramInt)
  {
    return new BiMapEntry[paramInt];
  }
  
  public Object get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = Hashing.smear(paramObject.hashCode()) & this.mask;
    for (BiMapEntry localBiMapEntry = this.kToVTable[i]; localBiMapEntry != null; localBiMapEntry = localBiMapEntry.getNextInKToVBucket()) {
      if (paramObject.equals(localBiMapEntry.getKey())) {
        return localBiMapEntry.getValue();
      }
    }
    return null;
  }
  
  ImmutableSet createEntrySet()
  {
    new ImmutableMapEntrySet()
    {
      ImmutableMap map()
      {
        return RegularImmutableBiMap.this;
      }
      
      public UnmodifiableIterator iterator()
      {
        return asList().iterator();
      }
      
      ImmutableList createAsList()
      {
        return new RegularImmutableAsList(this, RegularImmutableBiMap.this.entries);
      }
      
      boolean isHashCodeFast()
      {
        return true;
      }
      
      public int hashCode()
      {
        return RegularImmutableBiMap.this.hashCode;
      }
    };
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  public int size()
  {
    return this.entries.length;
  }
  
  public ImmutableBiMap inverse()
  {
    ImmutableBiMap localImmutableBiMap = this.inverse;
    return localImmutableBiMap == null ? (this.inverse = new Inverse(null)) : localImmutableBiMap;
  }
  
  private static class InverseSerializedForm
    implements Serializable
  {
    private final ImmutableBiMap forward;
    private static final long serialVersionUID = 1L;
    
    InverseSerializedForm(ImmutableBiMap paramImmutableBiMap)
    {
      this.forward = paramImmutableBiMap;
    }
    
    Object readResolve()
    {
      return this.forward.inverse();
    }
  }
  
  private final class Inverse
    extends ImmutableBiMap
  {
    private Inverse() {}
    
    public int size()
    {
      return inverse().size();
    }
    
    public ImmutableBiMap inverse()
    {
      return RegularImmutableBiMap.this;
    }
    
    public Object get(Object paramObject)
    {
      if (paramObject == null) {
        return null;
      }
      int i = Hashing.smear(paramObject.hashCode()) & RegularImmutableBiMap.this.mask;
      for (RegularImmutableBiMap.BiMapEntry localBiMapEntry = RegularImmutableBiMap.this.vToKTable[i]; localBiMapEntry != null; localBiMapEntry = localBiMapEntry.getNextInVToKBucket()) {
        if (paramObject.equals(localBiMapEntry.getValue())) {
          return localBiMapEntry.getKey();
        }
      }
      return null;
    }
    
    ImmutableSet createEntrySet()
    {
      return new InverseEntrySet();
    }
    
    boolean isPartialView()
    {
      return false;
    }
    
    Object writeReplace()
    {
      return new RegularImmutableBiMap.InverseSerializedForm(RegularImmutableBiMap.this);
    }
    
    final class InverseEntrySet
      extends ImmutableMapEntrySet
    {
      InverseEntrySet() {}
      
      ImmutableMap map()
      {
        return RegularImmutableBiMap.Inverse.this;
      }
      
      boolean isHashCodeFast()
      {
        return true;
      }
      
      public int hashCode()
      {
        return RegularImmutableBiMap.this.hashCode;
      }
      
      public UnmodifiableIterator iterator()
      {
        return asList().iterator();
      }
      
      ImmutableList createAsList()
      {
        new ImmutableAsList()
        {
          public Map.Entry get(int paramAnonymousInt)
          {
            RegularImmutableBiMap.BiMapEntry localBiMapEntry = RegularImmutableBiMap.this.entries[paramAnonymousInt];
            return Maps.immutableEntry(localBiMapEntry.getValue(), localBiMapEntry.getKey());
          }
          
          ImmutableCollection delegateCollection()
          {
            return RegularImmutableBiMap.Inverse.InverseEntrySet.this;
          }
        };
      }
    }
  }
  
  private static class NonTerminalBiMapEntry
    extends RegularImmutableBiMap.BiMapEntry
  {
    private final RegularImmutableBiMap.BiMapEntry nextInKToVBucket;
    private final RegularImmutableBiMap.BiMapEntry nextInVToKBucket;
    
    NonTerminalBiMapEntry(Object paramObject1, Object paramObject2, RegularImmutableBiMap.BiMapEntry paramBiMapEntry1, RegularImmutableBiMap.BiMapEntry paramBiMapEntry2)
    {
      super(paramObject2);
      this.nextInKToVBucket = paramBiMapEntry1;
      this.nextInVToKBucket = paramBiMapEntry2;
    }
    
    RegularImmutableBiMap.BiMapEntry getNextInKToVBucket()
    {
      return this.nextInKToVBucket;
    }
    
    RegularImmutableBiMap.BiMapEntry getNextInVToKBucket()
    {
      return this.nextInVToKBucket;
    }
  }
  
  private static class BiMapEntry
    extends ImmutableEntry
  {
    BiMapEntry(Object paramObject1, Object paramObject2)
    {
      super(paramObject2);
    }
    
    BiMapEntry getNextInKToVBucket()
    {
      return null;
    }
    
    BiMapEntry getNextInVToKBucket()
    {
      return null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */