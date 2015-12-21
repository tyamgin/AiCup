package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public final class ArrayListMultimap
  extends AbstractListMultimap
{
  private static final int DEFAULT_VALUES_PER_KEY = 3;
  @VisibleForTesting
  transient int expectedValuesPerKey;
  @GwtIncompatible("Not needed in emulated source.")
  private static final long serialVersionUID = 0L;
  
  public static ArrayListMultimap create()
  {
    return new ArrayListMultimap();
  }
  
  public static ArrayListMultimap create(int paramInt1, int paramInt2)
  {
    return new ArrayListMultimap(paramInt1, paramInt2);
  }
  
  public static ArrayListMultimap create(Multimap paramMultimap)
  {
    return new ArrayListMultimap(paramMultimap);
  }
  
  private ArrayListMultimap()
  {
    super(new HashMap());
    this.expectedValuesPerKey = 3;
  }
  
  private ArrayListMultimap(int paramInt1, int paramInt2)
  {
    super(Maps.newHashMapWithExpectedSize(paramInt1));
    Preconditions.checkArgument(paramInt2 >= 0);
    this.expectedValuesPerKey = paramInt2;
  }
  
  private ArrayListMultimap(Multimap paramMultimap)
  {
    this(paramMultimap.keySet().size(), (paramMultimap instanceof ArrayListMultimap) ? ((ArrayListMultimap)paramMultimap).expectedValuesPerKey : 3);
    putAll(paramMultimap);
  }
  
  List createCollection()
  {
    return new ArrayList(this.expectedValuesPerKey);
  }
  
  public void trimToSize()
  {
    Iterator localIterator = backingMap().values().iterator();
    while (localIterator.hasNext())
    {
      Collection localCollection = (Collection)localIterator.next();
      ArrayList localArrayList = (ArrayList)localCollection;
      localArrayList.trimToSize();
    }
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.expectedValuesPerKey);
    Serialization.writeMultimap(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.expectedValuesPerKey = paramObjectInputStream.readInt();
    int i = Serialization.readCount(paramObjectInputStream);
    HashMap localHashMap = Maps.newHashMapWithExpectedSize(i);
    setMap(localHashMap);
    Serialization.populateMultimap(this, paramObjectInputStream, i);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ArrayListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */