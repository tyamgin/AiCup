package com.google.inject.matcher;

import java.io.Serializable;

public abstract class AbstractMatcher
  implements Matcher
{
  public Matcher and(Matcher paramMatcher)
  {
    return new AndMatcher(this, paramMatcher);
  }
  
  public Matcher or(Matcher paramMatcher)
  {
    return new OrMatcher(this, paramMatcher);
  }
  
  private static class OrMatcher
    extends AbstractMatcher
    implements Serializable
  {
    private final Matcher a;
    private final Matcher b;
    private static final long serialVersionUID = 0L;
    
    public OrMatcher(Matcher paramMatcher1, Matcher paramMatcher2)
    {
      this.a = paramMatcher1;
      this.b = paramMatcher2;
    }
    
    public boolean matches(Object paramObject)
    {
      return (this.a.matches(paramObject)) || (this.b.matches(paramObject));
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof OrMatcher)) && (((OrMatcher)paramObject).a.equals(this.a)) && (((OrMatcher)paramObject).b.equals(this.b));
    }
    
    public int hashCode()
    {
      return 37 * (this.a.hashCode() ^ this.b.hashCode());
    }
    
    public String toString()
    {
      String str1 = String.valueOf(String.valueOf(this.a));
      String str2 = String.valueOf(String.valueOf(this.b));
      return 6 + str1.length() + str2.length() + "or(" + str1 + ", " + str2 + ")";
    }
  }
  
  private static class AndMatcher
    extends AbstractMatcher
    implements Serializable
  {
    private final Matcher a;
    private final Matcher b;
    private static final long serialVersionUID = 0L;
    
    public AndMatcher(Matcher paramMatcher1, Matcher paramMatcher2)
    {
      this.a = paramMatcher1;
      this.b = paramMatcher2;
    }
    
    public boolean matches(Object paramObject)
    {
      return (this.a.matches(paramObject)) && (this.b.matches(paramObject));
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof AndMatcher)) && (((AndMatcher)paramObject).a.equals(this.a)) && (((AndMatcher)paramObject).b.equals(this.b));
    }
    
    public int hashCode()
    {
      return 41 * (this.a.hashCode() ^ this.b.hashCode());
    }
    
    public String toString()
    {
      String str1 = String.valueOf(String.valueOf(this.a));
      String str2 = String.valueOf(String.valueOf(this.b));
      return 7 + str1.length() + str2.length() + "and(" + str1 + ", " + str2 + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\matcher\AbstractMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */