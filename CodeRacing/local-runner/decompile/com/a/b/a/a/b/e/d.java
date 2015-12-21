package com.a.b.a.a.b.e;

import com.a.b.a.a.a.b;
import com.a.b.a.a.c.a;
import com.a.b.a.a.c.c;
import com.a.b.a.a.c.f;
import com.a.b.a.a.c.g;
import com.a.b.a.a.c.h;
import com.a.b.a.a.c.j;
import com.a.b.a.a.c.n;
import com.a.b.a.a.c.o;
import com.a.b.a.a.c.u;
import com.a.b.a.a.c.v;
import com.codeforces.commons.collection.CollectionUtil;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.reflection.ReflectionUtil;
import com.codeforces.commons.text.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Until;
import com.google.inject.ConfigurationException;
import com.google.inject.spi.Message;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class d
{
  private static final Logger a = LoggerFactory.getLogger(d.class);
  private static final ConcurrentMap b = new ConcurrentHashMap();
  private static final ConcurrentMap c = new ConcurrentHashMap();
  private final Set d = new HashSet();
  private final Set e = new HashSet();
  private final Set f = new HashSet();
  private final StringBuilder g = new StringBuilder(NumberUtil.toInt(262144L));
  private final b h;
  private final boolean i;
  private final a j;
  private final int k;
  private final int l;
  private double m;
  private float n;
  private h o;
  
  public d(b paramb, boolean paramBoolean, a parama, int paramInt1, int paramInt2)
  {
    if (paramb == null) {
      throw new IllegalArgumentException("Argument 'properties' is null");
    }
    if (parama == null) {
      throw new IllegalArgumentException("Argument 'specialFloatingPointValueSerializationStrategy' is null");
    }
    if ((paramInt1 < 1) || (paramInt1 > 15)) {
      throw new IllegalArgumentException(String.format("Illegal value %d of argument 'maxDoubleFractionalPartDigitCount'.", new Object[] { Integer.valueOf(paramInt1) }));
    }
    if ((paramInt2 < 1) || (paramInt2 > 5)) {
      throw new IllegalArgumentException(String.format("Illegal value %d of argument 'maxFloatFractionalPartDigitCount'.", new Object[] { Integer.valueOf(paramInt2) }));
    }
    this.h = paramb;
    this.i = paramBoolean;
    this.j = parama;
    this.k = paramInt1;
    this.l = paramInt2;
    this.m = 10.0D;
    for (int i1 = 1; i1 < paramInt1; i1++) {
      this.m *= 10.0D;
    }
    this.n = 10.0F;
    for (i1 = 1; i1 < paramInt2; i1++) {
      this.n *= 10.0F;
    }
  }
  
  public d(b paramb)
  {
    this(paramb, false, a.b, 3, 3);
  }
  
  public String a(h paramh)
  {
    this.g.setLength(0);
    try
    {
      a(paramh);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ConfigurationException(Collections.singletonList(new Message("Got unexpected exception while serializing world.", localIllegalAccessException)));
    }
    if (this.o != null)
    {
      if (paramh.getTick() != this.o.getTick() + 1) {
        a.warn(String.format("Unexpected tick %d of previous world. Current tick is %d.", new Object[] { Integer.valueOf(this.o.getTick()), Integer.valueOf(paramh.getTick()) }));
      }
      a(paramh);
      c(paramh);
    }
    this.o = paramh;
    return this.g.toString();
  }
  
  private void a(Object paramObject)
    throws IllegalAccessException
  {
    Class localClass = paramObject.getClass();
    this.g.append('{');
    boolean bool1 = a(paramObject, localClass);
    boolean bool2 = false;
    Iterator localIterator = ReflectionUtil.getFieldsByNameMap(localClass).entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      if (localList.size() != 1) {
        throw new ConfigurationException(Collections.singletonList(new Message(String.format("Zero or multiple fields with name '%s' in class '%s'.", new Object[] { str, localClass.getSimpleName() }))));
      }
      Field localField = (Field)localList.get(0);
      Expose localExpose = (Expose)localField.getAnnotation(Expose.class);
      if ((localExpose == null) || (localExpose.serialize()))
      {
        if (!bool1)
        {
          Until localUntil = (Until)localField.getAnnotation(Until.class);
          if (localUntil != null)
          {
            if (NumberUtil.equals(Double.valueOf(localUntil.value()), Double.valueOf(1.0D))) {
              continue;
            }
            throw new ConfigurationException(Collections.singletonList(new Message(String.format("Unsupported value %s of @Until annotation on field '%s' of class '%s'.", new Object[] { Double.valueOf(localUntil.value()), str, localClass.getSimpleName() }))));
          }
        }
        int i1 = this.g.length();
        a(paramObject, localField, bool2);
        bool2 |= this.g.length() > i1;
      }
    }
    this.g.append('}');
  }
  
  private boolean a(Object paramObject, Class paramClass)
  {
    if (paramClass == h.class) {
      return ((v)paramObject).getTick() == 0;
    }
    if (paramClass == o.class) {
      return this.d.add(Long.valueOf(((o)paramObject).getId()));
    }
    if (u.class.isAssignableFrom(paramClass)) {
      return this.e.add(Long.valueOf(((u)paramObject).getId()));
    }
    if (paramClass == j.class) {
      return this.f.add(Long.valueOf(((j)paramObject).getId()));
    }
    if ((paramClass == f.class) || (paramClass == g.class)) {
      return true;
    }
    throw new IllegalArgumentException("Unsupported class: " + paramClass + '.');
  }
  
  private void a(Object paramObject, Field paramField, boolean paramBoolean)
    throws IllegalAccessException
  {
    Class localClass = paramField.getType();
    Object localObject1;
    if (localClass == Boolean.class)
    {
      localObject1 = (Boolean)paramField.get(paramObject);
      if ((this.i) || (localObject1 != null)) {
        a(paramField, paramBoolean).append(localObject1);
      }
    }
    else if (localClass == Boolean.TYPE)
    {
      a(paramField, paramBoolean).append(paramField.getBoolean(paramObject));
    }
    else if (localClass == Integer.class)
    {
      localObject1 = (Integer)paramField.get(paramObject);
      if ((this.i) || (localObject1 != null)) {
        a(paramField, paramBoolean).append(localObject1);
      }
    }
    else if (localClass == Integer.TYPE)
    {
      a(paramField, paramBoolean).append(paramField.getInt(paramObject));
    }
    else if (localClass == Long.class)
    {
      localObject1 = (Long)paramField.get(paramObject);
      if ((this.i) || (localObject1 != null)) {
        a(paramField, paramBoolean).append(localObject1);
      }
    }
    else if (localClass == Long.TYPE)
    {
      a(paramField, paramBoolean).append(paramField.getLong(paramObject));
    }
    else
    {
      String str1;
      if ((localClass == Float.class) || (localClass == Float.TYPE))
      {
        localObject1 = (Float)paramField.get(paramObject);
        if (localObject1 == null)
        {
          if (this.i) {
            a(paramField, paramBoolean).append("null");
          }
        }
        else if (Float.isNaN(((Float)localObject1).floatValue()))
        {
          a(paramObject, paramField, localObject1, paramBoolean, "NaN");
        }
        else if (Float.isInfinite(((Float)localObject1).floatValue()))
        {
          str1 = ((Float)localObject1).floatValue() == Float.NEGATIVE_INFINITY ? "-Infinity" : "Infinity";
          a(paramObject, paramField, localObject1, paramBoolean, str1);
        }
        else
        {
          a(paramField, paramBoolean).append(Math.round(((Float)localObject1).floatValue() * this.n) / this.n);
        }
      }
      else if ((localClass == Double.class) || (localClass == Double.TYPE))
      {
        localObject1 = (Double)paramField.get(paramObject);
        if (localObject1 == null)
        {
          if (this.i) {
            a(paramField, paramBoolean).append("null");
          }
        }
        else if (Double.isNaN(((Double)localObject1).doubleValue()))
        {
          a(paramObject, paramField, localObject1, paramBoolean, "NaN");
        }
        else if (Double.isInfinite(((Double)localObject1).doubleValue()))
        {
          str1 = ((Double)localObject1).doubleValue() == Double.NEGATIVE_INFINITY ? "-Infinity" : "Infinity";
          a(paramObject, paramField, localObject1, paramBoolean, str1);
        }
        else
        {
          a(paramField, paramBoolean).append(Math.round(((Double)localObject1).doubleValue() * this.m) / this.m);
        }
      }
      else if (localClass == String.class)
      {
        localObject1 = (String)paramField.get(paramObject);
        if (localObject1 == null)
        {
          if (this.i) {
            a(paramField, paramBoolean).append("null");
          }
        }
        else {
          a(paramField, paramBoolean).append('"').append(StringEscapeUtils.escapeJson((String)localObject1)).append('"');
        }
      }
      else if (Enum.class.isAssignableFrom(localClass))
      {
        localObject1 = (Enum)paramField.get(paramObject);
        if (localObject1 == null)
        {
          if (this.i) {
            a(paramField, paramBoolean).append("null");
          }
        }
        else {
          a(paramField, paramBoolean).append('"').append(StringEscapeUtils.escapeJson(((Enum)localObject1).name())).append('"');
        }
      }
      else
      {
        int i1;
        if (Map.class.isAssignableFrom(localClass))
        {
          localObject1 = (Map)paramField.get(paramObject);
          if (localObject1 == null)
          {
            if (this.i) {
              a(paramField, paramBoolean).append("null");
            }
          }
          else
          {
            a(paramField, paramBoolean).append('{');
            i1 = 1;
            Iterator localIterator = ((Map)localObject1).entrySet().iterator();
            while (localIterator.hasNext())
            {
              Object localObject2 = localIterator.next();
              Map.Entry localEntry = (Map.Entry)localObject2;
              String str2;
              if ((localEntry.getKey() == null) || (StringUtil.isBlank(str2 = localEntry.getKey().toString())) || (StringUtil.trim(str2).length() != str2.length())) {
                throw new IllegalArgumentException(String.format("Illegal map key '%s' of field '%s.%s'.", new Object[] { localEntry.getKey(), paramObject.getClass().getSimpleName(), paramField.getName() }));
              }
              if ((this.i) || (localEntry.getValue() != null))
              {
                if (i1 != 0) {
                  i1 = 0;
                } else {
                  this.g.append(',');
                }
                this.g.append('"').append(StringEscapeUtils.escapeJson(str2)).append("\":");
                a(localEntry.getValue(), paramField);
              }
            }
            this.g.append('}');
          }
        }
        else
        {
          localObject1 = paramField.get(paramObject);
          if (localObject1 == null)
          {
            if (this.i) {
              a(paramField, paramBoolean).append("null");
            }
          }
          else if (localClass.isArray())
          {
            a(paramField, paramBoolean).append('[');
            i1 = Array.getLength(localObject1);
            if (i1 > 0)
            {
              a(Array.get(localObject1, 0), paramField);
              for (int i2 = 1; i2 < i1; i2++)
              {
                this.g.append(',');
                a(Array.get(localObject1, i2), paramField);
              }
            }
            this.g.append(']');
          }
          else
          {
            a(paramField, paramBoolean);
            a(paramField.get(paramObject));
          }
        }
      }
    }
  }
  
  private void a(Object paramObject, Field paramField)
    throws IllegalAccessException
  {
    if (paramObject == null)
    {
      this.g.append("null");
      return;
    }
    Class localClass = paramObject.getClass();
    if ((localClass == Boolean.class) || (localClass == Boolean.TYPE) || (localClass == Byte.class) || (localClass == Byte.TYPE) || (localClass == Short.class) || (localClass == Short.TYPE) || (localClass == Integer.class) || (localClass == Integer.TYPE) || (localClass == Long.class) || (localClass == Long.TYPE))
    {
      this.g.append(paramObject);
    }
    else
    {
      String str;
      if ((localClass == Float.class) || (localClass == Float.TYPE))
      {
        if (Float.isNaN(((Float)paramObject).floatValue()))
        {
          a(paramField, paramObject, "NaN");
        }
        else if (Float.isInfinite(((Float)paramObject).floatValue()))
        {
          str = paramObject.equals(Float.valueOf(Float.NEGATIVE_INFINITY)) ? "-Infinity" : "Infinity";
          a(paramField, paramObject, str);
        }
        else
        {
          this.g.append(Math.round(((Float)paramObject).floatValue() * this.n) / this.n);
        }
      }
      else if ((localClass == Double.class) || (localClass == Double.TYPE))
      {
        if (Double.isNaN(((Double)paramObject).doubleValue()))
        {
          a(paramField, paramObject, "NaN");
        }
        else if (Double.isInfinite(((Double)paramObject).doubleValue()))
        {
          str = paramObject.equals(Double.valueOf(Double.NEGATIVE_INFINITY)) ? "-Infinity" : "Infinity";
          a(paramField, paramObject, str);
        }
        else
        {
          this.g.append(Math.round(((Double)paramObject).doubleValue() * this.m) / this.m);
        }
      }
      else if (Enum.class.isAssignableFrom(localClass))
      {
        this.g.append('"').append(StringEscapeUtils.escapeJson(((Enum)paramObject).name())).append('"');
      }
      else if (localClass.isArray())
      {
        this.g.append('[');
        int i1 = Array.getLength(paramObject);
        if (i1 > 0)
        {
          a(Array.get(paramObject, 0), paramField);
          for (int i2 = 1; i2 < i1; i2++)
          {
            this.g.append(',');
            a(Array.get(paramObject, i2), paramField);
          }
        }
        this.g.append(']');
      }
      else
      {
        a(paramObject);
      }
    }
  }
  
  private void a(Object paramObject1, Field paramField, Object paramObject2, boolean paramBoolean, String paramString)
  {
    switch (e.a[this.j.ordinal()])
    {
    case 1: 
      a(paramField, paramBoolean).append(paramString);
      break;
    case 2: 
      break;
    case 3: 
      throw new IllegalArgumentException(String.format("Can't serialize special floating point value '%s' of field '%s.%s'.", new Object[] { paramObject2, paramObject1.getClass().getSimpleName(), paramField.getName() }));
    case 4: 
      a(paramField, paramBoolean).append("0.0");
      break;
    default: 
      throw new IllegalArgumentException(String.format("Unsupported special floating point value serialization strategy: %s.", new Object[] { this.j }));
    }
  }
  
  private void a(Field paramField, Object paramObject, String paramString)
  {
    switch (e.a[this.j.ordinal()])
    {
    case 1: 
      this.g.append(paramString);
      break;
    case 2: 
    case 3: 
      throw new IllegalArgumentException(String.format("Can't serialize special floating point value '%s' of item of array/collection field '%s.%s'.", new Object[] { paramObject, paramField.getDeclaringClass().getSimpleName(), paramField.getName() }));
    case 4: 
      this.g.append("0.0");
      break;
    default: 
      throw new IllegalArgumentException(String.format("Unsupported special floating point value serialization strategy: %s.", new Object[] { this.j }));
    }
  }
  
  private StringBuilder a(Field paramField, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.g.append(',');
    }
    return this.g.append('"').append(paramField.getName()).append("\":");
  }
  
  private void a(v paramv)
  {
    Map localMap = q.b(this.o);
    List localList = b(paramv);
    boolean bool = this.h.n();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      u localu = (u)localIterator.next();
      if (p.a(localu, (u)localMap.get(Long.valueOf(localu.getId()))))
      {
        Pattern localPattern = a(localu.getId());
        Matcher localMatcher;
        for (int i1 = 0; ((bool) || (i1 == 0)) && ((localMatcher = localPattern.matcher(this.g)).find()); i1++) {
          this.g.replace(this.g.lastIndexOf("{", localMatcher.start()) + 1, this.g.indexOf("}", localMatcher.end()), "\"id\":" + localu.getId());
        }
        if (i1 != 1) {
          a.warn(String.format("Found %d matches for %s {id=%d} in the world's JSON-dump (tick=%d).", new Object[] { Integer.valueOf(i1), localu.getClass().getSimpleName(), Long.valueOf(localu.getId()), Integer.valueOf(paramv.getTick()) }));
        }
      }
    }
  }
  
  private static List b(v paramv)
  {
    c[] arrayOfc = paramv.getCarsUnsafe();
    com.a.b.a.a.c.q[] arrayOfq = paramv.getProjectilesUnsafe();
    a[] arrayOfa = paramv.getBonusesUnsafe();
    n[] arrayOfn = paramv.getOilSlicksUnsafe();
    ArrayList localArrayList = new ArrayList(arrayOfc.length + arrayOfq.length + arrayOfa.length + arrayOfn.length);
    CollectionUtil.addAll(localArrayList, arrayOfc);
    CollectionUtil.addAll(localArrayList, arrayOfq);
    CollectionUtil.addAll(localArrayList, arrayOfa);
    CollectionUtil.addAll(localArrayList, arrayOfn);
    return localArrayList;
  }
  
  private void c(v paramv)
  {
    Map localMap = q.a(this.o);
    boolean bool = this.h.n();
    for (o localo : paramv.getPlayersUnsafe()) {
      if (o.areFieldEquals(localo, (o)localMap.get(Long.valueOf(localo.getId()))))
      {
        Pattern localPattern = b(localo.getId());
        Matcher localMatcher;
        for (int i3 = 0; ((bool) || (i3 == 0)) && ((localMatcher = localPattern.matcher(this.g)).find()); i3++) {
          this.g.replace(this.g.lastIndexOf("{", localMatcher.start()) + 1, this.g.indexOf("}", localMatcher.end()), "\"id\":" + localo.getId());
        }
        if (i3 != 1) {
          a.warn(String.format("Found %d matches for %s {id=%d} in the world's JSON-dump (tick=%d).", new Object[] { Integer.valueOf(i3), localo.getClass().getSimpleName(), Long.valueOf(localo.getId()), Integer.valueOf(paramv.getTick()) }));
        }
      }
    }
  }
  
  private static Pattern a(long paramLong)
  {
    Pattern localPattern = (Pattern)b.get(Long.valueOf(paramLong));
    if (localPattern == null)
    {
      localPattern = Pattern.compile("\"id\"\\s*:\\s*" + paramLong + "\\s*,\\s*\"x\"\\s*:", 8);
      b.putIfAbsent(Long.valueOf(paramLong), localPattern);
    }
    return localPattern;
  }
  
  private static Pattern b(long paramLong)
  {
    Pattern localPattern = (Pattern)c.get(Long.valueOf(paramLong));
    if (localPattern == null)
    {
      localPattern = Pattern.compile("\"id\"\\s*:\\s*" + paramLong + "\\s*,\\s*\"strategyCrashed\"\\s*:", 8);
      c.putIfAbsent(Long.valueOf(paramLong), localPattern);
    }
    return localPattern;
  }
  
  public static enum a
  {
    a,  b,  c,  d;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */