package com.google.common.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class Serialization
{
  static int readCount(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    return paramObjectInputStream.readInt();
  }
  
  static void writeMap(Map paramMap, ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(paramMap.size());
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramObjectOutputStream.writeObject(localEntry.getKey());
      paramObjectOutputStream.writeObject(localEntry.getValue());
    }
  }
  
  static void populateMap(Map paramMap, ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = paramObjectInputStream.readInt();
    populateMap(paramMap, paramObjectInputStream, i);
  }
  
  static void populateMap(Map paramMap, ObjectInputStream paramObjectInputStream, int paramInt)
    throws IOException, ClassNotFoundException
  {
    for (int i = 0; i < paramInt; i++)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      Object localObject2 = paramObjectInputStream.readObject();
      paramMap.put(localObject1, localObject2);
    }
  }
  
  static void writeMultiset(Multiset paramMultiset, ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    int i = paramMultiset.entrySet().size();
    paramObjectOutputStream.writeInt(i);
    Iterator localIterator = paramMultiset.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      paramObjectOutputStream.writeObject(localEntry.getElement());
      paramObjectOutputStream.writeInt(localEntry.getCount());
    }
  }
  
  static void populateMultiset(Multiset paramMultiset, ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = paramObjectInputStream.readInt();
    populateMultiset(paramMultiset, paramObjectInputStream, i);
  }
  
  static void populateMultiset(Multiset paramMultiset, ObjectInputStream paramObjectInputStream, int paramInt)
    throws IOException, ClassNotFoundException
  {
    for (int i = 0; i < paramInt; i++)
    {
      Object localObject = paramObjectInputStream.readObject();
      int j = paramObjectInputStream.readInt();
      paramMultiset.add(localObject, j);
    }
  }
  
  static void writeMultimap(Multimap paramMultimap, ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(paramMultimap.asMap().size());
    Iterator localIterator1 = paramMultimap.asMap().entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      paramObjectOutputStream.writeObject(localEntry.getKey());
      paramObjectOutputStream.writeInt(((Collection)localEntry.getValue()).size());
      Iterator localIterator2 = ((Collection)localEntry.getValue()).iterator();
      while (localIterator2.hasNext())
      {
        Object localObject = localIterator2.next();
        paramObjectOutputStream.writeObject(localObject);
      }
    }
  }
  
  static void populateMultimap(Multimap paramMultimap, ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = paramObjectInputStream.readInt();
    populateMultimap(paramMultimap, paramObjectInputStream, i);
  }
  
  static void populateMultimap(Multimap paramMultimap, ObjectInputStream paramObjectInputStream, int paramInt)
    throws IOException, ClassNotFoundException
  {
    for (int i = 0; i < paramInt; i++)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      Collection localCollection = paramMultimap.get(localObject1);
      int j = paramObjectInputStream.readInt();
      for (int k = 0; k < j; k++)
      {
        Object localObject2 = paramObjectInputStream.readObject();
        localCollection.add(localObject2);
      }
    }
  }
  
  static FieldSetter getFieldSetter(Class paramClass, String paramString)
  {
    try
    {
      Field localField = paramClass.getDeclaredField(paramString);
      return new FieldSetter(localField, null);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new AssertionError(localNoSuchFieldException);
    }
  }
  
  static final class FieldSetter
  {
    private final Field field;
    
    private FieldSetter(Field paramField)
    {
      this.field = paramField;
      paramField.setAccessible(true);
    }
    
    void set(Object paramObject1, Object paramObject2)
    {
      try
      {
        this.field.set(paramObject1, paramObject2);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
    
    void set(Object paramObject, int paramInt)
    {
      try
      {
        this.field.set(paramObject, Integer.valueOf(paramInt));
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Serialization.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */