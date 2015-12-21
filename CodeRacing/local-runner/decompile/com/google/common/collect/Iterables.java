package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
public final class Iterables
{
  public static Iterable unmodifiableIterable(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    if (((paramIterable instanceof UnmodifiableIterable)) || ((paramIterable instanceof ImmutableCollection))) {
      return paramIterable;
    }
    return new UnmodifiableIterable(paramIterable, null);
  }
  
  @Deprecated
  public static Iterable unmodifiableIterable(ImmutableCollection paramImmutableCollection)
  {
    return (Iterable)Preconditions.checkNotNull(paramImmutableCollection);
  }
  
  public static int size(Iterable paramIterable)
  {
    return (paramIterable instanceof Collection) ? ((Collection)paramIterable).size() : Iterators.size(paramIterable.iterator());
  }
  
  public static boolean contains(Iterable paramIterable, Object paramObject)
  {
    if ((paramIterable instanceof Collection))
    {
      Collection localCollection = (Collection)paramIterable;
      return Collections2.safeContains(localCollection, paramObject);
    }
    return Iterators.contains(paramIterable.iterator(), paramObject);
  }
  
  public static boolean removeAll(Iterable paramIterable, Collection paramCollection)
  {
    return (paramIterable instanceof Collection) ? ((Collection)paramIterable).removeAll((Collection)Preconditions.checkNotNull(paramCollection)) : Iterators.removeAll(paramIterable.iterator(), paramCollection);
  }
  
  public static boolean retainAll(Iterable paramIterable, Collection paramCollection)
  {
    return (paramIterable instanceof Collection) ? ((Collection)paramIterable).retainAll((Collection)Preconditions.checkNotNull(paramCollection)) : Iterators.retainAll(paramIterable.iterator(), paramCollection);
  }
  
  public static boolean removeIf(Iterable paramIterable, Predicate paramPredicate)
  {
    if (((paramIterable instanceof RandomAccess)) && ((paramIterable instanceof List))) {
      return removeIfFromRandomAccessList((List)paramIterable, (Predicate)Preconditions.checkNotNull(paramPredicate));
    }
    return Iterators.removeIf(paramIterable.iterator(), paramPredicate);
  }
  
  private static boolean removeIfFromRandomAccessList(List paramList, Predicate paramPredicate)
  {
    int i = 0;
    int j = 0;
    while (i < paramList.size())
    {
      Object localObject = paramList.get(i);
      if (!paramPredicate.apply(localObject))
      {
        if (i > j) {
          try
          {
            paramList.set(j, localObject);
          }
          catch (UnsupportedOperationException localUnsupportedOperationException)
          {
            slowRemoveIfForRemainingElements(paramList, paramPredicate, j, i);
            return true;
          }
        }
        j++;
      }
      i++;
    }
    paramList.subList(j, paramList.size()).clear();
    return i != j;
  }
  
  private static void slowRemoveIfForRemainingElements(List paramList, Predicate paramPredicate, int paramInt1, int paramInt2)
  {
    for (int i = paramList.size() - 1; i > paramInt2; i--) {
      if (paramPredicate.apply(paramList.get(i))) {
        paramList.remove(i);
      }
    }
    for (i = paramInt2 - 1; i >= paramInt1; i--) {
      paramList.remove(i);
    }
  }
  
  public static boolean elementsEqual(Iterable paramIterable1, Iterable paramIterable2)
  {
    if (((paramIterable1 instanceof Collection)) && ((paramIterable2 instanceof Collection)))
    {
      Collection localCollection1 = (Collection)paramIterable1;
      Collection localCollection2 = (Collection)paramIterable2;
      if (localCollection1.size() != localCollection2.size()) {
        return false;
      }
    }
    return Iterators.elementsEqual(paramIterable1.iterator(), paramIterable2.iterator());
  }
  
  public static String toString(Iterable paramIterable)
  {
    return Iterators.toString(paramIterable.iterator());
  }
  
  public static Object getOnlyElement(Iterable paramIterable)
  {
    return Iterators.getOnlyElement(paramIterable.iterator());
  }
  
  public static Object getOnlyElement(Iterable paramIterable, Object paramObject)
  {
    return Iterators.getOnlyElement(paramIterable.iterator(), paramObject);
  }
  
  @GwtIncompatible("Array.newInstance(Class, int)")
  public static Object[] toArray(Iterable paramIterable, Class paramClass)
  {
    Collection localCollection = toCollection(paramIterable);
    Object[] arrayOfObject = ObjectArrays.newArray(paramClass, localCollection.size());
    return localCollection.toArray(arrayOfObject);
  }
  
  static Object[] toArray(Iterable paramIterable)
  {
    return toCollection(paramIterable).toArray();
  }
  
  private static Collection toCollection(Iterable paramIterable)
  {
    return (paramIterable instanceof Collection) ? (Collection)paramIterable : Lists.newArrayList(paramIterable.iterator());
  }
  
  public static boolean addAll(Collection paramCollection, Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection))
    {
      Collection localCollection = Collections2.cast(paramIterable);
      return paramCollection.addAll(localCollection);
    }
    return Iterators.addAll(paramCollection, paramIterable.iterator());
  }
  
  public static int frequency(Iterable paramIterable, Object paramObject)
  {
    if ((paramIterable instanceof Multiset)) {
      return ((Multiset)paramIterable).count(paramObject);
    }
    if ((paramIterable instanceof Set)) {
      return ((Set)paramIterable).contains(paramObject) ? 1 : 0;
    }
    return Iterators.frequency(paramIterable.iterator(), paramObject);
  }
  
  public static Iterable cycle(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.cycle(this.val$iterable);
      }
      
      public String toString()
      {
        return this.val$iterable.toString() + " (cycled)";
      }
    };
  }
  
  public static Iterable cycle(Object... paramVarArgs)
  {
    return cycle(Lists.newArrayList(paramVarArgs));
  }
  
  public static Iterable concat(Iterable paramIterable1, Iterable paramIterable2)
  {
    Preconditions.checkNotNull(paramIterable1);
    Preconditions.checkNotNull(paramIterable2);
    return concat(Arrays.asList(new Iterable[] { paramIterable1, paramIterable2 }));
  }
  
  public static Iterable concat(Iterable paramIterable1, Iterable paramIterable2, Iterable paramIterable3)
  {
    Preconditions.checkNotNull(paramIterable1);
    Preconditions.checkNotNull(paramIterable2);
    Preconditions.checkNotNull(paramIterable3);
    return concat(Arrays.asList(new Iterable[] { paramIterable1, paramIterable2, paramIterable3 }));
  }
  
  public static Iterable concat(Iterable paramIterable1, Iterable paramIterable2, Iterable paramIterable3, Iterable paramIterable4)
  {
    Preconditions.checkNotNull(paramIterable1);
    Preconditions.checkNotNull(paramIterable2);
    Preconditions.checkNotNull(paramIterable3);
    Preconditions.checkNotNull(paramIterable4);
    return concat(Arrays.asList(new Iterable[] { paramIterable1, paramIterable2, paramIterable3, paramIterable4 }));
  }
  
  public static Iterable concat(Iterable... paramVarArgs)
  {
    return concat(ImmutableList.copyOf(paramVarArgs));
  }
  
  public static Iterable concat(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.concat(Iterables.iterators(this.val$inputs));
      }
    };
  }
  
  private static UnmodifiableIterator iterators(Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    new UnmodifiableIterator()
    {
      public boolean hasNext()
      {
        return this.val$iterableIterator.hasNext();
      }
      
      public Iterator next()
      {
        return ((Iterable)this.val$iterableIterator.next()).iterator();
      }
    };
  }
  
  public static Iterable partition(Iterable paramIterable, final int paramInt)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkArgument(paramInt > 0);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.partition(this.val$iterable.iterator(), paramInt);
      }
    };
  }
  
  public static Iterable paddedPartition(Iterable paramIterable, final int paramInt)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkArgument(paramInt > 0);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.paddedPartition(this.val$iterable.iterator(), paramInt);
      }
    };
  }
  
  public static Iterable filter(Iterable paramIterable, final Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkNotNull(paramPredicate);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.filter(this.val$unfiltered.iterator(), paramPredicate);
      }
    };
  }
  
  @GwtIncompatible("Class.isInstance")
  public static Iterable filter(Iterable paramIterable, final Class paramClass)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkNotNull(paramClass);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.filter(this.val$unfiltered.iterator(), paramClass);
      }
    };
  }
  
  public static boolean any(Iterable paramIterable, Predicate paramPredicate)
  {
    return Iterators.any(paramIterable.iterator(), paramPredicate);
  }
  
  public static boolean all(Iterable paramIterable, Predicate paramPredicate)
  {
    return Iterators.all(paramIterable.iterator(), paramPredicate);
  }
  
  public static Object find(Iterable paramIterable, Predicate paramPredicate)
  {
    return Iterators.find(paramIterable.iterator(), paramPredicate);
  }
  
  public static Object find(Iterable paramIterable, Predicate paramPredicate, Object paramObject)
  {
    return Iterators.find(paramIterable.iterator(), paramPredicate, paramObject);
  }
  
  public static Optional tryFind(Iterable paramIterable, Predicate paramPredicate)
  {
    return Iterators.tryFind(paramIterable.iterator(), paramPredicate);
  }
  
  public static int indexOf(Iterable paramIterable, Predicate paramPredicate)
  {
    return Iterators.indexOf(paramIterable.iterator(), paramPredicate);
  }
  
  public static Iterable transform(Iterable paramIterable, final Function paramFunction)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkNotNull(paramFunction);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.transform(this.val$fromIterable.iterator(), paramFunction);
      }
    };
  }
  
  public static Object get(Iterable paramIterable, int paramInt)
  {
    Preconditions.checkNotNull(paramIterable);
    if ((paramIterable instanceof List)) {
      return ((List)paramIterable).get(paramInt);
    }
    if ((paramIterable instanceof Collection))
    {
      Collection localCollection = (Collection)paramIterable;
      Preconditions.checkElementIndex(paramInt, localCollection.size());
    }
    else
    {
      checkNonnegativeIndex(paramInt);
    }
    return Iterators.get(paramIterable.iterator(), paramInt);
  }
  
  private static void checkNonnegativeIndex(int paramInt)
  {
    if (paramInt < 0) {
      throw new IndexOutOfBoundsException("position cannot be negative: " + paramInt);
    }
  }
  
  public static Object get(Iterable paramIterable, int paramInt, Object paramObject)
  {
    Preconditions.checkNotNull(paramIterable);
    checkNonnegativeIndex(paramInt);
    try
    {
      return get(paramIterable, paramInt);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
    return paramObject;
  }
  
  public static Object getFirst(Iterable paramIterable, Object paramObject)
  {
    return Iterators.getNext(paramIterable.iterator(), paramObject);
  }
  
  public static Object getLast(Iterable paramIterable)
  {
    Object localObject;
    if ((paramIterable instanceof List))
    {
      localObject = (List)paramIterable;
      if (((List)localObject).isEmpty()) {
        throw new NoSuchElementException();
      }
      return getLastInNonemptyList((List)localObject);
    }
    if ((paramIterable instanceof SortedSet))
    {
      localObject = (SortedSet)paramIterable;
      return ((SortedSet)localObject).last();
    }
    return Iterators.getLast(paramIterable.iterator());
  }
  
  public static Object getLast(Iterable paramIterable, Object paramObject)
  {
    Object localObject;
    if ((paramIterable instanceof Collection))
    {
      localObject = Collections2.cast(paramIterable);
      if (((Collection)localObject).isEmpty()) {
        return paramObject;
      }
    }
    if ((paramIterable instanceof List))
    {
      localObject = Lists.cast(paramIterable);
      return getLastInNonemptyList((List)localObject);
    }
    if ((paramIterable instanceof SortedSet))
    {
      localObject = Sets.cast(paramIterable);
      return ((SortedSet)localObject).last();
    }
    return Iterators.getLast(paramIterable.iterator(), paramObject);
  }
  
  private static Object getLastInNonemptyList(List paramList)
  {
    return paramList.get(paramList.size() - 1);
  }
  
  public static Iterable skip(Iterable paramIterable, final int paramInt)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkArgument(paramInt >= 0, "number to skip cannot be negative");
    if ((paramIterable instanceof List))
    {
      final List localList = (List)paramIterable;
      new FluentIterable()
      {
        public Iterator iterator()
        {
          return this.val$numberToSkip >= localList.size() ? Iterators.emptyIterator() : localList.subList(this.val$numberToSkip, localList.size()).iterator();
        }
      };
    }
    new FluentIterable()
    {
      public Iterator iterator()
      {
        final Iterator localIterator = this.val$iterable.iterator();
        Iterators.advance(localIterator, paramInt);
        new Iterator()
        {
          boolean atStart = true;
          
          public boolean hasNext()
          {
            return localIterator.hasNext();
          }
          
          public Object next()
          {
            if (!hasNext()) {
              throw new NoSuchElementException();
            }
            try
            {
              Object localObject1 = localIterator.next();
              return localObject1;
            }
            finally
            {
              this.atStart = false;
            }
          }
          
          public void remove()
          {
            if (this.atStart) {
              throw new IllegalStateException();
            }
            localIterator.remove();
          }
        };
      }
    };
  }
  
  public static Iterable limit(Iterable paramIterable, final int paramInt)
  {
    Preconditions.checkNotNull(paramIterable);
    Preconditions.checkArgument(paramInt >= 0, "limit is negative");
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.limit(this.val$iterable.iterator(), paramInt);
      }
    };
  }
  
  public static Iterable consumingIterable(Iterable paramIterable)
  {
    if ((paramIterable instanceof Queue)) {
      new FluentIterable()
      {
        public Iterator iterator()
        {
          return new Iterables.ConsumingQueueIterator((Queue)this.val$iterable, null);
        }
      };
    }
    Preconditions.checkNotNull(paramIterable);
    new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.consumingIterator(this.val$iterable.iterator());
      }
    };
  }
  
  public static boolean isEmpty(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return ((Collection)paramIterable).isEmpty();
    }
    return !paramIterable.iterator().hasNext();
  }
  
  @Beta
  public static Iterable mergeSorted(Iterable paramIterable, final Comparator paramComparator)
  {
    Preconditions.checkNotNull(paramIterable, "iterables");
    Preconditions.checkNotNull(paramComparator, "comparator");
    FluentIterable local14 = new FluentIterable()
    {
      public Iterator iterator()
      {
        return Iterators.mergeSorted(Iterables.transform(this.val$iterables, Iterables.access$300()), paramComparator);
      }
    };
    return new UnmodifiableIterable(local14, null);
  }
  
  private static Function toIterator()
  {
    new Function()
    {
      public Iterator apply(Iterable paramAnonymousIterable)
      {
        return paramAnonymousIterable.iterator();
      }
    };
  }
  
  private static class ConsumingQueueIterator
    extends AbstractIterator
  {
    private final Queue queue;
    
    private ConsumingQueueIterator(Queue paramQueue)
    {
      this.queue = paramQueue;
    }
    
    public Object computeNext()
    {
      try
      {
        return this.queue.remove();
      }
      catch (NoSuchElementException localNoSuchElementException) {}
      return endOfData();
    }
  }
  
  private static final class UnmodifiableIterable
    extends FluentIterable
  {
    private final Iterable iterable;
    
    private UnmodifiableIterable(Iterable paramIterable)
    {
      this.iterable = paramIterable;
    }
    
    public Iterator iterator()
    {
      return Iterators.unmodifiableIterator(this.iterable.iterator());
    }
    
    public String toString()
    {
      return this.iterable.toString();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Iterables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */