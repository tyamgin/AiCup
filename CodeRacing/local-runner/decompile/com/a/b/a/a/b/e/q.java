package com.a.b.a.a.b.e;

import com.a.b.a.a.c.g;
import com.a.b.a.a.c.h;
import com.a.b.a.a.c.j;
import com.a.b.a.a.c.u;
import com.a.b.a.a.c.v;
import com.a.b.e;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.pair.IntPair;
import com.codeforces.commons.reflection.ReflectionUtil;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class q
{
  private static final Logger a = LoggerFactory.getLogger(q.class);
  private static final Gson b = new GsonBuilder().serializeSpecialFloatingPointValues().create();
  private static final Lock c = new ReentrantLock();
  
  private q()
  {
    throw new UnsupportedOperationException();
  }
  
  public static v a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble, List paramList1, com.a.b.a.a.a.b paramb, List paramList2, com.a.b.a.a.b.n paramn)
  {
    ArrayList localArrayList1 = new ArrayList(4);
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    Object localObject1 = paramList2.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (e)((Iterator)localObject1).next();
      if ((localObject2 instanceof com.a.b.a.a.b.d.c.b)) {
        localArrayList1.add(b.a((com.a.b.a.a.b.d.c.b)localObject2, paramDouble, paramn));
      } else if ((localObject2 instanceof com.a.b.a.a.b.d.e.a)) {
        localArrayList2.add(o.a((com.a.b.a.a.b.d.e.a)localObject2, paramDouble));
      } else if ((localObject2 instanceof com.a.b.a.a.b.d.a.a)) {
        localArrayList3.add(a.a((com.a.b.a.a.b.d.a.a)localObject2));
      } else if ((localObject2 instanceof com.a.b.a.a.b.d.d.a)) {
        localArrayList4.add(k.a((com.a.b.a.a.b.d.d.a)localObject2));
      } else if (!(localObject2 instanceof com.a.b.a.a.b.d.b.a)) {
        throw new IllegalArgumentException("Unsupported unit class: " + localObject2.getClass() + '.');
      }
    }
    com.a.a.a.a.c.a(localArrayList1);
    com.a.a.a.a.c.a(localArrayList2);
    com.a.a.a.a.c.a(localArrayList3);
    com.a.a.a.a.c.a(localArrayList4);
    localObject1 = paramb.e();
    Object localObject2 = ((i.a)localObject1).c();
    int i = localObject2.length;
    int[][] arrayOfInt = new int[i][2];
    for (int j = 0; j < i; j++)
    {
      Object localObject3 = localObject2[j];
      Preconditions.checkNotNull(((IntPair)localObject3).getFirst());
      Preconditions.checkNotNull(((IntPair)localObject3).getSecond());
      arrayOfInt[j][0] = ((Integer)((IntPair)localObject3).getFirst()).intValue();
      arrayOfInt[j][1] = ((Integer)((IntPair)localObject3).getSecond()).intValue();
    }
    return new v(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, l.a(paramList1, paramn), (com.a.b.a.a.c.c[])localArrayList1.toArray(new com.a.b.a.a.c.c[localArrayList1.size()]), (com.a.b.a.a.c.q[])localArrayList2.toArray(new com.a.b.a.a.c.q[localArrayList2.size()]), (com.a.b.a.a.c.a[])localArrayList3.toArray(new com.a.b.a.a.c.a[localArrayList3.size()]), (com.a.b.a.a.c.n[])localArrayList4.toArray(new com.a.b.a.a.c.n[localArrayList4.size()]), ((i.a)localObject1).a(), ((i.a)localObject1).b(), arrayOfInt, ((i.a)localObject1).d());
  }
  
  public static h a(v paramv, Long paramLong, double paramDouble, boolean paramBoolean, List paramList1, List paramList2, List paramList3)
  {
    int i = paramList1.size();
    j[] arrayOfj = new j[i];
    for (int j = 0; j < i; j++) {
      arrayOfj[j] = f.a((com.a.b.a.a.b.a.a)paramList1.get(j));
    }
    HashMap localHashMap = new HashMap();
    Object localObject1 = paramList3.iterator();
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (e)((Iterator)localObject1).next();
      if ((localObject2 instanceof com.a.b.a.a.b.d.c.b))
      {
        localObject3 = (com.a.b.a.a.b.d.c.b)localObject2;
        Vector2D localVector2D = new Vector2D(1.0D, 0.0D).rotate(((com.a.b.a.a.b.d.c.b)localObject3).b().e());
        double d = ((com.a.b.a.a.b.d.c.b)localObject3).j().dotProduct(localVector2D);
        localHashMap.put(Long.valueOf(((com.a.b.a.a.b.d.c.b)localObject3).a()), new com.a.b.a.a.c.f(((com.a.b.a.a.b.d.c.b)localObject3).i() == null ? 0.0D : ((com.a.b.a.a.b.d.c.b)localObject3).i().doubleValue(), ((com.a.b.a.a.b.d.c.b)localObject3).I() > 0 ? Integer.valueOf(((com.a.b.a.a.b.d.c.b)localObject3).I()) : null, ((com.a.b.a.a.b.d.c.b)localObject3).K() ? Boolean.valueOf(true) : null, d));
      }
    }
    localObject1 = null;
    Object localObject2 = paramList2.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (com.a.b.a.a.b.n)((Iterator)localObject2).next();
      if (((com.a.b.a.a.b.n)localObject3).e())
      {
        if (localObject1 == null) {
          localObject1 = new HashMap();
        }
        ((Map)localObject1).put(Long.valueOf(((com.a.b.a.a.b.n)localObject3).a()), new g(true));
      }
    }
    return new h(paramv, paramLong, paramDouble, paramBoolean, arrayOfj, localHashMap, (Map)localObject1, paramList3);
  }
  
  public static h a(String paramString, h paramh, Object paramObject)
  {
    c.lock();
    try
    {
      if (paramh == null)
      {
        localObject1 = (h)b.fromJson(paramString, h.class);
        return (h)localObject1;
      }
      Object localObject1 = (JsonObject)b.fromJson(paramString, JsonObject.class);
      a(b, (JsonObject)localObject1, paramh);
      b(b, (JsonObject)localObject1, paramh);
      c(b, (JsonObject)localObject1, paramh);
      d(b, (JsonObject)localObject1, paramh);
      e(b, (JsonObject)localObject1, paramh);
      f(b, (JsonObject)localObject1, paramh);
      h localh1 = (h)b.fromJson((JsonElement)localObject1, h.class);
      a(localh1, paramObject);
      h localh2 = localh1;
      return localh2;
    }
    catch (RuntimeException localRuntimeException)
    {
      a.error(String.format("Can't de-serialize decorated world. Input string: %s.", new Object[] { paramString }));
      throw localRuntimeException;
    }
    finally
    {
      c.unlock();
    }
  }
  
  private static void a(Gson paramGson, JsonObject paramJsonObject, h paramh)
  {
    Map localMap = ReflectionUtil.getFieldsByNameMap(h.class);
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      if ((paramJsonObject.get(str) == null) && (!"systemData".equals(str))) {
        a(paramGson, paramJsonObject, paramh, (Field)((List)localEntry.getValue()).get(0));
      }
    }
  }
  
  private static void b(Gson paramGson, JsonObject paramJsonObject, h paramh)
  {
    Map localMap1 = a(paramh);
    Map localMap2 = ReflectionUtil.getFieldsByNameMap(com.a.b.a.a.c.o.class);
    Iterator localIterator1 = paramJsonObject.getAsJsonArray("players").iterator();
    while (localIterator1.hasNext())
    {
      JsonElement localJsonElement = (JsonElement)localIterator1.next();
      JsonObject localJsonObject = localJsonElement.getAsJsonObject();
      long l = localJsonObject.get("id").getAsLong();
      com.a.b.a.a.c.o localo = (com.a.b.a.a.c.o)localMap1.get(Long.valueOf(l));
      if (localo != null)
      {
        Iterator localIterator2 = localMap2.entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          if (localJsonObject.get((String)localEntry.getKey()) == null) {
            a(paramGson, localJsonObject, localo, (Field)((List)localEntry.getValue()).get(0));
          }
        }
      }
    }
  }
  
  private static void c(Gson paramGson, JsonObject paramJsonObject, h paramh)
  {
    Map localMap1 = a(paramh.getCarsUnsafe());
    Map localMap2 = ReflectionUtil.getFieldsByNameMap(com.a.b.a.a.c.c.class);
    Iterator localIterator1 = paramJsonObject.getAsJsonArray("cars").iterator();
    while (localIterator1.hasNext())
    {
      JsonElement localJsonElement = (JsonElement)localIterator1.next();
      JsonObject localJsonObject = localJsonElement.getAsJsonObject();
      long l = localJsonObject.get("id").getAsLong();
      com.a.b.a.a.c.c localc = (com.a.b.a.a.c.c)localMap1.get(Long.valueOf(l));
      if (localc != null)
      {
        Iterator localIterator2 = localMap2.entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          if (localJsonObject.get((String)localEntry.getKey()) == null) {
            a(paramGson, localJsonObject, localc, (Field)((List)localEntry.getValue()).get(0));
          }
        }
      }
    }
  }
  
  private static void d(Gson paramGson, JsonObject paramJsonObject, h paramh)
  {
    Map localMap1 = a(paramh.getProjectilesUnsafe());
    Map localMap2 = ReflectionUtil.getFieldsByNameMap(com.a.b.a.a.c.q.class);
    Iterator localIterator1 = paramJsonObject.getAsJsonArray("projectiles").iterator();
    while (localIterator1.hasNext())
    {
      JsonElement localJsonElement = (JsonElement)localIterator1.next();
      JsonObject localJsonObject = localJsonElement.getAsJsonObject();
      long l = localJsonObject.get("id").getAsLong();
      com.a.b.a.a.c.q localq = (com.a.b.a.a.c.q)localMap1.get(Long.valueOf(l));
      if (localq != null)
      {
        Iterator localIterator2 = localMap2.entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          if (localJsonObject.get((String)localEntry.getKey()) == null) {
            a(paramGson, localJsonObject, localq, (Field)((List)localEntry.getValue()).get(0));
          }
        }
      }
    }
  }
  
  private static void e(Gson paramGson, JsonObject paramJsonObject, h paramh)
  {
    Map localMap1 = a(paramh.getBonusesUnsafe());
    Map localMap2 = ReflectionUtil.getFieldsByNameMap(com.a.b.a.a.c.a.class);
    Iterator localIterator1 = paramJsonObject.getAsJsonArray("bonuses").iterator();
    while (localIterator1.hasNext())
    {
      JsonElement localJsonElement = (JsonElement)localIterator1.next();
      JsonObject localJsonObject = localJsonElement.getAsJsonObject();
      long l = localJsonObject.get("id").getAsLong();
      com.a.b.a.a.c.a locala = (com.a.b.a.a.c.a)localMap1.get(Long.valueOf(l));
      if (locala != null)
      {
        Iterator localIterator2 = localMap2.entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          if (localJsonObject.get((String)localEntry.getKey()) == null) {
            a(paramGson, localJsonObject, locala, (Field)((List)localEntry.getValue()).get(0));
          }
        }
      }
    }
  }
  
  private static void f(Gson paramGson, JsonObject paramJsonObject, h paramh)
  {
    Map localMap1 = a(paramh.getOilSlicksUnsafe());
    Map localMap2 = ReflectionUtil.getFieldsByNameMap(com.a.b.a.a.c.n.class);
    Iterator localIterator1 = paramJsonObject.getAsJsonArray("oilSlicks").iterator();
    while (localIterator1.hasNext())
    {
      JsonElement localJsonElement = (JsonElement)localIterator1.next();
      JsonObject localJsonObject = localJsonElement.getAsJsonObject();
      long l = localJsonObject.get("id").getAsLong();
      com.a.b.a.a.c.n localn = (com.a.b.a.a.c.n)localMap1.get(Long.valueOf(l));
      if (localn != null)
      {
        Iterator localIterator2 = localMap2.entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          if (localJsonObject.get((String)localEntry.getKey()) == null) {
            a(paramGson, localJsonObject, localn, (Field)((List)localEntry.getValue()).get(0));
          }
        }
      }
    }
  }
  
  private static void a(Gson paramGson, JsonObject paramJsonObject, Object paramObject, Field paramField)
  {
    try
    {
      paramJsonObject.add(paramField.getName(), paramGson.toJsonTree(paramField.get(paramObject)));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      a.error(String.format("Can't read field '%s' of previous %s.", new Object[] { paramField.getName(), paramObject.getClass().getSimpleName() }), localIllegalAccessException);
    }
  }
  
  public static Map a(h paramh)
  {
    com.a.b.a.a.c.o[] arrayOfo = paramh.getPlayersUnsafe();
    int i = arrayOfo.length;
    HashMap localHashMap = new HashMap();
    for (int j = 0; j < i; j++)
    {
      com.a.b.a.a.c.o localo = arrayOfo[j];
      localHashMap.put(Long.valueOf(localo.getId()), localo);
    }
    return Collections.unmodifiableMap(localHashMap);
  }
  
  public static Map b(h paramh)
  {
    HashMap localHashMap = new HashMap();
    Object localObject2;
    for (localObject2 : paramh.getCarsUnsafe()) {
      localHashMap.put(Long.valueOf(((com.a.b.a.a.c.c)localObject2).getId()), localObject2);
    }
    for (localObject2 : paramh.getProjectilesUnsafe()) {
      localHashMap.put(Long.valueOf(((com.a.b.a.a.c.q)localObject2).getId()), localObject2);
    }
    for (localObject2 : paramh.getBonusesUnsafe()) {
      localHashMap.put(Long.valueOf(((com.a.b.a.a.c.a)localObject2).getId()), localObject2);
    }
    for (localObject2 : paramh.getOilSlicksUnsafe()) {
      localHashMap.put(Long.valueOf(((com.a.b.a.a.c.n)localObject2).getId()), localObject2);
    }
    return Collections.unmodifiableMap(localHashMap);
  }
  
  private static Map a(u[] paramArrayOfu)
  {
    int i = paramArrayOfu.length;
    HashMap localHashMap = new HashMap(i);
    for (int j = 0; j < i; j++)
    {
      u localu = paramArrayOfu[j];
      localHashMap.put(Long.valueOf(localu.getId()), localu);
    }
    return Collections.unmodifiableMap(localHashMap);
  }
  
  private static void a(h paramh, Object paramObject)
  {
    if (paramObject != null) {
      try
      {
        ((Field)((List)ReflectionUtil.getFieldsByNameMap(h.class).get("systemData")).get(0)).set(paramh, paramObject);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        a.error("Can't set field 'systemData' of decorated world.", localIllegalAccessException);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\q.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */