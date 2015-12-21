package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableSet
  extends ImmutableCollection
  implements Set
{
  static final int MAX_TABLE_SIZE = 1073741824;
  private static final double DESIRED_LOAD_FACTOR = 0.7D;
  private static final int CUTOFF = (int)Math.floor(7.516192768E8D);
  
  public static ImmutableSet of()
  {
    return EmptyImmutableSet.INSTANCE;
  }
  
  public static ImmutableSet of(Object paramObject)
  {
    return new SingletonImmutableSet(paramObject);
  }
  
  public static ImmutableSet of(Object paramObject1, Object paramObject2)
  {
    return construct(2, new Object[] { paramObject1, paramObject2 });
  }
  
  public static ImmutableSet of(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return construct(3, new Object[] { paramObject1, paramObject2, paramObject3 });
  }
  
  public static ImmutableSet of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return construct(4, new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
  }
  
  public static ImmutableSet of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5)
  {
    return construct(5, new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
  }
  
  public static ImmutableSet of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object... paramVarArgs)
  {
    int i = 6;
    Object[] arrayOfObject = new Object[6 + paramVarArgs.length];
    arrayOfObject[0] = paramObject1;
    arrayOfObject[1] = paramObject2;
    arrayOfObject[2] = paramObject3;
    arrayOfObject[3] = paramObject4;
    arrayOfObject[4] = paramObject5;
    arrayOfObject[5] = paramObject6;
    System.arraycopy(paramVarArgs, 0, arrayOfObject, 6, paramVarArgs.length);
    return construct(arrayOfObject.length, arrayOfObject);
  }
  
  private static ImmutableSet construct(int paramInt, Object... paramVarArgs)
  {
    switch (paramInt)
    {
    case 0: 
      return of();
    case 1: 
      Object localObject1 = paramVarArgs[0];
      return of(localObject1);
    }
    int i = chooseTableSize(paramInt);
    Object[] arrayOfObject = new Object[i];
    int j = i - 1;
    int k = 0;
    int m = 0;
    for (int n = 0; n < paramInt; n++)
    {
      Object localObject3 = ObjectArrays.checkElementNotNull(paramVarArgs[n], n);
      int i1 = localObject3.hashCode();
      for (int i2 = Hashing.smear(i1);; i2++)
      {
        int i3 = i2 & j;
        Object localObject4 = arrayOfObject[i3];
        if (localObject4 == null)
        {
          paramVarArgs[(m++)] = localObject3;
          arrayOfObject[i3] = localObject3;
          k += i1;
        }
        else
        {
          if (localObject4.equals(localObject3)) {
            break;
          }
        }
      }
    }
    Arrays.fill(paramVarArgs, m, paramInt, null);
    if (m == 1)
    {
      localObject2 = paramVarArgs[0];
      return new SingletonImmutableSet(localObject2, k);
    }
    if (i != chooseTableSize(m)) {
      return construct(m, paramVarArgs);
    }
    Object localObject2 = m < paramVarArgs.length ? ObjectArrays.arraysCopyOf(paramVarArgs, m) : paramVarArgs;
    return new RegularImmutableSet((Object[])localObject2, k, arrayOfObject, j);
  }
  
  @VisibleForTesting
  static int chooseTableSize(int paramInt)
  {
    if (paramInt < CUTOFF)
    {
      int i = Integer.highestOneBit(paramInt - 1) << 1;
      while (i * 0.7D < paramInt) {
        i <<= 1;
      }
      return i;
    }
    Preconditions.checkArgument(paramInt < 1073741824, "collection too large");
    return 1073741824;
  }
  
  public static ImmutableSet copyOf(Object[] paramArrayOfObject)
  {
    switch (paramArrayOfObject.length)
    {
    case 0: 
      return of();
    case 1: 
      return of(paramArrayOfObject[0]);
    }
    return construct(paramArrayOfObject.length, (Object[])paramArrayOfObject.clone());
  }
  
  public static ImmutableSet copyOf(Iterable paramIterable)
  {
    return (paramIterable instanceof Collection) ? copyOf(Collections2.cast(paramIterable)) : copyOf(paramIterable.iterator());
  }
  
  public static ImmutableSet copyOf(Iterator paramIterator)
  {
    if (!paramIterator.hasNext()) {
      return of();
    }
    Object localObject = paramIterator.next();
    if (!paramIterator.hasNext()) {
      return of(localObject);
    }
    return new Builder().add(localObject).addAll(paramIterator).build();
  }
  
  public static ImmutableSet copyOf(Collection paramCollection)
  {
    Object localObject;
    if (((paramCollection instanceof ImmutableSet)) && (!(paramCollection instanceof ImmutableSortedSet)))
    {
      localObject = (ImmutableSet)paramCollection;
      if (!((ImmutableSet)localObject).isPartialView()) {
        return (ImmutableSet)localObject;
      }
    }
    else if ((paramCollection instanceof EnumSet))
    {
      localObject = EnumSet.copyOf((EnumSet)paramCollection);
      ImmutableSet localImmutableSet = ImmutableEnumSet.asImmutable((EnumSet)localObject);
      return localImmutableSet;
    }
    return copyFromCollection(paramCollection);
  }
  
  private static ImmutableSet copyFromCollection(Collection paramCollection)
  {
    Object[] arrayOfObject = paramCollection.toArray();
    switch (arrayOfObject.length)
    {
    case 0: 
      return of();
    case 1: 
      Object localObject = arrayOfObject[0];
      return of(localObject);
    }
    return construct(arrayOfObject.length, arrayOfObject);
  }
  
  boolean isHashCodeFast()
  {
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (((paramObject instanceof ImmutableSet)) && (isHashCodeFast()) && (((ImmutableSet)paramObject).isHashCodeFast()) && (hashCode() != paramObject.hashCode())) {
      return false;
    }
    return Sets.equalsImpl(this, paramObject);
  }
  
  public int hashCode()
  {
    return Sets.hashCodeImpl(this);
  }
  
  public abstract UnmodifiableIterator iterator();
  
  Object writeReplace()
  {
    return new SerializedForm(toArray());
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static class Builder
    extends ImmutableCollection.Builder
  {
    Object[] contents;
    int size;
    
    public Builder()
    {
      this(4);
    }
    
    Builder(int paramInt)
    {
      Preconditions.checkArgument(paramInt >= 0, "capacity must be >= 0 but was %s", new Object[] { Integer.valueOf(paramInt) });
      this.contents = new Object[paramInt];
      this.size = 0;
    }
    
    Builder ensureCapacity(int paramInt)
    {
      if (this.contents.length < paramInt) {
        this.contents = ObjectArrays.arraysCopyOf(this.contents, expandedCapacity(this.contents.length, paramInt));
      }
      return this;
    }
    
    public Builder add(Object paramObject)
    {
      ensureCapacity(this.size + 1);
      this.contents[(this.size++)] = Preconditions.checkNotNull(paramObject);
      return this;
    }
    
    public Builder add(Object... paramVarArgs)
    {
      for (int i = 0; i < paramVarArgs.length; i++) {
        ObjectArrays.checkElementNotNull(paramVarArgs[i], i);
      }
      ensureCapacity(this.size + paramVarArgs.length);
      System.arraycopy(paramVarArgs, 0, this.contents, this.size, paramVarArgs.length);
      this.size += paramVarArgs.length;
      return this;
    }
    
    public Builder addAll(Iterable paramIterable)
    {
      if ((paramIterable instanceof Collection))
      {
        Collection localCollection = (Collection)paramIterable;
        ensureCapacity(this.size + localCollection.size());
      }
      super.addAll(paramIterable);
      return this;
    }
    
    public Builder addAll(Iterator paramIterator)
    {
      super.addAll(paramIterator);
      return this;
    }
    
    public ImmutableSet build()
    {
      ImmutableSet localImmutableSet = ImmutableSet.construct(this.size, this.contents);
      this.size = localImmutableSet.size();
      return localImmutableSet;
    }
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
      return ImmutableSet.copyOf(this.elements);
    }
  }
  
  static abstract class ArrayImmutableSet
    extends ImmutableSet
  {
    final transient Object[] elements;
    
    ArrayImmutableSet(Object[] paramArrayOfObject)
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
      return asList().iterator();
    }
    
    public Object[] toArray()
    {
      return asList().toArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return asList().toArray(paramArrayOfObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      if (paramCollection == this) {
        return true;
      }
      if (!(paramCollection instanceof ArrayImmutableSet)) {
        return super.containsAll(paramCollection);
      }
      if (paramCollection.size() > size()) {
        return false;
      }
      for (Object localObject : ((ArrayImmutableSet)paramCollection).elements) {
        if (!contains(localObject)) {
          return false;
        }
      }
      return true;
    }
    
    boolean isPartialView()
    {
      return false;
    }
    
    ImmutableList createAsList()
    {
      return new RegularImmutableAsList(this, this.elements);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */