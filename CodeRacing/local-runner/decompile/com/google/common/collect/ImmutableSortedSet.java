package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableSortedSet
  extends ImmutableSortedSetFauxverideShim
  implements SortedIterable, NavigableSet
{
  private static final Comparator NATURAL_ORDER = ;
  private static final ImmutableSortedSet NATURAL_EMPTY_SET = new EmptyImmutableSortedSet(NATURAL_ORDER);
  final transient Comparator comparator;
  @GwtIncompatible("NavigableSet")
  transient ImmutableSortedSet descendingSet;
  
  private static ImmutableSortedSet emptySet()
  {
    return NATURAL_EMPTY_SET;
  }
  
  static ImmutableSortedSet emptySet(Comparator paramComparator)
  {
    if (NATURAL_ORDER.equals(paramComparator)) {
      return emptySet();
    }
    return new EmptyImmutableSortedSet(paramComparator);
  }
  
  public static ImmutableSortedSet of()
  {
    return emptySet();
  }
  
  public static ImmutableSortedSet of(Comparable paramComparable)
  {
    return new RegularImmutableSortedSet(ImmutableList.of(paramComparable), Ordering.natural());
  }
  
  public static ImmutableSortedSet of(Comparable paramComparable1, Comparable paramComparable2)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2 }));
  }
  
  public static ImmutableSortedSet of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2, paramComparable3 }));
  }
  
  public static ImmutableSortedSet of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable paramComparable4)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2, paramComparable3, paramComparable4 }));
  }
  
  public static ImmutableSortedSet of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable paramComparable4, Comparable paramComparable5)
  {
    return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { paramComparable1, paramComparable2, paramComparable3, paramComparable4, paramComparable5 }));
  }
  
  public static ImmutableSortedSet of(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable paramComparable4, Comparable paramComparable5, Comparable paramComparable6, Comparable... paramVarArgs)
  {
    int i = paramVarArgs.length + 6;
    ArrayList localArrayList = new ArrayList(i);
    Collections.addAll(localArrayList, new Comparable[] { paramComparable1, paramComparable2, paramComparable3, paramComparable4, paramComparable5, paramComparable6 });
    Collections.addAll(localArrayList, paramVarArgs);
    return copyOf(Ordering.natural(), localArrayList);
  }
  
  public static ImmutableSortedSet copyOf(Comparable[] paramArrayOfComparable)
  {
    return copyOf(Ordering.natural(), Arrays.asList(paramArrayOfComparable));
  }
  
  public static ImmutableSortedSet copyOf(Iterable paramIterable)
  {
    Ordering localOrdering = Ordering.natural();
    return copyOf(localOrdering, paramIterable);
  }
  
  public static ImmutableSortedSet copyOf(Collection paramCollection)
  {
    Ordering localOrdering = Ordering.natural();
    return copyOf(localOrdering, paramCollection);
  }
  
  public static ImmutableSortedSet copyOf(Iterator paramIterator)
  {
    Ordering localOrdering = Ordering.natural();
    return copyOf(localOrdering, paramIterator);
  }
  
  public static ImmutableSortedSet copyOf(Comparator paramComparator, Iterator paramIterator)
  {
    return copyOf(paramComparator, Lists.newArrayList(paramIterator));
  }
  
  public static ImmutableSortedSet copyOf(Comparator paramComparator, Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramComparator);
    boolean bool = SortedIterables.hasSameComparator(paramComparator, paramIterable);
    if ((bool) && ((paramIterable instanceof ImmutableSortedSet)))
    {
      localObject = (ImmutableSortedSet)paramIterable;
      if (!((ImmutableSortedSet)localObject).isPartialView()) {
        return (ImmutableSortedSet)localObject;
      }
    }
    Object localObject = (Object[])Iterables.toArray(paramIterable);
    return construct(paramComparator, localObject.length, (Object[])localObject);
  }
  
  public static ImmutableSortedSet copyOf(Comparator paramComparator, Collection paramCollection)
  {
    return copyOf(paramComparator, paramCollection);
  }
  
  public static ImmutableSortedSet copyOfSorted(SortedSet paramSortedSet)
  {
    Comparator localComparator = SortedIterables.comparator(paramSortedSet);
    Object[] arrayOfObject = paramSortedSet.toArray();
    if (arrayOfObject.length == 0) {
      return emptySet(localComparator);
    }
    return new RegularImmutableSortedSet(ImmutableList.asImmutableList(arrayOfObject), localComparator);
  }
  
  static int sortAndUnique(Comparator paramComparator, int paramInt, Object... paramVarArgs)
  {
    if (paramInt == 0) {
      return 0;
    }
    for (int i = 0; i < paramInt; i++) {
      ObjectArrays.checkElementNotNull(paramVarArgs[i], i);
    }
    Arrays.sort(paramVarArgs, 0, paramInt, paramComparator);
    i = 1;
    for (int j = 1; j < paramInt; j++)
    {
      Object localObject1 = paramVarArgs[j];
      Object localObject2 = paramVarArgs[(i - 1)];
      if (paramComparator.compare(localObject1, localObject2) != 0) {
        paramVarArgs[(i++)] = localObject1;
      }
    }
    Arrays.fill(paramVarArgs, i, paramInt, null);
    return i;
  }
  
  static ImmutableSortedSet construct(Comparator paramComparator, int paramInt, Object... paramVarArgs)
  {
    int i = sortAndUnique(paramComparator, paramInt, paramVarArgs);
    if (i == 0) {
      return emptySet(paramComparator);
    }
    if (i < paramVarArgs.length) {
      paramVarArgs = ObjectArrays.arraysCopyOf(paramVarArgs, i);
    }
    return new RegularImmutableSortedSet(ImmutableList.asImmutableList(paramVarArgs), paramComparator);
  }
  
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
  
  int unsafeCompare(Object paramObject1, Object paramObject2)
  {
    return unsafeCompare(this.comparator, paramObject1, paramObject2);
  }
  
  static int unsafeCompare(Comparator paramComparator, Object paramObject1, Object paramObject2)
  {
    Comparator localComparator = paramComparator;
    return localComparator.compare(paramObject1, paramObject2);
  }
  
  ImmutableSortedSet(Comparator paramComparator)
  {
    this.comparator = paramComparator;
  }
  
  public Comparator comparator()
  {
    return this.comparator;
  }
  
  public abstract UnmodifiableIterator iterator();
  
  public ImmutableSortedSet headSet(Object paramObject)
  {
    return headSet(paramObject, false);
  }
  
  @GwtIncompatible("NavigableSet")
  public ImmutableSortedSet headSet(Object paramObject, boolean paramBoolean)
  {
    return headSetImpl(Preconditions.checkNotNull(paramObject), paramBoolean);
  }
  
  public ImmutableSortedSet subSet(Object paramObject1, Object paramObject2)
  {
    return subSet(paramObject1, true, paramObject2, false);
  }
  
  @GwtIncompatible("NavigableSet")
  public ImmutableSortedSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    Preconditions.checkArgument(this.comparator.compare(paramObject1, paramObject2) <= 0);
    return subSetImpl(paramObject1, paramBoolean1, paramObject2, paramBoolean2);
  }
  
  public ImmutableSortedSet tailSet(Object paramObject)
  {
    return tailSet(paramObject, true);
  }
  
  @GwtIncompatible("NavigableSet")
  public ImmutableSortedSet tailSet(Object paramObject, boolean paramBoolean)
  {
    return tailSetImpl(Preconditions.checkNotNull(paramObject), paramBoolean);
  }
  
  abstract ImmutableSortedSet headSetImpl(Object paramObject, boolean paramBoolean);
  
  abstract ImmutableSortedSet subSetImpl(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2);
  
  abstract ImmutableSortedSet tailSetImpl(Object paramObject, boolean paramBoolean);
  
  @GwtIncompatible("NavigableSet")
  public Object lower(Object paramObject)
  {
    return Iterators.getNext(headSet(paramObject, false).descendingIterator(), null);
  }
  
  @GwtIncompatible("NavigableSet")
  public Object floor(Object paramObject)
  {
    return Iterators.getNext(headSet(paramObject, true).descendingIterator(), null);
  }
  
  @GwtIncompatible("NavigableSet")
  public Object ceiling(Object paramObject)
  {
    return Iterables.getFirst(tailSet(paramObject, true), null);
  }
  
  @GwtIncompatible("NavigableSet")
  public Object higher(Object paramObject)
  {
    return Iterables.getFirst(tailSet(paramObject, false), null);
  }
  
  public Object first()
  {
    return iterator().next();
  }
  
  public Object last()
  {
    return descendingIterator().next();
  }
  
  @GwtIncompatible("NavigableSet")
  @Deprecated
  public final Object pollFirst()
  {
    throw new UnsupportedOperationException();
  }
  
  @GwtIncompatible("NavigableSet")
  @Deprecated
  public final Object pollLast()
  {
    throw new UnsupportedOperationException();
  }
  
  @GwtIncompatible("NavigableSet")
  public ImmutableSortedSet descendingSet()
  {
    ImmutableSortedSet localImmutableSortedSet = this.descendingSet;
    if (localImmutableSortedSet == null)
    {
      localImmutableSortedSet = this.descendingSet = createDescendingSet();
      localImmutableSortedSet.descendingSet = this;
    }
    return localImmutableSortedSet;
  }
  
  @GwtIncompatible("NavigableSet")
  ImmutableSortedSet createDescendingSet()
  {
    return new DescendingImmutableSortedSet(this);
  }
  
  @GwtIncompatible("NavigableSet")
  public abstract UnmodifiableIterator descendingIterator();
  
  abstract int indexOf(Object paramObject);
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this.comparator, toArray());
  }
  
  private static class SerializedForm
    implements Serializable
  {
    final Comparator comparator;
    final Object[] elements;
    private static final long serialVersionUID = 0L;
    
    public SerializedForm(Comparator paramComparator, Object[] paramArrayOfObject)
    {
      this.comparator = paramComparator;
      this.elements = paramArrayOfObject;
    }
    
    Object readResolve()
    {
      return new ImmutableSortedSet.Builder(this.comparator).add((Object[])this.elements).build();
    }
  }
  
  public static final class Builder
    extends ImmutableSet.Builder
  {
    private final Comparator comparator;
    
    public Builder(Comparator paramComparator)
    {
      this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
    }
    
    public Builder add(Object paramObject)
    {
      super.add(paramObject);
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
    
    public ImmutableSortedSet build()
    {
      Object[] arrayOfObject = (Object[])this.contents;
      ImmutableSortedSet localImmutableSortedSet = ImmutableSortedSet.construct(this.comparator, this.size, arrayOfObject);
      this.size = localImmutableSortedSet.size();
      return localImmutableSortedSet;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */