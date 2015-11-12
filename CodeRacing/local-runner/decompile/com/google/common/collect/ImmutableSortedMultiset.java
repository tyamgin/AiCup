package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

@Beta
@GwtIncompatible("hasn't been tested yet")
public abstract class ImmutableSortedMultiset
  extends ImmutableSortedMultisetFauxverideShim
  implements SortedMultiset
{
  private static final Comparator NATURAL_ORDER = ;
  private static final ImmutableSortedMultiset NATURAL_EMPTY_MULTISET = new EmptyImmutableSortedMultiset(NATURAL_ORDER);
  transient ImmutableSortedMultiset descendingMultiset;
  
  public static ImmutableSortedMultiset of()
  {
    return NATURAL_EMPTY_MULTISET;
  }
  
  public static ImmutableSortedMultiset of(Comparable paramComparable)
  {
    RegularImmutableSortedSet localRegularImmutableSortedSet = (RegularImmutableSortedSet)ImmutableSortedSet.of(paramComparable);
    int[] arrayOfInt = { 1 };
    long[] arrayOfLong = { 0L, 1L };
    return new RegularImmutableSortedMultiset(localRegularImmutableSortedSet, arrayOfInt, arrayOfLong, 0, 1);
  }
  
  public static ImmutableSortedMultiset of(Comparable paramComparable1, Comparable paramComparable2)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2 }));
  }
  
  public static ImmutableSortedMultiset of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2, paramComparable3 }));
  }
  
  public static ImmutableSortedMultiset of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable paramComparable4)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2, paramComparable3, paramComparable4 }));
  }
  
  public static ImmutableSortedMultiset of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable paramComparable4, Comparable paramComparable5)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2, paramComparable3, paramComparable4, paramComparable5 }));
  }
  
  public static ImmutableSortedMultiset of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable paramComparable4, Comparable paramComparable5, Comparable paramComparable6, Comparable... paramVarArgs)
  {
    int i = paramVarArgs.length + 6;
    ArrayList localArrayList = Lists.newArrayListWithCapacity(i);
    Collections.addAll(localArrayList, new Comparable[] { paramComparable1, paramComparable2, paramComparable3, paramComparable4, paramComparable5, paramComparable6 });
    Collections.addAll(localArrayList, paramVarArgs);
    return copyOf(Ordering.natural(), localArrayList);
  }
  
  public static ImmutableSortedMultiset copyOf(Comparable[] paramArrayOfComparable)
  {
    return copyOf(Ordering.natural(), Arrays.asList(paramArrayOfComparable));
  }
  
  public static ImmutableSortedMultiset copyOf(Iterable paramIterable)
  {
    Ordering localOrdering = Ordering.natural();
    return copyOf(localOrdering, paramIterable);
  }
  
  public static ImmutableSortedMultiset copyOf(Iterator paramIterator)
  {
    Ordering localOrdering = Ordering.natural();
    return copyOf(localOrdering, paramIterator);
  }
  
  public static ImmutableSortedMultiset copyOf(Comparator paramComparator, Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramComparator);
    return new Builder(paramComparator).addAll(paramIterator).build();
  }
  
  public static ImmutableSortedMultiset copyOf(Comparator paramComparator, Iterable paramIterable)
  {
    if ((paramIterable instanceof ImmutableSortedMultiset))
    {
      localObject = (ImmutableSortedMultiset)paramIterable;
      if (paramComparator.equals(((ImmutableSortedMultiset)localObject).comparator()))
      {
        if (((ImmutableSortedMultiset)localObject).isPartialView()) {
          return copyOfSortedEntries(paramComparator, ((ImmutableSortedMultiset)localObject).entrySet().asList());
        }
        return (ImmutableSortedMultiset)localObject;
      }
    }
    paramIterable = Lists.newArrayList(paramIterable);
    Object localObject = TreeMultiset.create((Comparator)Preconditions.checkNotNull(paramComparator));
    Iterables.addAll((Collection)localObject, paramIterable);
    return copyOfSortedEntries(paramComparator, ((TreeMultiset)localObject).entrySet());
  }
  
  public static ImmutableSortedMultiset copyOfSorted(SortedMultiset paramSortedMultiset)
  {
    return copyOfSortedEntries(paramSortedMultiset.comparator(), Lists.newArrayList(paramSortedMultiset.entrySet()));
  }
  
  private static ImmutableSortedMultiset copyOfSortedEntries(Comparator paramComparator, Collection paramCollection)
  {
    if (paramCollection.isEmpty()) {
      return emptyMultiset(paramComparator);
    }
    ImmutableList.Builder localBuilder = new ImmutableList.Builder(paramCollection.size());
    int[] arrayOfInt = new int[paramCollection.size()];
    long[] arrayOfLong = new long[paramCollection.size() + 1];
    int i = 0;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      localBuilder.add(localEntry.getElement());
      arrayOfInt[i] = localEntry.getCount();
      arrayOfLong[(i + 1)] = (arrayOfLong[i] + arrayOfInt[i]);
      i++;
    }
    return new RegularImmutableSortedMultiset(new RegularImmutableSortedSet(localBuilder.build(), paramComparator), arrayOfInt, arrayOfLong, 0, paramCollection.size());
  }
  
  static ImmutableSortedMultiset emptyMultiset(Comparator paramComparator)
  {
    if (NATURAL_ORDER.equals(paramComparator)) {
      return NATURAL_EMPTY_MULTISET;
    }
    return new EmptyImmutableSortedMultiset(paramComparator);
  }
  
  public final Comparator comparator()
  {
    return elementSet().comparator();
  }
  
  public abstract ImmutableSortedSet elementSet();
  
  public ImmutableSortedMultiset descendingMultiset()
  {
    ImmutableSortedMultiset localImmutableSortedMultiset = this.descendingMultiset;
    if (localImmutableSortedMultiset == null) {
      return this.descendingMultiset = new DescendingImmutableSortedMultiset(this);
    }
    return localImmutableSortedMultiset;
  }
  
  @Deprecated
  public final Multiset.Entry pollFirstEntry()
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final Multiset.Entry pollLastEntry()
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract ImmutableSortedMultiset headMultiset(Object paramObject, BoundType paramBoundType);
  
  public ImmutableSortedMultiset subMultiset(Object paramObject1, BoundType paramBoundType1, Object paramObject2, BoundType paramBoundType2)
  {
    Preconditions.checkArgument(comparator().compare(paramObject1, paramObject2) <= 0, "Expected lowerBound <= upperBound but %s > %s", new Object[] { paramObject1, paramObject2 });
    return tailMultiset(paramObject1, paramBoundType1).headMultiset(paramObject2, paramBoundType2);
  }
  
  public abstract ImmutableSortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType);
  
  public static Builder orderedBy(Comparator paramComparator)
  {
    return new Builder(paramComparator);
  }
  
  public static Builder reverseOrder()
  {
    return new Builder(Ordering.natural().reverse());
  }
  
  public static Builder naturalOrder()
  {
    return new Builder(Ordering.natural());
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this);
  }
  
  private static final class SerializedForm
    implements Serializable
  {
    Comparator comparator;
    Object[] elements;
    int[] counts;
    
    SerializedForm(SortedMultiset paramSortedMultiset)
    {
      this.comparator = paramSortedMultiset.comparator();
      int i = paramSortedMultiset.entrySet().size();
      this.elements = new Object[i];
      this.counts = new int[i];
      int j = 0;
      Iterator localIterator = paramSortedMultiset.entrySet().iterator();
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
      int i = this.elements.length;
      ImmutableSortedMultiset.Builder localBuilder = ImmutableSortedMultiset.orderedBy(this.comparator);
      for (int j = 0; j < i; j++) {
        localBuilder.addCopies(this.elements[j], this.counts[j]);
      }
      return localBuilder.build();
    }
  }
  
  public static class Builder
    extends ImmutableMultiset.Builder
  {
    private final Comparator comparator;
    
    public Builder(Comparator paramComparator)
    {
      super();
      this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
    }
    
    public Builder add(Object paramObject)
    {
      super.add(paramObject);
      return this;
    }
    
    public Builder addCopies(Object paramObject, int paramInt)
    {
      super.addCopies(paramObject, paramInt);
      return this;
    }
    
    public Builder setCount(Object paramObject, int paramInt)
    {
      super.setCount(paramObject, paramInt);
      return this;
    }
    
    public Builder add(Object... paramVarArgs)
    {
      super.add(paramVarArgs);
      return this;
    }
    
    public Builder addAll(Iterable paramIterable)
    {
      super.addAll(paramIterable);
      return this;
    }
    
    public Builder addAll(Iterator paramIterator)
    {
      super.addAll(paramIterator);
      return this;
    }
    
    public ImmutableSortedMultiset build()
    {
      return ImmutableSortedMultiset.copyOfSorted((SortedMultiset)this.contents);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */