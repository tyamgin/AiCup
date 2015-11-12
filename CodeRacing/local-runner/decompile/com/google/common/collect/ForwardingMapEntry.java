package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;

@GwtCompatible
public abstract class ForwardingMapEntry
  extends ForwardingObject
  implements Map.Entry
{
  protected abstract Map.Entry delegate();
  
  public Object getKey()
  {
    return delegate().getKey();
  }
  
  public Object getValue()
  {
    return delegate().getValue();
  }
  
  public Object setValue(Object paramObject)
  {
    return delegate().setValue(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    return delegate().equals(paramObject);
  }
  
  public int hashCode()
  {
    return delegate().hashCode();
  }
  
  protected boolean standardEquals(Object paramObject)
  {
    if ((paramObject instanceof Map.Entry))
    {
      Map.Entry localEntry = (Map.Entry)paramObject;
      return (Objects.equal(getKey(), localEntry.getKey())) && (Objects.equal(getValue(), localEntry.getValue()));
    }
    return false;
  }
  
  protected int standardHashCode()
  {
    Object localObject1 = getKey();
    Object localObject2 = getValue();
    return (localObject1 == null ? 0 : localObject1.hashCode()) ^ (localObject2 == null ? 0 : localObject2.hashCode());
  }
  
  @Beta
  protected String standardToString()
  {
    return getKey() + "=" + getValue();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingMapEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */