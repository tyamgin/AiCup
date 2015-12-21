package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

@GwtCompatible(emulated=true)
public abstract class ImmutableCollection
  implements Serializable, Collection
{
  static final ImmutableCollection EMPTY_IMMUTABLE_COLLECTION = new EmptyImmutableCollection(null);
  private transient ImmutableList asList;
  
  public abstract UnmodifiableIterator iterator();
  
  public Object[] toArray()
  {
    return ObjectArrays.toArrayImpl(this);
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return ObjectArrays.toArrayImpl(this, paramArrayOfObject);
  }
  
  public boolean contains(Object paramObject)
  {
    return (paramObject != null) && (Iterators.contains(iterator(), paramObject));
  }
  
  public boolean containsAll(Collection paramCollection)
  {
    return Collections2.containsAllImpl(this, paramCollection);
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public String toString()
  {
    return Collections2.toStringImpl(this);
  }
  
  @Deprecated
  public final boolean add(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final boolean remove(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final boolean addAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final boolean removeAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final boolean retainAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableList asList()
  {
    ImmutableList localImmutableList = this.asList;
    return localImmutableList == null ? (this.asList = createAsList()) : localImmutableList;
  }
  
  ImmutableList createAsList()
  {
    switch (size())
    {
    case 0: 
      return ImmutableList.of();
    case 1: 
      return ImmutableList.of(iterator().next());
    }
    return new RegularImmutableAsList(this, toArray());
  }
  
  abstract boolean isPartialView();
  
  Object writeReplace()
  {
    return new SerializedForm(toArray());
  }
  
  public static abstract class Builder
  {
    static final int DEFAULT_INITIAL_CAPACITY = 4;
    
    @VisibleForTesting
    static int expandedCapacity(int paramInt1, int paramInt2)
    {
      if (paramInt2 < 0) {
        throw new AssertionError("cannot store more than MAX_VALUE elements");
      }
      int i = paramInt1 + (paramInt1 >> 1) + 1;
      if (i < paramInt2) {
        i = Integer.highestOneBit(paramInt2 - 1) << 1;
      }
      if (i < 0) {
        i = Integer.MAX_VALUE;
      }
      return i;
    }
    
    public abstract Builder add(Object paramObject);
    
    public Builder add(Object... paramVarArgs)
    {
      for (Object localObject : paramVarArgs) {
        add(localObject);
      }
      return this;
    }
    
    public Builder addAll(Iterable paramIterable)
    {
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        add(localObject);
      }
      return this;
    }
    
    public Builder addAll(Iterator paramIterator)
    {
      while (paramIterator.hasNext()) {
        add(paramIterator.next());
      }
      return this;
    }
    
    public abstract ImmutableCollection build();
  }
  
  private static class SerializedForm
    implements Serializable
  {
    final Object[] elements;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(Object[] paramArrayOfObject)
    {
      this.elements = paramArrayOfObject;
    }
    
    Object readResolve()
    {
      return this.elements.length == 0 ? ImmutableCollection.EMPTY_IMMUTABLE_COLLECTION : new ImmutableCollection.ArrayImmutableCollection(Platform.clone(this.elements));
    }
  }
  
  private static class ArrayImmutableCollection
    extends ImmutableCollection
  {
    private final Object[] elements;
    
    ArrayImmutableCollection(Object[] paramArrayOfObject)
    {
      this.elements = paramArrayOfObject;
    }
    
    public int size()
    {
      return this.elements.length;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public UnmodifiableIterator iterator()
    {
      return Iterators.forArray(this.elements);
    }
    
    ImmutableList createAsList()
    {
      return this.elements.length == 1 ? new SingletonImmutableList(this.elements[0]) : new RegularImmutableList(this.elements);
    }
    
    boolean isPartialView()
    {
      return false;
    }
  }
  
  private static class EmptyImmutableCollection
    extends ImmutableCollection
  {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    
    public int size()
    {
      return 0;
    }
    
    public boolean isEmpty()
    {
      return true;
    }
    
    public boolean contains(Object paramObject)
    {
      return false;
    }
    
    public UnmodifiableIterator iterator()
    {
      return Iterators.EMPTY_LIST_ITERATOR;
    }
    
    public Object[] toArray()
    {
      return EMPTY_ARRAY;
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      if (paramArrayOfObject.length > 0) {
        paramArrayOfObject[0] = null;
      }
      return paramArrayOfObject;
    }
    
    ImmutableList createAsList()
    {
      return ImmutableList.of();
    }
    
    boolean isPartialView()
    {
      return false;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableCollection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */