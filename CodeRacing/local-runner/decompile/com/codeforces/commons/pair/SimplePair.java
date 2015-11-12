package com.codeforces.commons.pair;

import com.codeforces.commons.text.StringUtil;

public class SimplePair
{
  private Object first;
  private Object second;
  
  public SimplePair() {}
  
  public SimplePair(Object paramObject1, Object paramObject2)
  {
    this.first = paramObject1;
    this.second = paramObject2;
  }
  
  public SimplePair(SimplePair paramSimplePair)
  {
    this.first = paramSimplePair.first;
    this.second = paramSimplePair.second;
  }
  
  public Object getFirst()
  {
    return this.first;
  }
  
  public void setFirst(Object paramObject)
  {
    this.first = paramObject;
  }
  
  public Object getSecond()
  {
    return this.second;
  }
  
  public void setSecond(Object paramObject)
  {
    this.second = paramObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SimplePair)) {
      return false;
    }
    SimplePair localSimplePair = (SimplePair)paramObject;
    return (this.first == null ? localSimplePair.first == null : this.first.equals(localSimplePair.first)) && (this.second == null ? localSimplePair.second == null : this.second.equals(localSimplePair.second));
  }
  
  public int hashCode()
  {
    int i = this.first == null ? 0 : this.first.hashCode();
    i = 31 * i + (this.second == null ? 0 : this.second.hashCode());
    return i;
  }
  
  public String toString()
  {
    return toString(this);
  }
  
  public static String toString(SimplePair paramSimplePair)
  {
    return toString(SimplePair.class, paramSimplePair);
  }
  
  public static String toString(Class paramClass, SimplePair paramSimplePair)
  {
    return StringUtil.toString(paramClass, paramSimplePair, false, new String[] { "first", "second" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\pair\SimplePair.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */