package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@Beta
@GwtCompatible
final class FunctionalEquivalence
  extends Equivalence
  implements Serializable
{
  private static final long serialVersionUID = 0L;
  private final Function function;
  private final Equivalence resultEquivalence;
  
  FunctionalEquivalence(Function paramFunction, Equivalence paramEquivalence)
  {
    this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    this.resultEquivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
  }
  
  protected boolean doEquivalent(Object paramObject1, Object paramObject2)
  {
    return this.resultEquivalence.equivalent(this.function.apply(paramObject1), this.function.apply(paramObject2));
  }
  
  protected int doHash(Object paramObject)
  {
    return this.resultEquivalence.hash(this.function.apply(paramObject));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof FunctionalEquivalence))
    {
      FunctionalEquivalence localFunctionalEquivalence = (FunctionalEquivalence)paramObject;
      return (this.function.equals(localFunctionalEquivalence.function)) && (this.resultEquivalence.equals(localFunctionalEquivalence.resultEquivalence));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.function, this.resultEquivalence });
  }
  
  public String toString()
  {
    return this.resultEquivalence + ".onResultOf(" + this.function + ")";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\FunctionalEquivalence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */