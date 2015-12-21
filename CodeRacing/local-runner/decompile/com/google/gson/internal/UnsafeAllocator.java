package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator
{
  public abstract Object newInstance(Class paramClass)
    throws Exception;
  
  public static UnsafeAllocator create()
  {
    try
    {
      Class localClass = Class.forName("sun.misc.Unsafe");
      Field localField = localClass.getDeclaredField("theUnsafe");
      localField.setAccessible(true);
      localObject = localField.get(null);
      Method localMethod3 = localClass.getMethod("allocateInstance", new Class[] { Class.class });
      new UnsafeAllocator()
      {
        public Object newInstance(Class paramAnonymousClass)
          throws Exception
        {
          return this.val$allocateInstance.invoke(localObject, new Object[] { paramAnonymousClass });
        }
      };
    }
    catch (Exception localException1)
    {
      try
      {
        Method localMethod1 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
        localMethod1.setAccessible(true);
        final int i = ((Integer)localMethod1.invoke(null, new Object[] { Object.class })).intValue();
        final Object localObject = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Integer.TYPE });
        ((Method)localObject).setAccessible(true);
        new UnsafeAllocator()
        {
          public Object newInstance(Class paramAnonymousClass)
            throws Exception
          {
            return this.val$newInstance.invoke(null, new Object[] { paramAnonymousClass, Integer.valueOf(i) });
          }
        };
      }
      catch (Exception localException2)
      {
        try
        {
          Method localMethod2 = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
          localMethod2.setAccessible(true);
          new UnsafeAllocator()
          {
            public Object newInstance(Class paramAnonymousClass)
              throws Exception
            {
              return this.val$newInstance.invoke(null, new Object[] { paramAnonymousClass, Object.class });
            }
          };
        }
        catch (Exception localException3) {}
      }
    }
    new UnsafeAllocator()
    {
      public Object newInstance(Class paramAnonymousClass)
      {
        throw new UnsupportedOperationException("Cannot allocate " + paramAnonymousClass);
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\internal\UnsafeAllocator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */