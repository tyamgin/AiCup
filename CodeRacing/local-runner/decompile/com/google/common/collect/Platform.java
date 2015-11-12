package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.lang.reflect.Array;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
class Platform
{
  static Object[] clone(Object[] paramArrayOfObject)
  {
    return (Object[])paramArrayOfObject.clone();
  }
  
  static Object[] newArray(Object[] paramArrayOfObject, int paramInt)
  {
    Class localClass = paramArrayOfObject.getClass().getComponentType();
    Object[] arrayOfObject = (Object[])Array.newInstance(localClass, paramInt);
    return arrayOfObject;
  }
  
  static MapMaker tryWeakKeys(MapMaker paramMapMaker)
  {
    return paramMapMaker.weakKeys();
  }
  
  static SortedMap mapsTransformEntriesSortedMap(SortedMap paramSortedMap, Maps.EntryTransformer paramEntryTransformer)
  {
    return (paramSortedMap instanceof NavigableMap) ? Maps.transformEntries((NavigableMap)paramSortedMap, paramEntryTransformer) : Maps.transformEntriesIgnoreNavigable(paramSortedMap, paramEntryTransformer);
  }
  
  static SortedMap mapsAsMapSortedSet(SortedSet paramSortedSet, Function paramFunction)
  {
    return (paramSortedSet instanceof NavigableSet) ? Maps.asMap((NavigableSet)paramSortedSet, paramFunction) : Maps.asMapSortedIgnoreNavigable(paramSortedSet, paramFunction);
  }
  
  static SortedSet setsFilterSortedSet(SortedSet paramSortedSet, Predicate paramPredicate)
  {
    return (paramSortedSet instanceof NavigableSet) ? Sets.filter((NavigableSet)paramSortedSet, paramPredicate) : Sets.filterSortedIgnoreNavigable(paramSortedSet, paramPredicate);
  }
  
  static SortedMap mapsFilterSortedMap(SortedMap paramSortedMap, Predicate paramPredicate)
  {
    return (paramSortedMap instanceof NavigableMap) ? Maps.filterEntries((NavigableMap)paramSortedMap, paramPredicate) : Maps.filterSortedIgnoreNavigable(paramSortedMap, paramPredicate);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Platform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */