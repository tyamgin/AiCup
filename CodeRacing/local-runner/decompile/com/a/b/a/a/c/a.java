package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;
import com.google.gson.annotations.Until;

public class a
  extends s
{
  @Until(1.0D)
  private final b type;
  
  public a(@Name("id") long paramLong, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7, @Name("width") double paramDouble8, @Name("height") double paramDouble9, @Name("type") b paramb)
  {
    super(paramLong, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9);
    this.type = paramb;
  }
  
  public b getType()
  {
    return this.type;
  }
  
  public static boolean areFieldEquals(a parama1, a parama2)
  {
    return (parama1 == parama2) || ((parama1 != null) && (parama2 != null) && (s.areFieldEquals(parama1, parama2)) && (parama1.type == parama2.type));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */