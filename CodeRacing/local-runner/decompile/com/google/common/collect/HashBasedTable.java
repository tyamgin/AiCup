package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@GwtCompatible(serializable=true)
public class HashBasedTable
  extends StandardTable
{
  private static final long serialVersionUID = 0L;
  
  public static HashBasedTable create()
  {
    return new HashBasedTable(new HashMap(), new Factory(0));
  }
  
  public static HashBasedTable create(int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt2 >= 0);
    HashMap localHashMap = Maps.newHashMapWithExpectedSize(paramInt1);
    return new HashBasedTable(localHashMap, new Factory(paramInt2));
  }
  
  public static HashBasedTable create(Table paramTable)
  {
    HashBasedTable localHashBasedTable = create();
    localHashBasedTable.putAll(paramTable);
    return localHashBasedTable;
  }
  
  HashBasedTable(Map paramMap, Factory paramFactory)
  {
    super(paramMap, paramFactory);
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    return super.contains(paramObject1, paramObject2);
  }
  
  public boolean containsColumn(Object paramObject)
  {
    return super.containsColumn(paramObject);
  }
  
  public boolean containsRow(Object paramObject)
  {
    return super.containsRow(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return super.containsValue(paramObject);
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    return super.get(paramObject1, paramObject2);
  }
  
  public boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public Object remove(Object paramObject1, Object paramObject2)
  {
    return super.remove(paramObject1, paramObject2);
  }
  
  private static class Factory
    implements Supplier, Serializable
  {
    final int expectedSize;
    private static final long serialVersionUID = 0L;
    
    Factory(int paramInt)
    {
      this.expectedSize = paramInt;
    }
    
    public Map get()
    {
      return Maps.newHashMapWithExpectedSize(this.expectedSize);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\HashBasedTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */