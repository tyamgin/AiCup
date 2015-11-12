package com.codeforces.commons.pair;

public class Pair
  extends SimplePair
  implements Comparable
{
  public Pair() {}
  
  public Pair(Comparable paramComparable1, Comparable paramComparable2)
  {
    super(paramComparable1, paramComparable2);
  }
  
  public Pair(SimplePair paramSimplePair)
  {
    super(paramSimplePair);
  }
  
  public int compareTo(Pair paramPair)
  {
    int i;
    if (getFirst() != paramPair.getFirst())
    {
      if (getFirst() == null) {
        return -1;
      }
      if (paramPair.getFirst() == null) {
        return 1;
      }
      i = ((Comparable)getFirst()).compareTo(paramPair.getFirst());
      if (i != 0) {
        return i;
      }
    }
    if (getSecond() != paramPair.getSecond())
    {
      if (getSecond() == null) {
        return -1;
      }
      if (paramPair.getSecond() == null) {
        return 1;
      }
      i = ((Comparable)getSecond()).compareTo(paramPair.getSecond());
      if (i != 0) {
        return i;
      }
    }
    return 0;
  }
  
  public boolean equals(Comparable paramComparable1, Comparable paramComparable2)
  {
    return (getFirst() == null ? paramComparable1 == null : ((Comparable)getFirst()).equals(paramComparable1)) && (getSecond() == null ? paramComparable2 == null : ((Comparable)getSecond()).equals(paramComparable2));
  }
  
  public boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    return toString(this);
  }
  
  public static String toString(Pair paramPair)
  {
    return toString(Pair.class, paramPair);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\pair\Pair.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */