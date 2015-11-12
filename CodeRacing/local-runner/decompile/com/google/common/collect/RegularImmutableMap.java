package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map.Entry;

@GwtCompatible(serializable=true, emulated=true)
final class RegularImmutableMap
  extends ImmutableMap
{
  private final transient LinkedEntry[] entries;
  private final transient LinkedEntry[] table;
  private final transient int mask;
  private static final double MAX_LOAD_FACTOR = 1.2D;
  private static final long serialVersionUID = 0L;
  
  RegularImmutableMap(Map.Entry... paramVarArgs)
  {
    int i = paramVarArgs.length;
    this.entries = createEntryArray(i);
    int j = Hashing.closedTableSize(i, 1.2D);
    this.table = createEntryArray(j);
    this.mask = (j - 1);
    for (int k = 0; k < i; k++)
    {
      Map.Entry localEntry = paramVarArgs[k];
      Object localObject = localEntry.getKey();
      int m = localObject.hashCode();
      int n = Hashing.smear(m) & this.mask;
      LinkedEntry localLinkedEntry1 = this.table[n];
      LinkedEntry localLinkedEntry2 = newLinkedEntry(localObject, localEntry.getValue(), localLinkedEntry1);
      this.table[n] = localLinkedEntry2;
      this.entries[k] = localLinkedEntry2;
      while (localLinkedEntry1 != null)
      {
        Preconditions.checkArgument(!localObject.equals(localLinkedEntry1.getKey()), "duplicate key: %s", new Object[] { localObject });
        localLinkedEntry1 = localLinkedEntry1.next();
      }
    }
  }
  
  private LinkedEntry[] createEntryArray(int paramInt)
  {
    return new LinkedEntry[paramInt];
  }
  
  private static LinkedEntry newLinkedEntry(Object paramObject1, Object paramObject2, LinkedEntry paramLinkedEntry)
  {
    return (LinkedEntry)(paramLinkedEntry == null ? new TerminalEntry(paramObject1, paramObject2) : new NonTerminalEntry(paramObject1, paramObject2, paramLinkedEntry));
  }
  
  public Object get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = Hashing.smear(paramObject.hashCode()) & this.mask;
    for (LinkedEntry localLinkedEntry = this.table[i]; localLinkedEntry != null; localLinkedEntry = localLinkedEntry.next())
    {
      Object localObject = localLinkedEntry.getKey();
      if (paramObject.equals(localObject)) {
        return localLinkedEntry.getValue();
      }
    }
    return null;
  }
  
  public int size()
  {
    return this.entries.length;
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  ImmutableSet createEntrySet()
  {
    return new EntrySet(null);
  }
  
  private class EntrySet
    extends ImmutableMapEntrySet
  {
    private EntrySet() {}
    
    ImmutableMap map()
    {
      return RegularImmutableMap.this;
    }
    
    public UnmodifiableIterator iterator()
    {
      return asList().iterator();
    }
    
    ImmutableList createAsList()
    {
      return new RegularImmutableAsList(this, RegularImmutableMap.this.entries);
    }
  }
  
  private static final class TerminalEntry
    extends ImmutableEntry
    implements RegularImmutableMap.LinkedEntry
  {
    TerminalEntry(Object paramObject1, Object paramObject2)
    {
      super(paramObject2);
    }
    
    public RegularImmutableMap.LinkedEntry next()
    {
      return null;
    }
  }
  
  private static final class NonTerminalEntry
    extends ImmutableEntry
    implements RegularImmutableMap.LinkedEntry
  {
    final RegularImmutableMap.LinkedEntry next;
    
    NonTerminalEntry(Object paramObject1, Object paramObject2, RegularImmutableMap.LinkedEntry paramLinkedEntry)
    {
      super(paramObject2);
      this.next = paramLinkedEntry;
    }
    
    public RegularImmutableMap.LinkedEntry next()
    {
      return this.next;
    }
  }
  
  private static abstract interface LinkedEntry
    extends Map.Entry
  {
    public abstract LinkedEntry next();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */