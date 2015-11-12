package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

@Beta
public final class MinMaxPriorityQueue
  extends AbstractQueue
{
  private final Heap minHeap;
  private final Heap maxHeap;
  @VisibleForTesting
  final int maximumSize;
  private Object[] queue;
  private int size;
  private int modCount;
  private static final int EVEN_POWERS_OF_TWO = 1431655765;
  private static final int ODD_POWERS_OF_TWO = -1431655766;
  private static final int DEFAULT_CAPACITY = 11;
  
  public static MinMaxPriorityQueue create()
  {
    return new Builder(Ordering.natural(), null).create();
  }
  
  public static MinMaxPriorityQueue create(Iterable paramIterable)
  {
    return new Builder(Ordering.natural(), null).create(paramIterable);
  }
  
  public static Builder orderedBy(Comparator paramComparator)
  {
    return new Builder(paramComparator, null);
  }
  
  public static Builder expectedSize(int paramInt)
  {
    return new Builder(Ordering.natural(), null).expectedSize(paramInt);
  }
  
  public static Builder maximumSize(int paramInt)
  {
    return new Builder(Ordering.natural(), null).maximumSize(paramInt);
  }
  
  private MinMaxPriorityQueue(Builder paramBuilder, int paramInt)
  {
    Ordering localOrdering = paramBuilder.ordering();
    this.minHeap = new Heap(localOrdering);
    this.maxHeap = new Heap(localOrdering.reverse());
    this.minHeap.otherHeap = this.maxHeap;
    this.maxHeap.otherHeap = this.minHeap;
    this.maximumSize = paramBuilder.maximumSize;
    this.queue = new Object[paramInt];
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean add(Object paramObject)
  {
    offer(paramObject);
    return true;
  }
  
  public boolean addAll(Collection paramCollection)
  {
    boolean bool = false;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      offer(localObject);
      bool = true;
    }
    return bool;
  }
  
  public boolean offer(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    this.modCount += 1;
    int i = this.size++;
    growIfNeeded();
    heapForIndex(i).bubbleUp(i, paramObject);
    return (this.size <= this.maximumSize) || (pollLast() != paramObject);
  }
  
  public Object poll()
  {
    return isEmpty() ? null : removeAndGet(0);
  }
  
  Object elementData(int paramInt)
  {
    return this.queue[paramInt];
  }
  
  public Object peek()
  {
    return isEmpty() ? null : elementData(0);
  }
  
  private int getMaxElementIndex()
  {
    switch (this.size)
    {
    case 1: 
      return 0;
    case 2: 
      return 1;
    }
    return this.maxHeap.compareElements(1, 2) <= 0 ? 1 : 2;
  }
  
  public Object pollFirst()
  {
    return poll();
  }
  
  public Object removeFirst()
  {
    return remove();
  }
  
  public Object peekFirst()
  {
    return peek();
  }
  
  public Object pollLast()
  {
    return isEmpty() ? null : removeAndGet(getMaxElementIndex());
  }
  
  public Object removeLast()
  {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return removeAndGet(getMaxElementIndex());
  }
  
  public Object peekLast()
  {
    return isEmpty() ? null : elementData(getMaxElementIndex());
  }
  
  @VisibleForTesting
  MoveDesc removeAt(int paramInt)
  {
    Preconditions.checkPositionIndex(paramInt, this.size);
    this.modCount += 1;
    this.size -= 1;
    if (this.size == paramInt)
    {
      this.queue[this.size] = null;
      return null;
    }
    Object localObject1 = elementData(this.size);
    int i = heapForIndex(this.size).getCorrectLastElement(localObject1);
    Object localObject2 = elementData(this.size);
    this.queue[this.size] = null;
    MoveDesc localMoveDesc = fillHole(paramInt, localObject2);
    if (i < paramInt)
    {
      if (localMoveDesc == null) {
        return new MoveDesc(localObject1, localObject2);
      }
      return new MoveDesc(localObject1, localMoveDesc.replaced);
    }
    return localMoveDesc;
  }
  
  private MoveDesc fillHole(int paramInt, Object paramObject)
  {
    Heap localHeap = heapForIndex(paramInt);
    int i = localHeap.fillHoleAt(paramInt);
    int j = localHeap.bubbleUpAlternatingLevels(i, paramObject);
    if (j == i) {
      return localHeap.tryCrossOverAndBubbleUp(paramInt, i, paramObject);
    }
    return j < paramInt ? new MoveDesc(paramObject, elementData(paramInt)) : null;
  }
  
  private Object removeAndGet(int paramInt)
  {
    Object localObject = elementData(paramInt);
    removeAt(paramInt);
    return localObject;
  }
  
  private Heap heapForIndex(int paramInt)
  {
    return isEvenLevel(paramInt) ? this.minHeap : this.maxHeap;
  }
  
  @VisibleForTesting
  static boolean isEvenLevel(int paramInt)
  {
    int i = paramInt + 1;
    Preconditions.checkState(i > 0, "negative index");
    return (i & 0x55555555) > (i & 0xAAAAAAAA);
  }
  
  @VisibleForTesting
  boolean isIntact()
  {
    for (int i = 1; i < this.size; i++) {
      if (!heapForIndex(i).verifyIndex(i)) {
        return false;
      }
    }
    return true;
  }
  
  public Iterator iterator()
  {
    return new QueueIterator(null);
  }
  
  public void clear()
  {
    for (int i = 0; i < this.size; i++) {
      this.queue[i] = null;
    }
    this.size = 0;
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[this.size];
    System.arraycopy(this.queue, 0, arrayOfObject, 0, this.size);
    return arrayOfObject;
  }
  
  public Comparator comparator()
  {
    return this.minHeap.ordering;
  }
  
  @VisibleForTesting
  int capacity()
  {
    return this.queue.length;
  }
  
  @VisibleForTesting
  static int initialQueueSize(int paramInt1, int paramInt2, Iterable paramIterable)
  {
    int i = paramInt1 == -1 ? 11 : paramInt1;
    if ((paramIterable instanceof Collection))
    {
      int j = ((Collection)paramIterable).size();
      i = Math.max(i, j);
    }
    return capAtMaximumSize(i, paramInt2);
  }
  
  private void growIfNeeded()
  {
    if (this.size > this.queue.length)
    {
      int i = calculateNewCapacity();
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.queue, 0, arrayOfObject, 0, this.queue.length);
      this.queue = arrayOfObject;
    }
  }
  
  private int calculateNewCapacity()
  {
    int i = this.queue.length;
    int j = i < 64 ? (i + 1) * 2 : IntMath.checkedMultiply(i / 2, 3);
    return capAtMaximumSize(j, this.maximumSize);
  }
  
  private static int capAtMaximumSize(int paramInt1, int paramInt2)
  {
    return Math.min(paramInt1 - 1, paramInt2) + 1;
  }
  
  private class QueueIterator
    implements Iterator
  {
    private int cursor = -1;
    private int expectedModCount = MinMaxPriorityQueue.this.modCount;
    private Queue forgetMeNot;
    private List skipMe;
    private Object lastFromForgetMeNot;
    private boolean canRemove;
    
    private QueueIterator() {}
    
    public boolean hasNext()
    {
      checkModCount();
      return (nextNotInSkipMe(this.cursor + 1) < MinMaxPriorityQueue.this.size()) || ((this.forgetMeNot != null) && (!this.forgetMeNot.isEmpty()));
    }
    
    public Object next()
    {
      checkModCount();
      int i = nextNotInSkipMe(this.cursor + 1);
      if (i < MinMaxPriorityQueue.this.size())
      {
        this.cursor = i;
        this.canRemove = true;
        return MinMaxPriorityQueue.this.elementData(this.cursor);
      }
      if (this.forgetMeNot != null)
      {
        this.cursor = MinMaxPriorityQueue.this.size();
        this.lastFromForgetMeNot = this.forgetMeNot.poll();
        if (this.lastFromForgetMeNot != null)
        {
          this.canRemove = true;
          return this.lastFromForgetMeNot;
        }
      }
      throw new NoSuchElementException("iterator moved past last element in queue.");
    }
    
    public void remove()
    {
      Preconditions.checkState(this.canRemove, "no calls to remove() since the last call to next()");
      checkModCount();
      this.canRemove = false;
      this.expectedModCount += 1;
      if (this.cursor < MinMaxPriorityQueue.this.size())
      {
        MinMaxPriorityQueue.MoveDesc localMoveDesc = MinMaxPriorityQueue.this.removeAt(this.cursor);
        if (localMoveDesc != null)
        {
          if (this.forgetMeNot == null)
          {
            this.forgetMeNot = new ArrayDeque();
            this.skipMe = new ArrayList(3);
          }
          this.forgetMeNot.add(localMoveDesc.toTrickle);
          this.skipMe.add(localMoveDesc.replaced);
        }
        this.cursor -= 1;
      }
      else
      {
        Preconditions.checkState(removeExact(this.lastFromForgetMeNot));
        this.lastFromForgetMeNot = null;
      }
    }
    
    private boolean containsExact(Iterable paramIterable, Object paramObject)
    {
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (localObject == paramObject) {
          return true;
        }
      }
      return false;
    }
    
    boolean removeExact(Object paramObject)
    {
      for (int i = 0; i < MinMaxPriorityQueue.this.size; i++) {
        if (MinMaxPriorityQueue.this.queue[i] == paramObject)
        {
          MinMaxPriorityQueue.this.removeAt(i);
          return true;
        }
      }
      return false;
    }
    
    void checkModCount()
    {
      if (MinMaxPriorityQueue.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
    
    private int nextNotInSkipMe(int paramInt)
    {
      if (this.skipMe != null) {
        while ((paramInt < MinMaxPriorityQueue.this.size()) && (containsExact(this.skipMe, MinMaxPriorityQueue.this.elementData(paramInt)))) {
          paramInt++;
        }
      }
      return paramInt;
    }
  }
  
  private class Heap
  {
    final Ordering ordering;
    Heap otherHeap;
    
    Heap(Ordering paramOrdering)
    {
      this.ordering = paramOrdering;
    }
    
    int compareElements(int paramInt1, int paramInt2)
    {
      return this.ordering.compare(MinMaxPriorityQueue.this.elementData(paramInt1), MinMaxPriorityQueue.this.elementData(paramInt2));
    }
    
    MinMaxPriorityQueue.MoveDesc tryCrossOverAndBubbleUp(int paramInt1, int paramInt2, Object paramObject)
    {
      int i = crossOver(paramInt2, paramObject);
      if (i == paramInt2) {
        return null;
      }
      Object localObject;
      if (i < paramInt1) {
        localObject = MinMaxPriorityQueue.this.elementData(paramInt1);
      } else {
        localObject = MinMaxPriorityQueue.this.elementData(getParentIndex(paramInt1));
      }
      if (this.otherHeap.bubbleUpAlternatingLevels(i, paramObject) < paramInt1) {
        return new MinMaxPriorityQueue.MoveDesc(paramObject, localObject);
      }
      return null;
    }
    
    void bubbleUp(int paramInt, Object paramObject)
    {
      int i = crossOverUp(paramInt, paramObject);
      Heap localHeap;
      if (i == paramInt)
      {
        localHeap = this;
      }
      else
      {
        paramInt = i;
        localHeap = this.otherHeap;
      }
      localHeap.bubbleUpAlternatingLevels(paramInt, paramObject);
    }
    
    int bubbleUpAlternatingLevels(int paramInt, Object paramObject)
    {
      while (paramInt > 2)
      {
        int i = getGrandparentIndex(paramInt);
        Object localObject = MinMaxPriorityQueue.this.elementData(i);
        if (this.ordering.compare(localObject, paramObject) <= 0) {
          break;
        }
        MinMaxPriorityQueue.this.queue[paramInt] = localObject;
        paramInt = i;
      }
      MinMaxPriorityQueue.this.queue[paramInt] = paramObject;
      return paramInt;
    }
    
    int findMin(int paramInt1, int paramInt2)
    {
      if (paramInt1 >= MinMaxPriorityQueue.this.size) {
        return -1;
      }
      Preconditions.checkState(paramInt1 > 0);
      int i = Math.min(paramInt1, MinMaxPriorityQueue.this.size - paramInt2) + paramInt2;
      int j = paramInt1;
      for (int k = paramInt1 + 1; k < i; k++) {
        if (compareElements(k, j) < 0) {
          j = k;
        }
      }
      return j;
    }
    
    int findMinChild(int paramInt)
    {
      return findMin(getLeftChildIndex(paramInt), 2);
    }
    
    int findMinGrandChild(int paramInt)
    {
      int i = getLeftChildIndex(paramInt);
      if (i < 0) {
        return -1;
      }
      return findMin(getLeftChildIndex(i), 4);
    }
    
    int crossOverUp(int paramInt, Object paramObject)
    {
      if (paramInt == 0)
      {
        MinMaxPriorityQueue.this.queue[0] = paramObject;
        return 0;
      }
      int i = getParentIndex(paramInt);
      Object localObject1 = MinMaxPriorityQueue.this.elementData(i);
      if (i != 0)
      {
        int j = getParentIndex(i);
        int k = getRightChildIndex(j);
        if ((k != i) && (getLeftChildIndex(k) >= MinMaxPriorityQueue.this.size))
        {
          Object localObject2 = MinMaxPriorityQueue.this.elementData(k);
          if (this.ordering.compare(localObject2, localObject1) < 0)
          {
            i = k;
            localObject1 = localObject2;
          }
        }
      }
      if (this.ordering.compare(localObject1, paramObject) < 0)
      {
        MinMaxPriorityQueue.this.queue[paramInt] = localObject1;
        MinMaxPriorityQueue.this.queue[i] = paramObject;
        return i;
      }
      MinMaxPriorityQueue.this.queue[paramInt] = paramObject;
      return paramInt;
    }
    
    int getCorrectLastElement(Object paramObject)
    {
      int i = getParentIndex(MinMaxPriorityQueue.this.size);
      if (i != 0)
      {
        int j = getParentIndex(i);
        int k = getRightChildIndex(j);
        if ((k != i) && (getLeftChildIndex(k) >= MinMaxPriorityQueue.this.size))
        {
          Object localObject = MinMaxPriorityQueue.this.elementData(k);
          if (this.ordering.compare(localObject, paramObject) < 0)
          {
            MinMaxPriorityQueue.this.queue[k] = paramObject;
            MinMaxPriorityQueue.this.queue[MinMaxPriorityQueue.this.size] = localObject;
            return k;
          }
        }
      }
      return MinMaxPriorityQueue.this.size;
    }
    
    int crossOver(int paramInt, Object paramObject)
    {
      int i = findMinChild(paramInt);
      if ((i > 0) && (this.ordering.compare(MinMaxPriorityQueue.this.elementData(i), paramObject) < 0))
      {
        MinMaxPriorityQueue.this.queue[paramInt] = MinMaxPriorityQueue.this.elementData(i);
        MinMaxPriorityQueue.this.queue[i] = paramObject;
        return i;
      }
      return crossOverUp(paramInt, paramObject);
    }
    
    int fillHoleAt(int paramInt)
    {
      int i;
      while ((i = findMinGrandChild(paramInt)) > 0)
      {
        MinMaxPriorityQueue.this.queue[paramInt] = MinMaxPriorityQueue.this.elementData(i);
        paramInt = i;
      }
      return paramInt;
    }
    
    private boolean verifyIndex(int paramInt)
    {
      if ((getLeftChildIndex(paramInt) < MinMaxPriorityQueue.this.size) && (compareElements(paramInt, getLeftChildIndex(paramInt)) > 0)) {
        return false;
      }
      if ((getRightChildIndex(paramInt) < MinMaxPriorityQueue.this.size) && (compareElements(paramInt, getRightChildIndex(paramInt)) > 0)) {
        return false;
      }
      if ((paramInt > 0) && (compareElements(paramInt, getParentIndex(paramInt)) > 0)) {
        return false;
      }
      return (paramInt <= 2) || (compareElements(getGrandparentIndex(paramInt), paramInt) <= 0);
    }
    
    private int getLeftChildIndex(int paramInt)
    {
      return paramInt * 2 + 1;
    }
    
    private int getRightChildIndex(int paramInt)
    {
      return paramInt * 2 + 2;
    }
    
    private int getParentIndex(int paramInt)
    {
      return (paramInt - 1) / 2;
    }
    
    private int getGrandparentIndex(int paramInt)
    {
      return getParentIndex(getParentIndex(paramInt));
    }
  }
  
  static class MoveDesc
  {
    final Object toTrickle;
    final Object replaced;
    
    MoveDesc(Object paramObject1, Object paramObject2)
    {
      this.toTrickle = paramObject1;
      this.replaced = paramObject2;
    }
  }
  
  @Beta
  public static final class Builder
  {
    private static final int UNSET_EXPECTED_SIZE = -1;
    private final Comparator comparator;
    private int expectedSize = -1;
    private int maximumSize = Integer.MAX_VALUE;
    
    private Builder(Comparator paramComparator)
    {
      this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
    }
    
    public Builder expectedSize(int paramInt)
    {
      Preconditions.checkArgument(paramInt >= 0);
      this.expectedSize = paramInt;
      return this;
    }
    
    public Builder maximumSize(int paramInt)
    {
      Preconditions.checkArgument(paramInt > 0);
      this.maximumSize = paramInt;
      return this;
    }
    
    public MinMaxPriorityQueue create()
    {
      return create(Collections.emptySet());
    }
    
    public MinMaxPriorityQueue create(Iterable paramIterable)
    {
      MinMaxPriorityQueue localMinMaxPriorityQueue = new MinMaxPriorityQueue(this, MinMaxPriorityQueue.initialQueueSize(this.expectedSize, this.maximumSize, paramIterable), null);
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        localMinMaxPriorityQueue.offer(localObject);
      }
      return localMinMaxPriorityQueue;
    }
    
    private Ordering ordering()
    {
      return Ordering.from(this.comparator);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\MinMaxPriorityQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */