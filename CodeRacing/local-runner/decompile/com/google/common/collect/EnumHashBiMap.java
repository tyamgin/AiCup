package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@GwtCompatible(emulated=true)
public final class EnumHashBiMap
  extends AbstractBiMap
{
  private transient Class keyType;
  @GwtIncompatible("only needed in emulated source.")
  private static final long serialVersionUID = 0L;
  
  public static EnumHashBiMap create(Class paramClass)
  {
    return new EnumHashBiMap(paramClass);
  }
  
  public static EnumHashBiMap create(Map paramMap)
  {
    EnumHashBiMap localEnumHashBiMap = create(EnumBiMap.inferKeyType(paramMap));
    localEnumHashBiMap.putAll(paramMap);
    return localEnumHashBiMap;
  }
  
  private EnumHashBiMap(Class paramClass)
  {
    super(WellBehavedMap.wrap(new EnumMap(paramClass)), Maps.newHashMapWithExpectedSize(((Enum[])paramClass.getEnumConstants()).length));
    this.keyType = paramClass;
  }
  
  Enum checkKey(Enum paramEnum)
  {
    return (Enum)Preconditions.checkNotNull(paramEnum);
  }
  
  public Object put(Enum paramEnum, Object paramObject)
  {
    return super.put(paramEnum, paramObject);
  }
  
  public Object forcePut(Enum paramEnum, Object paramObject)
  {
    return super.forcePut(paramEnum, paramObject);
  }
  
  public Class keyType()
  {
    return this.keyType;
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.keyType);
    Serialization.writeMap(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.keyType = ((Class)paramObjectInputStream.readObject());
    setDelegates(WellBehavedMap.wrap(new EnumMap(this.keyType)), new HashMap(((Enum[])this.keyType.getEnumConstants()).length * 3 / 2));
    Serialization.populateMap(this, paramObjectInputStream);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EnumHashBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */