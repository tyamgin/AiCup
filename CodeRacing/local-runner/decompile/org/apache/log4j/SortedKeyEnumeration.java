package org.apache.log4j;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class SortedKeyEnumeration
  implements Enumeration
{
  private Enumeration e;
  
  public SortedKeyEnumeration(Hashtable paramHashtable)
  {
    Enumeration localEnumeration = paramHashtable.keys();
    Vector localVector = new Vector(paramHashtable.size());
    for (int j = 0; localEnumeration.hasMoreElements(); j++)
    {
      String str1 = (String)localEnumeration.nextElement();
      for (int i = 0; i < j; i++)
      {
        String str2 = (String)localVector.get(i);
        if (str1.compareTo(str2) <= 0) {
          break;
        }
      }
      localVector.add(i, str1);
    }
    this.e = localVector.elements();
  }
  
  public boolean hasMoreElements()
  {
    return this.e.hasMoreElements();
  }
  
  public Object nextElement()
  {
    return this.e.nextElement();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\SortedKeyEnumeration.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */