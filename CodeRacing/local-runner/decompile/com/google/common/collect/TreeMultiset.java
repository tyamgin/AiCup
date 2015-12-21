package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;

@GwtCompatible(emulated=true)
public final class TreeMultiset
  extends AbstractSortedMultiset
  implements Serializable
{
  private final transient Reference rootReference;
  private final transient GeneralRange range;
  private final transient AvlNode header;
  @GwtIncompatible("not needed in emulated source")
  private static final long serialVersionUID = 1L;
  
  public static TreeMultiset create()
  {
    return new TreeMultiset(Ordering.natural());
  }
  
  public static TreeMultiset create(Comparator paramComparator)
  {
    return paramComparator == null ? new TreeMultiset(Ordering.natural()) : new TreeMultiset(paramComparator);
  }
  
  public static TreeMultiset create(Iterable paramIterable)
  {
    TreeMultiset localTreeMultiset = create();
    Iterables.addAll(localTreeMultiset, paramIterable);
    return localTreeMultiset;
  }
  
  TreeMultiset(Reference paramReference, GeneralRange paramGeneralRange, AvlNode paramAvlNode)
  {
    super(paramGeneralRange.comparator());
    this.rootReference = paramReference;
    this.range = paramGeneralRange;
    this.header = paramAvlNode;
  }
  
  TreeMultiset(Comparator paramComparator)
  {
    super(paramComparator);
    this.range = GeneralRange.all(paramComparator);
    this.header = new AvlNode(null, 1);
    successor(this.header, this.header);
    this.rootReference = new Reference(null);
  }
  
  private long aggregateForEntries(Aggregate paramAggregate)
  {
    AvlNode localAvlNode = (AvlNode)this.rootReference.get();
    long l = paramAggregate.treeAggregate(localAvlNode);
    if (this.range.hasLowerBound()) {
      l -= aggregateBelowRange(paramAggregate, localAvlNode);
    }
    if (this.range.hasUpperBound()) {
      l -= aggregateAboveRange(paramAggregate, localAvlNode);
    }
    return l;
  }
  
  private long aggregateBelowRange(Aggregate paramAggregate, AvlNode paramAvlNode)
  {
    if (paramAvlNode == null) {
      return 0L;
    }
    int i = comparator().compare(this.range.getLowerEndpoint(), paramAvlNode.elem);
    if (i < 0) {
      return aggregateBelowRange(paramAggregate, paramAvlNode.left);
    }
    if (i == 0)
    {
      switch (this.range.getLowerBoundType())
      {
      case OPEN: 
        return paramAggregate.nodeAggregate(paramAvlNode) + paramAggregate.treeAggregate(paramAvlNode.left);
      case CLOSED: 
        return paramAggregate.treeAggregate(paramAvlNode.left);
      }
      throw new AssertionError();
    }
    return paramAggregate.treeAggregate(paramAvlNode.left) + paramAggregate.nodeAggregate(paramAvlNode) + aggregateBelowRange(paramAggregate, paramAvlNode.right);
  }
  
  private long aggregateAboveRange(Aggregate paramAggregate, AvlNode paramAvlNode)
  {
    if (paramAvlNode == null) {
      return 0L;
    }
    int i = comparator().compare(this.range.getUpperEndpoint(), paramAvlNode.elem);
    if (i > 0) {
      return aggregateAboveRange(paramAggregate, paramAvlNode.right);
    }
    if (i == 0)
    {
      switch (this.range.getUpperBoundType())
      {
      case OPEN: 
        return paramAggregate.nodeAggregate(paramAvlNode) + paramAggregate.treeAggregate(paramAvlNode.right);
      case CLOSED: 
        return paramAggregate.treeAggregate(paramAvlNode.right);
      }
      throw new AssertionError();
    }
    return paramAggregate.treeAggregate(paramAvlNode.right) + paramAggregate.nodeAggregate(paramAvlNode) + aggregateAboveRange(paramAggregate, paramAvlNode.left);
  }
  
  public int size()
  {
    return Ints.saturatedCast(aggregateForEntries(Aggregate.SIZE));
  }
  
  int distinctElements()
  {
    return Ints.saturatedCast(aggregateForEntries(Aggregate.DISTINCT));
  }
  
  public int count(Object paramObject)
  {
    try
    {
      Object localObject = paramObject;
      AvlNode localAvlNode = (AvlNode)this.rootReference.get();
      if ((!this.range.contains(localObject)) || (localAvlNode == null)) {
        return 0;
      }
      return localAvlNode.count(comparator(), localObject);
    }
    catch (ClassCastException localClassCastException)
    {
      return 0;
    }
    catch (NullPointerException localNullPointerException) {}
    return 0;
  }
  
  public int add(Object paramObject, int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0, "occurrences must be >= 0 but was %s", new Object[] { Integer.valueOf(paramInt) });
    if (paramInt == 0) {
      return count(paramObject);
    }
    Preconditions.checkArgument(this.range.contains(paramObject));
    AvlNode localAvlNode1 = (AvlNode)this.rootReference.get();
    if (localAvlNode1 == null)
    {
      comparator().compare(paramObject, paramObject);
      localObject = new AvlNode(paramObject, paramInt);
      successor(this.header, (AvlNode)localObject, this.header);
      this.rootReference.checkAndSet(localAvlNode1, localObject);
      return 0;
    }
    Object localObject = new int[1];
    AvlNode localAvlNode2 = localAvlNode1.add(comparator(), paramObject, paramInt, (int[])localObject);
    this.rootReference.checkAndSet(localAvlNode1, localAvlNode2);
    return localObject[0];
  }
  
  public int remove(Object paramObject, int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0, "occurrences must be >= 0 but was %s", new Object[] { Integer.valueOf(paramInt) });
    if (paramInt == 0) {
      return count(paramObject);
    }
    AvlNode localAvlNode1 = (AvlNode)this.rootReference.get();
    int[] arrayOfInt = new int[1];
    AvlNode localAvlNode2;
    try
    {
      Object localObject = paramObject;
      if ((!this.range.contains(localObject)) || (localAvlNode1 == null)) {
        return 0;
      }
      localAvlNode2 = localAvlNode1.remove(comparator(), localObject, paramInt, arrayOfInt);
    }
    catch (ClassCastException localClassCastException)
    {
      return 0;
    }
    catch (NullPointerException localNullPointerException)
    {
      return 0;
    }
    this.rootReference.checkAndSet(localAvlNode1, localAvlNode2);
    return arrayOfInt[0];
  }
  
  public int setCount(Object paramObject, int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0);
    if (!this.range.contains(paramObject))
    {
      Preconditions.checkArgument(paramInt == 0);
      return 0;
    }
    AvlNode localAvlNode1 = (AvlNode)this.rootReference.get();
    if (localAvlNode1 == null)
    {
      if (paramInt > 0) {
        add(paramObject, paramInt);
      }
      return 0;
    }
    int[] arrayOfInt = new int[1];
    AvlNode localAvlNode2 = localAvlNode1.setCount(comparator(), paramObject, paramInt, arrayOfInt);
    this.rootReference.checkAndSet(localAvlNode1, localAvlNode2);
    return arrayOfInt[0];
  }
  
  public boolean setCount(Object paramObject, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt2 >= 0);
    Preconditions.checkArgument(paramInt1 >= 0);
    Preconditions.checkArgument(this.range.contains(paramObject));
    AvlNode localAvlNode1 = (AvlNode)this.rootReference.get();
    if (localAvlNode1 == null)
    {
      if (paramInt1 == 0)
      {
        if (paramInt2 > 0) {
          add(paramObject, paramInt2);
        }
        return true;
      }
      return false;
    }
    int[] arrayOfInt = new int[1];
    AvlNode localAvlNode2 = localAvlNode1.setCount(comparator(), paramObject, paramInt1, paramInt2, arrayOfInt);
    this.rootReference.checkAndSet(localAvlNode1, localAvlNode2);
    return arrayOfInt[0] == paramInt1;
  }
  
  private Multiset.Entry wrapEntry(final AvlNode paramAvlNode)
  {
    new Multisets.AbstractEntry()
    {
      public Object getElement()
      {
        return paramAvlNode.getElement();
      }
      
      public int getCount()
      {
        int i = paramAvlNode.getCount();
        if (i == 0) {
          return TreeMultiset.this.count(getElement());
        }
        return i;
      }
    };
  }
  
  private AvlNode firstNode()
  {
    AvlNode localAvlNode1 = (AvlNode)this.rootReference.get();
    if (localAvlNode1 == null) {
      return null;
    }
    AvlNode localAvlNode2;
    if (this.range.hasLowerBound())
    {
      Object localObject = this.range.getLowerEndpoint();
      localAvlNode2 = ((AvlNode)this.rootReference.get()).ceiling(comparator(), localObject);
      if (localAvlNode2 == null) {
        return null;
      }
      if ((this.range.getLowerBoundType() == BoundType.OPEN) && (comparator().compare(localObject, localAvlNode2.getElement()) == 0)) {
        localAvlNode2 = localAvlNode2.succ;
      }
    }
    else
    {
      localAvlNode2 = this.header.succ;
    }
    return (localAvlNode2 == this.header) || (!this.range.contains(localAvlNode2.getElement())) ? null : localAvlNode2;
  }
  
  private AvlNode lastNode()
  {
    AvlNode localAvlNode1 = (AvlNode)this.rootReference.get();
    if (localAvlNode1 == null) {
      return null;
    }
    AvlNode localAvlNode2;
    if (this.range.hasUpperBound())
    {
      Object localObject = this.range.getUpperEndpoint();
      localAvlNode2 = ((AvlNode)this.rootReference.get()).floor(comparator(), localObject);
      if (localAvlNode2 == null) {
        return null;
      }
      if ((this.range.getUpperBoundType() == BoundType.OPEN) && (comparator().compare(localObject, localAvlNode2.getElement()) == 0)) {
        localAvlNode2 = localAvlNode2.pred;
      }
    }
    else
    {
      localAvlNode2 = this.header.pred;
    }
    return (localAvlNode2 == this.header) || (!this.range.contains(localAvlNode2.getElement())) ? null : localAvlNode2;
  }
  
  Iterator entryIterator()
  {
    new Iterator()
    {
      TreeMultiset.AvlNode current = TreeMultiset.this.firstNode();
      Multiset.Entry prevEntry;
      
      public boolean hasNext()
      {
        if (this.current == null) {
          return false;
        }
        if (TreeMultiset.this.range.tooHigh(this.current.getElement()))
        {
          this.current = null;
          return false;
        }
        return true;
      }
      
      public Multiset.Entry next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Multiset.Entry localEntry = TreeMultiset.this.wrapEntry(this.current);
        this.prevEntry = localEntry;
        if (this.current.succ == TreeMultiset.this.header) {
          this.current = null;
        } else {
          this.current = this.current.succ;
        }
        return localEntry;
      }
      
      public void remove()
      {
        Preconditions.checkState(this.prevEntry != null);
        TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
        this.prevEntry = null;
      }
    };
  }
  
  Iterator descendingEntryIterator()
  {
    new Iterator()
    {
      TreeMultiset.AvlNode current = TreeMultiset.this.lastNode();
      Multiset.Entry prevEntry = null;
      
      public boolean hasNext()
      {
        if (this.current == null) {
          return false;
        }
        if (TreeMultiset.this.range.tooLow(this.current.getElement()))
        {
          this.current = null;
          return false;
        }
        return true;
      }
      
      public Multiset.Entry next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Multiset.Entry localEntry = TreeMultiset.this.wrapEntry(this.current);
        this.prevEntry = localEntry;
        if (this.current.pred == TreeMultiset.this.header) {
          this.current = null;
        } else {
          this.current = this.current.pred;
        }
        return localEntry;
      }
      
      public void remove()
      {
        Preconditions.checkState(this.prevEntry != null);
        TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
        this.prevEntry = null;
      }
    };
  }
  
  public SortedMultiset headMultiset(Object paramObject, BoundType paramBoundType)
  {
    return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.upTo(comparator(), paramObject, paramBoundType)), this.header);
  }
  
  public SortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType)
  {
    return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.downTo(comparator(), paramObject, paramBoundType)), this.header);
  }
  
  static int distinctElements(AvlNode paramAvlNode)
  {
    return paramAvlNode == null ? 0 : paramAvlNode.distinctElements;
  }
  
  private static void successor(AvlNode paramAvlNode1, AvlNode paramAvlNode2)
  {
    paramAvlNode1.succ = paramAvlNode2;
    paramAvlNode2.pred = paramAvlNode1;
  }
  
  private static void successor(AvlNode paramAvlNode1, AvlNode paramAvlNode2, AvlNode paramAvlNode3)
  {
    successor(paramAvlNode1, paramAvlNode2);
    successor(paramAvlNode2, paramAvlNode3);
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(elementSet().comparator());
    Serialization.writeMultiset(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Comparator localComparator = (Comparator)paramObjectInputStream.readObject();
    Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set(this, localComparator);
    Serialization.getFieldSetter(TreeMultiset.class, "range").set(this, GeneralRange.all(localComparator));
    Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set(this, new Reference(null));
    AvlNode localAvlNode = new AvlNode(null, 1);
    Serialization.getFieldSetter(TreeMultiset.class, "header").set(this, localAvlNode);
    successor(localAvlNode, localAvlNode);
    Serialization.populateMultiset(this, paramObjectInputStream);
  }
  
  private static final class AvlNode
    extends Multisets.AbstractEntry
  {
    private final Object elem;
    private int elemCount;
    private int distinctElements;
    private long totalCount;
    private int height;
    private AvlNode left;
    private AvlNode right;
    private AvlNode pred;
    private AvlNode succ;
    
    AvlNode(Object paramObject, int paramInt)
    {
      Preconditions.checkArgument(paramInt > 0);
      this.elem = paramObject;
      this.elemCount = paramInt;
      this.totalCount = paramInt;
      this.distinctElements = 1;
      this.height = 1;
      this.left = null;
      this.right = null;
    }
    
    public int count(Comparator paramComparator, Object paramObject)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      if (i < 0) {
        return this.left == null ? 0 : this.left.count(paramComparator, paramObject);
      }
      if (i > 0) {
        return this.right == null ? 0 : this.right.count(paramComparator, paramObject);
      }
      return this.elemCount;
    }
    
    private AvlNode addRightChild(Object paramObject, int paramInt)
    {
      this.right = new AvlNode(paramObject, paramInt);
      TreeMultiset.successor(this, this.right, this.succ);
      this.height = Math.max(2, this.height);
      this.distinctElements += 1;
      this.totalCount += paramInt;
      return this;
    }
    
    private AvlNode addLeftChild(Object paramObject, int paramInt)
    {
      this.left = new AvlNode(paramObject, paramInt);
      TreeMultiset.successor(this.pred, this.left, this);
      this.height = Math.max(2, this.height);
      this.distinctElements += 1;
      this.totalCount += paramInt;
      return this;
    }
    
    AvlNode add(Comparator paramComparator, Object paramObject, int paramInt, int[] paramArrayOfInt)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      AvlNode localAvlNode;
      int j;
      if (i < 0)
      {
        localAvlNode = this.left;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          return addLeftChild(paramObject, paramInt);
        }
        j = localAvlNode.height;
        this.left = localAvlNode.add(paramComparator, paramObject, paramInt, paramArrayOfInt);
        if (paramArrayOfInt[0] == 0) {
          this.distinctElements += 1;
        }
        this.totalCount += paramInt;
        return this.left.height == j ? this : rebalance();
      }
      if (i > 0)
      {
        localAvlNode = this.right;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          return addRightChild(paramObject, paramInt);
        }
        j = localAvlNode.height;
        this.right = localAvlNode.add(paramComparator, paramObject, paramInt, paramArrayOfInt);
        if (paramArrayOfInt[0] == 0) {
          this.distinctElements += 1;
        }
        this.totalCount += paramInt;
        return this.right.height == j ? this : rebalance();
      }
      paramArrayOfInt[0] = this.elemCount;
      long l = this.elemCount + paramInt;
      Preconditions.checkArgument(l <= 2147483647L);
      this.elemCount += paramInt;
      this.totalCount += paramInt;
      return this;
    }
    
    AvlNode remove(Comparator paramComparator, Object paramObject, int paramInt, int[] paramArrayOfInt)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      AvlNode localAvlNode;
      if (i < 0)
      {
        localAvlNode = this.left;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          return this;
        }
        this.left = localAvlNode.remove(paramComparator, paramObject, paramInt, paramArrayOfInt);
        if (paramArrayOfInt[0] > 0) {
          if (paramInt >= paramArrayOfInt[0])
          {
            this.distinctElements -= 1;
            this.totalCount -= paramArrayOfInt[0];
          }
          else
          {
            this.totalCount -= paramInt;
          }
        }
        return paramArrayOfInt[0] == 0 ? this : rebalance();
      }
      if (i > 0)
      {
        localAvlNode = this.right;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          return this;
        }
        this.right = localAvlNode.remove(paramComparator, paramObject, paramInt, paramArrayOfInt);
        if (paramArrayOfInt[0] > 0) {
          if (paramInt >= paramArrayOfInt[0])
          {
            this.distinctElements -= 1;
            this.totalCount -= paramArrayOfInt[0];
          }
          else
          {
            this.totalCount -= paramInt;
          }
        }
        return rebalance();
      }
      paramArrayOfInt[0] = this.elemCount;
      if (paramInt >= this.elemCount) {
        return deleteMe();
      }
      this.elemCount -= paramInt;
      this.totalCount -= paramInt;
      return this;
    }
    
    AvlNode setCount(Comparator paramComparator, Object paramObject, int paramInt, int[] paramArrayOfInt)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      AvlNode localAvlNode;
      if (i < 0)
      {
        localAvlNode = this.left;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          return paramInt > 0 ? addLeftChild(paramObject, paramInt) : this;
        }
        this.left = localAvlNode.setCount(paramComparator, paramObject, paramInt, paramArrayOfInt);
        if ((paramInt == 0) && (paramArrayOfInt[0] != 0)) {
          this.distinctElements -= 1;
        } else if ((paramInt > 0) && (paramArrayOfInt[0] == 0)) {
          this.distinctElements += 1;
        }
        this.totalCount += paramInt - paramArrayOfInt[0];
        return rebalance();
      }
      if (i > 0)
      {
        localAvlNode = this.right;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          return paramInt > 0 ? addRightChild(paramObject, paramInt) : this;
        }
        this.right = localAvlNode.setCount(paramComparator, paramObject, paramInt, paramArrayOfInt);
        if ((paramInt == 0) && (paramArrayOfInt[0] != 0)) {
          this.distinctElements -= 1;
        } else if ((paramInt > 0) && (paramArrayOfInt[0] == 0)) {
          this.distinctElements += 1;
        }
        this.totalCount += paramInt - paramArrayOfInt[0];
        return rebalance();
      }
      paramArrayOfInt[0] = this.elemCount;
      if (paramInt == 0) {
        return deleteMe();
      }
      this.totalCount += paramInt - this.elemCount;
      this.elemCount = paramInt;
      return this;
    }
    
    AvlNode setCount(Comparator paramComparator, Object paramObject, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      AvlNode localAvlNode;
      if (i < 0)
      {
        localAvlNode = this.left;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          if ((paramInt1 == 0) && (paramInt2 > 0)) {
            return addLeftChild(paramObject, paramInt2);
          }
          return this;
        }
        this.left = localAvlNode.setCount(paramComparator, paramObject, paramInt1, paramInt2, paramArrayOfInt);
        if (paramArrayOfInt[0] == paramInt1)
        {
          if ((paramInt2 == 0) && (paramArrayOfInt[0] != 0)) {
            this.distinctElements -= 1;
          } else if ((paramInt2 > 0) && (paramArrayOfInt[0] == 0)) {
            this.distinctElements += 1;
          }
          this.totalCount += paramInt2 - paramArrayOfInt[0];
        }
        return rebalance();
      }
      if (i > 0)
      {
        localAvlNode = this.right;
        if (localAvlNode == null)
        {
          paramArrayOfInt[0] = 0;
          if ((paramInt1 == 0) && (paramInt2 > 0)) {
            return addRightChild(paramObject, paramInt2);
          }
          return this;
        }
        this.right = localAvlNode.setCount(paramComparator, paramObject, paramInt1, paramInt2, paramArrayOfInt);
        if (paramArrayOfInt[0] == paramInt1)
        {
          if ((paramInt2 == 0) && (paramArrayOfInt[0] != 0)) {
            this.distinctElements -= 1;
          } else if ((paramInt2 > 0) && (paramArrayOfInt[0] == 0)) {
            this.distinctElements += 1;
          }
          this.totalCount += paramInt2 - paramArrayOfInt[0];
        }
        return rebalance();
      }
      paramArrayOfInt[0] = this.elemCount;
      if (paramInt1 == this.elemCount)
      {
        if (paramInt2 == 0) {
          return deleteMe();
        }
        this.totalCount += paramInt2 - this.elemCount;
        this.elemCount = paramInt2;
      }
      return this;
    }
    
    private AvlNode deleteMe()
    {
      int i = this.elemCount;
      this.elemCount = 0;
      TreeMultiset.successor(this.pred, this.succ);
      if (this.left == null) {
        return this.right;
      }
      if (this.right == null) {
        return this.left;
      }
      if (this.left.height >= this.right.height)
      {
        localAvlNode = this.pred;
        localAvlNode.left = this.left.removeMax(localAvlNode);
        localAvlNode.right = this.right;
        this.distinctElements -= 1;
        this.totalCount -= i;
        return localAvlNode.rebalance();
      }
      AvlNode localAvlNode = this.succ;
      localAvlNode.right = this.right.removeMin(localAvlNode);
      localAvlNode.left = this.left;
      this.distinctElements -= 1;
      this.totalCount -= i;
      return localAvlNode.rebalance();
    }
    
    private AvlNode removeMin(AvlNode paramAvlNode)
    {
      if (this.left == null) {
        return this.right;
      }
      this.left = this.left.removeMin(paramAvlNode);
      this.distinctElements -= 1;
      this.totalCount -= paramAvlNode.elemCount;
      return rebalance();
    }
    
    private AvlNode removeMax(AvlNode paramAvlNode)
    {
      if (this.right == null) {
        return this.left;
      }
      this.right = this.right.removeMax(paramAvlNode);
      this.distinctElements -= 1;
      this.totalCount -= paramAvlNode.elemCount;
      return rebalance();
    }
    
    private void recomputeMultiset()
    {
      this.distinctElements = (1 + TreeMultiset.distinctElements(this.left) + TreeMultiset.distinctElements(this.right));
      this.totalCount = (this.elemCount + totalCount(this.left) + totalCount(this.right));
    }
    
    private void recomputeHeight()
    {
      this.height = (1 + Math.max(height(this.left), height(this.right)));
    }
    
    private void recompute()
    {
      recomputeMultiset();
      recomputeHeight();
    }
    
    private AvlNode rebalance()
    {
      switch (balanceFactor())
      {
      case -2: 
        if (this.right.balanceFactor() > 0) {
          this.right = this.right.rotateRight();
        }
        return rotateLeft();
      case 2: 
        if (this.left.balanceFactor() < 0) {
          this.left = this.left.rotateLeft();
        }
        return rotateRight();
      }
      recomputeHeight();
      return this;
    }
    
    private int balanceFactor()
    {
      return height(this.left) - height(this.right);
    }
    
    private AvlNode rotateLeft()
    {
      Preconditions.checkState(this.right != null);
      AvlNode localAvlNode = this.right;
      this.right = localAvlNode.left;
      localAvlNode.left = this;
      localAvlNode.totalCount = this.totalCount;
      localAvlNode.distinctElements = this.distinctElements;
      recompute();
      localAvlNode.recomputeHeight();
      return localAvlNode;
    }
    
    private AvlNode rotateRight()
    {
      Preconditions.checkState(this.left != null);
      AvlNode localAvlNode = this.left;
      this.left = localAvlNode.right;
      localAvlNode.right = this;
      localAvlNode.totalCount = this.totalCount;
      localAvlNode.distinctElements = this.distinctElements;
      recompute();
      localAvlNode.recomputeHeight();
      return localAvlNode;
    }
    
    private static long totalCount(AvlNode paramAvlNode)
    {
      return paramAvlNode == null ? 0L : paramAvlNode.totalCount;
    }
    
    private static int height(AvlNode paramAvlNode)
    {
      return paramAvlNode == null ? 0 : paramAvlNode.height;
    }
    
    private AvlNode ceiling(Comparator paramComparator, Object paramObject)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      if (i < 0) {
        return this.left == null ? this : (AvlNode)Objects.firstNonNull(this.left.ceiling(paramComparator, paramObject), this);
      }
      if (i == 0) {
        return this;
      }
      return this.right == null ? null : this.right.ceiling(paramComparator, paramObject);
    }
    
    private AvlNode floor(Comparator paramComparator, Object paramObject)
    {
      int i = paramComparator.compare(paramObject, this.elem);
      if (i > 0) {
        return this.right == null ? this : (AvlNode)Objects.firstNonNull(this.right.floor(paramComparator, paramObject), this);
      }
      if (i == 0) {
        return this;
      }
      return this.left == null ? null : this.left.floor(paramComparator, paramObject);
    }
    
    public Object getElement()
    {
      return this.elem;
    }
    
    public int getCount()
    {
      return this.elemCount;
    }
    
    public String toString()
    {
      return Multisets.immutableEntry(getElement(), getCount()).toString();
    }
  }
  
  private static final class Reference
  {
    private Object value;
    
    public Object get()
    {
      return this.value;
    }
    
    public void checkAndSet(Object paramObject1, Object paramObject2)
    {
      if (this.value != paramObject1) {
        throw new ConcurrentModificationException();
      }
      this.value = paramObject2;
    }
  }
  
  private static abstract enum Aggregate
  {
    SIZE,  DISTINCT;
    
    abstract int nodeAggregate(TreeMultiset.AvlNode paramAvlNode);
    
    abstract long treeAggregate(TreeMultiset.AvlNode paramAvlNode);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TreeMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */