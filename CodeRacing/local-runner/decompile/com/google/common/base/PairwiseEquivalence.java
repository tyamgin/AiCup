package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(serializable=true)
final class PairwiseEquivalence
  extends Equivalence
  implements Serializable
{
  final Equivalence elementEquivalence;
  private static final long serialVersionUID = 1L;
  
  PairwiseEquivalence(Equivalence paramEquivalence)
  {
    this.elementEquivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
  }
  
  protected boolean doEquivalent(Iterable paramIterable1, Iterable paramIterable2)
  {
    Iterator localIterator1 = paramIterable1.iterator();
    Iterator localIterator2 = paramIterable2.iterator();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext())) {
      if (!this.elementEquivalence.equivalent(localIterator1.next(), localIterator2.next())) {
        return false;
      }
    }
    return (!localIterator1.hasNext()) && (!localIterator2.hasNext());
  }
  
  protected int doHash(Iterable paramIterable)
  {
    int i = 78721;
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      i = i * 24943 + this.elementEquivalence.hash(localObject);
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PairwiseEquivalence))
    {
      PairwiseEquivalence localPairwiseEquivalence = (PairwiseEquivalence)paramObject;
      return this.elementEquivalence.equals(localPairwiseEquivalence.elementEquivalence);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.elementEquivalence.hashCode() ^ 0x46A3EB07;
  }
  
  public String toString()
  {
    return this.elementEquivalence + ".pairwise()";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\PairwiseEquivalence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */