package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@GwtCompatible
public final class Collections2
{
  static final Joiner STANDARD_JOINER = Joiner.on(", ").useForNull("null");
  
  public static Collection filter(Collection paramCollection, Predicate paramPredicate)
  {
    if ((paramCollection instanceof FilteredCollection)) {
      return ((FilteredCollection)paramCollection).createCombined(paramPredicate);
    }
    return new FilteredCollection((Collection)Preconditions.checkNotNull(paramCollection), (Predicate)Preconditions.checkNotNull(paramPredicate));
  }
  
  static boolean safeContains(Collection paramCollection, Object paramObject)
  {
    Preconditions.checkNotNull(paramCollection);
    try
    {
      return paramCollection.contains(paramObject);
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  static boolean safeRemove(Collection paramCollection, Object paramObject)
  {
    Preconditions.checkNotNull(paramCollection);
    try
    {
      return paramCollection.remove(paramObject);
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  public static Collection transform(Collection paramCollection, Function paramFunction)
  {
    return new TransformedCollection(paramCollection, paramFunction);
  }
  
  static boolean containsAllImpl(Collection paramCollection1, Collection paramCollection2)
  {
    Preconditions.checkNotNull(paramCollection1);
    Iterator localIterator = paramCollection2.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (!paramCollection1.contains(localObject)) {
        return false;
      }
    }
    return true;
  }
  
  static String toStringImpl(Collection paramCollection)
  {
    StringBuilder localStringBuilder = newStringBuilderForCollection(paramCollection.size()).append('[');
    STANDARD_JOINER.appendTo(localStringBuilder, Iterables.transform(paramCollection, new Function()
    {
      public Object apply(Object paramAnonymousObject)
      {
        return paramAnonymousObject == this.val$collection ? "(this Collection)" : paramAnonymousObject;
      }
    }));
    return ']';
  }
  
  static StringBuilder newStringBuilderForCollection(int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0, "size must be non-negative");
    return new StringBuilder((int)Math.min(paramInt * 8L, 1073741824L));
  }
  
  static Collection cast(Iterable paramIterable)
  {
    return (Collection)paramIterable;
  }
  
  @Beta
  public static Collection orderedPermutations(Iterable paramIterable)
  {
    return orderedPermutations(paramIterable, Ordering.natural());
  }
  
  @Beta
  public static Collection orderedPermutations(Iterable paramIterable, Comparator paramComparator)
  {
    return new OrderedPermutationCollection(paramIterable, paramComparator);
  }
  
  @Beta
  public static Collection permutations(Collection paramCollection)
  {
    return new PermutationCollection(ImmutableList.copyOf(paramCollection));
  }
  
  private static boolean isPermutation(List paramList1, List paramList2)
  {
    if (paramList1.size() != paramList2.size()) {
      return false;
    }
    HashMultiset localHashMultiset1 = HashMultiset.create(paramList1);
    HashMultiset localHashMultiset2 = HashMultiset.create(paramList2);
    return localHashMultiset1.equals(localHashMultiset2);
  }
  
  private static boolean isPositiveInt(long paramLong)
  {
    return (paramLong >= 0L) && (paramLong <= 2147483647L);
  }
  
  private static class PermutationIterator
    extends AbstractIterator
  {
    final List list;
    final int[] c;
    final int[] o;
    int j;
    
    PermutationIterator(List paramList)
    {
      this.list = new ArrayList(paramList);
      int i = paramList.size();
      this.c = new int[i];
      this.o = new int[i];
      for (int k = 0; k < i; k++)
      {
        this.c[k] = 0;
        this.o[k] = 1;
      }
      this.j = Integer.MAX_VALUE;
    }
    
    protected List computeNext()
    {
      if (this.j <= 0) {
        return (List)endOfData();
      }
      ImmutableList localImmutableList = ImmutableList.copyOf(this.list);
      calculateNextPermutation();
      return localImmutableList;
    }
    
    void calculateNextPermutation()
    {
      this.j = (this.list.size() - 1);
      int i = 0;
      if (this.j == -1) {
        return;
      }
      int k;
      for (;;)
      {
        k = this.c[this.j] + this.o[this.j];
        if (k < 0)
        {
          switchDirection();
        }
        else
        {
          if (k != this.j + 1) {
            break;
          }
          if (this.j == 0) {
            return;
          }
          i++;
          switchDirection();
        }
      }
      Collections.swap(this.list, this.j - this.c[this.j] + i, this.j - k + i);
      this.c[this.j] = k;
    }
    
    void switchDirection()
    {
      this.o[this.j] = (-this.o[this.j]);
      this.j -= 1;
    }
  }
  
  private static final class PermutationCollection
    extends AbstractCollection
  {
    final ImmutableList inputList;
    
    PermutationCollection(ImmutableList paramImmutableList)
    {
      this.inputList = paramImmutableList;
    }
    
    public int size()
    {
      return IntMath.factorial(this.inputList.size());
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public Iterator iterator()
    {
      return new Collections2.PermutationIterator(this.inputList);
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof List))
      {
        List localList = (List)paramObject;
        return Collections2.isPermutation(this.inputList, localList);
      }
      return false;
    }
    
    public String toString()
    {
      return "permutations(" + this.inputList + ")";
    }
  }
  
  private static final class OrderedPermutationIterator
    extends AbstractIterator
  {
    List nextPermutation;
    final Comparator comparator;
    
    OrderedPermutationIterator(List paramList, Comparator paramComparator)
    {
      this.nextPermutation = Lists.newArrayList(paramList);
      this.comparator = paramComparator;
    }
    
    protected List computeNext()
    {
      if (this.nextPermutation == null) {
        return (List)endOfData();
      }
      ImmutableList localImmutableList = ImmutableList.copyOf(this.nextPermutation);
      calculateNextPermutation();
      return localImmutableList;
    }
    
    void calculateNextPermutation()
    {
      int i = findNextJ();
      if (i == -1)
      {
        this.nextPermutation = null;
        return;
      }
      int j = findNextL(i);
      Collections.swap(this.nextPermutation, i, j);
      int k = this.nextPermutation.size();
      Collections.reverse(this.nextPermutation.subList(i + 1, k));
    }
    
    int findNextJ()
    {
      for (int i = this.nextPermutation.size() - 2; i >= 0; i--) {
        if (this.comparator.compare(this.nextPermutation.get(i), this.nextPermutation.get(i + 1)) < 0) {
          return i;
        }
      }
      return -1;
    }
    
    int findNextL(int paramInt)
    {
      Object localObject = this.nextPermutation.get(paramInt);
      for (int i = this.nextPermutation.size() - 1; i > paramInt; i--) {
        if (this.comparator.compare(localObject, this.nextPermutation.get(i)) < 0) {
          return i;
        }
      }
      throw new AssertionError("this statement should be unreachable");
    }
  }
  
  private static final class OrderedPermutationCollection
    extends AbstractCollection
  {
    final ImmutableList inputList;
    final Comparator comparator;
    final int size;
    
    OrderedPermutationCollection(Iterable paramIterable, Comparator paramComparator)
    {
      this.inputList = Ordering.from(paramComparator).immutableSortedCopy(paramIterable);
      this.comparator = paramComparator;
      this.size = calculateSize(this.inputList, paramComparator);
    }
    
    private static int calculateSize(List paramList, Comparator paramComparator)
    {
      long l = 1L;
      int i = 1;
      for (int j = 1; i < paramList.size(); j++)
      {
        int k = paramComparator.compare(paramList.get(i - 1), paramList.get(i));
        if (k < 0)
        {
          l *= LongMath.binomial(i, j);
          j = 0;
          if (!Collections2.isPositiveInt(l)) {
            return Integer.MAX_VALUE;
          }
        }
        i++;
      }
      l *= LongMath.binomial(i, j);
      if (!Collections2.isPositiveInt(l)) {
        return Integer.MAX_VALUE;
      }
      return (int)l;
    }
    
    public int size()
    {
      return this.size;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public Iterator iterator()
    {
      return new Collections2.OrderedPermutationIterator(this.inputList, this.comparator);
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof List))
      {
        List localList = (List)paramObject;
        return Collections2.isPermutation(this.inputList, localList);
      }
      return false;
    }
    
    public String toString()
    {
      return "orderedPermutationCollection(" + this.inputList + ")";
    }
  }
  
  static class TransformedCollection
    extends AbstractCollection
  {
    final Collection fromCollection;
    final Function function;
    
    TransformedCollection(Collection paramCollection, Function paramFunction)
    {
      this.fromCollection = ((Collection)Preconditions.checkNotNull(paramCollection));
      this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public void clear()
    {
      this.fromCollection.clear();
    }
    
    public boolean isEmpty()
    {
      return this.fromCollection.isEmpty();
    }
    
    public Iterator iterator()
    {
      return Iterators.transform(this.fromCollection.iterator(), this.function);
    }
    
    public int size()
    {
      return this.fromCollection.size();
    }
  }
  
  static class FilteredCollection
    implements Collection
  {
    final Collection unfiltered;
    final Predicate predicate;
    
    FilteredCollection(Collection paramCollection, Predicate paramPredicate)
    {
      this.unfiltered = paramCollection;
      this.predicate = paramPredicate;
    }
    
    FilteredCollection createCombined(Predicate paramPredicate)
    {
      return new FilteredCollection(this.unfiltered, Predicates.and(this.predicate, paramPredicate));
    }
    
    public boolean add(Object paramObject)
    {
      Preconditions.checkArgument(this.predicate.apply(paramObject));
      return this.unfiltered.add(paramObject);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        Preconditions.checkArgument(this.predicate.apply(localObject));
      }
      return this.unfiltered.addAll(paramCollection);
    }
    
    public void clear()
    {
      Iterables.removeIf(this.unfiltered, this.predicate);
    }
    
    public boolean contains(Object paramObject)
    {
      try
      {
        Object localObject = paramObject;
        return (this.predicate.apply(localObject)) && (this.unfiltered.contains(paramObject));
      }
      catch (NullPointerException localNullPointerException)
      {
        return false;
      }
      catch (ClassCastException localClassCastException) {}
      return false;
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (!contains(localObject)) {
          return false;
        }
      }
      return true;
    }
    
    public boolean isEmpty()
    {
      return !Iterators.any(this.unfiltered.iterator(), this.predicate);
    }
    
    public Iterator iterator()
    {
      return Iterators.filter(this.unfiltered.iterator(), this.predicate);
    }
    
    public boolean remove(Object paramObject)
    {
      try
      {
        Object localObject = paramObject;
        return (this.predicate.apply(localObject)) && (this.unfiltered.remove(paramObject));
      }
      catch (NullPointerException localNullPointerException)
      {
        return false;
      }
      catch (ClassCastException localClassCastException) {}
      return false;
    }
    
    public boolean removeAll(final Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      Predicate local1 = new Predicate()
      {
        public boolean apply(Object paramAnonymousObject)
        {
          return (Collections2.FilteredCollection.this.predicate.apply(paramAnonymousObject)) && (paramCollection.contains(paramAnonymousObject));
        }
      };
      return Iterables.removeIf(this.unfiltered, local1);
    }
    
    public boolean retainAll(final Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      Predicate local2 = new Predicate()
      {
        public boolean apply(Object paramAnonymousObject)
        {
          return (Collections2.FilteredCollection.this.predicate.apply(paramAnonymousObject)) && (!paramCollection.contains(paramAnonymousObject));
        }
      };
      return Iterables.removeIf(this.unfiltered, local2);
    }
    
    public int size()
    {
      return Iterators.size(iterator());
    }
    
    public Object[] toArray()
    {
      return Lists.newArrayList(iterator()).toArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return Lists.newArrayList(iterator()).toArray(paramArrayOfObject);
    }
    
    public String toString()
    {
      return Iterators.toString(iterator());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Collections2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */