package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
final class WellBehavedMap
  extends ForwardingMap
{
  private final Map delegate;
  private Set entrySet;
  
  private WellBehavedMap(Map paramMap)
  {
    this.delegate = paramMap;
  }
  
  static WellBehavedMap wrap(Map paramMap)
  {
    return new WellBehavedMap(paramMap);
  }
  
  protected Map delegate()
  {
    return this.delegate;
  }
  
  public Set entrySet()
  {
    Set localSet = this.entrySet;
    if (localSet != null) {
      return localSet;
    }
    return this.entrySet = new EntrySet(null);
  }
  
  private final class EntrySet
    extends Maps.EntrySet
  {
    private EntrySet() {}
    
    Map map()
    {
      return WellBehavedMap.this;
    }
    
    public Iterator iterator()
    {
      new TransformedIterator(WellBehavedMap.this.keySet().iterator())
      {
        Map.Entry transform(final Object paramAnonymousObject)
        {
          new AbstractMapEntry()
          {
            public Object getKey()
            {
              return paramAnonymousObject;
            }
            
            public Object getValue()
            {
              return WellBehavedMap.this.get(paramAnonymousObject);
            }
            
            public Object setValue(Object paramAnonymous2Object)
            {
              return WellBehavedMap.this.put(paramAnonymousObject, paramAnonymous2Object);
            }
          };
        }
      };
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\WellBehavedMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */