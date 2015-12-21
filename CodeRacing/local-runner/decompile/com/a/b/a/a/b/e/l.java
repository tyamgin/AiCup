package com.a.b.a.a.b.e;

import com.a.b.a.a.c.o;
import com.a.b.a.a.e.a.c;
import com.a.b.a.a.e.a.d;
import com.codeforces.commons.text.StringUtil;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class l
{
  private static final Logger a = LoggerFactory.getLogger(l.class);
  
  private l()
  {
    throw new UnsupportedOperationException();
  }
  
  public static com.a.b.a.a.b.n a(com.a.b.a.a.a.b paramb, int paramInt1, String paramString1, String paramString2, int paramInt2, List paramList)
  {
    a(paramString1, paramString2);
    try
    {
      if (paramString2.endsWith(".class")) {
        return new com.a.b.a.a.b.n(paramString1, new com.a.b.a.a.e.a.a(paramString2, paramInt2));
      }
      if ("#KeyboardPlayer".equals(paramString2))
      {
        localObject1 = paramList.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (com.a.b.a.a.d.n)((Iterator)localObject1).next();
          if ((localObject2 instanceof com.a.b.a.a.d.a)) {
            return new com.a.b.a.a.b.n(paramString1, new c(paramInt2, ((com.a.b.a.a.d.a)localObject2).a()));
          }
        }
      }
      else
      {
        if ("#LocalTestPlayer".equals(paramString2))
        {
          localObject1 = d.b(paramb, paramInt1, paramString2, paramInt2);
          return a(paramString1, (d)localObject1);
        }
        if (com.a.b.a.a.e.a.a.m.a(paramString2) != null)
        {
          localObject1 = d.a(paramb, paramInt1, paramString2, paramInt2);
          return a(paramString1, (d)localObject1);
        }
      }
      Object localObject1 = String.format("Unsupported player definition: '%s'.", new Object[] { paramString2 });
      a.error((String)localObject1);
      throw new IllegalArgumentException((String)localObject1);
    }
    catch (RuntimeException localRuntimeException)
    {
      Object localObject2 = String.format("Can't load player defined by '%s'.", new Object[] { paramString2 });
      a.error((String)localObject2, localRuntimeException);
      com.a.b.a.a.b.n localn = new com.a.b.a.a.b.n(paramString1, new com.a.b.a.a.e.a.b(paramInt2));
      localn.a("При инициализации игрока возникло непредвиденное исключение.");
      return localn;
    }
  }
  
  private static com.a.b.a.a.b.n a(String paramString, d paramd)
  {
    try
    {
      paramd.c();
      return new com.a.b.a.a.b.n(paramString, paramd);
    }
    catch (RuntimeException localRuntimeException)
    {
      localRuntimeException.printStackTrace();
      paramd.close();
      throw localRuntimeException;
    }
  }
  
  private static void a(String paramString1, String paramString2)
  {
    if (StringUtil.isBlank(paramString1)) {
      throw new IllegalArgumentException("Argument 'name' is blank.");
    }
    if (StringUtil.isBlank(paramString2)) {
      throw new IllegalArgumentException("Argument 'playerDefinition' is blank.");
    }
  }
  
  public static o a(com.a.b.a.a.b.n paramn1, com.a.b.a.a.b.n paramn2)
  {
    return new o(paramn1.a(), paramn1.equals(paramn2), paramn1.b(), paramn1.f(), paramn1.i());
  }
  
  public static o[] a(List paramList, com.a.b.a.a.b.n paramn)
  {
    int i = paramList.size();
    o[] arrayOfo = new o[i];
    for (int j = 0; j < i; j++) {
      arrayOfo[j] = a((com.a.b.a.a.b.n)paramList.get(j), paramn);
    }
    return arrayOfo;
  }
  
  public static Comparator a()
  {
    return new m();
  }
  
  public static Comparator b()
  {
    return new n();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\l.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */