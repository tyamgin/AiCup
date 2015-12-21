package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public class ImmutableSetMultimap
  extends ImmutableMultimap
  implements SetMultimap
{
  private final transient ImmutableSortedSet emptySet;
  private transient ImmutableSetMultimap inverse;
  private transient ImmutableSet entries;
  @GwtIncompatible("not needed in emulated source.")
  private static final long serialVersionUID = 0L;
  
  public static ImmutableSetMultimap of()
  {
    return EmptyImmutableSetMultimap.INSTANCE;
  }
  
  public static ImmutableSetMultimap of(Object paramObject1, Object paramObject2)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    return localBuilder.build();
  }
  
  public static ImmutableSetMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    return localBuilder.build();
  }
  
  public static ImmutableSetMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    localBuilder.put(paramObject5, paramObject6);
    return localBuilder.build();
  }
  
  public static ImmutableSetMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    Builder localBuilder = builder();
    localBuilder.put(paramObject1, paramObject2);
    localBuilder.put(paramObject3, paramObject4);
    localBuilder.put(paramObject5, paramObject6);
    localBuilder.put(paramObject7, paramObject8);
    return localBuilder.build();
  }
  
  public static ImmutableSetMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
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
  
  public static ImmutableSetMultimap copyOf(Multimap paramMultimap)
  {
    return copyOf(paramMultimap, null);
  }
  
  private static ImmutableSetMultimap copyOf(Multimap paramMultimap, Comparator paramComparator)
  {
    Preconditions.checkNotNull(paramMultimap);
    if ((paramMultimap.isEmpty()) && (paramComparator == null)) {
      return of();
    }
    if ((paramMultimap instanceof ImmutableSetMultimap))
    {
      localObject1 = (ImmutableSetMultimap)paramMultimap;
      if (!((ImmutableSetMultimap)localObject1).isPartialView()) {
        return (ImmutableSetMultimap)localObject1;
      }
    }
    Object localObject1 = ImmutableMap.builder();
    int i = 0;
    Iterator localIterator = paramMultimap.asMap().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject2 = localEntry.getKey();
      Collection localCollection = (Collection)localEntry.getValue();
      ImmutableSortedSet localImmutableSortedSet = paramComparator == null ? ImmutableSet.copyOf(localCollection) : ImmutableSortedSet.copyOf(paramComparator, localCollection);
      if (!localImmutableSortedSet.isEmpty())
      {
        ((ImmutableMap.Builder)localObject1).put(localObject2, localImmutableSortedSet);
        i += localImmutableSortedSet.size();
      }
    }
    return new ImmutableSetMultimap(((ImmutableMap.Builder)localObject1).build(), i, paramComparator);
  }
  
  ImmutableSetMultimap(ImmutableMap paramImmutableMap, int paramInt, Comparator paramComparator)
  {
    super(paramImmutableMap, paramInt);
    this.emptySet = (paramComparator == null ? null : ImmutableSortedSet.emptySet(paramComparator));
  }
  
  public ImmutableSet get(Object paramObject)
  {
    ImmutableSet localImmutableSet = (ImmutableSet)this.map.get(paramObject);
    if (localImmutableSet != null) {
      return localImmutableSet;
    }
    if (this.emptySet != null) {
      return this.emptySet;
    }
    return ImmutableSet.of();
  }
  
  public ImmutableSetMultimap inverse()
  {
    ImmutableSetMultimap localImmutableSetMultimap = this.inverse;
    return localImmutableSetMultimap == null ? (this.inverse = invert()) : localImmutableSetMultimap;
  }
  
  private ImmutableSetMultimap invert()
  {
    Builder localBuilder = builder();
    Object localObject = entries().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      localBuilder.put(localEntry.getValue(), localEntry.getKey());
    }
    localObject = localBuilder.build();
    ((ImmutableSetMultimap)localObject).inverse = this;
    return (ImmutableSetMultimap)localObject;
  }
  
  @Deprecated
  public ImmutableSet removeAll(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public ImmutableSet replaceValues(Object paramObject, Iterable paramIterable)
  {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableSet entries()
  {
    ImmutableSet localImmutableSet = this.entries;
    return localImmutableSet == null ? (this.entries = ImmutableSet.copyOf(super.entries())) : localImmutableSet;
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
      ImmutableSet localImmutableSet = ImmutableSet.copyOf(arrayOfObject);
      if (localImmutableSet.size() != arrayOfObject.length) {
        throw new InvalidObjectException("Duplicate key-value pairs exist for key " + localObject);
      }
      localBuilder.put(localObject, localImmutableSet);
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
    public Builder()
    {
      this.builderMultimap = new ImmutableSetMultimap.BuilderMultimap();
    }
    
    public Builder put(Object paramObject1, Object paramObject2)
    {
      this.builderMultimap.put(Preconditions.checkNotNull(paramObject1), Preconditions.checkNotNull(paramObject2));
      return this;
    }
    
    public Builder put(Map.Entry paramEntry)
    {
      this.builderMultimap.put(Preconditions.checkNotNull(paramEntry.getKey()), Preconditions.checkNotNull(paramEntry.getValue()));
      return this;
    }
    
    public Builder putAll(Object paramObject, Iterable paramIterable)
    {
      Collection localCollection = this.builderMultimap.get(Preconditions.checkNotNull(paramObject));
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        localCollection.add(Preconditions.checkNotNull(localObject));
      }
      return this;
    }
    
    public Builder putAll(Object paramObject, Object... paramVarArgs)
    {
      return putAll(paramObject, Arrays.asList(paramVarArgs));
    }
    
    public Builder putAll(Multimap paramMultimap)
    {
      Iterator localIterator = paramMultimap.asMap().entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        putAll(localEntry.getKey(), (Iterable)localEntry.getValue());
      }
      return this;
    }
    
    public Builder orderKeysBy(Comparator paramComparator)
    {
      this.keyComparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
      return this;
    }
    
    public Builder orderValuesBy(Comparator paramComparator)
    {
      super.orderValuesBy(paramComparator);
      return this;
    }
    
    public ImmutableSetMultimap build()
    {
      if (this.keyComparator != null)
      {
        ImmutableSetMultimap.BuilderMultimap localBuilderMultimap = new ImmutableSetMultimap.BuilderMultimap();
        ArrayList localArrayList = Lists.newArrayList(this.builderMultimap.asMap().entrySet());
        Collections.sort(localArrayList, Ordering.from(this.keyComparator).onResultOf(new Function()
        {
          public Object apply(Map.Entry paramAnonymousEntry)
          {
            return paramAnonymousEntry.getKey();
          }
        }));
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          localBuilderMultimap.putAll(localEntry.getKey(), (Iterable)localEntry.getValue());
        }
        this.builderMultimap = localBuilderMultimap;
      }
      return ImmutableSetMultimap.copyOf(this.builderMultimap, this.valueComparator);
    }
  }
  
  private static class BuilderMultimap
    extends AbstractMapBasedMultimap
  {
    private static final long serialVersionUID = 0L;
    
    BuilderMultimap()
    {
      super();
    }
    
    Collection createCollection()
    {
      return Sets.newLinkedHashSet();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */