package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(emulated=true)
abstract class AbstractBiMap
  extends ForwardingMap
  implements BiMap, Serializable
{
  private transient Map delegate;
  transient AbstractBiMap inverse;
  private transient Set keySet;
  private transient Set valueSet;
  private transient Set entrySet;
  @GwtIncompatible("Not needed in emulated source.")
  private static final long serialVersionUID = 0L;
  
  AbstractBiMap(Map paramMap1, Map paramMap2)
  {
    setDelegates(paramMap1, paramMap2);
  }
  
  private AbstractBiMap(Map paramMap, AbstractBiMap paramAbstractBiMap)
  {
    this.delegate = paramMap;
    this.inverse = paramAbstractBiMap;
  }
  
  protected Map delegate()
  {
    return this.delegate;
  }
  
  Object checkKey(Object paramObject)
  {
    return paramObject;
  }
  
  Object checkValue(Object paramObject)
  {
    return paramObject;
  }
  
  void setDelegates(Map paramMap1, Map paramMap2)
  {
    Preconditions.checkState(this.delegate == null);
    Preconditions.checkState(this.inverse == null);
    Preconditions.checkArgument(paramMap1.isEmpty());
    Preconditions.checkArgument(paramMap2.isEmpty());
    Preconditions.checkArgument(paramMap1 != paramMap2);
    this.delegate = paramMap1;
    this.inverse = new Inverse(paramMap2, this, null);
  }
  
  void setInverse(AbstractBiMap paramAbstractBiMap)
  {
    this.inverse = paramAbstractBiMap;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return this.inverse.containsKey(paramObject);
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    return putInBothMaps(paramObject1, paramObject2, false);
  }
  
  public Object forcePut(Object paramObject1, Object paramObject2)
  {
    return putInBothMaps(paramObject1, paramObject2, true);
  }
  
  private Object putInBothMaps(Object paramObject1, Object paramObject2, boolean paramBoolean)
  {
    checkKey(paramObject1);
    checkValue(paramObject2);
    boolean bool = containsKey(paramObject1);
    if ((bool) && (Objects.equal(paramObject2, get(paramObject1)))) {
      return paramObject2;
    }
    if (paramBoolean) {
      inverse().remove(paramObject2);
    } else {
      Preconditions.checkArgument(!containsValue(paramObject2), "value already present: %s", new Object[] { paramObject2 });
    }
    Object localObject = this.delegate.put(paramObject1, paramObject2);
    updateInverseMap(paramObject1, bool, localObject, paramObject2);
    return localObject;
  }
  
  private void updateInverseMap(Object paramObject1, boolean paramBoolean, Object paramObject2, Object paramObject3)
  {
    if (paramBoolean) {
      removeFromInverseMap(paramObject2);
    }
    this.inverse.delegate.put(paramObject3, paramObject1);
  }
  
  public Object remove(Object paramObject)
  {
    return containsKey(paramObject) ? removeFromBothMaps(paramObject) : null;
  }
  
  private Object removeFromBothMaps(Object paramObject)
  {
    Object localObject = this.delegate.remove(paramObject);
    removeFromInverseMap(localObject);
    return localObject;
  }
  
  private void removeFromInverseMap(Object paramObject)
  {
    this.inverse.delegate.remove(paramObject);
  }
  
  public void putAll(Map paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public void clear()
  {
    this.delegate.clear();
    this.inverse.delegate.clear();
  }
  
  public BiMap inverse()
  {
    return this.inverse;
  }
  
  public Set keySet()
  {
    Set localSet = this.keySet;
    return localSet == null ? (this.keySet = new KeySet(null)) : localSet;
  }
  
  public Set values()
  {
    Set localSet = this.valueSet;
    return localSet == null ? (this.valueSet = new ValueSet(null)) : localSet;
  }
  
  public Set entrySet()
  {
    Set localSet = this.entrySet;
    return localSet == null ? (this.entrySet = new EntrySet(null)) : localSet;
  }
  
  private static class Inverse
    extends AbstractBiMap
  {
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;
    
    private Inverse(Map paramMap, AbstractBiMap paramAbstractBiMap)
    {
      super(paramAbstractBiMap, null);
    }
    
    Object checkKey(Object paramObject)
    {
      return this.inverse.checkValue(paramObject);
    }
    
    Object checkValue(Object paramObject)
    {
      return this.inverse.checkKey(paramObject);
    }
    
    @GwtIncompatible("java.io.ObjectOuputStream")
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(inverse());
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      setInverse((AbstractBiMap)paramObjectInputStream.readObject());
    }
    
    @GwtIncompatible("Not needed in the emulated source.")
    Object readResolve()
    {
      return inverse().inverse();
    }
  }
  
  private class EntrySet
    extends ForwardingSet
  {
    final Set esDelegate = AbstractBiMap.this.delegate.entrySet();
    
    private EntrySet() {}
    
    protected Set delegate()
    {
      return this.esDelegate;
    }
    
    public void clear()
    {
      AbstractBiMap.this.clear();
    }
    
    public boolean remove(Object paramObject)
    {
      if (!this.esDelegate.contains(paramObject)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      AbstractBiMap.this.inverse.delegate.remove(localEntry.getValue());
      this.esDelegate.remove(localEntry);
      return true;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.esDelegate.iterator();
      new Iterator()
      {
        Map.Entry entry;
        
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public Map.Entry next()
        {
          this.entry = ((Map.Entry)localIterator.next());
          final Map.Entry localEntry = this.entry;
          new ForwardingMapEntry()
          {
            protected Map.Entry delegate()
            {
              return localEntry;
            }
            
            public Object setValue(Object paramAnonymous2Object)
            {
              Preconditions.checkState(AbstractBiMap.EntrySet.this.contains(this), "entry no longer in map");
              if (Objects.equal(paramAnonymous2Object, getValue())) {
                return paramAnonymous2Object;
              }
              Preconditions.checkArgument(!AbstractBiMap.this.containsValue(paramAnonymous2Object), "value already present: %s", new Object[] { paramAnonymous2Object });
              Object localObject = localEntry.setValue(paramAnonymous2Object);
              Preconditions.checkState(Objects.equal(paramAnonymous2Object, AbstractBiMap.this.get(getKey())), "entry no longer in map");
              AbstractBiMap.this.updateInverseMap(getKey(), true, localObject, paramAnonymous2Object);
              return localObject;
            }
          };
        }
        
        public void remove()
        {
          Preconditions.checkState(this.entry != null);
          Object localObject = this.entry.getValue();
          localIterator.remove();
          AbstractBiMap.this.removeFromInverseMap(localObject);
        }
      };
    }
    
    public Object[] toArray()
    {
      return standardToArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return standardToArray(paramArrayOfObject);
    }
    
    public boolean contains(Object paramObject)
    {
      return Maps.containsEntryImpl(delegate(), paramObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return standardContainsAll(paramCollection);
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return standardRemoveAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return standardRetainAll(paramCollection);
    }
  }
  
  private class ValueSet
    extends ForwardingSet
  {
    final Set valuesDelegate = AbstractBiMap.this.inverse.keySet();
    
    private ValueSet() {}
    
    protected Set delegate()
    {
      return this.valuesDelegate;
    }
    
    public Iterator iterator()
    {
      return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
    }
    
    public Object[] toArray()
    {
      return standardToArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return standardToArray(paramArrayOfObject);
    }
    
    public String toString()
    {
      return standardToString();
    }
  }
  
  private class KeySet
    extends ForwardingSet
  {
    private KeySet() {}
    
    protected Set delegate()
    {
      return AbstractBiMap.this.delegate.keySet();
    }
    
    public void clear()
    {
      AbstractBiMap.this.clear();
    }
    
    public boolean remove(Object paramObject)
    {
      if (!contains(paramObject)) {
        return false;
      }
      AbstractBiMap.this.removeFromBothMaps(paramObject);
      return true;
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return standardRemoveAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return standardRetainAll(paramCollection);
    }
    
    public Iterator iterator()
    {
      return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */