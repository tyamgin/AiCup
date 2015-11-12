package org.apache.commons.math3.exception.util;

import java.util.ArrayList;
import java.util.List;

public class ArgUtils
{
  public static Object[] flatten(Object[] paramArrayOfObject)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramArrayOfObject != null) {
      for (Object localObject1 : paramArrayOfObject) {
        if ((localObject1 instanceof Object[])) {
          for (Object localObject2 : flatten((Object[])localObject1)) {
            localArrayList.add(localObject2);
          }
        } else {
          localArrayList.add(localObject1);
        }
      }
    }
    return localArrayList.toArray();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\exception\util\ArgUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */