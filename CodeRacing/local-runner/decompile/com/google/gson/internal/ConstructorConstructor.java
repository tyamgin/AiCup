package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class ConstructorConstructor
{
  private final Map instanceCreators;
  
  public ConstructorConstructor(Map paramMap)
  {
    this.instanceCreators = paramMap;
  }
  
  public ObjectConstructor get(TypeToken paramTypeToken)
  {
    final Type localType = paramTypeToken.getType();
    Class localClass = paramTypeToken.getRawType();
    final InstanceCreator localInstanceCreator1 = (InstanceCreator)this.instanceCreators.get(localType);
    if (localInstanceCreator1 != null) {
      new ObjectConstructor()
      {
        public Object construct()
        {
          return localInstanceCreator1.createInstance(localType);
        }
      };
    }
    final InstanceCreator localInstanceCreator2 = (InstanceCreator)this.instanceCreators.get(localClass);
    if (localInstanceCreator2 != null) {
      new ObjectConstructor()
      {
        public Object construct()
        {
          return localInstanceCreator2.createInstance(localType);
        }
      };
    }
    ObjectConstructor localObjectConstructor1 = newDefaultConstructor(localClass);
    if (localObjectConstructor1 != null) {
      return localObjectConstructor1;
    }
    ObjectConstructor localObjectConstructor2 = newDefaultImplementationConstructor(localType, localClass);
    if (localObjectConstructor2 != null) {
      return localObjectConstructor2;
    }
    return newUnsafeAllocator(localType, localClass);
  }
  
  private ObjectConstructor newDefaultConstructor(Class paramClass)
  {
    try
    {
      final Constructor localConstructor = paramClass.getDeclaredConstructor(new Class[0]);
      if (!localConstructor.isAccessible()) {
        localConstructor.setAccessible(true);
      }
      new ObjectConstructor()
      {
        public Object construct()
        {
          try
          {
            Object[] arrayOfObject = null;
            return localConstructor.newInstance(arrayOfObject);
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new RuntimeException("Failed to invoke " + localConstructor + " with no args", localInstantiationException);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            throw new RuntimeException("Failed to invoke " + localConstructor + " with no args", localInvocationTargetException.getTargetException());
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new AssertionError(localIllegalAccessException);
          }
        }
      };
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
  private ObjectConstructor newDefaultImplementationConstructor(final Type paramType, Class paramClass)
  {
    if (Collection.class.isAssignableFrom(paramClass))
    {
      if (SortedSet.class.isAssignableFrom(paramClass)) {
        new ObjectConstructor()
        {
          public Object construct()
          {
            return new TreeSet();
          }
        };
      }
      if (EnumSet.class.isAssignableFrom(paramClass)) {
        new ObjectConstructor()
        {
          public Object construct()
          {
            if ((paramType instanceof ParameterizedType))
            {
              Type localType = ((ParameterizedType)paramType).getActualTypeArguments()[0];
              if ((localType instanceof Class)) {
                return EnumSet.noneOf((Class)localType);
              }
              throw new JsonIOException("Invalid EnumSet type: " + paramType.toString());
            }
            throw new JsonIOException("Invalid EnumSet type: " + paramType.toString());
          }
        };
      }
      if (Set.class.isAssignableFrom(paramClass)) {
        new ObjectConstructor()
        {
          public Object construct()
          {
            return new LinkedHashSet();
          }
        };
      }
      if (Queue.class.isAssignableFrom(paramClass)) {
        new ObjectConstructor()
        {
          public Object construct()
          {
            return new LinkedList();
          }
        };
      }
      new ObjectConstructor()
      {
        public Object construct()
        {
          return new ArrayList();
        }
      };
    }
    if (Map.class.isAssignableFrom(paramClass))
    {
      if (SortedMap.class.isAssignableFrom(paramClass)) {
        new ObjectConstructor()
        {
          public Object construct()
          {
            return new TreeMap();
          }
        };
      }
      if (((paramType instanceof ParameterizedType)) && (!String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)paramType).getActualTypeArguments()[0]).getRawType()))) {
        new ObjectConstructor()
        {
          public Object construct()
          {
            return new LinkedHashMap();
          }
        };
      }
      new ObjectConstructor()
      {
        public Object construct()
        {
          return new LinkedTreeMap();
        }
      };
    }
    return null;
  }
  
  private ObjectConstructor newUnsafeAllocator(final Type paramType, final Class paramClass)
  {
    new ObjectConstructor()
    {
      private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
      
      public Object construct()
      {
        try
        {
          Object localObject = this.unsafeAllocator.newInstance(paramClass);
          return localObject;
        }
        catch (Exception localException)
        {
          throw new RuntimeException("Unable to invoke no-args constructor for " + paramType + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", localException);
        }
      }
    };
  }
  
  public String toString()
  {
    return this.instanceCreators.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\ConstructorConstructor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */