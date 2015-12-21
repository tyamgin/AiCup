package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@GwtCompatible(emulated=true)
public final class EnumBiMap
  extends AbstractBiMap
{
  private transient Class keyType;
  private transient Class valueType;
  @GwtIncompatible("not needed in emulated source.")
  private static final long serialVersionUID = 0L;
  
  public static EnumBiMap create(Class paramClass1, Class paramClass2)
  {
    return new EnumBiMap(paramClass1, paramClass2);
  }
  
  public static EnumBiMap create(Map paramMap)
  {
    EnumBiMap localEnumBiMap = create(inferKeyType(paramMap), inferValueType(paramMap));
    localEnumBiMap.putAll(paramMap);
    return localEnumBiMap;
  }
  
  private EnumBiMap(Class paramClass1, Class paramClass2)
  {
    super(WellBehavedMap.wrap(new EnumMap(paramClass1)), WellBehavedMap.wrap(new EnumMap(paramClass2)));
    this.keyType = paramClass1;
    this.valueType = paramClass2;
  }
  
  static Class inferKeyType(Map paramMap)
  {
    if ((paramMap instanceof EnumBiMap)) {
      return ((EnumBiMap)paramMap).keyType();
    }
    if ((paramMap instanceof EnumHashBiMap)) {
      return ((EnumHashBiMap)paramMap).keyType();
    }
    Preconditions.checkArgument(!paramMap.isEmpty());
    return ((Enum)paramMap.keySet().iterator().next()).getDeclaringClass();
  }
  
  private static Class inferValueType(Map paramMap)
  {
    if ((paramMap instanceof EnumBiMap)) {
      return ((EnumBiMap)paramMap).valueType;
    }
    Preconditions.checkArgument(!paramMap.isEmpty());
    return ((Enum)paramMap.values().iterator().next()).getDeclaringClass();
  }
  
  public Class keyType()
  {
    return this.keyType;
  }
  
  public Class valueType()
  {
    return this.valueType;
  }
  
  Enum checkKey(Enum paramEnum)
  {
    return (Enum)Preconditions.checkNotNull(paramEnum);
  }
  
  Enum checkValue(Enum paramEnum)
  {
    return (Enum)Preconditions.checkNotNull(paramEnum);
  }
  
  @GwtIncompatible("java.io.ObjectOutputStream")
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.keyType);
    paramObjectOutputStream.writeObject(this.valueType);
    Serialization.writeMap(this, paramObjectOutputStream);
  }
  
  @GwtIncompatible("java.io.ObjectInputStream")
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.keyType = ((Class)paramObjectInputStream.readObject());
    this.valueType = ((Class)paramObjectInputStream.readObject());
    setDelegates(WellBehavedMap.wrap(new EnumMap(this.keyType)), WellBehavedMap.wrap(new EnumMap(this.valueType)));
    Serialization.populateMap(this, paramObjectInputStream);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EnumBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */