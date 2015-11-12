package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;

class ComputingConcurrentHashMap
  extends MapMakerInternalMap
{
  final Function computingFunction;
  private static final long serialVersionUID = 4L;
  
  ComputingConcurrentHashMap(MapMaker paramMapMaker, Function paramFunction)
  {
    super(paramMapMaker);
    this.computingFunction = ((Function)Preconditions.checkNotNull(paramFunction));
  }
  
  MapMakerInternalMap.Segment createSegment(int paramInt1, int paramInt2)
  {
    return new ComputingSegment(this, paramInt1, paramInt2);
  }
  
  ComputingSegment segmentFor(int paramInt)
  {
    return (ComputingSegment)super.segmentFor(paramInt);
  }
  
  Object getOrCompute(Object paramObject)
    throws ExecutionException
  {
    int i = hash(Preconditions.checkNotNull(paramObject));
    return segmentFor(i).getOrCompute(paramObject, i, this.computingFunction);
  }
  
  Object writeReplace()
  {
    return new ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
  }
  
  static final class ComputingSerializationProxy
    extends MapMakerInternalMap.AbstractSerializationProxy
  {
    final Function computingFunction;
    private static final long serialVersionUID = 4L;
    
    ComputingSerializationProxy(MapMakerInternalMap.Strength paramStrength1, MapMakerInternalMap.Strength paramStrength2, Equivalence paramEquivalence1, Equivalence paramEquivalence2, long paramLong1, long paramLong2, int paramInt1, int paramInt2, MapMaker.RemovalListener paramRemovalListener, ConcurrentMap paramConcurrentMap, Function paramFunction)
    {
      super(paramStrength2, paramEquivalence1, paramEquivalence2, paramLong1, paramLong2, paramInt1, paramInt2, paramRemovalListener, paramConcurrentMap);
      this.computingFunction = paramFunction;
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      writeMapTo(paramObjectOutputStream);
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      MapMaker localMapMaker = readMapMaker(paramObjectInputStream);
      this.delegate = localMapMaker.makeComputingMap(this.computingFunction);
      readEntries(paramObjectInputStream);
    }
    
    Object readResolve()
    {
      return this.delegate;
    }
  }
  
  private static final class ComputingValueReference
    implements MapMakerInternalMap.ValueReference
  {
    final Function computingFunction;
    volatile MapMakerInternalMap.ValueReference computedReference = MapMakerInternalMap.unset();
    
    public ComputingValueReference(Function paramFunction)
    {
      this.computingFunction = paramFunction;
    }
    
    public Object get()
    {
      return null;
    }
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return this;
    }
    
    public boolean isComputingReference()
    {
      return true;
    }
    
    public Object waitForValue()
      throws ExecutionException
    {
      if (this.computedReference == MapMakerInternalMap.UNSET)
      {
        int i = 0;
        try
        {
          synchronized (this)
          {
            while (this.computedReference == MapMakerInternalMap.UNSET) {
              try
              {
                wait();
              }
              catch (InterruptedException localInterruptedException)
              {
                i = 1;
              }
            }
          }
        }
        finally
        {
          if (i != 0) {
            Thread.currentThread().interrupt();
          }
        }
      }
      return this.computedReference.waitForValue();
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramValueReference)
    {
      setValueReference(paramValueReference);
    }
    
    Object compute(Object paramObject, int paramInt)
      throws ExecutionException
    {
      Object localObject;
      try
      {
        localObject = this.computingFunction.apply(paramObject);
      }
      catch (Throwable localThrowable)
      {
        setValueReference(new ComputingConcurrentHashMap.ComputationExceptionReference(localThrowable));
        throw new ExecutionException(localThrowable);
      }
      setValueReference(new ComputingConcurrentHashMap.ComputedReference(localObject));
      return localObject;
    }
    
    void setValueReference(MapMakerInternalMap.ValueReference paramValueReference)
    {
      synchronized (this)
      {
        if (this.computedReference == MapMakerInternalMap.UNSET)
        {
          this.computedReference = paramValueReference;
          notifyAll();
        }
      }
    }
  }
  
  private static final class ComputedReference
    implements MapMakerInternalMap.ValueReference
  {
    final Object value;
    
    ComputedReference(Object paramObject)
    {
      this.value = paramObject;
    }
    
    public Object get()
    {
      return this.value;
    }
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return this;
    }
    
    public boolean isComputingReference()
    {
      return false;
    }
    
    public Object waitForValue()
    {
      return get();
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramValueReference) {}
  }
  
  private static final class ComputationExceptionReference
    implements MapMakerInternalMap.ValueReference
  {
    final Throwable t;
    
    ComputationExceptionReference(Throwable paramThrowable)
    {
      this.t = paramThrowable;
    }
    
    public Object get()
    {
      return null;
    }
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return this;
    }
    
    public boolean isComputingReference()
    {
      return false;
    }
    
    public Object waitForValue()
      throws ExecutionException
    {
      throw new ExecutionException(this.t);
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramValueReference) {}
  }
  
  static final class ComputingSegment
    extends MapMakerInternalMap.Segment
  {
    ComputingSegment(MapMakerInternalMap paramMapMakerInternalMap, int paramInt1, int paramInt2)
    {
      super(paramInt1, paramInt2);
    }
    
    Object getOrCompute(Object paramObject, int paramInt, Function paramFunction)
      throws ExecutionException
    {
      try
      {
        Object localObject1;
        Object localObject3;
        do
        {
          localObject1 = getEntry(paramObject, paramInt);
          if (localObject1 != null)
          {
            Object localObject2 = getLiveValue((MapMakerInternalMap.ReferenceEntry)localObject1);
            if (localObject2 != null)
            {
              recordRead((MapMakerInternalMap.ReferenceEntry)localObject1);
              localObject4 = localObject2;
              return localObject4;
            }
          }
          if ((localObject1 == null) || (!((MapMakerInternalMap.ReferenceEntry)localObject1).getValueReference().isComputingReference()))
          {
            int i = 1;
            localObject4 = null;
            lock();
            try
            {
              preWriteCleanup();
              int j = this.count - 1;
              AtomicReferenceArray localAtomicReferenceArray = this.table;
              int k = paramInt & localAtomicReferenceArray.length() - 1;
              MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(k);
              for (localObject1 = localReferenceEntry; localObject1 != null; localObject1 = ((MapMakerInternalMap.ReferenceEntry)localObject1).getNext())
              {
                Object localObject6 = ((MapMakerInternalMap.ReferenceEntry)localObject1).getKey();
                if ((((MapMakerInternalMap.ReferenceEntry)localObject1).getHash() == paramInt) && (localObject6 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject6)))
                {
                  MapMakerInternalMap.ValueReference localValueReference = ((MapMakerInternalMap.ReferenceEntry)localObject1).getValueReference();
                  if (localValueReference.isComputingReference())
                  {
                    i = 0;
                    break;
                  }
                  Object localObject7 = ((MapMakerInternalMap.ReferenceEntry)localObject1).getValueReference().get();
                  if (localObject7 == null)
                  {
                    enqueueNotification(localObject6, paramInt, localObject7, MapMaker.RemovalCause.COLLECTED);
                  }
                  else if ((this.map.expires()) && (this.map.isExpired((MapMakerInternalMap.ReferenceEntry)localObject1)))
                  {
                    enqueueNotification(localObject6, paramInt, localObject7, MapMaker.RemovalCause.EXPIRED);
                  }
                  else
                  {
                    recordLockedRead((MapMakerInternalMap.ReferenceEntry)localObject1);
                    Object localObject8 = localObject7;
                    return localObject8;
                  }
                  this.evictionQueue.remove(localObject1);
                  this.expirationQueue.remove(localObject1);
                  this.count = j;
                  break;
                }
              }
              if (i != 0)
              {
                localObject4 = new ComputingConcurrentHashMap.ComputingValueReference(paramFunction);
                if (localObject1 == null)
                {
                  localObject1 = newEntry(paramObject, paramInt, localReferenceEntry);
                  ((MapMakerInternalMap.ReferenceEntry)localObject1).setValueReference((MapMakerInternalMap.ValueReference)localObject4);
                  localAtomicReferenceArray.set(k, localObject1);
                }
                else
                {
                  ((MapMakerInternalMap.ReferenceEntry)localObject1).setValueReference((MapMakerInternalMap.ValueReference)localObject4);
                }
              }
            }
            finally
            {
              unlock();
            }
            if (i != 0)
            {
              Object localObject5 = compute(paramObject, paramInt, (MapMakerInternalMap.ReferenceEntry)localObject1, (ComputingConcurrentHashMap.ComputingValueReference)localObject4);
              return localObject5;
            }
          }
          Preconditions.checkState(!Thread.holdsLock(localObject1), "Recursive computation");
          localObject3 = ((MapMakerInternalMap.ReferenceEntry)localObject1).getValueReference().waitForValue();
        } while (localObject3 == null);
        recordRead((MapMakerInternalMap.ReferenceEntry)localObject1);
        Object localObject4 = localObject3;
        return localObject4;
      }
      finally
      {
        postReadCleanup();
      }
    }
    
    Object compute(Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry, ComputingConcurrentHashMap.ComputingValueReference paramComputingValueReference)
      throws ExecutionException
    {
      Object localObject1 = null;
      long l1 = System.nanoTime();
      long l2 = 0L;
      try
      {
        synchronized (paramReferenceEntry)
        {
          localObject1 = paramComputingValueReference.compute(paramObject, paramInt);
          l2 = System.nanoTime();
        }
        if (localObject1 != null)
        {
          ??? = put(paramObject, paramInt, localObject1, true);
          if (??? != null) {
            enqueueNotification(paramObject, paramInt, localObject1, MapMaker.RemovalCause.REPLACED);
          }
        }
        ??? = localObject1;
        return ???;
      }
      finally
      {
        if (l2 == 0L) {
          l2 = System.nanoTime();
        }
        if (localObject1 == null) {
          clearValue(paramObject, paramInt, paramComputingValueReference);
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ComputingConcurrentHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */