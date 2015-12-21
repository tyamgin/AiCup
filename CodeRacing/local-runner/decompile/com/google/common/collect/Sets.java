package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

@GwtCompatible(emulated=true)
public final class Sets
{
  @GwtCompatible(serializable=true)
  public static ImmutableSet immutableEnumSet(Enum paramEnum, Enum... paramVarArgs)
  {
    return ImmutableEnumSet.asImmutable(EnumSet.of(paramEnum, paramVarArgs));
  }
  
  @GwtCompatible(serializable=true)
  public static ImmutableSet immutableEnumSet(Iterable paramIterable)
  {
    if ((paramIterable instanceof ImmutableEnumSet)) {
      return (ImmutableEnumSet)paramIterable;
    }
    if ((paramIterable instanceof Collection))
    {
      localObject = (Collection)paramIterable;
      if (((Collection)localObject).isEmpty()) {
        return ImmutableSet.of();
      }
      return ImmutableEnumSet.asImmutable(EnumSet.copyOf((Collection)localObject));
    }
    Object localObject = paramIterable.iterator();
    if (((Iterator)localObject).hasNext())
    {
      EnumSet localEnumSet = EnumSet.of((Enum)((Iterator)localObject).next());
      Iterators.addAll(localEnumSet, (Iterator)localObject);
      return ImmutableEnumSet.asImmutable(localEnumSet);
    }
    return ImmutableSet.of();
  }
  
  public static EnumSet newEnumSet(Iterable paramIterable, Class paramClass)
  {
    EnumSet localEnumSet = EnumSet.noneOf(paramClass);
    Iterables.addAll(localEnumSet, paramIterable);
    return localEnumSet;
  }
  
  public static HashSet newHashSet()
  {
    return new HashSet();
  }
  
  public static HashSet newHashSet(Object... paramVarArgs)
  {
    HashSet localHashSet = newHashSetWithExpectedSize(paramVarArgs.length);
    Collections.addAll(localHashSet, paramVarArgs);
    return localHashSet;
  }
  
  public static HashSet newHashSetWithExpectedSize(int paramInt)
  {
    return new HashSet(Maps.capacity(paramInt));
  }
  
  public static HashSet newHashSet(Iterable paramIterable)
  {
    return (paramIterable instanceof Collection) ? new HashSet(Collections2.cast(paramIterable)) : newHashSet(paramIterable.iterator());
  }
  
  public static HashSet newHashSet(Iterator paramIterator)
  {
    HashSet localHashSet = newHashSet();
    while (paramIterator.hasNext()) {
      localHashSet.add(paramIterator.next());
    }
    return localHashSet;
  }
  
  public static LinkedHashSet newLinkedHashSet()
  {
    return new LinkedHashSet();
  }
  
  public static LinkedHashSet newLinkedHashSetWithExpectedSize(int paramInt)
  {
    return new LinkedHashSet(Maps.capacity(paramInt));
  }
  
  public static LinkedHashSet newLinkedHashSet(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new LinkedHashSet(Collections2.cast(paramIterable));
    }
    LinkedHashSet localLinkedHashSet = newLinkedHashSet();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localLinkedHashSet.add(localObject);
    }
    return localLinkedHashSet;
  }
  
  public static TreeSet newTreeSet()
  {
    return new TreeSet();
  }
  
  public static TreeSet newTreeSet(Iterable paramIterable)
  {
    TreeSet localTreeSet = newTreeSet();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Comparable localComparable = (Comparable)localIterator.next();
      localTreeSet.add(localComparable);
    }
    return localTreeSet;
  }
  
  public static TreeSet newTreeSet(Comparator paramComparator)
  {
    return new TreeSet((Comparator)Preconditions.checkNotNull(paramComparator));
  }
  
  public static Set newIdentityHashSet()
  {
    return newSetFromMap(Maps.newIdentityHashMap());
  }
  
  @GwtIncompatible("CopyOnWriteArraySet")
  public static CopyOnWriteArraySet newCopyOnWriteArraySet()
  {
    return new CopyOnWriteArraySet();
  }
  
  @GwtIncompatible("CopyOnWriteArraySet")
  public static CopyOnWriteArraySet newCopyOnWriteArraySet(Iterable paramIterable)
  {
    ArrayList localArrayList = (paramIterable instanceof Collection) ? Collections2.cast(paramIterable) : Lists.newArrayList(paramIterable);
    return new CopyOnWriteArraySet(localArrayList);
  }
  
  public static EnumSet complementOf(Collection paramCollection)
  {
    if ((paramCollection instanceof EnumSet)) {
      return EnumSet.complementOf((EnumSet)paramCollection);
    }
    Preconditions.checkArgument(!paramCollection.isEmpty(), "collection is empty; use the other version of this method");
    Class localClass = ((Enum)paramCollection.iterator().next()).getDeclaringClass();
    return makeComplementByHand(paramCollection, localClass);
  }
  
  public static EnumSet complementOf(Collection paramCollection, Class paramClass)
  {
    Preconditions.checkNotNull(paramCollection);
    return (paramCollection instanceof EnumSet) ? EnumSet.complementOf((EnumSet)paramCollection) : makeComplementByHand(paramCollection, paramClass);
  }
  
  private static EnumSet makeComplementByHand(Collection paramCollection, Class paramClass)
  {
    EnumSet localEnumSet = EnumSet.allOf(paramClass);
    localEnumSet.removeAll(paramCollection);
    return localEnumSet;
  }
  
  public static Set newSetFromMap(Map paramMap)
  {
    return new SetFromMap(paramMap);
  }
  
  public static SetView union(Set paramSet1, final Set paramSet2)
  {
    Preconditions.checkNotNull(paramSet1, "set1");
    Preconditions.checkNotNull(paramSet2, "set2");
    final SetView localSetView = difference(paramSet2, paramSet1);
    new SetView(paramSet1)
    {
      public int size()
      {
        return this.val$set1.size() + localSetView.size();
      }
      
      public boolean isEmpty()
      {
        return (this.val$set1.isEmpty()) && (paramSet2.isEmpty());
      }
      
      public Iterator iterator()
      {
        return Iterators.unmodifiableIterator(Iterators.concat(this.val$set1.iterator(), localSetView.iterator()));
      }
      
      public boolean contains(Object paramAnonymousObject)
      {
        return (this.val$set1.contains(paramAnonymousObject)) || (paramSet2.contains(paramAnonymousObject));
      }
      
      public Set copyInto(Set paramAnonymousSet)
      {
        paramAnonymousSet.addAll(this.val$set1);
        paramAnonymousSet.addAll(paramSet2);
        return paramAnonymousSet;
      }
      
      public ImmutableSet immutableCopy()
      {
        return new ImmutableSet.Builder().addAll(this.val$set1).addAll(paramSet2).build();
      }
    };
  }
  
  public static SetView intersection(Set paramSet1, final Set paramSet2)
  {
    Preconditions.checkNotNull(paramSet1, "set1");
    Preconditions.checkNotNull(paramSet2, "set2");
    final Predicate localPredicate = Predicates.in(paramSet2);
    new SetView(paramSet1)
    {
      public Iterator iterator()
      {
        return Iterators.filter(this.val$set1.iterator(), localPredicate);
      }
      
      public int size()
      {
        return Iterators.size(iterator());
      }
      
      public boolean isEmpty()
      {
        return !iterator().hasNext();
      }
      
      public boolean contains(Object paramAnonymousObject)
      {
        return (this.val$set1.contains(paramAnonymousObject)) && (paramSet2.contains(paramAnonymousObject));
      }
      
      public boolean containsAll(Collection paramAnonymousCollection)
      {
        return (this.val$set1.containsAll(paramAnonymousCollection)) && (paramSet2.containsAll(paramAnonymousCollection));
      }
    };
  }
  
  public static SetView difference(Set paramSet1, final Set paramSet2)
  {
    Preconditions.checkNotNull(paramSet1, "set1");
    Preconditions.checkNotNull(paramSet2, "set2");
    final Predicate localPredicate = Predicates.not(Predicates.in(paramSet2));
    new SetView(paramSet1)
    {
      public Iterator iterator()
      {
        return Iterators.filter(this.val$set1.iterator(), localPredicate);
      }
      
      public int size()
      {
        return Iterators.size(iterator());
      }
      
      public boolean isEmpty()
      {
        return paramSet2.containsAll(this.val$set1);
      }
      
      public boolean contains(Object paramAnonymousObject)
      {
        return (this.val$set1.contains(paramAnonymousObject)) && (!paramSet2.contains(paramAnonymousObject));
      }
    };
  }
  
  public static SetView symmetricDifference(Set paramSet1, Set paramSet2)
  {
    Preconditions.checkNotNull(paramSet1, "set1");
    Preconditions.checkNotNull(paramSet2, "set2");
    return difference(union(paramSet1, paramSet2), intersection(paramSet1, paramSet2));
  }
  
  public static Set filter(Set paramSet, Predicate paramPredicate)
  {
    if ((paramSet instanceof SortedSet)) {
      return filter((SortedSet)paramSet, paramPredicate);
    }
    if ((paramSet instanceof FilteredSet))
    {
      FilteredSet localFilteredSet = (FilteredSet)paramSet;
      Predicate localPredicate = Predicates.and(localFilteredSet.predicate, paramPredicate);
      return new FilteredSet((Set)localFilteredSet.unfiltered, localPredicate);
    }
    return new FilteredSet((Set)Preconditions.checkNotNull(paramSet), (Predicate)Preconditions.checkNotNull(paramPredicate));
  }
  
  public static SortedSet filter(SortedSet paramSortedSet, Predicate paramPredicate)
  {
    return Platform.setsFilterSortedSet(paramSortedSet, paramPredicate);
  }
  
  static SortedSet filterSortedIgnoreNavigable(SortedSet paramSortedSet, Predicate paramPredicate)
  {
    if ((paramSortedSet instanceof FilteredSet))
    {
      FilteredSet localFilteredSet = (FilteredSet)paramSortedSet;
      Predicate localPredicate = Predicates.and(localFilteredSet.predicate, paramPredicate);
      return new FilteredSortedSet((SortedSet)localFilteredSet.unfiltered, localPredicate);
    }
    return new FilteredSortedSet((SortedSet)Preconditions.checkNotNull(paramSortedSet), (Predicate)Preconditions.checkNotNull(paramPredicate));
  }
  
  @GwtIncompatible("NavigableSet")
  public static NavigableSet filter(NavigableSet paramNavigableSet, Predicate paramPredicate)
  {
    if ((paramNavigableSet instanceof FilteredSet))
    {
      FilteredSet localFilteredSet = (FilteredSet)paramNavigableSet;
      Predicate localPredicate = Predicates.and(localFilteredSet.predicate, paramPredicate);
      return new FilteredNavigableSet((NavigableSet)localFilteredSet.unfiltered, localPredicate);
    }
    return new FilteredNavigableSet((NavigableSet)Preconditions.checkNotNull(paramNavigableSet), (Predicate)Preconditions.checkNotNull(paramPredicate));
  }
  
  public static Set cartesianProduct(List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Set localSet = (Set)localIterator.next();
      if (localSet.isEmpty()) {
        return ImmutableSet.of();
      }
    }
    return CartesianSet.create(paramList);
  }
  
  public static Set cartesianProduct(Set... paramVarArgs)
  {
    return cartesianProduct(Arrays.asList(paramVarArgs));
  }
  
  @GwtCompatible(serializable=false)
  public static Set powerSet(Set paramSet)
  {
    ImmutableSet localImmutableSet = ImmutableSet.copyOf(paramSet);
    Preconditions.checkArgument(localImmutableSet.size() <= 30, "Too many elements to create power set: %s > 30", new Object[] { Integer.valueOf(localImmutableSet.size()) });
    return new PowerSet(localImmutableSet);
  }
  
  static int hashCodeImpl(Set paramSet)
  {
    int i = 0;
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      i += (localObject != null ? localObject.hashCode() : 0);
      i = i ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    }
    return i;
  }
  
  static boolean equalsImpl(Set paramSet, Object paramObject)
  {
    if (paramSet == paramObject) {
      return true;
    }
    if ((paramObject instanceof Set))
    {
      Set localSet = (Set)paramObject;
      try
      {
        return (paramSet.size() == localSet.size()) && (paramSet.containsAll(localSet));
      }
      catch (NullPointerException localNullPointerException)
      {
        return false;
      }
      catch (ClassCastException localClassCastException)
      {
        return false;
      }
    }
    return false;
  }
  
  @GwtIncompatible("NavigableSet")
  public static NavigableSet unmodifiableNavigableSet(NavigableSet paramNavigableSet)
  {
    if (((paramNavigableSet instanceof ImmutableSortedSet)) || ((paramNavigableSet instanceof UnmodifiableNavigableSet))) {
      return paramNavigableSet;
    }
    return new UnmodifiableNavigableSet(paramNavigableSet);
  }
  
  @GwtIncompatible("NavigableSet")
  public static NavigableSet synchronizedNavigableSet(NavigableSet paramNavigableSet)
  {
    return Synchronized.navigableSet(paramNavigableSet);
  }
  
  static boolean removeAllImpl(Set paramSet, Iterator paramIterator)
  {
    boolean bool = false;
    while (paramIterator.hasNext()) {
      bool |= paramSet.remove(paramIterator.next());
    }
    return bool;
  }
  
  static boolean removeAllImpl(Set paramSet, Collection paramCollection)
  {
    Preconditions.checkNotNull(paramCollection);
    if ((paramCollection instanceof Multiset)) {
      paramCollection = ((Multiset)paramCollection).elementSet();
    }
    if (((paramCollection instanceof Set)) && (paramCollection.size() > paramSet.size()))
    {
      Iterator localIterator = paramSet.iterator();
      boolean bool = false;
      while (localIterator.hasNext()) {
        if (paramCollection.contains(localIterator.next()))
        {
          bool = true;
          localIterator.remove();
        }
      }
      return bool;
    }
    return removeAllImpl(paramSet, paramCollection.iterator());
  }
  
  static SortedSet cast(Iterable paramIterable)
  {
    return (SortedSet)paramIterable;
  }
  
  @GwtIncompatible("NavigableSet")
  static class DescendingSet
    extends ForwardingNavigableSet
  {
    private final NavigableSet forward;
    
    DescendingSet(NavigableSet paramNavigableSet)
    {
      this.forward = paramNavigableSet;
    }
    
    protected NavigableSet delegate()
    {
      return this.forward;
    }
    
    public Object lower(Object paramObject)
    {
      return this.forward.higher(paramObject);
    }
    
    public Object floor(Object paramObject)
    {
      return this.forward.ceiling(paramObject);
    }
    
    public Object ceiling(Object paramObject)
    {
      return this.forward.floor(paramObject);
    }
    
    public Object higher(Object paramObject)
    {
      return this.forward.lower(paramObject);
    }
    
    public Object pollFirst()
    {
      return this.forward.pollLast();
    }
    
    public Object pollLast()
    {
      return this.forward.pollFirst();
    }
    
    public NavigableSet descendingSet()
    {
      return this.forward;
    }
    
    public Iterator descendingIterator()
    {
      return this.forward.iterator();
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return this.forward.subSet(paramObject2, paramBoolean2, paramObject1, paramBoolean1).descendingSet();
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return this.forward.tailSet(paramObject, paramBoolean).descendingSet();
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return this.forward.headSet(paramObject, paramBoolean).descendingSet();
    }
    
    public Comparator comparator()
    {
      Comparator localComparator = this.forward.comparator();
      if (localComparator == null) {
        return Ordering.natural().reverse();
      }
      return reverse(localComparator);
    }
    
    private static Ordering reverse(Comparator paramComparator)
    {
      return Ordering.from(paramComparator).reverse();
    }
    
    public Object first()
    {
      return this.forward.last();
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return standardHeadSet(paramObject);
    }
    
    public Object last()
    {
      return this.forward.first();
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return standardSubSet(paramObject1, paramObject2);
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return standardTailSet(paramObject);
    }
    
    public Iterator iterator()
    {
      return this.forward.descendingIterator();
    }
    
    public Object[] toArray()
    {
      return standardToArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return standardToArray(paramArrayOfObject);
    }
    
    public String toString()
    {
      return standardToString();
    }
  }
  
  @GwtIncompatible("NavigableSet")
  static final class UnmodifiableNavigableSet
    extends ForwardingSortedSet
    implements Serializable, NavigableSet
  {
    private final NavigableSet delegate;
    private transient UnmodifiableNavigableSet descendingSet;
    private static final long serialVersionUID = 0L;
    
    UnmodifiableNavigableSet(NavigableSet paramNavigableSet)
    {
      this.delegate = ((NavigableSet)Preconditions.checkNotNull(paramNavigableSet));
    }
    
    protected SortedSet delegate()
    {
      return Collections.unmodifiableSortedSet(this.delegate);
    }
    
    public Object lower(Object paramObject)
    {
      return this.delegate.lower(paramObject);
    }
    
    public Object floor(Object paramObject)
    {
      return this.delegate.floor(paramObject);
    }
    
    public Object ceiling(Object paramObject)
    {
      return this.delegate.ceiling(paramObject);
    }
    
    public Object higher(Object paramObject)
    {
      return this.delegate.higher(paramObject);
    }
    
    public Object pollFirst()
    {
      throw new UnsupportedOperationException();
    }
    
    public Object pollLast()
    {
      throw new UnsupportedOperationException();
    }
    
    public NavigableSet descendingSet()
    {
      UnmodifiableNavigableSet localUnmodifiableNavigableSet = this.descendingSet;
      if (localUnmodifiableNavigableSet == null)
      {
        localUnmodifiableNavigableSet = this.descendingSet = new UnmodifiableNavigableSet(this.delegate.descendingSet());
        localUnmodifiableNavigableSet.descendingSet = this;
      }
      return localUnmodifiableNavigableSet;
    }
    
    public Iterator descendingIterator()
    {
      return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return Sets.unmodifiableNavigableSet(this.delegate.subSet(paramObject1, paramBoolean1, paramObject2, paramBoolean2));
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return Sets.unmodifiableNavigableSet(this.delegate.headSet(paramObject, paramBoolean));
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return Sets.unmodifiableNavigableSet(this.delegate.tailSet(paramObject, paramBoolean));
    }
  }
  
  private static final class PowerSet
    extends AbstractSet
  {
    final ImmutableSet inputSet;
    final ImmutableList inputList;
    final int powerSetSize;
    
    PowerSet(ImmutableSet paramImmutableSet)
    {
      this.inputSet = paramImmutableSet;
      this.inputList = paramImmutableSet.asList();
      this.powerSetSize = (1 << paramImmutableSet.size());
    }
    
    public int size()
    {
      return this.powerSetSize;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public Iterator iterator()
    {
      new AbstractIndexedListIterator(this.powerSetSize)
      {
        protected Set get(final int paramAnonymousInt)
        {
          new AbstractSet()
          {
            public int size()
            {
              return Integer.bitCount(paramAnonymousInt);
            }
            
            public Iterator iterator()
            {
              return new Sets.PowerSet.BitFilteredSetIterator(Sets.PowerSet.this.inputList, paramAnonymousInt);
            }
          };
        }
      };
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Set))
      {
        Set localSet = (Set)paramObject;
        return this.inputSet.containsAll(localSet);
      }
      return false;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof PowerSet))
      {
        PowerSet localPowerSet = (PowerSet)paramObject;
        return this.inputSet.equals(localPowerSet.inputSet);
      }
      return super.equals(paramObject);
    }
    
    public int hashCode()
    {
      return this.inputSet.hashCode() << this.inputSet.size() - 1;
    }
    
    public String toString()
    {
      return "powerSet(" + this.inputSet + ")";
    }
    
    private static final class BitFilteredSetIterator
      extends UnmodifiableIterator
    {
      final ImmutableList input;
      int remainingSetBits;
      
      BitFilteredSetIterator(ImmutableList paramImmutableList, int paramInt)
      {
        this.input = paramImmutableList;
        this.remainingSetBits = paramInt;
      }
      
      public boolean hasNext()
      {
        return this.remainingSetBits != 0;
      }
      
      public Object next()
      {
        int i = Integer.numberOfTrailingZeros(this.remainingSetBits);
        if (i == 32) {
          throw new NoSuchElementException();
        }
        int j = 1 << i;
        this.remainingSetBits &= (j ^ 0xFFFFFFFF);
        return this.input.get(i);
      }
    }
  }
  
  private static final class CartesianSet
    extends ForwardingCollection
    implements Set
  {
    private final transient ImmutableList axes;
    private final transient CartesianList delegate;
    
    static Set create(List paramList)
    {
      ImmutableList.Builder localBuilder = new ImmutableList.Builder(paramList.size());
      Object localObject1 = paramList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Set)((Iterator)localObject1).next();
        ImmutableSet localImmutableSet = ImmutableSet.copyOf((Collection)localObject2);
        if (localImmutableSet.isEmpty()) {
          return ImmutableSet.of();
        }
        localBuilder.add(localImmutableSet);
      }
      localObject1 = localBuilder.build();
      Object localObject2 = new ImmutableList()
      {
        public int size()
        {
          return this.val$axes.size();
        }
        
        public List get(int paramAnonymousInt)
        {
          return ((ImmutableSet)this.val$axes.get(paramAnonymousInt)).asList();
        }
        
        boolean isPartialView()
        {
          return true;
        }
      };
      return new CartesianSet((ImmutableList)localObject1, new CartesianList((ImmutableList)localObject2));
    }
    
    private CartesianSet(ImmutableList paramImmutableList, CartesianList paramCartesianList)
    {
      this.axes = paramImmutableList;
      this.delegate = paramCartesianList;
    }
    
    protected Collection delegate()
    {
      return this.delegate;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof CartesianSet))
      {
        CartesianSet localCartesianSet = (CartesianSet)paramObject;
        return this.axes.equals(localCartesianSet.axes);
      }
      return super.equals(paramObject);
    }
    
    public int hashCode()
    {
      int i = size() - 1;
      for (int j = 0; j < this.axes.size(); j++)
      {
        i *= 31;
        i = i ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
      }
      j = 1;
      Iterator localIterator = this.axes.iterator();
      while (localIterator.hasNext())
      {
        Set localSet = (Set)localIterator.next();
        j = 31 * j + size() / localSet.size() * localSet.hashCode();
        j = j ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
      }
      j += i;
      return j ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    }
  }
  
  @GwtIncompatible("NavigableSet")
  private static class FilteredNavigableSet
    extends Sets.FilteredSortedSet
    implements NavigableSet
  {
    FilteredNavigableSet(NavigableSet paramNavigableSet, Predicate paramPredicate)
    {
      super(paramPredicate);
    }
    
    NavigableSet unfiltered()
    {
      return (NavigableSet)this.unfiltered;
    }
    
    public Object lower(Object paramObject)
    {
      return Iterators.getNext(headSet(paramObject, false).descendingIterator(), null);
    }
    
    public Object floor(Object paramObject)
    {
      return Iterators.getNext(headSet(paramObject, true).descendingIterator(), null);
    }
    
    public Object ceiling(Object paramObject)
    {
      return Iterables.getFirst(tailSet(paramObject, true), null);
    }
    
    public Object higher(Object paramObject)
    {
      return Iterables.getFirst(tailSet(paramObject, false), null);
    }
    
    public Object pollFirst()
    {
      Iterator localIterator = unfiltered().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (this.predicate.apply(localObject))
        {
          localIterator.remove();
          return localObject;
        }
      }
      return null;
    }
    
    public Object pollLast()
    {
      Iterator localIterator = unfiltered().descendingIterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (this.predicate.apply(localObject))
        {
          localIterator.remove();
          return localObject;
        }
      }
      return null;
    }
    
    public NavigableSet descendingSet()
    {
      return Sets.filter(unfiltered().descendingSet(), this.predicate);
    }
    
    public Iterator descendingIterator()
    {
      return Iterators.filter(unfiltered().descendingIterator(), this.predicate);
    }
    
    public Object last()
    {
      return descendingIterator().next();
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return Sets.filter(unfiltered().subSet(paramObject1, paramBoolean1, paramObject2, paramBoolean2), this.predicate);
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return Sets.filter(unfiltered().headSet(paramObject, paramBoolean), this.predicate);
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return Sets.filter(unfiltered().tailSet(paramObject, paramBoolean), this.predicate);
    }
  }
  
  private static class FilteredSortedSet
    extends Sets.FilteredSet
    implements SortedSet
  {
    FilteredSortedSet(SortedSet paramSortedSet, Predicate paramPredicate)
    {
      super(paramPredicate);
    }
    
    public Comparator comparator()
    {
      return ((SortedSet)this.unfiltered).comparator();
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return new FilteredSortedSet(((SortedSet)this.unfiltered).subSet(paramObject1, paramObject2), this.predicate);
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return new FilteredSortedSet(((SortedSet)this.unfiltered).headSet(paramObject), this.predicate);
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return new FilteredSortedSet(((SortedSet)this.unfiltered).tailSet(paramObject), this.predicate);
    }
    
    public Object first()
    {
      return iterator().next();
    }
    
    public Object last()
    {
      Object localObject;
      for (SortedSet localSortedSet = (SortedSet)this.unfiltered;; localSortedSet = localSortedSet.headSet(localObject))
      {
        localObject = localSortedSet.last();
        if (this.predicate.apply(localObject)) {
          return localObject;
        }
      }
    }
  }
  
  private static class FilteredSet
    extends Collections2.FilteredCollection
    implements Set
  {
    FilteredSet(Set paramSet, Predicate paramPredicate)
    {
      super(paramPredicate);
    }
    
    public boolean equals(Object paramObject)
    {
      return Sets.equalsImpl(this, paramObject);
    }
    
    public int hashCode()
    {
      return Sets.hashCodeImpl(this);
    }
  }
  
  public static abstract class SetView
    extends AbstractSet
  {
    public ImmutableSet immutableCopy()
    {
      return ImmutableSet.copyOf(this);
    }
    
    public Set copyInto(Set paramSet)
    {
      paramSet.addAll(this);
      return paramSet;
    }
  }
  
  private static class SetFromMap
    extends AbstractSet
    implements Serializable, Set
  {
    private final Map m;
    private transient Set s;
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;
    
    SetFromMap(Map paramMap)
    {
      Preconditions.checkArgument(paramMap.isEmpty(), "Map is non-empty");
      this.m = paramMap;
      this.s = paramMap.keySet();
    }
    
    public void clear()
    {
      this.m.clear();
    }
    
    public int size()
    {
      return this.m.size();
    }
    
    public boolean isEmpty()
    {
      return this.m.isEmpty();
    }
    
    public boolean contains(Object paramObject)
    {
      return this.m.containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      return this.m.remove(paramObject) != null;
    }
    
    public boolean add(Object paramObject)
    {
      return this.m.put(paramObject, Boolean.TRUE) == null;
    }
    
    public Iterator iterator()
    {
      return this.s.iterator();
    }
    
    public Object[] toArray()
    {
      return this.s.toArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return this.s.toArray(paramArrayOfObject);
    }
    
    public String toString()
    {
      return this.s.toString();
    }
    
    public int hashCode()
    {
      return this.s.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return (this == paramObject) || (this.s.equals(paramObject));
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return this.s.containsAll(paramCollection);
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return this.s.removeAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return this.s.retainAll(paramCollection);
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      this.s = this.m.keySet();
    }
  }
  
  static abstract class ImprovedAbstractSet
    extends AbstractSet
  {
    public boolean removeAll(Collection paramCollection)
    {
      return Sets.removeAllImpl(this, paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return super.retainAll((Collection)Preconditions.checkNotNull(paramCollection));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Sets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */