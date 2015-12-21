package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible
public abstract class Equivalence
{
  public final boolean equivalent(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return false;
    }
    return doEquivalent(paramObject1, paramObject2);
  }
  
  protected abstract boolean doEquivalent(Object paramObject1, Object paramObject2);
  
  public final int hash(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    return doHash(paramObject);
  }
  
  protected abstract int doHash(Object paramObject);
  
  public final Equivalence onResultOf(Function paramFunction)
  {
    return new FunctionalEquivalence(paramFunction, this);
  }
  
  public final Wrapper wrap(Object paramObject)
  {
    return new Wrapper(this, paramObject, null);
  }
  
  @GwtCompatible(serializable=true)
  public final Equivalence pairwise()
  {
    return new PairwiseEquivalence(this);
  }
  
  @Beta
  public final Predicate equivalentTo(Object paramObject)
  {
    return new EquivalentToPredicate(this, paramObject);
  }
  
  public static Equivalence equals()
  {
    return Equals.INSTANCE;
  }
  
  public static Equivalence identity()
  {
    return Identity.INSTANCE;
  }
  
  static final class Identity
    extends Equivalence
    implements Serializable
  {
    static final Identity INSTANCE = new Identity();
    private static final long serialVersionUID = 1L;
    
    protected boolean doEquivalent(Object paramObject1, Object paramObject2)
    {
      return false;
    }
    
    protected int doHash(Object paramObject)
    {
      return System.identityHashCode(paramObject);
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
  }
  
  static final class Equals
    extends Equivalence
    implements Serializable
  {
    static final Equals INSTANCE = new Equals();
    private static final long serialVersionUID = 1L;
    
    protected boolean doEquivalent(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    public int doHash(Object paramObject)
    {
      return paramObject.hashCode();
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
  }
  
  private static final class EquivalentToPredicate
    implements Predicate, Serializable
  {
    private final Equivalence equivalence;
    private final Object target;
    private static final long serialVersionUID = 0L;
    
    EquivalentToPredicate(Equivalence paramEquivalence, Object paramObject)
    {
      this.equivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
      this.target = paramObject;
    }
    
    public boolean apply(Object paramObject)
    {
      return this.equivalence.equivalent(paramObject, this.target);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if ((paramObject instanceof EquivalentToPredicate))
      {
        EquivalentToPredicate localEquivalentToPredicate = (EquivalentToPredicate)paramObject;
        return (this.equivalence.equals(localEquivalentToPredicate.equivalence)) && (Objects.equal(this.target, localEquivalentToPredicate.target));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.equivalence, this.target });
    }
    
    public String toString()
    {
      return this.equivalence + ".equivalentTo(" + this.target + ")";
    }
  }
  
  public static final class Wrapper
    implements Serializable
  {
    private final Equivalence equivalence;
    private final Object reference;
    private static final long serialVersionUID = 0L;
    
    private Wrapper(Equivalence paramEquivalence, Object paramObject)
    {
      this.equivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
      this.reference = paramObject;
    }
    
    public Object get()
    {
      return this.reference;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof Wrapper))
      {
        Wrapper localWrapper = (Wrapper)paramObject;
        Equivalence localEquivalence = this.equivalence;
        return (localEquivalence.equals(localWrapper.equivalence)) && (localEquivalence.equivalent(this.reference, localWrapper.reference));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.equivalence.hash(this.reference);
    }
    
    public String toString()
    {
      return this.equivalence + ".wrap(" + this.reference + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Equivalence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */