package com.codeforces.commons.collection;

import java.util.Collection;

public class CollectionUtil
{
  public static boolean addAll(Collection paramCollection, Object[] paramArrayOfObject)
  {
    boolean bool = false;
    int i = 0;
    int j = paramArrayOfObject.length;
    while (i < j)
    {
      bool |= paramCollection.add(paramArrayOfObject[i]);
      i++;
    }
    return bool;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\collection\CollectionUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */