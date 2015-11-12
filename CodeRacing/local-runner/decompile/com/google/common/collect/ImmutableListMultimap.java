package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public class ImmutableListMultimap
  extends ImmutableMultimap
  implements ListMultimap
{
  private transient ImmutableListMultimap inverse;
  @GwtIncompatible("Not needed in emulated source")
  private static final long serialVersionUID = 0L;
  
  public static ImmutableListMultimap of()
  {
    return EmptyImmutableListMultimap.INSTANCE;
  }
  
  public static ImmutableListMultimap of(Object paramObject1, Object paramObject2)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    return localBuilder.build();
  }
  
  public static ImmutableListMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    return localBuilder.build();
  }
  
  public static ImmutableListMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    localBuilder.put(paramObject5, paramObject6);
    return localBuilder.build();
  }
  
  public static ImmutableListMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    localBuilder.put(paramObject5, paramObject6);
    localBuilder.put(paramObject7, paramObject8);
    return localBuilder.build();
  }
  
  public static ImmutableListMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    localBuilder.put(paramObject5, paramObject6);
    localBuilder.put(paramObject7, paramObject8);
    localBuilder.put(paramObject9, paramObject10);
    return localBuilder.build();
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static ImmutableListMultimap copyOf(Multimap paramMultimap)
  {
    if (paramMultimap.isEmpty()) {
      return of();
    }
    if ((paramMultimap instanceof ImmutableListMultimap))
    {
      localObject = (ImmutableListMultimap)paramMultimap;
      if (!((ImmutableListMultimap)localObject).isPartialView()) {
        return (ImmutableListMultimap)localObject;
      }
    }
    Object localObject = ImmutableMap.builder();
    int i = 0;
    Iterator localIterator = paramMultimap.asMap().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      ImmutableList localImmutableList = ImmutableList.copyOf((Collection)localEntry.getValue());
      if (!localImmutableList.isEmpty())
      {
        ((ImmutableMap.Builder)localObject).put(localEntry.getKey(), localImmutableList);
        i += localImmutableList.size();
      }
    }
    return new ImmutableListMultimap(((ImmutableMap.Builder)localObject).build(), i);
  }
  
  ImmutableListMultimap(ImmutableMap paramImmutableMap, int paramInt)
  {
    super(paramImmutableMap, paramInt);
  }
  
  public ImmutableList get(Object paramObject)
  {
    ImmutableList localImmutableList = (ImmutableList)this.map.get(paramObject);
    return localImmutableList == null ? ImmutableList.of() : localImmutableList;
  }
  
  public ImmutableListMultimap inverse()
  {
    ImmutableListMultimap localImmutableListMultimap = this.inverse;
    return localImmutableListMultimap == null ? (this.inverse = invert()) : localImmutableListMultimap;
  }
  
  private ImmutableListMultimap invert()
  {
    Builder localBuilder = builder();
    Object localObject = entries().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      localBuilder.put(localEntry.getValue(), localEntry.getKey());
    }
    localObject = localBuilder.build();
    ((ImmutableListMultimap)localObject).inverse = this;
    return (ImmutableListMultimap)localObject;
  }
  
  @Deprecated
  public ImmutableList removeAll(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public ImmutableList replaceValues(Object paramObject, Iterable paramIterable)
  {
    throw new UnsupportedOperationException();
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Serialization.writeMultimap(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i < 0) {
      throw new InvalidObjectException("Invalid key count " + i);
    }
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      Object localObject = paramObjectInputStream.readObject();
      int m = paramObjectInputStream.readInt();
      if (m <= 0) {
        throw new InvalidObjectException("Invalid value count " + m);
      }
      Object[] arrayOfObject = new Object[m];
      for (int n = 0; n < m; n++) {
        arrayOfObject[n] = paramObjectInputStream.readObject();
      }
      localBuilder.put(localObject, ImmutableList.copyOf(arrayOfObject));
      j += m;
    }
    ImmutableMap localImmutableMap;
    try
    {
      localImmutableMap = localBuilder.build();
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw ((InvalidObjectException)new InvalidObjectException(localIllegalArgumentException.getMessage()).initCause(localIllegalArgumentException));
    }
    ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, localImmutableMap);
    ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, j);
  }
  
  public static final class Builder
    extends ImmutableMultimap.Builder
  {
    public Builder put(Object paramObject1, Object paramObject2)
    {
      super.put(paramObject1, paramObject2);
      return this;
    }
    
    public Builder put(Map.Entry paramEntry)
    {
      super.put(paramEntry);
      return this;
    }
    
    public Builder putAll(Object paramObject, Iterable paramIterable)
    {
      super.putAll(paramObject, paramIterable);
      return this;
    }
    
    public Builder putAll(Object paramObject, Object... paramVarArgs)
    {
      super.putAll(paramObject, paramVarArgs);
      return this;
    }
    
    public Builder putAll(Multimap paramMultimap)
    {
      super.putAll(paramMultimap);
      return this;
    }
    
    public Builder orderKeysBy(Comparator paramComparator)
    {
      super.orderKeysBy(paramComparator);
      return this;
    }
    
    public Builder orderValuesBy(Comparator paramComparator)
    {
      super.orderValuesBy(paramComparator);
      return this;
    }
    
    public ImmutableListMultimap build()
    {
      return (ImmutableListMultimap)super.build();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */