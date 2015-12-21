package com.codeforces.commons.holder;

public class Holders
{
  public static void setQuietly(Writable paramWritable, Object paramObject)
  {
    if (paramWritable != null) {
      try
      {
        paramWritable.set(paramObject);
      }
      catch (RuntimeException localRuntimeException) {}
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\holder\Holders.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */