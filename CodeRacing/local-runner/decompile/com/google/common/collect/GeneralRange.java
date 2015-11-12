package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;

@GwtCompatible(serializable=true)
final class GeneralRange
  implements Serializable
{
  private final Comparator comparator;
  private final boolean hasLowerBound;
  private final Object lowerEndpoint;
  private final BoundType lowerBoundType;
  private final boolean hasUpperBound;
  private final Object upperEndpoint;
  private final BoundType upperBoundType;
  private transient GeneralRange reverse;
  
  static GeneralRange from(Range paramRange)
  {
    Object localObject1 = paramRange.hasLowerBound() ? paramRange.lowerEndpoint() : null;
    BoundType localBoundType1 = paramRange.hasLowerBound() ? paramRange.lowerBoundType() : BoundType.OPEN;
    Object localObject2 = paramRange.hasUpperBound() ? paramRange.upperEndpoint() : null;
    BoundType localBoundType2 = paramRange.hasUpperBound() ? paramRange.upperBoundType() : BoundType.OPEN;
    return new GeneralRange(Ordering.natural(), paramRange.hasLowerBound(), localObject1, localBoundType1, paramRange.hasUpperBound(), localObject2, localBoundType2);
  }
  
  static GeneralRange all(Comparator paramComparator)
  {
    return new GeneralRange(paramComparator, false, null, BoundType.OPEN, false, null, BoundType.OPEN);
  }
  
  static GeneralRange downTo(Comparator paramComparator, Object paramObject, BoundType paramBoundType)
  {
    return new GeneralRange(paramComparator, true, paramObject, paramBoundType, false, null, BoundType.OPEN);
  }
  
  static GeneralRange upTo(Comparator paramComparator, Object paramObject, BoundType paramBoundType)
  {
    return new GeneralRange(paramComparator, false, null, BoundType.OPEN, true, paramObject, paramBoundType);
  }
  
  static GeneralRange range(Comparator paramComparator, Object paramObject1, BoundType paramBoundType1, Object paramObject2, BoundType paramBoundType2)
  {
    return new GeneralRange(paramComparator, true, paramObject1, paramBoundType1, true, paramObject2, paramBoundType2);
  }
  
  private GeneralRange(Comparator paramComparator, boolean paramBoolean1, Object paramObject1, BoundType paramBoundType1, boolean paramBoolean2, Object paramObject2, BoundType paramBoundType2)
  {
    this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
    this.hasLowerBound = paramBoolean1;
    this.hasUpperBound = paramBoolean2;
    this.lowerEndpoint = paramObject1;
    this.lowerBoundType = ((BoundType)Preconditions.checkNotNull(paramBoundType1));
    this.upperEndpoint = paramObject2;
    this.upperBoundType = ((BoundType)Preconditions.checkNotNull(paramBoundType2));
    if (paramBoolean1) {
      paramComparator.compare(paramObject1, paramObject1);
    }
    if (paramBoolean2) {
      paramComparator.compare(paramObject2, paramObject2);
    }
    if ((paramBoolean1) && (paramBoolean2))
    {
      int i = paramComparator.compare(paramObject1, paramObject2);
      Preconditions.checkArgument(i <= 0, "lowerEndpoint (%s) > upperEndpoint (%s)", new Object[] { paramObject1, paramObject2 });
      if (i == 0) {
        Preconditions.checkArgument((paramBoundType1 != BoundType.OPEN ? 1 : 0) | (paramBoundType2 != BoundType.OPEN ? 1 : 0));
      }
    }
  }
  
  Comparator comparator()
  {
    return this.comparator;
  }
  
  boolean hasLowerBound()
  {
    return this.hasLowerBound;
  }
  
  boolean hasUpperBound()
  {
    return this.hasUpperBound;
  }
  
  boolean isEmpty()
  {
    return ((hasUpperBound()) && (tooLow(getUpperEndpoint()))) || ((hasLowerBound()) && (tooHigh(getLowerEndpoint())));
  }
  
  boolean tooLow(Object paramObject)
  {
    if (!hasLowerBound()) {
      return false;
    }
    Object localObject = getLowerEndpoint();
    int i = this.comparator.compare(paramObject, localObject);
    return (i < 0 ? 1 : 0) | (i == 0 ? 1 : 0) & (getLowerBoundType() == BoundType.OPEN ? 1 : 0);
  }
  
  boolean tooHigh(Object paramObject)
  {
    if (!hasUpperBound()) {
      return false;
    }
    Object localObject = getUpperEndpoint();
    int i = this.comparator.compare(paramObject, localObject);
    return (i > 0 ? 1 : 0) | (i == 0 ? 1 : 0) & (getUpperBoundType() == BoundType.OPEN ? 1 : 0);
  }
  
  boolean contains(Object paramObject)
  {
    return (!tooLow(paramObject)) && (!tooHigh(paramObject));
  }
  
  GeneralRange intersect(GeneralRange paramGeneralRange)
  {
    Preconditions.checkNotNull(paramGeneralRange);
    Preconditions.checkArgument(this.comparator.equals(paramGeneralRange.comparator));
    boolean bool1 = this.hasLowerBound;
    Object localObject1 = getLowerEndpoint();
    BoundType localBoundType1 = getLowerBoundType();
    if (!hasLowerBound())
    {
      bool1 = paramGeneralRange.hasLowerBound;
      localObject1 = paramGeneralRange.getLowerEndpoint();
      localBoundType1 = paramGeneralRange.getLowerBoundType();
    }
    else if (paramGeneralRange.hasLowerBound())
    {
      int i = this.comparator.compare(getLowerEndpoint(), paramGeneralRange.getLowerEndpoint());
      if ((i < 0) || ((i == 0) && (paramGeneralRange.getLowerBoundType() == BoundType.OPEN)))
      {
        localObject1 = paramGeneralRange.getLowerEndpoint();
        localBoundType1 = paramGeneralRange.getLowerBoundType();
      }
    }
    boolean bool2 = this.hasUpperBound;
    Object localObject2 = getUpperEndpoint();
    BoundType localBoundType2 = getUpperBoundType();
    int j;
    if (!hasUpperBound())
    {
      bool2 = paramGeneralRange.hasUpperBound;
      localObject2 = paramGeneralRange.getUpperEndpoint();
      localBoundType2 = paramGeneralRange.getUpperBoundType();
    }
    else if (paramGeneralRange.hasUpperBound())
    {
      j = this.comparator.compare(getUpperEndpoint(), paramGeneralRange.getUpperEndpoint());
      if ((j > 0) || ((j == 0) && (paramGeneralRange.getUpperBoundType() == BoundType.OPEN)))
      {
        localObject2 = paramGeneralRange.getUpperEndpoint();
        localBoundType2 = paramGeneralRange.getUpperBoundType();
      }
    }
    if ((bool1) && (bool2))
    {
      j = this.comparator.compare(localObject1, localObject2);
      if ((j > 0) || ((j == 0) && (localBoundType1 == BoundType.OPEN) && (localBoundType2 == BoundType.OPEN)))
      {
        localObject1 = localObject2;
        localBoundType1 = BoundType.OPEN;
        localBoundType2 = BoundType.CLOSED;
      }
    }
    return new GeneralRange(this.comparator, bool1, localObject1, localBoundType1, bool2, localObject2, localBoundType2);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof GeneralRange))
    {
      GeneralRange localGeneralRange = (GeneralRange)paramObject;
      return (this.comparator.equals(localGeneralRange.comparator)) && (this.hasLowerBound == localGeneralRange.hasLowerBound) && (this.hasUpperBound == localGeneralRange.hasUpperBound) && (getLowerBoundType().equals(localGeneralRange.getLowerBoundType())) && (getUpperBoundType().equals(localGeneralRange.getUpperBoundType())) && (Objects.equal(getLowerEndpoint(), localGeneralRange.getLowerEndpoint())) && (Objects.equal(getUpperEndpoint(), localGeneralRange.getUpperEndpoint()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.comparator, getLowerEndpoint(), getLowerBoundType(), getUpperEndpoint(), getUpperBoundType() });
  }
  
  GeneralRange reverse()
  {
    GeneralRange localGeneralRange = this.reverse;
    if (localGeneralRange == null)
    {
      localGeneralRange = new GeneralRange(Ordering.from(this.comparator).reverse(), this.hasUpperBound, getUpperEndpoint(), getUpperBoundType(), this.hasLowerBound, getLowerEndpoint(), getLowerBoundType());
      localGeneralRange.reverse = this;
      return this.reverse = localGeneralRange;
    }
    return localGeneralRange;
  }
  
  public String toString()
  {
    return this.comparator + ":" + (this.lowerBoundType == BoundType.CLOSED ? '[' : '(') + (this.hasLowerBound ? this.lowerEndpoint : "-∞") + ',' + (this.hasUpperBound ? this.upperEndpoint : "∞") + (this.upperBoundType == BoundType.CLOSED ? ']' : ')');
  }
  
  Object getLowerEndpoint()
  {
    return this.lowerEndpoint;
  }
  
  BoundType getLowerBoundType()
  {
    return this.lowerBoundType;
  }
  
  Object getUpperEndpoint()
  {
    return this.upperEndpoint;
  }
  
  BoundType getUpperBoundType()
  {
    return this.upperBoundType;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\GeneralRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */