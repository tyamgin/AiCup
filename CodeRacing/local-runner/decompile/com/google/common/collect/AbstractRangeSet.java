package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractRangeSet
  implements RangeSet
{
  public boolean contains(Comparable paramComparable)
  {
    return rangeContaining(paramComparable) != null;
  }
  
  public Range rangeContaining(Comparable paramComparable)
  {
    Preconditions.checkNotNull(paramComparable);
    Iterator localIterator = asRanges().iterator();
    while (localIterator.hasNext())
    {
      Range localRange = (Range)localIterator.next();
      if (localRange.contains(paramComparable)) {
        return localRange;
      }
    }
    return null;
  }
  
  public boolean isEmpty()
  {
    return asRanges().isEmpty();
  }
  
  public void add(Range paramRange)
  {
    throw new UnsupportedOperationException();
  }
  
  public void remove(Range paramRange)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    remove(Range.all());
  }
  
  public boolean enclosesAll(RangeSet paramRangeSet)
  {
    Iterator localIterator = paramRangeSet.asRanges().iterator();
    while (localIterator.hasNext())
    {
      Range localRange = (Range)localIterator.next();
      if (!encloses(localRange)) {
        return false;
      }
    }
    return true;
  }
  
  public void addAll(RangeSet paramRangeSet)
  {
    Iterator localIterator = paramRangeSet.asRanges().iterator();
    while (localIterator.hasNext())
    {
      Range localRange = (Range)localIterator.next();
      add(localRange);
    }
  }
  
  public void removeAll(RangeSet paramRangeSet)
  {
    Iterator localIterator = paramRangeSet.asRanges().iterator();
    while (localIterator.hasNext())
    {
      Range localRange = (Range)localIterator.next();
      remove(localRange);
    }
  }
  
  public boolean encloses(Range paramRange)
  {
    Iterator localIterator = asRanges().iterator();
    while (localIterator.hasNext())
    {
      Range localRange = (Range)localIterator.next();
      if (localRange.encloses(paramRange)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RangeSet))
    {
      RangeSet localRangeSet = (RangeSet)paramObject;
      return asRanges().equals(localRangeSet.asRanges());
    }
    return false;
  }
  
  public final int hashCode()
  {
    return asRanges().hashCode();
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    Iterator localIterator = asRanges().iterator();
    while (localIterator.hasNext())
    {
      Range localRange = (Range)localIterator.next();
      localStringBuilder.append(localRange);
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractRangeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */