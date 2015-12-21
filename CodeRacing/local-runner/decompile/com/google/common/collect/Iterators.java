package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

@GwtCompatible(emulated=true)
public final class Iterators
{
  static final UnmodifiableListIterator EMPTY_LIST_ITERATOR = new UnmodifiableListIterator()
  {
    public boolean hasNext()
    {
      return false;
    }
    
    public Object next()
    {
      throw new NoSuchElementException();
    }
    
    public boolean hasPrevious()
    {
      return false;
    }
    
    public Object previous()
    {
      throw new NoSuchElementException();
    }
    
    public int nextIndex()
    {
      return 0;
    }
    
    public int previousIndex()
    {
      return -1;
    }
  };
  private static final Iterator EMPTY_MODIFIABLE_ITERATOR = new Iterator()
  {
    public boolean hasNext()
    {
      return false;
    }
    
    public Object next()
    {
      throw new NoSuchElementException();
    }
    
    public void remove()
    {
      throw new IllegalStateException();
    }
  };
  
  public static UnmodifiableIterator emptyIterator()
  {
    return emptyListIterator();
  }
  
  static UnmodifiableListIterator emptyListIterator()
  {
    return EMPTY_LIST_ITERATOR;
  }
  
  static Iterator emptyModifiableIterator()
  {
    return EMPTY_MODIFIABLE_ITERATOR;
  }
  
  public static UnmodifiableIterator unmodifiableIterator(Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramIterator);
    if ((paramIterator instanceof UnmodifiableIterator)) {
      return (UnmodifiableIterator)paramIterator;
    }
    new UnmodifiableIterator()
    {
      public boolean hasNext()
      {
        return this.val$iterator.hasNext();
      }
      
      public Object next()
      {
        return this.val$iterator.next();
      }
    };
  }
  
  @Deprecated
  public static UnmodifiableIterator unmodifiableIterator(UnmodifiableIterator paramUnmodifiableIterator)
  {
    return (UnmodifiableIterator)Preconditions.checkNotNull(paramUnmodifiableIterator);
  }
  
  public static int size(Iterator paramIterator)
  {
    for (int i = 0; paramIterator.hasNext(); i++) {
      paramIterator.next();
    }
    return i;
  }
  
  public static boolean contains(Iterator paramIterator, Object paramObject)
  {
    if (paramObject == null)
    {
      do
      {
        if (!paramIterator.hasNext()) {
          break;
        }
      } while (paramIterator.next() != null);
      return true;
    }
    while (paramIterator.hasNext()) {
      if (paramObject.equals(paramIterator.next())) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean removeAll(Iterator paramIterator, Collection paramCollection)
  {
    Preconditions.checkNotNull(paramCollection);
    boolean bool = false;
    while (paramIterator.hasNext()) {
      if (paramCollection.contains(paramIterator.next()))
      {
        paramIterator.remove();
        bool = true;
      }
    }
    return bool;
  }
  
  public static boolean removeIf(Iterator paramIterator, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    boolean bool = false;
    while (paramIterator.hasNext()) {
      if (paramPredicate.apply(paramIterator.next()))
      {
        paramIterator.remove();
        bool = true;
      }
    }
    return bool;
  }
  
  public static boolean retainAll(Iterator paramIterator, Collection paramCollection)
  {
    Preconditions.checkNotNull(paramCollection);
    boolean bool = false;
    while (paramIterator.hasNext()) {
      if (!paramCollection.contains(paramIterator.next()))
      {
        paramIterator.remove();
        bool = true;
      }
    }
    return bool;
  }
  
  public static boolean elementsEqual(Iterator paramIterator1, Iterator paramIterator2)
  {
    while (paramIterator1.hasNext())
    {
      if (!paramIterator2.hasNext()) {
        return false;
      }
      Object localObject1 = paramIterator1.next();
      Object localObject2 = paramIterator2.next();
      if (!Objects.equal(localObject1, localObject2)) {
        return false;
      }
    }
    return !paramIterator2.hasNext();
  }
  
  public static String toString(Iterator paramIterator)
  {
    return ']';
  }
  
  public static Object getOnlyElement(Iterator paramIterator)
  {
    Object localObject = paramIterator.next();
    if (!paramIterator.hasNext()) {
      return localObject;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("expected one element but was: <" + localObject);
    for (int i = 0; (i < 4) && (paramIterator.hasNext()); i++) {
      localStringBuilder.append(", " + paramIterator.next());
    }
    if (paramIterator.hasNext()) {
      localStringBuilder.append(", ...");
    }
    localStringBuilder.append('>');
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static Object getOnlyElement(Iterator paramIterator, Object paramObject)
  {
    return paramIterator.hasNext() ? getOnlyElement(paramIterator) : paramObject;
  }
  
  @GwtIncompatible("Array.newInstance(Class, int)")
  public static Object[] toArray(Iterator paramIterator, Class paramClass)
  {
    ArrayList localArrayList = Lists.newArrayList(paramIterator);
    return Iterables.toArray(localArrayList, paramClass);
  }
  
  public static boolean addAll(Collection paramCollection, Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramCollection);
    boolean bool = false;
    while (paramIterator.hasNext()) {
      bool |= paramCollection.add(paramIterator.next());
    }
    return bool;
  }
  
  public static int frequency(Iterator paramIterator, Object paramObject)
  {
    int i = 0;
    if (paramObject == null) {
      while (paramIterator.hasNext()) {
        if (paramIterator.next() == null) {
          i++;
        }
      }
    }
    while (paramIterator.hasNext()) {
      if (paramObject.equals(paramIterator.next())) {
        i++;
      }
    }
    return i;
  }
  
  public static Iterator cycle(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    new Iterator()
    {
      Iterator iterator = Iterators.emptyIterator();
      Iterator removeFrom;
      
      public boolean hasNext()
      {
        if (!this.iterator.hasNext()) {
          this.iterator = this.val$iterable.iterator();
        }
        return this.iterator.hasNext();
      }
      
      public Object next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        this.removeFrom = this.iterator;
        return this.iterator.next();
      }
      
      public void remove()
      {
        Preconditions.checkState(this.removeFrom != null, "no calls to next() since last call to remove()");
        this.removeFrom.remove();
        this.removeFrom = null;
      }
    };
  }
  
  public static Iterator cycle(Object... paramVarArgs)
  {
    return cycle(Lists.newArrayList(paramVarArgs));
  }
  
  public static Iterator concat(Iterator paramIterator1, Iterator paramIterator2)
  {
    Preconditions.checkNotNull(paramIterator1);
    Preconditions.checkNotNull(paramIterator2);
    return concat(Arrays.asList(new Iterator[] { paramIterator1, paramIterator2 }).iterator());
  }
  
  public static Iterator concat(Iterator paramIterator1, Iterator paramIterator2, Iterator paramIterator3)
  {
    Preconditions.checkNotNull(paramIterator1);
    Preconditions.checkNotNull(paramIterator2);
    Preconditions.checkNotNull(paramIterator3);
    return concat(Arrays.asList(new Iterator[] { paramIterator1, paramIterator2, paramIterator3 }).iterator());
  }
  
  public static Iterator concat(Iterator paramIterator1, Iterator paramIterator2, Iterator paramIterator3, Iterator paramIterator4)
  {
    Preconditions.checkNotNull(paramIterator1);
    Preconditions.checkNotNull(paramIterator2);
    Preconditions.checkNotNull(paramIterator3);
    Preconditions.checkNotNull(paramIterator4);
    return concat(Arrays.asList(new Iterator[] { paramIterator1, paramIterator2, paramIterator3, paramIterator4 }).iterator());
  }
  
  public static Iterator concat(Iterator... paramVarArgs)
  {
    return concat(ImmutableList.copyOf(paramVarArgs).iterator());
  }
  
  public static Iterator concat(Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramIterator);
    new Iterator()
    {
      Iterator current = Iterators.emptyIterator();
      Iterator removeFrom;
      
      public boolean hasNext()
      {
        boolean bool;
        while ((!(bool = ((Iterator)Preconditions.checkNotNull(this.current)).hasNext())) && (this.val$inputs.hasNext())) {
          this.current = ((Iterator)this.val$inputs.next());
        }
        return bool;
      }
      
      public Object next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        this.removeFrom = this.current;
        return this.current.next();
      }
      
      public void remove()
      {
        Preconditions.checkState(this.removeFrom != null, "no calls to next() since last call to remove()");
        this.removeFrom.remove();
        this.removeFrom = null;
      }
    };
  }
  
  public static UnmodifiableIterator partition(Iterator paramIterator, int paramInt)
  {
    return partitionImpl(paramIterator, paramInt, false);
  }
  
  public static UnmodifiableIterator paddedPartition(Iterator paramIterator, int paramInt)
  {
    return partitionImpl(paramIterator, paramInt, true);
  }
  
  private static UnmodifiableIterator partitionImpl(Iterator paramIterator, final int paramInt, final boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramIterator);
    Preconditions.checkArgument(paramInt > 0);
    new UnmodifiableIterator()
    {
      public boolean hasNext()
      {
        return this.val$iterator.hasNext();
      }
      
      public List next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Object[] arrayOfObject = new Object[paramInt];
        for (int i = 0; (i < paramInt) && (this.val$iterator.hasNext()); i++) {
          arrayOfObject[i] = this.val$iterator.next();
        }
        for (int j = i; j < paramInt; j++) {
          arrayOfObject[j] = null;
        }
        List localList = Collections.unmodifiableList(Arrays.asList(arrayOfObject));
        return (paramBoolean) || (i == paramInt) ? localList : localList.subList(0, i);
      }
    };
  }
  
  public static UnmodifiableIterator filter(Iterator paramIterator, final Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramIterator);
    Preconditions.checkNotNull(paramPredicate);
    new AbstractIterator()
    {
      protected Object computeNext()
      {
        while (this.val$unfiltered.hasNext())
        {
          Object localObject = this.val$unfiltered.next();
          if (paramPredicate.apply(localObject)) {
            return localObject;
          }
        }
        return endOfData();
      }
    };
  }
  
  @GwtIncompatible("Class.isInstance")
  public static UnmodifiableIterator filter(Iterator paramIterator, Class paramClass)
  {
    return filter(paramIterator, Predicates.instanceOf(paramClass));
  }
  
  public static boolean any(Iterator paramIterator, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    while (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      if (paramPredicate.apply(localObject)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean all(Iterator paramIterator, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    while (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      if (!paramPredicate.apply(localObject)) {
        return false;
      }
    }
    return true;
  }
  
  public static Object find(Iterator paramIterator, Predicate paramPredicate)
  {
    return filter(paramIterator, paramPredicate).next();
  }
  
  public static Object find(Iterator paramIterator, Predicate paramPredicate, Object paramObject)
  {
    UnmodifiableIterator localUnmodifiableIterator = filter(paramIterator, paramPredicate);
    return localUnmodifiableIterator.hasNext() ? localUnmodifiableIterator.next() : paramObject;
  }
  
  public static Optional tryFind(Iterator paramIterator, Predicate paramPredicate)
  {
    UnmodifiableIterator localUnmodifiableIterator = filter(paramIterator, paramPredicate);
    return localUnmodifiableIterator.hasNext() ? Optional.of(localUnmodifiableIterator.next()) : Optional.absent();
  }
  
  public static int indexOf(Iterator paramIterator, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate, "predicate");
    for (int i = 0; paramIterator.hasNext(); i++)
    {
      Object localObject = paramIterator.next();
      if (paramPredicate.apply(localObject)) {
        return i;
      }
    }
    return -1;
  }
  
  public static Iterator transform(Iterator paramIterator, final Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    new TransformedIterator(paramIterator)
    {
      Object transform(Object paramAnonymousObject)
      {
        return paramFunction.apply(paramAnonymousObject);
      }
    };
  }
  
  public static Object get(Iterator paramIterator, int paramInt)
  {
    checkNonnegative(paramInt);
    int i = 0;
    while (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      if (i++ == paramInt) {
        return localObject;
      }
    }
    throw new IndexOutOfBoundsException("position (" + paramInt + ") must be less than the number of elements that remained (" + i + ")");
  }
  
  private static void checkNonnegative(int paramInt)
  {
    if (paramInt < 0) {
      throw new IndexOutOfBoundsException("position (" + paramInt + ") must not be negative");
    }
  }
  
  public static Object get(Iterator paramIterator, int paramInt, Object paramObject)
  {
    checkNonnegative(paramInt);
    try
    {
      return get(paramIterator, paramInt);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
    return paramObject;
  }
  
  public static Object getNext(Iterator paramIterator, Object paramObject)
  {
    return paramIterator.hasNext() ? paramIterator.next() : paramObject;
  }
  
  public static Object getLast(Iterator paramIterator)
  {
    for (;;)
    {
      Object localObject = paramIterator.next();
      if (!paramIterator.hasNext()) {
        return localObject;
      }
    }
  }
  
  public static Object getLast(Iterator paramIterator, Object paramObject)
  {
    return paramIterator.hasNext() ? getLast(paramIterator) : paramObject;
  }
  
  public static int advance(Iterator paramIterator, int paramInt)
  {
    Preconditions.checkNotNull(paramIterator);
    Preconditions.checkArgument(paramInt >= 0, "number to advance cannot be negative");
    for (int i = 0; (i < paramInt) && (paramIterator.hasNext()); i++) {
      paramIterator.next();
    }
    return i;
  }
  
  public static Iterator limit(final Iterator paramIterator, int paramInt)
  {
    Preconditions.checkNotNull(paramIterator);
    Preconditions.checkArgument(paramInt >= 0, "limit is negative");
    new Iterator()
    {
      private int count;
      
      public boolean hasNext()
      {
        return (this.count < this.val$limitSize) && (paramIterator.hasNext());
      }
      
      public Object next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        this.count += 1;
        return paramIterator.next();
      }
      
      public void remove()
      {
        paramIterator.remove();
      }
    };
  }
  
  public static Iterator consumingIterator(Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramIterator);
    new UnmodifiableIterator()
    {
      public boolean hasNext()
      {
        return this.val$iterator.hasNext();
      }
      
      public Object next()
      {
        Object localObject = this.val$iterator.next();
        this.val$iterator.remove();
        return localObject;
      }
    };
  }
  
  static Object pollNext(Iterator paramIterator)
  {
    if (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      paramIterator.remove();
      return localObject;
    }
    return null;
  }
  
  static void clear(Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramIterator);
    while (paramIterator.hasNext())
    {
      paramIterator.next();
      paramIterator.remove();
    }
  }
  
  public static UnmodifiableIterator forArray(final Object... paramVarArgs)
  {
    Preconditions.checkNotNull(paramVarArgs);
    new AbstractIndexedListIterator(paramVarArgs.length)
    {
      protected Object get(int paramAnonymousInt)
      {
        return paramVarArgs[paramAnonymousInt];
      }
    };
  }
  
  static UnmodifiableListIterator forArray(final Object[] paramArrayOfObject, final int paramInt1, int paramInt2, int paramInt3)
  {
    Preconditions.checkArgument(paramInt2 >= 0);
    int i = paramInt1 + paramInt2;
    Preconditions.checkPositionIndexes(paramInt1, i, paramArrayOfObject.length);
    new AbstractIndexedListIterator(paramInt2, paramInt3)
    {
      protected Object get(int paramAnonymousInt)
      {
        return paramArrayOfObject[(paramInt1 + paramAnonymousInt)];
      }
    };
  }
  
  public static UnmodifiableIterator singletonIterator(Object paramObject)
  {
    new UnmodifiableIterator()
    {
      boolean done;
      
      public boolean hasNext()
      {
        return !this.done;
      }
      
      public Object next()
      {
        if (this.done) {
          throw new NoSuchElementException();
        }
        this.done = true;
        return this.val$value;
      }
    };
  }
  
  public static UnmodifiableIterator forEnumeration(Enumeration paramEnumeration)
  {
    Preconditions.checkNotNull(paramEnumeration);
    new UnmodifiableIterator()
    {
      public boolean hasNext()
      {
        return this.val$enumeration.hasMoreElements();
      }
      
      public Object next()
      {
        return this.val$enumeration.nextElement();
      }
    };
  }
  
  public static Enumeration asEnumeration(Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramIterator);
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return this.val$iterator.hasNext();
      }
      
      public Object nextElement()
      {
        return this.val$iterator.next();
      }
    };
  }
  
  public static PeekingIterator peekingIterator(Iterator paramIterator)
  {
    if ((paramIterator instanceof PeekingImpl))
    {
      PeekingImpl localPeekingImpl = (PeekingImpl)paramIterator;
      return localPeekingImpl;
    }
    return new PeekingImpl(paramIterator);
  }
  
  @Deprecated
  public static PeekingIterator peekingIterator(PeekingIterator paramPeekingIterator)
  {
    return (PeekingIterator)Preconditions.checkNotNull(paramPeekingIterator);
  }
  
  @Beta
  public static UnmodifiableIterator mergeSorted(Iterable paramIterable, Comparator paramComparator)
  {
    Preconditions.checkNotNull(paramIterable, "iterators");
    Preconditions.checkNotNull(paramComparator, "comparator");
    return new MergingIterator(paramIterable, paramComparator);
  }
  
  static void checkRemove(boolean paramBoolean)
  {
    Preconditions.checkState(paramBoolean, "no calls to next() since the last call to remove()");
  }
  
  static ListIterator cast(Iterator paramIterator)
  {
    return (ListIterator)paramIterator;
  }
  
  private static class MergingIterator
    extends AbstractIterator
  {
    final Queue queue;
    final Comparator comparator;
    
    public MergingIterator(Iterable paramIterable, Comparator paramComparator)
    {
      this.comparator = paramComparator;
      Comparator local1 = new Comparator()
      {
        public int compare(PeekingIterator paramAnonymousPeekingIterator1, PeekingIterator paramAnonymousPeekingIterator2)
        {
          return Iterators.MergingIterator.this.comparator.compare(paramAnonymousPeekingIterator1.peek(), paramAnonymousPeekingIterator2.peek());
        }
      };
      this.queue = new PriorityQueue(2, local1);
      Iterator localIterator1 = paramIterable.iterator();
      while (localIterator1.hasNext())
      {
        Iterator localIterator2 = (Iterator)localIterator1.next();
        if (localIterator2.hasNext()) {
          this.queue.add(Iterators.peekingIterator(localIterator2));
        }
      }
    }
    
    protected Object computeNext()
    {
      if (this.queue.isEmpty()) {
        return endOfData();
      }
      PeekingIterator localPeekingIterator = (PeekingIterator)this.queue.poll();
      Object localObject = localPeekingIterator.next();
      if (localPeekingIterator.hasNext()) {
        this.queue.add(localPeekingIterator);
      }
      return localObject;
    }
  }
  
  private static class PeekingImpl
    implements PeekingIterator
  {
    private final Iterator iterator;
    private boolean hasPeeked;
    private Object peekedElement;
    
    public PeekingImpl(Iterator paramIterator)
    {
      this.iterator = ((Iterator)Preconditions.checkNotNull(paramIterator));
    }
    
    public boolean hasNext()
    {
      return (this.hasPeeked) || (this.iterator.hasNext());
    }
    
    public Object next()
    {
      if (!this.hasPeeked) {
        return this.iterator.next();
      }
      Object localObject = this.peekedElement;
      this.hasPeeked = false;
      this.peekedElement = null;
      return localObject;
    }
    
    public void remove()
    {
      Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
      this.iterator.remove();
    }
    
    public Object peek()
    {
      if (!this.hasPeeked)
      {
        this.peekedElement = this.iterator.next();
        this.hasPeeked = true;
      }
      return this.peekedElement;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Iterators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */