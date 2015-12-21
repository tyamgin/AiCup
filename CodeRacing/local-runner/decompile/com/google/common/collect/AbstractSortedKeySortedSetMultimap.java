package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap
  extends AbstractSortedSetMultimap
{
  AbstractSortedKeySortedSetMultimap(SortedMap paramSortedMap)
  {
    super(paramSortedMap);
  }
  
  public SortedMap asMap()
  {
    return (SortedMap)super.asMap();
  }
  
  SortedMap backingMap()
  {
    return (SortedMap)super.backingMap();
  }
  
  public SortedSet keySet()
  {
    return (SortedSet)super.keySet();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractSortedKeySortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */