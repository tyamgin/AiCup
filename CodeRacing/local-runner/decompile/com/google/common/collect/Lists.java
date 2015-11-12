package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;

@GwtCompatible(emulated=true)
public final class Lists
{
  @GwtCompatible(serializable=true)
  public static ArrayList newArrayList()
  {
    return new ArrayList();
  }
  
  @GwtCompatible(serializable=true)
  public static ArrayList newArrayList(Object... paramVarArgs)
  {
    Preconditions.checkNotNull(paramVarArgs);
    int i = computeArrayListCapacity(paramVarArgs.length);
    ArrayList localArrayList = new ArrayList(i);
    Collections.addAll(localArrayList, paramVarArgs);
    return localArrayList;
  }
  
  @VisibleForTesting
  static int computeArrayListCapacity(int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0);
    return Ints.saturatedCast(5L + paramInt + paramInt / 10);
  }
  
  @GwtCompatible(serializable=true)
  public static ArrayList newArrayList(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    return (paramIterable instanceof Collection) ? new ArrayList(Collections2.cast(paramIterable)) : newArrayList(paramIterable.iterator());
  }
  
  @GwtCompatible(serializable=true)
  public static ArrayList newArrayList(Iterator paramIterator)
  {
    Preconditions.checkNotNull(paramIterator);
    ArrayList localArrayList = newArrayList();
    while (paramIterator.hasNext()) {
      localArrayList.add(paramIterator.next());
    }
    return localArrayList;
  }
  
  @GwtCompatible(serializable=true)
  public static ArrayList newArrayListWithCapacity(int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0);
    return new ArrayList(paramInt);
  }
  
  @GwtCompatible(serializable=true)
  public static ArrayList newArrayListWithExpectedSize(int paramInt)
  {
    return new ArrayList(computeArrayListCapacity(paramInt));
  }
  
  @GwtCompatible(serializable=true)
  public static LinkedList newLinkedList()
  {
    return new LinkedList();
  }
  
  @GwtCompatible(serializable=true)
  public static LinkedList newLinkedList(Iterable paramIterable)
  {
    LinkedList localLinkedList = newLinkedList();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localLinkedList.add(localObject);
    }
    return localLinkedList;
  }
  
  @GwtIncompatible("CopyOnWriteArrayList")
  public static CopyOnWriteArrayList newCopyOnWriteArrayList()
  {
    return new CopyOnWriteArrayList();
  }
  
  @GwtIncompatible("CopyOnWriteArrayList")
  public static CopyOnWriteArrayList newCopyOnWriteArrayList(Iterable paramIterable)
  {
    ArrayList localArrayList = (paramIterable instanceof Collection) ? Collections2.cast(paramIterable) : newArrayList(paramIterable);
    return new CopyOnWriteArrayList(localArrayList);
  }
  
  public static List asList(Object paramObject, Object[] paramArrayOfObject)
  {
    return new OnePlusArrayList(paramObject, paramArrayOfObject);
  }
  
  public static List asList(Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    return new TwoPlusArrayList(paramObject1, paramObject2, paramArrayOfObject);
  }
  
  static List cartesianProduct(List paramList)
  {
    return CartesianList.create(paramList);
  }
  
  static List cartesianProduct(List... paramVarArgs)
  {
    return cartesianProduct(Arrays.asList(paramVarArgs));
  }
  
  public static List transform(List paramList, Function paramFunction)
  {
    return (paramList instanceof RandomAccess) ? new TransformingRandomAccessList(paramList, paramFunction) : new TransformingSequentialList(paramList, paramFunction);
  }
  
  public static List partition(List paramList, int paramInt)
  {
    Preconditions.checkNotNull(paramList);
    Preconditions.checkArgument(paramInt > 0);
    return (paramList instanceof RandomAccess) ? new RandomAccessPartition(paramList, paramInt) : new Partition(paramList, paramInt);
  }
  
  @Beta
  public static ImmutableList charactersOf(String paramString)
  {
    return new StringAsImmutableList((String)Preconditions.checkNotNull(paramString));
  }
  
  @Beta
  public static List charactersOf(CharSequence paramCharSequence)
  {
    return new CharSequenceAsList((CharSequence)Preconditions.checkNotNull(paramCharSequence));
  }
  
  public static List reverse(List paramList)
  {
    if ((paramList instanceof ReverseList)) {
      return ((ReverseList)paramList).getForwardList();
    }
    if ((paramList instanceof RandomAccess)) {
      return new RandomAccessReverseList(paramList);
    }
    return new ReverseList(paramList);
  }
  
  static int hashCodeImpl(List paramList)
  {
    int i = 1;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      i = 31 * i + (localObject == null ? 0 : localObject.hashCode());
      i = i ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    }
    return i;
  }
  
  static boolean equalsImpl(List paramList, Object paramObject)
  {
    if (paramObject == Preconditions.checkNotNull(paramList)) {
      return true;
    }
    if (!(paramObject instanceof List)) {
      return false;
    }
    List localList = (List)paramObject;
    return (paramList.size() == localList.size()) && (Iterators.elementsEqual(paramList.iterator(), localList.iterator()));
  }
  
  static boolean addAllImpl(List paramList, int paramInt, Iterable paramIterable)
  {
    boolean bool = false;
    ListIterator localListIterator = paramList.listIterator(paramInt);
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localListIterator.add(localObject);
      bool = true;
    }
    return bool;
  }
  
  static int indexOfImpl(List paramList, Object paramObject)
  {
    ListIterator localListIterator = paramList.listIterator();
    while (localListIterator.hasNext()) {
      if (Objects.equal(paramObject, localListIterator.next())) {
        return localListIterator.previousIndex();
      }
    }
    return -1;
  }
  
  static int lastIndexOfImpl(List paramList, Object paramObject)
  {
    ListIterator localListIterator = paramList.listIterator(paramList.size());
    while (localListIterator.hasPrevious()) {
      if (Objects.equal(paramObject, localListIterator.previous())) {
        return localListIterator.nextIndex();
      }
    }
    return -1;
  }
  
  static ListIterator listIteratorImpl(List paramList, int paramInt)
  {
    return new AbstractListWrapper(paramList).listIterator(paramInt);
  }
  
  static List subListImpl(List paramList, int paramInt1, int paramInt2)
  {
    Object localObject;
    if ((paramList instanceof RandomAccess)) {
      localObject = new RandomAccessListWrapper(paramList)
      {
        private static final long serialVersionUID = 0L;
        
        public ListIterator listIterator(int paramAnonymousInt)
        {
          return this.backingList.listIterator(paramAnonymousInt);
        }
      };
    } else {
      localObject = new AbstractListWrapper(paramList)
      {
        private static final long serialVersionUID = 0L;
        
        public ListIterator listIterator(int paramAnonymousInt)
        {
          return this.backingList.listIterator(paramAnonymousInt);
        }
      };
    }
    return ((List)localObject).subList(paramInt1, paramInt2);
  }
  
  static List cast(Iterable paramIterable)
  {
    return (List)paramIterable;
  }
  
  private static class RandomAccessListWrapper
    extends Lists.AbstractListWrapper
    implements RandomAccess
  {
    RandomAccessListWrapper(List paramList)
    {
      super();
    }
  }
  
  private static class AbstractListWrapper
    extends AbstractList
  {
    final List backingList;
    
    AbstractListWrapper(List paramList)
    {
      this.backingList = ((List)Preconditions.checkNotNull(paramList));
    }
    
    public void add(int paramInt, Object paramObject)
    {
      this.backingList.add(paramInt, paramObject);
    }
    
    public boolean addAll(int paramInt, Collection paramCollection)
    {
      return this.backingList.addAll(paramInt, paramCollection);
    }
    
    public Object get(int paramInt)
    {
      return this.backingList.get(paramInt);
    }
    
    public Object remove(int paramInt)
    {
      return this.backingList.remove(paramInt);
    }
    
    public Object set(int paramInt, Object paramObject)
    {
      return this.backingList.set(paramInt, paramObject);
    }
    
    public boolean contains(Object paramObject)
    {
      return this.backingList.contains(paramObject);
    }
    
    public int size()
    {
      return this.backingList.size();
    }
  }
  
  private static class RandomAccessReverseList
    extends Lists.ReverseList
    implements RandomAccess
  {
    RandomAccessReverseList(List paramList)
    {
      super();
    }
  }
  
  private static class ReverseList
    extends AbstractList
  {
    private final List forwardList;
    
    ReverseList(List paramList)
    {
      this.forwardList = ((List)Preconditions.checkNotNull(paramList));
    }
    
    List getForwardList()
    {
      return this.forwardList;
    }
    
    private int reverseIndex(int paramInt)
    {
      int i = size();
      Preconditions.checkElementIndex(paramInt, i);
      return i - 1 - paramInt;
    }
    
    private int reversePosition(int paramInt)
    {
      int i = size();
      Preconditions.checkPositionIndex(paramInt, i);
      return i - paramInt;
    }
    
    public void add(int paramInt, Object paramObject)
    {
      this.forwardList.add(reversePosition(paramInt), paramObject);
    }
    
    public void clear()
    {
      this.forwardList.clear();
    }
    
    public Object remove(int paramInt)
    {
      return this.forwardList.remove(reverseIndex(paramInt));
    }
    
    protected void removeRange(int paramInt1, int paramInt2)
    {
      subList(paramInt1, paramInt2).clear();
    }
    
    public Object set(int paramInt, Object paramObject)
    {
      return this.forwardList.set(reverseIndex(paramInt), paramObject);
    }
    
    public Object get(int paramInt)
    {
      return this.forwardList.get(reverseIndex(paramInt));
    }
    
    public boolean isEmpty()
    {
      return this.forwardList.isEmpty();
    }
    
    public int size()
    {
      return this.forwardList.size();
    }
    
    public boolean contains(Object paramObject)
    {
      return this.forwardList.contains(paramObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return this.forwardList.containsAll(paramCollection);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, size());
      return Lists.reverse(this.forwardList.subList(reversePosition(paramInt2), reversePosition(paramInt1)));
    }
    
    public int indexOf(Object paramObject)
    {
      int i = this.forwardList.lastIndexOf(paramObject);
      return i >= 0 ? reverseIndex(i) : -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      int i = this.forwardList.indexOf(paramObject);
      return i >= 0 ? reverseIndex(i) : -1;
    }
    
    public Iterator iterator()
    {
      return listIterator();
    }
    
    public ListIterator listIterator(int paramInt)
    {
      int i = reversePosition(paramInt);
      final ListIterator localListIterator = this.forwardList.listIterator(i);
      new ListIterator()
      {
        boolean canRemove;
        boolean canSet;
        
        public void add(Object paramAnonymousObject)
        {
          localListIterator.add(paramAnonymousObject);
          localListIterator.previous();
          this.canSet = (this.canRemove = 0);
        }
        
        public boolean hasNext()
        {
          return localListIterator.hasPrevious();
        }
        
        public boolean hasPrevious()
        {
          return localListIterator.hasNext();
        }
        
        public Object next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          this.canSet = (this.canRemove = 1);
          return localListIterator.previous();
        }
        
        public int nextIndex()
        {
          return Lists.ReverseList.this.reversePosition(localListIterator.nextIndex());
        }
        
        public Object previous()
        {
          if (!hasPrevious()) {
            throw new NoSuchElementException();
          }
          this.canSet = (this.canRemove = 1);
          return localListIterator.next();
        }
        
        public int previousIndex()
        {
          return nextIndex() - 1;
        }
        
        public void remove()
        {
          Preconditions.checkState(this.canRemove);
          localListIterator.remove();
          this.canRemove = (this.canSet = 0);
        }
        
        public void set(Object paramAnonymousObject)
        {
          Preconditions.checkState(this.canSet);
          localListIterator.set(paramAnonymousObject);
        }
      };
    }
  }
  
  private static final class CharSequenceAsList
    extends AbstractList
  {
    private final CharSequence sequence;
    
    CharSequenceAsList(CharSequence paramCharSequence)
    {
      this.sequence = paramCharSequence;
    }
    
    public Character get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Character.valueOf(this.sequence.charAt(paramInt));
    }
    
    public boolean contains(Object paramObject)
    {
      return indexOf(paramObject) >= 0;
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Character))
      {
        int i = ((Character)paramObject).charValue();
        for (int j = 0; j < this.sequence.length(); j++) {
          if (this.sequence.charAt(j) == i) {
            return j;
          }
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Character))
      {
        int i = ((Character)paramObject).charValue();
        for (int j = this.sequence.length() - 1; j >= 0; j--) {
          if (this.sequence.charAt(j) == i) {
            return j;
          }
        }
      }
      return -1;
    }
    
    public int size()
    {
      return this.sequence.length();
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, size());
      return Lists.charactersOf(this.sequence.subSequence(paramInt1, paramInt2));
    }
    
    public int hashCode()
    {
      int i = 1;
      for (int j = 0; j < this.sequence.length(); j++) {
        i = i * 31 + this.sequence.charAt(j);
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof List)) {
        return false;
      }
      List localList = (List)paramObject;
      int i = this.sequence.length();
      if (i != localList.size()) {
        return false;
      }
      Iterator localIterator = localList.iterator();
      for (int j = 0; j < i; j++)
      {
        Object localObject = localIterator.next();
        if ((!(localObject instanceof Character)) || (((Character)localObject).charValue() != this.sequence.charAt(j))) {
          return false;
        }
      }
      return true;
    }
  }
  
  private static final class StringAsImmutableList
    extends ImmutableList
  {
    private final String string;
    int hash = 0;
    
    StringAsImmutableList(String paramString)
    {
      this.string = paramString;
    }
    
    public int indexOf(Object paramObject)
    {
      return (paramObject instanceof Character) ? this.string.indexOf(((Character)paramObject).charValue()) : -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      return (paramObject instanceof Character) ? this.string.lastIndexOf(((Character)paramObject).charValue()) : -1;
    }
    
    public ImmutableList subList(int paramInt1, int paramInt2)
    {
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, size());
      return Lists.charactersOf(this.string.substring(paramInt1, paramInt2));
    }
    
    boolean isPartialView()
    {
      return false;
    }
    
    public Character get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Character.valueOf(this.string.charAt(paramInt));
    }
    
    public int size()
    {
      return this.string.length();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof List)) {
        return false;
      }
      List localList = (List)paramObject;
      int i = this.string.length();
      if (i != localList.size()) {
        return false;
      }
      Iterator localIterator = localList.iterator();
      for (int j = 0; j < i; j++)
      {
        Object localObject = localIterator.next();
        if ((!(localObject instanceof Character)) || (((Character)localObject).charValue() != this.string.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      int i = this.hash;
      if (i == 0)
      {
        i = 1;
        for (int j = 0; j < this.string.length(); j++) {
          i = i * 31 + this.string.charAt(j);
        }
        this.hash = i;
      }
      return i;
    }
  }
  
  private static class RandomAccessPartition
    extends Lists.Partition
    implements RandomAccess
  {
    RandomAccessPartition(List paramList, int paramInt)
    {
      super(paramInt);
    }
  }
  
  private static class Partition
    extends AbstractList
  {
    final List list;
    final int size;
    
    Partition(List paramList, int paramInt)
    {
      this.list = paramList;
      this.size = paramInt;
    }
    
    public List get(int paramInt)
    {
      int i = size();
      Preconditions.checkElementIndex(paramInt, i);
      int j = paramInt * this.size;
      int k = Math.min(j + this.size, this.list.size());
      return this.list.subList(j, k);
    }
    
    public int size()
    {
      int i = this.list.size() / this.size;
      if (i * this.size != this.list.size()) {
        i++;
      }
      return i;
    }
    
    public boolean isEmpty()
    {
      return this.list.isEmpty();
    }
  }
  
  private static class TransformingRandomAccessList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final List fromList;
    final Function function;
    private static final long serialVersionUID = 0L;
    
    TransformingRandomAccessList(List paramList, Function paramFunction)
    {
      this.fromList = ((List)Preconditions.checkNotNull(paramList));
      this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public void clear()
    {
      this.fromList.clear();
    }
    
    public Object get(int paramInt)
    {
      return this.function.apply(this.fromList.get(paramInt));
    }
    
    public boolean isEmpty()
    {
      return this.fromList.isEmpty();
    }
    
    public Object remove(int paramInt)
    {
      return this.function.apply(this.fromList.remove(paramInt));
    }
    
    public int size()
    {
      return this.fromList.size();
    }
  }
  
  private static class TransformingSequentialList
    extends AbstractSequentialList
    implements Serializable
  {
    final List fromList;
    final Function function;
    private static final long serialVersionUID = 0L;
    
    TransformingSequentialList(List paramList, Function paramFunction)
    {
      this.fromList = ((List)Preconditions.checkNotNull(paramList));
      this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public void clear()
    {
      this.fromList.clear();
    }
    
    public int size()
    {
      return this.fromList.size();
    }
    
    public ListIterator listIterator(int paramInt)
    {
      new TransformedListIterator(this.fromList.listIterator(paramInt))
      {
        Object transform(Object paramAnonymousObject)
        {
          return Lists.TransformingSequentialList.this.function.apply(paramAnonymousObject);
        }
      };
    }
  }
  
  private static class TwoPlusArrayList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final Object first;
    final Object second;
    final Object[] rest;
    private static final long serialVersionUID = 0L;
    
    TwoPlusArrayList(Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
    {
      this.first = paramObject1;
      this.second = paramObject2;
      this.rest = ((Object[])Preconditions.checkNotNull(paramArrayOfObject));
    }
    
    public int size()
    {
      return this.rest.length + 2;
    }
    
    public Object get(int paramInt)
    {
      switch (paramInt)
      {
      case 0: 
        return this.first;
      case 1: 
        return this.second;
      }
      Preconditions.checkElementIndex(paramInt, size());
      return this.rest[(paramInt - 2)];
    }
  }
  
  private static class OnePlusArrayList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final Object first;
    final Object[] rest;
    private static final long serialVersionUID = 0L;
    
    OnePlusArrayList(Object paramObject, Object[] paramArrayOfObject)
    {
      this.first = paramObject;
      this.rest = ((Object[])Preconditions.checkNotNull(paramArrayOfObject));
    }
    
    public int size()
    {
      return this.rest.length + 1;
    }
    
    public Object get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return paramInt == 0 ? this.first : this.rest[(paramInt - 1)];
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Lists.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */