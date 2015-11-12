package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.io.Serializable;
import java.util.NoSuchElementException;

@GwtCompatible
abstract class Cut
  implements Serializable, Comparable
{
  final Comparable endpoint;
  private static final long serialVersionUID = 0L;
  
  Cut(Comparable paramComparable)
  {
    this.endpoint = paramComparable;
  }
  
  abstract boolean isLessThan(Comparable paramComparable);
  
  abstract BoundType typeAsLowerBound();
  
  abstract BoundType typeAsUpperBound();
  
  abstract Cut withLowerBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain);
  
  abstract Cut withUpperBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain);
  
  abstract void describeAsLowerBound(StringBuilder paramStringBuilder);
  
  abstract void describeAsUpperBound(StringBuilder paramStringBuilder);
  
  abstract Comparable leastValueAbove(DiscreteDomain paramDiscreteDomain);
  
  abstract Comparable greatestValueBelow(DiscreteDomain paramDiscreteDomain);
  
  Cut canonical(DiscreteDomain paramDiscreteDomain)
  {
    return this;
  }
  
  public int compareTo(Cut paramCut)
  {
    if (paramCut == belowAll()) {
      return 1;
    }
    if (paramCut == aboveAll()) {
      return -1;
    }
    int i = Range.compareOrThrow(this.endpoint, paramCut.endpoint);
    if (i != 0) {
      return i;
    }
    return Booleans.compare(this instanceof AboveValue, paramCut instanceof AboveValue);
  }
  
  Comparable endpoint()
  {
    return this.endpoint;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Cut))
    {
      Cut localCut = (Cut)paramObject;
      try
      {
        int i = compareTo(localCut);
        return i == 0;
      }
      catch (ClassCastException localClassCastException) {}
    }
    return false;
  }
  
  static Cut belowAll()
  {
    return BelowAll.INSTANCE;
  }
  
  static Cut aboveAll()
  {
    return AboveAll.INSTANCE;
  }
  
  static Cut belowValue(Comparable paramComparable)
  {
    return new BelowValue(paramComparable);
  }
  
  static Cut aboveValue(Comparable paramComparable)
  {
    return new AboveValue(paramComparable);
  }
  
  private static final class AboveValue
    extends Cut
  {
    private static final long serialVersionUID = 0L;
    
    AboveValue(Comparable paramComparable)
    {
      super();
    }
    
    boolean isLessThan(Comparable paramComparable)
    {
      return Range.compareOrThrow(this.endpoint, paramComparable) < 0;
    }
    
    BoundType typeAsLowerBound()
    {
      return BoundType.OPEN;
    }
    
    BoundType typeAsUpperBound()
    {
      return BoundType.CLOSED;
    }
    
    Cut withLowerBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[paramBoundType.ordinal()])
      {
      case 2: 
        return this;
      case 1: 
        Comparable localComparable = paramDiscreteDomain.next(this.endpoint);
        return localComparable == null ? Cut.belowAll() : belowValue(localComparable);
      }
      throw new AssertionError();
    }
    
    Cut withUpperBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[paramBoundType.ordinal()])
      {
      case 2: 
        Comparable localComparable = paramDiscreteDomain.next(this.endpoint);
        return localComparable == null ? Cut.aboveAll() : belowValue(localComparable);
      case 1: 
        return this;
      }
      throw new AssertionError();
    }
    
    void describeAsLowerBound(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append('(').append(this.endpoint);
    }
    
    void describeAsUpperBound(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append(this.endpoint).append(']');
    }
    
    Comparable leastValueAbove(DiscreteDomain paramDiscreteDomain)
    {
      return paramDiscreteDomain.next(this.endpoint);
    }
    
    Comparable greatestValueBelow(DiscreteDomain paramDiscreteDomain)
    {
      return this.endpoint;
    }
    
    Cut canonical(DiscreteDomain paramDiscreteDomain)
    {
      Comparable localComparable = leastValueAbove(paramDiscreteDomain);
      return localComparable != null ? belowValue(localComparable) : Cut.aboveAll();
    }
    
    public int hashCode()
    {
      return this.endpoint.hashCode() ^ 0xFFFFFFFF;
    }
    
    public String toString()
    {
      return "/" + this.endpoint + "\\";
    }
  }
  
  private static final class BelowValue
    extends Cut
  {
    private static final long serialVersionUID = 0L;
    
    BelowValue(Comparable paramComparable)
    {
      super();
    }
    
    boolean isLessThan(Comparable paramComparable)
    {
      return Range.compareOrThrow(this.endpoint, paramComparable) <= 0;
    }
    
    BoundType typeAsLowerBound()
    {
      return BoundType.CLOSED;
    }
    
    BoundType typeAsUpperBound()
    {
      return BoundType.OPEN;
    }
    
    Cut withLowerBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[paramBoundType.ordinal()])
      {
      case 1: 
        return this;
      case 2: 
        Comparable localComparable = paramDiscreteDomain.previous(this.endpoint);
        return localComparable == null ? Cut.belowAll() : new Cut.AboveValue(localComparable);
      }
      throw new AssertionError();
    }
    
    Cut withUpperBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[paramBoundType.ordinal()])
      {
      case 1: 
        Comparable localComparable = paramDiscreteDomain.previous(this.endpoint);
        return localComparable == null ? Cut.aboveAll() : new Cut.AboveValue(localComparable);
      case 2: 
        return this;
      }
      throw new AssertionError();
    }
    
    void describeAsLowerBound(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append('[').append(this.endpoint);
    }
    
    void describeAsUpperBound(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append(this.endpoint).append(')');
    }
    
    Comparable leastValueAbove(DiscreteDomain paramDiscreteDomain)
    {
      return this.endpoint;
    }
    
    Comparable greatestValueBelow(DiscreteDomain paramDiscreteDomain)
    {
      return paramDiscreteDomain.previous(this.endpoint);
    }
    
    public int hashCode()
    {
      return this.endpoint.hashCode();
    }
    
    public String toString()
    {
      return "\\" + this.endpoint + "/";
    }
  }
  
  private static final class AboveAll
    extends Cut
  {
    private static final AboveAll INSTANCE = new AboveAll();
    private static final long serialVersionUID = 0L;
    
    private AboveAll()
    {
      super();
    }
    
    Comparable endpoint()
    {
      throw new IllegalStateException("range unbounded on this side");
    }
    
    boolean isLessThan(Comparable paramComparable)
    {
      return false;
    }
    
    BoundType typeAsLowerBound()
    {
      throw new AssertionError("this statement should be unreachable");
    }
    
    BoundType typeAsUpperBound()
    {
      throw new IllegalStateException();
    }
    
    Cut withLowerBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      throw new AssertionError("this statement should be unreachable");
    }
    
    Cut withUpperBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      throw new IllegalStateException();
    }
    
    void describeAsLowerBound(StringBuilder paramStringBuilder)
    {
      throw new AssertionError();
    }
    
    void describeAsUpperBound(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append("+∞)");
    }
    
    Comparable leastValueAbove(DiscreteDomain paramDiscreteDomain)
    {
      throw new AssertionError();
    }
    
    Comparable greatestValueBelow(DiscreteDomain paramDiscreteDomain)
    {
      return paramDiscreteDomain.maxValue();
    }
    
    public int compareTo(Cut paramCut)
    {
      return paramCut == this ? 0 : 1;
    }
    
    public String toString()
    {
      return "+∞";
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
  }
  
  private static final class BelowAll
    extends Cut
  {
    private static final BelowAll INSTANCE = new BelowAll();
    private static final long serialVersionUID = 0L;
    
    private BelowAll()
    {
      super();
    }
    
    Comparable endpoint()
    {
      throw new IllegalStateException("range unbounded on this side");
    }
    
    boolean isLessThan(Comparable paramComparable)
    {
      return true;
    }
    
    BoundType typeAsLowerBound()
    {
      throw new IllegalStateException();
    }
    
    BoundType typeAsUpperBound()
    {
      throw new AssertionError("this statement should be unreachable");
    }
    
    Cut withLowerBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      throw new IllegalStateException();
    }
    
    Cut withUpperBoundType(BoundType paramBoundType, DiscreteDomain paramDiscreteDomain)
    {
      throw new AssertionError("this statement should be unreachable");
    }
    
    void describeAsLowerBound(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append("(-∞");
    }
    
    void describeAsUpperBound(StringBuilder paramStringBuilder)
    {
      throw new AssertionError();
    }
    
    Comparable leastValueAbove(DiscreteDomain paramDiscreteDomain)
    {
      return paramDiscreteDomain.minValue();
    }
    
    Comparable greatestValueBelow(DiscreteDomain paramDiscreteDomain)
    {
      throw new AssertionError();
    }
    
    Cut canonical(DiscreteDomain paramDiscreteDomain)
    {
      try
      {
        return Cut.belowValue(paramDiscreteDomain.minValue());
      }
      catch (NoSuchElementException localNoSuchElementException) {}
      return this;
    }
    
    public int compareTo(Cut paramCut)
    {
      return paramCut == this ? 0 : -1;
    }
    
    public String toString()
    {
      return "-∞";
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Cut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */