package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@GwtCompatible
public final class Multisets
{
  private static final Ordering DECREASING_COUNT_ORDERING = new Ordering()
  {
    public int compare(Multiset.Entry paramAnonymousEntry1, Multiset.Entry paramAnonymousEntry2)
    {
      return Ints.compare(paramAnonymousEntry2.getCount(), paramAnonymousEntry1.getCount());
    }
  };
  
  public static Multiset unmodifiableMultiset(Multiset paramMultiset)
  {
    if (((paramMultiset instanceof UnmodifiableMultiset)) || ((paramMultiset instanceof ImmutableMultiset)))
    {
      Multiset localMultiset = paramMultiset;
      return localMultiset;
    }
    return new UnmodifiableMultiset((Multiset)Preconditions.checkNotNull(paramMultiset));
  }
  
  @Deprecated
  public static Multiset unmodifiableMultiset(ImmutableMultiset paramImmutableMultiset)
  {
    return (Multiset)Preconditions.checkNotNull(paramImmutableMultiset);
  }
  
  @Beta
  public static SortedMultiset unmodifiableSortedMultiset(SortedMultiset paramSortedMultiset)
  {
    return new UnmodifiableSortedMultiset((SortedMultiset)Preconditions.checkNotNull(paramSortedMultiset));
  }
  
  public static Multiset.Entry immutableEntry(Object paramObject, int paramInt)
  {
    return new ImmutableEntry(paramObject, paramInt);
  }
  
  @Beta
  public static Multiset filter(Multiset paramMultiset, Predicate paramPredicate)
  {
    if ((paramMultiset instanceof FilteredMultiset))
    {
      FilteredMultiset localFilteredMultiset = (FilteredMultiset)paramMultiset;
      Predicate localPredicate = Predicates.and(localFilteredMultiset.predicate, paramPredicate);
      return new FilteredMultiset(localFilteredMultiset.unfiltered, localPredicate);
    }
    return new FilteredMultiset(paramMultiset, paramPredicate);
  }
  
  static int inferDistinctElements(Iterable paramIterable)
  {
    if ((paramIterable instanceof Multiset)) {
      return ((Multiset)paramIterable).elementSet().size();
    }
    return 11;
  }
  
  @Beta
  public static Multiset union(Multiset paramMultiset1, final Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    new AbstractMultiset()
    {
      public boolean contains(Object paramAnonymousObject)
      {
        return (this.val$multiset1.contains(paramAnonymousObject)) || (paramMultiset2.contains(paramAnonymousObject));
      }
      
      public boolean isEmpty()
      {
        return (this.val$multiset1.isEmpty()) && (paramMultiset2.isEmpty());
      }
      
      public int count(Object paramAnonymousObject)
      {
        return Math.max(this.val$multiset1.count(paramAnonymousObject), paramMultiset2.count(paramAnonymousObject));
      }
      
      Set createElementSet()
      {
        return Sets.union(this.val$multiset1.elementSet(), paramMultiset2.elementSet());
      }
      
      Iterator entryIterator()
      {
        final Iterator localIterator1 = this.val$multiset1.entrySet().iterator();
        final Iterator localIterator2 = paramMultiset2.entrySet().iterator();
        new AbstractIterator()
        {
          protected Multiset.Entry computeNext()
          {
            Multiset.Entry localEntry;
            Object localObject;
            if (localIterator1.hasNext())
            {
              localEntry = (Multiset.Entry)localIterator1.next();
              localObject = localEntry.getElement();
              int i = Math.max(localEntry.getCount(), Multisets.1.this.val$multiset2.count(localObject));
              return Multisets.immutableEntry(localObject, i);
            }
            while (localIterator2.hasNext())
            {
              localEntry = (Multiset.Entry)localIterator2.next();
              localObject = localEntry.getElement();
              if (!Multisets.1.this.val$multiset1.contains(localObject)) {
                return Multisets.immutableEntry(localObject, localEntry.getCount());
              }
            }
            return (Multiset.Entry)endOfData();
          }
        };
      }
      
      int distinctElements()
      {
        return elementSet().size();
      }
    };
  }
  
  public static Multiset intersection(Multiset paramMultiset1, final Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    new AbstractMultiset()
    {
      public int count(Object paramAnonymousObject)
      {
        int i = this.val$multiset1.count(paramAnonymousObject);
        return i == 0 ? 0 : Math.min(i, paramMultiset2.count(paramAnonymousObject));
      }
      
      Set createElementSet()
      {
        return Sets.intersection(this.val$multiset1.elementSet(), paramMultiset2.elementSet());
      }
      
      Iterator entryIterator()
      {
        final Iterator localIterator = this.val$multiset1.entrySet().iterator();
        new AbstractIterator()
        {
          protected Multiset.Entry computeNext()
          {
            while (localIterator.hasNext())
            {
              Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
              Object localObject = localEntry.getElement();
              int i = Math.min(localEntry.getCount(), Multisets.2.this.val$multiset2.count(localObject));
              if (i > 0) {
                return Multisets.immutableEntry(localObject, i);
              }
            }
            return (Multiset.Entry)endOfData();
          }
        };
      }
      
      int distinctElements()
      {
        return elementSet().size();
      }
    };
  }
  
  @Beta
  public static Multiset sum(Multiset paramMultiset1, final Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    new AbstractMultiset()
    {
      public boolean contains(Object paramAnonymousObject)
      {
        return (this.val$multiset1.contains(paramAnonymousObject)) || (paramMultiset2.contains(paramAnonymousObject));
      }
      
      public boolean isEmpty()
      {
        return (this.val$multiset1.isEmpty()) && (paramMultiset2.isEmpty());
      }
      
      public int size()
      {
        return this.val$multiset1.size() + paramMultiset2.size();
      }
      
      public int count(Object paramAnonymousObject)
      {
        return this.val$multiset1.count(paramAnonymousObject) + paramMultiset2.count(paramAnonymousObject);
      }
      
      Set createElementSet()
      {
        return Sets.union(this.val$multiset1.elementSet(), paramMultiset2.elementSet());
      }
      
      Iterator entryIterator()
      {
        final Iterator localIterator1 = this.val$multiset1.entrySet().iterator();
        final Iterator localIterator2 = paramMultiset2.entrySet().iterator();
        new AbstractIterator()
        {
          protected Multiset.Entry computeNext()
          {
            Multiset.Entry localEntry;
            Object localObject;
            if (localIterator1.hasNext())
            {
              localEntry = (Multiset.Entry)localIterator1.next();
              localObject = localEntry.getElement();
              int i = localEntry.getCount() + Multisets.3.this.val$multiset2.count(localObject);
              return Multisets.immutableEntry(localObject, i);
            }
            while (localIterator2.hasNext())
            {
              localEntry = (Multiset.Entry)localIterator2.next();
              localObject = localEntry.getElement();
              if (!Multisets.3.this.val$multiset1.contains(localObject)) {
                return Multisets.immutableEntry(localObject, localEntry.getCount());
              }
            }
            return (Multiset.Entry)endOfData();
          }
        };
      }
      
      int distinctElements()
      {
        return elementSet().size();
      }
    };
  }
  
  @Beta
  public static Multiset difference(Multiset paramMultiset1, final Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    new AbstractMultiset()
    {
      public int count(Object paramAnonymousObject)
      {
        int i = this.val$multiset1.count(paramAnonymousObject);
        return i == 0 ? 0 : Math.max(0, i - paramMultiset2.count(paramAnonymousObject));
      }
      
      Iterator entryIterator()
      {
        final Iterator localIterator = this.val$multiset1.entrySet().iterator();
        new AbstractIterator()
        {
          protected Multiset.Entry computeNext()
          {
            while (localIterator.hasNext())
            {
              Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
              Object localObject = localEntry.getElement();
              int i = localEntry.getCount() - Multisets.4.this.val$multiset2.count(localObject);
              if (i > 0) {
                return Multisets.immutableEntry(localObject, i);
              }
            }
            return (Multiset.Entry)endOfData();
          }
        };
      }
      
      int distinctElements()
      {
        return Iterators.size(entryIterator());
      }
    };
  }
  
  public static boolean containsOccurrences(Multiset paramMultiset1, Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    Iterator localIterator = paramMultiset2.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      int i = paramMultiset1.count(localEntry.getElement());
      if (i < localEntry.getCount()) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean retainOccurrences(Multiset paramMultiset1, Multiset paramMultiset2)
  {
    return retainOccurrencesImpl(paramMultiset1, paramMultiset2);
  }
  
  private static boolean retainOccurrencesImpl(Multiset paramMultiset1, Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    Iterator localIterator = paramMultiset1.entrySet().iterator();
    boolean bool = false;
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      int i = paramMultiset2.count(localEntry.getElement());
      if (i == 0)
      {
        localIterator.remove();
        bool = true;
      }
      else if (i < localEntry.getCount())
      {
        paramMultiset1.setCount(localEntry.getElement(), i);
        bool = true;
      }
    }
    return bool;
  }
  
  public static boolean removeOccurrences(Multiset paramMultiset1, Multiset paramMultiset2)
  {
    return removeOccurrencesImpl(paramMultiset1, paramMultiset2);
  }
  
  private static boolean removeOccurrencesImpl(Multiset paramMultiset1, Multiset paramMultiset2)
  {
    Preconditions.checkNotNull(paramMultiset1);
    Preconditions.checkNotNull(paramMultiset2);
    boolean bool = false;
    Iterator localIterator = paramMultiset1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      int i = paramMultiset2.count(localEntry.getElement());
      if (i >= localEntry.getCount())
      {
        localIterator.remove();
        bool = true;
      }
      else if (i > 0)
      {
        paramMultiset1.remove(localEntry.getElement(), i);
        bool = true;
      }
    }
    return bool;
  }
  
  static boolean equalsImpl(Multiset paramMultiset, Object paramObject)
  {
    if (paramObject == paramMultiset) {
      return true;
    }
    if ((paramObject instanceof Multiset))
    {
      Multiset localMultiset = (Multiset)paramObject;
      if ((paramMultiset.size() != localMultiset.size()) || (paramMultiset.entrySet().size() != localMultiset.entrySet().size())) {
        return false;
      }
      Iterator localIterator = localMultiset.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
        if (paramMultiset.count(localEntry.getElement()) != localEntry.getCount()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  static boolean addAllImpl(Multiset paramMultiset, Collection paramCollection)
  {
    if (paramCollection.isEmpty()) {
      return false;
    }
    if ((paramCollection instanceof Multiset))
    {
      Multiset localMultiset = cast(paramCollection);
      Iterator localIterator = localMultiset.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
        paramMultiset.add(localEntry.getElement(), localEntry.getCount());
      }
    }
    else
    {
      Iterators.addAll(paramMultiset, paramCollection.iterator());
    }
    return true;
  }
  
  static boolean removeAllImpl(Multiset paramMultiset, Collection paramCollection)
  {
    Collection localCollection = (paramCollection instanceof Multiset) ? ((Multiset)paramCollection).elementSet() : paramCollection;
    return paramMultiset.elementSet().removeAll(localCollection);
  }
  
  static boolean retainAllImpl(Multiset paramMultiset, Collection paramCollection)
  {
    Preconditions.checkNotNull(paramCollection);
    Collection localCollection = (paramCollection instanceof Multiset) ? ((Multiset)paramCollection).elementSet() : paramCollection;
    return paramMultiset.elementSet().retainAll(localCollection);
  }
  
  static int setCountImpl(Multiset paramMultiset, Object paramObject, int paramInt)
  {
    checkNonnegative(paramInt, "count");
    int i = paramMultiset.count(paramObject);
    int j = paramInt - i;
    if (j > 0) {
      paramMultiset.add(paramObject, j);
    } else if (j < 0) {
      paramMultiset.remove(paramObject, -j);
    }
    return i;
  }
  
  static boolean setCountImpl(Multiset paramMultiset, Object paramObject, int paramInt1, int paramInt2)
  {
    checkNonnegative(paramInt1, "oldCount");
    checkNonnegative(paramInt2, "newCount");
    if (paramMultiset.count(paramObject) == paramInt1)
    {
      paramMultiset.setCount(paramObject, paramInt2);
      return true;
    }
    return false;
  }
  
  static Iterator iteratorImpl(Multiset paramMultiset)
  {
    return new MultisetIteratorImpl(paramMultiset, paramMultiset.entrySet().iterator());
  }
  
  static int sizeImpl(Multiset paramMultiset)
  {
    long l = 0L;
    Iterator localIterator = paramMultiset.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      l += localEntry.getCount();
    }
    return Ints.saturatedCast(l);
  }
  
  static void checkNonnegative(int paramInt, String paramString)
  {
    Preconditions.checkArgument(paramInt >= 0, "%s cannot be negative: %s", new Object[] { paramString, Integer.valueOf(paramInt) });
  }
  
  static Multiset cast(Iterable paramIterable)
  {
    return (Multiset)paramIterable;
  }
  
  @Beta
  public static ImmutableMultiset copyHighestCountFirst(Multiset paramMultiset)
  {
    List localList = DECREASING_COUNT_ORDERING.sortedCopy(paramMultiset.entrySet());
    return ImmutableMultiset.copyFromEntries(localList);
  }
  
  static final class MultisetIteratorImpl
    implements Iterator
  {
    private final Multiset multiset;
    private final Iterator entryIterator;
    private Multiset.Entry currentEntry;
    private int laterCount;
    private int totalCount;
    private boolean canRemove;
    
    MultisetIteratorImpl(Multiset paramMultiset, Iterator paramIterator)
    {
      this.multiset = paramMultiset;
      this.entryIterator = paramIterator;
    }
    
    public boolean hasNext()
    {
      return (this.laterCount > 0) || (this.entryIterator.hasNext());
    }
    
    public Object next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      if (this.laterCount == 0)
      {
        this.currentEntry = ((Multiset.Entry)this.entryIterator.next());
        this.totalCount = (this.laterCount = this.currentEntry.getCount());
      }
      this.laterCount -= 1;
      this.canRemove = true;
      return this.currentEntry.getElement();
    }
    
    public void remove()
    {
      Iterators.checkRemove(this.canRemove);
      if (this.totalCount == 1) {
        this.entryIterator.remove();
      } else {
        this.multiset.remove(this.currentEntry.getElement());
      }
      this.totalCount -= 1;
      this.canRemove = false;
    }
  }
  
  static abstract class EntrySet
    extends Sets.ImprovedAbstractSet
  {
    abstract Multiset multiset();
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Multiset.Entry))
      {
        Multiset.Entry localEntry = (Multiset.Entry)paramObject;
        if (localEntry.getCount() <= 0) {
          return false;
        }
        int i = multiset().count(localEntry.getElement());
        return i == localEntry.getCount();
      }
      return false;
    }
    
    public boolean remove(Object paramObject)
    {
      if ((paramObject instanceof Multiset.Entry))
      {
        Multiset.Entry localEntry = (Multiset.Entry)paramObject;
        Object localObject = localEntry.getElement();
        int i = localEntry.getCount();
        if (i != 0)
        {
          Multiset localMultiset = multiset();
          return localMultiset.setCount(localObject, i, 0);
        }
      }
      return false;
    }
    
    public void clear()
    {
      multiset().clear();
    }
  }
  
  static abstract class ElementSet
    extends Sets.ImprovedAbstractSet
  {
    abstract Multiset multiset();
    
    public void clear()
    {
      multiset().clear();
    }
    
    public boolean contains(Object paramObject)
    {
      return multiset().contains(paramObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return multiset().containsAll(paramCollection);
    }
    
    public boolean isEmpty()
    {
      return multiset().isEmpty();
    }
    
    public Iterator iterator()
    {
      new TransformedIterator(multiset().entrySet().iterator())
      {
        Object transform(Multiset.Entry paramAnonymousEntry)
        {
          return paramAnonymousEntry.getElement();
        }
      };
    }
    
    public boolean remove(Object paramObject)
    {
      int i = multiset().count(paramObject);
      if (i > 0)
      {
        multiset().remove(paramObject, i);
        return true;
      }
      return false;
    }
    
    public int size()
    {
      return multiset().entrySet().size();
    }
  }
  
  static abstract class AbstractEntry
    implements Multiset.Entry
  {
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Multiset.Entry))
      {
        Multiset.Entry localEntry = (Multiset.Entry)paramObject;
        return (getCount() == localEntry.getCount()) && (Objects.equal(getElement(), localEntry.getElement()));
      }
      return false;
    }
    
    public int hashCode()
    {
      Object localObject = getElement();
      return (localObject == null ? 0 : localObject.hashCode()) ^ getCount();
    }
    
    public String toString()
    {
      String str = String.valueOf(getElement());
      int i = getCount();
      return str + " x " + i;
    }
  }
  
  private static final class FilteredMultiset
    extends AbstractMultiset
  {
    final Multiset unfiltered;
    final Predicate predicate;
    
    FilteredMultiset(Multiset paramMultiset, Predicate paramPredicate)
    {
      this.unfiltered = ((Multiset)Preconditions.checkNotNull(paramMultiset));
      this.predicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
    }
    
    Set createElementSet()
    {
      return Sets.filter(this.unfiltered.elementSet(), this.predicate);
    }
    
    Set createEntrySet()
    {
      Sets.filter(this.unfiltered.entrySet(), new Predicate()
      {
        public boolean apply(Multiset.Entry paramAnonymousEntry)
        {
          return Multisets.FilteredMultiset.this.predicate.apply(paramAnonymousEntry.getElement());
        }
      });
    }
    
    Iterator entryIterator()
    {
      throw new AssertionError("should never be called");
    }
    
    int distinctElements()
    {
      return elementSet().size();
    }
    
    public boolean contains(Object paramObject)
    {
      return count(paramObject) > 0;
    }
    
    public int count(Object paramObject)
    {
      int i = this.unfiltered.count(paramObject);
      if (i > 0)
      {
        Object localObject = paramObject;
        return this.predicate.apply(localObject) ? i : 0;
      }
      return 0;
    }
    
    public int add(Object paramObject, int paramInt)
    {
      Preconditions.checkArgument(this.predicate.apply(paramObject), "Element %s does not match predicate %s", new Object[] { paramObject, this.predicate });
      return this.unfiltered.add(paramObject, paramInt);
    }
    
    public int remove(Object paramObject, int paramInt)
    {
      Multisets.checkNonnegative(paramInt, "occurrences");
      if (paramInt == 0) {
        return count(paramObject);
      }
      return contains(paramObject) ? this.unfiltered.remove(paramObject, paramInt) : 0;
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return elementSet().removeAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return elementSet().retainAll(paramCollection);
    }
    
    public void clear()
    {
      elementSet().clear();
    }
  }
  
  static final class ImmutableEntry
    extends Multisets.AbstractEntry
    implements Serializable
  {
    final Object element;
    final int count;
    private static final long serialVersionUID = 0L;
    
    ImmutableEntry(Object paramObject, int paramInt)
    {
      this.element = paramObject;
      this.count = paramInt;
      Preconditions.checkArgument(paramInt >= 0);
    }
    
    public Object getElement()
    {
      return this.element;
    }
    
    public int getCount()
    {
      return this.count;
    }
  }
  
  static class UnmodifiableMultiset
    extends ForwardingMultiset
    implements Serializable
  {
    final Multiset delegate;
    transient Set elementSet;
    transient Set entrySet;
    private static final long serialVersionUID = 0L;
    
    UnmodifiableMultiset(Multiset paramMultiset)
    {
      this.delegate = paramMultiset;
    }
    
    protected Multiset delegate()
    {
      return this.delegate;
    }
    
    Set createElementSet()
    {
      return Collections.unmodifiableSet(this.delegate.elementSet());
    }
    
    public Set elementSet()
    {
      Set localSet = this.elementSet;
      return localSet == null ? (this.elementSet = createElementSet()) : localSet;
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      return localSet == null ? (this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet())) : localSet;
    }
    
    public Iterator iterator()
    {
      return Iterators.unmodifiableIterator(this.delegate.iterator());
    }
    
    public boolean add(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public int add(Object paramObject, int paramInt)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public int remove(Object paramObject, int paramInt)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public int setCount(Object paramObject, int paramInt)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean setCount(Object paramObject, int paramInt1, int paramInt2)
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Multisets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */