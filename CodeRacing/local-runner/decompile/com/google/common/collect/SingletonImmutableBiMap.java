package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map.Entry;

@GwtCompatible(serializable=true, emulated=true)
final class SingletonImmutableBiMap
  extends ImmutableBiMap
{
  final transient Object singleKey;
  final transient Object singleValue;
  transient ImmutableBiMap inverse;
  
  SingletonImmutableBiMap(Object paramObject1, Object paramObject2)
  {
    this.singleKey = paramObject1;
    this.singleValue = paramObject2;
  }
  
  private SingletonImmutableBiMap(Object paramObject1, Object paramObject2, ImmutableBiMap paramImmutableBiMap)
  {
    this.singleKey = paramObject1;
    this.singleValue = paramObject2;
    this.inverse = paramImmutableBiMap;
  }
  
  SingletonImmutableBiMap(Map.Entry paramEntry)
  {
    this(paramEntry.getKey(), paramEntry.getValue());
  }
  
  public Object get(Object paramObject)
  {
    return this.singleKey.equals(paramObject) ? this.singleValue : null;
  }
  
  public int size()
  {
    return 1;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.singleKey.equals(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return this.singleValue.equals(paramObject);
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  ImmutableSet createEntrySet()
  {
    return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
  }
  
  ImmutableSet createKeySet()
  {
    return ImmutableSet.of(this.singleKey);
  }
  
  public ImmutableBiMap inverse()
  {
    ImmutableBiMap localImmutableBiMap = this.inverse;
    if (localImmutableBiMap == null) {
      return this.inverse = new SingletonImmutableBiMap(this.singleValue, this.singleKey, this);
    }
    return localImmutableBiMap;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SingletonImmutableBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */