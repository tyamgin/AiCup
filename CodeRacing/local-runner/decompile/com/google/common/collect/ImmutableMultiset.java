package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

@GwtCompatible(serializable=true)
public abstract class ImmutableMultiset
  extends ImmutableCollection
  implements Multiset
{
  private transient ImmutableSet entrySet;
  
  public static ImmutableMultiset of()
  {
    return EmptyImmutableMultiset.INSTANCE;
  }
  
  public static ImmutableMultiset of(Object paramObject)
  {
    return copyOfInternal(new Object[] { paramObject });
  }
  
  public static ImmutableMultiset of(Object paramObject1, Object paramObject2)
  {
    return copyOfInternal(new Object[] { paramObject1, paramObject2 });
  }
  
  public static ImmutableMultiset of(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return copyOfInternal(new Object[] { paramObject1, paramObject2, paramObject3 });
  }
  
  public static ImmutableMultiset of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return copyOfInternal(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
  }
  
  public static ImmutableMultiset of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5)
  {
    return copyOfInternal(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
  }
  
  public static ImmutableMultiset of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object... paramVarArgs)
  {
    int i = paramVarArgs.length + 6;
    ArrayList localArrayList = new ArrayList(i);
    Collections.addAll(localArrayList, new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
    Collections.addAll(localArrayList, paramVarArgs);
    return copyOf(localArrayList);
  }
  
  public static ImmutableMultiset copyOf(Object[] paramArrayOfObject)
  {
    return copyOf(Arrays.asList(paramArrayOfObject));
  }
  
  public static ImmutableMultiset copyOf(Iterable paramIterable)
  {
    if ((paramIterable instanceof ImmutableMultiset))
    {
      localObject = (ImmutableMultiset)paramIterable;
      if (!((ImmutableMultiset)localObject).isPartialView()) {
        return (ImmutableMultiset)localObject;
      }
    }
    Object localObject = (paramIterable instanceof Multiset) ? Multisets.cast(paramIterable) : LinkedHashMultiset.create(paramIterable);
    return copyOfInternal((Multiset)localObject);
  }
  
  private static ImmutableMultiset copyOfInternal(Object... paramVarArgs)
  {
    return copyOf(Arrays.asList(paramVarArgs));
  }
  
  private static ImmutableMultiset copyOfInternal(Multiset paramMultiset)
  {
    return copyFromEntries(paramMultiset.entrySet());
  }
  
  static ImmutableMultiset copyFromEntries(Collection paramCollection)
  {
    long l = 0L;
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      int i = localEntry.getCount();
      if (i > 0)
      {
        localBuilder.put(localEntry.getElement(), Integer.valueOf(i));
        l += i;
      }
    }
    if (l == 0L) {
      return of();
    }
    return new RegularImmutableMultiset(localBuilder.build(), Ints.saturatedCast(l));
  }
  
  public static ImmutableMultiset copyOf(Iterator paramIterator)
  {
    LinkedHashMultiset localLinkedHashMultiset = LinkedHashMultiset.create();
    Iterators.addAll(localLinkedHashMultiset, paramIterator);
    return copyOfInternal(localLinkedHashMultiset);
  }
  
  public UnmodifiableIterator iterator()
  {
    final UnmodifiableIterator localUnmodifiableIterator = entrySet().iterator();
    new UnmodifiableIterator()
    {
      int remaining;
      Object element;
      
      public boolean hasNext()
      {
        return (this.remaining > 0) || (localUnmodifiableIterator.hasNext());
      }
      
      public Object next()
      {
        if (this.remaining <= 0)
        {
          Multiset.Entry localEntry = (Multiset.Entry)localUnmodifiableIterator.next();
          this.element = localEntry.getElement();
          this.remaining = localEntry.getCount();
        }
        this.remaining -= 1;
        return this.element;
      }
    };
  }
  
  public boolean contains(Object paramObject)
  {
    return count(paramObject) > 0;
  }
  
  public boolean containsAll(Collection paramCollection)
  {
    return elementSet().containsAll(paramCollection);
  }
  
  @Deprecated
  public final int add(Object paramObject, int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final int remove(Object paramObject, int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final int setCount(Object paramObject, int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final boolean setCount(Object paramObject, int paramInt1, int paramInt2)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Multiset))
    {
      Multiset localMultiset = (Multiset)paramObject;
      if (size() != localMultiset.size()) {
        return false;
      }
      Iterator localIterator = localMultiset.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
        if (count(localEntry.getElement()) != localEntry.getCount()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public int hashCode()
  {
    return Sets.hashCodeImpl(entrySet());
  }
  
  public String toString()
  {
    return entrySet().toString();
  }
  
  public ImmutableSet entrySet()
  {
    ImmutableSet localImmutableSet = this.entrySet;
    return localImmutableSet == null ? (this.entrySet = createEntrySet()) : localImmutableSet;
  }
  
  abstract ImmutableSet createEntrySet();
  
  Object writeReplace()
  {
    return new SerializedForm(this);
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static class Builder
    extends ImmutableCollection.Builder
  {
    final Multiset contents;
    
    public Builder()
    {
      this(LinkedHashMultiset.create());
    }
    
    Builder(Multiset paramMultiset)
    {
      this.contents = paramMultiset;
    }
    
    public Builder add(Object paramObject)
    {
      this.contents.add(Preconditions.checkNotNull(paramObject));
      return this;
    }
    
    public Builder addCopies(Object paramObject, int paramInt)
    {
      this.contents.add(Preconditions.checkNotNull(paramObject), paramInt);
      return this;
    }
    
    public Builder setCount(Object paramObject, int paramInt)
    {
      this.contents.setCount(Preconditions.checkNotNull(paramObject), paramInt);
      return this;
    }
    
    public Builder add(Object... paramVarArgs)
    {
      super.add(paramVarArgs);
      return this;
    }
    
    public Builder addAll(Iterable paramIterable)
    {
      if ((paramIterable instanceof Multiset))
      {
        Multiset localMultiset = Multisets.cast(paramIterable);
        Iterator localIterator = localMultiset.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
          addCopies(localEntry.getElement(), localEntry.getCount());
        }
      }
      else
      {
        super.addAll(paramIterable);
      }
      return this;
    }
    
    public Builder addAll(Iterator paramIterator)
    {
      super.addAll(paramIterator);
      return this;
    }
    
    public ImmutableMultiset build()
    {
      return ImmutableMultiset.copyOf(this.contents);
    }
  }
  
  private static class SerializedForm
    implements Serializable
  {
    final Object[] elements;
    final int[] counts;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(Multiset paramMultiset)
    {
      int i = paramMultiset.entrySet().size();
      this.elements = new Object[i];
      this.counts = new int[i];
      int j = 0;
      Iterator localIterator = paramMultiset.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
        this.elements[j] = localEntry.getElement();
        this.counts[j] = localEntry.getCount();
        j++;
      }
    }
    
    Object readResolve()
    {
      LinkedHashMultiset localLinkedHashMultiset = LinkedHashMultiset.create(this.elements.length);
      for (int i = 0; i < this.elements.length; i++) {
        localLinkedHashMultiset.add(this.elements[i], this.counts[i]);
      }
      return ImmutableMultiset.copyOf(localLinkedHashMultiset);
    }
  }
  
  static class EntrySetSerializedForm
    implements Serializable
  {
    final ImmutableMultiset multiset;
    
    EntrySetSerializedForm(ImmutableMultiset paramImmutableMultiset)
    {
      this.multiset = paramImmutableMultiset;
    }
    
    Object readResolve()
    {
      return this.multiset.entrySet();
    }
  }
  
  abstract class EntrySet
    extends ImmutableSet
  {
    private static final long serialVersionUID = 0L;
    
    EntrySet() {}
    
    boolean isPartialView()
    {
      return ImmutableMultiset.this.isPartialView();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Multiset.Entry))
      {
        Multiset.Entry localEntry = (Multiset.Entry)paramObject;
        if (localEntry.getCount() <= 0) {
          return false;
        }
        int i = ImmutableMultiset.this.count(localEntry.getElement());
        return i == localEntry.getCount();
      }
      return false;
    }
    
    public Object[] toArray()
    {
      Object[] arrayOfObject = new Object[size()];
      return toArray(arrayOfObject);
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      int i = size();
      if (paramArrayOfObject.length < i) {
        paramArrayOfObject = ObjectArrays.newArray(paramArrayOfObject, i);
      } else if (paramArrayOfObject.length > i) {
        paramArrayOfObject[i] = null;
      }
      Object[] arrayOfObject = paramArrayOfObject;
      int j = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
        arrayOfObject[(j++)] = localEntry;
      }
      return paramArrayOfObject;
    }
    
    public int hashCode()
    {
      return ImmutableMultiset.this.hashCode();
    }
    
    Object writeReplace()
    {
      return new ImmutableMultiset.EntrySetSerializedForm(ImmutableMultiset.this);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */