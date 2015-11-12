package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@GwtCompatible(serializable=true, emulated=true)
abstract class ImmutableAsList
  extends ImmutableList
{
  abstract ImmutableCollection delegateCollection();
  
  public boolean contains(Object paramObject)
  {
    return delegateCollection().contains(paramObject);
  }
  
  public int size()
  {
    return delegateCollection().size();
  }
  
  public boolean isEmpty()
  {
    return delegateCollection().isEmpty();
  }
  
  boolean isPartialView()
  {
    return delegateCollection().isPartialView();
  }
  
  @GwtIncompatible("serialization")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  @GwtIncompatible("serialization")
  Object writeReplace()
  {
    return new SerializedForm(delegateCollection());
  }
  
  @GwtIncompatible("serialization")
  static class SerializedForm
    implements Serializable
  {
    final ImmutableCollection collection;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableCollection paramImmutableCollection)
    {
      this.collection = paramImmutableCollection;
    }
    
    Object readResolve()
    {
      return this.collection.asList();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableAsList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */