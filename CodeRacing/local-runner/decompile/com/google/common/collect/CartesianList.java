package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@GwtCompatible
final class CartesianList
  extends AbstractList
{
  private final transient ImmutableList axes;
  private final transient int[] axesSizeProduct;
  
  static List create(List paramList)
  {
    ImmutableList.Builder localBuilder = new ImmutableList.Builder(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      List localList = (List)localIterator.next();
      ImmutableList localImmutableList = ImmutableList.copyOf(localList);
      if (localImmutableList.isEmpty()) {
        return ImmutableList.of();
      }
      localBuilder.add(localImmutableList);
    }
    return new CartesianList(localBuilder.build());
  }
  
  CartesianList(ImmutableList paramImmutableList)
  {
    this.axes = paramImmutableList;
    int[] arrayOfInt = new int[paramImmutableList.size() + 1];
    arrayOfInt[paramImmutableList.size()] = 1;
    try
    {
      for (int i = paramImmutableList.size() - 1; i >= 0; i--) {
        arrayOfInt[i] = IntMath.checkedMultiply(arrayOfInt[(i + 1)], ((List)paramImmutableList.get(i)).size());
      }
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new IllegalArgumentException("Cartesian product too large; must have size at most Integer.MAX_VALUE");
    }
    this.axesSizeProduct = arrayOfInt;
  }
  
  private int getAxisIndexForProductIndex(int paramInt1, int paramInt2)
  {
    return paramInt1 / this.axesSizeProduct[(paramInt2 + 1)] % ((List)this.axes.get(paramInt2)).size();
  }
  
  public ImmutableList get(final int paramInt)
  {
    Preconditions.checkElementIndex(paramInt, size());
    new ImmutableList()
    {
      public int size()
      {
        return CartesianList.this.axes.size();
      }
      
      public Object get(int paramAnonymousInt)
      {
        Preconditions.checkElementIndex(paramAnonymousInt, size());
        int i = CartesianList.this.getAxisIndexForProductIndex(paramInt, paramAnonymousInt);
        return ((List)CartesianList.this.axes.get(paramAnonymousInt)).get(i);
      }
      
      boolean isPartialView()
      {
        return true;
      }
    };
  }
  
  public int size()
  {
    return this.axesSizeProduct[0];
  }
  
  public boolean contains(Object paramObject)
  {
    if (!(paramObject instanceof List)) {
      return false;
    }
    List localList = (List)paramObject;
    if (localList.size() != this.axes.size()) {
      return false;
    }
    ListIterator localListIterator = localList.listIterator();
    while (localListIterator.hasNext())
    {
      int i = localListIterator.nextIndex();
      if (!((List)this.axes.get(i)).contains(localListIterator.next())) {
        return false;
      }
    }
    return true;
  }
  
  public int indexOf(Object paramObject)
  {
    if (!(paramObject instanceof List)) {
      return -1;
    }
    List localList1 = (List)paramObject;
    if (localList1.size() != this.axes.size()) {
      return -1;
    }
    Iterator localIterator1 = localList1.iterator();
    int i = 0;
    Iterator localIterator2 = this.axes.iterator();
    while (localIterator2.hasNext())
    {
      List localList2 = (List)localIterator2.next();
      Object localObject = localIterator1.next();
      int j = localList2.indexOf(localObject);
      if (j == -1) {
        return -1;
      }
      i = i * localList2.size() + j;
    }
    return i;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    if (!(paramObject instanceof List)) {
      return -1;
    }
    List localList1 = (List)paramObject;
    if (localList1.size() != this.axes.size()) {
      return -1;
    }
    Iterator localIterator1 = localList1.iterator();
    int i = 0;
    Iterator localIterator2 = this.axes.iterator();
    while (localIterator2.hasNext())
    {
      List localList2 = (List)localIterator2.next();
      Object localObject = localIterator1.next();
      int j = localList2.lastIndexOf(localObject);
      if (j == -1) {
        return -1;
      }
      i = i * localList2.size() + j;
    }
    return i;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\CartesianList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */