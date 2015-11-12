package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;
import com.codeforces.commons.text.StringUtil;
import com.google.gson.annotations.Until;

public class o
{
  private final long id;
  @Until(1.0D)
  private final boolean me;
  @Until(1.0D)
  private final String name;
  private final boolean strategyCrashed;
  private final int score;
  
  public o(@Name("id") long paramLong, @Name("me") boolean paramBoolean1, @Name("name") String paramString, @Name("strategyCrashed") boolean paramBoolean2, @Name("score") int paramInt)
  {
    this.id = paramLong;
    this.me = paramBoolean1;
    this.name = paramString;
    this.strategyCrashed = paramBoolean2;
    this.score = paramInt;
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public boolean isMe()
  {
    return this.me;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public boolean isStrategyCrashed()
  {
    return this.strategyCrashed;
  }
  
  public int getScore()
  {
    return this.score;
  }
  
  public static boolean areFieldEquals(o paramo1, o paramo2)
  {
    return (paramo1 == paramo2) || ((paramo1 != null) && (paramo2 != null) && (paramo1.id == paramo2.id) && (paramo1.me == paramo2.me) && (StringUtil.equals(paramo1.name, paramo2.name)) && (paramo1.strategyCrashed == paramo2.strategyCrashed) && (paramo1.score == paramo2.score));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\o.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */