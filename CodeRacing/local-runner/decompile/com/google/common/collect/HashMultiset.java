package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

@GwtCompatible(serializable=true, emulated=true)
public final class HashMultiset
  extends AbstractMapBasedMultiset
{
  @GwtIncompatible("Not needed in emulated source.")
  private static final long serialVersionUID = 0L;
  
  public static HashMultiset create()
  {
    return new HashMultiset();
  }
  
  public static HashMultiset create(int paramInt)
  {
    return new HashMultiset(paramInt);
  }
  
  public static HashMultiset create(Iterable paramIterable)
  {
    HashMultiset localHashMultiset = create(Multisets.inferDistinctElements(paramIterable));
    Iterables.addAll(localHashMultiset, paramIterable);
    return localHashMultiset;
  }
  
  private HashMultiset()
  {
    super(new HashMap());
  }
  
  private HashMultiset(int paramInt)
  {
    super(Maps.newHashMapWithExpectedSize(paramInt));
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Serialization.writeMultiset(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = Serialization.readCount(paramObjectInputStream);
    setBackingMap(Maps.newHashMapWithExpectedSize(i));
    Serialization.populateMultiset(this, paramObjectInputStream, i);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\HashMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */