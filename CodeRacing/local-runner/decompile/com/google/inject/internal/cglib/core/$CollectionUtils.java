package com.google.inject.internal.cglib.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class $CollectionUtils
{
  public static Map bucket(Collection paramCollection, .Transformer paramTransformer)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = localIterator.next();
      Object localObject2 = paramTransformer.transform(localObject1);
      Object localObject3 = (List)localHashMap.get(localObject2);
      if (localObject3 == null) {
        localHashMap.put(localObject2, localObject3 = new LinkedList());
      }
      ((List)localObject3).add(localObject1);
    }
    return localHashMap;
  }
  
  public static void reverse(Map paramMap1, Map paramMap2)
  {
    Iterator localIterator = paramMap1.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramMap2.put(paramMap1.get(localObject), localObject);
    }
  }
  
  public static Collection filter(Collection paramCollection, .Predicate paramPredicate)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext()) {
      if (!paramPredicate.evaluate(localIterator.next())) {
        localIterator.remove();
      }
    }
    return paramCollection;
  }
  
  public static List transform(Collection paramCollection, .Transformer paramTransformer)
  {
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext()) {
      localArrayList.add(paramTransformer.transform(localIterator.next()));
    }
    return localArrayList;
  }
  
  public static Map getIndexMap(List paramList)
  {
    HashMap localHashMap = new HashMap();
    int i = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext()) {
      localHashMap.put(localIterator.next(), new Integer(i++));
    }
    return localHashMap;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$CollectionUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */