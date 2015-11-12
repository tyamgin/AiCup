package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

@Beta
@GwtCompatible
public final class Constraints
{
  public static Constraint notNull()
  {
    return NotNullConstraint.INSTANCE;
  }
  
  public static Collection constrainedCollection(Collection paramCollection, Constraint paramConstraint)
  {
    return new ConstrainedCollection(paramCollection, paramConstraint);
  }
  
  public static Set constrainedSet(Set paramSet, Constraint paramConstraint)
  {
    return new ConstrainedSet(paramSet, paramConstraint);
  }
  
  public static SortedSet constrainedSortedSet(SortedSet paramSortedSet, Constraint paramConstraint)
  {
    return new ConstrainedSortedSet(paramSortedSet, paramConstraint);
  }
  
  public static List constrainedList(List paramList, Constraint paramConstraint)
  {
    return (paramList instanceof RandomAccess) ? new ConstrainedRandomAccessList(paramList, paramConstraint) : new ConstrainedList(paramList, paramConstraint);
  }
  
  private static ListIterator constrainedListIterator(ListIterator paramListIterator, Constraint paramConstraint)
  {
    return new ConstrainedListIterator(paramListIterator, paramConstraint);
  }
  
  static Collection constrainedTypePreservingCollection(Collection paramCollection, Constraint paramConstraint)
  {
    if ((paramCollection instanceof SortedSet)) {
      return constrainedSortedSet((SortedSet)paramCollection, paramConstraint);
    }
    if ((paramCollection instanceof Set)) {
      return constrainedSet((Set)paramCollection, paramConstraint);
    }
    if ((paramCollection instanceof List)) {
      return constrainedList((List)paramCollection, paramConstraint);
    }
    return constrainedCollection(paramCollection, paramConstraint);
  }
  
  public static Multiset constrainedMultiset(Multiset paramMultiset, Constraint paramConstraint)
  {
    return new ConstrainedMultiset(paramMultiset, paramConstraint);
  }
  
  private static Collection checkElements(Collection paramCollection, Constraint paramConstraint)
  {
    ArrayList localArrayList = Lists.newArrayList(paramCollection);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramConstraint.checkElement(localObject);
    }
    return localArrayList;
  }
  
  static class ConstrainedMultiset
    extends ForwardingMultiset
  {
    private Multiset delegate;
    private final Constraint constraint;
    
    public ConstrainedMultiset(Multiset paramMultiset, Constraint paramConstraint)
    {
      this.delegate = ((Multiset)Preconditions.checkNotNull(paramMultiset));
      this.constraint = ((Constraint)Preconditions.checkNotNull(paramConstraint));
    }
    
    protected Multiset delegate()
    {
      return this.delegate;
    }
    
    public boolean add(Object paramObject)
    {
      return standardAdd(paramObject);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      return this.delegate.addAll(Constraints.checkElements(paramCollection, this.constraint));
    }
    
    public int add(Object paramObject, int paramInt)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.add(paramObject, paramInt);
    }
    
    public int setCount(Object paramObject, int paramInt)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.setCount(paramObject, paramInt);
    }
    
    public boolean setCount(Object paramObject, int paramInt1, int paramInt2)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.setCount(paramObject, paramInt1, paramInt2);
    }
  }
  
  static class ConstrainedListIterator
    extends ForwardingListIterator
  {
    private final ListIterator delegate;
    private final Constraint constraint;
    
    public ConstrainedListIterator(ListIterator paramListIterator, Constraint paramConstraint)
    {
      this.delegate = paramListIterator;
      this.constraint = paramConstraint;
    }
    
    protected ListIterator delegate()
    {
      return this.delegate;
    }
    
    public void add(Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      this.delegate.add(paramObject);
    }
    
    public void set(Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      this.delegate.set(paramObject);
    }
  }
  
  static class ConstrainedRandomAccessList
    extends Constraints.ConstrainedList
    implements RandomAccess
  {
    ConstrainedRandomAccessList(List paramList, Constraint paramConstraint)
    {
      super(paramConstraint);
    }
  }
  
  @GwtCompatible
  private static class ConstrainedList
    extends ForwardingList
  {
    final List delegate;
    final Constraint constraint;
    
    ConstrainedList(List paramList, Constraint paramConstraint)
    {
      this.delegate = ((List)Preconditions.checkNotNull(paramList));
      this.constraint = ((Constraint)Preconditions.checkNotNull(paramConstraint));
    }
    
    protected List delegate()
    {
      return this.delegate;
    }
    
    public boolean add(Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.add(paramObject);
    }
    
    public void add(int paramInt, Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      this.delegate.add(paramInt, paramObject);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      return this.delegate.addAll(Constraints.checkElements(paramCollection, this.constraint));
    }
    
    public boolean addAll(int paramInt, Collection paramCollection)
    {
      return this.delegate.addAll(paramInt, Constraints.checkElements(paramCollection, this.constraint));
    }
    
    public ListIterator listIterator()
    {
      return Constraints.constrainedListIterator(this.delegate.listIterator(), this.constraint);
    }
    
    public ListIterator listIterator(int paramInt)
    {
      return Constraints.constrainedListIterator(this.delegate.listIterator(paramInt), this.constraint);
    }
    
    public Object set(int paramInt, Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.set(paramInt, paramObject);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      return Constraints.constrainedList(this.delegate.subList(paramInt1, paramInt2), this.constraint);
    }
  }
  
  private static class ConstrainedSortedSet
    extends ForwardingSortedSet
  {
    final SortedSet delegate;
    final Constraint constraint;
    
    ConstrainedSortedSet(SortedSet paramSortedSet, Constraint paramConstraint)
    {
      this.delegate = ((SortedSet)Preconditions.checkNotNull(paramSortedSet));
      this.constraint = ((Constraint)Preconditions.checkNotNull(paramConstraint));
    }
    
    protected SortedSet delegate()
    {
      return this.delegate;
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return Constraints.constrainedSortedSet(this.delegate.headSet(paramObject), this.constraint);
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return Constraints.constrainedSortedSet(this.delegate.subSet(paramObject1, paramObject2), this.constraint);
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return Constraints.constrainedSortedSet(this.delegate.tailSet(paramObject), this.constraint);
    }
    
    public boolean add(Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.add(paramObject);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      return this.delegate.addAll(Constraints.checkElements(paramCollection, this.constraint));
    }
  }
  
  static class ConstrainedSet
    extends ForwardingSet
  {
    private final Set delegate;
    private final Constraint constraint;
    
    public ConstrainedSet(Set paramSet, Constraint paramConstraint)
    {
      this.delegate = ((Set)Preconditions.checkNotNull(paramSet));
      this.constraint = ((Constraint)Preconditions.checkNotNull(paramConstraint));
    }
    
    protected Set delegate()
    {
      return this.delegate;
    }
    
    public boolean add(Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.add(paramObject);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      return this.delegate.addAll(Constraints.checkElements(paramCollection, this.constraint));
    }
  }
  
  static class ConstrainedCollection
    extends ForwardingCollection
  {
    private final Collection delegate;
    private final Constraint constraint;
    
    public ConstrainedCollection(Collection paramCollection, Constraint paramConstraint)
    {
      this.delegate = ((Collection)Preconditions.checkNotNull(paramCollection));
      this.constraint = ((Constraint)Preconditions.checkNotNull(paramConstraint));
    }
    
    protected Collection delegate()
    {
      return this.delegate;
    }
    
    public boolean add(Object paramObject)
    {
      this.constraint.checkElement(paramObject);
      return this.delegate.add(paramObject);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      return this.delegate.addAll(Constraints.checkElements(paramCollection, this.constraint));
    }
  }
  
  private static enum NotNullConstraint
    implements Constraint
  {
    INSTANCE;
    
    public Object checkElement(Object paramObject)
    {
      return Preconditions.checkNotNull(paramObject);
    }
    
    public String toString()
    {
      return "Not null";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Constraints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */