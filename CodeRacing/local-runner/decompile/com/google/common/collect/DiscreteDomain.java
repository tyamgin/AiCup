package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@GwtCompatible
@Beta
public abstract class DiscreteDomain
{
  public static DiscreteDomain integers()
  {
    return IntegerDomain.INSTANCE;
  }
  
  public static DiscreteDomain longs()
  {
    return LongDomain.INSTANCE;
  }
  
  static DiscreteDomain bigIntegers()
  {
    return BigIntegerDomain.INSTANCE;
  }
  
  public abstract Comparable next(Comparable paramComparable);
  
  public abstract Comparable previous(Comparable paramComparable);
  
  public abstract long distance(Comparable paramComparable1, Comparable paramComparable2);
  
  public Comparable minValue()
  {
    throw new NoSuchElementException();
  }
  
  public Comparable maxValue()
  {
    throw new NoSuchElementException();
  }
  
  private static final class BigIntegerDomain
    extends DiscreteDomain
    implements Serializable
  {
    private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final long serialVersionUID = 0L;
    
    public BigInteger next(BigInteger paramBigInteger)
    {
      return paramBigInteger.add(BigInteger.ONE);
    }
    
    public BigInteger previous(BigInteger paramBigInteger)
    {
      return paramBigInteger.subtract(BigInteger.ONE);
    }
    
    public long distance(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
    {
      return paramBigInteger2.subtract(paramBigInteger1).max(MIN_LONG).min(MAX_LONG).longValue();
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
    
    public String toString()
    {
      return "DiscreteDomains.bigIntegers()";
    }
  }
  
  private static final class LongDomain
    extends DiscreteDomain
    implements Serializable
  {
    private static final LongDomain INSTANCE = new LongDomain();
    private static final long serialVersionUID = 0L;
    
    public Long next(Long paramLong)
    {
      long l = paramLong.longValue();
      return l == Long.MAX_VALUE ? null : Long.valueOf(l + 1L);
    }
    
    public Long previous(Long paramLong)
    {
      long l = paramLong.longValue();
      return l == Long.MIN_VALUE ? null : Long.valueOf(l - 1L);
    }
    
    public long distance(Long paramLong1, Long paramLong2)
    {
      long l = paramLong2.longValue() - paramLong1.longValue();
      if ((paramLong2.longValue() > paramLong1.longValue()) && (l < 0L)) {
        return Long.MAX_VALUE;
      }
      if ((paramLong2.longValue() < paramLong1.longValue()) && (l > 0L)) {
        return Long.MIN_VALUE;
      }
      return l;
    }
    
    public Long minValue()
    {
      return Long.valueOf(Long.MIN_VALUE);
    }
    
    public Long maxValue()
    {
      return Long.valueOf(Long.MAX_VALUE);
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
    
    public String toString()
    {
      return "DiscreteDomains.longs()";
    }
  }
  
  private static final class IntegerDomain
    extends DiscreteDomain
    implements Serializable
  {
    private static final IntegerDomain INSTANCE = new IntegerDomain();
    private static final long serialVersionUID = 0L;
    
    public Integer next(Integer paramInteger)
    {
      int i = paramInteger.intValue();
      return i == Integer.MAX_VALUE ? null : Integer.valueOf(i + 1);
    }
    
    public Integer previous(Integer paramInteger)
    {
      int i = paramInteger.intValue();
      return i == Integer.MIN_VALUE ? null : Integer.valueOf(i - 1);
    }
    
    public long distance(Integer paramInteger1, Integer paramInteger2)
    {
      return paramInteger2.intValue() - paramInteger1.intValue();
    }
    
    public Integer minValue()
    {
      return Integer.valueOf(Integer.MIN_VALUE);
    }
    
    public Integer maxValue()
    {
      return Integer.valueOf(Integer.MAX_VALUE);
    }
    
    private Object readResolve()
    {
      return INSTANCE;
    }
    
    public String toString()
    {
      return "DiscreteDomains.integers()";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\DiscreteDomain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */