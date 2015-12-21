package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

@Beta
@GwtIncompatible("NavigableMap")
public final class TreeRangeMap
  implements RangeMap
{
  private final NavigableMap entriesByLowerBound = Maps.newTreeMap();
  private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap()
  {
    public Object get(Comparable paramAnonymousComparable)
    {
      return null;
    }
    
    public Map.Entry getEntry(Comparable paramAnonymousComparable)
    {
      return null;
    }
    
    public Range span()
    {
      throw new NoSuchElementException();
    }
    
    public void put(Range paramAnonymousRange, Object paramAnonymousObject)
    {
      Preconditions.checkNotNull(paramAnonymousRange);
      throw new IllegalArgumentException("Cannot insert range " + paramAnonymousRange + " into an empty subRangeMap");
    }
    
    public void putAll(RangeMap paramAnonymousRangeMap)
    {
      if (!paramAnonymousRangeMap.asMapOfRanges().isEmpty()) {
        throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
      }
    }
    
    public void clear() {}
    
    public void remove(Range paramAnonymousRange)
    {
      Preconditions.checkNotNull(paramAnonymousRange);
    }
    
    public Map asMapOfRanges()
    {
      return Collections.emptyMap();
    }
    
    public RangeMap subRangeMap(Range paramAnonymousRange)
    {
      Preconditions.checkNotNull(paramAnonymousRange);
      return this;
    }
  };
  
  public static TreeRangeMap create()
  {
    return new TreeRangeMap();
  }
  
  public Object get(Comparable paramComparable)
  {
    Map.Entry localEntry = getEntry(paramComparable);
    return localEntry == null ? null : localEntry.getValue();
  }
  
  public Map.Entry getEntry(Comparable paramComparable)
  {
    Map.Entry localEntry = this.entriesByLowerBound.floorEntry(Cut.belowValue(paramComparable));
    if ((localEntry != null) && (((RangeMapEntry)localEntry.getValue()).contains(paramComparable))) {
      return (Map.Entry)localEntry.getValue();
    }
    return null;
  }
  
  public void put(Range paramRange, Object paramObject)
  {
    if (!paramRange.isEmpty())
    {
      Preconditions.checkNotNull(paramObject);
      remove(paramRange);
      this.entriesByLowerBound.put(paramRange.lowerBound, new RangeMapEntry(paramRange, paramObject));
    }
  }
  
  public void putAll(RangeMap paramRangeMap)
  {
    Iterator localIterator = paramRangeMap.asMapOfRanges().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put((Range)localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public void clear()
  {
    this.entriesByLowerBound.clear();
  }
  
  public Range span()
  {
    Map.Entry localEntry1 = this.entriesByLowerBound.firstEntry();
    Map.Entry localEntry2 = this.entriesByLowerBound.lastEntry();
    if (localEntry1 == null) {
      throw new NoSuchElementException();
    }
    return Range.create(((RangeMapEntry)localEntry1.getValue()).getKey().lowerBound, ((RangeMapEntry)localEntry2.getValue()).getKey().upperBound);
  }
  
  private void putRangeMapEntry(Cut paramCut1, Cut paramCut2, Object paramObject)
  {
    this.entriesByLowerBound.put(paramCut1, new RangeMapEntry(paramCut1, paramCut2, paramObject));
  }
  
  public void remove(Range paramRange)
  {
    if (paramRange.isEmpty()) {
      return;
    }
    Map.Entry localEntry = this.entriesByLowerBound.lowerEntry(paramRange.lowerBound);
    if (localEntry != null)
    {
      localObject = (RangeMapEntry)localEntry.getValue();
      if (((RangeMapEntry)localObject).getUpperBound().compareTo(paramRange.lowerBound) > 0)
      {
        if (((RangeMapEntry)localObject).getUpperBound().compareTo(paramRange.upperBound) > 0) {
          putRangeMapEntry(paramRange.upperBound, ((RangeMapEntry)localObject).getUpperBound(), ((RangeMapEntry)localEntry.getValue()).getValue());
        }
        putRangeMapEntry(((RangeMapEntry)localObject).getLowerBound(), paramRange.lowerBound, ((RangeMapEntry)localEntry.getValue()).getValue());
      }
    }
    Object localObject = this.entriesByLowerBound.lowerEntry(paramRange.upperBound);
    if (localObject != null)
    {
      RangeMapEntry localRangeMapEntry = (RangeMapEntry)((Map.Entry)localObject).getValue();
      if (localRangeMapEntry.getUpperBound().compareTo(paramRange.upperBound) > 0)
      {
        putRangeMapEntry(paramRange.upperBound, localRangeMapEntry.getUpperBound(), ((RangeMapEntry)((Map.Entry)localObject).getValue()).getValue());
        this.entriesByLowerBound.remove(paramRange.lowerBound);
      }
    }
    this.entriesByLowerBound.subMap(paramRange.lowerBound, paramRange.upperBound).clear();
  }
  
  public Map asMapOfRanges()
  {
    return new AsMapOfRanges(null);
  }
  
  public RangeMap subRangeMap(Range paramRange)
  {
    if (paramRange.equals(Range.all())) {
      return this;
    }
    return new SubRangeMap(paramRange);
  }
  
  private RangeMap emptySubRangeMap()
  {
    return EMPTY_SUB_RANGE_MAP;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RangeMap))
    {
      RangeMap localRangeMap = (RangeMap)paramObject;
      return asMapOfRanges().equals(localRangeMap.asMapOfRanges());
    }
    return false;
  }
  
  public int hashCode()
  {
    return asMapOfRanges().hashCode();
  }
  
  public String toString()
  {
    return this.entriesByLowerBound.values().toString();
  }
  
  private class SubRangeMap
    implements RangeMap
  {
    private final Range subRange;
    
    SubRangeMap(Range paramRange)
    {
      this.subRange = paramRange;
    }
    
    public Object get(Comparable paramComparable)
    {
      return this.subRange.contains(paramComparable) ? TreeRangeMap.this.get(paramComparable) : null;
    }
    
    public Map.Entry getEntry(Comparable paramComparable)
    {
      if (this.subRange.contains(paramComparable))
      {
        Map.Entry localEntry = TreeRangeMap.this.getEntry(paramComparable);
        if (localEntry != null) {
          return Maps.immutableEntry(((Range)localEntry.getKey()).intersection(this.subRange), localEntry.getValue());
        }
      }
      return null;
    }
    
    public Range span()
    {
      Map.Entry localEntry1 = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
      Cut localCut1;
      if ((localEntry1 != null) && (((TreeRangeMap.RangeMapEntry)localEntry1.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0))
      {
        localCut1 = this.subRange.lowerBound;
      }
      else
      {
        localCut1 = (Cut)TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
        if ((localCut1 == null) || (localCut1.compareTo(this.subRange.upperBound) >= 0)) {
          throw new NoSuchElementException();
        }
      }
      Map.Entry localEntry2 = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
      if (localEntry2 == null) {
        throw new NoSuchElementException();
      }
      Cut localCut2;
      if (((TreeRangeMap.RangeMapEntry)localEntry2.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
        localCut2 = this.subRange.upperBound;
      } else {
        localCut2 = ((TreeRangeMap.RangeMapEntry)localEntry2.getValue()).getUpperBound();
      }
      return Range.create(localCut1, localCut2);
    }
    
    public void put(Range paramRange, Object paramObject)
    {
      Preconditions.checkArgument(this.subRange.encloses(paramRange), "Cannot put range %s into a subRangeMap(%s)", new Object[] { paramRange, this.subRange });
      TreeRangeMap.this.put(paramRange, paramObject);
    }
    
    public void putAll(RangeMap paramRangeMap)
    {
      if (paramRangeMap.asMapOfRanges().isEmpty()) {
        return;
      }
      Range localRange = paramRangeMap.span();
      Preconditions.checkArgument(this.subRange.encloses(localRange), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", new Object[] { localRange, this.subRange });
      TreeRangeMap.this.putAll(paramRangeMap);
    }
    
    public void clear()
    {
      TreeRangeMap.this.remove(this.subRange);
    }
    
    public void remove(Range paramRange)
    {
      if (paramRange.isConnected(this.subRange)) {
        TreeRangeMap.this.remove(paramRange.intersection(this.subRange));
      }
    }
    
    public RangeMap subRangeMap(Range paramRange)
    {
      if (!paramRange.isConnected(this.subRange)) {
        return TreeRangeMap.this.emptySubRangeMap();
      }
      return TreeRangeMap.this.subRangeMap(paramRange.intersection(this.subRange));
    }
    
    public Map asMapOfRanges()
    {
      return new SubRangeMapAsMap();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof RangeMap))
      {
        RangeMap localRangeMap = (RangeMap)paramObject;
        return asMapOfRanges().equals(localRangeMap.asMapOfRanges());
      }
      return false;
    }
    
    public int hashCode()
    {
      return asMapOfRanges().hashCode();
    }
    
    public String toString()
    {
      return asMapOfRanges().toString();
    }
    
    class SubRangeMapAsMap
      extends AbstractMap
    {
      SubRangeMapAsMap() {}
      
      public boolean containsKey(Object paramObject)
      {
        return get(paramObject) != null;
      }
      
      public Object get(Object paramObject)
      {
        try
        {
          if ((paramObject instanceof Range))
          {
            Range localRange = (Range)paramObject;
            if ((!TreeRangeMap.SubRangeMap.this.subRange.encloses(localRange)) || (localRange.isEmpty())) {
              return null;
            }
            TreeRangeMap.RangeMapEntry localRangeMapEntry = null;
            if (localRange.lowerBound.compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) == 0)
            {
              Map.Entry localEntry = TreeRangeMap.this.entriesByLowerBound.floorEntry(localRange.lowerBound);
              if (localEntry != null) {
                localRangeMapEntry = (TreeRangeMap.RangeMapEntry)localEntry.getValue();
              }
            }
            else
            {
              localRangeMapEntry = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(localRange.lowerBound);
            }
            if ((localRangeMapEntry != null) && (localRangeMapEntry.getKey().isConnected(TreeRangeMap.SubRangeMap.this.subRange)) && (localRangeMapEntry.getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange).equals(localRange))) {
              return localRangeMapEntry.getValue();
            }
          }
        }
        catch (ClassCastException localClassCastException)
        {
          return null;
        }
        return null;
      }
      
      public Object remove(Object paramObject)
      {
        Object localObject = get(paramObject);
        if (localObject != null)
        {
          Range localRange = (Range)paramObject;
          TreeRangeMap.this.remove(localRange);
          return localObject;
        }
        return null;
      }
      
      public void clear()
      {
        TreeRangeMap.SubRangeMap.this.clear();
      }
      
      private boolean removeIf(Predicate paramPredicate)
      {
        ArrayList localArrayList = Lists.newArrayList();
        Iterator localIterator = entrySet().iterator();
        Object localObject;
        while (localIterator.hasNext())
        {
          localObject = (Map.Entry)localIterator.next();
          if (paramPredicate.apply(localObject)) {
            localArrayList.add(((Map.Entry)localObject).getKey());
          }
        }
        localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          localObject = (Range)localIterator.next();
          TreeRangeMap.this.remove((Range)localObject);
        }
        return !localArrayList.isEmpty();
      }
      
      public Set keySet()
      {
        new Maps.KeySet()
        {
          Map map()
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this;
          }
          
          public boolean remove(Object paramAnonymousObject)
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.remove(paramAnonymousObject) != null;
          }
          
          public boolean retainAll(Collection paramAnonymousCollection)
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeIf(Predicates.compose(Predicates.not(Predicates.in(paramAnonymousCollection)), Maps.keyFunction()));
          }
        };
      }
      
      public Set entrySet()
      {
        new Maps.EntrySet()
        {
          Map map()
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this;
          }
          
          public Iterator iterator()
          {
            if (TreeRangeMap.SubRangeMap.this.subRange.isEmpty()) {
              return Iterators.emptyIterator();
            }
            Cut localCut = (Cut)Objects.firstNonNull(TreeRangeMap.this.entriesByLowerBound.floorKey(TreeRangeMap.SubRangeMap.this.subRange.lowerBound), TreeRangeMap.SubRangeMap.this.subRange.lowerBound);
            final Iterator localIterator = TreeRangeMap.this.entriesByLowerBound.tailMap(localCut, true).values().iterator();
            new AbstractIterator()
            {
              protected Map.Entry computeNext()
              {
                while (localIterator.hasNext())
                {
                  TreeRangeMap.RangeMapEntry localRangeMapEntry = (TreeRangeMap.RangeMapEntry)localIterator.next();
                  if (localRangeMapEntry.getLowerBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.upperBound) >= 0) {
                    break;
                  }
                  if (localRangeMapEntry.getUpperBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) > 0) {
                    return Maps.immutableEntry(localRangeMapEntry.getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange), localRangeMapEntry.getValue());
                  }
                }
                return (Map.Entry)endOfData();
              }
            };
          }
          
          public boolean retainAll(Collection paramAnonymousCollection)
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeIf(Predicates.not(Predicates.in(paramAnonymousCollection)));
          }
          
          public int size()
          {
            return Iterators.size(iterator());
          }
          
          public boolean isEmpty()
          {
            return !iterator().hasNext();
          }
        };
      }
      
      public Collection values()
      {
        new Maps.Values()
        {
          Map map()
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this;
          }
          
          public boolean removeAll(Collection paramAnonymousCollection)
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeIf(Predicates.compose(Predicates.in(paramAnonymousCollection), Maps.valueFunction()));
          }
          
          public boolean retainAll(Collection paramAnonymousCollection)
          {
            return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeIf(Predicates.compose(Predicates.not(Predicates.in(paramAnonymousCollection)), Maps.valueFunction()));
          }
        };
      }
    }
  }
  
  private final class AsMapOfRanges
    extends AbstractMap
  {
    private AsMapOfRanges() {}
    
    public boolean containsKey(Object paramObject)
    {
      return get(paramObject) != null;
    }
    
    public Object get(Object paramObject)
    {
      if ((paramObject instanceof Range))
      {
        Range localRange = (Range)paramObject;
        TreeRangeMap.RangeMapEntry localRangeMapEntry = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(localRange.lowerBound);
        if ((localRangeMapEntry != null) && (localRangeMapEntry.getKey().equals(localRange))) {
          return localRangeMapEntry.getValue();
        }
      }
      return null;
    }
    
    public Set entrySet()
    {
      new AbstractSet()
      {
        public Iterator iterator()
        {
          return TreeRangeMap.this.entriesByLowerBound.values().iterator();
        }
        
        public int size()
        {
          return TreeRangeMap.this.entriesByLowerBound.size();
        }
      };
    }
  }
  
  private static final class RangeMapEntry
    extends AbstractMapEntry
  {
    private final Range range;
    private final Object value;
    
    RangeMapEntry(Cut paramCut1, Cut paramCut2, Object paramObject)
    {
      this(Range.create(paramCut1, paramCut2), paramObject);
    }
    
    RangeMapEntry(Range paramRange, Object paramObject)
    {
      this.range = paramRange;
      this.value = paramObject;
    }
    
    public Range getKey()
    {
      return this.range;
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
    public boolean contains(Comparable paramComparable)
    {
      return this.range.contains(paramComparable);
    }
    
    Cut getLowerBound()
    {
      return this.range.lowerBound;
    }
    
    Cut getUpperBound()
    {
      return this.range.upperBound;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TreeRangeMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */