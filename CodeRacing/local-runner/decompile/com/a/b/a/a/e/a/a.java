package com.a.b.a.a.e.a;

import com.a.b.a.a.c.c;
import com.a.b.a.a.c.l;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.v;
import com.a.b.a.a.e.d;
import java.lang.reflect.Constructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class a
  implements f
{
  private static final Logger a = LoggerFactory.getLogger(a.class);
  private static final String b = com.a.b.a.a.e.a.class.getPackage().getName();
  private final d[] c;
  private final int d;
  private l e;
  
  public a(String paramString, int paramInt)
  {
    if (!paramString.endsWith(".class")) {
      throw new IllegalArgumentException(String.format("Illegal player definition: '%s'.", new Object[] { paramString }));
    }
    paramString = paramString.substring(0, paramString.length() - ".class".length());
    if (paramString.indexOf('.') == -1) {
      paramString = b + '.' + paramString;
    }
    Constructor localConstructor;
    Object localObject1;
    try
    {
      localConstructor = Class.forName(paramString).getConstructor(new Class[0]);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localObject1 = String.format("Class '%s' does not exist.", new Object[] { paramString });
      a.error((String)localObject1, localClassNotFoundException);
      throw new IllegalArgumentException((String)localObject1, localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localObject1 = String.format("Class '%s' hasn't default constructor.", new Object[] { paramString });
      a.error((String)localObject1, localNoSuchMethodException);
      throw new IllegalArgumentException((String)localObject1, localNoSuchMethodException);
    }
    this.c = new d[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      try
      {
        Object localObject2 = localConstructor.newInstance(new Object[0]);
        if ((localObject2 instanceof d))
        {
          localObject1 = (d)localObject2;
        }
        else
        {
          a.error(String.format("Instance of class '%s' is not a strategy.", new Object[] { paramString }));
          localObject1 = new com.a.b.a.a.e.a();
        }
      }
      catch (Exception localException)
      {
        a.error(String.format("Can't create instance of class '%s'.", new Object[] { paramString }), localException);
        localObject1 = new com.a.b.a.a.e.a();
      }
      this.c[i] = localObject1;
    }
    this.d = paramInt;
  }
  
  public int a()
  {
    return 1;
  }
  
  public void a(l paraml)
  {
    this.e = paraml;
  }
  
  public m[] a(c[] paramArrayOfc, v paramv)
  {
    if (paramArrayOfc.length != this.d) {
      throw new IllegalArgumentException(String.format("Strategy adapter '%s' got %d cars while team size is %d.", new Object[] { getClass().getSimpleName(), Integer.valueOf(paramArrayOfc.length), Integer.valueOf(this.d) }));
    }
    m[] arrayOfm = new m[this.d];
    for (int i = 0; i < this.d; i++)
    {
      arrayOfm[i] = new m();
      this.c[i].a(paramArrayOfc[i], paramv, this.e, arrayOfm[i]);
    }
    return arrayOfm;
  }
  
  public void close() {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */