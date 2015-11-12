package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated=true)
final class EmptyImmutableBiMap
  extends ImmutableBiMap
{
  static final EmptyImmutableBiMap INSTANCE = new EmptyImmutableBiMap();
  
  public ImmutableBiMap inverse()
  {
    return this;
  }
  
  public int size()
  {
    return 0;
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public Object get(Object paramObject)
  {
    return null;
  }
  
  public ImmutableSet entrySet()
  {
    return ImmutableSet.of();
  }
  
  ImmutableSet createEntrySet()
  {
    throw new AssertionError("should never be called");
  }
  
  public ImmutableSet keySet()
  {
    return ImmutableSet.of();
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  Object readResolve()
  {
    return INSTANCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */