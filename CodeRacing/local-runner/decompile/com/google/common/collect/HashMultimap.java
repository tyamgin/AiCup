package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public final class HashMultimap
  extends AbstractSetMultimap
{
  private static final int DEFAULT_VALUES_PER_KEY = 2;
  @VisibleForTesting
  transient int expectedValuesPerKey = 2;
  @GwtIncompatible("Not needed in emulated source")
  private static final long serialVersionUID = 0L;
  
  public static HashMultimap create()
  {
    return new HashMultimap();
  }
  
  public static HashMultimap create(int paramInt1, int paramInt2)
  {
    return new HashMultimap(paramInt1, paramInt2);
  }
  
  public static HashMultimap create(Multimap paramMultimap)
  {
    return new HashMultimap(paramMultimap);
  }
  
  private HashMultimap()
  {
    super(new HashMap());
  }
  
  private HashMultimap(int paramInt1, int paramInt2)
  {
    super(Maps.newHashMapWithExpectedSize(paramInt1));
    Preconditions.checkArgument(paramInt2 >= 0);
    this.expectedValuesPerKey = paramInt2;
  }
  
  private HashMultimap(Multimap paramMultimap)
  {
    super(Maps.newHashMapWithExpectedSize(paramMultimap.keySet().size()));
    putAll(paramMultimap);
  }
  
  Set createCollection()
  {
    return Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.expectedValuesPerKey);
    Serialization.writeMultimap(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\HashMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */