package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@GwtCompatible
public abstract class ImmutableTable
  implements Table
{
  public static final ImmutableTable of()
  {
    return EmptyImmutableTable.INSTANCE;
  }
  
  public static final ImmutableTable of(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return new SingletonImmutableTable(paramObject1, paramObject2, paramObject3);
  }
  
  public static final ImmutableTable copyOf(Table paramTable)
  {
    if ((paramTable instanceof ImmutableTable))
    {
      ImmutableTable localImmutableTable = (ImmutableTable)paramTable;
      return localImmutableTable;
    }
    int i = paramTable.size();
    switch (i)
    {
    case 0: 
      return of();
    case 1: 
      Table.Cell localCell1 = (Table.Cell)Iterables.getOnlyElement(paramTable.cellSet());
      return of(localCell1.getRowKey(), localCell1.getColumnKey(), localCell1.getValue());
    }
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    Iterator localIterator = paramTable.cellSet().iterator();
    while (localIterator.hasNext())
    {
      Table.Cell localCell2 = (Table.Cell)localIterator.next();
      localBuilder.add(cellOf(localCell2.getRowKey(), localCell2.getColumnKey(), localCell2.getValue()));
    }
    return RegularImmutableTable.forCells(localBuilder.build());
  }
  
  public static final Builder builder()
  {
    return new Builder();
  }
  
  static Table.Cell cellOf(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return Tables.immutableCell(Preconditions.checkNotNull(paramObject1), Preconditions.checkNotNull(paramObject2), Preconditions.checkNotNull(paramObject3));
  }
  
  public abstract ImmutableSet cellSet();
  
  public abstract ImmutableMap column(Object paramObject);
  
  public abstract ImmutableSet columnKeySet();
  
  public abstract ImmutableMap columnMap();
  
  public abstract ImmutableMap row(Object paramObject);
  
  public abstract ImmutableSet rowKeySet();
  
  public abstract ImmutableMap rowMap();
  
  @Deprecated
  public final void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final Object put(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final void putAll(Table paramTable)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final Object remove(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Table))
    {
      Table localTable = (Table)paramObject;
      return cellSet().equals(localTable.cellSet());
    }
    return false;
  }
  
  public int hashCode()
  {
    return cellSet().hashCode();
  }
  
  public String toString()
  {
    return rowMap().toString();
  }
  
  public static final class Builder
  {
    private final List cells = Lists.newArrayList();
    private Comparator rowComparator;
    private Comparator columnComparator;
    
    public Builder orderRowsBy(Comparator paramComparator)
    {
      this.rowComparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
      return this;
    }
    
    public Builder orderColumnsBy(Comparator paramComparator)
    {
      this.columnComparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
      return this;
    }
    
    public Builder put(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      this.cells.add(ImmutableTable.cellOf(paramObject1, paramObject2, paramObject3));
      return this;
    }
    
    public Builder put(Table.Cell paramCell)
    {
      if ((paramCell instanceof Tables.ImmutableCell))
      {
        Preconditions.checkNotNull(paramCell.getRowKey());
        Preconditions.checkNotNull(paramCell.getColumnKey());
        Preconditions.checkNotNull(paramCell.getValue());
        Table.Cell localCell = paramCell;
        this.cells.add(localCell);
      }
      else
      {
        put(paramCell.getRowKey(), paramCell.getColumnKey(), paramCell.getValue());
      }
      return this;
    }
    
    public Builder putAll(Table paramTable)
    {
      Iterator localIterator = paramTable.cellSet().iterator();
      while (localIterator.hasNext())
      {
        Table.Cell localCell = (Table.Cell)localIterator.next();
        put(localCell);
      }
      return this;
    }
    
    public ImmutableTable build()
    {
      int i = this.cells.size();
      switch (i)
      {
      case 0: 
        return ImmutableTable.of();
      case 1: 
        return new SingletonImmutableTable((Table.Cell)Iterables.getOnlyElement(this.cells));
      }
      return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */