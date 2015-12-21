package com.codeforces.commons.holder;

public abstract class Mutable
  implements Readable, Writable
{
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof Mutable)) && (get() == null ? ((Readable)paramObject).get() == null : get().equals(((Readable)paramObject).get())));
  }
  
  public int hashCode()
  {
    return get() == null ? 0 : get().hashCode();
  }
  
  public String toString()
  {
    return String.valueOf(get());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\holder\Mutable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */