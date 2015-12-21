package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@Beta
@GwtIncompatible("uses NavigableMap")
public class TreeRangeSet
  extends AbstractRangeSet
{
  @VisibleForTesting
  final NavigableMap rangesByLowerBound;
  private transient Set asRanges;
  private transient RangeSet complement;
  
  public static TreeRangeSet create()
  {
    return new TreeRangeSet(new TreeMap());
  }
  
  public static TreeRangeSet create(RangeSet paramRangeSet)
  {
    TreeRangeSet localTreeRangeSet = create();
    localTreeRangeSet.addAll(paramRangeSet);
    return localTreeRangeSet;
  }
  
  private TreeRangeSet(NavigableMap paramNavigableMap)
  {
    this.rangesByLowerBound = paramNavigableMap;
  }
  
  public Set asRanges()
  {
    Set localSet = this.asRanges;
    return localSet == null ? (this.asRanges = new AsRanges()) : localSet;
  }
  
  public Range rangeContaining(Comparable paramComparable)
  {
    Preconditions.checkNotNull(paramComparable);
    Map.Entry localEntry = this.rangesByLowerBound.floorEntry(Cut.belowValue(paramComparable));
    if ((localEntry != null) && (((Range)localEntry.getValue()).contains(paramComparable))) {
      return (Range)localEntry.getValue();
    }
    return null;
  }
  
  public boolean encloses(Range paramRange)
  {
    Preconditions.checkNotNull(paramRange);
    Map.Entry localEntry = this.rangesByLowerBound.floorEntry(paramRange.lowerBound);
    return (localEntry != null) && (((Range)localEntry.getValue()).encloses(paramRange));
  }
  
  private Range rangeEnclosing(Range paramRange)
  {
    Preconditions.checkNotNull(paramRange);
    Map.Entry localEntry = this.rangesByLowerBound.floorEntry(paramRange.lowerBound);
    return (localEntry != null) && (((Range)localEntry.getValue()).encloses(paramRange)) ? (Range)localEntry.getValue() : null;
  }
  
  public Range span()
  {
    Map.Entry localEntry1 = this.rangesByLowerBound.firstEntry();
    Map.Entry localEntry2 = this.rangesByLowerBound.lastEntry();
    if (localEntry1 == null) {
      throw new NoSuchElementException();
    }
    return Range.create(((Range)localEntry1.getValue()).lowerBound, ((Range)localEntry2.getValue()).upperBound);
  }
  
  public void add(Range paramRange)
  {
    Preconditions.checkNotNull(paramRange);
    if (paramRange.isEmpty()) {
      return;
    }
    Cut localCut1 = paramRange.lowerBound;
    Cut localCut2 = paramRange.upperBound;
    Map.Entry localEntry = this.rangesByLowerBound.lowerEntry(localCut1);
    if (localEntry != null)
    {
      localObject = (Range)localEntry.getValue();
      if (((Range)localObject).upperBound.compareTo(localCut1) >= 0)
      {
        if (((Range)localObject).upperBound.compareTo(localCut2) >= 0) {
          localCut2 = ((Range)localObject).upperBound;
        }
        localCut1 = ((Range)localObject).lowerBound;
      }
    }
    Object localObject = this.rangesByLowerBound.floorEntry(localCut2);
    if (localObject != null)
    {
      Range localRange = (Range)((Map.Entry)localObject).getValue();
      if (localRange.upperBound.compareTo(localCut2) >= 0) {
        localCut2 = localRange.upperBound;
      }
    }
    this.rangesByLowerBound.subMap(localCut1, localCut2).clear();
    replaceRangeWithSameLowerBound(Range.create(localCut1, localCut2));
  }
  
  public void remove(Range paramRange)
  {
    Preconditions.checkNotNull(paramRange);
    if (paramRange.isEmpty()) {
      return;
    }
    Map.Entry localEntry = this.rangesByLowerBound.lowerEntry(paramRange.lowerBound);
    if (localEntry != null)
    {
      localObject = (Range)localEntry.getValue();
      if (((Range)localObject).upperBound.compareTo(paramRange.lowerBound) >= 0)
      {
        if ((paramRange.hasUpperBound()) && (((Range)localObject).upperBound.compareTo(paramRange.upperBound) >= 0)) {
          replaceRangeWithSameLowerBound(Range.create(paramRange.upperBound, ((Range)localObject).upperBound));
        }
        replaceRangeWithSameLowerBound(Range.create(((Range)localObject).lowerBound, paramRange.lowerBound));
      }
    }
    Object localObject = this.rangesByLowerBound.floorEntry(paramRange.upperBound);
    if (localObject != null)
    {
      Range localRange = (Range)((Map.Entry)localObject).getValue();
      if ((paramRange.hasUpperBound()) && (localRange.upperBound.compareTo(paramRange.upperBound) >= 0)) {
        replaceRangeWithSameLowerBound(Range.create(paramRange.upperBound, localRange.upperBound));
      }
    }
    this.rangesByLowerBound.subMap(paramRange.lowerBound, paramRange.upperBound).clear();
  }
  
  private void replaceRangeWithSameLowerBound(Range paramRange)
  {
    if (paramRange.isEmpty()) {
      this.rangesByLowerBound.remove(paramRange.lowerBound);
    } else {
      this.rangesByLowerBound.put(paramRange.lowerBound, paramRange);
    }
  }
  
  public RangeSet complement()
  {
    RangeSet localRangeSet = this.complement;
    return localRangeSet == null ? (this.complement = new Complement()) : localRangeSet;
  }
  
  public RangeSet subRangeSet(Range paramRange)
  {
    return paramRange.equals(Range.all()) ? this : new SubRangeSet(paramRange);
  }
  
  private final class SubRangeSet
    extends TreeRangeSet
  {
    private final Range restriction;
    
    SubRangeSet(Range paramRange)
    {
      super(null);
      this.restriction = paramRange;
    }
    
    public boolean encloses(Range paramRange)
    {
      if ((!this.restriction.isEmpty()) && (this.restriction.encloses(paramRange)))
      {
        Range localRange = TreeRangeSet.this.rangeEnclosing(paramRange);
        return (localRange != null) && (!localRange.intersection(this.restriction).isEmpty());
      }
      return false;
    }
    
    public Range rangeContaining(Comparable paramComparable)
    {
      if (!this.restriction.contains(paramComparable)) {
        return null;
      }
      Range localRange = TreeRangeSet.this.rangeContaining(paramComparable);
      return localRange == null ? null : localRange.intersection(this.restriction);
    }
    
    public void add(Range paramRange)
    {
      Preconditions.checkArgument(this.restriction.encloses(paramRange), "Cannot add range %s to subRangeSet(%s)", new Object[] { paramRange, this.restriction });
      super.add(paramRange);
    }
    
    public void remove(Range paramRange)
    {
      if (paramRange.isConnected(this.restriction)) {
        TreeRangeSet.this.remove(paramRange.intersection(this.restriction));
      }
    }
    
    public boolean contains(Comparable paramComparable)
    {
      return (this.restriction.contains(paramComparable)) && (TreeRangeSet.this.contains(paramComparable));
    }
    
    public void clear()
    {
      TreeRangeSet.this.remove(this.restriction);
    }
    
    public RangeSet subRangeSet(Range paramRange)
    {
      if (paramRange.encloses(this.restriction)) {
        return this;
      }
      if (paramRange.isConnected(this.restriction)) {
        return new SubRangeSet(this, this.restriction.intersection(paramRange));
      }
      return ImmutableRangeSet.of();
    }
  }
  
  private static final class SubRangeSetRangesByLowerBound
    extends AbstractNavigableMap
  {
    private final Range lowerBoundWindow;
    private final Range restriction;
    private final NavigableMap rangesByLowerBound;
    private final NavigableMap rangesByUpperBound;
    
    private SubRangeSetRangesByLowerBound(Range paramRange1, Range paramRange2, NavigableMap paramNavigableMap)
    {
      this.lowerBoundWindow = ((Range)Preconditions.checkNotNull(paramRange1));
      this.restriction = ((Range)Preconditions.checkNotNull(paramRange2));
      this.rangesByLowerBound = ((NavigableMap)Preconditions.checkNotNull(paramNavigableMap));
      this.rangesByUpperBound = new TreeRangeSet.RangesByUpperBound(paramNavigableMap);
    }
    
    private NavigableMap subMap(Range paramRange)
    {
      if (!paramRange.isConnected(this.lowerBoundWindow)) {
        return ImmutableSortedMap.of();
      }
      return new SubRangeSetRangesByLowerBound(this.lowerBoundWindow.intersection(paramRange), this.restriction, this.rangesByLowerBound);
    }
    
    public NavigableMap subMap(Cut paramCut1, boolean paramBoolean1, Cut paramCut2, boolean paramBoolean2)
    {
      return subMap(Range.range(paramCut1, BoundType.forBoolean(paramBoolean1), paramCut2, BoundType.forBoolean(paramBoolean2)));
    }
    
    public NavigableMap headMap(Cut paramCut, boolean paramBoolean)
    {
      return subMap(Range.upTo(paramCut, BoundType.forBoolean(paramBoolean)));
    }
    
    public NavigableMap tailMap(Cut paramCut, boolean paramBoolean)
    {
      return subMap(Range.downTo(paramCut, BoundType.forBoolean(paramBoolean)));
    }
    
    public Comparator comparator()
    {
      return Ordering.natural();
    }
    
    public boolean containsKey(Object paramObject)
    {
      return get(paramObject) != null;
    }
    
    public Range get(Object paramObject)
    {
      if ((paramObject instanceof Cut)) {
        try
        {
          Cut localCut = (Cut)paramObject;
          if ((!this.lowerBoundWindow.contains(localCut)) || (localCut.compareTo(this.restriction.lowerBound) < 0) || (localCut.compareTo(this.restriction.upperBound) >= 0)) {
            return null;
          }
          Range localRange;
          if (localCut.equals(this.restriction.lowerBound))
          {
            localRange = (Range)Maps.valueOrNull(this.rangesByLowerBound.floorEntry(localCut));
            if ((localRange != null) && (localRange.upperBound.compareTo(this.restriction.lowerBound) > 0)) {
              return localRange.intersection(this.restriction);
            }
          }
          else
          {
            localRange = (Range)this.rangesByLowerBound.get(localCut);
            if (localRange != null) {
              return localRange.intersection(this.restriction);
            }
          }
        }
        catch (ClassCastException localClassCastException)
        {
          return null;
        }
      }
      return null;
    }
    
    Iterator entryIterator()
    {
      if (this.restriction.isEmpty()) {
        return Iterators.emptyIterator();
      }
      if (this.lowerBoundWindow.upperBound.isLessThan(this.restriction.lowerBound)) {
        return Iterators.emptyIterator();
      }
      final Iterator localIterator;
      if (this.lowerBoundWindow.lowerBound.isLessThan(this.restriction.lowerBound)) {
        localIterator = this.rangesByUpperBound.tailMap(this.restriction.lowerBound, false).values().iterator();
      } else {
        localIterator = this.rangesByLowerBound.tailMap(this.lowerBoundWindow.lowerBound.endpoint(), this.lowerBoundWindow.lowerBoundType() == BoundType.CLOSED).values().iterator();
      }
      final Cut localCut = (Cut)Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
      new AbstractIterator()
      {
        protected Map.Entry computeNext()
        {
          if (!localIterator.hasNext()) {
            return (Map.Entry)endOfData();
          }
          Range localRange = (Range)localIterator.next();
          if (localCut.isLessThan(localRange.lowerBound)) {
            return (Map.Entry)endOfData();
          }
          localRange = localRange.intersection(TreeRangeSet.SubRangeSetRangesByLowerBound.this.restriction);
          return Maps.immutableEntry(localRange.lowerBound, localRange);
        }
      };
    }
    
    Iterator descendingEntryIterator()
    {
      if (this.restriction.isEmpty()) {
        return Iterators.emptyIterator();
      }
      Cut localCut = (Cut)Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
      final Iterator localIterator = this.rangesByLowerBound.headMap(localCut.endpoint(), localCut.typeAsUpperBound() == BoundType.CLOSED).descendingMap().values().iterator();
      new AbstractIterator()
      {
        protected Map.Entry computeNext()
        {
          if (!localIterator.hasNext()) {
            return (Map.Entry)endOfData();
          }
          Range localRange = (Range)localIterator.next();
          if (TreeRangeSet.SubRangeSetRangesByLowerBound.this.restriction.lowerBound.compareTo(localRange.upperBound) >= 0) {
            return (Map.Entry)endOfData();
          }
          localRange = localRange.intersection(TreeRangeSet.SubRangeSetRangesByLowerBound.this.restriction);
          if (TreeRangeSet.SubRangeSetRangesByLowerBound.this.lowerBoundWindow.contains(localRange.lowerBound)) {
            return Maps.immutableEntry(localRange.lowerBound, localRange);
          }
          return (Map.Entry)endOfData();
        }
      };
    }
    
    public int size()
    {
      return Iterators.size(entryIterator());
    }
  }
  
  private final class Complement
    extends TreeRangeSet
  {
    Complement()
    {
      super(null);
    }
    
    public void add(Range paramRange)
    {
      TreeRangeSet.this.remove(paramRange);
    }
    
    public void remove(Range paramRange)
    {
      TreeRangeSet.this.add(paramRange);
    }
    
    public boolean contains(Comparable paramComparable)
    {
      return !TreeRangeSet.this.contains(paramComparable);
    }
    
    public RangeSet complement()
    {
      return TreeRangeSet.this;
    }
  }
  
  private static final class ComplementRangesByLowerBound
    extends AbstractNavigableMap
  {
    private final NavigableMap positiveRangesByLowerBound;
    private final NavigableMap positiveRangesByUpperBound;
    private final Range complementLowerBoundWindow;
    
    ComplementRangesByLowerBound(NavigableMap paramNavigableMap)
    {
      this(paramNavigableMap, Range.all());
    }
    
    private ComplementRangesByLowerBound(NavigableMap paramNavigableMap, Range paramRange)
    {
      this.positiveRangesByLowerBound = paramNavigableMap;
      this.positiveRangesByUpperBound = new TreeRangeSet.RangesByUpperBound(paramNavigableMap);
      this.complementLowerBoundWindow = paramRange;
    }
    
    private NavigableMap subMap(Range paramRange)
    {
      if (!this.complementLowerBoundWindow.isConnected(paramRange)) {
        return ImmutableSortedMap.of();
      }
      paramRange = paramRange.intersection(this.complementLowerBoundWindow);
      return new ComplementRangesByLowerBound(this.positiveRangesByLowerBound, paramRange);
    }
    
    public NavigableMap subMap(Cut paramCut1, boolean paramBoolean1, Cut paramCut2, boolean paramBoolean2)
    {
      return subMap(Range.range(paramCut1, BoundType.forBoolean(paramBoolean1), paramCut2, BoundType.forBoolean(paramBoolean2)));
    }
    
    public NavigableMap headMap(Cut paramCut, boolean paramBoolean)
    {
      return subMap(Range.upTo(paramCut, BoundType.forBoolean(paramBoolean)));
    }
    
    public NavigableMap tailMap(Cut paramCut, boolean paramBoolean)
    {
      return subMap(Range.downTo(paramCut, BoundType.forBoolean(paramBoolean)));
    }
    
    public Comparator comparator()
    {
      return Ordering.natural();
    }
    
    Iterator entryIterator()
    {
      Collection localCollection;
      if (this.complementLowerBoundWindow.hasLowerBound()) {
        localCollection = this.positiveRangesByUpperBound.tailMap(this.complementLowerBoundWindow.lowerEndpoint(), this.complementLowerBoundWindow.lowerBoundType() == BoundType.CLOSED).values();
      } else {
        localCollection = this.positiveRangesByUpperBound.values();
      }
      final PeekingIterator localPeekingIterator = Iterators.peekingIterator(localCollection.iterator());
      final Cut localCut;
      if ((this.complementLowerBoundWindow.contains(Cut.belowAll())) && ((!localPeekingIterator.hasNext()) || (((Range)localPeekingIterator.peek()).lowerBound != Cut.belowAll()))) {
        localCut = Cut.belowAll();
      } else if (localPeekingIterator.hasNext()) {
        localCut = ((Range)localPeekingIterator.next()).upperBound;
      } else {
        return Iterators.emptyIterator();
      }
      new AbstractIterator()
      {
        Cut nextComplementRangeLowerBound = localCut;
        
        protected Map.Entry computeNext()
        {
          if ((TreeRangeSet.ComplementRangesByLowerBound.this.complementLowerBoundWindow.upperBound.isLessThan(this.nextComplementRangeLowerBound)) || (this.nextComplementRangeLowerBound == Cut.aboveAll())) {
            return (Map.Entry)endOfData();
          }
          Range localRange1;
          if (localPeekingIterator.hasNext())
          {
            Range localRange2 = (Range)localPeekingIterator.next();
            localRange1 = Range.create(this.nextComplementRangeLowerBound, localRange2.lowerBound);
            this.nextComplementRangeLowerBound = localRange2.upperBound;
          }
          else
          {
            localRange1 = Range.create(this.nextComplementRangeLowerBound, Cut.aboveAll());
            this.nextComplementRangeLowerBound = Cut.aboveAll();
          }
          return Maps.immutableEntry(localRange1.lowerBound, localRange1);
        }
      };
    }
    
    Iterator descendingEntryIterator()
    {
      Cut localCut1 = this.complementLowerBoundWindow.hasUpperBound() ? (Cut)this.complementLowerBoundWindow.upperEndpoint() : Cut.aboveAll();
      boolean bool = (this.complementLowerBoundWindow.hasUpperBound()) && (this.complementLowerBoundWindow.upperBoundType() == BoundType.CLOSED);
      final PeekingIterator localPeekingIterator = Iterators.peekingIterator(this.positiveRangesByUpperBound.headMap(localCut1, bool).descendingMap().values().iterator());
      Cut localCut2;
      if (localPeekingIterator.hasNext())
      {
        localCut2 = ((Range)localPeekingIterator.peek()).upperBound == Cut.aboveAll() ? ((Range)localPeekingIterator.next()).lowerBound : (Cut)this.positiveRangesByLowerBound.higherKey(((Range)localPeekingIterator.peek()).upperBound);
      }
      else
      {
        if ((!this.complementLowerBoundWindow.contains(Cut.belowAll())) || (this.positiveRangesByLowerBound.containsKey(Cut.belowAll()))) {
          return Iterators.emptyIterator();
        }
        localCut2 = (Cut)this.positiveRangesByLowerBound.higherKey(Cut.belowAll());
      }
      final Cut localCut3 = (Cut)Objects.firstNonNull(localCut2, Cut.aboveAll());
      new AbstractIterator()
      {
        Cut nextComplementRangeUpperBound = localCut3;
        
        protected Map.Entry computeNext()
        {
          if (this.nextComplementRangeUpperBound == Cut.belowAll()) {
            return (Map.Entry)endOfData();
          }
          Range localRange1;
          if (localPeekingIterator.hasNext())
          {
            localRange1 = (Range)localPeekingIterator.next();
            Range localRange2 = Range.create(localRange1.upperBound, this.nextComplementRangeUpperBound);
            this.nextComplementRangeUpperBound = localRange1.lowerBound;
            if (TreeRangeSet.ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(localRange2.lowerBound)) {
              return Maps.immutableEntry(localRange2.lowerBound, localRange2);
            }
          }
          else if (TreeRangeSet.ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(Cut.belowAll()))
          {
            localRange1 = Range.create(Cut.belowAll(), this.nextComplementRangeUpperBound);
            this.nextComplementRangeUpperBound = Cut.belowAll();
            return Maps.immutableEntry(Cut.belowAll(), localRange1);
          }
          return (Map.Entry)endOfData();
        }
      };
    }
    
    public int size()
    {
      return Iterators.size(entryIterator());
    }
    
    public Range get(Object paramObject)
    {
      if ((paramObject instanceof Cut)) {
        try
        {
          Cut localCut = (Cut)paramObject;
          Map.Entry localEntry = tailMap(localCut, true).firstEntry();
          if ((localEntry != null) && (((Cut)localEntry.getKey()).equals(localCut))) {
            return (Range)localEntry.getValue();
          }
        }
        catch (ClassCastException localClassCastException)
        {
          return null;
        }
      }
      return null;
    }
    
    public boolean containsKey(Object paramObject)
    {
      return get(paramObject) != null;
    }
  }
  
  @VisibleForTesting
  static final class RangesByUpperBound
    extends AbstractNavigableMap
  {
    private final NavigableMap rangesByLowerBound;
    private final Range upperBoundWindow;
    
    RangesByUpperBound(NavigableMap paramNavigableMap)
    {
      this.rangesByLowerBound = paramNavigableMap;
      this.upperBoundWindow = Range.all();
    }
    
    private RangesByUpperBound(NavigableMap paramNavigableMap, Range paramRange)
    {
      this.rangesByLowerBound = paramNavigableMap;
      this.upperBoundWindow = paramRange;
    }
    
    private NavigableMap subMap(Range paramRange)
    {
      if (paramRange.isConnected(this.upperBoundWindow)) {
        return new RangesByUpperBound(this.rangesByLowerBound, paramRange.intersection(this.upperBoundWindow));
      }
      return ImmutableSortedMap.of();
    }
    
    public NavigableMap subMap(Cut paramCut1, boolean paramBoolean1, Cut paramCut2, boolean paramBoolean2)
    {
      return subMap(Range.range(paramCut1, BoundType.forBoolean(paramBoolean1), paramCut2, BoundType.forBoolean(paramBoolean2)));
    }
    
    public NavigableMap headMap(Cut paramCut, boolean paramBoolean)
    {
      return subMap(Range.upTo(paramCut, BoundType.forBoolean(paramBoolean)));
    }
    
    public NavigableMap tailMap(Cut paramCut, boolean paramBoolean)
    {
      return subMap(Range.downTo(paramCut, BoundType.forBoolean(paramBoolean)));
    }
    
    public Comparator comparator()
    {
      return Ordering.natural();
    }
    
    public boolean containsKey(Object paramObject)
    {
      return get(paramObject) != null;
    }
    
    public Range get(Object paramObject)
    {
      if ((paramObject instanceof Cut)) {
        try
        {
          Cut localCut = (Cut)paramObject;
          if (!this.upperBoundWindow.contains(localCut)) {
            return null;
          }
          Map.Entry localEntry = this.rangesByLowerBound.lowerEntry(localCut);
          if ((localEntry != null) && (((Range)localEntry.getValue()).upperBound.equals(localCut))) {
            return (Range)localEntry.getValue();
          }
        }
        catch (ClassCastException localClassCastException)
        {
          return null;
        }
      }
      return null;
    }
    
    Iterator entryIterator()
    {
      final Iterator localIterator;
      if (!this.upperBoundWindow.hasLowerBound())
      {
        localIterator = this.rangesByLowerBound.values().iterator();
      }
      else
      {
        Map.Entry localEntry = this.rangesByLowerBound.lowerEntry(this.upperBoundWindow.lowerEndpoint());
        if (localEntry == null) {
          localIterator = this.rangesByLowerBound.values().iterator();
        } else if (this.upperBoundWindow.lowerBound.isLessThan(((Range)localEntry.getValue()).upperBound)) {
          localIterator = this.rangesByLowerBound.tailMap(localEntry.getKey(), true).values().iterator();
        } else {
          localIterator = this.rangesByLowerBound.tailMap(this.upperBoundWindow.lowerEndpoint(), true).values().iterator();
        }
      }
      new AbstractIterator()
      {
        protected Map.Entry computeNext()
        {
          if (!localIterator.hasNext()) {
            return (Map.Entry)endOfData();
          }
          Range localRange = (Range)localIterator.next();
          if (TreeRangeSet.RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan(localRange.upperBound)) {
            return (Map.Entry)endOfData();
          }
          return Maps.immutableEntry(localRange.upperBound, localRange);
        }
      };
    }
    
    Iterator descendingEntryIterator()
    {
      Collection localCollection;
      if (this.upperBoundWindow.hasUpperBound()) {
        localCollection = this.rangesByLowerBound.headMap(this.upperBoundWindow.upperEndpoint(), false).descendingMap().values();
      } else {
        localCollection = this.rangesByLowerBound.descendingMap().values();
      }
      final PeekingIterator localPeekingIterator = Iterators.peekingIterator(localCollection.iterator());
      if ((localPeekingIterator.hasNext()) && (this.upperBoundWindow.upperBound.isLessThan(((Range)localPeekingIterator.peek()).upperBound))) {
        localPeekingIterator.next();
      }
      new AbstractIterator()
      {
        protected Map.Entry computeNext()
        {
          if (!localPeekingIterator.hasNext()) {
            return (Map.Entry)endOfData();
          }
          Range localRange = (Range)localPeekingIterator.next();
          return TreeRangeSet.RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan(localRange.upperBound) ? Maps.immutableEntry(localRange.upperBound, localRange) : (Map.Entry)endOfData();
        }
      };
    }
    
    public int size()
    {
      if (this.upperBoundWindow.equals(Range.all())) {
        return this.rangesByLowerBound.size();
      }
      return Iterators.size(entryIterator());
    }
    
    public boolean isEmpty()
    {
      return !entryIterator().hasNext() ? true : this.upperBoundWindow.equals(Range.all()) ? this.rangesByLowerBound.isEmpty() : false;
    }
  }
  
  final class AsRanges
    extends ForwardingCollection
    implements Set
  {
    AsRanges() {}
    
    protected Collection delegate()
    {
      return TreeRangeSet.this.rangesByLowerBound.values();
    }
    
    public int hashCode()
    {
      return Sets.hashCodeImpl(this);
    }
    
    public boolean equals(Object paramObject)
    {
      return Sets.equalsImpl(this, paramObject);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TreeRangeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */