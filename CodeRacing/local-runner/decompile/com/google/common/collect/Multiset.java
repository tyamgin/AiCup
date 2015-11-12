package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@GwtCompatible
public abstract interface Multiset
  extends Collection
{
  public abstract int count(Object paramObject);
  
  public abstract int add(Object paramObject, int paramInt);
  
  public abstract int remove(Object paramObject, int paramInt);
  
  public abstract int setCount(Object paramObject, int paramInt);
  
  public abstract boolean setCount(Object paramObject, int paramInt1, int paramInt2);
  
  public abstract Set elementSet();
  
  public abstract Set entrySet();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
  
  public abstract Iterator iterator();
  
  public abstract boolean contains(Object paramObject);
  
  public abstract boolean containsAll(Collection paramCollection);
  
  public abstract boolean add(Object paramObject);
  
  public abstract boolean remove(Object paramObject);
  
  public abstract boolean removeAll(Collection paramCollection);
  
  public abstract boolean retainAll(Collection paramCollection);
  
  public static abstract interface Entry
  {
    public abstract Object getElement();
    
    public abstract int getCount();
    
    public abstract boolean equals(Object paramObject);
    
    public abstract int hashCode();
    
    public abstract String toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Multiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */