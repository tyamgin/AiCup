package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
public abstract class FluentIterable
  implements Iterable
{
  private final Iterable iterable;
  
  protected FluentIterable()
  {
    this.iterable = this;
  }
  
  FluentIterable(Iterable paramIterable)
  {
    this.iterable = ((Iterable)Preconditions.checkNotNull(paramIterable));
  }
  
  public static FluentIterable from(final Iterable paramIterable)
  {
    (paramIterable instanceof FluentIterable) ? (FluentIterable)paramIterable : new FluentIterable(paramIterable)
    {
      public Iterator iterator()
      {
        return paramIterable.iterator();
      }
    };
  }
  
  @Deprecated
  public static FluentIterable from(FluentIterable paramFluentIterable)
  {
    return (FluentIterable)Preconditions.checkNotNull(paramFluentIterable);
  }
  
  public String toString()
  {
    return Iterables.toString(this.iterable);
  }
  
  public final int size()
  {
    return Iterables.size(this.iterable);
  }
  
  public final boolean contains(Object paramObject)
  {
    return Iterables.contains(this.iterable, paramObject);
  }
  
  public final FluentIterable cycle()
  {
    return from(Iterables.cycle(this.iterable));
  }
  
  public final FluentIterable filter(Predicate paramPredicate)
  {
    return from(Iterables.filter(this.iterable, paramPredicate));
  }
  
  @GwtIncompatible("Class.isInstance")
  public final FluentIterable filter(Class paramClass)
  {
    return from(Iterables.filter(this.iterable, paramClass));
  }
  
  public final boolean anyMatch(Predicate paramPredicate)
  {
    return Iterables.any(this.iterable, paramPredicate);
  }
  
  public final boolean allMatch(Predicate paramPredicate)
  {
    return Iterables.all(this.iterable, paramPredicate);
  }
  
  public final Optional firstMatch(Predicate paramPredicate)
  {
    return Iterables.tryFind(this.iterable, paramPredicate);
  }
  
  public final FluentIterable transform(Function paramFunction)
  {
    return from(Iterables.transform(this.iterable, paramFunction));
  }
  
  public FluentIterable transformAndConcat(Function paramFunction)
  {
    return from(Iterables.concat(transform(paramFunction)));
  }
  
  public final Optional first()
  {
    Iterator localIterator = this.iterable.iterator();
    return localIterator.hasNext() ? Optional.of(localIterator.next()) : Optional.absent();
  }
  
  public final Optional last()
  {
    if ((this.iterable instanceof List))
    {
      localObject1 = (List)this.iterable;
      if (((List)localObject1).isEmpty()) {
        return Optional.absent();
      }
      return Optional.of(((List)localObject1).get(((List)localObject1).size() - 1));
    }
    Object localObject1 = this.iterable.iterator();
    if (!((Iterator)localObject1).hasNext()) {
      return Optional.absent();
    }
    Object localObject2;
    if ((this.iterable instanceof SortedSet))
    {
      localObject2 = (SortedSet)this.iterable;
      return Optional.of(((SortedSet)localObject2).last());
    }
    for (;;)
    {
      localObject2 = ((Iterator)localObject1).next();
      if (!((Iterator)localObject1).hasNext()) {
        return Optional.of(localObject2);
      }
    }
  }
  
  public final FluentIterable skip(int paramInt)
  {
    return from(Iterables.skip(this.iterable, paramInt));
  }
  
  public final FluentIterable limit(int paramInt)
  {
    return from(Iterables.limit(this.iterable, paramInt));
  }
  
  public final boolean isEmpty()
  {
    return !this.iterable.iterator().hasNext();
  }
  
  public final ImmutableList toList()
  {
    return ImmutableList.copyOf(this.iterable);
  }
  
  @Beta
  public final ImmutableList toSortedList(Comparator paramComparator)
  {
    return Ordering.from(paramComparator).immutableSortedCopy(this.iterable);
  }
  
  public final ImmutableSet toSet()
  {
    return ImmutableSet.copyOf(this.iterable);
  }
  
  public final ImmutableSortedSet toSortedSet(Comparator paramComparator)
  {
    return ImmutableSortedSet.copyOf(paramComparator, this.iterable);
  }
  
  public final ImmutableMap toMap(Function paramFunction)
  {
    return Maps.toMap(this.iterable, paramFunction);
  }
  
  public final ImmutableListMultimap index(Function paramFunction)
  {
    return Multimaps.index(this.iterable, paramFunction);
  }
  
  public final ImmutableMap uniqueIndex(Function paramFunction)
  {
    return Maps.uniqueIndex(this.iterable, paramFunction);
  }
  
  @Deprecated
  public final ImmutableList toImmutableList()
  {
    return toList();
  }
  
  @Deprecated
  public final ImmutableList toSortedImmutableList(Comparator paramComparator)
  {
    return toSortedList(paramComparator);
  }
  
  @Deprecated
  public final ImmutableSet toImmutableSet()
  {
    return toSet();
  }
  
  @Deprecated
  public final ImmutableSortedSet toImmutableSortedSet(Comparator paramComparator)
  {
    return toSortedSet(paramComparator);
  }
  
  @GwtIncompatible("Array.newArray(Class, int)")
  public final Object[] toArray(Class paramClass)
  {
    return Iterables.toArray(this.iterable, paramClass);
  }
  
  public final Collection copyInto(Collection paramCollection)
  {
    Preconditions.checkNotNull(paramCollection);
    if ((this.iterable instanceof Collection))
    {
      paramCollection.addAll(Collections2.cast(this.iterable));
    }
    else
    {
      Iterator localIterator = this.iterable.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        paramCollection.add(localObject);
      }
    }
    return paramCollection;
  }
  
  public final Object get(int paramInt)
  {
    return Iterables.get(this.iterable, paramInt);
  }
  
  private static class FromIterableFunction
    implements Function
  {
    public FluentIterable apply(Iterable paramIterable)
    {
      return FluentIterable.from(paramIterable);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\FluentIterable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */