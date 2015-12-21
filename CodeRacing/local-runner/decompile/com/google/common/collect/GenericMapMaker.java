package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Beta
@GwtCompatible(emulated=true)
public abstract class GenericMapMaker
{
  @GwtIncompatible("To be supported")
  MapMaker.RemovalListener removalListener;
  
  @GwtIncompatible("To be supported")
  abstract GenericMapMaker keyEquivalence(Equivalence paramEquivalence);
  
  public abstract GenericMapMaker initialCapacity(int paramInt);
  
  abstract GenericMapMaker maximumSize(int paramInt);
  
  public abstract GenericMapMaker concurrencyLevel(int paramInt);
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public abstract GenericMapMaker weakKeys();
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public abstract GenericMapMaker weakValues();
  
  @GwtIncompatible("java.lang.ref.SoftReference")
  public abstract GenericMapMaker softValues();
  
  abstract GenericMapMaker expireAfterWrite(long paramLong, TimeUnit paramTimeUnit);
  
  @GwtIncompatible("To be supported")
  abstract GenericMapMaker expireAfterAccess(long paramLong, TimeUnit paramTimeUnit);
  
  @GwtIncompatible("To be supported")
  MapMaker.RemovalListener getRemovalListener()
  {
    return (MapMaker.RemovalListener)Objects.firstNonNull(this.removalListener, NullListener.INSTANCE);
  }
  
  public abstract ConcurrentMap makeMap();
  
  @GwtIncompatible("MapMakerInternalMap")
  abstract MapMakerInternalMap makeCustomMap();
  
  @Deprecated
  public abstract ConcurrentMap makeComputingMap(Function paramFunction);
  
  @GwtIncompatible("To be supported")
  static enum NullListener
    implements MapMaker.RemovalListener
  {
    INSTANCE;
    
    public void onRemoval(MapMaker.RemovalNotification paramRemovalNotification) {}
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\GenericMapMaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */