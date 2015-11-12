package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract interface Table
{
  public abstract boolean contains(Object paramObject1, Object paramObject2);
  
  public abstract boolean containsRow(Object paramObject);
  
  public abstract boolean containsColumn(Object paramObject);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract Object get(Object paramObject1, Object paramObject2);
  
  public abstract boolean isEmpty();
  
  public abstract int size();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract void clear();
  
  public abstract Object put(Object paramObject1, Object paramObject2, Object paramObject3);
  
  public abstract void putAll(Table paramTable);
  
  public abstract Object remove(Object paramObject1, Object paramObject2);
  
  public abstract Map row(Object paramObject);
  
  public abstract Map column(Object paramObject);
  
  public abstract Set cellSet();
  
  public abstract Set rowKeySet();
  
  public abstract Set columnKeySet();
  
  public abstract Collection values();
  
  public abstract Map rowMap();
  
  public abstract Map columnMap();
  
  public static abstract interface Cell
  {
    public abstract Object getRowKey();
    
    public abstract Object getColumnKey();
    
    public abstract Object getValue();
    
    public abstract boolean equals(Object paramObject);
    
    public abstract int hashCode();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Table.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */